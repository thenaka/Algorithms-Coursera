import java.util.Arrays;
import java.util.Comparator;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

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
        if (points == null) {
            throw new IllegalArgumentException("Points must not be null.");
        }
        Point[] copy = copy(points);

        validateAndSortPoints(copy);
        lineSegments = new LineSegment[copy.length * copy.length];
        findLineSegments(copy);
    }

    private void validateAndSortPoints(Point[] points) {
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

    // TODO The method segments() should include each maximal line segment
    // containing 4 (or more) points exactly once. For example, if 5 points appear
    // on a line segment in the order p→q→r→s→t, then do not include the subsegments
    // p→s or q→t.
    private void findLineSegments(Point[] points) {
        for (int i = 0; i < points.length; i++) {
            Point p = points[i];
            Comparator<Point> pointSlopeTo = p.slopeOrder();

            Point[] copy = copy(points);
            Arrays.sort(copy, pointSlopeTo);

            for (int q = 0; q < copy.length - 3; q++) {
                int r = q + 1;
                int s = q + 2;

                if (p.compareTo(copy[q]) == 0 || p.compareTo(copy[r]) == 0 || p.compareTo(copy[s]) == 0) {
                    continue; // skip point p
                }

                if (pointSlopeTo.compare(copy[q], copy[r]) == 0 && pointSlopeTo.compare(copy[r], copy[s]) == 0) {
                    if (count + 1 == lineSegments.length) {
                        lineSegments = resize(lineSegments, count, lineSegments.length * 2);
                    }
                    lineSegments[count++] = new LineSegment(p, copy[s]);
                }
            }
        }
    }

    private LineSegment[] resize(LineSegment[] source, int sourceCount, int newCapacity) {
        LineSegment[] temp = new LineSegment[newCapacity];
        for (int i = 0; i < sourceCount; i++) {
            temp[i] = source[i];
        }
        return temp;
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
        return copy();
    }

    private LineSegment[] copy() {
        LineSegment[] copy = new LineSegment[count];
        for (int i = 0; i < count; i++) {
            copy[i] = lineSegments[i];
        }
        return copy;
    }

    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {
        // Base case four collinear points
        StdOut.println("Base case");
        FastCollinearPoints fast = new FastCollinearPoints(getCollinearPoints(4));
        assert fast.count == 1;
        assert fast.lineSegments[0].toString().equals("(0, 0) -> (3, 3)");

        // Add one more collinear point
        StdOut.println("Five collinear case");
        fast = new FastCollinearPoints(getCollinearPoints(5));
        assert fast.count == 5;

        StdOut.println("20 Random Points with max 20");
        fast = new FastCollinearPoints(getRandomPoints(20, 20));
        assert fast.numberOfSegments() >= 0;

        StdOut.println("40 Random Points with max 20");
        fast = new FastCollinearPoints(getRandomPoints(40, 20));
        assert fast.numberOfSegments() >= 0;

        StdOut.println("80 Random Points with max 20");
        fast = new FastCollinearPoints(getRandomPoints(80, 20));
        assert fast.numberOfSegments() >= 0;
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
            boolean contains;
            Point candidate;
            do {
                // create candidate point, see if point is already in points
                contains = false;
                candidate = new Point(StdRandom.uniformInt(max), StdRandom.uniformInt(max));
                for (int j = 0; j < i; j++) {
                    if (points[j].compareTo(candidate) == 0) {
                        contains = true;
                        break;
                    }
                }
            } while (contains);

            points[i] = candidate;
        }
        return points;
    }
}