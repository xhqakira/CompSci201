import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;

import javax.print.DocFlavor.STRING;

/**
 * General trie/priority queue algorithm for implementing Autocompletor
 * 
 * @author Austin Lu
 * @author Jeff Forbes
 */
public class TrieAutocomplete implements Autocompletor {

	/**
	 * Root of entire trie
	 */
	protected Node myRoot;

	/**
	 * Constructor method for TrieAutocomplete. Should initialize the trie
	 * rooted at myRoot, as well as add all nodes necessary to represent the
	 * words in terms.
	 * 
	 * @param terms
	 *            - The words we will autocomplete from
	 * @param weights
	 *            - Their weights, such that terms[i] has weight weights[i].
	 * @throws NullPointerException
	 *             if either argument is null
	 * @throws IllegalArgumentException
	 *             if terms and weights are different weight
	 */
	public TrieAutocomplete(String[] terms, double[] weights) {
		if (terms == null || weights == null)
			throw new NullPointerException("One or more arguments null");
		if (terms.length != weights.length)
			throw new IllegalArgumentException("terms and weights are not the same length");
		// Represent the root as a dummy/placeholder node
		myRoot = new Node('-', null, 0);

		for (int i = 0; i < terms.length; i++) {
			add(terms[i], weights[i]);
		}
	}

	/**
	 * Add the word with given weight to the trie. If word already exists in the
	 * trie, no new nodes should be created, but the weight of word should be
	 * updated.
	 * 
	 * In adding a word, this method should do the following: Create any
	 * necessary intermediate nodes if they do not exist. Update the
	 * subtreeMaxWeight of all nodes in the path from root to the node
	 * representing word. Set the value of myWord, myWeight, isWord, and
	 * mySubtreeMaxWeight of the node corresponding to the added word to the
	 * correct values
	 * 
	 * @throws a
	 *             NullPointerException if word is null
	 * @throws an
	 *             IllegalArgumentException if weight is negative.
	 */
	private void add(String word, double weight) {
		// TODO: Implement add
		Node curr = myRoot;
		for(int i = 0; i < word.length(); i++){
			if(curr.mySubtreeMaxWeight < weight){
				curr.mySubtreeMaxWeight = weight;
			}
//			if(curr.parent == null){
//				curr.prefix = "";
//			}else{
//				curr.prefix = curr.parent.prefix + curr.myInfo;
//			}
//			System.out.println("curr_myinfo: "+ curr.myInfo);
//			System.out.println("curr_mySubtreeMaxWeight: " + curr.mySubtreeMaxWeight);
//			System.out.println();
			if(! curr.children.containsKey(word.charAt(i))){
				curr.children.put(word.charAt(i), new Node(word.charAt(i), curr, 0));
			}
			curr = curr.children.get(word.charAt(i));		
		}
		
//		System.out.println("curr_myinfo: " + curr.myInfo);
		curr.myWord = word;
		curr.myWeight = weight;
		if(curr.mySubtreeMaxWeight < weight){
			curr.mySubtreeMaxWeight = weight;
		}
//		curr.prefix = curr.parent.prefix + curr.myInfo;
//		System.out.println("word node(myword): " + curr.myWord);
//		System.out.println("word node(myweight): " + curr.myWeight);
//		System.out.println("word node(mySubtreeMaxWeight): " + curr.mySubtreeMaxWeight);
//		System.out.println();
		if(curr.isWord != false){
			if(curr.mySubtreeMaxWeight > weight){
				// check whether word node needs to change mySubtreeMaxWeight or not
				// based on whether it has children or not 
				// and if it has, the max weight of its children
				if(curr.children != null){
					double childrenMax = findSubtreeMaxWeight(curr);
					if(childrenMax < curr.mySubtreeMaxWeight){
						curr.mySubtreeMaxWeight = Math.max(childrenMax, weight);
					}
				}else{
					curr.mySubtreeMaxWeight = weight;
				}
				
				// search upward until to the root node
				while(curr.parent != null){
					curr = curr.parent;
					double childrenMax = findSubtreeMaxWeight(curr);
					curr.mySubtreeMaxWeight = Math.max(curr.myWeight, childrenMax);
				}
			}			
		}else{
			curr.isWord = true;
		}		
				
	}
	
