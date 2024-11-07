Let me solve this systematically using the given approach.

1. INITIAL ANALYSIS
- Minimum required constraints:
  - Time: Need to check subsequence validity multiple times
  - Space: Need to store/modify string s and check against p
- Optimizing for: Finding maximum k where p remains subsequence after k removals 
- Questions for clarity:
  - Can we modify string s directly or need a copy? Since we're checking multiple k values, likely need copies
  - Are removable indices sorted? No, not guaranteed per examples
  - Does order of removal matter? Yes, must use first k indices in removable

2. REQUIREMENTS ANALYSIS
- Full requirements list:
  - Must process removals in order of removable array
  - Must verify subsequence property after each set of removals
  - p must be subsequence of s initially
  - Need to find maximum valid k
  - All indices in removable are distinct
  - Need to handle strings with length up to 10^5
  - Key points often missed:
    - Order of removals is fixed by removable array order
    - Don't need to try all combinations, just first k elements
  - Most complex test case: Long strings with many possible removals

3. SOLUTION BRAINSTORMING
Approaches:
1. Brute Force (O(n²) time)
   - Try each k from 0 to removable.length
   - For each k, remove characters and check subsequence
   - Very inefficient for large strings

2. Binary Search with Subsequence Check (O(n log m) time, where m = removable.length)
   - Since valid k's form a continuous range (0...some value)
   - Can binary search for maximum valid k
   - For each mid point, check if valid
   - Much more efficient for large inputs

4. DRY RUN
Using Example 1: s = "abcacb", p = "ab", removable = [3,1,0]
- k = 0: "abcacb" - valid
- k = 1: "abcacb" -> "abccb" (remove index 3) - valid
- k = 2: "abcacb" -> "accb" (remove indices 3,1) - valid
- k = 3: "abcacb" -> "ccb" (remove indices 3,1,0) - invalid
Pattern observed: Once invalid, higher k's will also be invalid 
→ Binary search is valid approach

5. DETAILED APPROACH
Using Binary Search approach:
1. Do binary search on k from 0 to removable.length
2. For each mid point:
   - Create temporary string after removing first mid characters
   - Check if p is still subsequence
   - If valid, search in right half
   - If invalid, search in left half
3. Handle edge cases:
   - Empty strings
   - When p equals s
   - When removable is empty


Time Complexity: O(n * log m) where:
- n is length of string s
- m is length of removable array
- Binary search takes log m iterations
- Each iteration needs O(n) to check subsequence

Space Complexity: O(n)
- Need boolean array of size n to track removals
- Other variables use constant space

The solution efficiently finds the maximum k by:
1. Using binary search to try different k values
2. For each k, marking first k indices from removable as removed
3. Efficiently checking if p remains subsequence using two-pointer approach
4. Avoiding string copies by using boolean array to track removals

The code handles all edge cases and constraints:
- Works with empty removable array (returns 0)
- Works when p equals s
- Handles non-sorted removable arrays
- Processes removals in order of removable array
- Uses boolean array instead of string modification for efficiency

Test cases covered:
1. Example 1: s = "abcacb", p = "ab", removable = [3,1,0] → 2
2. Example 2: s = "abcbddddd", p = "abcd", removable = [3,2,1,4,5,6] → 1
3. Example 3: s = "abcab", p = "abc", removable = [0,1,2,3,4] → 0