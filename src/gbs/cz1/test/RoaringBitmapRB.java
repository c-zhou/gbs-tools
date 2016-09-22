package gbs.cz1.test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.roaringbitmap.RoaringBitmap;



public class RoaringBitmapRB {

	public static void main(String[] args) throws IOException {
		//rb.add(20*32-1);
		int mb = 1024*1024;
		Random random = new Random(1234);
		Map<RoaringBitmap,RoaringBitmap> map = 
				new HashMap<RoaringBitmap, RoaringBitmap>();
		RoaringBitmap rb_key, rb_val;
		Runtime instance = Runtime.getRuntime();

		System.out.println("Max Memory: "+instance.maxMemory() / mb);
		System.out.println("Total Memory: "+instance.totalMemory() / mb);
		System.out.println("Free Memory: "+instance.freeMemory() / mb);
		System.out.println("Used Memory: "+(instance.totalMemory()-instance.freeMemory()) / mb);

		long start = System.nanoTime();
		for(int i=0; i<10; i++) {
			rb_key = RoaringBitmap.bitmapOf();
			for(int k=0; k<306; k++)
				if(random.nextDouble()<.5)
					rb_key.add(k);
			rb_val = RoaringBitmap.bitmapOf();
			for(int k=0; k<6400; k++)
				if(random.nextDouble()<.5)
					rb_val.add(k);
			map.put(rb_key, rb_val);

			System.out.println(rb_key.toString());
			System.out.println(rb_val.toString());
		}

		long end = System.nanoTime();
		System.out.println("\nTime: "+(end-start)/1e9);
		System.out.println("Max Memory: "+instance.maxMemory() / mb);
		System.out.println("Total Memory: "+instance.totalMemory() / mb);
		System.out.println("Free Memory: "+instance.freeMemory() / mb);
		System.out.println("Used Memory: "+(instance.totalMemory()-instance.freeMemory()) / mb);

	}


}
