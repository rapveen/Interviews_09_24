from abc import ABC, abstractmethod
from webcrawler.models.page import CrawledPage


# Content Processor
class ContentProcessor(ABC):
    @abstractmethod
    async def process(self, page: CrawledPage) -> None:
        pass