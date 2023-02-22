import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Permutation {
    public static void main(String[] args) {
        int numToPrint = Integer.parseInt(args[0]);
        RandomizedQueue<String> randomQueue = new RandomizedQueue<>();
        while (!StdIn.isEmpty()) {
            randomQueue.enqueue(StdIn.readString());
        }

        int count = 0;
        if (numToPrint == 0) {
            return;
        }

        for (String s : randomQueue) {
            StdOut.println(s);
            if (++count == numToPrint) {
                break;
            }
        }
    }
}