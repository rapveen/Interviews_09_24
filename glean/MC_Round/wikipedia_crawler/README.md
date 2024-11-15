```markdown
# Wikipedia Web Crawler

## Introduction

The **Wikipedia Web Crawler** is a Python-based tool designed to traverse Wikipedia pages by following internal links. Starting from a specified seed URL, the crawler explores linked pages, prioritizing those whose titles begin with unique letters that haven't been encountered before. Additionally, the crawler respects a global rate limit to prevent overwhelming Wikipedia's servers and supports concurrent crawling for faster results.

### Key Features

- **Unique Letter Prioritization**: Ensures each crawled page starts with a distinct letter.
- **Concurrency Support**: Utilizes asynchronous programming to crawl multiple pages in parallel.
- **Rate Limiting**: Configurable rate limit to control the number of requests per second.
- **Efficient Resource Management**: Avoids revisiting already processed pages.
- **Flexible Configuration**: Allows specifying the starting URL, maximum number of pages to crawl, and the rate limit via command-line arguments.

## Installation

### Prerequisites

- **Python 3.7 or higher**:

### Steps

1. **unzip the project**

2. **Create a Virtual Environment (Optional but Recommended)**

For windows
   ```bash
   python -m venv venv
   venv\Scripts\activate 
   ```
For Mac
    ```bash
    python -m venv venv
    source venv/bin/activate
    ```

3. **Install Dependencies**

   Ensure you have `pip` installed. Then, install the required packages using the provided `requirements.txt` file.

   ```bash
   pip install -r requirements.txt
   ```


## Usage

The crawler is executed via the command line. It requires three positional arguments:

1. **Start URL**: The seed Wikipedia page from which the crawler begins.
2. **Maximum Number of Pages**: The total number of unique pages to crawl.
3. **Rate Limit**: The maximum number of requests per second to respect Wikipedia's server load.

### Command Syntax

```bash
python main.py <start_url> <max_pages> <rate_limit>
```

### Example

To crawl starting from the "Google" Wikipedia page, aiming to retrieve up to 30 unique pages, with a rate limit of 1 request per second:

```bash
python main.py https://en.wikipedia.org/wiki/Google 3 1
```

## Example Output

Upon running the crawler with the above command, you might receive output similar to the following:

```plaintext
[https://en.wikipedia.org/wiki/David_Cheriton, David Cheriton]
[https://en.wikipedia.org/wiki/Google, Google]
[https://en.wikipedia.org/wiki/Nest_Wifi, Nest Wifi]
```

*Each entry in the output represents a tuple containing the URL and the title of the crawled Wikipedia page. All titles start with unique letters, ensuring diversity in the crawled content.*

## Additional Information

### Logging

The crawler provides informative logs during its execution to help monitor its progress and debug if necessary. By default, logging is set to the `INFO` level. You can adjust the logging level in `main.py` as needed.
