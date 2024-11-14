from dataclasses import dataclass
from datetime import datetime
from typing import List

@dataclass
class CrawledPage:
    url: str
    content: str
    content_hash: str
    links: List[str]
    crawl_time: datetime
    status_code: int