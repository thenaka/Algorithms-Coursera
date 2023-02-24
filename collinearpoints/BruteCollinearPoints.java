import java.util.Arrays;
import java.util.Comparator;

public class BruteCollinearPoints {
    private int count = 0;
    private LineSegment[] lineSegments;

    /**
     * Finds all of the four point line segments for the given points.
     * 
     * @param points Points to find the four point segments.
     * @throws IllegalArgumentException when points is null, any point is null, or
     *                                  any two points are equal.
     */
    public BruteCollinearPoints(Point[] points) {
        validateAndSortPoints(points);
        lineSegments = new LineSegment[points.length * points.length];
        findLineSegments(points);
    }

    private void validateAndSortPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException("Points must not be null.");
        }
        for (Point p : points) {
            if (p == null) {
                throw new IllegalArgumentException("No point may be null.");
            }
        }

        Arrays.sort(points);
        for (int i = 0; i < points.length; i++) {
            if ((i + 1) == points.length) {
                break; // no more points to compare
            }
            if (points[i].compareTo(points[i + 1]) == 0) {
                throw new IllegalArgumentException("No two points may be the same.");
            }
        }
    }

    private void findLineSegments(Point[] points) {
        int N = points.length;
        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                for (int k = j + 1; k < N; k++) {
                    for (int l = k + 1; l < N; l++) {
                        Comparator<Point> comparator = points[i].slopeOrder();
                        if (comparator.compare(points[j], points[k]) == 0
                                && comparator.compare(points[k], points[l]) == 0) {
                            if (count + 1 == lineSegments.length) {
                                resizeLineSegments(lineSegments.length * 2);
                            }
                            StdOut.println(points[i] + " " + points[j] + " " + points[k] + " " + points[l]);
                            lineSegments[count++] = new LineSegment(points[i], points[l]);
                        }
                    }
                }
            }
        }
    }

    private void resizeLineSegments(int newCapacity) {
        LineSegment[] temp = new LineSegment[newCapacity];
        for (int i = 0; i < count; i++) {
            temp[i] = lineSegments[i];
        }
        lineSegments = temp;
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
        BruteCollinearPoints brute = new BruteCollinearPoints(getCollinearPoints(4));
        assert brute.count == 1;
        assert brute.lineSegments[0].toString().equals("(0, 0) -> (3, 3)");

        // Add one more collinear point
        StdOut.println("Five collinear case");
        brute = new BruteCollinearPoints(getCollinearPoints(5));
        assert brute.count == 5;

        StdOut.println("20 Random Points with max 20");
        brute = new BruteCollinearPoints(getRandomPoints(20, 20));

        StdOut.println("40 Random Points with max 20");
        brute = new BruteCollinearPoints(getRandomPoints(40, 20));
    }

    private static Point[] getCollinearPoints(int count) {
        Point[] points = new Point[count];
        for (int i = 0; i < count; i++) {
            points[i] = new Point(i, i);
        }
        return points;
    }

    private static Point[] getRandomPoints(int count, int max) {
        Point[] points = new Point[count];
        for (int i = 0; i < count; i++) {
            // create candidate point, see if point is already in points
            Point candidate = new Point(StdRandom.uniformInt(max), StdRandom.uniformInt(max));
            boolean contains = false;
            for (int j = 0; j < i; j++) {
                if (points[j].compareTo(candidate) == 0) {
                    contains = true;
                    break;
                }
            }

            if (!contains) {
                points[i] = candidate;
            } else {
                i--; // reduce iteration to ensure we fill up the array
            }
        }
        return points;
    }
}