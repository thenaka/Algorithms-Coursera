
/******************************************************************************
 *  Compilation:  javac Point.java
 *  Execution:    java Point
 *  Dependencies: none
 *  
 *  An immutable data type for points in the plane.
 *  For use on Coursera, Algorithms Part I programming assignment.
 *
 ******************************************************************************/

import java.util.Arrays;
import java.util.Comparator;
import edu.princeton.cs.algs4.StdDraw;

public class Point implements Comparable<Point> {

    private final int x; // x-coordinate of this point
    private final int y; // y-coordinate of this point

    /**
     * Initializes a new point.
     *
     * @param x the <em>x</em>-coordinate of the point
     * @param y the <em>y</em>-coordinate of the point
     */
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    /**
     * Draws this point to standard draw.
     */
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    /**
     * Draws the line segment between this point and the specified point
     * to standard draw.
     *
     * @param that the other point
     */
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Returns the slope between this point and the specified point.
     * Formally, if the two points are (x0, y0) and (x1, y1), then the slope
     * is (y1 - y0) / (x1 - x0). For completeness, the slope is defined to be
     * +0.0 if the line segment connecting the two points is horizontal;
     * Double.POSITIVE_INFINITY if the line segment is vertical;
     * and Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1) are equal.
     *
     * @param that the other point
     * @return the slope between this point and the specified point
     */
    public double slopeTo(Point that) {
        if (that == null) {
            throw new IllegalArgumentException("Point to find slope must not be null.");
        }

        double yDelta = that.y - this.y;
        double xDelta = that.x - this.x;

        if (yDelta == 0 && xDelta == 0) {
            // the same point
            return Double.NEGATIVE_INFINITY;
        }
        if (yDelta == 0) {
            // horizontal line
            return 0.0;
        }
        if (xDelta == 0) {
            // vertical line
            return Double.POSITIVE_INFINITY;
        }

        return yDelta / xDelta;
    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate.
     * Formally, the invoking point (x0, y0) is less than the argument point
     * (x1, y1) if and only if either y0 < y1 or if y0 = y1 and x0 < x1.
     *
     * @param that the other point
     * @return the value <tt>0</tt> if this point is equal to the argument
     *         point (x0 = x1 and y0 = y1);
     *         a negative integer if this point is less than the argument
     *         point; and a positive integer if this point is greater than the
     *         argument point
     */
    public int compareTo(Point that) {
        if (that == null) {
            throw new IllegalArgumentException("Point to compare to must not be null.");
        }
        if (this.y < that.y) {
            return -1;
        }
        if (this.y == that.y) {
            if (this.x < that.x) {
                return -1;
            }
            if (this.x == that.x) {
                return 0;
            }
        }
        return 1;
    }

    /**
     * Compares two points by the slope they make with this point.
     * The slope is defined as in the slopeTo() method.
     *
     * @return the Comparator that defines this ordering on points
     */
    public Comparator<Point> slopeOrder() {
        return new BySlope();
    }

    private class BySlope implements Comparator<Point> {
        public int compare(Point a, Point b) {
            double slopeA = slopeTo(a);
            double slopeB = slopeTo(b);
            return Double.compare(slopeA, slopeB);
        }
    }

    /**
     * Returns a string representation of this point.
     * This method is provide for debugging;
     * your program should not rely on the format of the string representation.
     *
     * @return a string representation of this point
     */
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {
        // point assertion
        Point a = new Point(1, 1);
        assert a.x == 1;
        assert a.y == 1;

        // slopeTo assertions
        assert a.slopeTo(a) == Double.NEGATIVE_INFINITY : "Slope to the same point should be negative infinity.";

        Point b = new Point(1, 2);
        assert a.slopeTo(b) == Double.POSITIVE_INFINITY : "Vertical slope should be positive infinity.";

        Point c = new Point(2, 1);
        assert a.slopeTo(c) == 0.0 : "Horizontal slope should be zero.";

        Point d = new Point(2, 2);
        assert a.slopeTo(d) == 1;

        Point e = new Point(2, 3);
        assert a.slopeTo(e) == 2;

        Point f = new Point(3, 2);
        assert a.slopeTo(f) == 0.5;

        // compareTo assertions
        assert a.compareTo(a) == 0;
        assert a.compareTo(b) == -1;
        assert a.compareTo(c) == -1;
        assert b.compareTo(a) == 1;
        assert c.compareTo(a) == 1;

        // comparator assertions
        Comparator<Point> comparator = a.slopeOrder();
        assert comparator.compare(b, c) == 1;
        assert comparator.compare(d, e) == -1;
        assert comparator.compare(a, b) == -1;
        assert comparator.compare(e, f) == 1;
        assert comparator.compare(a, a) == 0;
        assert comparator.compare(b, b) == 0;
        assert comparator.compare(c, c) == 0;
        assert comparator.compare(d, d) == 0;
        assert comparator.compare(e, e) == 0;
        assert comparator.compare(f, f) == 0;

        // slopeOrder sort
        Point[] points = new Point[6];
        points[0] = d;
        points[1] = b;
        points[2] = a;
        points[3] = f;
        points[4] = c;
        points[5] = e;

        Arrays.sort(points, a.slopeOrder());
        assert points[0].equals(a);
        assert points[1].equals(c);
        assert points[2].equals(f);
        assert points[3].equals(d);
        assert points[4].equals(e);
        assert points[5].equals(b);
    }
}
