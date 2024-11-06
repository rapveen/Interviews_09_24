/* 
Pattern matching *, .

.: Matches any single character.
*: Matches zero or more of the preceding element.
The solution should determine if the pattern p matches the entire string s.
Approach: Dynamic Programming (DP)
We'll solve this problem using Dynamic Programming because the structure of the problem has overlapping subproblems that can be broken down into smaller, manageable parts.
Key Idea:
We use a DP table dp[i][j] to store whether the substring s[0:i] matches the pattern p[0:j].
Important Points:
. matches any single character: If the current character in p is ., it matches any character in s.
* matches zero or more of the preceding character: If the current character in p is *, it can either:
Match zero occurrences of the preceding character.
Match one or more occurrences of the preceding character.
Transitions:
If p[j-1] == s[i-1] or p[j-1] == '.': This is a direct match, so we propagate from dp[i-1][j-1].
If p[j-1] == '*': Here, we have two possibilities:
Match zero occurrences of the preceding character: This means ignoring the * and its preceding character, so we propagate from dp[i][j-2].
Match one or more occurrences of the preceding character: This means checking if the preceding character matches, and if it does, we propagate from dp[i-1][j].
Steps:
Create a DP table with dimensions (len(s)+1) x (len(p)+1).
Initialize the base cases:
dp[0][0] = true because an empty string matches an empty pattern.
For patterns that consist entirely of elements like a*, a*b*, etc., initialize dp[0][j].
Fill the DP table based on the transitions.
Return the result in dp[len(s)][len(p)].

 */

 
public class pattern_matching_2 {
    public boolean isMatch(String s, String p) {
        int m = s.length();
        int n = p.length();
       
        // dp[i][j] means whether s[0:i] matches p[0:j]
        boolean[][] dp = new boolean[m + 1][n + 1];
       
        // Base case: empty string and empty pattern match
        dp[0][0] = true;
       
        // Handle patterns like a*, a*b*, etc., that can match an empty string
        for (int j = 2; j <= n; j += 2) {
            if (p.charAt(j - 1) == '*' && dp[0][j - 2]) {
                dp[0][j] = true;
            }
        }
       
        // Fill the DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                // Case 1: Current characters match, or pattern has '.'
                if (p.charAt(j - 1) == s.charAt(i - 1) || p.charAt(j - 1) == '.') {
                    dp[i][j] = dp[i - 1][j - 1];
                }
                // Case 2: Current pattern character is '*'
                else if (p.charAt(j - 1) == '*') {
                    // We can ignore the pattern character and the preceding one (zero occurrences)
                    dp[i][j] = dp[i][j - 2];
                   
                    // Or if the previous character in the pattern matches the current character in the string
                    // or the previous character in the pattern is '.', then check for one or more occurrences
                    if (p.charAt(j - 2) == s.charAt(i - 1) || p.charAt(j - 2) == '.') {
                        dp[i][j] = dp[i][j] || dp[i - 1][j];
                    }
                }
            }
        }
       
        // The result is in dp[m][n]
        return dp[m][n];
    }
}
