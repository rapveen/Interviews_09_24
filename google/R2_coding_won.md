Given on-call rotation schedule for multiple people by: their unique name, start time (timestamp O-max long) and end time (timestamp 0-max long) of the rotation:

Example input:

Name | Start | End

Abby |   10  | 100
Ben  |   50  | 70
Carla| 60    | 120
David | 150  | 300

Your goal is to return a rotation table without overlapping periods representing who is on call during that time.
Return "Start time", "End time" and list of on-call people:
Do not include time periods that do not have on-call people. Specifically in the given example, this means excluding the period between 120-150.

Example output:

Start | End | Names


10 | 50| Abby

50 | 60 I Abby, Ben

60| 70 | Abby, Ben, Carla

70 | 100 | Abby, Carla

100 | 120 | Carla

150 300 | David



import java.util.*;

public class OnCallRotation {
    // Event class to represent start or end of a rotation
    static class Event implements Comparable<Event> {
        long time;
        boolean isStart;
        String name;

        Event(long time, boolean isStart, String name) {
            this.time = time;
            this.isStart = isStart;
            this.name = name;
        }

        // Sort by time, end events before start if times are equal
        public int compareTo(Event other) {
            if (this.time != other.time) {
                return Long.compare(this.time, other.time);
            }
            if (this.isStart == other.isStart) {
                return 0;
            }
            return this.isStart ? 1 : -1;
        }
    }

    public static List<String> getRotationTable(List<String[]> schedules) {
        List<Event> events = new ArrayList<>();
        // Parse input schedules into events
        for (String[] schedule : schedules) {
            String name = schedule[0];
            long start = Long.parseLong(schedule[1]);
            long end = Long.parseLong(schedule[2]);
            events.add(new Event(start, true, name));
            events.add(new Event(end, false, name));
        }

        // Sort events
        Collections.sort(events);

        List<String> result = new ArrayList<>();
        Set<String> active = new TreeSet<>();
        Long prevTime = null;

        for (Event event : events) {
            if (prevTime != null && prevTime < event.time && !active.isEmpty()) {
                // Build interval string
                StringBuilder sb = new StringBuilder();
                sb.append(prevTime).append(" | ").append(event.time).append(" | ");
                sb.append(String.join(", ", active));
                result.add(sb.toString());
            }

            // Update active set
            if (event.isStart) {
                active.add(event.name);
            } else {
                active.remove(event.name);
            }
            prevTime = event.time;
        }
        return result;
    }

    // Example usage with input and output
    public static void main(String[] args) {
        List<String[]> schedules = Arrays.asList(
            new String[]{"Abby", "10", "100"},
            new String[]{"Ben", "50", "70"},
            new String[]{"Carla", "60", "120"},
            new String[]{"David", "150", "300"}
        );

        List<String> rotationTable = getRotationTable(schedules);
        System.out.println("Start | End | Names");
        for (String interval : rotationTable) {
            System.out.println(interval);
        }
    }
}



### Followup

explain the dry run with an edge case example
A 10 70
B 50 70

what is the output?

what is the time complexity
-> I said nlogn
-> But said think twise and pointed a line
```
 sb.append(prevTime).append(" | ").append(event.time).append(" | ");
 sb.append(String.join(", ", active));
```
The complexity of the line:

```java
sb.append(prevTime).append(" | ").append(event.time).append(" | ");
```

**Explanation:**
1. **`sb.append(prevTime)`**
   - Appends the string representation of `prevTime` to the `StringBuilder`.
   - Converting a number (`long`) to a string is an **O(d)** operation, where `d` is the number of digits in the number (logarithmic in the value of `prevTime`).

2. **`sb.append(" | ")`**
   - Appends a constant string `" | "` to the `StringBuilder`.
   - This operation is **O(1)** because the length of the string is fixed.

3. **`sb.append(event.time)`**
   - Similar to `prevTime`, appends the string representation of `event.time`, which is also **O(d)**.

4. **Another `sb.append(" | ")`**
   - Again, **O(1)**.

**Overall Complexity of the Line:**
- Appending multiple strings or numbers to a `StringBuilder` is **O(total characters appended)**.
- For this line:
  - Converting `prevTime` and `event.time` to strings each takes **O(d)**, where `d = O(log(prevTime))` or `O(log(event.time))`.
  - Adding fixed strings `" | "` is **O(1)** each.
- Total complexity for the line = **O(d) + O(1) + O(d) + O(1) = O(d)**.

**In Context of the Loop:**
- This line is executed once per interval.
- Across all intervals, the cost of this operation depends on the number of intervals and the maximum number of digits in the timestamps being processed. If there are `K` intervals and the maximum number of digits in a timestamp is `d`, the total complexity is **O(K × d)**.


### but asked what is the worst case complexity here for a very large input
Certainly. Let's perform a precise worst-case complexity analysis for the operation:

