import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import java.util.LinkedList;

public class MoveToFront {
    private static final int R = 256;

    /**
     * Apply move-to-front encoding, reading from standard input and writing to
     * standard output.
     */
    public static void encode() {
        LinkedList<Character> chars = setupCharList();

        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();

            int charIndex = chars.indexOf(c);
            chars.remove(charIndex);
            chars.addFirst(c);

            BinaryStdOut.write(charIndex, 8);
        }

        BinaryStdOut.close();
    }

    /**
     * Apply move-to-front decoding, reading from standard input and writing to
     * standard output.
     */
    public static void decode() {
        LinkedList<Character> chars = setupCharList();

        while (!BinaryStdIn.isEmpty()) {
            char position = BinaryStdIn.readChar();
            Character c = chars.remove((int) position);
            chars.addFirst(c);
            BinaryStdOut.write(c);
        }

        BinaryStdOut.close();
    }

    private static LinkedList<Character> setupCharList() {
        LinkedList<Character> chars = new LinkedList<Character>();
        for (int i = 0; i < R; i++) {
            chars.add((char) i);
        }
        return chars;
    }

    /**
     * If args[0] is "-", apply move-to-front encoding
     * If args[0] is "+", apply move-to-front decoding
     *
     * @param args see above.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("args must be '-' or '+'");
        }
        switch (args[0]) {
            case "-":
                encode();
                break;
            case "+":
                decode();
                break;
            default:
                throw new IllegalArgumentException("args must be '-' or '+'");
        }
    }
}