import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int radix = 256;

    /**
     * Apply Burrows-Wheeler transform, reading from standard input and writing to
     * standard output.
     */
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(s);

        int first = 0;
        int length = s.length();
        for (int i = 0; i < length; i++) {
            if (csa.index(i) == 0) {
                first = i;
                break;
            }
        }
        BinaryStdOut.write(first);

        for (int i = 0; i < length; i++) {
            int lastIndex = (csa.index(i) - 1 + length) % length;
            BinaryStdOut.write(s.charAt(lastIndex));
        }

        BinaryStdOut.close();
    }

    /**
     * Apply Burrows-Wheeler inverse transform, reading from standard input and
     * writing to standard output.
     */
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String lastCol = BinaryStdIn.readString();

        int length = lastCol.length();
        int[] next = new int[length];
        int[] count = new int[radix + 1];
        char[] firstCol = new char[length];
        for (int i = 0; i < length; i++) {
            count[lastCol.charAt(i) + 1]++;
        }
        for (int i = 0; i < radix; i++) {
            count[i + 1] += count[i];
        }
        for (int i = 0; i < length; i++) {
            int position = count[lastCol.charAt(i)]++;
            firstCol[position] = lastCol.charAt(i);
            next[position] = i;
        }

        for (int i = 0; i < length; i++) {
            BinaryStdOut.write(firstCol[first]);
            first = next[first];
        }
        BinaryStdOut.close();
    }

    /**
     * If args[0] is "-", apply Burrows-Wheeler transform.
     * If args[0] is "+", apply Burrows-Wheeler inverse transform.
     *
     * @param args see above.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("args must be '-' or '+'");
        }
        switch (args[0]) {
            case "-":
                transform();
                break;
            case "+":
                inverseTransform();
                break;
            default:
                throw new IllegalArgumentException("args must be '-' or '+'");
        }
    }

}