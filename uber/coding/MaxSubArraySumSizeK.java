

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;

class MaxSubArraySumSizeK {
    public long maximumSubarraySum(int[] nums, int k) {
        if (k > nums.length) {
            return 0;
        }
        
        HashSet<Integer> duplicate = new HashSet<>();    // Track unique elements
        long output = 0;                                 // Store maximum sum
        long calculation = 0;                           // Current window sum
        Deque<Integer> dq = new LinkedList<>();         // Store window elements
        
        for (int i = 0; i < nums.length; i++) {
            // Clever part: Remove elements until current element is unique
            while (duplicate.contains(nums[i])) {
                int out = dq.pollFirst();               // Remove from front
                calculation -= out;                     // Update sum
                duplicate.remove(out);                  // Update unique set
            }
            
            // Add new element to window
            dq.addLast(nums[i]);
            duplicate.add(nums[i]);
            calculation += nums[i];
            
            // Check if window size equals k
            if (dq.size() == k) {
                output = Math.max(output, calculation);
                // Remove first element for next window
                int out = dq.pollFirst();
                calculation -= out;
                duplicate.remove(out);
            }
        }
        return output;
    }
    
    // Added test method to demonstrate advantages
    public static void testWithVariousCases() {
        MaxSubArraySumSizeK solution = new MaxSubArraySumSizeK();
        
        // Test Case 1: Original failing case
        int[] nums1 = {9,9,9,1,2,3};
        int k1 = 3;
        System.out.println("Test 1: " + solution.maximumSubarraySum(nums1, k1)); // Expected: 12
        
        // Test Case 2: Many duplicates
        int[] nums2 = {1,1,1,2,2,2,3,3,3,4,4,4};
        int k2 = 3;
        System.out.println("Test 2: " + solution.maximumSubarraySum(nums2, k2)); // Expected: 9 (3+2+4)
        
        // Test Case 3: No duplicates
        int[] nums3 = {1,2,3,4,5};
        int k3 = 3;
        System.out.println("Test 3: " + solution.maximumSubarraySum(nums3, k3)); // Expected: 12 (3+4+5)
        
        // Test Case 4: All duplicates
        int[] nums4 = {1,1,1,1,1};
        int k4 = 3;
        System.out.println("Test 4: " + solution.maximumSubarraySum(nums4, k4)); // Expected: 0
        
        // Test Case 5: Alternating duplicates
        int[] nums5 = {1,2,1,2,1,2};
        int k5 = 3;
        System.out.println("Test 5: " + solution.maximumSubarraySum(nums5, k5)); // Expected: 5 (1+2+2)
    }
}
