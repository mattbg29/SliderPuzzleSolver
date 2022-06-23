/* *****************************************************************************
 *  Part of my solution to Princeton's slider puzzle problem
 *  Problem spec can be found here: https://coursera.cs.princeton.edu/algs4/assignments/8puzzle/specification.php
 *  Nearly all of the code below is my own
 **************************************************************************** */

import edu.princeton.cs.algs4.Stack;

public class Board {
    private int n;
    private final char[] boardNow;

    // Creates an n x n board made up of chars
    // I use chars to keep memory usage down
    // 0 represents the blank space
    public Board(int[][] tiles) {
        n = tiles[0].length;
        boardNow = new char[n * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                boardNow[i * n + j] = (char) tiles[i][j];
            }
        }
    }

    // String representation of the board
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(n + "\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int a = boardNow[i * n + j];
                s.append(String.format("%2d ", a));
            }
            s.append("\n");
        }
        return s.toString();
    }

    public int dimension() {
        return n;
    }

    // To solve the problem, I will sort boards by one of two strategies
    // The Hamming strategy simply adds 1 for each tile that is out of place
    public int hamming() {
        int hammingNow = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if ((int) boardNow[n * i + j] != n * i + j + 1 && (n * i + j + 1) < n * n) {
                    hammingNow++;
                }
            }
        }
        return hammingNow;
    }

    // The Manhattan strategy adds up the number of moves each tile is from its target
    public int manhattan() {
        int manhattanNow = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if ((int) boardNow[n * i + j] != 0) {
                    int gapNow = (int) boardNow[n * i + j] - (n * i + j + 1);
                    int gapRows = Math.abs(gapNow / n);
                    int gapCols = Math.abs(gapNow % n);
                    manhattanNow += gapRows + gapCols;
                }
            }
        }
        return manhattanNow;
    }

    // If hamming is 0, the target has been reached
    // Currently this is not called as it is solved for independently in Solver
    public boolean isGoal() {
        return this.hamming() == 0;
    }

    // checks if two boards are equal.  This method is not actually used, but is required by the assignment.
    public boolean equals(Object y) {
        if (y == null) {
            return false;
        }
        if (getClass() != y.getClass()) {
            return false;
        }
        Board other = (Board) y;
        int thisN = this.dimension();
        int otherN = other.dimension();
        if (thisN != otherN) {
            return false;
        }
        for (int i = 0; i < thisN; i++) {
            for (int j = 0; j < thisN; j++) {
                if (this.boardNow[i * thisN + j] != other.boardNow[i * thisN + j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // Creates 'neighbor' boards, where neighbor is any board that results from moving any
    // tile in the original board.
    public Board createNeighbor(int tile0, int left, int right, int up, int down) {
        int[][] tiles = new int[n][n];
        int changeTile = -left + right - up * n + down * n;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i * n + j == tile0) {
                    tiles[i][j] = boardNow[tile0 + changeTile];
                }
                else if (i * n + j == tile0 + changeTile) {
                    tiles[i][j] = boardNow[tile0];
                }
                else {
                    tiles[i][j] = boardNow[i * n + j];
                }
            }
        }
        Board neighbor = new Board(tiles);
        return neighbor;
    }

    // Returns an interable list of neighbor Boards; each board can have 2-4 neighbor boards
    // depending on the location of the 0 ie blank space.
    public Iterable<Board> neighbors() {
        Stack<Board> stack = new Stack<Board>();
        int tile0 = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if ((int) boardNow[i * n + j] == 0) {
                    tile0 = i * n + j;
                    j = n;
                    i = n;
                }
            }
        }
        if ((tile0 + 1) / n == (tile0 / n) && (tile0 + 1) < (n * n)) {
            stack.push(createNeighbor(tile0, 0, 1, 0, 0));
        }
        if ((tile0 - 1) / n == (tile0 / n) && (tile0 - 1) >= 0) {
            stack.push(createNeighbor(tile0, 1, 0, 0, 0));
        }
        if ((tile0 + n) < (n * n)) {
            stack.push(createNeighbor(tile0, 0, 0, 0, 1));
        }
        if ((tile0 - n) >= 0) {
            stack.push(createNeighbor(tile0, 0, 0, 1, 0));
        }
        return stack;

    }

    // Exactly one of the following is true: a board can be solved or a board with two of its
    // non-empty tiles swapped can be solved.  As such, to test if a board is solvable, I'll
    // create a 'twin' board with two non-zero elements swapped and attempt to solve this
    // simultaneous with the original.  If the twin is solved first, then the original is
    // unsolvable.
    public Board twin() {
        int[][] tiles = new int[n][n];
        boolean switched = false;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tiles[i][j] = boardNow[i * n + j];
                if (j > 0 && !switched) {
                    if ((int) boardNow[i * n + j] != 0 && (int) boardNow[i * n + j - 1] != 0) {
                        int prev = tiles[i][j];
                        tiles[i][j] = tiles[i][j - 1];
                        tiles[i][j - 1] = prev;
                        switched = true;
                    }
                }
            }
        }
        Board twinNow = new Board(tiles);
        return twinNow;
    }

}
