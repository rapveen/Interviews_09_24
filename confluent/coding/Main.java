public class Main {
    public void solveSudoku(int[][] board) {
        backtrack(board);
    }

    private boolean backtrack(int[][] board) {
        int n = 9;
        int[] cell = findUnassignedCell(board);
        if (cell == null) {
            return true;
        }
        int row = cell[0];
        int col = cell[1];

        for (int num = 1; num <= 9; num++) {
            if (isValid(board, row, col, num)) {
                board[row][col] = num;

                if (backtrack(board)) {
                    return true;
                }

                board[row][col] = 0; // Reset to empty
            }
        }
        return false;
    }

    private int[] findUnassignedCell(int[][] board) {
        int minCount = 10;
        int[] result = null;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    int count = countPossibleNumbers(board, i, j);
                    if (count < minCount) {
                        minCount = count;
                        result = new int[]{i, j};
                        if (minCount == 1) {
                            return result;
                        }
                    }
                }
            }
        }
        return result;
    }

    private int countPossibleNumbers(int[][] board, int row, int col) {
        boolean[] used = new boolean[10];
        // Check row and column
        for (int i = 0; i < 9; i++) {
            if (board[row][i] != 0) {
                used[board[row][i]] = true;
            }
            if (board[i][col] != 0) {
                used[board[i][col]] = true;
            }
        }
        // Check 3x3 sub-grid
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (board[i][j] != 0) {
                    used[board[i][j]] = true;
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

    private boolean isValid(int[][] board, int row, int col, int num) {
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
    private static void printBoard(int[][] board) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
    public static void main(String[] args) {
        // Example Sudoku puzzle (0 represents empty cells)
        int[][] board = {
            {5,3,0,0,7,0,0,0,0},
            {6,0,0,1,9,5,0,0,0},
            {0,9,8,0,0,0,0,6,0},
            {8,0,0,0,6,0,0,0,3},
            {4,0,0,8,0,3,0,0,1},
            {7,0,0,0,2,0,0,0,6},
            {0,6,0,0,0,0,2,8,0},
            {0,0,0,4,1,9,0,0,5},
            {0,0,0,0,8,0,0,7,9}
        };

        System.out.println("Original Sudoku puzzle:");
        // printBoard(board);

        Main solution = new Main();
        solution.solveSudoku(board);

        System.out.println("\nSolved Sudoku puzzle:");
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
}