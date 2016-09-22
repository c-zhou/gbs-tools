package gbs.cz1.test;

import java.util.BitSet;

import gbs.cz1.core.BaseEncoder;

public class BaseEncoderGBS {
	public static void main(String[] args) {
		
		
		String seq = "ATCGAGTNNTCTGAGAAATTTTGGCACACTACATNAAN";
		BitSet bs = BaseEncoder.getBitSetFromSeq(seq);
		BitSet bs_r = BaseEncoder.getReverseComplement(bs);
		System.out.println(BaseEncoder.getSequenceFromBitSet(bs));
		System.out.println(BaseEncoder.getSequenceFromBitSet(bs_r));
		
		
		long l = BaseEncoder.getLongFromSeq("TATGT");
		System.out.println(l);
		long ll = l>>3*(BaseEncoder.chunkSize-5);
		System.out.println(ll);
		long mask = 7;
		while(ll>0) {
			System.out.print(ll&mask);
			ll = ll>>3;
		}
	}
}
