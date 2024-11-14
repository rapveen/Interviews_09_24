from abc import ABC, abstractmethod
from typing import Optional
from webcrawler.models.page import CrawledPage


# Storage Interface
class Storage(ABC):
    @abstractmethod
    async def save_page(self, page: CrawledPage) -> None:
        pass

    @abstractmethod
    async def get_page(self, url: str) -> Optional[CrawledPage]:
        pass