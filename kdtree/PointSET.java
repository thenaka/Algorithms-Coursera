import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;

public class PointSET {
    private SET<Point2D> points;

    /**
     * Represents a set of points in the unit square (all points have x- and
     * y-coordinates between 0 and 1).
     * 
     */
    public PointSET() {
        points = new SET<>();
    }

    /**
     * 
     * Is the set empty.
     * 
     * @return true if the set empty, otherwise false.
     */
    public boolean isEmpty() {
        return points.isEmpty();
    }

    /**
     * 
     * Number of points in the set.
     * 
     * @return number of points in the set.
     */
    public int size() {
        return points.size();
    }

    /**
     * 
     * Add the point to the set (if it is not already in the set).
     * 
     * @param p point to add.
     * @throws IllegalArgumentException when p is null.
     */
    public void insert(Point2D p) {
        throwIfNull(p);
        points.add(p);
    }

    /**
     * 
     * Does the set contain point p?
     * 
     * @param p point to check.
     * @return true if the points is contained in the set, otherwise false.
     * @throws IllegalArgumentException when p is null.
     */
    public boolean contains(Point2D p) {
        throwIfNull(p);
        return points.contains(p);
    }

    /**
     * 
     * Draw all points to standard draw.
     * 
     */
    public void draw() {
        for (Point2D p : points) {
            p.draw();
        }
    }

    /**
     * 
     * All points that are inside the rectangle (or on the boundary).
     * 
     * @param rect rectangle to check what points are in boundary.
     * @return all points in or on the boundar of the rectangle.
     * @throws IllegalArgumentException when rect is null.
     */
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("Rectangle must not be null.");
        }

        Stack<Point2D> pointInRect = new Stack<>();
        for (Point2D p : points) {
            if (rect.contains(p)) {
                pointInRect.push(p);
            }
        }
        return pointInRect;
    }

    /**
     * 
     * A nearest neighbor in the set to point p; null if the set is empty.
     * 
     * @param p point to check for its nearest neighbor.
     * @return the nearest point. Null when no points.
     * @throws IlllegalArgumentException when p is null.
     */
    public Point2D nearest(Point2D p) {
        throwIfNull(p);

        Point2D nearest = null;
        double distance = Double.MAX_VALUE;
        for (Point2D point : points) {
            double distanceTo = point.distanceSquaredTo(p);
            if (distanceTo < distance) {
                nearest = point;
                distance = distanceTo;
            }
        }
        return nearest;
    }

    private void throwIfNull(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Point must not be null.");
        }
    }

    // unit testing of the methods
    public static void main(String[] args) {
        PointSET pointSet = new PointSET();
        assert pointSet.size() == 0;
        assert pointSet.isEmpty();

        Point2D point1x1 = new Point2D(0.1, 0.1);
        Point2D point2x2 = new Point2D(0.2, 0.2);
        Point2D point3x3 = new Point2D(0.3, 0.3);
        Point2D point4x4 = new Point2D(0.4, 0.4);
        Point2D point5x5 = new Point2D(0.5, 0.5);

        pointSet.insert(point1x1);
        pointSet.insert(point2x2);
        pointSet.insert(point3x3);
        pointSet.insert(point4x4);
        pointSet.insert(point5x5);
        assert pointSet.size() == 5;
        assert !pointSet.isEmpty();

        Point2D nearest = pointSet.nearest(new Point2D(0, 0));
        assert nearest.equals(point1x1);
        nearest = pointSet.nearest(new Point2D(0.25, 0.25));
        assert nearest.equals(point3x3);
        nearest = pointSet.nearest(new Point2D(0.4125, 0.5125));
        assert nearest.equals(point5x5);

        Iterable<Point2D> pointsInRect = pointSet.range(new RectHV(0.1, 0.2, 0.2, 0.3));
        for (Point2D p : pointsInRect) {
            assert p.equals(point2x2) : "This rectangle only contains one of the points.";
        }
        pointsInRect = pointSet.range(new RectHV(0.01, 0.01, 0.6, 0.6));
        int count = 0;
        for (Point2D p : pointsInRect) {
            switch (++count) {
                case 1:
                    p.equals(point1x1);
                    break;
                case 2:
                    p.equals(point2x2);
                    break;
                case 3:
                    p.equals(point3x3);
                    break;
                case 4:
                    p.equals(point4x4);
                    break;
                case 5:
                    p.equals(point5x5);
                    break;
            }
        }
        assert count == 5;
    }
}