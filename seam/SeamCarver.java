import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private int colorMask = 0xFF;

    private Picture picture;
    private double[][] energy;
    private ShortestPath[][] shortestPath;

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
        this.shortestPath = new ShortestPath[this.picture.width()][this.picture.height()];

        initialize();
    }

    private void initialize() {
        for (int row = 0; row < this.picture.height(); row++) {
            for (int col = 0; col < this.picture.width(); col++) {
                this.shortestPath[col][row] = new ShortestPath();

                if (col == 0 || col == this.picture.width() - 1 || row == 0 || row == this.picture.height() - 1) {
                    this.energy[col][row] = 1000;
                    continue;
                }
                this.energy[col][row] = Double.MAX_VALUE;
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

        for (int row = 1; row < this.picture.height(); row++) {
            for (int col = 1; col < this.picture.width(); col++) {

            }
        }
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
        private int[] pathTo;
        private double distanceTo;
        private Orientation orientation;

        /**
         * The shortest path to this pixel.
         * 
         */
        public ShortestPath() {
            this.distanceTo = Double.MAX_VALUE;
        }

        /**
         * The shortest path to this pixel.
         * 
         * @param pathTo      the shortest path to this pixel.
         * @param distanceTo  the shortest path's distance to this pixel.
         * @param orientation the orientation of this path.
         */
        public ShortestPath(int[] pathTo, double distanceTo, Orientation orientation) {
            this.pathTo = pathTo;
            this.distanceTo = distanceTo;
            this.orientation = orientation;
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
    }
}