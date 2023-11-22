import java.util.Arrays;

public class CircularSuffixArray {
    private final int length;
    private Integer[] index;

    /**
     * Circular suffix array of s.
     *
     * @param s String to create a circular suffix.
     */
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException("String must not be null.");
        }

        length = s.length();
        initalizeIndex();
        sortIndex(s);
    }

    /**
     * Length of string.
     *
     * @return length of string.
     */
    public int length() {
        return length;
    }

    /**
     * Returns index of ith sorted suffix.
     *
     * @param i The ith sorted suffix.
     * @return index of the ith sorted suffix.
     */
    public int index(int i) {
        if (i < 0 || i >= length) {
            throw new IllegalArgumentException("Index must be in valid range");
        }
        return index[i];
    }

    /**
     * Initializes index array with the index of the character that should start
     * this row
     */
    private void initalizeIndex() {
        index = new Integer[length];
        for (int i = 0; i < length; i++) {
            index[i] = i;
        }
    }

    /**
     * Sorts index.
     *
     * @param s string used to sort index.
     */
    private void sortIndex(String s) {
        Arrays.sort(index, (Integer t, Integer t1) -> {
            for (int i = 0; i < length; i++) {
                char c = s.charAt((t + i) % length);
                char c1 = s.charAt((t1 + i) % length);

                if (c < c1) {
                    return -1;
                }
                if (c > c1) {
                    return 1;
                }
            }
            return t.compareTo(t1);
        });
    }

    // unit testing (required)
    public static void main(String[] args) {
        String s = "ABRACADABRA!";
        CircularSuffixArray c = new CircularSuffixArray(s);
        assert c.length() == s.length();
        assert c.index(0) == 11;
        assert c.index(1) == 10;
        assert c.index(2) == 7;
        assert c.index(3) == 0;
        assert c.index(4) == 3;
        assert c.index(5) == 5;
        assert c.index(6) == 8;
        assert c.index(7) == 1;
        assert c.index(8) == 4;
        assert c.index(9) == 6;
        assert c.index(10) == 9;
        assert c.index(11) == 2;
    }
}