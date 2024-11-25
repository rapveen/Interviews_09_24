### Part - 1 Q

we have logs,
each log contain info like user_id, page_id, timestamp. 
one activity requires one log line.
we are creating a file for one day of activity. 1 day:1 log file
we are looking for a function that returns the list of user_ids and they has to be loyal customer
-->loyal customer
   =› 2 days log, this customer has to visit on both days
   => during those 2 visits, the customer has to visit 2 unique pages. (different page_ids from the logs)
so bascially input is 2 log files returning a list of user_ids 

[
  {user_id: 1, page_id: 'A', timestamp: '2024-04-01T10:00:00Z'},
  {user_id: 2, page_id: 'B', timestamp: '2024-04-01T11:00:00Z'},
  {user_id: 1, page_id: 'C', timestamp: '2024-04-01T12:00:00Z'}
]

import java.util.*;

public class LoyalCustomers {
    
    /**
     * Finds loyal customers based on two days of log data.
     * 
     * @param day1Logs List of comma-separated log strings for Day 1.
     * @param day2Logs List of comma-separated log strings for Day 2.
     * @return List of user_ids who are loyal customers.
     */
    public static List<Integer> findLoyalCustomers(List<String> day1Logs, List<String> day2Logs) {
        // Map to store user_id to set of page_ids for Day 1
        Map<Integer, Set<String>> day1Map = new HashMap<>();
        // Populate day1Map by parsing Day 1 logs
        for (String log : day1Logs) {
            String[] parts = log.split(",");
            if (parts.length != 3) continue; // Skip invalid log entries
            int userId = Integer.parseInt(parts[0].trim());
            String pageId = parts[1].trim();
            day1Map.computeIfAbsent(userId, k -> new HashSet<>()).add(pageId);
        }
        
        // Map to store user_id to set of page_ids for Day 2
        Map<Integer, Set<String>> day2Map = new HashMap<>();
        // Populate day2Map by parsing Day 2 logs
        for (String log : day2Logs) {
            String[] parts = log.split(",");
            if (parts.length != 3) continue; // Skip invalid log entries
            int userId = Integer.parseInt(parts[0].trim());
            String pageId = parts[1].trim();
            day2Map.computeIfAbsent(userId, k -> new HashSet<>()).add(pageId);
        }
        
        List<Integer> loyalCustomers = new ArrayList<>();
        
        // Iterate through Day 1 users
        for (Map.Entry<Integer, Set<String>> entry : day1Map.entrySet()) {
            int userId = entry.getKey();
            // Check if user exists in Day 2
            if (day2Map.containsKey(userId)) {
                // Combine unique pages from both days
                Set<String> uniquePages = new HashSet<>(entry.getValue());
                uniquePages.addAll(day2Map.get(userId));
                // Check if unique pages are at least 2
                if (uniquePages.size() >= 2) {
                    loyalCustomers.add(userId);
                }
            }
        }
        
        return loyalCustomers;
    }
    
    // Example usage and test cases
    public static void main(String[] args) {
        List<String> day1Logs = Arrays.asList(
            "1,A,2024-04-01T10:00:00Z",
            "2,B,2024-04-01T11:00:00Z",
            "1,C,2024-04-01T12:00:00Z"
        );
        
        List<String> day2Logs = Arrays.asList(
            "1,A,2024-04-02T10:00:00Z",
            "2,B,2024-04-02T11:00:00Z",
            "3,D,2024-04-02T12:00:00Z"
        );
        
        List<Integer> loyalCustomers = findLoyalCustomers(day1Logs, day2Logs);
        System.out.println("Loyal Customers: " + loyalCustomers); // Output: [1]
        
        // Additional test case with edge cases
        List<String> day1LogsEdge = Arrays.asList(
            "4,E,2024-04-01T09:00:00Z",
            "4,F,2024-04-01T10:00:00Z",
            "5,G,2024-04-01T11:00:00Z",
            "6,H,2024-04-01T12:00:00Z"
        );
        
        List<String> day2LogsEdge = Arrays.asList(
            "4,E,2024-04-02T09:00:00Z",
            "4,G,2024-04-02T10:00:00Z",
            "5,G,2024-04-02T11:00:00Z",
            "7,I,2024-04-02T12:00:00Z"
        );
        
        List<Integer> loyalCustomersEdge = findLoyalCustomers(day1LogsEdge, day2LogsEdge);
        System.out.println("Loyal Customers (Edge Cases): " + loyalCustomersEdge); // Output: [4]
    }
}

