package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import objects.CustomExceptions.FASTAException;

public class FASTAUtilities {
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
	
	/*
	 * Adapted from:
	 * https://github.com/mdshw5/pyfaidx/blob/master/pyfaidx/__init__.py
	 * pyfaidx python program for manipulating fasta files efficiently
	 *
	 *	contig_name\tcontig_length\toffset_distance_from_last_contig\tcolumnlength\tcolumnlength_with_endline\n"
     *	chr1    230218  6       60      61 
     *	chr2    813184  234067  60      61 
     */
    public static void buildFASTAIndex(File fasta) throws IOException, FASTAException {
	    	ArrayList<String> IMPROPER_FASTA = new ArrayList<String>();
	    	long counter = 0;
	
	    	String contig = "";
	    	long binaryOffset = 0;
	    	long currentOffset = 0;
	    	long contigLength = 0;
	    	long column_Length = 0;
	    	long untrimmed_Column_Length = 0;
	    	    	
	    	BufferedReader b_read = new BufferedReader(new FileReader(fasta));
	    	LineReader reader = new LineReader(b_read);
	    	PrintStream FAI = new PrintStream(fasta.getCanonicalPath() + ".fai");
	    	
	    	String strLine = "";
	    	while(!(strLine = reader.readLine()).equals("")) {
	    		//Pull parameters line
	    		long current_untrimmed_Column_Length = strLine.length();
	    		long current_column_Length = strLine.trim().length();
	
			if(strLine.contains(">")) {
				if(IMPROPER_FASTA.size() > 1) {
					System.out.println("Unequal column size FASTA Line at:");
					for(int z = 0; z < IMPROPER_FASTA.size(); z++) {	System.out.println(contig + "\t" + IMPROPER_FASTA.get(z));	}
					FAI.close();
					new File(fasta.getName() + ".fai").delete();
					throw new FASTAException(fasta);
				}
				if(counter > 0) { FAI.println(contig + "\t" + contigLength + "\t" + currentOffset + "\t" + column_Length + "\t" + untrimmed_Column_Length);	}
				//Reset parameters for new contig
				untrimmed_Column_Length = 0;
				contigLength = 0;
				column_Length = 0;
				contig = strLine.trim().substring(1);
				binaryOffset += current_untrimmed_Column_Length;
				currentOffset = binaryOffset;
				IMPROPER_FASTA = new ArrayList<String>();
			} else {
				if(untrimmed_Column_Length == 0) { untrimmed_Column_Length = current_untrimmed_Column_Length; }
				if(column_Length == 0) { column_Length = current_column_Length;	}
				binaryOffset += current_untrimmed_Column_Length;
				contigLength += current_column_Length;
					
				//Check to make sure all the columns are equal. Index is invalid otherwise
				if(current_untrimmed_Column_Length != untrimmed_Column_Length || current_untrimmed_Column_Length == 0) { IMPROPER_FASTA.add(strLine.trim());	}
			}
			counter++;
	    	}
		FAI.println(contig + "\t" + contigLength + "\t" + currentOffset + "\t" + column_Length + "\t" + untrimmed_Column_Length);
		b_read.close();
	    	FAI.close();
	    	
		System.out.println("Genome Index Built");
		return;
    }
}
