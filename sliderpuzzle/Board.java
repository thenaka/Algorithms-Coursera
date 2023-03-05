import edu.princeton.cs.algs4.Queue;

public class Board {
    private int size;
    private Tile[] boardTiles;
    private Tile zeroTile;

    private class Tile {
        public int row;
        public int col;
        public int index;
        public int value;

        public Tile(int r, int c, int i, int val) {
            row = r;
            col = c;
            index = i;
            value = val;
        }

        public Tile(Tile tile) {
            this(tile.row, tile.col, tile.index, tile.value);
        }
    }

    private int hammingValue = -1;
    private int manhattanValue = -1;

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
        createBoardTiles(tiles);
    }

    private void createBoardTiles(int[][] tiles) {
        boardTiles = new Tile[size * size];
        int i = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int value = tiles[row][col];
                Tile currentTile = new Tile(row, col, i, value);
                boardTiles[i++] = currentTile;
                if (value == 0) {
                    zeroTile = currentTile;
                }
            }
        }
    }

    private Board(Tile[] tiles) {
        assert tiles != null;
        size = (int) Math.sqrt(tiles.length);
        createBoardTiles(tiles);
    }

    private void createBoardTiles(Tile[] tiles) {
        boardTiles = new Tile[size * size];
        for (int i = 0; i < size * size; i++) {
            boardTiles[i] = new Tile(tiles[i]);
            if (boardTiles[i].value == 0) {
                zeroTile = boardTiles[i];
            }
        }
    }

    /**
     * String representation of this board
     * 
     * @return string representation of this board
     */
    public String toString() {
        String output = "" + size;
        for (int i = 0; i < size * size; i++) {
            if (i % size == 0) {
                output += "\n";
            }
            output += boardTiles[i].value + " ";
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
        if (hammingValue > -1) {
            return hammingValue;
        }

        hammingValue = 0;
        int goalTileValue = 1;
        for (int i = 0; i < size * size; i++) {
            int tileValue = boardTiles[i].value;
            if (tileValue == 0) {
                goalTileValue++;
                continue; // don't count zero out of place
            }

            if (tileValue != goalTileValue++) {
                hammingValue++;
            }
        }

        return hammingValue;
    }

    /**
     * 
     * Sum of Manhattan distances between tiles and goal.
     * 
     * @return sum of Manhattan distances between tiles and goal.
     */
    public int manhattan() {
        if (manhattanValue > -1) {
            return manhattanValue;
        }

        manhattanValue = 0;
        for (int i = 0; i < size * size; i++) {
            int tileValue = boardTiles[i].value;
            if (tileValue == 0) {
                continue; // don't count the zero tile as out of place
            }

            int rowDiff = Math.abs(boardTiles[i].row - ((tileValue - 1) / size));
            int colDiff = Math.abs(boardTiles[i].col - ((tileValue - 1) % size));
            manhattanValue += rowDiff + colDiff;
        }

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
        if (!(that instanceof Board)) {
            return false;
        }

        Board other = (Board) that;
        if (size != other.size) {
            return false;
        }

        for (int i = 0; i < size * size; i++) {
            if (boardTiles[i].value != other.boardTiles[i].value) {
                return false;
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

        if (zeroTile.row > 0) {
            Board upNeighbor = new Board(boardTiles);
            upNeighbor.swap(zeroTile.index, zeroTile.index - size);
            n.enqueue(upNeighbor);
        }
        if (zeroTile.row < size - 1) {
            Board downNeighbor = new Board(boardTiles);
            downNeighbor.swap(zeroTile.index, zeroTile.index + size);
            n.enqueue(downNeighbor);
        }
        if (zeroTile.col > 0) {
            Board leftNeighbor = new Board(boardTiles);
            leftNeighbor.swap(zeroTile.index, zeroTile.index - 1);
            n.enqueue(leftNeighbor);
        }
        if (zeroTile.col < size - 1) {
            Board rightNeighbor = new Board(boardTiles);
            rightNeighbor.swap(zeroTile.index, zeroTile.index + 1);
            n.enqueue(rightNeighbor);
        }

        return n;
    }

    private void swap(int index1, int index2) {
        assert index1 > -1 && index1 < size * size;
        assert index2 > -1 && index2 < size * size;

        int tempValue = boardTiles[index1].value;
        boardTiles[index1].value = boardTiles[index2].value;
        boardTiles[index2].value = tempValue;

        if (boardTiles[index1].value == 0) {
            zeroTile = boardTiles[index1];
        }
        if (boardTiles[index2].value == 0) {
            zeroTile = boardTiles[index2];
        }
    }

    /**
     * 
     * A board that is obtained by exchanging any pair of tiles.
     * 
     * @return a board that has a pair of tiles exchanged.
     */
    public Board twin() {
        Board twin = new Board(boardTiles);

        int index1 = 0;
        int index2 = 0;

        do {
            index1 = StdRandom.uniformInt(size * size);
            index2 = StdRandom.uniformInt(size * size);
        } while (index1 == index2);

        twin.swap(index1, index2);
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
        assert solvedBoard.equals(null) == false;
        assert solvedBoard.equals(solvedBoard);
        assert solvedBoard.equals(solvedBoard.twin()) == false;

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
        assert board2.equals(solvedBoard) == false;
        assert board2.equals(board2.twin()) == false;

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
        assert board3.equals(solvedBoard) == false;
        assert board3.equals(board2) == false;
        assert board3.equals(board3.twin()) == false;

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
    }
}