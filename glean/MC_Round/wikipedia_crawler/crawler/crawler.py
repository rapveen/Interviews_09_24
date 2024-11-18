import asyncio
import aiohttp
import logging
from .parser import parse_links, parse_title
from .rate_limiter import RateLimiter
from collections import defaultdict
from urllib.parse import unquote
from urllib.parse import urljoin, unquote
from bs4 import BeautifulSoup

class Crawler:
    def __init__(self, start_url, max_pages, rate_limit):
        self.start_url = start_url
        self.max_pages = max_pages
        self.rate_limiter = RateLimiter(rate_limit)
        self.visited = set()
        self.letters_seen = set()
        self.to_visit = asyncio.Queue()
        self.pending_urls = defaultdict(set)
        self.results = []
        self.lock = asyncio.Lock()

    def get_url_first_letter(self, url):
        try:
            page_name = url.split('/wiki/')[-1]
            first_char = unquote(page_name)[0].upper()
            return first_char
        except (IndexError, AttributeError):
            return None

    async def fetch(self, session, url):
        async with self.rate_limiter:
            try:
                async with session.get(url) as response:
                    if response.status == 200:
                        return await response.text()
                    logging.warning(f"Failed to fetch {url}: {response.status}")
            except Exception as e:
                logging.error(f"Error fetching {url}: {e}")
        return None
    
    async def process_url(self, session, url):
        async with self.lock:
            if url in self.visited:
                return
            self.visited.add(url)

        html = await self.fetch(session, url)
        if not html:
            return

        links = parse_links(html, url)
        title = parse_title(html)
        self.results.append((url, title))

        for link in links:
            if link in self.visited:
                continue
                
            first_letter = self.get_url_first_letter(link)
            if not first_letter:
                continue

            async with self.lock:
                if first_letter not in self.letters_seen:
                    self.letters_seen.add(first_letter)
                    await self.to_visit.put(link)
                else:
                    self.pending_urls[first_letter].add(link)

    async def crawl(self):
        first_letter = self.get_url_first_letter(self.start_url)
        if first_letter:
            self.letters_seen.add(first_letter)
        await self.to_visit.put(self.start_url)

        async with aiohttp.ClientSession() as session:
            while len(self.visited) < self.max_pages:
                try:
                    url = await asyncio.wait_for(self.to_visit.get(), timeout=0.1)
                except asyncio.TimeoutError:
                    url = None
                    for letter_urls in self.pending_urls.values():
                        unvisited = letter_urls - self.visited
                        if unvisited:
                            url = unvisited.pop()
                            break
                    
                    if not url:
                        break 

                await self.process_url(session, url)

    def get_results(self):
        return sorted(self.results, key=lambda x: (self.get_url_first_letter(x[0]), x[1]))