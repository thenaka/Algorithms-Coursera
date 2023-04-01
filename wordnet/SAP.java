import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;

public class SAP {
    private final Digraph graph;

    /**
     * Find the shortest ancestral path.
     * 
     * @param G digraph (not necessarily a DAG).
     * @throws IllegalArgumentException if the given graph is null.
     */
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException("G cannot be null.");
        }
        this.graph = new Digraph(G);
    }

    /**
     * Length of shortest ancestral path between v and w; -1 if no such path.
     * 
     * @param v vertex to find the shortest ancestral path.
     * @param w vertex to find the shortest ancestral path.
     * @return length of shortest ancestral path between v and w; -1 if no such
     *         path.
     * @throws IllegalArgumentException if either vertex is outside the valid range.
     */
    public int length(int v, int w) {
        throwIfOutOfRange(v);
        throwIfOutOfRange(w);
        return getSolution(v, w).getMinLength();
    }

    /**
     * Find a common ancestor of v and w that participates in a shortest ancestral
     * path.
     * 
     * @param v vertex to find the common ancestor.
     * @param w vertex to find the common ancestor.
     * @return the common ancestor of v and w that participates in the shortest
     *         ancestral path; -1 if no such path.
     * @throws IllegalArgumentException if either vertex is outside the valid range.
     */
    public int ancestor(int v, int w) {
        throwIfOutOfRange(v);
        throwIfOutOfRange(w);
        return getSolution(v, w).getCommonAncestor();
    }

    /**
     * Find the length of shortest ancestral path between any vertex in v and any
     * vertex in w.
     * 
     * 
     * @param v collection of vertices to find the shortest ancestral path.
     * @param w collection of vertices to find the shortest ancestral path.
     * @return he length of shortest ancestral path between any vertex in v and any
     *         vertex in w; -1 if no path.
     * @throws IllegalArgumentException if v or w is null, empty, or any vertex is
     *                                  outside the valid range.
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        throwIfNull(v, w);
        throwIfOutOfRange(v, w);
        return getSolution(v, w).getMinLength();
    }

    /**
     * Finds a common ancestor that participates in shortest ancestral path.
     * 
     * @param v collection of vertices to find the common ancestor.
     * @param w collection of vertices to find the common ancestor.
     * @return a common ancestor that participates in shortest ancestral path; -1 if
     *         no such path.
     * @throws IllegalArgumentException if v or w is null, empty, or any vertex is
     *                                  outside the valid range.
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        throwIfNull(v, w);
        throwIfOutOfRange(v, w);
        return getSolution(v, w).getCommonAncestor();
    }

    private void throwIfNull(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException("v or w cannot be null");
        }
    }

    private void throwIfOutOfRange(Iterable<Integer> v, Iterable<Integer> w) {
        int countV = 0;
        int countW = 0;
        for (int vertex : v) {
            countV++;
            throwIfOutOfRange(vertex);
        }
        for (int vertex : w) {
            countW++;
            throwIfOutOfRange(vertex);
        }
        if (countV == 0 || countW == 0) {
            throw new IllegalArgumentException("Iterable must have at least one vertex.");
        }
    }

    private void throwIfOutOfRange(int vertex) {
        if (vertex < 0 || vertex >= this.graph.V()) {
            throw new IllegalArgumentException("vertices must be within valid range.");
        }
    }

    private Solution getSolution(int v, int w) {
        BreadthFirstDirectedPaths pathV = new BreadthFirstDirectedPaths(this.graph, v);
        BreadthFirstDirectedPaths pathW = new BreadthFirstDirectedPaths(this.graph, w);

        return getSolution(pathV, pathW);
    }

    private Solution getSolution(Iterable<Integer> v, Iterable<Integer> w) {
        BreadthFirstDirectedPaths pathV = new BreadthFirstDirectedPaths(this.graph, v);
        BreadthFirstDirectedPaths pathW = new BreadthFirstDirectedPaths(this.graph, w);

        return getSolution(pathV, pathW);
    }

    private Solution getSolution(BreadthFirstDirectedPaths pathV, BreadthFirstDirectedPaths pathW) {
        int vertices = this.graph.V();
        int minLength = Integer.MAX_VALUE;
        int commonAncestor = -1;
        for (int i = 0; i < vertices; i++) {
            if (pathV.hasPathTo(i) && pathW.hasPathTo(i)) {
                int currentLength = pathV.distTo(i) + pathW.distTo(i);
                if (currentLength < minLength) {
                    minLength = currentLength;
                    commonAncestor = i;
                }
            }
        }

        return new Solution(minLength == Integer.MAX_VALUE ? -1 : minLength, commonAncestor);
    }

    private class Solution {
        private final int minLength;
        private final int commonAncestor;

        public Solution(int minLength, int commonAncestor) {
            this.minLength = minLength;
            this.commonAncestor = commonAncestor;
        }

        public int getMinLength() {
            return this.minLength;
        }

        public int getCommonAncestor() {
            return this.commonAncestor;
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        Digraph graph = new Digraph(6);
        graph.addEdge(1, 3);
        graph.addEdge(2, 3);
        graph.addEdge(4, 2);
        graph.addEdge(0, 2);
        graph.addEdge(5, 1);

        SAP sap = new SAP(graph);
        assert sap.length(1, 0) == 3;
        assert sap.ancestor(1, 0) == 3;
        assert sap.length(4, 0) == 2;
        assert sap.ancestor(4, 0) == 2;
        assert sap.length(5, 2) == 3;
        assert sap.ancestor(5, 2) == 3;
    }
}