3 questions were asked
### Q 1

Long Encoded String
Description
Consider a string that consists of lowercase English letters (i.e., [a-z)) only. The following rules are used to encode all of its characters into the string s.
• a is encoded as 1, b is encoded as 2, c is encoded as 3, .., and i is encoded as 9.
• jis encoded as 10#, k is encoded as 11#, I is encoded as 12#,., and z is encoded as 26#.
• If any character occurs two or more consecutively, its count immediately follows the encoded character in parentheses, e.g. 'aa" is encoded as 1(2)'.
Examples
• String "abzx" is encoded as s = "1226#24#".
• String "aabccc" is encoded as s = "1(2)23(3)".
• String "bajj" is encoded as s = "2110#(2)*.
• String "wwxyzwww" is encoded as s = 23#(2)24#25#26#23#(3)".
Given an encoded string s, determine the character counts for each letter of the original, decoded string. Return array of 26 integers where index 0 contains the number of 'a' characters, index 1 contains the number of 'b' characters, and so
on.
Function Description
Complete the frequency function in the editor below.
frequency has the following parameter:
strings: an encoded string

Return
int[26]: the character frequencies as described
Constraints
• String s consists of decimal integers from 0 to 9, #s, and O's only.
• 1 ≤ length of s ≤ 105
• It is guaranteed that string s is a valid encoded string.
• 2 ≤ c ≤ 104,
*, where c is a parenthetical count of consecutive occurrences of an encoded character.
• Input Format For Custom Testing
y Sample Case O
Sample Input For Custom Testing
1226#24#
Sample Output 0
11000000000000000000000101

› Sample Case 1
Sample Input 1
1(2)23(3)
Sample Output 1
21300000000000000000000000

› Sample Case 2
Sample Input 2
2110# (2)
Sample Output 2
11000000020000000000000000

Sample Case 3

Sample Input 3

23# (2) 24#25#26#23# (3)



Sample Output 3

000000000000000005111


public class LongEncodedString {
    public static int[] frequency(String s) {
        int[] freq = new int[26]; // Frequency array for 'a' to 'z'
        int i = 0; // Pointer to traverse the string

        while (i < s.length()) {
            int num = 0; // To store the numeric value of the character
            // Check if it's a two-digit number followed by '#'
            if (i + 2 < s.length() && s.charAt(i + 2) == '#') {
                num = (s.charAt(i) - '0') * 10 + (s.charAt(i + 1) - '0');
                i += 3; // Move past the two digits and '#'
            } else {
                num = s.charAt(i) - '0';
                i += 1; // Move past the single digit
            }

            // Calculate the corresponding character
            char ch = (char) ('a' + num - 1);

            int count = 1; // Default count is 1
            // Check if the next character is '(' indicating a count
            if (i < s.length() && s.charAt(i) == '(') {
                i++; // Move past '('
                int start = i;
                // Find the closing ')'
                while (i < s.length() && s.charAt(i) != ')') {
                    i++;
                }
                // Extract the number inside parentheses
                count = Integer.parseInt(s.substring(start, i));
                i++; // Move past ')'
            }

            freq[ch - 'a'] += count; // Update frequency
        }

        return freq;
    }

    // Example usage and test cases
    public static void main(String[] args) {
        String[] testInputs = {
            "1226#24#",
            "1(2)23(3)",
            "2110#(2)",
            "23#(2)24#25#26#23#(3)"
        };

        for (String input : testInputs) {
            int[] result = frequency(input);
            // Print the frequency array
            for (int count : result) {
                System.out.print(count);
            }
            System.out.println();
        }
    }
}

Note: All went well but I forgot to write `i++` at line `92`
Due to that I ate some time there.
He is more keen on TC & SC
### Q 2

