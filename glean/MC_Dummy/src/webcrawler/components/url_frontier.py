from collections import deque
from typing import Optional, Set

class URLFrontier:
    """Manages URLs to be crawled with priority queues"""
    def __init__(self, max_size: int = 10000):
        self.high_priority: deque = deque()
        self.normal_priority: deque = deque()
        self.low_priority: deque = deque()
        self.max_size = max_size
        self._seen_urls: Set[str] = set()

    def push(self, url: str, priority: int = 1) -> bool:
        if self.size() >= self.max_size or url in self._seen_urls:
            return False

        self._seen_urls.add(url)
        if priority == 0:
            self.high_priority.append(url)
        elif priority == 1:
            self.normal_priority.append(url)
        else:
            self.low_priority.append(url)
        return True

    def pop(self) -> Optional[str]:
        for queue in [self.high_priority, self.normal_priority, self.low_priority]:
            if queue:
                return queue.popleft()
        return None

    def size(self) -> int:
        return len(self.high_priority) + len(self.normal_priority) + len(self.low_priority)
