package gbs.cz1.test;

public class QualS {
	
	public static void main(String[] args) {
		
		String qualS = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHI";
		System.out.println(qualS.length());
		System.out.println((char)64);
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<41; i++) 
			sb.append((char)(33+i));
		System.out.println(sb.toString());
		System.out.println(qualS);
	}

}