Key Intuition:
HashSet ensures uniqueness: It helps consolidate pages visited across both days without manual duplication checks.
Condition ensures loyalty: By checking uniquePages.size() >= 2, only users meeting the minimum unique page requirement are considered loyal.
Logical flow: Combines data from both days incrementally and verifies the required conditions step by step.

Empty Log Files:

Scenario: One or both log files (day1Logs, day2Logs) are empty.
Handling:
If either day has no logs, no user can be loyal since loyalty requires presence on both days.
Result: Return an empty loyalCustomers list.
Invalid Log Formats:

Scenario: Log entries do not have exactly three comma-separated values.
Handling:
The code currently skips such entries (if (parts.length != 3) continue;).
Ensure logs with missing fields are ignored to prevent errors.
Potential Improvement: Log or report invalid entries for further inspection.
Non-integer user_ids:

Scenario: user_id fields contain non-integer values (e.g., letters or special characters).
Handling:
Integer.parseInt(parts[0].trim()) will throw a NumberFormatException.
Solution: Use try-catch to handle parsing errors and skip invalid entries.

### Part - 2 Q
 we need to extend this for 3 days and 3 unique pages, we will have 3 day logs, how to do that

same as previous 2 day Q, 
Optimal Data Structures:
Use HashSet to store unique page_ids per user_id.
Use three HashMap<Integer, HashSet<String>> to map user_id to their set of page_ids for each day.


### Part - 3 Q
what is the best optimal way if the days are N, how to do that give the optimal approach and tell the complexity analysis

Data Structures:
HashMap<Integer, UserActivity>:
Key: user_id
Value: UserActivity object containing:
Set<String> uniquePages: Tracks unique page_ids visited.
int daysAppeared: Counts the number of distinct days the user appears in.


as we just require to count the customers who have visited N unique pages across N days
    ==> customer can visit these N pages may be in 1 day or else spreading across many days.
so, we will have `daysAppeared` for customer tracking
`uniquePages.size()` has to be N

## here is best optimized approach,
Yes, we can optimize further by **removing one of the `HashSet`s**, specifically the per-user `HashSet` (`pagesSeenThisDay`) used to track unique pages for each user during a day. Instead, we can use a **single global `HashSet`** for all users on a given day, combining it with a smarter approach to manage unique pages and user appearance.

### **Key Observations:**
1. **Days Appeared:**
   - We only need to ensure that `daysAppeared` is incremented once per user per day. This can still be achieved using a `Set<Integer>` (`usersSeenThisDay`).

2. **Unique Pages:**
   - Instead of maintaining a `HashSet` of pages for each user, we can:
     - Use a single `HashSet<String>` for all users in the current day's processing.
     - Track user-specific unique pages incrementally in the `uniquePageCount`.

---

### **Optimized Algorithm:**

1. **Single Global `HashSet` for Unique Pages:**
   - Use a single global `HashSet<String>` (`globalPagesSet`) to track all unique `(user_id, page_id)` combinations for the current day.

2. **Steps:**
   - For each log:
     - Parse `user_id` and `page_id`.
     - Generate a unique key for the combination, e.g., `"userId_pageId"`.
     - If the key doesn’t exist in `globalPagesSet`, add it and increment the user’s `uniquePageCount`.

3. **Tracking Days Appeared:**
   - Use `usersSeenThisDay` (`Set<Integer>`) to ensure `daysAppeared` is only incremented once per user per day.

4. **Post-Processing:**
   - After processing all logs, filter users where:
     - `daysAppeared == N`.
     - `uniquePageCount >= N`.

---

### **Further Optimized Java Code:**

