"""
Algorithm Steps
Step 1: Inserting Class Names into the Trie
Initialize the Trie:
Create a Trie instance with an empty root node.
Insert Each Class Name:
For each class name, iterate through its characters.
At each character:
If the character exists as a child of the current node, move to that child.
If not, create a new TrieNode for that character and add it to the children of the current node.
Add the class name to the childFunctions set of the current node.
After inserting all characters of the class name, mark the last node's endOfWord as True.
Step 2: Autocomplete Matching

Question Analysis:
Implementation of an autocomplete feature using Trie data structure
Needs to support partial matching of function names
Should handle both lowercase and uppercase characters
Must maintain a set of child functions at each node
Pattern matching should work with mixed case patterns
Clarity Questions (covered in the code):
Pattern matching rules are clear from the implementation
Test cases demonstrate the expected behavior
Edge Cases:
Empty string input
Case sensitivity handling
Partial matches with uppercase letters
Multiple matches for a pattern
No matches for a pattern
Pattern longer than any existing word
Brute Force Approach: Pros:
Store all strings in a list
For each search pattern, iterate through all strings to find matches Cons:
O(n*m) time complexity where n is number of strings and m is average string length
Inefficient for large datasets
No prefix optimization
Optimization Steps: a) Use Trie for prefix optimization b) Store complete strings at each node for quick retrieval c) Special handling for uppercase characters in pattern d) Use defaultdict to simplify node creation e) Implement recursive matching for flexible pattern matching
Algorithm Points:
Create Trie with nodes containing children and function set() { # store the function name that are part of this node}
Insert by adding characters and updating function sets
Search using recursive pattern matching
Handle uppercase characters with special backtracking
Return all matching functions from final node


Complexity Analysis: Time:
Insert: O(L) where L is length of word
Search: O(P * N) where P is pattern length, N is number of nodes Space: O(T) where T is total characters in all words

"""


 
import collections
from typing import List
class TrieNode:
    def __init__(self):
        self.children = collections.defaultdict(TrieNode)
        self.functions = set()
        self.is_end = False
class AutoComplete:
    def __init__(self):
        self.root = TrieNode()
   
    def insert(self, word: str) -> None:
        """Insert a word into the trie and update function sets."""
        if not word:  # Handle empty string
            return
           
        node = self.root
        for char in word:
            node = node.children[char]
            node.functions.add(word)
        node.is_end = True
   
    def search(self, pattern: str) -> List[str]:
        """Search for all functions matching the given pattern."""
        def dfs(node: TrieNode, index: int) -> List[str]:
            # Base case: reached end of pattern
            if index >= len(pattern):
                return list(node.functions)
           
            char = pattern[index]
            results = []
           
            # First char or lowercase must match exactly
            if index == 0 or char.islower():
                if char in node.children:
                    results.extend(dfs(node.children[char], index + 1))
            else:
                # For uppercase, try direct match and backtrack
                for child in node.children.values():
                    if char in child.children:
                        results.extend(dfs(child.children[char], index + 1))
                    else:
                        results.extend(dfs(child, index))
           
            return list(set(results))  # Remove duplicates
       
        if not pattern:  # Handle empty pattern
            return []
           
        return dfs(self.root, 0)
def run_tests():
    # Initialize AutoComplete and add sample functions
    ac = AutoComplete()
    sample_functions = [
        "ResumePanel",
        "RegularContainer",
        "RidePrinter",
        "Container",
        "Panel",
        "AutoPanel"
    ]
   
    for func in sample_functions:
        ac.insert(func)
   
    # Define test cases with expected results
    test_cases = [
        ("R", ["ResumePanel", "RegularContainer", "RidePrinter"]),
        ("Re", ["ResumePanel", "RegularContainer"]),
        ("RP", ["ResumePanel", "RidePrinter"]),
        ("RPr", ["RidePrinter"]),
        ("", []),  # Edge case: empty pattern
        ("X", []),  # Edge case: no matches
        ("RPrint", ["RidePrinter"])  # Edge case: longer pattern
    ]
   
    # Run test cases
    print("Running test cases...")
    for pattern, expected in test_cases:
        result = ac.search(pattern)
        try:
            assert sorted(result) == sorted(expected), \
                f"Failed for pattern '{pattern}'. Expected {expected}, got {result}"
            print(f"✓ Pattern '{pattern}': {result}")
        except AssertionError as e:
            print(f"✗ Test failed: {str(e)}")
   
    # Additional edge cases
    print("\nTesting edge cases...")
   
    # Test case sensitivity
    result = ac.search("rp")
    assert result == [], "Case sensitivity test failed"
    print("✓ Case sensitivity test passed")
   
    # Test pattern longer than any word
    result = ac.search("RegularContainerExtra")
    assert result == [], "Long pattern test failed"
    print("✓ Long pattern test passed")
   
    # Test special characters
    ac.insert("Test#Function")
    result = ac.search("#")
    assert result == [], "Special character test failed"
    print("✓ Special character test passed")
if __name__ == "__main__":
    run_tests()
