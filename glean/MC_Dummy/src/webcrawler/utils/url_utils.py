from urllib.parse import urlparse, urljoin
from typing import Optional

def is_valid_url(url: str) -> bool:
    try:
        result = urlparse(url)
        return all([result.scheme, result.netloc])
    except ValueError:
        return False

def get_domain(url: str) -> Optional[str]:
    try:
        return urlparse(url).netloc
    except ValueError:
        return None