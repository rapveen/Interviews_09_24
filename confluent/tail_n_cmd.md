implement Linux Tail Command. Tail -N Command.

Questions for Clarity:
Should it read from file or standard input or both?
What should N represent - lines or bytes?
What's the behavior if N is larger than total lines?
Should we support negative N values (like tail -n -10)?
Maximum file size constraint?
Memory constraints - should we optimize for large files?
Should we follow/monitor file changes (like tail -f)?
Character encoding requirements?
How to handle empty lines?
What to return for empty files?
Edge Cases:
File doesn't exist
N = 0
N = 1
N > total lines
File with only newlines
Very large files (GB/TB)
Binary files
Files with different line endings (CRLF vs LF)
Files with no final newline
Permission issues

Answered questions: 
it should read from a file
N represents lines
if N is larger than total lines then just give existing lines
no support for -ve values
currently no max file size constraint but may be memory available constraint that depends on ram available
yes we should allow and monitor file changes and output the last N lines
consider empty lines as single line only

Brute Force Approach:
Read entire file into memory
Split into lines array
Return last N lines
For monitoring (-f), poll file periodically and repeat 1-3
Pros:
Simple implementation
Fast for small files
Quick random access to lines
Cons:
Memory inefficient (O(file_size))
Poor for large files
Inefficient for continuous monitoring
Unnecessary full file reads
Optimization 1:
Use circular buffer of size N
Read file line by line
Keep overwriting oldest line
Final buffer has last N lines
Pros:
Memory O(N) instead of O(file_size) Cons:
Still reads entire file
Inefficient for monitoring
Optimization 2:
Start from file end
Read backwards in chunks
Count newlines until N lines found
Read only required portion
Pros:
Reads minimal data
Memory efficient Cons:
Complex for monitoring
Final Optimization (Real-world):
Use combination based on mode:
For initial read: Use backwards reading
For monitoring: Use inotify/kqueue + circular buffer
Real-world Implementation (like Linux):
Uses inotify/kqueue for file change notifications
Memory-maps files for large files
Uses multiple buffers for concurrent access
Handles file rotations
Supports multiple file descriptors
Complexity: Time: O(N) for initial read, O(1) for updates Space: O(N) where N is number of lines
I'll choose Python as it has better built-in support for file operations and simpler event monitoring.

import os
import time
from collections import deque
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler


class TailCommand:
    def __init__(self, file_path, n_lines=10):
        """Initialize with file path and number of lines to tail."""
        self.file_path = file_path
        self.n_lines = n_lines
        self.line_buffer = deque(maxlen=n_lines)
        self._validate_inputs()


    def _validate_inputs(self):
        """Validate input parameters and file existence."""
        if not os.path.exists(self.file_path):
            raise FileNotFoundError(f"File {self.file_path} not found")
        if self.n_lines < 0:
            raise ValueError("Number of lines cannot be negative")
        if not os.access(self.file_path, os.R_OK):
            raise PermissionError(f"No read permission for {self.file_path}")


    def _read_last_n_lines(self):
        """Read last N lines efficiently using backwards reading."""
        try:
            with open(self.file_path, 'rb') as f:
                # Seek to end of file
                f.seek(0, os.SEEK_END)
                file_size = remaining_size = f.tell()
               
                # Initialize block tracker
                block_size = 1024
                blocks = []
                lines_found = 0


                # Read blocks from end until we have N lines
                while remaining_size > 0 and lines_found <= self.n_lines:
                    block_size = min(block_size, remaining_size)
                    f.seek(remaining_size - block_size)
                    blocks.append(f.read(block_size))
                   
                    lines_found += blocks[-1].count(b'\n')
                    remaining_size -= block_size


                # Decode and split into lines
                content = b''.join(reversed(blocks)).decode('utf-8')
                all_lines = content.splitlines()


                # Update buffer with last N lines
                self.line_buffer.extend(all_lines[-self.n_lines:])
               
                return list(self.line_buffer)


        except Exception as e:
            raise RuntimeError(f"Error reading file: {str(e)}")


    def _monitor_changes(self):
        """Monitor file changes using watchdog."""
        event_handler = FileChangeHandler(self)
        observer = Observer()
        observer.schedule(event_handler, os.path.dirname(self.file_path), recursive=False)
        return observer


    def tail(self, follow=False):
        """Main tail function with optional follow mode."""
        # Initial read
        lines = self._read_last_n_lines()
        for line in lines:
            print(line)


        # Setup monitoring if follow mode is enabled
        if follow:
            observer = self._monitor_changes()
            observer.start()
            try:
                while True:
                    time.sleep(1)
            except KeyboardInterrupt:
                observer.stop()
                observer.join()


class FileChangeHandler(FileSystemEventHandler):
    def __init__(self, tail_command):
        self.tail_command = tail_command
        self.last_position = os.path.getsize(tail_command.file_path)


    def on_modified(self, event):
        if event.src_path == self.tail_command.file_path:
            current_position = os.path.getsize(event.src_path)
            if current_position > self.last_position:
                with open(event.src_path, 'r') as f:
                    f.seek(self.last_position)
                    new_lines = f.readlines()
                    for line in new_lines:
                        self.tail_command.line_buffer.append(line.strip())
                        print(line.strip())
                self.last_position = current_position


# Example usage
if __name__ == "__main__":
    try:
        tail = TailCommand("example.txt", 10)
        tail.tail(follow=True)
    except KeyboardInterrupt:
        print("\nStopping tail command...")
    except Exception as e:
        print(f"Error: {str(e)}")
