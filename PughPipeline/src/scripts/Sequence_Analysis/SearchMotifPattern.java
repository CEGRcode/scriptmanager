package scripts.Sequence_Analysis;

import java.awt.BorderLayout;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class SearchMotifPattern extends JFrame {
	private int ALLOWED_MISMATCH;
	private Map<String, String> IUPAC_HASH = new HashMap<>();
	private Map<String, String> RC_HASH = new HashMap<>();
	private String motif;
	private String motifPattern;
	private InputStream inputStream;
	private File OUTPUTPATH = null;
	private String INPUTFILE = null;
	private PrintStream OUT = null;
	
	private JTextArea textArea;
	
public SearchMotifPattern(File input, String mot, String motPattern, int num, File output) throws IOException {
	setTitle("Motif Search Progress");
	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	setBounds(150, 150, 600, 800);
	
	JScrollPane scrollPane = new JScrollPane();
	getContentPane().add(scrollPane, BorderLayout.CENTER);
	
	textArea = new JTextArea();
	textArea.setEditable(false);
	scrollPane.setViewportView(textArea);
	
	ALLOWED_MISMATCH = num;
	motif = mot;
	motifPattern = motPattern;
	inputStream = new FileInputStream(input);
	INPUTFILE = input.getName();
	
	OUTPUTPATH = output;
	String fname = motif + "_" + Integer.toString(ALLOWED_MISMATCH) + "Mismatch_" + motifPattern + "_" + input.getName().substring(0, input.getName().lastIndexOf('.')) + ".bed"; 
	if(OUTPUTPATH != null) {
		try {OUT = new PrintStream(new File(OUTPUTPATH.getCanonicalPath() + File.separator + fname)); }
		catch (FileNotFoundException e) { e.printStackTrace(); }
	} else {
		try {OUT = new PrintStream(new File(fname)); }
		catch (FileNotFoundException e) { e.printStackTrace(); }
	}
		
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

	public void run() throws IOException, InterruptedException
	{
		

		System.out.println("Motif pattern: " + motifPattern + " with " + ALLOWED_MISMATCH + " mismatch(s)");
		System.out.println("Starting: " + getTimeStamp());
		textArea.append("Searching motif: " + motif + " in " + INPUTFILE + "\n");
		textArea.append("Motif pattern: " + motifPattern + " with " + ALLOWED_MISMATCH + " mismatch(s)" + "\n");
		textArea.append("Starting: " + getTimeStamp() + "\n");
		
		char[] ORIG = motif.toUpperCase().toCharArray();
		char[] PATTERN = motifPattern.toUpperCase().toCharArray();
		List<String> MOTIF = new ArrayList<>();
		for(int i = 0; i < ORIG.length; i++)
		{	if(ORIG[i] == 'N')
				MOTIF.add("N");
			else
				MOTIF.add(IUPAC_HASH.get(Character.toString(ORIG[i])));			
		}
		List<String> RCMOTIF = new ArrayList<>();
		for(int j=ORIG.length-1; j >= 0; j--)	{
			if(ORIG[j] == 'N')
				RCMOTIF.add("N");
			else {
				String key = RC_HASH.get(Character.toString(ORIG[j]));
				RCMOTIF.add(IUPAC_HASH.get(key));				
			}
		}
		
		String currentChrom = "";
		String currentLine = ""; 
		int currentBP = 0;		
		int currentEND = 0;
		String ID;
		
	    BufferedReader lines = new BufferedReader(new InputStreamReader(inputStream), 100);
	    while(lines.ready()) {
			String line = lines.readLine().trim();
			if(line.startsWith(">")) {
				currentChrom = line.substring(1);
				currentLine = "";
				currentBP = 0;
				currentEND = currentBP + motif.length();
				System.out.println("Proccessing: " + currentChrom);
				textArea.append("Proccessing: " + currentChrom + "\n");
			}
			else {
				currentLine = currentLine + line;
				char[] array = currentLine.toCharArray();
				OUTER:
				for(int x = 0; x < array.length - motif.length(); x++) {
					char[] SEQ = currentLine.substring(x, x + ORIG.length).toCharArray();

					int dotsCount = (int) motifPattern.chars().filter(ch -> ch =='.').count();
					int MISMATCH = dotsCount;
					for(int i = 0; i < SEQ.length; i++) {
						if(PATTERN[i] == '.')
						{
							if(MOTIF.get(i).indexOf(SEQ[i]) != -1 || MOTIF.get(i).indexOf('N') != -1)
								MISMATCH--;
						}
						else {
							if(MOTIF.get(i).indexOf(SEQ[i])==-1) {
								currentBP++;
								currentEND++;
								continue OUTER;
								}
							}
						}
					if(MISMATCH <= ALLOWED_MISMATCH) {
						ID = currentChrom + "_" + Integer.toString(currentBP) + "_" + Integer.toString(currentEND) + "_+";
						OUT.print(currentChrom + "\t" + currentBP + "\t" + Integer.toString(currentEND) + "\t" + ID + "\t" + Integer.toString(MISMATCH) + "\t+\n");
					}
					
					//Reverse-complement now
					MISMATCH = SEQ.length - motifPattern.replaceAll(".", "").length();
					for(int i = 0; i < SEQ.length; i++)	{
						if(PATTERN[i] == '.')
						{
							if(RCMOTIF.get(i).indexOf(SEQ[i]) != -1 || RCMOTIF.get(i).indexOf('N') != -1)
								MISMATCH--;
						}
						else
						{
							if(RCMOTIF.get(i).indexOf(SEQ[i]) == -1) {
								MISMATCH = MISMATCH + SEQ.length;
							}
						}
					}
					if(MISMATCH <= ALLOWED_MISMATCH) {
						ID = currentChrom + "_" + Integer.toString(currentBP) + "_" + Integer.toString(currentEND) + "_-";
						OUT.print(currentChrom + "\t" + currentBP + "\t" + Integer.toString(currentEND) + "\t" + ID + "\t" + Integer.toString(MISMATCH) + "\t-\n");
					}
					currentBP++;
					currentEND++;
				}
				String tmp = currentLine.substring(currentLine.length() - motif.length());
				currentLine = tmp; 
			}
		}
		
		inputStream.close();
		lines.close();
		System.out.println("Completing: " + getTimeStamp());
		textArea.append("Completing: " + getTimeStamp() + "\n");
		
		Thread.sleep(2000);
		dispose();
	}
	
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}