	public double findSubtreeMaxWeight(Node curr){
		// not include node itself
		double max = 0;
		for(char ch: curr.children.keySet()){
			if(curr.children.get(ch).mySubtreeMaxWeight > max){
				max = curr.children.get(ch).mySubtreeMaxWeight;
			}
		}
		return max;
	}
	
	public Node findSubtreeMaxWeightNode(Node curr){
		// not include node itself
		Node max = null;
		double childrenMaxWeight = 0;
		for(char ch: curr.children.keySet()){
			if(curr.children.get(ch).mySubtreeMaxWeight > childrenMaxWeight){
				childrenMaxWeight = curr.children.get(ch).mySubtreeMaxWeight;
				max = curr.children.get(ch);
			}
		}
		return max;
	}
	
	/**
	 * Required by the Autocompletor interface. Returns an array containing the
	 * k words in the trie with the largest weight which match the given prefix,
	 * in descending weight order. If less than k words exist matching the given
	 * prefix (including if no words exist), then the array instead contains all
	 * those words. e.g. If terms is {air:3, bat:2, bell:4, boy:1}, then
	 * topKMatches("b", 2) should return {"bell", "bat"}, but topKMatches("a",
	 * 2) should return {"air"}
	 * 
	 * @param prefix
	 *            - A prefix which all returned words must start with
	 * @param k
	 *            - The (maximum) number of words to be returned
	 * @return An Iterable of the k words with the largest weights among all
	 *         words starting with prefix, in descending weight order. If less
	 *         than k such words exist, return all those words. If no such words
	 *         exist, return an empty Iterable
	 * @throws a
	 *             NullPointerException if prefix is null
	 */
	public Iterable<String> topMatches(String prefix, int k) {
		// TODO: Implement topKMatches
		if(prefix == null){
			throw new NullPointerException("prefix is null!");
		}
		if (k < 0)
			throw new IllegalArgumentException("Illegal value of k:"+k);
		LinkedList<String> ret = new LinkedList<String>();
		if(k == 0){
			return ret;
		}
		Node curr = myRoot;
		for(int i = 0; i < prefix.length(); i++){
			if(curr.children.containsKey(prefix.charAt(i))){
				curr = curr.children.get(prefix.charAt(i));
			}else{
				return ret;
			}
		}
		PriorityQueue<Node> pq1 = new PriorityQueue<Node>(1000, new Node.ReverseSubtreeMaxWeightComparator());
		PriorityQueue<Node> pq2 = new PriorityQueue<Node>(k);
		pq1.add(curr);
		while(! pq1.isEmpty()){
			curr = pq1.remove();
			if(curr.isWord){
				if(pq2.size() < k){
					pq2.add(curr);
				}else{
					if(curr.mySubtreeMaxWeight > pq2.peek().myWeight){
						pq2.remove();
						pq2.add(curr);
					}else{
						break;
					}
				}
			}
			
			if(! curr.children.isEmpty()){
				for(char ch : curr.children.keySet()){
					pq1.add(curr.children.get(ch));
				}
			}
		}
		int numResults = Math.min(pq2.size(), k);
		for(int j = 0; j < numResults; j++){
			ret.addFirst(pq2.remove().myWord);
		}
		return ret;
	}

