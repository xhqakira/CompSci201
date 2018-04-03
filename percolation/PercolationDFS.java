import java.util.Arrays;

/**
 * Simulate percolation thresholds for a grid-base system using depth-first-search,
 * aka 'flood-fill' techniques for determining if the top of a grid is connected
 * to the bottom of a grid.
 * <P>
 * Modified from the COS 226 Princeton code for use at Duke. The modifications
 * consist of supporting the <code>IPercolate</code> interface, renaming methods
 * and fields to be more consistent with Java/Duke standards and rewriting code
 * to reflect the DFS/flood-fill techniques used in discussion at Duke.
 * <P>
 * @author Kevin Wayne, wayne@cs.princeton.edu
 * @author Owen Astrachan, ola@cs.duke.edu
 * @author Jeff Forbes, forbes@cs.duke.edu
 */


public class PercolationDFS implements IPercolate {
	// possible instance variable for storing grid state
	public int[][] myGrid;
	private int myOpenSites;
	public static final int BLOCKED = 0;
	public static final int OPEN = 1;
	public static final int FULL = 2;
	public int mySize;

	/**
	 * Initialize a grid so that all cells are blocked.
	 * 
	 * @param n
	 *            is the size of the simulated (square) grid
	 */
	public PercolationDFS(int n) {
		// TODO complete constructor and add necessary instance variables
		if(n<=0)
			throw new IllegalArgumentException(n+" is illegal argument!");
		myOpenSites = 0;
		myGrid = new int[n][n];
		mySize = n;
		for (int[] row: myGrid)
			Arrays.fill(row, BLOCKED);
		/*
		for (int i = 0; i < myGrid.length; i++)
			for (int j=0; j < myGrid[i].length; j++)
				myGrid[i][j] = BLOCKED;
		*/
	}
	
	public boolean checkBound(int i, int j){
		if(i< 0 || i>= mySize || j< 0 || j>= mySize){
			return false;
		}
		return true;
	}
	
	public void open(int i, int j) {
		// TODO complete open
		if(! checkBound(i,j))
			throw new IndexOutOfBoundsException("Index "+i+","+j+" is out of bound!");
		if (myGrid[i][j] != BLOCKED)
			return;
		myOpenSites++;
		myGrid[i][j] = OPEN;
		for(int row= 0; row< mySize; row++)
			for(int col= 0; col< mySize; col++){
				if(myGrid[row][col] == FULL)
					myGrid[row][col] = OPEN;
			}
		for(int col= 0; col< mySize; col++){
			dfs(0,col);
		}	
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
		return myGrid[i][j] == FULL;
	}

	public int numberOfOpenSites() {
		// TODO return the number of calls to open new sites
		return myOpenSites;
	}

	public boolean percolates() {
		// TODO: determine whether any cells on the bottom row are full
		for(int col=0; col< myGrid[mySize-1].length; col++){
			if(myGrid[mySize-1][col] == FULL)
				return true;
		}
		return false;
	}

	/**
	 * Private helper method to mark all cells that are open and reachable from
	 * (row,col).
	 * 
	 * @param row
	 *            is the row coordinate of the cell being checked/marked
	 * @param col
	 *            is the col coordinate of the cell being checked/marked
	 */
	private void dfs(int row, int col) {
		if (! checkBound(row, col))
			// out of bounds
			return;
		if (isFull(row, col) || !isOpen(row, col))
			return;
		myGrid[row][col] = FULL;
		//dfs(row - 1, col);  up
		//dfs(row, col - 1);  left
		//dfs(row, col + 1);  right
		//dfs(row + 1, col);  down
		int[] dx = {-1, 1, 0, 0};
	    int[] dy = {0, 0, -1, 1};
	    for (int i = 0; i < dx.length; i++) {
	        int x = row + dx[i];
	        int y = col + dy[i];
	        dfs(x,y);
	    }
	}
}
