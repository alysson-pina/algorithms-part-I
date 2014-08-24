/*
 *
 *  Author: Alysson Ferreira
 *  Date: 7-28-2014
 *  Compilation:  javac Percolation.java
 *  Dependencies: WeightedQuickUnionUF.java
 *
 *  This class provides an API which implements the Percolation problem.
 *
 * It relies on the following assumptions:
 *    - When open sites with outOfBounds index values,
 * that node is not considered and flow moves on.
 *    - N >= 1.
 *    - Creates two extra virtual nodes on the Binary tree -
 *      the top virtual and the bottom virtual nodes.
 **/

public class Percolation {
    private int N; // dimension of matrix NxN
	private boolean[][] matrix; // matrix of characters NxN;
	private WeightedQuickUnionUF uf;
    private WeightedQuickUnionUF ufNoVirtualBottom; // to avoid backwash problems
	private final int virtualTop, virtualBottom, firstRow, lastRow;

	// create n-by-n grid, with all sites blocked
	public Percolation(int n) {
		this.N = n;
		this.matrix = new boolean[N][N];
		this.virtualTop = 0;
		this.virtualBottom = conv2DTo1D(N - 1, N);

		firstRow = 0;
		lastRow = this.N - 1;

		// initialize 1D array for quick uf and add top/bottom virtual node.
		this.uf = new WeightedQuickUnionUF(N * N + 2);

		// initialize 1D array for quick uf matrix with no virtual bottom
		this.ufNoVirtualBottom = new WeightedQuickUnionUF(N * N + 1);
	}

	/*
	 * open site given 2D position (row a, column b) if it is not already.
	 * assumes (a,b) index position is 1-based.
	 *
	 * Throws IndexOutOfBoundsException if position is out of bounds.
	 * */
	public void open(int a, int b) {
		int i = a - 1, // 0-based row index
			j = b - 1; // 0-based column index

		// validate indices
		if (!validateIndices(i, j)) {
			 throw new IndexOutOfBoundsException();
		}

		// open site i,j
		this.matrix[i][j] = true;

		// connect site with Neighboring component
		connectSite(i, j);
	}

	// is site (row i, column j) open?
	public boolean isOpen(int i, int j) {
		return matrix[i - 1][j - 1];
	}

	/*
	 * checks if site (row i, column j) is connected to top virtual node.
	 * assumes 2D index (i,j) is 0-based.
	 * uses ufNoVirtualBottom Union Find problem without virtual bottom node.
	 *
	 * return true -> site connected to top virtual node / false otherwise.
	 * */
	public boolean isFull(int i, int j) {
		int index1D = conv2DTo1D(i - 1, j - 1);
		return ufNoVirtualBottom.connected(index1D, virtualTop);
	}

	/*
	 * checks if system percolate.
	 * uses uf connected method with virtual nodes top and bottom.
	 *
	 * returns true -> percolates / false otherwise.
	 * */
	public boolean percolates() {
		// checks if bottom virtual site is connected to top virtual node
		return uf.connected(virtualBottom, virtualTop);
	}

	/*
	 * private method to convert 2D site position into 1D index on weighted array.
	 * assumes 2D index (i,j) is 0-based.
	 *
	 * returns integer corresponding to 1D index position.
	 * */
	private int conv2DTo1D(int i, int j) {
		return ((i * this.N) + j + 1);
	}

	/*
	 * checks if given 2D index i,j is within the boundaries of the matrix.
	 * assumes 2D index (i,j) is 0-based.
	 *
	 * returns true if inside bounds / false otherwise.
	 * */
	private boolean validateIndices(int i, int j) {
		if (i < 0 || i >= this.N || j < 0 || j >= this.N) {
			return false;
		}
		return true;
	}

	/*
	 * connects site to neighboring components on top, bottom, left, right positions.
	 * uses uf union method.
	 * assumes 2D index (i,j) is 0-based.
	 * */
	private void connectSite(int i, int j) {
		int index1D = conv2DTo1D(i, j);

		int	topRow = i - 1,
			bottomRow = i + 1,
			leftColumn = j - 1,
			rightColumn = j + 1;

		// if site is on the first row, it should be connected
		// to top virtual node of index 0
		if (i == firstRow) {
			uf.union(virtualTop, index1D);
			ufNoVirtualBottom.union(virtualTop, index1D);
		}

		// only connect site to its neighbor cells if they are also opened
		// attempt to connect to top cell
		connectNeighbor(index1D, topRow, j);

		// attempt to connect to left cell
		connectNeighbor(index1D, i, leftColumn);

		// attempt to connect to right cell
		connectNeighbor(index1D, i, rightColumn);

		// attempt to connect to bottom cell
		connectNeighbor(index1D, bottomRow, j);

		if (i == lastRow) {
			uf.union(index1D, virtualBottom);
		}
	}

	/*
	 * connects a site to a neighbor, if neighbor is eligible.
	 *
	 * @param
	 * siteIndex - 1D index of site
	 * neighborI - row index of neighbor to be connected to site. 0-based.
	 * neighborJ - column index of neighbor to be connected to site. 0-based.
	 * */
	private void connectNeighbor(int siteIndex, int neighborI, int neighborJ) {
		// validate indices
		if (!validateIndices(neighborI, neighborJ)) {
			return;
		}

		// check if neighbor is opened 
		// before transform i,j coordinate (0-indexed)
		// into a,b coordinate (1-indexed).
		if (isOpen(neighborI + 1, neighborJ + 1)) {
			uf.union(siteIndex, conv2DTo1D(neighborI, neighborJ));
			ufNoVirtualBottom.union(siteIndex, conv2DTo1D(neighborI, neighborJ));
		}
	}
}