```java
import java.util.*;

public class LoyalCustomersMinimalMemory {

    /**
     * Represents user activity across multiple days.
     */
    static class UserActivity {
        int daysAppeared;
        int uniquePageCount;

        UserActivity() {
            this.daysAppeared = 0;
            this.uniquePageCount = 0;
        }
    }

    /**
     * Finds loyal customers based on N days of log data with minimal memory usage.
     * 
     * @param dayLogs List of log lists, each representing a day's logs.
     * @param N Number of days to evaluate.
     * @return List of user_ids who are loyal customers.
     */
    public static List<Integer> findLoyalCustomers(List<List<String>> dayLogs, int N) {
        // Map to store user_id to their activity across all days
        Map<Integer, UserActivity> userActivityMap = new HashMap<>();

        // Iterate through each day's logs
        for (List<String> logs : dayLogs) {
            Set<Integer> usersSeenThisDay = new HashSet<>(); // Track users seen for the current day
            Set<String> globalPagesSet = new HashSet<>();    // Track unique (user_id, page_id) combinations for the day

            for (String log : logs) {
                String[] parts = log.split(",");
                if (parts.length != 3) continue; // Skip invalid logs

                try {
                    int userId = Integer.parseInt(parts[0].trim());
                    if (userId <= 0) continue; // Skip invalid user_id
                    String pageId = parts[1].trim().toLowerCase(); // Normalize page_id to lowercase
                    if (pageId.isEmpty()) continue; // Skip empty page_id

                    // Unique key for the (user_id, page_id) combination
                    String pageKey = userId + "_" + pageId;

                    // Get or create UserActivity for userId
                    UserActivity activity = userActivityMap.getOrDefault(userId, new UserActivity());

                    // Increment daysAppeared only if this is the user's first log for the day
                    if (!usersSeenThisDay.contains(userId)) {
                        activity.daysAppeared++;
                        usersSeenThisDay.add(userId);
                    }

                    // Check if this (user_id, page_id) is a new unique combination for the day
                    if (!globalPagesSet.contains(pageKey)) {
                        activity.uniquePageCount++;
                        globalPagesSet.add(pageKey);
                    }

                    // Update the map
                    userActivityMap.put(userId, activity);
                } catch (NumberFormatException e) {
                    // Skip logs with non-integer user_id
                    continue;
                }
            }
        }

        // Post-processing: Filter users based on loyalty criteria
        List<Integer> loyalCustomers = new ArrayList<>();
        for (Map.Entry<Integer, UserActivity> entry : userActivityMap.entrySet()) {
            UserActivity activity = entry.getValue();
            if (activity.daysAppeared == N && activity.uniquePageCount >= N) {
                loyalCustomers.add(entry.getKey());
            }
        }

        return loyalCustomers;
    }

    // Example usage and test cases
    public static void main(String[] args) {
        // Define logs for 3 days
        List<String> day1Logs = Arrays.asList(
            "1,A,2024-04-01T10:00:00Z",
            "2,B,2024-04-01T11:00:00Z",
            "1,C,2024-04-01T12:00:00Z"
        );

        List<String> day2Logs = Arrays.asList(
            "1,A,2024-04-02T10:00:00Z",
            "2,B,2024-04-02T11:00:00Z",
            "3,D,2024-04-02T12:00:00Z"
        );

        List<String> day3Logs = Arrays.asList(
            "1,E,2024-04-03T10:00:00Z",
            "2,B,2024-04-03T11:00:00Z",
            "3,D,2024-04-03T12:00:00Z"
        );

        List<List<String>> allDayLogs = Arrays.asList(day1Logs, day2Logs, day3Logs);
        int N = 3;

        List<Integer> loyalCustomers = findLoyalCustomers(allDayLogs, N);
        System.out.println("Loyal Customers: " + loyalCustomers); // Output: [1]
    }
}
```

---

### **Why This is More Optimized:**

1. **Single Global HashSet (`globalPagesSet`):**
   - Tracks all unique `(user_id, page_id)` combinations for a single day.
   - Eliminates the need for a separate `HashSet` for each user.

2. **Incremental Updates:**
   - `uniquePageCount` is incremented directly using the global `HashSet`.
   - `daysAppeared` is updated only once per day using a per-day `Set` (`usersSeenThisDay`).

3. **Memory Efficiency:**
   - No persistent storage of `page_id`s per user.
   - Only one global `HashSet` for a single day, which is discarded after processing the day.

---

### **Complexity Analysis:**

1. **Time Complexity:**  
   - **O(T):** Total time complexity remains linear with respect to the total number of log entries (`T`).
   - Each log is processed once, and `HashSet` operations (insert and lookup) are O(1) on average.

2. **Space Complexity:**  
   - **O(U + T):**
     - **U:** Number of unique `user_id`s (stored in `userActivityMap`).
     - **T:** Temporary space for `globalPagesSet`, proportional to the total number of unique `(user_id, page_id)` combinations for a single day.

---

### **Advantages:**

- **Minimal HashSet Usage:** Reduces the number of `HashSet`s to one (`globalPagesSet`).
- **Scalable for Large Datasets:** Handles millions of logs with minimal memory overhead.
- **Simplicity:** Straightforward logic while maintaining optimal performance.