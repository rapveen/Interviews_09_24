import asyncio
import time

class RateLimiter:
    def __init__(self, rate_limit):
        self.rate_limit = rate_limit
        self.interval = 1.0/rate_limit #req per sec
        self.lock = asyncio.Lock()
        self.last_time = 0

    async def __aenter__(self):
        await self.lock.acquire()
        now = time.time()
        elapsed_time = now - self.last_time
        wait_time = self.interval - elapsed_time
        if wait_time > 0:
            # wait the req with sleep
            await asyncio.sleep(wait_time)
        self.last_time = time.time()
        return self

    async def __aexit__(self, exc_type, exc, tb):
        self.lock.release()
