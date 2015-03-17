package util;

public class NucleotideUtilities {
	public static String RevComplement(String SEQ) {
		SEQ = SEQ.toUpperCase();
		String RC = "";
		for (int x = 0; x < SEQ.length(); x++){
			if(SEQ.charAt(x) == 'A') { RC = 'T' + RC; }
			else if(SEQ.charAt(x) == 'T') { RC = 'A' + RC; }
			else if(SEQ.charAt(x) == 'G') { RC = 'C' + RC; }
			else if(SEQ.charAt(x) == 'C') { RC = 'G' + RC; }
			else { RC = 'N' + RC; }
		}
		return RC;
	}
	
	public static boolean parseStringforInvalideNuc(String seq) {
		seq = seq.toUpperCase();
		char[] check = seq.toCharArray();
		for(int x = 0; x < check.length; x++) {
			if(check[x] != 'A' && check[x] != 'T' && check[x] != 'G' && check[x] != 'C') {
				return false;
			}
		}
		return true;
	}
}
