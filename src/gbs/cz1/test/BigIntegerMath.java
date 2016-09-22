package gbs.cz1.test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BigIntegerMath {

	private BigInteger bi3 = new BigInteger("8");
	private BigInteger bi0 = new BigInteger("7");
	private BigInteger bi = new BigInteger(new byte[40]);
	
	public static void main(String[] args) throws IOException {
		//rb.add(20*32-1);
		int mb = 1024*1024;
		Random random = new Random(1234);
		Map<BigInteger,BigInteger> map = 
				new HashMap<BigInteger, BigInteger>();
		BigInteger bi_key, bi_val;
		Runtime instance = Runtime.getRuntime();

		System.out.println("Max Memory: "+instance.maxMemory() / mb);
		System.out.println("Total Memory: "+instance.totalMemory() / mb);
		System.out.println("Free Memory: "+instance.freeMemory() / mb);
		System.out.println("Used Memory: "+(instance.totalMemory()-instance.freeMemory()) / mb);
		long start = System.nanoTime();
		for(int i=0; i<100000; i++) {
			bi_key = new BigInteger(new byte[40]);
			for(int k=0; k<300; k++) {
				bi_key = bi_key.shiftLeft(1);
				if(random.nextDouble()<.5)
					bi_key = bi_key.add(new BigInteger("1"));
			}
			bi_val = new BigInteger(new byte[800]);
			for(int k=0; k<6400; k++) {
				bi_val = bi_val.shiftLeft(1);
				if(random.nextDouble()<.5)
					bi_val.add(new BigInteger("1"));
			}
			map.put(bi_key, bi_val);
		}
		long end = System.nanoTime();
		System.out.println("\nTime: "+(end-start)/1e9);
		
		System.out.println("Max Memory: "+instance.maxMemory() / mb);
		System.out.println("Total Memory: "+instance.totalMemory() / mb);
		System.out.println("Free Memory: "+instance.freeMemory() / mb);
		System.out.println("Used Memory: "+(instance.totalMemory()-instance.freeMemory()) / mb);

	}
	
	public static void main2(String[] args) {
		BigIntegerMath bim = new BigIntegerMath();
		//bim.print();
		//System.out.println(bim.bi.bitCount());
		long start = System.nanoTime();
		
		for(int r=0; r<1; r++) {
			bim.set("7");
			
			for(int i=0; i<100; i++) {
				bim.shiftLeft1(1);
			}

			int n=0;
			int k=0;
			
			//System.out.println(bim.bi.bitLength());
			while( n!=7) {
				n = bim.getRight();
				bim.shiftRight();
				System.out.println(++k +"\t"+n);
			}
		}
		
		long end = System.nanoTime();
		System.out.println(end-start);
		System.out.println(bim.bi.intValue());
		
		//byte[] b = bim.bi.toByteArray();
		//for(int i=0; i<b.length; i++)
		//	System.out.println(b[i]);
		//for(int i=0; i<18; i++) 
		//	bim.print();
	}
	
	public void set(String val) {
		this.bi = new BigInteger(val);
	}
	
	public void print() {
		System.out.println(bi);
	}
	
	public void shiftLeft() {
		bi = bi.shiftLeft(3);
	}
	
	public void shiftLeft(int n) {
		bi = bi.shiftLeft(3).or(new BigInteger(""+n));
	}
	
	public void shiftLeft1(int n) {
		bi = bi.shiftLeft(3).add(new BigInteger(""+n));
	}
	
	public int getRight() {
		return bi.and(bi0).intValue();
	}
	
	public void shiftRight() {
		bi = bi.shiftRight(3);
	}
}
