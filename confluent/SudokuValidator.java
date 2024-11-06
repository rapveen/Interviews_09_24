/* 

5. Sudoku validator
Each row must contain digits 1-9 without repetition.
Each column must contain digits 1-9 without repetition.
Each of the 3x3 sub-boxes must also contain digits 1-9 without repetition.
Approach:
We can maintain three sets for each of these checks:
One set for each row to track digits in that row.
One set for each column to track digits in that column.
One set for each 3x3 sub-box to track digits in that sub-box.
To determine which 3x3 sub-box a particular cell belongs to, we can use integer division:
For a cell at position (i, j), the sub-box index is given by (i // 3, j // 3).
Steps:
Initialize three lists of sets:
A list of sets for rows.
A list of sets for columns.
A list of sets for 3x3 sub-boxes.
Iterate through the board:
For each filled cell (i.e., not '.'), check:
If the digit is already in the corresponding row set, column set, or sub-box set.
If it is, the board is invalid.
Otherwise, add the digit to the respective sets.
Return true if no violations are found.

*/
import java.util.HashSet;
public class SudokuValidator {
    public boolean isValidSudoku(char[][] board) {
        // Create a list of sets to track rows, columns, and boxes
        HashSet<Character>[] rows = new HashSet[9];
        HashSet<Character>[] cols = new HashSet[9];
        HashSet<Character>[] boxes = new HashSet[9];
       
        // Initialize each set for rows, columns, and boxes
        for (int i = 0; i < 9; i++) {
            rows[i] = new HashSet<>();
            cols[i] = new HashSet<>();
            boxes[i] = new HashSet<>();
        }
       
        // Iterate over the board to fill sets
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                char num = board[i][j];
                // If the cell is not empty, check its validity
                if (num != '.') {
                    // Check if the number is already in the row
                    if (rows[i].contains(num)) {
                        return false;
                    }
                    rows[i].add(num);
                   
                    // Check if the number is already in the column
                    if (cols[j].contains(num)) {
                        return false;
                    }
                    cols[j].add(num);
                   
                    // Check if the number is already in the corresponding 3x3 box
                    int boxIndex = (i / 3) * 3 + (j / 3);
                    if (boxes[boxIndex].contains(num)) {
                        return false;
                    }
                    boxes[boxIndex].add(num);
                }
            }
        }
        // If no violations were found, return true
        return true;
    }
}

/* 
TC: O(81)
SC: O(1)

Space complexity comparison: Original solution:
HashSets: O(3 * 9 * 9) = O(243) characters + HashSet overhead
Total space: Approximately 1-2 KB

Optimized Sudoku validator
Each HashSet object has internal array and linked list structures
Extra memory for load factor and resizing capabilities
Optimizations:
 Using boolean arrays instead of HashSets:
Boolean arrays use less memory per element
No overhead of HashSet implementation
Direct indexing instead of hashing
b) Using bit manipulation:
Each row/column/box can be represented by 9 bits
Entire board state can fit in few integers
Extremely memory efficient
Faster operations using bitwise operations
 Optimized with bit manip/boolean arrays

*/
public class Solution {
    public boolean isValidSudoku(char[][] board) {
        // Use integers to store the state of each row, column and box
        int[] rows = new int[9];
        int[] cols = new int[9];
        int[] boxes = new int[9];
       
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                char num = board[i][j];
                if (num != '.') {
                    // Convert char to 0-based index
                    int val = num - '1';
                    // Create bitmask for current number
                    int mask = 1 << val;
                    // Calculate box index
                    int boxIndex = (i / 3) * 3 + (j / 3);
                   
                    // Check if number already exists
                    if ((rows[i] & mask) != 0 ||
                        (cols[j] & mask) != 0 ||
                        (boxes[boxIndex] & mask) != 0) {
                        return false;
                    }
                   
                    // Update the states using bitwise OR
                    rows[i] |= mask;
                    cols[j] |= mask;
                    boxes[boxIndex] |= mask;
                }
            }
        }
        return true;
    }
}

// code to print the invalid rows, cols, boxes as well,
 
public class Solution {
    public boolean isValidSudoku(char[][] board) {
        // Use integers to store the state of each row, column and box
        int[] rows = new int[9];
        int[] cols = new int[9];
        int[] boxes = new int[9];
       
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                char num = board[i][j];
                if (num != '.') {
                    // Convert char to 0-based index
                    int val = num - '1';
                    // Create bitmask for current number
                    int mask = 1 << val;
                    // Calculate box index
                    int boxIndex = (i / 3) * 3 + (j / 3);
                   
                    // Check if number already exists
                    if ((rows[i] & mask) != 0) {
                        System.out.println("Invalid at row: " + i + " for number: " + num);
                        return false;
                    }
                    if ((cols[j] & mask) != 0) {
                        System.out.println("Invalid at column: " + j + " for number: " + num);
                        return false;
                    }
                    if ((boxes[boxIndex] & mask) != 0) {
                        System.out.println("Invalid at 3x3 box starting at (" + (i / 3) * 3 + ", " + (j / 3) * 3 + ") for number: " + num);
                        return false;
                    }
                   
                    // Update the states using bitwise OR
                    rows[i] |= mask;
                    cols[j] |= mask;
                    boxes[boxIndex] |= mask;
                }
            }
        }
        return true;
    }
}