	/**
	 * Given a prefix, returns the largest-weight word in the trie starting with
	 * that prefix.
	 * 
	 * @param prefix
	 *            - the prefix the returned word should start with
	 * @return The word from with the largest weight starting with prefix, or an
	 *         empty string if none exists
	 * @throws a
	 *             NullPointerException if the prefix is null
	 */
	public String topMatch(String prefix) {
		// TODO: Implement topMatch
		if(prefix == null){
			throw new NullPointerException("prefix is null!");
		}
		Node curr = myRoot;
		for(int i = 0; i < prefix.length(); i++){
			if(curr.children.containsKey(prefix.charAt(i))){
				curr = curr.children.get(prefix.charAt(i));
			}else{
				return "";
			}
		}
//		System.out.println("prefix: " + curr.myInfo);
		double max = curr.mySubtreeMaxWeight;
//		System.out.println("prefix max: " + max);
		StringBuilder sb = new StringBuilder(prefix);
		while(true){
			double childrenMaxWeight = findSubtreeMaxWeight(curr);
//			System.out.println("childrenMaxWeight: " + childrenMaxWeight);
			if(childrenMaxWeight == max){
				curr = findSubtreeMaxWeightNode(curr);
//				System.out.println("childrenMaxInfo: " + curr.myInfo);
				sb.append(curr.myInfo);
//				System.out.println();
			}else{
//				System.out.println("this is word node!");
//				System.out.println();
				return sb.toString();
			}
		}
	}

	/**
	 * Return the weight of a given term. If term is not in the dictionary,
	 * return 0.0
	 */
	public double weightOf(String term) {
		// TODO complete weightOf
		Node curr = myRoot;
		for(int i = 0; i < term.length(); i++){
			if(curr.children.containsKey(term.charAt(i))){
				curr = curr.children.get(term.charAt(i));
			}else{
				return 0.0;
			}
		}
		return curr.myWeight;
	}