A sales executive must sell various items in a bag, where each item has an ID number.
It is easier to sell items with the same ID numbers, and some maximum number of items can be removed from the bag.
Find the minimum number of different IDs the final bag can contain after removing the allowed number of items.
Example
The bag contains n = 6 items with ids = [1,1,1,2,2,3], and m = 2 items can be removed.
If two items of type 1 are removed, all three types remain. It is better to remove either two items of type 2 or one item
each of types 2 and 3. Either choice leaves two types: ids = [1,1,1,3] or ids = [1,1,1,2].
Function Description
Complete the function deleteProducts in the editor below.
deleteProducts has the following parameters:
int ids[n]: the ID numbers of the items
int m: the maximum number of items that can be removed from the bag
Returns
int: the minimum number of different IDs the final bag can contain
Constraints
• 1 ≤ n ≤ 100000
• 1 ≤ ids] ≤ 1000000
• 1≤ m ≤ 100000

y Sample Case 0
Sample Input 0
STDIN
Function
4
1
1
1
1
2
elements in ids] n = 4
ids = [1, 1, 1, 1]
m = 2
Sample Output O
1
Explanation 0
Initially, the bag contains 4 items of the same type. Whichever item is removed, the minimum number of IDs is 1.

› Sample Case 1
Sample Input 1
STDIN
Function
6
1
2
3
1
2
elements in ids[] n = 6
ids = [1, 2, 3, 1, 2, 21
3
m = 3
Sample Output 1
1
Explanation 1
It is optimal to remove items with IDs 1 and 3 to leave 3 items with ID = 2.

import java.util.*;
import java.io.*;

public class SmartSale {

    /**
     * Function to find the minimum number of different IDs after removing up to m items.
     *
     * @param ids Array of item IDs.
     * @param m   Maximum number of items that can be removed.
     * @return Minimum number of unique IDs remaining.
     */
    public static int deleteProducts(int[] ids, int m) {
        // Step 1: Count frequency of each ID using HashMap
        HashMap<Integer, Integer> frequencyMap = new HashMap<>();
        for (int id : ids) {
            frequencyMap.put(id, frequencyMap.getOrDefault(id, 0) + 1);
        }

        // Step 2: Insert all frequencies into a min-heap (PriorityQueue)
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(frequencyMap.values());

        // Step 3: Remove items starting from the smallest frequency
        while (m > 0 && !minHeap.isEmpty()) {
            int freq = minHeap.peek();
            if (freq <= m) {
                m -= freq;       // Remove all items of this ID
                minHeap.poll();  // Remove the ID from the heap
            } else {
                break; // Cannot remove this ID completely
            }
        }

        // Step 4: The remaining size of the heap is the answer
        return minHeap.size();
    }

    // Main method to execute the function with sample inputs
    public static void main(String[] args) throws IOException {
        // Sample Input 0
        int[] ids0 = {1, 1, 1, 1};
        int m0 = 2;
        System.out.println(deleteProducts(ids0, m0)); // Expected Output: 1

        // Sample Input 1
        int[] ids1 = {1, 2, 3, 1, 2, 2};
        int m1 = 3;
        System.out.println(deleteProducts(ids1, m1)); // Expected Output: 1

        // Additional Edge Cases
        // All IDs unique
        int[] ids2 = {1, 2, 3, 4, 5};
        int m2 = 3;
        System.out.println(deleteProducts(ids2, m2)); // Expected Output: 2

        // m is zero
        int[] ids3 = {1, 1, 2, 2, 3, 3};
        int m3 = 0;
        System.out.println(deleteProducts(ids3, m3)); // Expected Output: 3

        // m exceeds total items
        int[] ids4 = {1, 1, 2};
        int m4 = 5;
        System.out.println(deleteProducts(ids4, m4)); // Expected Output: 0
    }
}

### Q 3

Meetup Schedule
Description
A start-up owner is looking to meet new investors to get some funds for the company. Each investor has a tight schedule that the owner has to respect. Given the schedules of the days investors are available, determine how many meetings the owner can schedule. Note that the owner can only have one meeting per day.
The schedules consist of two integer arrays: firstDay, and lastDay, aligned by index. Each element in the array firstDay represents the first day an investor is available, and each element in lastDay represents the last day an investor is available, both inclusive.
Example:
firstDay = [1,2,3,3,3]
lastDay= [2,2,3,4,4]
• There are 5 investors [I-0, 1-1, 1-2, I-3, 1-4]
• The investor I-0 is available from day 1 to day 2 inclusive [1, 2]
• The investor I-1 is available in day 2 only [2, 2]. The investor I-2 is available in day 3 only [3, 3]
• The investors 1-3 and 1-4 are available from day 3 to day 4 only [3, 4]
• The owner can only meet 4 investors out of 5: I-0 in day 1, I-1 in day 2, 1-2 in day 3 and I-3 in day 4. The graphic below shows the scheduled meetings in green and blocked days are in gray.

Function Description
Complete the function countMeetings in the editor below.
countMeetings has the following parameters:
int firstDayIn]: firstDayli is the first day the ith investor is available to meet. int lastDay[n]. lastDayli is the last day the th investor is available to meet.
Returns:
int: the maximum number of meetings possible
Constraints
• 1 ≤ n ≤ 105
• 1 ≤ firstDay[i, lastDayli ≤ 105 (where 0 ≤ i < n)
• firstDayli] ≤ lastDayli] (where 0 ≤ i < n)
• Input Format For Custom Testing
› Sample Case 0
Sample Input For Custom Testing
STDIN
Function
3
1
1
2
3
1
2
2
→
→
firstDay [] size n = 3
firstDay= [1, 1, 21
→
→
LastDay|] size n = 3
lastDay = [1, 2, 2]

