import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class RandomWord {
    public static void main(String[] args) {
        String champion = "";
        String current = "";
        double count = 0;
        while (!StdIn.isEmpty()) {
            current = StdIn.readString();
            count++;
            if (StdRandom.bernoulli(1/count)) {
                champion = current;
            }
        }
        StdOut.println(champion);
    }
}
