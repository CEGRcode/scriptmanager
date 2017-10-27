package scripts.Sequence_Analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class SearchMotif extends JFrame{
	
	private int ALLOWED_MISMATCH;
	private Map<String, String> IUPAC_HASH = new HashMap<>();
	private Map<String, String> RC_HASH = new HashMap<>();
	private String motif;
	private InputStream inputStream;
	private String OUTPUTPATH = null;
	private String INPUTFILE = null;
	private PrintStream OUT = null;
	
	
public SearchMotif(File input, String mot, int num, String output) throws IOException {
	ALLOWED_MISMATCH = num;
	motif = mot;
	inputStream = new FileInputStream(input);
	INPUTFILE = input.getName();
	
	OUTPUTPATH = output;
		String fname = motif + "_" + Integer.toString(ALLOWED_MISMATCH) + "Mismatch_" + input.getName().substring(0, input.getName().lastIndexOf('.')) + ".bed"; 
		try {OUT = new PrintStream(new File(OUTPUTPATH + File.separator + fname)); }
		catch (FileNotFoundException e) { e.printStackTrace(); }
	
	IUPAC_HASH.put("A", "A");
	IUPAC_HASH.put("T", "T");
	IUPAC_HASH.put("G", "G");
	IUPAC_HASH.put("C", "C");
	IUPAC_HASH.put("R", "AG");
	IUPAC_HASH.put("Y", "CT");
	IUPAC_HASH.put("S", "GC");
	IUPAC_HASH.put("W", "AT");
	IUPAC_HASH.put("K", "GT");
	IUPAC_HASH.put("M", "AC");
	IUPAC_HASH.put("B", "CGT");
	IUPAC_HASH.put("D", "AGT");
	IUPAC_HASH.put("H", "ACT");
	IUPAC_HASH.put("V", "ACG");
	
	RC_HASH.put("V", "B");
	RC_HASH.put("H", "D");
	RC_HASH.put("D", "H");
	RC_HASH.put("B", "V");
	RC_HASH.put("M", "K");
	RC_HASH.put("K", "M");
	RC_HASH.put("W", "W");
	RC_HASH.put("S", "S");
	RC_HASH.put("Y", "R");
	RC_HASH.put("R", "Y");
	RC_HASH.put("T", "A");
	RC_HASH.put("G", "C");
	RC_HASH.put("C", "G");
	RC_HASH.put("A", "T");
	
	}

	public void run() throws IOException
	{
		System.out.println("Searching a Motif: " + motif + " in " + INPUTFILE);
		System.out.println("Starting: " + getTimeStamp());
		
		String[] ORIG = motif.toUpperCase().split("");
		
		List<String> MOTIF = new ArrayList<>();
		for(int i=0; i< ORIG.length; i++)
		{
			if(ORIG[i] == "N")
				MOTIF.add("N");
			else
				MOTIF.add(IUPAC_HASH.get(ORIG[i]));			
		}
	//--------	
		List<String> RCMOTIF = new ArrayList<>();
		for(int j=ORIG.length-1; j>=0; j--)
		{
			if(ORIG[j] == "N")
				RCMOTIF.add("N");
			else
				{
				String key = RC_HASH.get(ORIG[j]);
				RCMOTIF.add(IUPAC_HASH.get(key));				
				}
		}
	//---------
		
		String currentChrom = "";
		StringBuffer currentLine = new StringBuffer(); 
		int currentBP = 0;
		
		int currentEND = 0;
		String ID;
		
		
	    BufferedReader lines = new BufferedReader(new InputStreamReader(inputStream), 100); 
	    
		while(lines.ready())
		{
			String line = lines.readLine().trim();
			
			if(line.startsWith(">"))
			{
				currentChrom = line.substring(1);
				currentLine.setLength(0); 
				currentBP = 0;
				currentEND = currentBP + motif.length();

			}
			else
			{
				currentLine.append(line); 
				String[] array = currentLine.toString().split(""); 
				for(int x = 0; x < (array.length)-motif.length(); x++) 
				{
					char[] SEQ = currentLine.substring(x, x+ORIG.length).toCharArray();
					int MISMATCH = SEQ.length;
					for(int i=0; i< SEQ.length; i++)
					{
						for(int j=0; j<MOTIF.get(i).length(); j++) 
						{
							if(SEQ[i] == MOTIF.get(i).charAt(j) || MOTIF.get(i).charAt(j) == 'N')
							{
								MISMATCH--;
							}
								
						}
								
					}
					if(MISMATCH == ALLOWED_MISMATCH)
					{
						ID = currentChrom + "_" + Integer.toString(currentBP) + "_" + Integer.toString(currentEND) + "_+";
						OUT.print(currentChrom + "\t" + currentBP + "\t" + Integer.toString(currentEND) + "\t" + ID + "\t.\t+\n");
					}
					
					//Reverse-complement now
					MISMATCH = SEQ.length;
					for(int i = 0; i < SEQ.length; i++)
					{
						for(int j =0; j< RCMOTIF.get(i).length(); j++)
						{
							if(SEQ[i] == RCMOTIF.get(i).charAt(j) || RCMOTIF.get(i).charAt(j) == 'N')
								MISMATCH--;
						}
					}
					if(MISMATCH == ALLOWED_MISMATCH) {
						ID = currentChrom + "_" + Integer.toString(currentBP) + "_" + Integer.toString(currentEND) + "_-";
						OUT.print(currentChrom + "\t" + currentBP + "\t" + Integer.toString(currentEND) + "\t" + ID + "\t.\t-\n");
					}
					currentBP++;
					currentEND++;
				}
				String tmp = currentLine.substring(currentLine.length()-motif.length());

				currentLine.setLength(0); 
				currentLine.append(tmp); 

			}
		}
		
		inputStream.close();
		lines.close();
		System.out.println("Completing: " + getTimeStamp());
	}
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
		}
}
