import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private int colorMask = 0xFF;

    private Picture picture;
    private Picture transposedPicture;
    private double[][] energy;
    private double[][] transposedEnergy;
    private ShortestPath[][] verticalShortestPaths;
    private ShortestPath[][] horizontalShortestPaths;

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

        this.picture = new Picture(picture);
        this.transposedPicture = new Picture(this.picture.height(), this.picture.width());

        this.energy = new double[this.picture.width()][this.picture.height()];
        this.transposedEnergy = new double[this.picture.height()][this.picture.width()];

        this.verticalShortestPaths = new ShortestPath[this.picture.width()][this.picture.height()];
        this.horizontalShortestPaths = new ShortestPath[this.picture.height()][this.picture.width()];
        initialize();
    }

    private void initialize() {
        for (int row = 0; row < this.picture.height(); row++) {
            for (int col = 0; col < this.picture.width(); col++) {
                if (col == 0 || col == this.picture.width() - 1 || row == 0 || row == this.picture.height() - 1) {
                    this.energy[col][row] = 1000;
                    this.transposedEnergy[row][col] = 1000;
                } else {
                    this.energy[col][row] = Double.MAX_VALUE;
                    this.transposedEnergy[row][col] = Double.MAX_VALUE;
                }
                this.transposedPicture.setRGB(row, col, this.picture.getRGB(col, row));
            }
        }
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
        if (x < 0 || x >= this.picture.width() || y < 0 || y >= this.picture.height()) {
            throw new IllegalArgumentException("x or y must be within the bounds of the picture.");
        }

        // Already calculated energy
        if (energy[x][y] < Double.MAX_VALUE) {
            return energy[x][y];
        }

        // Calculate energy
        energy[x][y] = Math.sqrt(getDelta(this.picture.getRGB(x - 1, y), this.picture.getRGB(x + 1, y)) +
                getDelta(this.picture.getRGB(x, y - 1), this.picture.getRGB(x, y + 1)));
        return energy[x][y];
    }

    private double getDelta(int lowerRGB, int upperRGB) {
        int lowerRed = (lowerRGB >> 16) & colorMask;
        int lowerGreen = (lowerRGB >> 8) & colorMask;
        int lowerBlue = (lowerRGB >> 0) & colorMask;

        int upperRed = (upperRGB >> 16) & colorMask;
        int upperGreen = (upperRGB >> 8) & colorMask;
        int upperBlue = (upperRGB >> 0) & colorMask;

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
        return findSeam(this.transposedPicture, this.horizontalShortestPaths);
    }

    /**
     * Sequence of indices for vertical seam.
     *
     * @return sequence of indices for vertical seam.
     */
    public int[] findVerticalSeam() {
        return findSeam(this.picture, this.verticalShortestPaths);
    }

    private int[] findSeam(Picture pic, ShortestPath[][] shortestPaths) {
        ShortestPath shortestPath = null;
        for (int row = 1; row < pic.height(); row++) {
            for (int col = 1; col < pic.width() - 1; col++) {
                if (row == 1) {
                    int[] pathTo = new int[pic.height()];
                    pathTo[0] = col - 1;
                    shortestPaths[col][row] = new ShortestPath(row, col, pathTo, 0);
                    continue;
                }

                ShortestPath currentShortestPath;
                if (col == 1) {
                    currentShortestPath = getShortestPath(shortestPaths[col][row - 1], new ShortestPath(row, col));
                    currentShortestPath = getShortestPath(shortestPaths[col + 1][row - 1], currentShortestPath);
                } else if (col == pic.width() - 2) {
                    currentShortestPath = getShortestPath(shortestPaths[col - 1][row - 1], new ShortestPath(row, col));
                    currentShortestPath = getShortestPath(shortestPaths[col][row - 1], currentShortestPath);
                } else {
                    currentShortestPath = getShortestPath(shortestPaths[col - 1][row - 1], new ShortestPath(row, col));
                    currentShortestPath = getShortestPath(shortestPaths[col][row - 1], currentShortestPath);
                    currentShortestPath = getShortestPath(shortestPaths[col + 1][row - 1], currentShortestPath);
                }
                shortestPaths[col][row] = currentShortestPath;

                if (row == pic.height() - 1) {
                    if (col == 1) {
                        shortestPath = shortestPaths[col][row];
                    } else {
                        if (shortestPaths[col][row].distanceTo < shortestPath.distanceTo) {
                            shortestPath = shortestPaths[col][row];
                            shortestPath.getPathTo()[row] = col;
                        }
                    }
                }
            }
        }

        return shortestPath.getPathTo();
    }

    private ShortestPath getShortestPath(ShortestPath previousShortestPath, ShortestPath currentShortestPath) {
        assert previousShortestPath != null : "Previous shortest path is null row:" + currentShortestPath.getRow()
                + " col:" + currentShortestPath.getCol();

        double distanceTo = previousShortestPath.getDistanceTo() + previousShortestPath.getEnergy();
        if (distanceTo >= currentShortestPath.getDistanceTo()) {
            return currentShortestPath;
        }

        int[] pathTo = copyPathTo(previousShortestPath.getPathTo());
        pathTo[currentShortestPath.getRow() - 1] = previousShortestPath.getCol();

        return new ShortestPath(currentShortestPath.getRow(), currentShortestPath.getCol(), pathTo, distanceTo);
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
        for (int i = 0; i < verticalSeam.length; i++) {
            StdOut.print(verticalSeam[i]);
            StdOut.print(",");
        }

        StdOut.println();
        int[] horizontalSeam = sc.findHorizontalSeam();
        for (int i = 0; i < horizontalSeam.length; i++) {
            StdOut.print(horizontalSeam[i]);
            StdOut.print(",");
        }
    }

    private class ShortestPath {
        private int row;
        private int col;
        private int[] pathTo;
        private double distanceTo;

        /**
         * The shortest path to this pixel.
         *
         * @param row the row of this pixel.
         * @param col the col of this pixel.
         */
        public ShortestPath(int row, int col) {
            this.row = row;
            this.col = col;
            this.distanceTo = Double.MAX_VALUE;
        }

        /**
         * The shortest path to this pixel.
         *
         * @param row        the row of this pixel.
         * @param col        the col of this pixel.
         * @param pathTo     the shortest path to this pixel.
         * @param distanceTo the shortest path's distance to this pixel.
         */
        public ShortestPath(int row, int col, int[] pathTo, double distanceTo) {
            this.row = row;
            this.col = col;
            this.pathTo = pathTo;
            this.distanceTo = distanceTo;
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
            return this.pathTo;
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
            return energy(this.col, this.row);
        }
    }
}