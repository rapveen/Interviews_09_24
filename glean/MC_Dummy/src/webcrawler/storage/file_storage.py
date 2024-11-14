from webcrawler.storage.base import Storage
from webcrawler.models.page import CrawledPage
import aiofiles
import json

# File Storage Implementation
class FileStorage(Storage):
    def __init__(self, file_path: str = "crawled_pages.json"):
        self.file_path = file_path

    async def save_page(self, page: CrawledPage) -> None:
        data = {
            "url": page.url,
            "content_hash": page.content_hash,
            "crawl_time": page.crawl_time.isoformat(),
            "status_code": page.status_code,
            "links": page.links
        }
        
        async with aiofiles.open(self.file_path, 'a') as f:
            await f.write(json.dumps(data) + "\n")

    async def get_page(self, url: str) -> Optional[CrawledPage]:
        try:
            async with aiofiles.open(self.file_path, 'r') as f:
                async for line in f:
                    data = json.loads(line)
                    if data["url"] == url:
                        return CrawledPage(
                            url=data["url"],
                            content="",  # We don't store actual content
                            content_hash=data["content_hash"],
                            crawl_time=datetime.fromisoformat(data["crawl_time"]),
                            status_code=data["status_code"],
                            links=data["links"]
                        )
        except FileNotFoundError:
            return None