```java
sb.append(String.join(", ", active));
```

### **Understanding the Operation**

- **`String.join(", ", active)`**:
  - **Purpose:** Concatenates all elements in the `active` set, separated by `", "`.
  - **Components:**
    - **Separator:** `", "` (constant length of 2 characters).
    - **Elements:** All strings in the `active` set.

### **Variables Definition**

- **`N`:** Total number of on-call schedules.
- **`K`:** Total number of intervals (events) generated, which is at most `2N` (each schedule has a start and end event).
- **`n`:** Maximum number of active on-call people at any interval (in the worst case, `n = N`).
- **`m`:** Maximum length of a name string.

### **Step-by-Step Complexity Analysis**

1. **Iterating Through the `active` Set:**
   - **Operation:** The `String.join` method iterates over each element in the `active` set.
   - **Time Complexity:** O(n), where `n` is the number of active names.
   
2. **Calculating Total Length of the Joined String:**
   - **Operation:** For each name, its length (`m`) is considered, and separators are added.
   - **Time Complexity:** O(n * m) because each of the `n` names of maximum length `m` is processed, and `n-1` separators of constant length are added.

3. **Appending to the `StringBuilder`:**
   - **Operation:** The resulting string from `String.join` is appended to the `StringBuilder`.
   - **Time Complexity:** O(n * m) as it involves copying the characters of the joined string.

4. **Overall Per `String.join` Call:**
   - **Time Complexity:** O(n) + O(n * m) + O(n * m) = **O(n * m)**
   
5. **Loop Execution:**
   - **Operation:** The above `String.join` is executed for each of the `K` intervals.
   - **Time Complexity:** O(K * n * m)

### **Worst-Case Scenario**

- **Maximum Active Set Size (`n`):** All `N` schedules overlap completely, making `n = N`.
- **Maximum Number of Intervals (`K`):** `2N` (each schedule contributes a start and end event).
- **Maximum Name Length (`m`):** Considered as a constant or proportional to input size.

### **Final Worst-Case Complexity**

1. **Time Complexity:**
   - Substituting the worst-case values:
     - **O(K * n * m) = O(2N * N * m) = O(N² * m)**
   - **Thus, the worst-case time complexity is** **O(N² * m)**.

2. **Space Complexity:**
   - **Active Set (`active`):** Stores up to `N` names, each of length `m`.
     - **O(N * m)**
   - **Result List (`result`):** Stores up to `K` intervals, each containing start time, end time, and concatenated names.
     - **O(K * (m * N)) = O(N * (m * N)) = O(N² * m)**
   - **Overall Space Complexity:** **O(N² * m)**

### **Summary**

- **Worst-Case Time Complexity:** **O(N² * m)**
  - Occurs when all `N` schedules overlap, resulting in `n = N` and `K = 2N`.

- **Worst-Case Space Complexity:** **O(N² * m)**
  - Due to storing the concatenated strings for each of the `K` intervals.

### **Implications**

- **Scalability:** The quadratic time complexity means that the solution may become inefficient for large values of `N`, especially if `m` is also large.
- **Optimization Considerations:** While the sweep line algorithm is efficient for many practical purposes, for extremely large datasets with high overlap, alternative methods or optimizations (like minimizing string concatenations) might be necessary to improve performance.


### why are using a Set but need a TreeSet right
-> Yes need a treeset here

We used a **TreeSet** in the code for the following reasons:

### 1. **Maintaining a Sorted Order**
   - A **TreeSet** automatically maintains its elements in sorted order.
   - This ensures that the names of on-call people are always sorted lexicographically, making the output list easy to read and consistent.

   **Example:**
   If `active` contains `{Ben, Abby}`, `TreeSet` will ensure it is stored as `{Abby, Ben}`.

### 2. **Efficient Add, Remove, and Contains Operations**
   - A **TreeSet** provides efficient operations:
     - **Add/Remove:** O(log n)
     - **Check for Presence:** O(log n)
   - These operations are important because during the sweep line algorithm:
     - Names are added when a shift starts.
     - Names are removed when a shift ends.
   - These operations need to be efficient to handle large datasets.

### 3. **Output Consistency**
   - When converting the active set to a string using `String.join`, the order of names is crucial for readability and predictability.
   - Using a **TreeSet**, we ensure the names are always printed in sorted order.

   **Example:**
   Instead of `{Ben, Abby}`, the output will always show `Abby, Ben`.

### Alternative Structures and Why Not Used:
1. **HashSet:**
   - Does not maintain order, so the names would appear in arbitrary order in the output.
2. **ArrayList:**
   - Requires manual sorting for each operation, increasing complexity.

### Conclusion:
Using a **TreeSet** ensures:
- **Sorted output**
- **Efficient add/remove operations**
- **Consistent and predictable behavior** in the `active` set.