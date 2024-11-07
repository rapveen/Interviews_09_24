package moveworks;

class MaxRemovableCharacters {
    public int maximumRemovals(String s, String p, int[] removable) {
        // Binary search bounds
        int left = 0;
        int right = removable.length;
        
        // Convert strings to char arrays for faster access
        char[] sChars = s.toCharArray();
        char[] pChars = p.toCharArray();
        
        while (left < right) {
            int mid = left + (right - left + 1) / 2;
            
            // Create temporary array to mark removed characters
            boolean[] removed = new boolean[s.length()];
            // Mark first mid characters as removed
            for (int i = 0; i < mid; i++) {
                removed[removable[i]] = true;
            }
            
            // If p is still subsequence with mid removals, try more removals
            if (isSubsequence(sChars, pChars, removed)) {
                left = mid;
            } else {
                // If not subsequence, try fewer removals
                right = mid - 1;
            }
        }
        
        return left;
    }
    
    // Helper method to check if p is subsequence of s with removed characters
    private boolean isSubsequence(char[] s, char[] p, boolean[] removed) {
        int j = 0; // index for p
        
        // Try to find each character of p in s
        for (int i = 0; i < s.length && j < p.length; i++) {
            // Skip removed characters
            if (removed[i]) continue;
            
            // If current characters match, move to next character in p
            if (s[i] == p[j]) {
                j++;
            }
        }
        
        // Return true if we found all characters of p
        return j == p.length;
    }
}