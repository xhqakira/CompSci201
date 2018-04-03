import java.util.Arrays;

/**
 * Simulate a system to see its Percolation Threshold, but use a UnionFind
 * implementation to determine whether simulation occurs. The main idea is that
 * initially all cells of a simulated grid are each part of their own set so
 * that there will be n^2 sets in an nxn simulated grid. Finding an open cell
 * will connect the cell being marked to its neighbors --- this means that the
 * set in which the open cell is 'found' will be unioned with the sets of each
 * neighboring cell. The union/find implementation supports the 'find' and
 * 'union' typical of UF algorithms.
 * <P>
 * 
 * @author Owen Astrachan
 * @author Jeff Forbes
 *
 */

public class PercolationUF implements IPercolate {
	public int[][] myGrid;
	private int myOpenSites;
	public static final int BLOCKED = 0;
	public static final int OPEN = 1;
	private final int OUT_BOUNDS = -1;
	public IUnionFind uf;
	public int mySize;

	/**
	 * Constructs a Percolation object for a nxn grid that creates
	 * a IUnionFind object to determine whether cells are full
	 */
	public PercolationUF(int n) {
		// TODO complete PercolationUF constructor
		if(n<=0)
			throw new IllegalArgumentException(n+" is illegal argument!");
		myOpenSites = 0;
		myGrid = new int[n][n];
		mySize = n; 
		for(int[] row: myGrid){
			Arrays.fill(row, BLOCKED);
		}
		//uf = new QuickFind(n*n+2);
		uf = new QuickUWPC(n*n+2);
	}
	
	public boolean checkBound(int i, int j){
		if(i< 0 || i>= myGrid.length || j< 0 || j>= myGrid[i].length){
			return false;
		}
		return true;
	}

	/**
	 * Return an index that uniquely identifies (row,col), typically an index
	 * based on row-major ordering of cells in a two-dimensional grid. However,
	 * if (row,col) is out-of-bounds, return OUT_BOUNDS.
	 */
	public int getIndex(int row, int col) {
		// TODO complete getIndex
		if(! checkBound(row, col))
			return OUT_BOUNDS;
		return row*mySize+col+1;
	}

	public void open(int i, int j) {
		// TODO complete open
		if(! checkBound(i,j))
			throw new IndexOutOfBoundsException("Index "+i+","+j+" is out of bound!");
		if (myGrid[i][j] != BLOCKED)
			return;
		myOpenSites++;
		myGrid[i][j] = OPEN;
		connect(i,j);
	}

	public boolean isOpen(int i, int j) {
		// TODO complete isOpen
		if(! checkBound(i,j)){
			throw new IndexOutOfBoundsException("Index "+i+","+j+" is out of bound!");
		}
		return myGrid[i][j] == OPEN;
	}

	public boolean isFull(int i, int j) {
		// TODO complete isFull
		if(! checkBound(i,j)){
			throw new IndexOutOfBoundsException("Index "+i+","+j+" is out of bound!");
		}
		int current = getIndex(i,j);
		return uf.connected(current,0);
	}

	public int numberOfOpenSites() {
		// TODO return the number of calls to open new sites
		return myOpenSites;
	}

	public boolean percolates() {
		// TODO complete percolates
		return uf.connected(0, mySize*mySize+1);
	}

	/**
	 * Connect new site (row, col) to all adjacent open sites
	 */
	private void connect(int row, int col) {
		// TODO complete connect
		int current = getIndex(row, col);
		//up
		if(row-1< 0){
			uf.union(current, 0);
		}else{
			if(isOpen(row-1, col)){
				int up = getIndex(row-1, col);
				uf.union(current, up);
			}
		}
		//left
		if(col-1 >= 0){
			if(myGrid[row][col-1] == OPEN){
				int left = getIndex(row, col-1);
				uf.union(current, left);
			}
		}
		//right
		if(col+1 < myGrid[row].length){
			if(myGrid[row][col+1] == OPEN){
				int right = getIndex(row, col+1);
				uf.union(current, right);
			}
		}
		//down
		if(row+1>= mySize){
			uf.union(current, mySize*mySize+1);
		}else{
			if(isOpen(row+1, col)){
				int down = getIndex(row+1, col);
				uf.union(current, down);
			}
		}
	}

}
