package uipath;

public class TicTacToe {
    private static final int SIZE = 3;
    private final char[][] board;
    private char currentPlayer;
    private GameState gameState;
    private int moveCount;

    public enum GameState {
        IN_PROGRESS, WINNER, DRAW
    }

    public TicTacToe() {
        board = new char[SIZE][SIZE];
        initializeBoard();
        currentPlayer = 'X';
        gameState = GameState.IN_PROGRESS;
        moveCount = 0;
    }

    private void initializeBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = '-';
            }
        }
    }

    public boolean makeMove(int row, int col) {
        // Validate move
        if (!isValidMove(row, col)) {
            System.out.println("Invalid move! Try again.");
            return false;
        }

        // Make move
        board[row][col] = currentPlayer;
        moveCount++;

        // Display board
        displayBoard();

        // Check game ending conditions
        if (checkWin()) {
            gameState = GameState.WINNER;
            System.out.println("Player " + currentPlayer + " wins!");
            return true;
        }

        if (moveCount == SIZE * SIZE) {
            gameState = GameState.DRAW;
            System.out.println("Game is a draw!");
            return true;
        }

        // Switch player
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        System.out.println("Player " + currentPlayer + "'s turn");
        return true;
    }

    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < SIZE && 
               col >= 0 && col < SIZE && 
               board[row][col] == '-' && 
               gameState == GameState.IN_PROGRESS;
    }

    private boolean checkWin() {
        // Check rows
        for (int i = 0; i < SIZE; i++) {
            if (board[i][0] != '-' && 
                board[i][0] == board[i][1] && 
                board[i][1] == board[i][2]) {
                return true;
            }
        }

        // Check columns
        for (int j = 0; j < SIZE; j++) {
            if (board[0][j] != '-' && 
                board[0][j] == board[1][j] && 
                board[1][j] == board[2][j]) {
                return true;
            }
        }

        // Check diagonals
        if (board[0][0] != '-' && 
            board[0][0] == board[1][1] && 
            board[1][1] == board[2][2]) {
            return true;
        }

        return board[0][2] != '-' && 
               board[0][2] == board[1][1] && 
               board[1][1] == board[2][0];
    }

    public void displayBoard() {
        System.out.println("\nCurrent Board:");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public GameState getGameState() {
        return gameState;
    }

    public static void main(String[] args) {
        // Demo game
        TicTacToe game = new TicTacToe();
        System.out.println("Game started! Player X goes first.");
        game.displayBoard();

        // Test moves for X win scenario
        game.makeMove(0, 0); // X
        game.makeMove(1, 0); // O
        game.makeMove(0, 1); // X
        game.makeMove(1, 1); // O
        game.makeMove(0, 2); // X wins

        System.out.println("Game ended with state: " + game.getGameState());
    }
}
