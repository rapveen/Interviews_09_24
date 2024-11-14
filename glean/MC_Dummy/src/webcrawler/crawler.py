
from datetime import datetime
from typing import List
from urllib.parse import urlparse, urljoin
import asyncio
import aiohttp
from bs4 import BeautifulSoup
import logging

from webcrawler.config.crawler_config import CrawlerConfig
from webcrawler.storage.base import Storage
from webcrawler.processors.base import ContentProcessor
from webcrawler.components.url_frontier import URLFrontier
from webcrawler.components.dns_cache import DNSCache
from webcrawler.components.deduplicator import Deduplicator
from webcrawler.storage.file_storage import FileStorage
from webcrawler.processors.link_processor import LinkTrackingProcessor
from webcrawler.models.page import CrawledPage


# Main Crawler Class
class WebCrawler:
    def __init__(
        self,
        config: CrawlerConfig,
        storage: Storage,
        content_processor: ContentProcessor
    ):
        self.config = config
        self.storage = storage
        self.content_processor = content_processor
        self.url_frontier = URLFrontier(config.max_queue_size)
        self.dns_cache = DNSCache(config.dns_cache_timeout)
        self.deduplicator = Deduplicator()
        self.semaphore = asyncio.Semaphore(config.max_concurrent)

    async def crawl(self, start_url: str) -> None:
        self.url_frontier.push(start_url, priority=0)
        
        async with aiohttp.ClientSession() as session:
            tasks = []
            while True:
                url = self.url_frontier.pop()
                if not url:
                    break

                if self.deduplicator.is_seen_url(url):
                    continue

                task = asyncio.create_task(self._crawl_url(session, url))
                tasks.append(task)
                
                if len(tasks) >= self.config.max_concurrent:
                    completed, tasks = await asyncio.wait(
                        tasks, return_when=asyncio.FIRST_COMPLETED
                    )
                    for task in completed:
                        if exc := task.exception():
                            logging.error(f"Task failed with error: {exc}")

            # Wait for remaining tasks
            if tasks:
                await asyncio.gather(*tasks, return_exceptions=True)

    async def _crawl_url(self, session: aiohttp.ClientSession, url: str) -> None:
        async with self.semaphore:
            try:
                # DNS resolution
                domain = urlparse(url).netloc
                ip = await self.dns_cache.resolve(domain)

                # Fetch content
                async with session.get(url, headers={'Host': domain}) as response:
                    content = await response.text()
                    
                    # Check for duplicate content
                    if self.deduplicator.is_duplicate_content(content):
                        logging.info(f"Duplicate content found: {url}")
                        return

                    # Extract links
                    links = await self._extract_links(content, url)
                    
                    # Create page object
                    page = CrawledPage(
                        url=url,
                        content=content,
                        content_hash=self.deduplicator.mark_content_seen(content),
                        links=links,
                        crawl_time=datetime.now(),
                        status_code=response.status
                    )

                    # Process content
                    await self.content_processor.process(page)

                    # Add new URLs to frontier
                    for link in links:
                        self.url_frontier.push(link)

                    self.deduplicator.mark_url_seen(url)

            except Exception as e:
                logging.error(f"Error processing {url}: {str(e)}")
                raise

    async def _extract_links(self, content: str, base_url: str) -> List[str]:
        soup = BeautifulSoup(content, 'html.parser')
        links = []
        
        for anchor in soup.find_all('a'):
            href = anchor.get('href')
            if href:
                absolute_url = urljoin(base_url, href)
                parsed_url = urlparse(absolute_url)
                if (parsed_url.scheme in ['http', 'https'] and
                    (not self.config.allowed_domains or
                     parsed_url.netloc in self.config.allowed_domains)):
                    links.append(absolute_url)
        
        return links

async def main():
    # Configure logging
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(levelname)s - %(message)s'
    )

    # Create configuration
    config = CrawlerConfig(
        max_depth=2,
        max_concurrent=5,
        allowed_domains={'example.com'},
        dns_cache_timeout=3600,
        max_queue_size=10000
    )

    # Initialize components
    storage = FileStorage("crawled_pages.json")
    content_processor = LinkTrackingProcessor(storage)
    
    # Create and run crawler
    crawler = WebCrawler(
        config=config,
        storage=storage,
        content_processor=content_processor
    )

    try:
        start_url = "https://example.com"
        logging.info(f"Starting crawl from: {start_url}")
        await crawler.crawl(start_url)

        # Print final summary
        summary = content_processor.get_link_summary()
        print("\nFinal Crawl Summary:")
        print(f"Total pages crawled: {summary['total_pages_crawled']}")
        print(f"Total unique links found: {summary['total_unique_links']}")
        print(f"Total crawl time: {summary['crawl_duration']}")
        
        print("\nLinks discovered per page:")
        for url, count in summary['links_per_page'].items():
            print(f"{url}: {count} links")

        print("\nAll discovered links:")
        for link in summary['all_links']:
            print(link)

    except Exception as e:
        logging.error(f"Crawl failed: {str(e)}")
        raise
    finally:
        logging.info("Crawl completed")

if __name__ == "__main__":
    asyncio.run(main())