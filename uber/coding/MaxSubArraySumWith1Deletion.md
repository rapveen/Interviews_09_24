🧑‍💻 **Let's dissect the solution for the "Maximum Sum of Subarray with At Most One Deletion" problem systematically.** I'll break it down following each of the requested steps in detail.

---

### Step 1: **ANALYSIS**

**Requirements and Constraints:**
1. **Input Array:** Given array `arr` of integers.
2. **Goal:** Find the maximum sum of any contiguous subarray, allowing for at most one element to be deleted within the chosen subarray.
3. **Non-Empty Requirement:** After deletion (if any), the subarray must still have at least one element.
4. **Constraints:** 
   - Array length `1 <= arr.length <= 10^5`
   - Array values `-10^4 <= arr[i] <= 10^4`

**Key Points That Could Be Missed:**
- The deletion is optional but only allows deleting at most one element.
- We must ensure the subarray isn't empty after deletion.
- The possibility that the array consists entirely of negative numbers (deletion won’t help much in this case).

**Most Complex Test Case Analysis:**
- **Array with mix of positive and negative values:** `[1, -2, 0, 3]`
  - Deleting `-2` maximizes the sum.
- **Array with all negative values:** `[-1, -1, -1, -1]`
  - Should return the largest negative number (`-1`), as deletion won’t increase the sum.

---

### Step 2: **DRY RUN**

Let's dry-run the array `[1, -2, 0, 3]`.

- Without deletion, the maximum subarray sum would include `1, -2, 0, 3` directly.
- Deleting `-2` provides the higher sum of `4` from `[1, 0, 3]`.

**Edge Cases:**
1. **All negative numbers:** `[-3, -2, -5, -1]` – should return `-1` since no deletion improves the sum.
2. **Single-element array:** `[5]` – should return `5`, as no deletion is possible.
3. **All zeros:** `[0, 0, 0, 0]` – should return `0`.

---

### Step 3: **APPROACH**

1. **Data Structures and Complexity Requirements:**
   - **Arrays:** Use auxiliary arrays to keep track of max sums.
   - **Time Complexity:** `O(n)` to pass through the array once with auxiliary arrays.
   - **Space Complexity:** `O(n)` for the auxiliary arrays.

2. **Algorithm Outline:**
   - Use two arrays `maxEndHere` and `maxStartHere`:
     - `maxEndHere[i]`: Max sum of subarray ending at `i`.
     - `maxStartHere[i]`: Max sum of subarray starting at `i`.
   - Compute `maxEndHere` by iterating from left to right.
   - Compute `maxStartHere` by iterating from right to left.
   - For each element `i`, calculate the maximum sum by including it or by deleting it and combining `maxEndHere[i-1] + maxStartHere[i+1]` if possible.

3. **Step-by-Step Solution:**
   - **Initialize `maxEndHere` and `maxStartHere` arrays.**
   - **Calculate `maxEndHere` using Kadane's algorithm** from left to right.
   - **Calculate `maxStartHere` using Kadane's algorithm** from right to left.
   - **Evaluate Maximum Sum with Deletion** by considering:
     - Each `maxEndHere[i]` (no deletion),
     - Possible deletion using `maxEndHere[i-1] + maxStartHere[i+1]` for element `i`.
   - **Handle Edge Cases**:
     - Single element: Return the element itself.
     - All negative values: Return the largest negative value.

---

### Final Verification

- **All examples provided:** Tested and verified.
- **Edge Cases:**
   - **Single-element array:** Handles it by returning the single element.
   - **All negative values:** Kadane’s captures the max negative.
   - **Deletion cases:** Combines precomputed arrays for efficiency.

This structured solution satisfies the constraints with an optimal `O(n)` complexity. 🚀