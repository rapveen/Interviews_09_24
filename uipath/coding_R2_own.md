### Q 1
Maximum Streak
Description
A project manager wants to look at employee attendance data. Given that m employees are working on the project, and the manager has the record of the employees present on n days of the project, find the maximum number of consecutive days on which all employees were present and working on the project.
Example
m = 3
n = 5
data = [YYY, YYY, YNN, YYN, YYN]
There are 3 employees and 5 days attendance data. There are only two days, at the beginning of the period, where all workers are present. Using zero-based indexing for employee strings, employees 1 and 2 are absent on the third day, and employee 2 is also out on the fourth and fifth days. The maximum streak occurs at the beginning and is 2 days long.
Function Description
Complete the maxStreak function in the editor below. The function must return an integer denoting the maximum number of consecutive days where all the employees of the project are present.
maxStreak has the following parameters:
int m: the number of employees working on the project.
string data[n]: the value of each element datali] is a string where datalilfil denotes if the jth employee is present on the ith day. 

Constraints
• 1≤m ≤ 10
• 1≤n ≤ 100000
• Each data[i]li] € {'Y, 'N'}

• Input Format For Custom Testing
y Sample Case 0
Sample Input 0
Sample Output 0
STDIN

Function
2
→
m = 2
2

data []
Size n = 2
YN

data = [ "YN", "NN" J
NN


Explanation 0
There are no days in data which indicate that all the employees are present.


› Sample Case 1
Sample Input 1
Sample Output 1
STDIN
Function
3
→
m = 3
1
→
data|]
Size n = 1
NYY
→
data = [ "NYY" ]
Explanation 1
There are no days in data which indicate that all the employees are present.


y Sample Case 2
Sample Input 2
STDIN
Function
4
→
5
→
YNYY →
YYYY
YYYY
YYNY
NYYN
m = 4
data|]
Size n= 5
data = [ "YNYY", "YYYY", "YYYY", "YYNY" , "NYYN" ]
Sample Output 2
2
Explanation 2
The second and third days are the days on which all the employees were present and they are consecutive, so the result is 2.


Example:

m = 3, n = 5
data = ["YYY", "YYY", "YNN", "YYN", "YYN"]
Explanation:

Day 0: "YYY" → All present → Streak = 1
Day 1: "YYY" → All present → Streak = 2
Day 2: "YNN" → Not all present → Streak reset
Day 3: "YYN" → Not all present → Streak remains reset
Day 4: "YYN" → Not all present → Streak remains reset
Maximum Streak: 2
Optimal Algorithm Consideration
Pattern: Consecutive days with all 'Y' across all employees.
Optimal Data Structure: Iterate through the data array once, using simple variables to track the current streak and maximum streak.
Reason: Linear traversal is efficient given constraints (n up to 100,000).
Approach
Data Structure: Use simple integer variables to track current and maximum streaks.
Algorithm: Linear traversal of the data array.
Intuition:
Iterate through each day's attendance.
If all employees are present ('Y' in all positions), increment the current streak.
Otherwise, reset the current streak.
Keep updating the maximum streak found.

```
import java.io.*;
import java.util.*;

class Result {
    /*
     * Complete the 'maxStreak' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts following parameters:
     * 1. INTEGER m
     * 2. STRING_ARRAY data
     */
    public static int maxStreak(int m, List<String> data) {
        int maxStreak = 0; // To store the maximum streak found
        int currentStreak = 0; // To store the current streak count

        for (String day : data) { // Iterate through each day's attendance
            boolean allPresent = true; // Flag to check if all are present
            for (int i = 0; i < m; i++) { // Check each employee's status
                if (day.charAt(i) != 'Y') { // If any employee is absent
                    allPresent = false; // Set flag to false
                    break; // No need to check further
                }
            }
            if (allPresent) { // If all employees are present
                currentStreak++; // Increment current streak
                if (currentStreak > maxStreak) { // Update max streak if needed
                    maxStreak = currentStreak;
                }
            } else { // If not all present
                currentStreak = 0; // Reset current streak
            }
        }

        return maxStreak; // Return the maximum streak found
    }
}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        
        int m = Integer.parseInt(bufferedReader.readLine().trim()); // Number of employees
        int n = Integer.parseInt(bufferedReader.readLine().trim()); // Number of days

        List<String> data = new ArrayList<>();
        for (int i = 0; i < n; i++) { // Read attendance data for each day
            String day = bufferedReader.readLine().trim();
            if (day.length() != m) { // Ensure data length matches number of employees
                throw new IllegalArgumentException("Invalid attendance data length.");
            }
            data.add(day); // Add to data list
        }

        int result = Result.maxStreak(m, data); // Compute maximum streak
        System.out.println(result); // Output the result

        bufferedReader.close(); // Close the reader
    }
}
```

