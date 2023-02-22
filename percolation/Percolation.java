import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private int dimension;
    private int openSitesCount;
    private boolean[][] openSites;
    private WeightedQuickUnionUF quickUnion;
    private int quickUnionLength;
    private int virtualFirstElement = 0;
    private int virtualLastElement;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("Grid dimension must be greater than zero.");
        }

        dimension = n;
        setupOpenSites();
        setupQuickUnion();
    }

    // Example grid (5x5):
    //         0
    //   /   / | \   \
    //  /   /  |  \   \        
    // 1   2   3   4   5
    // 6   7   8   9   10
    // 11  12  13  14  15
    // 16  17  18  19  20
    // 21  22  23  24  25
    //  \   \  |  /   /
    //   \   \ | /   /
    //         26
    // The grid is created with two virtual nodes that the first row and last row
    // are connected to.
    // Which allows checking for percolation.
    private void setupQuickUnion() {
        quickUnionLength = (dimension * dimension) + 2; // two additional nodes are created for the virtual nodes
        quickUnion = new WeightedQuickUnionUF(quickUnionLength);

        // connect the first rows' elements to the 0th zero node
        for (int i = 1; i <= dimension; i++) {
            quickUnion.union(virtualFirstElement, i);
        }

        virtualLastElement = quickUnionLength - 1;
        int lastRow = (dimension * dimension) - dimension;
        for (int i = 1; i <= dimension; i++) {
            quickUnion.union(virtualLastElement, lastRow + i);
        }
    }

    private void setupOpenSites() {
        openSitesCount = 0;
        openSites = new boolean[dimension][dimension];
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                openSites[row][col] = false; // all sites are initially closed
            }
        }
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        throwIfOutsideDimension(row, col);

        int zeroBasedRow = row - 1;
        int zeroBasedCol = col - 1;

        if (openSites[zeroBasedRow][zeroBasedCol]) {
            return; // do nothing if this site is already open
        }

        openSitesCount++;
        openSites[zeroBasedRow][zeroBasedCol] = true;
        connectOpenSiteToAdjacent(zeroBasedRow, zeroBasedCol);
    }

    private void connectOpenSiteToAdjacent(int zeroBasedRow, int zeroBasedCol) {
        int oneRowUp = zeroBasedRow - 1;
        if (oneRowUp >= 0 && openSites[oneRowUp][zeroBasedCol]) {
            quickUnion.union(getIdFromRowCol(zeroBasedRow, zeroBasedCol), getIdFromRowCol(oneRowUp, zeroBasedCol));
        }

        int oneRowDown = zeroBasedRow + 1;
        if (oneRowDown < dimension && openSites[oneRowDown][zeroBasedCol]) {
            quickUnion.union(getIdFromRowCol(zeroBasedRow, zeroBasedCol), getIdFromRowCol(oneRowDown, zeroBasedCol));
        }

        int oneColLeft = zeroBasedCol - 1;
        if (oneColLeft >= 0 && openSites[zeroBasedRow][oneColLeft]) {
            quickUnion.union(getIdFromRowCol(zeroBasedRow, zeroBasedCol), getIdFromRowCol(zeroBasedRow, oneColLeft));
        }

        int oneColRight = zeroBasedCol + 1;
        if (oneColRight < dimension && openSites[zeroBasedRow][oneColRight]) {
            quickUnion.union(getIdFromRowCol(zeroBasedRow, zeroBasedCol), getIdFromRowCol(zeroBasedRow, oneColRight));
        }
    }

    private int getIdFromRowCol(int zeroBasedRow, int zeroBasedCol) {
        return (zeroBasedRow * dimension) + zeroBasedCol + 1;
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        throwIfOutsideDimension(row, col);

        int zeroBasedRow = row - 1;
        int zeroBasedCol = col - 1;

        return openSites[zeroBasedRow][zeroBasedCol];
    }

    // is the site (row, col) full?
    // meaning is the given row, col open and connected to the top
    public boolean isFull(int row, int col) {
        throwIfOutsideDimension(row, col);

        int zeroBasedRow = row - 1;
        int zeroBasedCol = col - 1;

        if (!openSites[zeroBasedRow][zeroBasedCol]) {
            return false;
        }

        int id = getIdFromRowCol(zeroBasedRow, zeroBasedCol);

        return quickUnion.find(virtualFirstElement) == quickUnion.find(id);
    }

    private void throwIfOutsideDimension(int row, int col) {
        if (row > dimension || row < 1) {
            throw new IllegalArgumentException("row must be from 0 to " + dimension);
        }
        if (col > dimension || col < 1) {
            throw new IllegalArgumentException("col must not from 0 to " + dimension);
        }
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openSitesCount;
    }

    // does the system percolate?
    public boolean percolates() {
        // handle corner case
        if (dimension == 1) {
            return isOpen(1, 1);
        }
        return quickUnion.find(virtualFirstElement) == quickUnion.find(virtualLastElement);
    }

    // test client (optional)
    public static void main(String[] args) {
        Percolation percolation = new Percolation(1);
        assert !percolation.percolates();
        assert percolation.numberOfOpenSites() == 0;

        percolation = new Percolation(5);
        assert !percolation.percolates();
        assert percolation.numberOfOpenSites() == 0;

        assert !percolation.isOpen(1, 1);
        assert !percolation.isFull(1, 1);
        percolation.open(1, 1);
        assert percolation.isOpen(1, 1);
        assert percolation.isFull(1, 1);
        assert !percolation.percolates();
        assert percolation.numberOfOpenSites() == 1;

        percolation.open(2, 1);
        assert percolation.isOpen(2, 1);
        assert percolation.isFull(2, 1);
        assert !percolation.percolates();
        assert percolation.numberOfOpenSites() == 2;

        percolation.open(3, 1);
        assert percolation.isOpen(3, 1);
        assert percolation.isFull(3, 1);
        assert !percolation.percolates();
        assert percolation.numberOfOpenSites() == 3;

        percolation.open(4, 1);
        assert percolation.isOpen(4, 1);
        assert percolation.isFull(4, 1);
        assert !percolation.percolates();
        assert percolation.numberOfOpenSites() == 4;

        percolation.open(5, 1);
        assert percolation.isOpen(5, 1);
        assert percolation.isFull(5, 1);
        assert percolation.percolates();
        assert percolation.numberOfOpenSites() == 5;
    }
}
