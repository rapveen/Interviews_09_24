Intuition
Identify Replacement Points: For each replacement operation, verify if the source string exists at the specified index in s.
Non-Overlapping Replacements: Since replacements don’t overlap, process them independently and ensure they don’t interfere with each other’s indices.
Brute Force Approach
Iterate and Replace: For each operation, check the substring at indices[i]. If it matches sources[i], perform the replacement.
Pros:
Simple to implement.
Easy to understand and debug.
Cons:
Repeated string manipulations are inefficient.
Handling multiple replacements can lead to incorrect indexing if not managed properly.
Optimized Solution
Sort Replacements:

int k = indices.length;
int[][] ops = new int[k][3];
for (int i = 0; i < k; i++) ops[i] = new int[]{indices[i], i};
Arrays.sort(ops, (a, b) -> b[0] - a[0]);


Benefit: Sorting in descending order prevents index shifting during replacements.
StringBuilder for Efficient Modifications:


StringBuilder sb = new StringBuilder(s);
for (int[] op : ops) {
    int idx = op[0];
    int i = op[1];
    if (s.startsWith(sources[i], idx)) {
        sb.replace(idx, idx + sources[i].length(), targets[i]);
    }
}
return sb.toString();


Benefit: StringBuilder allows efficient in-place modifications without creating multiple string copies.
HashMap for Quick Lookup:

Map<Integer, Integer> map = new HashMap<>();
for (int i = 0; i < k; i++) map.put(indices[i], i);
StringBuilder sb = new StringBuilder();
int i = 0;
while (i < s.length()) {
    if (map.containsKey(i)) {
        int idx = map.get(i);
        if (s.startsWith(sources[idx], i)) {
            sb.append(targets[idx]);
            i += sources[idx].length();
            continue;
        }
    }
    sb.append(s.charAt(i));
    i++;
}
return sb.toString();


Benefit: Single pass through s with constant-time lookup for replacements.
Real-World Implementation
Simultaneous Replacements: In production, ensure all replacements are determined before any modifications to maintain index integrity.
Concurrency Handling: Use thread-safe structures if replacements are processed in parallel.
Validation and Logging: Implement thorough validation of replacements and log actions for auditing and debugging.
Time and Space Complexity
Time Complexity:
Sorting: O(k log k)
Replacement: O(n + k) where n is the length of s
Total: O(k log k + n)
Space Complexity:
O(k) for storing replacement operations.
O(n) for the StringBuilder.
Total: O(n + k)
This approach ensures efficient and accurate replacements by minimizing unnecessary string operations and maintaining optimal time and space usage.
To perform simultaneous replacements without affecting the original indexing, follow these steps:
Mapping Replacements: Create a map where each index maps to its corresponding replacement operation (source and target). This allows quick lookup during iteration.
Iterate Through String: Traverse the original string s from left to right. At each position, check if there's a replacement starting at that index.
Validate and Replace: If a replacement exists and the source string matches the substring in s at that index, append the target string to the result and skip the length of the source string. Otherwise, append the current character.
Build the Result: Use a StringBuilder for efficient string concatenation.
This approach ensures that all replacements are applied simultaneously without index interference, achieving optimal time and space complexity.

class Solution {
    public String findReplaceString(String originalStr, int[] indices, String[] sources, String[] targets) {
        // Input validation
        if (originalStr == null || indices == null || sources == null || targets == null) {
            return "";
        }
       
        // Store replacements in sorted order: <index, [sourceLength, targetString]>
        TreeMap<Integer, Replacement> replacements = new TreeMap<>();
        for (int i = 0; i < indices.length; i++) {
            // Check if source matches at given index
            if (isValidReplacement(originalStr, indices[i], sources[i])) {
                replacements.put(indices[i], new Replacement(sources[i].length(), targets[i]));
            }
        }
       
        // Build result string
        StringBuilder result = new StringBuilder();
        int currentIndex = 0;
       
        // Process each valid replacement
        for (Map.Entry<Integer, Replacement> entry : replacements.entrySet()) {
            int replaceIndex = entry.getKey();
            Replacement rep = entry.getValue();
           
            // Copy characters from last position up to replacement
            result.append(originalStr.substring(currentIndex, replaceIndex));
           
            // Add replacement string
            result.append(rep.targetStr);
           
            // Update current position
            currentIndex = replaceIndex + rep.sourceLength;
        }
       
        // Append remaining characters
        result.append(originalStr.substring(currentIndex));
       
        return result.toString();
    }
   
    /**
     * Helper class to store replacement information
     */
    private static class Replacement {
        final int sourceLength;
        final String targetStr;
       
        Replacement(int sourceLength, String targetStr) {
            this.sourceLength = sourceLength;
            this.targetStr = targetStr;
        }
    }
   
    /**
     * Checks if source string matches original string at given index
     */
    private boolean isValidReplacement(String original, int index, String source) {
        // Check bounds
        if (index + source.length() > original.length()) {
            return false;
        }
       
        // Check if source matches at index
        return original.substring(index, index + source.length()).equals(source);
    }
}
