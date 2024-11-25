### Part - 1 Q

A transformation sequence from word beginword to word endword using a dictionary wordlist is a sequence of words beginword → s1 → s2 →....→ sk such that:

* Every adjacent pair of words differs by a single letter.
* Every si for 1 ≤ i ≤ k is in wordList. Note that beginword does not need to be in wordList.
* sk = endword
Given two words, beginword and endword,
and a dictionary wordList, return the number of words in the shortest transformation sequence from beginword to endword, or 0 if no such sequence exists.

Example 1:
Input: beginword = "hit", endword = "cog", wordList = ["hot", "dot", "dog", "lot", "log", "cog"]
Output: 5 
Explanation: One shortest transformation sequence is "hit" ~
"hot" → "dot" →
"dog" → cog", which is 5 words long.
Input: beginword = "hit", endword = "cog", wordList = ["hot", "dot", "dog", "lot", "log", "cog"]
Output: 5

Explanation: One shortest transformation sequence is "hit" → "hot" → "dot" →
"dog" > cog", which is 5 words long.


Constraints: each word is 3 letter size only

## Point-wise Analysis

1. **Transformation Sequence**: A series of words starting from `beginword` and ending with `endword`.
2. **Single Letter Difference**: Each consecutive pair of words differs by exactly one letter.
3. **Dictionary Inclusion**: All intermediate words must be present in `wordList`.
4. **Beginword Exclusion**: `beginword` doesn't need to be in `wordList`.
5. **Objective**: Find the shortest such transformation sequence's length.
6. **Edge Cases**:
   - `endword` not in `wordList` → return 0.
   - `beginword` equals `endword` → return 1.

## Dry Run Example

**Input**:
- `beginword`: "hit"
- `endword`: "cog"
- `wordList`: ["hot", "dot", "dog", "lot", "log", "cog"]

**Process**:
1. Start with "hit".
2. Change one letter: "hit" → "hot".
3. From "hot", possible changes: "dot", "lot".
4. From "dot": "dog".
5. From "dog": "cog".
6. Sequence: "hit" → "hot" → "dot" → "dog" → "cog".
7. Length: 5.

## Optimal Algorithm Consideration

- **Algorithm**: Breadth-First Search (BFS) is optimal for finding the shortest path in an unweighted graph.
- **Data Structure**: 
  - **Queue** for BFS traversal.
  - **HashSet** for quick lookup of `wordList` and to mark visited words.

## Approach

- **Data Structures**:
  - `Queue<String>` to perform BFS.
  - `HashSet<String>` for `wordList` and visited words.
  
- **Algorithm**:
  - Use BFS to explore all possible one-letter transformations level by level.
  
- **Steps**:
  1. Check if `endword` is in `wordList`. If not, return 0.
  2. Initialize a queue with `beginword` and a visited set.
  3. Iterate level by level, transforming each word by changing one letter at a time.
  4. If `endword` is found, return the current transformation sequence length.
  5. If BFS completes without finding `endword`, return 0.

## Optimized Java Code

```java
import java.util.*;

public class WordLadder {
    /**
     * Returns the number of words in the shortest transformation sequence from beginWord to endWord.
     * @param beginWord The starting word.
     * @param endWord The target word.
     * @param wordList The list of available words.
     * @return The length of the shortest transformation sequence, or 0 if none exists.
     */
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        // Convert wordList to a HashSet for O(1) lookups
        Set<String> wordSet = new HashSet<>(wordList);
        // If endWord is not in wordSet, no possible transformation
        if (!wordSet.contains(endWord)) return 0;
        
        // Initialize BFS queue
        Queue<String> queue = new LinkedList<>();
        queue.add(beginWord);
        
        // Initialize visited set to prevent revisiting
        Set<String> visited = new HashSet<>();
        visited.add(beginWord);
        
        int level = 1; // Start from level 1
        
        while (!queue.isEmpty()) {
            int size = queue.size(); // Number of elements at current level
            for (int i = 0; i < size; i++) {
                String currentWord = queue.poll();
                // If current word is the endWord, return the current level
                if (currentWord.equals(endWord)) return level;
                
                // Iterate through each character position
                for (int j = 0; j < currentWord.length(); j++) {
                    char[] wordChars = currentWord.toCharArray();
                    // Try all possible lowercase letters
                    for (char c = 'a'; c <= 'z'; c++) {
                        wordChars[j] = c;
                        String nextWord = new String(wordChars);
                        // If the transformed word is in wordSet and not visited
                        if (wordSet.contains(nextWord) && !visited.contains(nextWord)) {
                            queue.add(nextWord);
                            visited.add(nextWord);
                        }
                    }
                }
            }
            level++; // Increment level after processing current level
        }
        // If endWord was not found
        return 0;
    }

    // Example usage with the provided test case
    public static void main(String[] args) {
        WordLadder wl = new WordLadder();
        String beginWord = "hit";
        String endWord = "cog";
        List<String> wordList = Arrays.asList("hot", "dot", "dog", "lot", "log", "cog");
        int result = wl.ladderLength(beginWord, endWord, wordList);
        System.out.println(result); // Output: 5
    }
}
```

### Time and Space Complexity

- **Time Complexity**: O(N * M²)
  - N: Number of words in `wordList`.
  - M: Length of each word.
  - For each word, we try M positions and 26 possible letters, leading to O(N * M²).
  
- **Space Complexity**: O(N * M)
  - Storing the `wordSet` and the `visited` set requires O(N * M) space.


### Part - 2 Q
Longest Substring Without Repeating Characters
Given a string s, find the length of the longest substring without repeating characters.
Ex-1
Input: s = "abcabcbb" Output: 3 Explanation: The answer is "abc", with the length of 3.

public class LongestSubstringWithoutRepeatingOptimized {
    public static int lengthOfLongestSubstring(String s) {
        // Array to store the latest index of each ASCII character
        int[] charIndex = new int[128];
        // Initialize all indices to -1
        for(int i = 0; i < 128; i++) {
            charIndex[i] = -1;
        }

        int maxLength = 0; // To keep track of the maximum length found
        int left = 0; // Left pointer of the sliding window

        // Iterate over each character with the right pointer
        for (int right = 0; right < s.length(); right++) {
            char currentChar = s.charAt(right);
            // Get the last index of currentChar from the array
            if (charIndex[currentChar] >= left) {
                // Move the left pointer to the right of the same character's last index
                left = charIndex[currentChar] + 1;
            }
            // Update the current character's index to the current position
            charIndex[currentChar] = right;
            // Calculate the length of the current window and update maxLength if needed
            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength; // Return the maximum length found
    }

    // Main method to run example test cases
    public static void main(String[] args) {
        String test1 = "abcabcbb";
        System.out.println("Input: " + test1 + " | Output: " + lengthOfLongestSubstring(test1)); // Expected: 3

        String test2 = "";
        System.out.println("Input: \"" + test2 + "\" | Output: " + lengthOfLongestSubstring(test2)); // Expected: 0

        String test3 = "bbbb";
        System.out.println("Input: " + test3 + " | Output: " + lengthOfLongestSubstring(test3)); // Expected: 1

        String test4 = "pwwkew";
        System.out.println("Input: " + test4 + " | Output: " + lengthOfLongestSubstring(test4)); // Expected: 3

        String test5 = "abcdefg";
        System.out.println("Input: " + test5 + " | Output: " + lengthOfLongestSubstring(test5)); // Expected: 7
    }
}
