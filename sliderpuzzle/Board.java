public class Board {
    private int size;
    private int[][] board;

    /**
     * Create a board from an n-by-n array of tiles,
     * where tiles[row][col] = tile at (row, col)
     * 
     * @param tiles the nxn array of integers to create
     * @throws IllegalArgumentException if tiles is null.
     */
    public Board(int[][] tiles) {
        if (tiles == null) {
            throw new IllegalArgumentException("tiles must not be null.");
        }

        size = tiles.length;
        board = tiles;
    }

    /**
     * String representation of this board
     * 
     * @return string representation of this board
     */
    public String toString() {
        String output = "" + size;
        for (int row = 0; row < size; row++) {
            output += "\n";
            for (int col = 0; col < size; col++) {
                output += board[row][col] + " ";
            }
        }

        return output;
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
        int outOfPlaceCount = 0;
        int goalTileValue = 1;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                // The last one should always be the zero tile ...
                if (goalTileValue == size * size) {
                    break; // don't count the zero tile out of place
                }
                // otherwise check the current row/col goal value
                if (board[row][col] != goalTileValue++) {
                    outOfPlaceCount++;
                }
            }
        }

        return outOfPlaceCount;
    }

    /**
     * 
     * Sum of Manhattan distances between tiles and goal.
     * 
     * @return sum of Manhattan distances between tiles and goal.
     */
    public int manhattan() {
        int manhattanSum = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int tileValue = board[row][col];

                if (tileValue == 0) {
                    continue; // don't count the zero tile
                }

                int rowDiff = Math.abs(row - ((tileValue - 1) / size));
                int colDiff = Math.abs(col - ((tileValue - 1) % size));
                manhattanSum += rowDiff + colDiff;
            }
        }
        return manhattanSum;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // // does this board equal y?
    // public boolean equals(Object y)

    // // all neighboring boards
    // public Iterable<Board> neighbors()

    // // a board that is obtained by exchanging any pair of tiles
    // public Board twin()

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

        Board board = new Board(testBoard);
        assert board.dimension() == size;
        assert board.hamming() == 0;
        assert board.manhattan() == 0;
        assert board.toString().equals("" + size + "\n1 2 3 \n4 5 6 \n7 8 0 ");

        testBoard = new int[size][size];
        // 8 1 3
        // 4 0 2
        // 7 6 5

        // 3 1 0
        // 0 2 2
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
        board = new Board(testBoard);
        assert board.hamming() == 5;
        assert board.manhattan() == 10;
    }
}