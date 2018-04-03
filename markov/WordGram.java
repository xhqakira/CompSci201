import java.util.*;

public class WordGram implements Comparable<WordGram> {
	private String[] myWords;
	private int myHash;
	
	public WordGram(String[] source, int start, int size){
		myWords = new String[size];
		for(int i=start; i<start+size; i++){
			myWords[i-start] = source[i];
		}
		//System.out.println(Arrays.toString(myWords));
	} 
	
	//*****different hashCode*****//
	//**poor hashCode**//
	/*
	public int hashCode(){
        // TODO return a better hash value
        return 32;
    }
    */
	
	//** collision when "abc" and "cba"**//
	/*
	public int hashCode(){
		int sum = 0;
	    for(int k = 0; k < myWords.length; k++) {
	        sum += myWords[k].hashCode();
	    }
	    return sum;
	}
	*/
	
	//** care about order **//
	
	public int hashCode(){
		myHash = 0;
		for(int k=0; k<myWords.length; k++){
			myHash = 100*myHash + myWords[k].hashCode(); 
		}
		return myHash;
	}
	
	
	//** original hashCode for object **//
	/* can't work with NullPointerException happens at EfficientWordMarkov 67 line
	public int hashCode(){
		myHash = myWords.hashCode();
		return myHash;
	}
	*/
	
	/**
	 * The equals method compares and object other 
	 * with the wordgram that method is called on.
	 * If the object other doesn't belong to wordgram class or it's null, we return false.
	 * If the object and this wordgram point to the same box, then return true.
	 * If they all belong to wordgram class, 
	 * we compare them by their instance variables myWords word by word.
	 */
	public boolean equals(Object other){
		if(! (other instanceof WordGram) || other == null){
			return false;
		}
		if(other == this){
			return true;
		}
		// why we need to cast other to wg?
		WordGram wg = (WordGram) other;
		// what if the length of two Wordgrams are different?--OutofBound
		if(wg.length() != length()){
			return false;
		}
		for(int i=0; i<length(); i++){
			if(! myWords[i].equals(wg.myWords[i]))
				return false;
		}
		return true;
	} 
	
	@Override
	/**
	 * The compareTo method compares a wordgram o 
	 * with the wordgram that this method is called on.
	 * If this precedes o in a natural order, then return negative value;
	 * if this is behind o in a natural order, then return positive value;
	 * if this equals to o, then return 0.
	 */
	public int compareTo(WordGram o) {
		// TODO Auto-generated method stub
		if(this.equals(o))
			return 0;
		if(length() > o.length()){
			return (-o.compareTo(this));
		}
		
		for(int i = 0; i < length(); i++){
			if(! myWords[i].equals(o.myWords[i])){
				return myWords[i].compareTo(o.myWords[i]);
			}
		}
		return "".compareTo(o.myWords[length()]);

//		if(length() >= o.length()){
//			for(int i=0; i<o.length(); i++){
//				if(! myWords[i].equals(o.myWords[i])){
//					return myWords[i].compareTo(o.myWords[i]);
//				}
//			}
//			return myWords[o.length()].compareTo("");
//		}else{
//			for(int i=0; i<length(); i++){
//				if(! myWords[i].equals(o.myWords[i])){
//					return myWords[i].compareTo(o.myWords[i]);
//				}
//			}
//			return ("".compareTo(o.myWords[length()]));
//		}
		/*
		int mylength = length();
		int length = o.length();
		int min = Math.min(mylength, length);
		for(int i=0; i<min; i++){
			if(! myWords[i].equals(o.myWords[i])){
				return myWords[i].compareTo(o.myWords[i]);
			}
		}
		// the value matters, or the sign matters?
		if(mylength >length){
			return 1;
		}
		return -1;
		*/
	}
	
	public int length(){
		return myWords.length;
	}
	
	public String toString(){
		if(length() ==0)
			return "{}";
		String s = "{";
		for(int i=0; i<length()-1; i++){
			s += myWords[i] + ", ";
		}
		s += myWords[length()-1] + "}";
		return s;
	}
	
	/**
	 * @param last
	 * @return wordGram with k-1 words from the base, and the last one from String last
	 */
	public WordGram shiftAdd(String last){
		ArrayList<String> newsourceList = new ArrayList<>(Arrays.asList(myWords));
		newsourceList.add(last);
		String[] newsource = newsourceList.toArray(new String[0]);
		WordGram wg = new WordGram(newsource,1,length());
		return wg;
	}
	
	public static void main(String[] args){
		String[] source = {"apple", "cat", "cat", "apple"};
		String[] source1 = {"apple", "pear", "cherry"};
		WordGram wg1 = new WordGram(source,0,2);
		WordGram wg2 = new WordGram(source,2,2);
		// why this method doesn't work on WordGram argument?
		System.out.println(wg1.toString());
		System.out.println(wg1.hashCode());
		System.out.println(wg2.hashCode());
		WordGram wg3 = new WordGram(source1,0,3);
		//System.out.println(wg3.toString());
		System.out.println(wg3.equals(wg1));
		//System.out.println("hater".compareTo(""));
		System.out.println(wg1.shiftAdd("lemon".toString()));
	}

}
