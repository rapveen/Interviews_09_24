### Part 1 Q
Mahjong is a four-player game popular in China and Japan involving tiles (or say, cards), most of which represent a number from 1-9.
We only consider these numbered tiles for this problem's purpose.
Notions:
set - three tiles with same numbers. E.g., 111, 222
street - three tiles with consecutive numbers. E.g. 123, 234

pair - two tiles with same numbers. E.g., 11, 22

In a simplified version of the game, a player wins if there exists a partition of their 14-tile hand that forms 4 streets/sets and 1 pair.
Here are two examples of a winning hand, each one with 3 streets + 1 set and 1 pair:
1 1 2 2 3 3 4 4 5 5 5 6 78 -- [123, 123, 44, 555, 678]
1 2 2 3 3 3 4 4 5 5 5 5 9 9 -- [123, 234, 345, 555, 99]
takes in a list 0f 14 numbers and decides whether or not it forms a winning hand
Write a function that takes in a list of 14 numbers and decides whether or not it forms a winning hand.


provided a backtrack solution

import java.util.Arrays;

public class MahjongSolver {
    public static boolean isWinningHand(int[] tiles) {
        if (tiles.length != 14) return false;
        
        // Count frequency of each tile (1-9)
        int[] count = new int[10];
        for (int tile : tiles) {
            if (tile < 1 || tile > 9) return false; // Invalid tile
            count[tile]++;
        }
        
        // Iterate through possible pairs
        for (int i = 1; i <= 9; i++) {
            if (count[i] >= 2) {
                count[i] -= 2; // Remove pair
                if (canFormSets(count, 4)) {
                    return true;
                }
                count[i] += 2; // Backtrack
            }
        }
        return false;
    }
    
    // Recursive function to form required number of sets/streets
    private static boolean canFormSets(int[] count, int setsNeeded) {
        if (setsNeeded == 0) return true;
        
        // Find the first tile with count > 0
        int first = 1;
        while (first <= 9 && count[first] == 0) first++;
        if (first > 9) return false;
        
        // Try to form a set
        if (count[first] >= 3) {
            count[first] -= 3;
            if (canFormSets(count, setsNeeded - 1)) return true;
            count[first] += 3; // Backtrack
        }
        
        // Try to form a street
        if (first <= 7 && count[first + 1] > 0 && count[first + 2] > 0) {
            count[first]--;
            count[first + 1]--;
            count[first + 2]--;
            if (canFormSets(count, setsNeeded - 1)) return true;
            count[first]++;
            count[first + 1]++;
            count[first + 2]++;
        }
        
        return false;
    }
    
    // Example usage
    public static void main(String[] args) {
        int[][] hands = {
            {1,1,2,2,3,3,4,4,5,5,5,6,7,8},
            {1,2,2,3,3,3,4,4,5,5,5,5,9,9},
            {1,1,1,2,2,2,3,3,3,4,4,4,5,5}
        };
        
        for (int[] hand : hands) {
            System.out.println(Arrays.toString(hand) + " -> " + isWinningHand(hand));
        }
    }
}

### Followup
Now how do you print the winning hand no just return true/false values



he asked me what is the TC of the code to which I couldn't mention properly

but here is the analysis,
Let me break down the time complexity analysis of this Mahjong solver:

1) Space Complexity First:
- The count array is O(1) as it's always size 10 (fixed)
- The recursion stack depth is at most O(4) since we need to form exactly 4 sets/streets
- Therefore, Space Complexity is O(1)

2) Time Complexity Analysis:

Let's break it down step by step:

```java
// Initial counting of tiles: O(14) = O(1) as input size is fixed
for (int tile : tiles) {
    count[tile]++;
}

// Trying each possible pair:
for (int i = 1; i <= 9; i++) {  // O(9) iterations
    if (count[i] >= 2) {
        // For each pair, we try to form 4 sets/streets
        canFormSets(count, 4)
    }
}
```

The critical part is understanding the complexity of `canFormSets()`:

For each recursive call to `canFormSets()`:
1. We can try to form either:
   - A set (triplet) with current tile
   - A street (sequence) starting with current tile
2. This creates a branching factor of 2 at each decision point
3. We need to make 4 such decisions (as we need 4 sets/streets)
4. At each level, we might need to scan up to 9 tiles to find the next non-zero count

Therefore:
- Branching factor = 2 (set or street)
- Depth = 4 (need 4 sets/streets)
- At each node: O(9) work to find next tile

Total Time Complexity = O(9 * 2⁴) = O(144) = O(1)

The final analysis:
- Time Complexity: O(1)
- Space Complexity: O(1)

Why O(1)?
1. The input size is fixed (always 14 tiles)
2. The number of tiles types is fixed (1-9)
3. The number of required sets/streets is fixed (4)
4. The branching factor is constant (2)

Important Notes:
1. While the theoretical complexity is O(1) because all inputs are bounded constants, the actual running time can still be significant due to the exponential nature of the recursive search (2⁴ possible combinations).

2. The algorithm uses backtracking which helps prune invalid combinations early, making it more efficient in practice than the worst-case analysis might suggest.

3. The memoization/dynamic programming approach seen in some other solutions could potentially improve the practical performance by avoiding redundant computations, though it wouldn't change the theoretical complexity given the fixed input size.

Would you like me to elaborate on any part of this analysis?