	/**
	 * Optional: Returns the highest weighted matches within k edit distance of
	 * the word. If the word is in the dictionary, then return an empty list.
	 * 
	 * @param word
	 *            The word to spell-check
	 * @param dist
	 *            Maximum edit distance to search
	 * @param k
	 *            Number of results to return
	 * @return Iterable in descending weight order of the matches
	 */
//	public Iterable<String> spellCheck(String word, int dist, int k) {
//		if(dist <= 0){
//			throw new IllegalArgumentException("dist is not positive");
//		}
//		if(k < 0){
//			throw new IllegalArgumentException("k is negative!");
//		}
//		myRoot.LevenshteinDistanceRow = new int[word.length() + 1];
//		for(int i = 0; i < myRoot.LevenshteinDistanceRow.length; i++){
//			myRoot.LevenshteinDistanceRow[i] = i;
//		}
//		
//		Node curr = myRoot;
//		PriorityQueue<Node> pq = new PriorityQueue<Node>(k);
//		
//		for(char ch : curr.children.keySet()){
////			System.out.println("curr_info: " + curr.myInfo);
////			System.out.println("curr_prefix: " + curr.prefix);
//			traverse(curr.children.get(ch), pq, word, k, dist);
//		}
//		
//		LinkedList<String> ret = new LinkedList<String>();
//		int numResults = Math.min(pq.size(), k);
//		for(int j = 0; j < numResults; j++){
//			ret.addFirst(pq.remove().myWord);
//		}
//		return ret;
//	}
//	
//	public void traverse(Node curr, PriorityQueue<Node> pq, String word, int k, int dist){
////		for(int m = 0; m < curr.parent.LevenshteinDistanceRow.length; m++){
////			System.out.println(curr.parent.LevenshteinDistanceRow[m]);
////		}
//		int distance = LevenshteinDistance(curr, curr.prefix, word);
////		for(int m = 0; m < curr.LevenshteinDistanceRow.length; m++){
////			System.out.println(curr.LevenshteinDistanceRow[m]);
////		}
////		System.out.println("curr_prefix: " + curr.prefix);
////		System.out.println("curr_distance: " + distance);
//		if(curr.isWord == true && distance <= dist){
//			if(pq.size() < k){
//				pq.add(curr);
//			}else{
//				if(pq.peek().myWeight < curr.myWeight){
//					pq.remove();
//					pq.add(curr);
//				}
//			}
//		}
//	
//		if(curr.children.size() == 0){
//			return;
//		}else{
//			for(char ch : curr.children.keySet()){
////				System.out.println("ch: " + ch);
//				traverse(curr.children.get(ch), pq, word, k, dist);
//			}
//	    }		
//	}
//	
//	public int LevenshteinDistance(Node curr, String s, String t){
//		int[] v0 = curr.parent.LevenshteinDistanceRow;
//		int[] v1 = new int[t.length() + 1];
//				
//		v1[0] = s.length();
//		for(int j = 0; j < t.length(); j++){
//			int cost = 0;
//			if(s.charAt(s.length() - 1) != t.charAt(j)){
//				cost = 1;
//			}
//			v1[j + 1] = Math.min(Math.min(v1[j] + 1, v0[j + 1] + 1), v0[j] + cost);
//		}
//			
//		curr.LevenshteinDistanceRow = v1;
//		
//		if(s.equals(t)){
//			return 0;
//		}
//		if(s.length() == 0){
//			return t.length();
//		}
//		if(t.length() == 0){
//			return s.length();
//		}
//		
//		return v1[t.length()];
//	}
//	private String[] iterToArr(Iterable<String> it) {
//		ArrayList<String> list = new ArrayList<String>();
//		for (String s: it)
//			list.add(s);
//		return list.toArray(new String[0]);
//	}
//	
//	public static void main(String[] args) throws FileNotFoundException{
//		Term[] terms =
//				new Term[] {new Term("ape", 6), 
//				new Term("app", 4), 
//				new Term("ban", 2),
//				new Term("bat", 3),
//				new Term("bee", 5),
//				new Term("car", 7),
//				new Term("car", 2),
//				new Term("cat", 1)};
//		String[] names= {"ape", "app", "ban", "bat", "bee", "car", "cat"};
//		double[] weights = {6, 4, 2, 3, 5, 7, 1};
//		TrieAutocomplete ta = new TrieAutocomplete(names,weights);
////		String[] queries = {"", "a", "ap", "b", "ba", "c", "ca", "cat", "d", " "};
//////		System.out.println(ta.myRoot.mySubtreeMaxWeight);
////		for(int i = 0; i < queries.length; i++){
////			String query = queries[i];
////			String reported = ta.topMatch(query);
//////			System.out.println(reported);
////		}
//		String[] queries = {"", "", "", "", "a", "ap", "b", "ba", "d"};
//		int[] ks = {8, 0, 2, 3, 1, 1, 2, 2, 100};
//		for(int i = 0; i < queries.length; i++){
//			String query = queries[i];
//			String[] reported = ta.iterToArr(ta.topMatches(query, ks[i]));
//			System.out.println(Arrays.toString(reported));
//		}
//		String[] reported = ta.iterToArr(ta.spellCheck("a", 2, 10));
//		System.out.println(Arrays.toString(reported));	
//		String pfile = "data/wiktionary.txt";
//		File f = new File(pfile);
//		Scanner s = new Scanner(f);
//		int count = Integer.parseInt(s.nextLine());
//		String[] names = new String[count];
//		double[] weights = new double[count];
//		int i = 0;
//		while(s.hasNextLine()){
//			String[] info = s.nextLine().trim().split("\\s+");
//			weights[i] = Double.parseDouble(info[0]);
//			names[i] = info[1];
//			i++;
//		}
//		TrieAutocomplete ta = new TrieAutocomplete(names,weights);
//		String[] queries = {"whut", "efect", "heyy", "gurl"};
//		for(int j = 0; j < queries.length; j++){
//			String query = queries[j];
//			String[] reported = ta.iterToArr(ta.spellCheck(query, 1, 5));
////			System.out.println(Arrays.toString(reported));
////		}
//	}
}
