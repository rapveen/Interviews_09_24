from typing import Set
import hashlib

# Deduplication Service
class Deduplicator:
    """Handles content and URL deduplication"""
    def __init__(self):
        self.seen_urls: Set[str] = set()
        self.content_hashes: Set[str] = set()

    def is_seen_url(self, url: str) -> bool:
        return url in self.seen_urls

    def mark_url_seen(self, url: str) -> None:
        self.seen_urls.add(url)

    def is_duplicate_content(self, content: str) -> bool:
        content_hash = self._hash_content(content)
        return content_hash in self.content_hashes

    def mark_content_seen(self, content: str) -> str:
        content_hash = self._hash_content(content)
        self.content_hashes.add(content_hash)
        return content_hash

    @staticmethod
    def _hash_content(content: str) -> str:
        return hashlib.md5(content.encode()).hexdigest()