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
            moveToFront(chars, c, true);
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
            int value = BinaryStdIn.readByte() & 0xFF;
            Character c = chars.get(value);
            moveToFront(chars, c, false);
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

    private static void moveToFront(LinkedList<Character> chars, char c, boolean shouldPrint) {
        for (int i = 0; i < R; i++) {
            if (chars.get(i) == c) {
                chars.addFirst(c);
                chars.remove(i);
                if (shouldPrint) {
                    BinaryStdOut.write(i);
                }
                break;
            }
        }
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
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