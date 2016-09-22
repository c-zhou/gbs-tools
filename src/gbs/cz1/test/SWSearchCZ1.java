package gbs.cz1.test;

import gbs.cz1.model.ParseBarcodeRead;

public class SWSearchCZ1 {

	public static void main(String[] args) {
		long start = System.nanoTime();
		int k = 0;
		for(int i=0; i<1; i++) {
			k=ParseBarcodeRead.SWSearch(
					"TGCAGCAGGGTCAGAGTCAGCAGAAGTTTCAATCGGTAATCACCCAATGTACCTTATTCTGCATTATACTGCAGATCGGAAGAGCGGTTCAGCAGG", 
					"CTGCAAGATCGGAAGA");
		}
		
		long end = System.nanoTime();
		System.out.println(end-start);
		System.out.println(k);
	}
}
