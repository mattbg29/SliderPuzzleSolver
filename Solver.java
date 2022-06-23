/* *****************************************************************************
 *  Part of my solution to Princeton's slider puzzle problem using a minimum priority queue
 *  Problem spec can be found here: https://coursera.cs.princeton.edu/algs4/assignments/8puzzle/specification.php
 *  Nearly all of the code below is my own
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedList;

public class Solver {
    private int movesTotal;
    private boolean canSolve;
    private LinkedList<Board> queue;

    private class Node implements Comparable<Node> {
        private Board boardNow;
        private Node prevNode;
        private int manhattanNow;

        public Node(Board b, int m, Node p) {
            boardNow = b;
            prevNode = p;
            manhattanNow = b.manhattan();
        }

        // I'll compare boards by their manhattan values
        public int compareTo(Node that) {
            return manhattanNow - that.boardNow.manhattan();
        }
    }

    // To solve the puzzle, i create two minimum priority queues, one for the starting board
    // and one for the starting board's twin (see twin explanation in Board class)
    // I then delete the minimum of each, as defined by the board with the lowest Manhattan score,
    // and add these board's neighbors to each queue and repeat.
    // Once one board is solved, if it is the original, then I return stats on the solution,
    // and if it is the twin, then I return that the original was unsolvable.
    public Solver(Board initial) {
        int movesNow = 0;
        queue = new LinkedList<>();

        MinPQ<Node> pq = new MinPQ<>();
        MinPQ<Node> pqTwin = new MinPQ<>();

        pq.insert(new Node(initial, movesNow, null));
        pqTwin.insert(new Node(initial.twin(), movesNow, null));

        Node prev = pq.delMin();
        Node prevTwin = pqTwin.delMin();

        queue.add(prev.boardNow);

        while (prev.manhattanNow > 0 && prevTwin.manhattanNow > 0) {
            movesNow++;
            for (Board b : prev.boardNow.neighbors()) {
                if (prev.prevNode == null) {
                    pq.insert(new Node(b, movesNow, prev));
                }
                else if (!b.equals(prev.prevNode.boardNow)) {
                    pq.insert(new Node(b, movesNow, prev));
                }
            }
            for (Board b : prevTwin.boardNow.neighbors()) {
                if (prevTwin.prevNode == null) {
                    pqTwin.insert(new Node(b, movesNow, prevTwin));
                }
                else if (!b.equals(prevTwin.prevNode.boardNow)) {
                    pqTwin.insert(new Node(b, movesNow, prevTwin));
                }
            }

            prev = pq.delMin();
            prevTwin = pqTwin.delMin();
            queue.add(prev.boardNow);
        }
        if (prevTwin.boardNow.isGoal()) {
            canSolve = false;
        }
        else {
            canSolve = true;
            movesTotal = movesNow;
        }
    }

    public boolean isSolvable() {
        return canSolve;
    }

    public int moves() {
        if (isSolvable()) {
            return movesTotal;
        }
        else {
            return -1;
        }
    }

    public Iterable<Board> solution() {
        if (isSolvable()) {
            return queue;
        }
        else {
            return null;
        }
    }

    public static void main(String[] args) {

        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tiles[i][j] = in.readInt();
            }
        }
        Board initial = new Board(tiles);

        Solver solver = new Solver(initial);

        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
