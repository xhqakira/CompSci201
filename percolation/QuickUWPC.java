import java.util.ArrayList;

public class QuickUWPC implements IUnionFind {
	private int[] parent;   // parent[i] = parent of i
    private int[] size;     // size[i] = number of sites in subtree rooted at i
    private int count;      // number of components

	
	public QuickUWPC(){
		parent = null;
		count = 0;
	}
	
	public QuickUWPC(int n){
		initialize(n);
	}

	@Override
	public void initialize(int n) {
		// TODO Auto-generated method stub
		count = n;
		parent = new int[n];
		size =new int[n];
		for(int i = 0; i< n; i++){
			parent[i] = i;
			size[i] = 1;
		}
	}

	@Override
	public int components() {
		// TODO Auto-generated method stub
		return count;
	}

	@Override
	public int find(int x) {
		// TODO Auto-generated method stub
		validate(x);
		ArrayList<Integer> nodes = new ArrayList<Integer>(); 
		int p = x;
		while(p!= parent[p]){
			nodes.add(p);
			p = parent[p];
		}
		for(int node:nodes){
			parent[node] = p;
		}
		return p;
	}
	
	// validate that p is a valid index
    private void validate(int p) {
        int n = parent.length;
        if (p < 0 || p >= n) {
            throw new IndexOutOfBoundsException("index " + p + " is not between 0 and " + (n-1));  
        }
    }

	@Override
	public boolean connected(int p, int q) {
		// TODO Auto-generated method stub
		return find(p) == find(q);
	}

	@Override
	public void union(int p, int q) {
		// TODO Auto-generated method stub
		int root_p = find(p);
		int root_q = find(q);
		if(root_p == root_q)
			return;
		if(size[root_p] >= size[root_q]){
			parent[root_q] = root_p;
			size[root_p] += size[root_q];
		}else{
			parent[root_p] = root_q;
			size[root_q] += size[root_p];
		}
		count--;
	}

}
