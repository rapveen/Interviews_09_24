public class Solution {
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

    private static void printBoard(char[][] board) {
        for (int i = 0; i < 9; i++) {
            if (i % 3 == 0 && i != 0) {
                System.out.println("-------------------");
            }
            for (int j = 0; j < 9; j++) {
                if (j % 3 == 0 && j != 0) {
                    System.out.print("|");
                }
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        // Example Sudoku puzzle (0 represents empty cells)
        char[][] board = {
            {'5','3','.','.','7','.','.','.','.'},
            {'6','.','.','1','9','5','.','.','.'},
            {'.','9','8','.','.','.','.','6','.'},
            {'8','.','.','.','6','.','.','.','3'},
            {'4','.','.','8','.','3','.','.','1'},
            {'7','.','.','.','2','.','.','.','6'},
            {'.','6','.','.','.','.','2','8','.'},
            {'.','.','.','4','1','9','.','.','5'},
            {'.','.','.','.','8','.','.','7','9'}
        };
        
        System.out.println("Original Sudoku puzzle:");
        printBoard(board);
        
        Solution solution = new Solution();
        solution.solveSudoku(board);
        
        System.out.println("\nSolved Sudoku puzzle:");
        printBoard(board);
    }
}


