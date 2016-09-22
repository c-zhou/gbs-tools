package gbs.cz1.test;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BitsetUtil {

	public static final Map<Integer, Character> baseMap;
	static {
    	baseMap = new HashMap<Integer, Character>();
    	baseMap.put(1, 'A');
    	baseMap.put(6, 'C');
    	baseMap.put(4, 'G');
    	baseMap.put(3, 'T');
    	baseMap.put(0, 'N');
    	baseMap.put(7, 'N');
    	baseMap.put(2, 'X');
    	baseMap.put(5, 'X');
    }

	public static void main(String[] args) throws IOException {
		//rb.add(20*32-1);
		int mb = 1024*1024;
		Random random = new Random(1234);
		Map<BitSet,BitSet> map = 
				new HashMap<BitSet, BitSet>();
		BitSet bs_key, bs_val;
		Runtime instance = Runtime.getRuntime();

		System.out.println("Max Memory: "+instance.maxMemory() / mb);
		System.out.println("Total Memory: "+instance.totalMemory() / mb);
		System.out.println("Free Memory: "+instance.freeMemory() / mb);
		System.out.println("Used Memory: "+(instance.totalMemory()-instance.freeMemory()) / mb);

		long start = System.nanoTime();
		for(int i=0; i<100; i++) {
			bs_key = new BitSet(306);
			for(int k=0; k<306; k++)
				if(random.nextDouble()<.5)
					bs_key.flip(k);
			bs_val = new BitSet(6400);
			for(int k=0; k<6400; k++)
				if(random.nextDouble()<.5)
					bs_val.flip(k);
			map.put(bs_key, bs_val);
			
			Map<Integer, Character> decoder = decoder();
			StringBuilder sb = new StringBuilder();
			int b;
			for(int k=0; k<306; k+=3) {
				b = 0;
				for(int c=k; c<k+3; c++) {
					b = b << 1;
					if(bs_val.get(c))
						b  += 1;
				}
				sb.append(decoder.get(b));
			}
			
			//System.out.println(sb.toString()+"\n");
			
			int[] depth = new int[200];
			int s=0;
			for(int k=0; k<6400; k+=32) {
				for(int c=k; c<k+32; c++) {
					depth[s] = depth[s] << 1;
					if(bs_val.get(c))
						depth[s]  += 1;
				}
				s++;
			}
			//for(int k=0; k<depth.length-1; k++)
			//	System.out.print(depth[k]+",");
			//System.out.println(depth[199]+"\n");
			
			//System.out.println(bs_key.toString());
			//System.out.println(bs_val.toString());
		}

		long end = System.nanoTime();
		System.out.println("\nTime: "+(end-start)/1e9);
		System.out.println("Max Memory: "+instance.maxMemory() / mb);
		System.out.println("Total Memory: "+instance.totalMemory() / mb);
		System.out.println("Free Memory: "+instance.freeMemory() / mb);
		System.out.println("Used Memory: "+(instance.totalMemory()-instance.freeMemory()) / mb);

		
		BitSet bs2 = new BitSet(7);
		bs2.set(1);
		bs2.set(3);
		bs2.set(6);
		BitSet bs3 = (BitSet) bs2.clone();
		for(int i=0; i<7; i++) 
			if(bs2.get(i))
				System.out.print(i);
		System.out.println();
		
		
		bs3 = new BitSet(100);
		bs3.set(99);
		for(int i=0; i<bs3.length()-1; i++)
			if(random.nextBoolean())
				 bs3.set(i);
	
		BitSet bs4 = getReverseComplement(bs3);
		
		System.out.println(getSequenceFromBitSet(bs3));
		System.out.println(getSequenceFromBitSet(bs4));
	
		long[] l_bs3 = bs3.toLongArray();
		System.out.println(l_bs3.length);
		for(int i=0; i<l_bs3.length;i++)
			System.out.println(l_bs3[i]);
	
		System.out.println(str2BitSet("10101010101"));
	}
	
	private static BitSet str2BitSet(String seq) {
		// TODO Auto-generated method stub
	    BitSet bs = new BitSet(seq.length());

	    for (int i=seq.length()-1; i >= 0; i--) {
	        if (seq.charAt(i)=='1'){
	            bs.set(i);                            
	        }               
	    }
		return bs;
	}

	public static BitSet getReverseComplement(BitSet seq) {
        int len = seq.length();
    	BitSet rev = new BitSet(len);
        for (int i = 0; i < len-1; i++) 
        	if( !seq.get(len-i-2) )
        		rev.set(i);
        rev.set(len-1);
        return rev;
    }
	
	public static Map<Integer, Character> decoder() {
		Map<Integer, Character> map = new HashMap<Integer, Character>();
		map.put(0, 'N');
		map.put(1, 'A');
		map.put(2, 'G');
		map.put(3, 'T');
		map.put(4, 'C');
		map.put(5, 'T');
		map.put(6, 'S');
		map.put(7, 'B');
		return map;
	}
	
	public static String getSequenceFromBitSet(BitSet val) {
        StringBuilder seq = new StringBuilder( (val.length()-1)/3 );
        int mask;
        for (int i=0;i<val.length()-1; i+=3) {
            mask = 0;
        	for(int j=0; j<3; j++) 
        		mask = (mask << 1) + (val.get(i+j) ? 1 : 0);
        	seq.append(baseMap.get(mask));
        }
        return seq.toString();
    }
	
	

}
