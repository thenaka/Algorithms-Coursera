import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    private boolean solvable;
    private SearchNode solution;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException("board must not be null.");
        }

        solve(initial);
    }

    private void solve(Board board) {
        MinPQ<SearchNode> searchNodes = new MinPQ<>();
        MinPQ<SearchNode> twinSearchNodes = new MinPQ<>();

        SearchNode start = new SearchNode(board);
        SearchNode twinStart = new SearchNode(board.twin());

        searchNodes.insert(start);
        twinSearchNodes.insert(twinStart);

        SearchNode candidate;
        SearchNode twinCandidate;

        while (true) {
            // original board *************************************************************
            candidate = searchNodes.delMin();

            if (candidate.isSolved()) {
                solvable = true;
                solution = candidate;
                break;
            }

            Iterable<Board> neighbors = candidate.getBoard().neighbors();
            SearchNode parent = candidate.getParent();
            for (Board neighbor : neighbors) {
                if (parent != null && parent.getBoard() != null && parent.getBoard().equals(neighbor)) {
                    continue; // don't backtrack
                }
                searchNodes.insert(new SearchNode(candidate, neighbor, candidate.getMoves() + 1));
            }

            // twin board *****************************************************************
            twinCandidate = twinSearchNodes.delMin();

            if (twinCandidate.isSolved()) {
                solvable = false;
                solution = null;
                break;
            }

            Iterable<Board> twinNeighbors = twinCandidate.getBoard().neighbors();
            SearchNode twinParent = twinCandidate.getParent();
            for (Board neighbor : twinNeighbors) {
                if (twinParent != null && twinParent.getBoard() != null && twinParent.getBoard().equals(neighbor)) {
                    continue; // don't backtrack
                }
                twinSearchNodes.insert(
                        new SearchNode(twinCandidate, neighbor, twinCandidate.getMoves()));
            }
        }
    }

    /**
     * 
     * Is the initial board solvable.
     * 
     * @return if the initial board is solvable.
     */
    public boolean isSolvable() {
        return solvable;
    }

    //
    /**
     * 
     * Min number of moves to solve initial board; -1 if unsolvable.
     * 
     * @return min number of moves to solve initial board; -1 if unsolvable.
     */
    public int moves() {
        if (solution == null) {
            return -1;
        }
        return solution.getMoves();
    }

    /**
     * 
     * Sequence of boards in a shortest solution; null if unsolvable
     * 
     * @return sequence of boards in a shortest solution; null if unsolvable.
     */
    public Iterable<Board> solution() {
        if (solution == null) {
            return null;
        }

        Stack<Board> entireSolution = new Stack<>();
        SearchNode currentNode = solution;
        Board currentBoard;
        while (currentNode != null) {
            currentBoard = currentNode.getBoard();
            entireSolution.push(currentBoard);
            currentNode = currentNode.getParent();
        }
        return entireSolution;
    }

    // test client (see below)
    public static void main(String[] args) {
        int[][] tiles1 = {
                { 0, 1, 3 },
                { 4, 2, 5 },
                { 7, 8, 6 }
        };
        Solver solver = testSolver(new Board(tiles1));
        assert solver.isSolvable();
        assert solver.moves() == 4;

        int[][] tiles2 = {
                { 5, 8, 7 },
                { 2, 6, 4 },
                { 3, 1, 0 }
        };
        solver = testSolver(new Board(tiles2));
        assert solver.isSolvable();
        assert solver.moves() == 28;

        int[][] tiles3 = {
                { 1, 2, 3 },
                { 4, 5, 6 },
                { 7, 8, 0 }
        };
        solver = testSolver(new Board(tiles3));
        assert solver.isSolvable();
        assert solver.moves() == 0;

        int[][] tiles4 = {
                { 1, 0 },
                { 3, 2 }
        };
        solver = testSolver(new Board(tiles4));
        assert solver.isSolvable();
        assert solver.moves() == 1;
    }

    private static Solver testSolver(Board board) {
        StdOut.println("************************** Solving ***************************");
        StdOut.println(board);
        Solver solver = new Solver(board);
        StdOut.println("Solvable: " + solver.isSolvable());
        StdOut.println("Moves: " + solver.moves());

        if (solver.isSolvable()) {
            for (Board b : solver.solution()) {
                StdOut.println(b);
            }
        }
        StdOut.println("***********************************************************");
        return solver;
    }

    private class SearchNode implements Comparable<SearchNode> {
        private Board board;
        private int moves;
        private SearchNode parent;

        /**
         * 
         * Create the initial search node.
         * 
         * @param b initial board.
         */
        public SearchNode(Board b) {
            this(null, b, 0);
        }

        /**
         * 
         * Create a search node.
         * 
         * @param p the parent of this search node.
         * @param b the current board for this search node.
         * @param m the number of moves to reach this board.
         */
        public SearchNode(SearchNode p, Board b, int m) {
            if (b == null) {
                throw new IllegalArgumentException("Board must not be null.");
            }

            parent = p;
            board = b;
            moves = m;
        }

        /**
         * 
         * Get the number of moves taken to reach this board.
         * 
         * @return the number of moves to reach this board.
         */
        public int getMoves() {
            return moves;
        }

        /**
         * 
         * Does this search node have a solved board?
         * 
         * @return true if this node has a solved board, otherwise false.
         */
        public boolean isSolved() {
            return board.isGoal();
        }

        /**
         * 
         * Get the board for this search node.
         * 
         * @return the board for this search node.
         */
        public Board getBoard() {
            return board;
        }

        /**
         * 
         * Get the parent of this search node.
         * 
         * @return the parent of this search node.
         */
        public SearchNode getParent() {
            return parent;
        }

        private int manhattanPriority() {
            return board.manhattan() + moves;
        }

        /**
         * 
         * Compare this search node with the other search node.
         * 
         * @param other the other SearchNode to compare to.
         * @return -1 if this search node's manhattan priority is less than other',
         *         1 if this search node's manhattan priority is greater than other's,
         *         otherwise zero.
         * @throws NullPointerException if other is null.
         */
        public int compareTo(SearchNode other) {
            if (other == null) {
                throw new NullPointerException("SearchNode to compare to must not be null.");
            }

            if (manhattanPriority() < other.manhattanPriority()) {
                return -1;
            }
            if (manhattanPriority() > other.manhattanPriority()) {
                return 1;
            }

            return 0;
        }
    }
}