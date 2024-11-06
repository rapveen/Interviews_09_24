import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class VerifyAlienDictionaryOOM {

        public boolean isAlienSorted(String[] words, String order) {
            // Step 1: Collect unique characters from words
            Set<Character> neededChars = new HashSet<>();
            for (String word : words) {
                for (char c : word.toCharArray()) {
                    neededChars.add(c);
                }
            }
            
            // Step 2: Build partial ranking map for needed chars
            Map<Character, Integer> partialRank = new HashMap<>();
            int charCount = 0;
            
            // Process order string in chunks
            final int CHUNK_SIZE = 1024;
            char[] buffer = new char[CHUNK_SIZE];
            try (StringReader reader = new StringReader(order)) {
                int charsRead;
                while ((charsRead = reader.read(buffer, 0, CHUNK_SIZE)) != -1) {
                    for (int i = 0; i < charsRead; i++) {
                        char c = buffer[i];
                        if (neededChars.contains(c)) {
                            partialRank.put(c, charCount);
                        }
                        charCount++;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            
            // Step 3: Compare words using partial ranking
            for (int i = 0; i < words.length - 1; i++) {
                String word1 = words[i];
                String word2 = words[i + 1];
                
                int minLength = Math.min(word1.length(), word2.length());
                boolean foundDiff = false;
                
                for (int j = 0; j < minLength; j++) {
                    char c1 = word1.charAt(j);
                    char c2 = word2.charAt(j);
                    
                    if (c1 != c2) {
                        if (partialRank.get(c1) > partialRank.get(c2)) {
                            return false;
                        }
                        foundDiff = true;
                        break;
                    }
                }
                
                if (!foundDiff && word1.length() > word2.length()) {
                    return false;
                }
            }
            
            return true;
        }
    
 
    // Test case helper
    static void runTest(String[] words, String order, boolean expected) {
        VerifyAlienDictionaryOOM sol = new VerifyAlienDictionaryOOM();
        boolean result = sol.isAlienSorted(words, order);
        System.out.printf("Test case: words=%s, order='%s'\n", 
                         Arrays.toString(words), order);
        System.out.printf("Expected: %b, Got: %b, %s\n", 
                         expected, result, result == expected ? "PASS" : "FAIL");
        System.out.println("--------------------");
    }
 
    public static void main(String[] args) {
        // Test case 1: Basic valid case
        runTest(
            new String[]{"hello", "leetcode"},
            "hlabcdefgijkmnopqrstuvwxyz",
            true
        );
 
        // Test case 2: Invalid order
        runTest(
            new String[]{"word", "world", "row"},
            "worldabcefghijkmnpqstuvxyz",
            false
        );
 
        // Test case 3: Length comparison
        runTest(
            new String[]{"apple", "app"},
            "abcdefghijklmnopqrstuvwxyz",
            false
        );
 
        // Test case 4: Single word
        runTest(
            new String[]{"single"},
            "abcdefghijklmnopqrstuvwxyz",
            true
        );
 
        // Test case 5: Same words
        runTest(
            new String[]{"app", "app"},
            "abcdefghijklmnopqrstuvwxyz",
            true
        );
 
        // Test case 6: Complex case with multiple comparisons
        runTest(
            new String[]{"zxy", "wvu", "tsr"},
            "zyxwvutsrqponmlkjihgfedcba",
            true
        );
 
        // Test case 7: First character different
        runTest(
            new String[]{"zoo", "ant"},
            "zabcdefghijklmnopqrstuvwxy",
            false
        );
 
        // Test case 8: All characters same except last
        runTest(
            new String[]{"aaa", "aab"},
            "abcdefghijklmnopqrstuvwxyz",
            true
        );
 
        // Test case 9: Empty strings (if allowed by constraints)
        runTest(
            new String[]{"", "a"},
            "abcdefghijklmnopqrstuvwxyz",
            true
        );
 
        // Test case 10: Long words with common prefix
        runTest(
            new String[]{"applexxx", "appleyyy"},
            "abcdefghijklmnopqrstuvwxyz",
            true
        );
    }
 }