### Q 2

Balancing Elements
 
When an element is deleted from an array, the higher-indexed elements shift down one index to fill the gap. A "balancing element" is defined as an element that, when deleted from the array, results in the sum of the even-indexed elements being equal to the sum of the odd-indexed elements. Determine how many balancing elements a given array contains.
Example n=5
arr = [5, 5, 2, 5, 8]
When the first or second 5 is deleted, the array becomes [5, 2, 5, 8]. The sum[even] = 5 + 5 = 10
and sum[odd] = 2 + 8 = 10. No other elements of the original array have that property. There are
2 balancing elements: arr[0] and arr[1].
Function Description
Complete the function countBalancingElements in the editor below.
countBalancingElements has the following parameter(s): int arrn]: an integer array of size n
Returns:
int: an integer denoting the number of balancing elements in the input array
Constraints
• 1 ≤ n≤ 2*105
• 1 ≤ arr[i] ≤ 109

y Sample Case 0
Sample Input For Custom Testing
STDIN
Function
4
2
1
6
4
→
arr[] size n = 4
arr= 12, 1, 6, 4]
Sample Output
1
Explanation
When arr[1] = 1 is deleted, the array becomes [2, 6, 4]. The sum[even] = 2 + 4 = 6 and sum[odd]
= 6. No other elements of the original arrav have that propertv.

STDIN
Function
3
→
arr|]
size n = 3
2
→
arr] = [2, 2, 2]
2

2

Sample Case 1
Sample Input For Custom Testing
Sample Output
3
Explanation
The input array is [2, 2, 2]. All three elements of this array are balancing elements. After
deleting any of them, the array becomes [2, 2]. The sum[even] = 2 and sum[odd] = 2.


Optimal Algorithm Consideration
Prefix Sums: Utilize prefix sums to compute sums efficiently.
if an element is removed, then the odd indices become even even indices become odd.
so, 
//even_sum = left_even + right_odd
// odd_sum = left_odd + right_even

To accurately calculate the sums after deletion, adjust the formulas as follows:

Even Sum `(totalEven)`: Sum of even-indexed elements before `i` `(leftEven)` plus the sum of originally odd-indexed elements after `i` `(rightOdd)`.
Odd Sum `(totalOdd)`: Sum of odd-indexed elements before `i` `(leftOdd)` plus the sum of originally even-indexed elements after `i` `(rightEven)`.

```
import java.util.List;

class Result {
    /*
     * Complete the 'countBalancingElements' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts INTEGER_ARRAY arr as parameter.
     */
    public static int countBalancingElements(List<Integer> arr) {
        int n = arr.size();
        // Prefix sums for even and odd indices
        long[] prefixEven = new long[n];
        long[] prefixOdd = new long[n];
        
        // Initialize prefix sums
        prefixEven[0] = arr.get(0);
        prefixOdd[0] = 0;
        for (int i = 1; i < n; i++) {
            if (i % 2 == 0) {
                prefixEven[i] = prefixEven[i - 1] + arr.get(i);
                prefixOdd[i] = prefixOdd[i - 1];
            } else {
                prefixOdd[i] = prefixOdd[i - 1] + arr.get(i);
                prefixEven[i] = prefixEven[i - 1];
            }
        }
        
        int count = 0;
        // Iterate through each element to check balancing condition
        for (int i = 0; i < n; i++) {
            // Sum of even indices before the current index
            long leftEven = (i > 0) ? prefixEven[i - 1] : 0;
            // Sum of odd indices before the current index
            long leftOdd = (i > 0) ? prefixOdd[i - 1] : 0;
            
            // Sum after deletion
            long rightEven = (i < n) ? prefixOdd[n - 1] - prefixOdd[i] : 0;
            long rightOdd = (i < n) ? prefixEven[n - 1] - prefixEven[i] : 0;
            
            // Total sums after deletion
            long totalEven = leftEven + rightOdd;
            long totalOdd = leftOdd + rightEven;
            
            // Check if sums are equal
            if (totalEven == totalOdd) {
                count++;
            }
        }
        
        return count;
    }
}

public class Solution {
    public static void main(String[] args) throws IOException {
        // Example usage:
        List<Integer> arr1 = List.of(5, 5, 2, 5, 8);
        System.out.println(Result.countBalancingElements(arr1)); // Output: 2

        List<Integer> arr2 = List.of(12, 1, 6, 4);
        System.out.println(Result.countBalancingElements(arr2)); // Output: 1

        List<Integer> arr3 = List.of(2, 2, 2);
        System.out.println(Result.countBalancingElements(arr3)); // Output: 3
    }
}

```


### asked if I had solved any challenging problem
### when I said about HVLC problem, he asked how did I analyse the SQL query? any software?
    -> I said I've used SQL provided ANALYZE function to do it.