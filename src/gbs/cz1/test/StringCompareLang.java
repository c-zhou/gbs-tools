package gbs.cz1.test;

import org.apache.commons.lang3.StringUtils;

public class StringCompareLang {
	
	public static void main(String[] args) {
		
		String[][] ss = new String[2][];
		ss[0] = new String[]{"1","2","3"};
		ss[1] = new String[]{"1","2","3","4"};
		ss[0] = new String[]{"1","2","3","4","5"};
		ss[1] = new String[]{"1","2"};
		
		if(false) {
		
		String rg = "@RG     ID:F--105 PL:Illumina     PU:CTGCAG       LB:IGD  SM:F105 CN:Cornell";
		String rrg = rg.replaceAll("ID:(.*?)\\s+","ID:____ID____\t").
				replaceAll("SM:(.*?)\\s+","SM:____SM____\t");
		String nrg = rrg.replace("____ID____", "aaa").replace("____SM____", "aaa");
		System.out.println(rg);
		System.out.println(rrg);
		System.out.println(nrg);
		
		System.out.println("CHROM1".compareTo("CHROM2"));
		System.out.println("CHROM1".compareTo("CHROM12"));
		System.out.println("CHROM2".compareTo("CHROM12"));
		
		System.out.println("SSSSTTSSSSAAA".replaceAll("^SSS", ""));
		System.out.println("Itr_sc100001.1".replaceAll("^Itr_sc0{0,9}", "").replace(".1", ""));
		String a = "";
		for(int i=0; i<20; i++)
			a += "aaa:aa#aaaa:";
		a += "\n";
		String f = null;
		long start = System.nanoTime();
		for(int i=0; i<1000000; i++)
			f = a.split("#|:")[0];
		System.out.println(System.nanoTime()-start);
		System.out.println(f);
		start = System.nanoTime();
		for(int i=0; i<1000000; i++)
			f = a.substring(0,10).split("\\s+",2)[0];
		System.out.println(System.nanoTime()-start);
		System.out.println(f);
		
		
		String[] tags = new String[] {null, 
					null, null};
		System.out.println(max(tags));
		System.out.println("a".compareTo("aa"));
		String s = "DAFDADASAGATCGGAAGAGC";
		System.out.println(s.substring(0, s.indexOf("AGATCGGAAGAGC")));
		System.out.println(StringUtils.strip("NNFADFANNNN", "N"));
		}
	}
	
	private static String max(String[] tags) {
		// TODO Auto-generated method stub
		String tag = tags[0];
		for(int i=1; i<tags.length; i++) {
			if(tags[i]==null)
				continue;
			if(tag==null) {
				tag = tags[i];
				continue;
			}
			if(tags[i].length()>tag.length() || 
					tags[i].length()==tag.length() &&
					tags[i].compareTo(tag)>0) {
				tag = tags[i];
			}
		}
		return tag;
	}

}
