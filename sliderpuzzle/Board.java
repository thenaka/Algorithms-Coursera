import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Board {
    private int size;
    private int[][] board;

    private int zeroRow;
    private int zeroCol;

    private int hammingValue;
    private int manhattanValue;

    private Board twin;

    /**
     * Create a board from an n-by-n array of tiles,
     * where tiles[row][col] = tile at (row, col)
     * 
     * @param tiles the n x n array of integers to create
     * @throws IllegalArgumentException if tiles is null.
     */
    public Board(int[][] tiles) {
        if (tiles == null) {
            throw new IllegalArgumentException("tiles must not be null.");
        }

        size = tiles.length;
        createBoard(tiles);
    }

    private void createBoard(int[][] tiles) {
        int goalTileValue = 1;
        board = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int currentValue = tiles[row][col];

                // set board value ---------------------------
                board[row][col] = currentValue;

                // handle zero value -------------------------
                if (currentValue == 0) {
                    zeroRow = row;
                    zeroCol = col;

                    goalTileValue++;

                    continue; // don't count zero out of place
                }

                // set hamming value -------------------------
                if (currentValue != goalTileValue++) {
                    hammingValue++;
                }

                // set manhattan value -----------------------
                int rowDiff = Math.abs(row - ((currentValue - 1) / size));
                int colDiff = Math.abs(col - ((currentValue - 1) % size));
                manhattanValue += rowDiff + colDiff;
            }
        }
    }

    /**
     * String representation of this board
     * 
     * @return string representation of this board
     */
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(size);
        for (int row = 0; row < size; row++) {
            output.append("\n");
            for (int col = 0; col < size; col++) {
                output.append(board[row][col]);
                output.append(" ");
            }
        }

        return output.toString();
    }

    /**
     * The n dimension of the n x n board.
     * 
     * @return The n dimension of the n x n board.
     */
    public int dimension() {
        return size;
    }

    /**
     * Number of tiles out of place
     * 
     * @return number of tiles out of place.
     */
    public int hamming() {
        return hammingValue;
    }

    /**
     * 
     * Sum of Manhattan distances between tiles and goal.
     * 
     * @return sum of Manhattan distances between tiles and goal.
     */
    public int manhattan() {
        return manhattanValue;
    }

    /**
     * 
     * Is this board the goal board?
     * 
     * @return true if this is the goal board, otherwise false.
     */
    public boolean isGoal() {
        return hamming() == 0;
    }

    /**
     * 
     * Does this board equal that?
     * 
     * @param that the other Board to check for equality.
     * @return false if that is null or a different size or if any tile value is
     *         different, otherwise true.
     */
    public boolean equals(Object that) {
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        if (this == that) {
            return true;
        }

        Board other = (Board) that;
        if (size != other.size) {
            return false;
        }

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] != other.board[row][col]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 
     * All neighboring boards.
     * 
     * @return an iterable collection of neighboring boards.
     */
    public Iterable<Board> neighbors() {
        Queue<Board> n = new Queue<Board>();

        if (zeroRow > 0) {
            int[][] upBoard = copyBoard();
            swap(upBoard, zeroRow, zeroCol, zeroRow - 1, zeroCol);
            Board upNeighbor = new Board(upBoard);
            n.enqueue(upNeighbor);
        }
        if (zeroRow < size - 1) {
            int[][] downBoard = copyBoard();
            swap(downBoard, zeroRow, zeroCol, zeroRow + 1, zeroCol);
            Board downNeighbor = new Board(downBoard);
            n.enqueue(downNeighbor);
        }
        if (zeroCol > 0) {
            int[][] leftBoard = copyBoard();
            swap(leftBoard, zeroRow, zeroCol, zeroRow, zeroCol - 1);
            Board leftNeighbor = new Board(leftBoard);
            n.enqueue(leftNeighbor);
        }
        if (zeroCol < size - 1) {
            int[][] rightBoard = copyBoard();
            swap(rightBoard, zeroRow, zeroCol, zeroRow, zeroCol + 1);
            Board rightNeighbor = new Board(rightBoard);
            n.enqueue(rightNeighbor);
        }

        return n;
    }

    private int[][] copyBoard() {
        int[][] copy = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                copy[row][col] = board[row][col];
            }
        }
        return copy;
    }

    private void swap(int[][] boardToSwap, int row1, int col1, int row2, int col2) {

        int tempVal = boardToSwap[row1][col1];
        boardToSwap[row1][col1] = boardToSwap[row2][col2];
        boardToSwap[row2][col2] = tempVal;
    }

    /**
     * 
     * A board that is obtained by exchanging any pair of tiles.
     * 
     * @return a board that has a pair of tiles exchanged.
     */
    public Board twin() {
        if (twin != null) {
            return twin;
        }

        int twinSourceRow;
        int twinSourceCol;

        // Find the twin source row and col
        do {
            twinSourceRow = StdRandom.uniformInt(size);
            twinSourceCol = StdRandom.uniformInt(size);
        } while (twinSourceRow == zeroRow && twinSourceCol == zeroCol);

        int[][] twinBoard = copyBoard();

        int twinDestCol;
        if (twinSourceCol == 0) {
            // we must swap with the right value
            if (twinBoard[twinSourceRow][twinSourceCol + 1] == 0) {
                return twin(); // cannot swap with the zero tile
            }
            twinDestCol = twinSourceCol + 1;
        } else if (twinSourceCol == size - 1) {
            // we must swap with the left value
            if (twinBoard[twinSourceRow][twinSourceCol - 1] == 0) {
                return twin(); // cannot swap with the zero tile
            }
            twinDestCol = twinSourceCol - 1;
        } else {
            // can swap either way so flip a coin
            int coinFlip = StdRandom.uniformInt(2);
            if (coinFlip == 0) {
                // if possible swap left
                if (twinBoard[twinSourceRow][twinSourceCol - 1] == 0) {
                    // cannot swap left
                    twinDestCol = twinSourceCol + 1;
                } else {
                    twinDestCol = twinSourceCol - 1;
                }
            } else {
                // if possible swap right
                if (twinBoard[twinSourceRow][twinSourceCol + 1] == 0) {
                    // cannot swap right
                    twinDestCol = twinSourceCol - 1;
                } else {
                    twinDestCol = twinSourceCol + 1;
                }
            }
        }

        swap(twinBoard, twinSourceRow, twinSourceCol, twinSourceRow, twinDestCol);
        twin = new Board(twinBoard);
        return twin;
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        int size = 3;
        int tileValue = 1;
        int[][] testBoard = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                testBoard[row][col] = tileValue == size * size ? 0 : tileValue++;
            }
        }

        Board solvedBoard = new Board(testBoard);
        assert solvedBoard.dimension() == size;
        assert solvedBoard.hamming() == 0;
        assert solvedBoard.manhattan() == 0;
        assert solvedBoard.toString().equals("" + size + "\n1 2 3 \n4 5 6 \n7 8 0 ");
        assert solvedBoard.isGoal();
        assert !solvedBoard.equals(null);
        assert solvedBoard.equals(solvedBoard);
        assert !solvedBoard.equals(solvedBoard.twin());

        // 1 2 3
        // 4 5 6
        // 7 8 0
        Iterable<Board> neighbors = solvedBoard.neighbors();
        int count = 0;
        for (Board n : neighbors) {
            assert n.hamming() == 1 : "A neighbor of a solved puzzle should have one tile out of place.";
            assert n.manhattan() == 1 : "A neighbor of a solved puzzle should have one tile out of place.";
            count++;
        }
        assert count == 2 : "There should be two neighbors to a solved puzzle.";

        testBoard = new int[size][size];
        // 8 1 3
        // 4 0 2
        // 7 6 5
        // Manhattan
        // 3 1 0
        // 0 0 2
        // 0 2 2
        testBoard[0][0] = 8;
        testBoard[0][1] = 1;
        testBoard[0][2] = 3;
        testBoard[1][0] = 4;
        testBoard[1][1] = 0;
        testBoard[1][2] = 2;
        testBoard[2][0] = 7;
        testBoard[2][1] = 6;
        testBoard[2][2] = 5;
        Board board2 = new Board(testBoard);
        assert board2.hamming() == 5;
        assert board2.manhattan() == 10;
        assert !board2.equals(solvedBoard);
        assert !board2.equals(board2.twin());

        // 8 1 3
        // 4 0 2
        // 7 6 5
        neighbors = board2.neighbors();
        count = 0;
        for (Board n : neighbors) {
            switch (++count) {
                case 1:
                    // up neighbor
                    // 8 0 3
                    // 4 1 2
                    // 7 6 5
                    assert n.hamming() == 5;
                    assert n.manhattan() == 11;
                    break;
                case 2:
                    // down neighbor
                    // 8 1 3
                    // 4 6 2
                    // 7 0 5
                    assert n.hamming() == 5;
                    assert n.manhattan() == 9;
                    break;
                case 3:
                    // left neighbor
                    // 8 1 3
                    // 0 4 2
                    // 7 6 5
                    assert n.hamming() == 6;
                    assert n.manhattan() == 11;
                    break;
                case 4:
                    // right neighbor
                    // 8 1 3
                    // 4 2 0
                    // 7 6 5
                    assert n.hamming() == 5;
                    assert n.manhattan() == 9;
                    break;

            }
        }
        assert count == 4;

        testBoard = new int[size][size];
        // 3 7 5
        // 6 8 4
        // 1 0 2
        // Manhattan
        // 2 3 2
        // 2 1 2
        // 2 0 3
        testBoard[0][0] = 3;
        testBoard[0][1] = 7;
        testBoard[0][2] = 5;
        testBoard[1][0] = 6;
        testBoard[1][1] = 8;
        testBoard[1][2] = 4;
        testBoard[2][0] = 1;
        testBoard[2][1] = 0;
        testBoard[2][2] = 2;
        Board board3 = new Board(testBoard);
        assert board3.hamming() == 8;
        assert board3.manhattan() == 17;
        assert !board3.equals(solvedBoard);
        assert !board3.equals(board2);
        assert !board3.equals(board3.twin());

        neighbors = board3.neighbors();
        count = 0;
        for (Board n : neighbors) {
            switch (++count) {
                case 1:
                    // up neighbor
                    // 3 7 5
                    // 6 0 4
                    // 1 8 2
                    assert n.hamming() == 7;
                    assert n.manhattan() == 16;
                    break;
                case 2:
                    // left neighbor
                    // 3 7 5
                    // 6 8 4
                    // 0 1 2
                    assert n.hamming() == 8;
                    assert n.manhattan() == 18;
                    break;
                case 3:
                    // right neighbor
                    // 3 7 5
                    // 6 8 4
                    // 1 2 0
                    assert n.hamming() == 8;
                    assert n.manhattan() == 16;
                    break;

            }
        }
        assert count == 3;

        int maxHamming = 0;
        int maxManhattan = 0;

        for (int i = 0; i < 10; i++) {
            int[] vals = getHammingAndManhattan(4);
            if (vals[0] > maxHamming) {
                maxHamming = vals[0];
            }
            if (vals[1] > maxManhattan) {
                maxManhattan = vals[1];
            }
        }
        StdOut.println("max hamming:" + maxHamming);
        StdOut.println("max manhattan" + maxManhattan);
    }

    private static int[] getHammingAndManhattan(int n) {
        int[] nums = new int[n * n];
        for (int i = 0; i < n * n; i++) {
            nums[i] = i;
        }

        StdRandom.shuffle(nums);
        int i = 0;
        int[][] t = new int[n][n];
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                t[row][col] = nums[i++];
            }
        }
        Board b = new Board(t);
        int[] vals = new int[2];
        vals[0] = b.hamming();
        vals[1] = b.manhattan();
        return vals;
    }
}