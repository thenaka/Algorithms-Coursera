import java.util.Arrays;
import java.util.Comparator;

public class FastCollinearPoints {
    private int count = 0;
    private LineSegment[] lineSegments;

    /**
     * Finds all of the four point line segments for the given points.
     * 
     * @param points Points to find the four point segments.
     * @throws IllegalArgumentException when points is null, any point is null, or
     *                                  any two points are equal.
     */
    public FastCollinearPoints(Point[] points) {
        BruteCollinearPoints.validateAndSortPoints(points);
        lineSegments = new LineSegment[points.length * points.length];
        findLineSegments(points);
    }

    private void findLineSegments(Point[] points) {
        for (int i = 0; i < points.length; i++) {
            Point p = points[i];
            Comparator<Point> pointSlopeTo = p.slopeOrder();

            Point[] copy = copy(points);
            Arrays.sort(copy, pointSlopeTo);

            for (int q = 1; q < copy.length - 3; q++) {
                int r = q + 1;
                int s = q + 2;
                if (pointSlopeTo.compare(copy[q], copy[r]) == 0 && pointSlopeTo.compare(copy[r], copy[s]) == 0) {
                    if (count + 1 == lineSegments.length) {
                        BruteCollinearPoints.resize(lineSegments, count, lineSegments.length * 2);
                    }
                    StdOut.println(p + " " + copy[q] + " " + copy[r] + " " + copy[s]);
                    lineSegments[count++] = new LineSegment(p, copy[s]);
                }
            }
        }
    }

    private Point[] copy(Point[] source) {
        Point[] copy = new Point[source.length];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i];
        }
        return copy;
    }

    /**
     * Returns a count of all of the four point line segments.
     * 
     * @return count of all of the four point line segments.
     */
    public int numberOfSegments() {
        return count;
    }

    /**
     * Returns all of the four point line segments.
     * 
     * @return each line segment containing 4 points exactly once. If 4 points
     *         appear on a line segment in the order p→q→r→s, then either the
     *         line segment p→s or s→p is returned (but not both) and not the
     *         subsegments such as p→r or q→r
     */
    public LineSegment[] segments() {
        return lineSegments;
    }

        /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {
        // Base case four collinear points
        StdOut.println("Base case");
        FastCollinearPoints fast = new FastCollinearPoints(BruteCollinearPoints.getCollinearPoints(4));
        assert fast.count == 1;
        assert fast.lineSegments[0].toString().equals("(0, 0) -> (3, 3)");

        // Add one more collinear point
        StdOut.println("Five collinear case");
        fast = new FastCollinearPoints(BruteCollinearPoints.getCollinearPoints(5));
        assert fast.count == 5;

        StdOut.println("20 Random Points with max 20");
        fast = new FastCollinearPoints(BruteCollinearPoints.getRandomPoints(20, 20));

        StdOut.println("40 Random Points with max 20");
        fast = new FastCollinearPoints(BruteCollinearPoints.getRandomPoints(40, 20));

        StdOut.println("80 Random Points with max 20");
        fast = new FastCollinearPoints(BruteCollinearPoints.getRandomPoints(80, 20));
    }
}