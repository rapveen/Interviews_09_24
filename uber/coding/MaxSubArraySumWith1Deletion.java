public class MaxSubArraySumWith1Deletion {
    public int maximumSum(int[] arr) {
        int n = arr.length;
        if (n == 1) return arr[0]; // Edge case: single element

        // Initialize arrays to store max subarray sums ending/starting at each position
        int[] maxEndHere = new int[n];
        int[] maxStartHere = new int[n];

        // Calculate max subarray sums ending at each position (Kadane’s from left)
        maxEndHere[0] = arr[0];
        int maxSum = arr[0];
        for (int i = 1; i < n; i++) {
            maxEndHere[i] = Math.max(arr[i], maxEndHere[i - 1] + arr[i]);
            maxSum = Math.max(maxSum, maxEndHere[i]);
        }

        // Calculate max subarray sums starting at each position (Kadane’s from right)
        maxStartHere[n - 1] = arr[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            maxStartHere[i] = Math.max(arr[i], maxStartHere[i + 1] + arr[i]);
        }

        // Check for max sum with one deletion at each position
        for (int i = 1; i < n - 1; i++) {
            maxSum = Math.max(maxSum, maxEndHere[i - 1] + maxStartHere[i + 1]);
        }

        return maxSum;
    }

    public static void main(String[] args) {
        MaxSubArraySumWith1Deletion solution = new MaxSubArraySumWith1Deletion();

        // Test cases
        System.out.println(solution.maximumSum(new int[]{1, -2, 0, 3})); // Expected output: 4
        System.out.println(solution.maximumSum(new int[]{1, -2, -2, 3})); // Expected output: 3
        System.out.println(solution.maximumSum(new int[]{-1, -1, -1, -1})); // Expected output: -1
    }
}
