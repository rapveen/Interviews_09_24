class Solution {
    public String[] shortestSubstrings(String[] arr) {
        int n = arr.length;
        String[] answer = new String[n];
        
        // Step 1: Precompute all substrings and their occurrences
        Map<String, Set<Integer>> substringIndices = new HashMap<>();
        
        // For each string in array
        for (int i = 0; i < n; i++) {
            String s = arr[i];
            // Generate all possible substrings
            for (int len = 1; len <= s.length(); len++) {
                for (int start = 0; start <= s.length() - len; start++) {
                    String substr = s.substring(start, start + len);
                    substringIndices.computeIfAbsent(substr, k -> new HashSet<>()).add(i);
                }
            }
        }
        
        // Step 2: Find shortest unique substring for each string
        for (int i = 0; i < n; i++) {
            String s = arr[i];
            String shortestUnique = "";
            
            // Try all possible lengths
            outerLoop:
            for (int len = 1; len <= s.length(); len++) {
                TreeSet<String> candidates = new TreeSet<>(); // For lexicographical ordering
                
                // Generate substrings of current length
                for (int start = 0; start <= s.length() - len; start++) {
                    String substr = s.substring(start, start + len);
                    Set<Integer> indices = substringIndices.get(substr);
                    
                    // Check if substring is unique to current string
                    if (indices.size() == 1 && indices.contains(i)) {
                        candidates.add(substr);
                    }
                }
                
                // If found unique substrings of current length
                if (!candidates.isEmpty()) {
                    shortestUnique = candidates.first(); // Get lexicographically smallest
                    break outerLoop;
                }
            }
            
            answer[i] = shortestUnique;
        }
        
        return answer;
    }

    // Optional optimization: Cache generator for substrings of a given length
    private List<String> generateSubstrings(String s, int length) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i <= s.length() - length; i++) {
            result.add(s.substring(i, i + length));
        }
        return result;
    }
}