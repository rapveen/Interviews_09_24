/* 
BruteForce approach
Backtracking is a common brute-force approach to solving constraint-based problems like Sudoku. The idea is to try placing a number in each empty cell and check if it leads to a valid solution.
Here’s the step-by-step logic for the brute-force solution:
Traverse the board, find the first empty cell.
Try placing digits 1 through 9 in the empty cell.
For each digit, check if placing it violates any Sudoku rules (i.e., if it exists in the same row, column, or 3x3 sub-grid).
If placing the number doesn't cause any conflicts, recursively try to solve the rest of the board.
If placing a number leads to an invalid configuration later, undo (backtrack) and try the next possible number.
If all numbers 1 to 9 fail for an empty cell, backtrack further to previous cells and try other possibilities.
TC: O(9^n*n) 

*/
class SudokuFiller {
    public void solveSudoku(char[][] board) {
        backtrack(board);
    }
    private boolean backtrack(char[][] board) {
        // Traverse through each cell in the board
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                // Find an empty cell
                if (board[row][col] == '.') {
                    // Try each number from '1' to '9'
                    for (char num = '1'; num <= '9'; num++) {
                        // Check if placing 'num' is valid in this position
                        if (isValid(board, row, col, num)) {
                            board[row][col] = num;  // Place the number
                            // Recurse with the new board configuration
                            if (backtrack(board)) {
                                return true;  // If the board is valid, propagate success
                            }
                            board[row][col] = '.';  // Backtrack if needed
                        }
                    }
                    return false;  // No valid number found, trigger backtracking
                }
            }
        }
        return true;  // Board is complete and valid
    }
    // Helper method to check if placing 'num' at board[row][col] is valid
    private boolean isValid(char[][] board, int row, int col, char num) {
        // Check if 'num' is already in the current row, column, or 3x3 sub-box
        for (int i = 0; i < 9; i++) {
            // Check the row
            if (board[row][i] == num) {
                return false;
            }
            // Check the column
            if (board[i][col] == num) {
                return false;
            }
            // Check the 3x3 sub-box
            int boxRow = 3 * (row / 3) + i / 3;
            int boxCol = 3 * (col / 3) + i % 3;
            if (board[boxRow][boxCol] == num) {
                return false;
            }
        }
        return true;  // If no conflicts, it's a valid placement
    }
}

/* 
Optimize
1. High-Level Strategy
To efficiently solve Sudoku, we'll employ Backtracking enhanced with the Minimum Remaining Value (MRV) heuristic. This involves:
Backtracking: Recursively trying possible numbers in empty cells and backtracking upon encountering contradictions.
Minimum Remaining Value (MRV) Heuristic: Always choosing the empty cell with the fewest possible valid numbers first. This reduces the branching factor and leads to quicker solutions.

2. Simplified Implementation
We'll implement the Sudoku solver within the provided Solution class skeleton. The key components include:
Tracking Possible Numbers: For each cell, keep track of possible numbers based on Sudoku rules.
Choosing the Next Cell: Select the empty cell with the fewest possibilities (MRV heuristic).
Backtracking: Assign a possible number and recursively attempt to solve the rest of the board. If a contradiction is found, undo the assignment and try the next possibility.
3. Explaination:
Minimum Remaining Value (MRV) Heuristic:
Why: By selecting the cell with the fewest possible numbers first, we reduce the branching factor early, leading to faster solutions.
How: The findUnassignedCell method scans for the empty cell with the minimum number of valid possibilities.
Early Pruning:
Why: Detecting invalid placements early prevents unnecessary recursion.
How: The isValid method ensures that only valid numbers are tried in each cell.
Efficient State Management:
Why: Avoiding deep copies of the board reduces both time and space complexity.
How: The solver modifies the board in place and backtracks by resetting cells to '.' when a path fails.
Avoiding Redundant Checks:
Why: Minimizing repeated computations enhances performance.
How: By counting possible numbers only once per cell and reusing that information during selection.
 
*/
class Solution {
    public void solveSudoku(char[][] board) {
        backtrack(board);
    }
   
    private boolean backtrack(char[][] board) {
        int n = 9;
        // Find the next empty cell with the fewest possibilities
        int[] cell = findUnassignedCell(board);
        if (cell == null) {
            // No empty cells left; puzzle solved
            return true;
        }
        int row = cell[0];
        int col = cell[1];
       
        for (char num = '1'; num <= '9'; num++) {
            if (isValid(board, row, col, num)) {
                board[row][col] = num; // Assign num
               
                if (backtrack(board)) {
                    return true; // If successful, propagate true
                }
               
                board[row][col] = '.'; // Undo assignment (backtrack)
            }
        }
        return false; // Trigger backtracking
    }
   
    // Finds the empty cell with the fewest possible numbers
    private int[] findUnassignedCell(char[][] board) {
        int minCount = 10;
        int[] result = null;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == '.') {
                    int count = countPossibleNumbers(board, i, j);
                    if (count < minCount) {
                        minCount = count;
                        result = new int[]{i, j};
                        if (minCount == 1) {
                            return result; // Can't get better than one possibility
                        }
                    }
                }
            }
        }
        return result;
    }
   
    // Counts how many numbers are possible in a given cell
    private int countPossibleNumbers(char[][] board, int row, int col) {
        boolean[] used = new boolean[10]; // Index 0 unused
        // Check row and column
        for (int i = 0; i < 9; i++) {
            if (board[row][i] != '.') {
                used[board[row][i] - '0'] = true;
            }
            if (board[i][col] != '.') {
                used[board[i][col] - '0'] = true;
            }
        }
        // Check 3x3 sub-grid
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (board[i][j] != '.') {
                    used[board[i][j] - '0'] = true;
                }
            }
        }
        // Count unused numbers
        int count = 0;
        for (int num = 1; num <= 9; num++) {
            if (!used[num]) count++;
        }
        return count;
    }
   
    // Validates if placing num at (row, col) violates Sudoku rules
    private boolean isValid(char[][] board, int row, int col, char num) {
        // Check row and column
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num) return false;
            if (board[i][col] == num) return false;
        }
        // Check 3x3 sub-grid
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (board[i][j] == num) return false;
            }
        }
        return true;
    }
}

//Time Complexity: O(9^n)
//Space Complexity: O(n)
//n: Number of empty cell
