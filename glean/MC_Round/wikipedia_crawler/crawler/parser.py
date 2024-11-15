from bs4 import BeautifulSoup
from urllib.parse import urljoin


def parse_links(html, base_url):
    soup = BeautifulSoup(html, 'html.parser')
    links = set()
    for a_tag in soup.find_all('a', href=True):
        href = a_tag['href']
        if href.startswith('/wiki/'):
            if any(x in href for x in [':', 'Main_Page', 'Help:', 'File:', 'Wikipedia:']):
                continue
            full_url = urljoin(base_url, href)
            links.add(full_url)
    return links

def parse_title(html):
    soup = BeautifulSoup(html, 'html.parser')
    title_tag = soup.find('h1')
    if title_tag:
        return title_tag.text.strip()
    else:
        return "NA title"
    # return title_tag.strip() if title_tag else "NA title"
