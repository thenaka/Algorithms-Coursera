import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;

public class KdTree {
    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;

    private int size;
    private Node root;

    private static class Node {

        private Point2D point; // the point
        private RectHV rect; // the axis-aligned rectangle corresponding to this node
        private Node left; // the left/bottom subtree
        private Node right; // the right/top subtree
        private boolean orientation; // true = VERTICAL (x-coord), false = HORIZONTAL (y-coord)

        public Node(Point2D p) {
            point = p;
        }
    }

    /**
     * Represents a set of points in the unit square (all points have x- and
     * y-coordinates between 0 and 1).
     * 
     */
    public KdTree() {
        size = 0;
        root = null;
    }

    /**
     * 
     * Is the set empty.
     * 
     * @return true if the set empty, otherwise false.
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * 
     * Number of points in the set.
     * 
     * @return number of points in the set.
     */
    public int size() {
        return size;
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
        root = insert(root, p, VERTICAL);
    }

    private Node insert(Node node, Point2D point, boolean orientation) {
        if (node == null) {
            Node temp = new Node(point);
            temp.orientation = orientation;
            size++;
            return temp;
        }

        double pointX = point.x();
        double pointY = point.y();
        double nodeX = node.point.x();
        double nodeY = node.point.y();
        if (node.orientation == VERTICAL) {
            if (pointX < nodeX)
                node.left = insert(node.left, point, !node.orientation);
            else if (pointX > nodeX)
                node.right = insert(node.right, point, !node.orientation);
            else if (pointY != nodeY)
                node.right = insert(node.right, point, !node.orientation);
            // do nothing with equal points
        } else { // HORIZONTAL
            if (pointY < nodeY)
                node.left = insert(node.left, point, !node.orientation);
            else if (pointY > nodeY)
                node.right = insert(node.right, point, !node.orientation);
            else if (pointX != nodeX)
                node.right = insert(node.right, point, !node.orientation);
            // do nothing with equal points
        }
        return node;
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

        Node node = root;
        while (node != null) {
            if (node.point.equals(p))
                return true;

            double pointX = p.x();
            double pointY = p.y();
            double nodeX = node.point.x();
            double nodeY = node.point.y();
            if (node.orientation == VERTICAL) {
                if (pointX < nodeX)
                    node = node.left;
                else if (pointX > nodeX)
                    node = node.right;
                else if (pointY != nodeY)
                    node = node.right;
            } else { // HORIZONTAL
                if (pointY < nodeY)
                    node = node.left;
                else if (pointY > nodeY)
                    node = node.right;
                else if (pointX != nodeX)
                    node = node.right;
            }
        }
        return false;
    }

    /**
     * 
     * Draw all points to standard draw.
     * 
     */
    public void draw() {
        // TODO implement
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

        Stack<Point2D> pointsInRect = new Stack<>();
        range(root, new RectHV(0, 0, 1, 1), rect, pointsInRect);
        return pointsInRect;
    }

    private void range(Node node, RectHV nodeRect, RectHV queryRect, Stack<Point2D> pointsInRect) {
        if (node == null)
            return;
        if (!nodeRect.intersects(queryRect))
            return;

        if (queryRect.contains(node.point))
            pointsInRect.push(node.point);

        double xmin, ymin, xmax, ymax;
        if (node.orientation == VERTICAL) {
            ymin = nodeRect.ymin();
            ymax = nodeRect.ymax();

            xmin = node.point.x();
            xmax = nodeRect.xmax();
            range(node.right, new RectHV(xmin, ymin, xmax, ymax), queryRect, pointsInRect);

            xmin = nodeRect.xmin();
            xmax = node.point.x();
            range(node.left, new RectHV(xmin, ymin, xmax, ymax), queryRect, pointsInRect);
        } else { // HORIZONTAL
            xmin = nodeRect.xmin();
            xmax = nodeRect.xmax();

            ymin = node.point.y();
            ymax = nodeRect.ymax();
            range(node.right, new RectHV(xmin, ymin, xmax, ymax), queryRect, pointsInRect);

            ymin = nodeRect.ymin();
            ymax = node.point.y();
            range(node.left, new RectHV(xmin, ymin, xmax, ymax), queryRect, pointsInRect);
        }
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

        if (root == null)
            throw new IllegalArgumentException("No nearest point in as not points.");

        Point2D nearest = root.point;
        nearest(root, new RectHV(0, 0, 1, 1), p, nearest);
        return nearest;
    }

    private void nearest(Node node, RectHV nodeRect, Point2D queryPoint, Point2D nearest) {
        if (node == null)
            return;

        if (node.point.equals(queryPoint)) {
            nearest = node.point;
            return;
        }

        if (queryPoint.distanceSquaredTo(node.point) < queryPoint.distanceSquaredTo(nearest)) {
            nearest = node.point;
        }

        double xmin, ymin, xmax, ymax;
        double distanceToNearestPoint = queryPoint.distanceSquaredTo(nearest);
        if (node.orientation == VERTICAL) {
            ymin = nodeRect.ymin();
            ymax = nodeRect.ymax();

            xmin = node.point.x();
            xmax = nodeRect.xmax();
            RectHV rightRect = new RectHV(xmin, ymin, xmax, ymax);
            if (rightRect.distanceSquaredTo(queryPoint) < distanceToNearestPoint)
                nearest(node.right, rightRect, queryPoint, nearest);

            xmin = nodeRect.xmin();
            xmax = node.point.x();
            RectHV leftRect = new RectHV(xmin, ymin, xmax, ymax);
            if (leftRect.distanceSquaredTo(queryPoint) < distanceToNearestPoint)
                nearest(node.left, leftRect, queryPoint, nearest);
        } else { // HORIZONTAL
            xmin = nodeRect.xmin();
            xmax = nodeRect.xmax();

            ymin = node.point.y();
            ymax = nodeRect.ymax();
            RectHV rightRect = new RectHV(xmin, ymin, xmax, ymax);
            if (rightRect.distanceSquaredTo(queryPoint) < distanceToNearestPoint)
                nearest(node.right, rightRect, queryPoint, nearest);

            ymin = nodeRect.ymin();
            ymax = node.point.y();
            RectHV leftRect = new RectHV(xmin, ymin, xmax, ymax);
            if (leftRect.distanceSquaredTo(queryPoint) < distanceToNearestPoint)
                nearest(node.left, leftRect, queryPoint, nearest);
        }
    }

    private void throwIfNull(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Point must not be null.");
        }
    }

    // unit testing of the methods
    public static void main(String[] args) {

    }
}
