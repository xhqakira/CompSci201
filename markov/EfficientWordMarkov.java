import java.util.*;

public class EfficientWordMarkov implements MarkovInterface<WordGram> {
	private String myText;
	private Random myRandom;
	private int myOrder;
	private String[] myWords;
	private Map myMap;
	
	private static String PSEUDO_EOS = "";
	private static long RANDOM_SEED = 1234;

	public EfficientWordMarkov(int order){
		myRandom = new Random(RANDOM_SEED);
		myOrder =order;
	}
	
	public EfficientWordMarkov(){
		this(3);
	}
	/*
	public int size() {
		return myWords.length;
	}*/

	@Override
	/**
	 * The setTraining method creates a map 
	 * mapping from N-grams(here is a wordgram class with n words) 
	 * to an arraylist of strings with elements of following words.
	 */
	public void setTraining(String text) {
		myWords = text.split("\\s+");
		// TODO Auto-generated method stub
		Map<WordGram, ArrayList<String>> map = new HashMap<>();
		//Map<WordGram, ArrayList<String>> map = new TreeMap<>();
		// the way to get subarray?
		for(int i=0; i<=myWords.length-myOrder; i++){
			WordGram current = new WordGram(myWords,i,myOrder);
			if(!map.containsKey(current)){
				map.put(current, new ArrayList<String>());
			}
			ArrayList<String> follows = map.get(current);
			if(i!=myWords.length-myOrder){
				follows.add(myWords[i+myOrder]);
			}else{
				follows.add(PSEUDO_EOS);
			}
		}
		myText = text;
		myMap = map;
		//System.out.println("mapsize:"+myMap.size());
	}

	@Override
	/**
	 * The getRandomText method gives a text with length of numWords randomly generated.
	 * The method uses current wordgram as a key to find the corresponding arraylist 
	 * and randomly choose a following word.
	 * We use k-1(myOrder = k) words from the base and randomly generated next word 
	 * to create a new current wordgram and find its next item and keep generating text. 
	 */
	public String getRandomText(int numWords) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		// can't take last Ngrams?
		int index = myRandom.nextInt(myWords.length-myOrder);

		WordGram current = new WordGram(myWords,index,myOrder);
		//System.out.printf("first random %d for '%s'\n",index,current);
		/*
		for(int i=index; i<index+myOrder; i++){
			sb.append(myWords[i]);
		}*/
		sb.append(current.toString());
		sb.append(" ");
		// really k words sequence?
		for(int k=0; k < numWords-myOrder; k++){
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
			sb.append(" ");
			current = current.shiftAdd(nextItem);
		}
		return sb.toString();
	}

	@Override
	public ArrayList<String> getFollows(WordGram key) {
		// TODO Auto-generated method stub
		ArrayList<String> follows = new ArrayList<>();
		follows = (ArrayList<String>) myMap.get(key);
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
		/*
		StringBuilder sb = new StringBuilder();
		String a = "apple banana cat dog";
		System.out.println(Arrays.toString(a.split("\\s+")));
		sb.append(Arrays.toString(a.split("\\s+")));
		System.out.println(sb.toString());
		*/
		String text = "apple banana cat dog egg apple banana egg";
		EfficientWordMarkov ewm = new EfficientWordMarkov(2);
		ewm.setTraining(text);
		WordGram wm = new WordGram("apple banana".split("\\s+"),0,2);
		System.out.println(ewm.getFollows(wm));
	}
}
