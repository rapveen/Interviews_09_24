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