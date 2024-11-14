from typing import Dict, Set, List, Any
from datetime import datetime
from webcrawler.processors.base import ContentProcessor
from webcrawler.storage.base import Storage
from webcrawler.models.page import CrawledPage


class LinkTrackingProcessor(ContentProcessor):
    """Content processor that tracks and stores discovered links"""
    def __init__(self, storage: Storage):
        self.storage = storage
        self.links_by_page: Dict[str, List[str]] = {}
        self.all_discovered_links: Set[str] = set()
        self.start_time = datetime.now()

    async def process(self, page: CrawledPage) -> None:
        # Store links for this page
        self.links_by_page[page.url] = page.links
        
        # Add to all discovered links
        self.all_discovered_links.update(page.links)
        
        # Store in persistent storage
        await self.storage.save_page(page)
        
        # Print discovered links immediately
        print(f"\nLinks discovered from {page.url}:")
        for link in page.links:
            print(f"  → {link}")
        
        # Print summary for this page
        print(f"\nSummary for {page.url}:")
        print(f"Total links found on this page: {len(page.links)}")
        print(f"Total unique links discovered so far: {len(self.all_discovered_links)}")
        print(f"Time elapsed: {datetime.now() - self.start_time}")

    def get_link_summary(self) -> Dict[str, Any]:
        return {
            "total_pages_crawled": len(self.links_by_page),
            "total_unique_links": len(self.all_discovered_links),
            "links_per_page": {url: len(links) for url, links in self.links_by_page.items()},
            "all_links": sorted(list(self.all_discovered_links)),
            "crawl_duration": str(datetime.now() - self.start_time)
        }