Explanation

• There are 3 investors [I-0, I-1, 1-2]

• The investor I-0 is available in day 1 inclusive : [1, 1]

• The investor I-1 is available from day 1 to day 2 : [1, 2]

• The investors I-2 is available in day 2: [2, 2]

• The owner can only meet 2 investors out of 3: I-0 in day 1, 1-2 in day 2. The graphic below shows the
scheduled meetings in green and blacked days in gray

Note: Only 5 mins left for this question, so he asked me to just provide a psuedoCode only.
import java.util.*;

public class MeetupSchedule {
    
    public static int countMeetings(int[] firstDay, int[] lastDay) {
        int n = firstDay.length;
        // Pair each investor's first and last day
        Investor[] investors = new Investor[n];
        for (int i = 0; i < n; i++) {
            investors[i] = new Investor(firstDay[i], lastDay[i]);
        }
        
        // Sort investors by their last available day
        Arrays.sort(investors, Comparator.comparingInt(a -> a.last));
        
        // Initialize TreeSet with all possible days
        TreeSet<Integer> availableDays = new TreeSet<>();
        for (int day = 1; day <= 100005; day++) { // Assuming day ranges up to 10^5
            availableDays.add(day);
        }
        
        int meetings = 0;
        
        // Assign days to investors
        for (Investor inv : investors) {
            // Find the earliest available day >= firstDay
            Integer day = availableDays.ceiling(inv.first);
            if (day != null && day <= inv.last) {
                meetings++;
                availableDays.remove(day); // Day is now occupied
            }
        }
        
        return meetings;
    }
    
    // Helper class to store investor's availability
    static class Investor {
        int first;
        int last;
        
        Investor(int first, int last) {
            this.first = first;
            this.last = last;
        }
    }
    
    // Example usage
    public static void main(String[] args) {
        int[] firstDay = {1, 2, 3, 3, 3};
        int[] lastDay = {2, 2, 3, 4, 4};
        System.out.println(countMeetings(firstDay, lastDay)); // Output: 4
        
        int[] firstDay2 = {1, 1, 2};
        int[] lastDay2 = {1, 2, 2};
        System.out.println(countMeetings(firstDay2, lastDay2)); // Output: 2
    }
}

