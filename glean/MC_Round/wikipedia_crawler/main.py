import argparse
from crawler.crawler import Crawler
import asyncio


def main():
    parser = argparse.ArgumentParser(description="Wikipedia parser")
    parser.add_argument('start_url', type=str, help="Seed URL")
    parser.add_argument('max_pages', type=int, help="Max no of pages to crawl")
    parser.add_argument('rate_limit', type=int, help="rate limit req per sec")
    args = parser.parse_args()


    crawler = Crawler(args.start_url, args.max_pages, args.rate_limit)
    asyncio.run(crawler.crawl())

    for url, title in crawler.get_results():
        print(f"[{url}, {title}]")

if __name__=='__main__':
    main()