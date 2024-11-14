import time
import aiodns
import logging
from typing import Dict

# DNS Cache
class DNSCache:
    """Caches DNS resolutions to reduce lookups"""
    def __init__(self, timeout: int):
        self.cache: Dict[str, tuple[str, float]] = {}
        self.timeout = timeout
        self.resolver = aiodns.DNSResolver()

    async def resolve(self, domain: str) -> str:
        current_time = time.time()
        
        if domain in self.cache:
            ip, timestamp = self.cache[domain]
            if current_time - timestamp < self.timeout:
                return ip

        try:
            result = await self.resolver.query(domain, 'A')
            ip = result[0].host
            self.cache[domain] = (ip, current_time)
            return ip
        except Exception as e:
            logging.error(f"DNS resolution failed for {domain}: {str(e)}")
            raise