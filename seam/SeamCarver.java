import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {
    private static final int COLOR_MASK = 0xFF;

    private Picture picture;
    private Picture transposedPicture;
    private double[][] energy;
    private double[][] transposedEnergy;
    private ShortestPath[][] verticalShortestPaths;
    private ShortestPath[][] horizontalShortestPaths;
    private int[] verticalPath;
    private int[] horizonalPath;

    /**
     * Create a seam carver object based on the given picture
     *
     * @param picture used to create seam carver object.
     * @exception IllegalArgumentException if picture is null.
     */
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("picture must not be null.");
        }
        initialize(picture);
    }

    private void initialize(Picture pic) {
        this.picture = new Picture(pic);

        int picHeight = this.picture.height();
        int picWidth = this.picture.width();

        this.transposedPicture = new Picture(picHeight, picWidth);

        this.energy = new double[picWidth][picHeight];
        this.transposedEnergy = new double[picHeight][picWidth];

        this.verticalShortestPaths = new ShortestPath[picWidth][picHeight];
        this.horizontalShortestPaths = new ShortestPath[picHeight][picWidth];

        for (int row = 0; row < picHeight; row++) {
            for (int col = 0; col < picWidth; col++) {
                if (picHeight > 2 && picWidth > 2 && // no border pixels for 1 or 2 height or width pictures
                        col == 0 || col == picWidth - 1 || row == 0 || row == picHeight - 1) {
                    this.energy[col][row] = 1000;
                } else {
                    this.energy[col][row] = Double.POSITIVE_INFINITY;
                }
                this.transposedPicture.setRGB(row, col, this.picture.getRGB(col, row));
            }
        }

        int tranPicHeight = this.transposedPicture.height();
        int tranPicWidth = this.transposedPicture.width();
        for (int row = 0; row < tranPicHeight; row++) {
            for (int col = 0; col < tranPicWidth; col++) {
                if (tranPicHeight > 2 && tranPicWidth > 2 && // no border pixels for 1 or 2 height or width pictures
                        col == 0 || col == tranPicWidth - 1 || row == 0 || row == tranPicHeight - 1) {
                    this.transposedEnergy[col][row] = 1000;
                } else {
                    this.transposedEnergy[col][row] = Double.POSITIVE_INFINITY;
                }
            }
        }
        this.verticalPath = null;
        this.horizonalPath = null;
    }

    /**
     * Get the current picture.
     *
     * @return the current pciture.
     */
    public Picture picture() {
        return this.picture;
    }

    /**
     * Get the width of current picture.
     *
     * @return the width of the current picture.
     */
    public int width() {
        return this.picture.width();
    }

    /**
     * Get the height of current picture.
     *
     * @return height of current picture.
     */
    public int height() {
        return this.picture.height();
    }

    /**
     * Get the energy of pixel at column x and row y.
     *
     * @param x zero-based x-pixel position.
     * @param y zero-based y-pixel position.
     * @return energy of pixel at column x and row y.
     * @exception IllegalArgumentExceptio if x or y is outside the bounds of the
     *                                    picture.
     */
    public double energy(int x, int y) {
        return energy(x, y, this.energy, this.picture);
    }

    private double energy(int x, int y, double[][] en, Picture pic) {
        if (x < 0 || x >= pic.width() || y < 0 || y >= pic.height()) {
            throw new IllegalArgumentException("x:" + x + " or y:" + y
                    + " must be within the bounds of the picture width:" + pic.width() + " height:" + pic.height());
        }

        // Already calculated energy
        if (en[x][y] < Double.POSITIVE_INFINITY) {
            return en[x][y];
        }

        // Calculate energy
        if (pic.height() == 1 && pic.width() == 1 ||
                x == 0 && y == 0 ||
                x == pic.width() - 1 && y == 0 ||
                x == 0 && y == pic.height() - 1 ||
                x == pic.width() - 1 && y == pic.height() - 1) {
            en[x][y] = pic.getRGB(x, y);
        } else if (x == 0 || x == pic.width() - 1) {
            en[x][y] = Math.sqrt(getDelta(pic.getRGB(x, y - 1), pic.getRGB(x, y + 1)));
        } else if (y == 0 || y == pic.height() - 1) {
            en[x][y] = Math.sqrt(getDelta(pic.getRGB(x - 1, y), pic.getRGB(x + 1, y)));
        } else {
            en[x][y] = Math.sqrt(getDelta(pic.getRGB(x - 1, y), pic.getRGB(x + 1, y)) +
                    getDelta(pic.getRGB(x, y - 1), pic.getRGB(x, y + 1)));
        }

        return en[x][y];
    }

    private double getDelta(int lowerRGB, int upperRGB) {
        int lowerRed = (lowerRGB >> 16) & COLOR_MASK;
        int lowerGreen = (lowerRGB >> 8) & COLOR_MASK;
        int lowerBlue = (lowerRGB >> 0) & COLOR_MASK;

        int upperRed = (upperRGB >> 16) & COLOR_MASK;
        int upperGreen = (upperRGB >> 8) & COLOR_MASK;
        int upperBlue = (upperRGB >> 0) & COLOR_MASK;

        double deltaXRed = Math.pow(lowerRed - upperRed, 2);
        double deltaXGreen = Math.pow(lowerGreen - upperGreen, 2);
        double deltaXBlue = Math.pow(lowerBlue - upperBlue, 2);

        return deltaXRed + deltaXGreen + deltaXBlue;
    }

    /**
     * Sequence of indices for horizontal seam.
     *
     * @return sequence of indices for horizontal seam.
     */
    public int[] findHorizontalSeam() {
        if (horizonalPath != null) {
            return copyPathTo(horizonalPath);
        }
        horizonalPath = findSeam(this.transposedPicture, this.horizontalShortestPaths, this.transposedEnergy);
        return copyPathTo(horizonalPath);
    }

    /**
     * Sequence of indices for vertical seam.
     *
     * @return sequence of indices for vertical seam.
     */
    public int[] findVerticalSeam() {
        if (verticalPath != null) {
            return copyPathTo(verticalPath);
        }
        verticalPath = findSeam(this.picture, this.verticalShortestPaths, this.energy);
        return copyPathTo(verticalPath);
    }

    private int[] findSeam(Picture pic, ShortestPath[][] shortestPaths, double[][] en) {
        ShortestPath shortestPath = null;
        for (int row = 0; row < pic.height(); row++) {
            for (int col = 0; col < pic.width(); col++) {
                if (row == 0) {
                    int[] pathTo = new int[pic.height()];
                    pathTo[0] = col;
                    shortestPaths[col][row] = new ShortestPath(row, col, pathTo, energy(col, row),
                            energy(col, row, en, pic));
                    continue;
                }

                ShortestPath currentShortestPath;
                if (col == 0) { // first column
                    currentShortestPath = getShortestPath(shortestPaths[col][row - 1],
                            new ShortestPath(row, col, energy(col, row, en, pic)), en, pic);
                    currentShortestPath = getShortestPath(shortestPaths[col + 1][row - 1], currentShortestPath, en,
                            pic);
                } else if (col == pic.width() - 1) { // last column
                    currentShortestPath = getShortestPath(shortestPaths[col - 1][row - 1],
                            new ShortestPath(row, col, energy(col, row, en, pic)), en, pic);
                    currentShortestPath = getShortestPath(shortestPaths[col][row - 1], currentShortestPath, en, pic);
                } else {
                    currentShortestPath = getShortestPath(shortestPaths[col - 1][row - 1],
                            new ShortestPath(row, col, energy(col, row, en, pic)), en, pic);
                    currentShortestPath = getShortestPath(shortestPaths[col][row - 1], currentShortestPath, en, pic);
                    currentShortestPath = getShortestPath(shortestPaths[col + 1][row - 1], currentShortestPath, en,
                            pic);
                }
                shortestPaths[col][row] = currentShortestPath;

                if (row == pic.height() - 1) {
                    if (col == 0) {
                        shortestPath = shortestPaths[col][row];
                        shortestPath.setPathToIndex(row, shortestPath.getPathTo()[row - 1]);
                    } else {
                        if (shortestPaths[col][row].distanceTo < shortestPath.distanceTo) {
                            shortestPath = shortestPaths[col][row];
                            shortestPath.setPathToIndex(row, shortestPath.getPathTo()[row - 1]);
                        }
                    }
                }
            }
        }

        return shortestPath.getPathTo();
    }

    private ShortestPath getShortestPath(ShortestPath previousShortestPath, ShortestPath currentShortestPath,
            double[][] en, Picture pic) {
        assert previousShortestPath != null : "Previous shortest path is null row:" + currentShortestPath.getRow()
                + " col:" + currentShortestPath.getCol();

        double distanceTo = previousShortestPath.getDistanceTo() + previousShortestPath.getEnergy();
        if (distanceTo >= currentShortestPath.getDistanceTo()) {
            return currentShortestPath;
        }

        int[] pathTo = copyPathTo(previousShortestPath.getPathTo());
        pathTo[currentShortestPath.getRow() - 1] = previousShortestPath.getCol();

        return new ShortestPath(currentShortestPath.getRow(), currentShortestPath.getCol(), pathTo, distanceTo,
                energy(currentShortestPath.getCol(), currentShortestPath.getRow(), en, pic));
    }

    private int[] copyPathTo(int[] sourcePathTo) {
        int[] pathTo = new int[sourcePathTo.length];
        for (int i = 0; i < pathTo.length; i++) {
            pathTo[i] = sourcePathTo[i];
        }
        return pathTo;
    }

    /**
     * Remove horizontal seam from the current picture.
     *
     * @param seam to remove from the picture.
     * @exception IllegalArgumentException if seam is null or seam is not equal to
     *                                     the picture width.
     */
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("seam cannot be null.");
        }
        if (seam.length != this.picture.width()) {
            throw new IllegalArgumentException("seam length must be equal to the width");
        }

        int width = this.picture.width();
        int newHeight = this.picture.height() - 1;
        Picture horizontalPicture = new Picture(width, newHeight);
        for (int row = 0; row < newHeight; row++) {
            for (int col = 0; col < width; col++) {
                if (seam[col] == row) {
                    continue; // skip the seam
                }
                horizontalPicture.setRGB(col, row, this.picture.getRGB(col, row));
            }
        }
        initialize(horizontalPicture);
    }

    /**
     * Remove vertical seam from current picture.
     *
     * @param seam to remove from the picture.
     * @exception IllegalArgumentException if seam is null or seam is not equal to
     *                                     the picture height.
     */
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("seam cannot be null.");
        }
        if (seam.length != this.picture.height()) {
            throw new IllegalArgumentException("seam length must be equal to the height.");
        }

        int newWidth = this.picture.width() - 1;
        int height = this.picture.height();
        Picture verticalPicture = new Picture(newWidth, height);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < newWidth; col++) {
                if (seam[row] == col) {
                    continue; // skip the seam
                }
                verticalPicture.setRGB(col, row, this.picture.getRGB(col, row));
            }
        }
        initialize(verticalPicture);
    }

    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        StdOut.printf("image is %d pixels wide by %d pixels high.\n", picture.width(), picture.height());

        SeamCarver sc = new SeamCarver(picture);

        StdOut.printf("Printing energy calculated for each pixel.\n");

        for (int row = 0; row < sc.height(); row++) {
            for (int col = 0; col < sc.width(); col++)
                StdOut.printf("%9.2f ", sc.energy(col, row));
            StdOut.println();
        }

        StdOut.println();
        int[] verticalSeam = sc.findVerticalSeam();
        StdOut.print("Vertical Seam: ");
        for (int i = 0; i < verticalSeam.length; i++) {
            StdOut.print(verticalSeam[i]);
            if (i + 1 < verticalSeam.length) {
                StdOut.print(",");
            }
        }

        StdOut.println();
        int[] horizontalSeam = sc.findHorizontalSeam();
        StdOut.print("Horizontal Seam: ");
        for (int i = 0; i < horizontalSeam.length; i++) {
            StdOut.print(horizontalSeam[i]);
            if (i + 1 < horizontalSeam.length) {
                StdOut.print(",");
            }
        }
    }

    private class ShortestPath {
        private final int row;
        private final int col;
        private final double energy;
        private int[] pathTo;
        private final double distanceTo;

        /**
         * The shortest path to this pixel.
         *
         * @param row    the row of this pixel.
         * @param col    the col of this pixel.
         * @param energy this pixel's energy.
         */
        public ShortestPath(int row, int col, double energy) {
            this.row = row;
            this.col = col;
            this.distanceTo = Double.POSITIVE_INFINITY;
            this.energy = energy;
        }

        /**
         * The shortest path to this pixel.
         *
         * @param row        the row of this pixel.
         * @param col        the col of this pixel.
         * @param pathTo     the shortest path to this pixel.
         * @param distanceTo the shortest path's distance to this pixel.
         * @param energy     this pixel's energy.
         */
        public ShortestPath(int row, int col, int[] pathTo, double distanceTo, double energy) {
            this.row = row;
            this.col = col;
            this.pathTo = copyPathTo(pathTo);
            this.distanceTo = distanceTo;
            this.energy = energy;
        }

        /**
         * The row of this pixel.
         *
         * @return the row of this pixel.
         */
        public int getRow() {
            return this.row;
        }

        /**
         * The column of this pixel.
         *
         * @return the column of this pixel.
         */
        public int getCol() {
            return this.col;
        }

        /**
         * The shortest path to this pixel.
         *
         * @return the shortest path to this pixel.
         */
        public int[] getPathTo() {
            return copyPathTo(this.pathTo);
        }

        /**
         * The shortest path's distance to this pixel.
         *
         * @return the shortest path's distance to this pixel.
         */
        public double getDistanceTo() {
            return this.distanceTo;
        }

        /**
         * This pixel's energy.
         *
         * @return this pixel's energy.
         */
        public double getEnergy() {
            return this.energy;
        }

        /**
         * Set the index of pathTo to the given value.
         *
         * @param index The index to set.
         * @param value The value to set.
         */
        public void setPathToIndex(int index, int value) {
            this.pathTo[index] = value;
        }
    }
}