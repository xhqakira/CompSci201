import java.util.*;

public class EfficientMarkov implements MarkovInterface<String> {
	private String myText;
	private Random myRandom;
	private int myOrder;
	private Map<String, ArrayList<String>> myMap;
	
	private static String PSEUDO_EOS = "";
	private static long RANDOM_SEED = 1234;
	
	public EfficientMarkov(int order){
		myRandom = new Random(RANDOM_SEED);
		myOrder =order;
	}
	
	public EfficientMarkov(){
		this(3);
	}
	public int size() {
		return myText.length();
	}
	
	@Override
	/**
	 * The setTraining method creates a map 
	 * mapping n-gram keys to an arraylist of strings 
	 * with element of the following characters.
	 */
	public void setTraining(String text) {
		// TODO Auto-generated method stub
		Map<String, ArrayList<String>> map = new HashMap<>();
		for(int i = 0; i <= text.length()-myOrder; i++){
			String current = text.substring(i, i+myOrder);
			if(!map.containsKey(current)){
				map.put(current, new ArrayList<String>());
			}
			ArrayList<String> follows = map.get(current);
			if(i != text.length()-myOrder){
				follows.add(String.valueOf(text.charAt(i+myOrder)));
			}else{
				follows.add(PSEUDO_EOS);
			}
		}
		myText = text;
		myMap = map;
		//System.out.println(myMap);
	}

	@Override
	public String getRandomText(int length) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		// why we set it to be corner case? 
		// To guarantee that don't occur myRandom.nextInt(0) which is wrong
		// random.nextInt(n), n must be positive.
		if(myText.length() == myOrder){
			return "";
		}
		int index = myRandom.nextInt(myText.length() - myOrder);

		String current = myText.substring(index, index + myOrder);
		//System.out.printf("first random %d for '%s'\n",index,current);
		sb.append(current);
		for(int k=0; k < length-myOrder; k++){
			ArrayList<String> follows = getFollows(current);
			if (follows.size() == 0){
				break;
			}
			index = myRandom.nextInt(follows.size());
			
			String nextItem = follows.get(index);
			if (nextItem.equals(PSEUDO_EOS)) {
				//System.out.println("PSEUDO");
				break;
			}
			sb.append(nextItem);
			current = current.substring(1)+ nextItem;
		}
		return sb.toString();
	}

	@Override
	public ArrayList<String> getFollows(String key) {
		// TODO Auto-generated method stub
		ArrayList<String> follows = myMap.get(key);
		return follows;
	}

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return myOrder;
	}

	@Override
	public void setSeed(long seed) {
		// TODO Auto-generated method stub
		RANDOM_SEED = seed;
		myRandom = new Random(RANDOM_SEED);	

	}
	public static void main(String[] args){
		EfficientMarkov em = new EfficientMarkov();
		em.setTraining("bbbabbabbbbaba");
		//System.out.println(myMap);
		System.out.println(em.getFollows("aba").size());
		
	}

}
