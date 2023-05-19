import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private int colorMask = 0xFF;

    private Picture picture;
    private double[][] energy;
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

        this.energy = new double[this.picture.width()][this.picture.height()];
        this.verticalShortestPaths = new ShortestPath[this.picture.width()][this.picture.height()];
        this.horizontalShortestPaths = new ShortestPath[this.picture.width()][this.picture.height()];
        initialize();
    }

    private void initialize() {
        for (int row = 0; row < this.picture.height(); row++) {
            for (int col = 0; col < this.picture.width(); col++) {
                if (energy[col][row] == 0 &&
                        col == 0 || col == this.picture.width() - 1 ||
                        row == 0 || row == this.picture.height() - 1) {
                    this.energy[col][row] = 1000;
                }
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

        // Energy is 1000 for the border pixels
        if (x == 0 || x == this.picture.width() - 1 || y == 0 || y == this.picture.height() - 1) {
            return 1000;
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

    // // sequence of indices for horizontal seam
    // public int[] findHorizontalSeam()

    /**
     * Sequence of indices for vertical seam.
     * 
     * @return sequence of indices for vertical seam.
     */
    public int[] findVerticalSeam() {
        for (int row = 1; row < this.picture.height() - 1; row++) {
            for (int col = 1; col < this.picture.width() - 1; col++) {
                if (row == 1) {
                    int[] pathTo = new int[this.picture.height()];
                    pathTo[0] = col;
                    pathTo[row] = col;
                    this.verticalShortestPaths[col][row] = new ShortestPath(row, col, pathTo, 0, Orientation.VERTICAL);
                    continue;
                }

                ShortestPath currentShortestPath = getShortestPath(this.verticalShortestPaths[col - 1][row - 1],
                        new ShortestPath(row, col, Orientation.VERTICAL));
                currentShortestPath = getShortestPath(this.verticalShortestPaths[col][row - 1], currentShortestPath);
                currentShortestPath = getShortestPath(this.verticalShortestPaths[col + 1][row - 1],
                        currentShortestPath);
            }
        }

        ShortestPath shortestPath = new ShortestPath(0, 0, Orientation.VERTICAL);
        int penultimateRow = this.picture.height() - 2;
        for (int col = 1; col < this.picture.width() - 1; col++) {
            ShortestPath currentShortestPath = this.verticalShortestPaths[col][penultimateRow];
            if (currentShortestPath.distanceTo < shortestPath.distanceTo) {
                shortestPath = currentShortestPath;
            }
        }
        shortestPath.pathTo[this.picture.height() - 1] = shortestPath.getCol();
        return shortestPath.getPathTo();
    }

    private ShortestPath getShortestPath(ShortestPath previousShortestPath, ShortestPath currentShortestPath) {
        double distanceTo = previousShortestPath.getDistanceTo() + previousShortestPath.getEnergy();
        if (distanceTo < currentShortestPath.getDistanceTo()) {
            int[] pathTo = copyPathTo(previousShortestPath.getPathTo());
            pathTo[currentShortestPath.getCol()] = currentShortestPath.getCol();

            currentShortestPath = new ShortestPath(currentShortestPath.getRow(), currentShortestPath.getCol(), pathTo,
                    distanceTo, currentShortestPath.getOrientation());
        }
        return currentShortestPath;
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

    // // unit testing (optional)
    // public static void main(String[] args)

    private enum Orientation {
        VERTICAL,
        HORIZONTAL
    }

    private class ShortestPath {
        private int row;
        private int col;
        private int[] pathTo;
        private double distanceTo;
        private Orientation orientation;

        /**
         * The shortest path to this pixel.
         * 
         * @param row         the row of this pixel.
         * @param col         the col of this pixel.
         * @param orientation the orientation of this path.
         */
        public ShortestPath(int row, int col, Orientation orientation) {
            this.row = row;
            this.col = col;
            this.orientation = orientation;
            this.distanceTo = Double.MAX_VALUE;
        }

        /**
         * The shortest path to this pixel.
         * 
         * @param row         the row of this pixel.
         * @param col         the col of this pixel.
         * @param pathTo      the shortest path to this pixel.
         * @param distanceTo  the shortest path's distance to this pixel.
         * @param orientation the orientation of this path.
         */
        public ShortestPath(int row, int col, int[] pathTo, double distanceTo, Orientation orientation) {
            this.row = row;
            this.col = col;
            this.pathTo = pathTo;
            this.distanceTo = distanceTo;
            this.orientation = orientation;
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
            return energy(col, row);
        }

        /**
         * This path's orientation.
         * 
         * @return this path's orientation.
         */
        public Orientation getOrientation() {
            return this.orientation;
        }
    }
}