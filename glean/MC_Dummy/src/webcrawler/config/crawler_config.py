from dataclasses import dataclass
from typing import Set, Optional

@dataclass
class CrawlerConfig:
    max_depth: int = 2
    max_concurrent: int = 5
    allowed_domains: Optional[Set[str]] = None
    dns_cache_timeout: int = 3600
    robots_cache_timeout: int = 3600
    max_queue_size: int = 10000