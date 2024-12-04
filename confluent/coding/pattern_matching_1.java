/* 
Pattern matching *, ?


handle the two special characters:
?: Matches any single character.
*: Matches any sequence of characters (including an empty sequence).


Approach: Dynamic Programming (DP)
We can approach this problem using Dynamic Programming (DP), which helps us break down the problem into smaller subproblems and use their results to solve the overall problem.
Key Idea:
We use a DP table dp[i][j], where:
i represents the length of the string s processed so far.
j represents the length of the pattern p processed so far.
dp[i][j] is true if the substring s[0:i] matches the pattern p[0:j].
Base Case:
dp[0][0] = true: This means an empty pattern matches an empty string.
dp[i][0] = false for all i > 0: A non-empty string cannot be matched by an empty pattern.
dp[0][j]: Only when the pattern consists entirely of *s can it match an empty string.
Transitions:
If p[j-1] == s[i-1] or p[j-1] == '?':
We can treat this as a match for this position, so we propagate the result from dp[i-1][j-1] to dp[i][j].
If p[j-1] == '*':
We have two choices:
Treat * as matching an empty sequence: In this case, we check dp[i][j-1].
Treat * as matching the current character: In this case, we check dp[i-1][j].
Steps to Solve:
Create a DP table with dimensions (len(s) + 1) x (len(p) + 1) and initialize all entries to false.
Handle the base cases as described.
Fill the DP table based on the transitions outlined above.
The final answer will be in dp[len(s)][len(p)], indicating whether the entire string s matches the pattern p.

*/

public class pattern_matching_1 {
    public boolean isMatch(String s, String p) {
        int m = s.length();
        int n = p.length();
       
        // Create a DP table with dimensions (m+1) x (n+1)
        boolean[][] dp = new boolean[m + 1][n + 1];
       
        // Base case: empty string and empty pattern match
        dp[0][0] = true;
       
        // Base case: Handle patterns starting with '*' to match an empty string
        for (int j = 1; j <= n; j++) {
            if (p.charAt(j - 1) == '*') {
                dp[0][j] = dp[0][j - 1];  // '*' can match an empty string
            }
        }
       
        // Fill the DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                // Case 1: Exact match or '?'
                if (p.charAt(j - 1) == s.charAt(i - 1) || p.charAt(j - 1) == '?') {
                    dp[i][j] = dp[i - 1][j - 1];
                }
                // Case 2: '*' can match zero or more characters
                else if (p.charAt(j - 1) == '*') {
                    dp[i][j] = dp[i][j - 1] || dp[i - 1][j];
                }
            }
        }
       
        // The result is in dp[m][n], where m = length of s, n = length of p
        return dp[m][n];
    }
}

//Time Complexity: O(m×n)
//Space Complexity: O(m×n)
