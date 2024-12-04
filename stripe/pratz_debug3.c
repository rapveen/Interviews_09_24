#include <stdio.h>
#include <limits.h>

int minSubArrayLen(int target, int* nums, int numsSize) {
    int start = 0, sum = 0;
    int min_length = INT_MAX;  // Set to max possible value initially

    for (int end = 0; end < numsSize; end++) {
        sum += nums[end];  // Add current element to the sum

        // While sum is greater than or equal to target, try to shrink the window
        while (sum >= target) {
            int current_length = end - start + 1;  // Current window length

            // Update min_length if the current window is smaller
            if (current_length < min_length) {
                min_length = current_length;
            }

            sum -= nums[start];  // Remove the start element from sum
            start++;  // Move start pointer to the right to shrink the window
        }
    }

    // If min_length was never updated, return 0 (no such subarray found)
    return (min_length == INT_MAX) ? 0 : min_length;
}

int main() {
    int nums[] = {2, 3, 1, 2, 4, 3};
    int target = 7;
    int numsSize = sizeof(nums) / sizeof(nums[0]);

    int result = minSubArrayLen(target, nums, numsSize);
    printf("Minimal length of subarray with sum >= %d is: %d\n", target, result);

    return 0;
}
