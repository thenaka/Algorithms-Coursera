import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private static final double CONFIDENCE_95 = 1.96;
    private int dimension;
    private int numTrials;
    private double[] trialResults;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n < 1) {
            throw new IllegalArgumentException("n must be greater than zero.");
        }
        if (trials < 1) {
            throw new IllegalArgumentException("trials must be greater than zero");
        }

        dimension = n;
        numTrials = trials;
        trialResults = new double[trials];
        conductTrials();
    }

    private void conductTrials() {
        for (int i = 0; i < numTrials; i++) {
            trialResults[i] = conductTrial() / (dimension * dimension);
        }
    }

    private double conductTrial() {
        Percolation percolation = new Percolation(dimension);
        double count = 0.0;
        while (!percolation.percolates()) {
            int row = StdRandom.uniformInt(dimension) + 1;
            int col = StdRandom.uniformInt(dimension) + 1;
            if (!percolation.isOpen(row, col)) {
                count++;
                percolation.open(row, col);
            }
        }
        return count;
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(trialResults);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(trialResults);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - (CONFIDENCE_95 * stddev()) / Math.sqrt(numTrials);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + (CONFIDENCE_95 * stddev()) / Math.sqrt(numTrials);
    }

    // test client (see below)
    public static void main(String[] args) {
        PercolationStats percStats = new PercolationStats(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        System.out.println("mean\t\t\t= " + percStats.mean());
        System.out.println("stddev\t\t\t= " + percStats.stddev());
        System.out.println("95% confidence interval\t= [" + percStats.confidenceLo() + ", " + percStats.confidenceHi() + "]");
    }
}
