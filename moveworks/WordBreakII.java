package moveworks;

class WordBreakII {
    private Set<String> dict;
    private Map<Integer, List<String>> memo;
    private String s;
    
    public List<String> wordBreak(String s, List<String> wordDict) {
        // Initialize data structures
        this.dict = new HashSet<>(wordDict);
        this.memo = new HashMap<>();
        this.s = s;
        
        return wordBreakHelper(0);
    }
    
    private List<String> wordBreakHelper(int start) {
        // Check memoization first
        if (memo.containsKey(start)) {
            return memo.get(start);
        }
        
        List<String> result = new ArrayList<>();
        
        // Base case: reached end of string
        if (start == s.length()) {
            result.add("");
            return result;
        }
        
        // Try all possible words starting at current position
        for (int end = start + 1; end <= s.length(); end++) {
            String word = s.substring(start, end);
            
            // If word exists in dictionary, process remaining string
            if (dict.contains(word)) {
                List<String> sublist = wordBreakHelper(end);
                
                // Combine current word with all possible combinations of remaining string
                for (String sub : sublist) {
                    result.add(word + (sub.isEmpty() ? "" : " " + sub));
                }
            }
        }
        
        // Cache result before returning
        memo.put(start, result);
        return result;
    }
    
    // Helper method for input validation
    private boolean isValid(String s, List<String> wordDict) {
        if (s == null || s.isEmpty() || wordDict == null || wordDict.isEmpty()) {
            return false;
        }
        if (s.length() > 20 || wordDict.size() > 1000) {
            return false;
        }
        // Check if string and dictionary words contain only lowercase letters
        return s.matches("[a-z]+") && 
               wordDict.stream().allMatch(word -> 
                   word.matches("[a-z]+") && word.length() <= 10);
    }
}