import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.TreeSet;

/**
 *	Interface that all compression suites must implement. That is they must be
 *	able to compress a file and also reverse/decompress that process.
 * 
 *	@author Brian Lavallee
 *	@since 5 November 2015
 *  @author Owen Atrachan
 *  @since December 1, 2016
 */
public class HuffProcessor {

	public static final int BITS_PER_WORD = 8;
	public static final int BITS_PER_INT = 32;
	public static final int ALPH_SIZE = (1 << BITS_PER_WORD); // or 256
	public static final int PSEUDO_EOF = ALPH_SIZE;
	public static final int HUFF_NUMBER = 0xface8200;
	public static final int HUFF_TREE  = HUFF_NUMBER | 1;
	public static final int HUFF_COUNTS = HUFF_NUMBER | 2;

	public enum Header{TREE_HEADER, COUNT_HEADER};
	public Header myHeader = Header.TREE_HEADER;
	
	/**
	 * Compresses a file. Process must be reversible and loss-less.
	 *
	 * @param in
	 *            Buffered bit stream of the file to be compressed.
	 * @param out
	 *            Buffered bit stream writing to the output file.
	 */
	public void compress(BitInputStream in, BitOutputStream out){
//	    while (true){
//	        int val = in.readBits(BITS_PER_WORD);
//	        if (val == -1) break;
//	        
//	        out.writeBits(BITS_PER_WORD, val);
//	    }
		int first = in.readBits(BITS_PER_WORD);
		if(first == -1){
			return;
		}
		in.reset();
		int[] counts = readForCounts(in);
		HuffNode root = makeTreeFromCounts(counts);
		String[] codings = makeCodingsFromTree(root);
		writeHeader(root,out);
		
		in.reset();
		writeCompressedBits(in, codings, out);
	}

	/**
	 * Decompresses a file. Output file must be identical bit-by-bit to the
	 * original.
	 *
	 * @param in
	 *            Buffered bit stream of the file to be decompressed.
	 * @param out
	 *            Buffered bit stream writing to the output file.
	 */
	public void decompress(BitInputStream in, BitOutputStream out){
//		while (true){
//            int val = in.readBits(BITS_PER_WORD);
//            if (val == -1) break;
//            
//            out.writeBits(BITS_PER_WORD, val);
//        }
		int id = in.readBits(BITS_PER_INT);
		if(id != HUFF_NUMBER && id != HUFF_TREE){
			throw new HuffException("The file can't be decompressed!");
		}
		HuffNode root = readTreeHeader(in);
		readCompressedBits(root, in, out);
	}
	
	public void setHeader(Header header) {
        myHeader = header;
        System.out.println("header set to "+myHeader);
    }
	
	
	private HuffNode readTreeHeader(BitInputStream in){
		int bit = in.readBits(1);
		HuffNode root;
		if(bit == 0){
			HuffNode left = readTreeHeader(in);
			HuffNode right = readTreeHeader(in);
			root = new HuffNode(-1,-1,left,right);
		}else{
			int val = in.readBits(9);
			root = new HuffNode(val,1);	
		}
		return root;
	}
	
	
	private void readCompressedBits(HuffNode root, BitInputStream in, BitOutputStream out){
		HuffNode curr = root;
		while(true){
			int bit = in.readBits(1);
			if(bit == -1){
				throw new HuffException("bad input, no PSEUDO_EOF");
			}else{
				if(bit == 0){
					curr = curr.left();
				}else{
					curr = curr.right();
				}
				
				if(curr.left() == null && curr.right() == null){
					if(curr.value() == PSEUDO_EOF){
						break;
					}else{
						out.writeBits(BITS_PER_WORD, curr.value());
						curr = root;
					}
				}
			}
		}
	}
	
	
	private int[] readForCounts(BitInputStream in){
//		BitInputStream in_curr = in;
		int[] ret = new int[256];
		int bit = in.readBits(BITS_PER_WORD);
		TreeSet<Integer> alphabetSize = new TreeSet<Integer>();
		while(bit != -1){
			alphabetSize.add(bit);
			ret[bit]++;
			bit = in.readBits(BITS_PER_WORD);
		}
		System.out.println("alphabetSize: " + alphabetSize.size());
		return ret;
	}
	
	
	private HuffNode makeTreeFromCounts(int[] ret){
		PriorityQueue<HuffNode> pq = new PriorityQueue<>();
		HuffNode pseudoEOF = new HuffNode(PSEUDO_EOF,1);
		pq.add(pseudoEOF);
		for(int i = 0; i < ret.length; i++){
			if(ret[i] > 0){
				HuffNode node = new HuffNode(i,ret[i]);
				pq.add(node);
			}
		}
		
		while(pq.size() > 1){
			HuffNode left = pq.remove();
			HuffNode right = pq.remove();
			HuffNode t = new HuffNode(-1, left.weight() + right.weight(), left, right);
			pq.add(t);
		}
		
		HuffNode root = pq.remove();
		return root;
	}
	
	
	private String[] makeCodingsFromTree(HuffNode root){
		String[] ret = new String[257];
		recurse(root, "", ret);
		return ret;
	}
	
	
	private void recurse(HuffNode t, String str, String[] ret){
		if(t.left() == null && t.right() == null){
			if(t.value() == PSEUDO_EOF){
				ret[ret.length - 1] = str;
			}else{
				ret[t.value()] = str;
			}
			return;
		}
		
		for(int i = 0; i < 2; i++){
			HuffNode curr = t;
			if(i == 0){
				curr = curr.left();
				str = str + "0";
				recurse(curr, str, ret);
			}else{
				curr = curr.right();
				str = str + "1";
				recurse(curr, str, ret);
			}
			str = str.substring(0, str.length() - 1);
		}
	}
	
	
	private void writeHeader(HuffNode root, BitOutputStream out){
		out.writeBits(BITS_PER_INT, HUFF_NUMBER);
		preorderTraversal(root, out);
	}
	
	
	private void preorderTraversal(HuffNode root, BitOutputStream out){
		if(root.left() == null && root.right() == null){
			out.writeBits(1, 1);
			out.writeBits(9, root.value());
			return;
		}else{
			out.writeBits(1, 0);
			preorderTraversal(root.left(), out);
			preorderTraversal(root.right(),out);
		}		
	}
	
	
	private void writeCompressedBits(BitInputStream in, String[] codings, BitOutputStream out){
		int word = in.readBits(BITS_PER_WORD);
		while(word != -1){
			String encode = codings[word];
			out.writeBits(encode.length(), Integer.parseInt(encode, 2));
			word = in.readBits(BITS_PER_WORD);
		}
		String pseudoEOF = codings[256];
		out.writeBits(pseudoEOF.length(), Integer.parseInt(pseudoEOF, 2));
	}
}