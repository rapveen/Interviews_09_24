class VerifyAlienDictionary {
    public boolean isAlienSorted(String[] words, String order) {
        // Create letter to rank mapping
        int[] letterRank = new int[26];
        for (int i = 0; i < order.length(); i++) {
            letterRank[order.charAt(i) - 'a'] = i;
        }
        
        // Compare adjacent words
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];
            
            // Find the length of shorter word
            int minLength = Math.min(word1.length(), word2.length());
            
            // Flag to track if we found different characters
            boolean foundDiff = false;
            
            // Compare characters
            for (int j = 0; j < minLength; j++) {
                char c1 = word1.charAt(j);
                char c2 = word2.charAt(j);
                
                if (c1 != c2) {
                    // Different characters found, check their ranks
                    if (letterRank[c1 - 'a'] > letterRank[c2 - 'a']) {
                        return false;
                    }
                    foundDiff = true;
                    break;
                }
            }
            
            // If no different characters found and first word is longer
            if (!foundDiff && word1.length() > word2.length()) {
                return false;
            }
        }
        
        return true;
    }
}


/*
Test cases:
1. ["hello","leetcode"], "hlabcdefgijkmnopqrstuvwxyz" => true
2. ["word","world","row"], "worldabcefghijkmnpqstuvxyz" => false
3. ["apple","app"], "abcdefghijklmnopqrstuvwxyz" => false
4. ["aa","aa"], "abcdefghijklmnopqrstuvwxyz" => true
5. ["zxy","abc"], "abcdefghijklmnopqrstuvwxyz" => false
6. ["single"], "abcdefghijklmnopqrstuvwxyz" => true
*/

