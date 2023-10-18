import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {
    private static final int RADIX = 26; // A-Z

    private char[] boardChars;
    private boolean[] marked;
    private int rowCount;
    private int colCount;
    private Node root;
    private Neighbors[] adj;

    private class Node {
        private boolean isWord;
        private Node[] next = new Node[RADIX];
    }

    private class Neighbors {
        private int n = 0;
        private int[] neighbor = new int[8];
    }

    /**
     * Initializes the data structure using the given array of strings as the
     * dictionary.
     *
     * @param dictionary Array of dictionary words. Each word only contains capital
     *                   letters A to Z.
     * @throws IllegalArgumentException if {@code dictionary} is null or empty.
     */
    public BoggleSolver(String[] dictionary) {
        if (dictionary == null) {
            throw new IllegalArgumentException("Dictionary must not be null.");
        }
        if (dictionary.length == 0) {
            throw new IllegalArgumentException("Dictionary must not be empty.");
        }

        buildTrie(dictionary);
    }

    private void buildTrie(String[] dictionary) {
        for (String word : dictionary) {
            root = addWord(root, word, 0);
        }
    }

    private Node addWord(Node node, String word, int charIndex) {
        if (node == null) {
            node = new Node();
        }
        if (word.length() == charIndex) {
            node.isWord = true;
        } else {
            char currentChar = word.charAt(charIndex);
            node.next[currentChar - 'A'] = addWord(node.next[currentChar - 'A'] , word, charIndex + 1);
        }
        return node;
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null");
        }

        rowCount = board.rows();
        colCount = board.cols();

        int boardDimensions = rowCount * colCount;
        marked = new boolean[boardDimensions];
        boardChars = new char[boardDimensions];
        adj = new Neighbors[boardDimensions];
        setupNeighbors();
        copyBoard(board);

        SET<String> words = DFS();
        return words;
    }

    private void setupNeighbors() {
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                int index = i * colCount + j;
                adj[index] = new Neighbors();
                if (i > 0) {
                    adj[index].neighbor[adj[index].n++] = (i - 1) * colCount + j;
                    if (j < colCount - 1) {
                        adj[index].neighbor[adj[index].n++] = (i - 1) * colCount + j + 1;
                    }
                }
                if (i < rowCount - 1) {
                    adj[index].neighbor[adj[index].n++] = (i + 1) * colCount + j;
                    if (j > 0) {
                        adj[index].neighbor[adj[index].n++] = (i + 1) * colCount + j - 1;
                    }
                }
                if (j > 0) {
                    adj[index].neighbor[adj[index].n++] = i * colCount + j - 1;
                    if (i > 0) {
                        adj[index].neighbor[adj[index].n++] = (i - 1) * colCount + j - 1;
                    }
                }
                if (j < colCount - 1) {
                    adj[index].neighbor[adj[index].n++] = i * colCount + j + 1;
                    if (i < rowCount - 1) {
                        adj[index].neighbor[adj[index].n++] = (i + 1) * colCount + j + 1;
                    }
                }
            }
        }
    }

    private void copyBoard(BoggleBoard board) {
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                int index = i * colCount + j;
                char c = board.getLetter(i, j);
                boardChars[index] = c;
            }
        }
    }

    private SET<String> DFS() {
        SET<String> words = new SET<String>();
        for (int i = 0; i < rowCount * colCount; i++) {
            DFS(i, new StringBuilder(), words, root);
        }
        return words;
    }

    private void DFS(int index, StringBuilder pre, SET<String> words, Node node) {
        char c = boardChars[index];
        Node next = node.next[c - 'A'];
        if (c == 'Q' && next != null) {
            next = next.next['U' - 'A'];
        }
        if (next == null) {
            return;
        }

        if (c == 'Q') {
            pre.append("QU");
        } else {
            pre.append(c);
        }

        String str = pre.toString();
        if (pre.length() > 2 && next.isWord) {
            words.add(str);
        }

        marked[index] = true;
        for (int j = 0; j < adj[index].n; j++) {
            int nextIndex = adj[index].neighbor[j];
            if (!marked[nextIndex]) {
                DFS(nextIndex, new StringBuilder(pre), words, next);
            }
        }
        marked[index] = false;
    }

    // Returns the score of the given word if it is in the dictionary, zero
    // otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        throwIfWordNull(word);

        if (!containsWord(word)) {
            return 0;
        }

        switch (word.length()) {
            case 0:
            case 1:
            case 2:
                return 0;
            case 3:
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 5;
            default:
                return 11;
        }
    }

    private void throwIfWordNull(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Word must not be null");
        }
    }

    private boolean containsWord(String word) {
        throwIfWordNull(word);

        Node node = getWord(root, word, 0);
        return node == null ? false : node.isWord;
    }

    private Node getWord(Node node, String word, int charIndex) {
        if (node == null) {
            return null;
        }
        if (word.length() == charIndex) {
            return node;
        }
        char currentChar = word.charAt(charIndex);
        return getWord(node.next[currentChar - 'A'], word, charIndex + 1);
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}