import java.util.Arrays;
import java.util.Comparator;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

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
        if (points == null) {
            throw new IllegalArgumentException("Points must not be null.");
        }
        Point[] copy = copy(points);

        validateAndSortPoints(copy);
        lineSegments = new LineSegment[copy.length * copy.length];
        findLineSegments(copy);
    }

    private Point[] copy(Point[] source) {
        Point[] copy = new Point[source.length];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i];
        }
        return copy;
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

    private void findLineSegments(Point[] points) {
        int length = points.length;
        for (int i = 0; i < length; i++) {
            for (int j = i + 1; j < length; j++) {
                for (int k = j + 1; k < length; k++) {
                    for (int m = k + 1; m < length; m++) {
                        Comparator<Point> comparator = points[i].slopeOrder();
                        if (comparator.compare(points[j], points[k]) == 0
                                && comparator.compare(points[k], points[m]) == 0) {
                            if (count + 1 == lineSegments.length) {
                                lineSegments = resize(lineSegments, count, lineSegments.length * 2);
                            }
                            // StdOut.println(points[i] + " " + points[j] + " " + points[k] + " " +
                            // points[m]);
                            lineSegments[count++] = new LineSegment(points[i], points[m]);
                        }
                    }
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
        BruteCollinearPoints brute = new BruteCollinearPoints(getCollinearPoints(4));
        assert brute.numberOfSegments() == 1;
        assert brute.segments()[0].toString().equals("(0, 0) -> (3, 3)");

        // Add one more collinear point
        StdOut.println("Five collinear case");
        brute = new BruteCollinearPoints(getCollinearPoints(5));
        assert brute.numberOfSegments() == 5;

        StdOut.println("20 Random Points with max 20");
        brute = new BruteCollinearPoints(getRandomPoints(20, 20));
        assert brute.numberOfSegments() >= 0;

        StdOut.println("40 Random Points with max 20");
        brute = new BruteCollinearPoints(getRandomPoints(40, 20));
        assert brute.numberOfSegments() >= 0;

        StdOut.println("80 Random Points with max 20");
        brute = new BruteCollinearPoints(getRandomPoints(80, 20));
        assert brute.numberOfSegments() >= 0;
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