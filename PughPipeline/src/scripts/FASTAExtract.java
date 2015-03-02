package scripts;

import htsjdk.samtools.SAMException;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import objects.BEDCoord;
import util.LineReader;

/*
 * Adapted from:
 * https://github.com/mdshw5/pyfaidx/blob/master/pyfaidx/__init__.py
 * pyfaidx python program for manipulating fasta files efficiently
 */

@SuppressWarnings("serial")
public class FASTAExtract extends JFrame {
	private File GENOME = null;
	private File OUTPUTPATH = null;
	private ArrayList<File> BED = null;
	private PrintStream OUT = null;
	private boolean STRAND = true;
	private boolean INDEX = true;
	
	private JTextArea textArea;
	
	public FASTAExtract(File gen, ArrayList<File> b, File out, boolean str) {
		setTitle("FASTA Extraction Progress");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		GENOME = gen;
		BED = b;
		OUTPUTPATH = out;
		STRAND = str;
	}
	
	public void run() throws IOException, InterruptedException {
		File FAI = new File(GENOME + ".fai");
		//Check if FAI index file exists
		if(!FAI.exists() || FAI.isDirectory()) {
			textArea.append("FASTA Index file not found.\nGenerating new one...\n");
			INDEX = buildFASTAIndex(GENOME);
		}		
		if(INDEX) {
			try{			
				IndexedFastaSequenceFile QUERY = new IndexedFastaSequenceFile(GENOME);
				for(int x = 0; x < BED.size(); x++) {
					textArea.append("Proccessing File: " + BED.get(x).getName() + "\n");
					//Open Output File
					String NAME = BED.get(x).getName().split("\\.")[0] + ".fa";
					if(OUTPUTPATH != null) {
						try { OUT = new PrintStream(new File(OUTPUTPATH.getCanonicalPath() + File.separator + NAME)); }
						catch (FileNotFoundException e) { e.printStackTrace(); }
						catch (IOException e) {	e.printStackTrace(); }
					} else {
						try { OUT = new PrintStream(new File(NAME)); }
						catch (FileNotFoundException e) { e.printStackTrace(); }
					}
					
					ArrayList<BEDCoord> BED_Coord = loadCoord(BED.get(x));
					
					for(int y = 0; y < BED_Coord.size(); y++) {
						try {
							String seq = new String(QUERY.getSubsequenceAt(BED_Coord.get(y).getChrom(), BED_Coord.get(y).getStart() + 1, BED_Coord.get(y).getStop()).getBases());
							if(STRAND && BED_Coord.get(y).getDir().equals("-")) {
								seq = RevComplement(seq);
							}
							OUT.println(">" + BED_Coord.get(y).getName() + "\n" + seq);
						} catch (SAMException e) {
							textArea.append("INVALID COORDINATE: " + BED_Coord.get(y).toString() + "\n");
						}
					}
					OUT.close();
			        firePropertyChange("fa",x, x + 1);	
				}
				QUERY.close();
				textArea.append("Extraction Complete\n");
			} catch(IllegalArgumentException e) {
				textArea.append(e.getMessage());
			} catch(FileNotFoundException e) {
				textArea.append(e.getMessage());
			} catch(SAMException e) {
				textArea.append(e.getMessage());
			}
		} else {
			textArea.append("Genome FASTA file contains invalid lines!!!\n");
		}
	}
	
	public String RevComplement (String SEQ) {
		String RC = "";
		for (int x = 0; x < SEQ.length(); x++){
			if(SEQ.charAt(x) == 'A') { RC = 'T' + RC; }
			if(SEQ.charAt(x) == 'T') { RC = 'A' + RC; }
			if(SEQ.charAt(x) == 'G') { RC = 'C' + RC; }
			if(SEQ.charAt(x) == 'C') { RC = 'G' + RC; }
		}
		return RC;
	}
	
    public ArrayList<BEDCoord> loadCoord(File INPUT) throws FileNotFoundException {
		Scanner scan = new Scanner(INPUT);
		ArrayList<BEDCoord> COORD = new ArrayList<BEDCoord>();
		while (scan.hasNextLine()) {
			String[] temp = scan.nextLine().split("\t");
			if(temp.length > 2) { 
				if(!temp[0].contains("track") && !temp[0].contains("#")) {
					String name = "";
					if(temp.length > 3) { name = temp[3]; }
					else { name = temp[0] + "_" + temp[1] + "_" + temp[2]; }
					if(Integer.parseInt(temp[1]) >= 0) {
						if(temp[5].equals("+")) { COORD.add(new BEDCoord(temp[0], Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), "+", name)); }
						else { COORD.add(new BEDCoord(temp[0], Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), "-", name)); }
					} else {
						System.out.println("Invalid Coordinate in File!!!\n" + Arrays.toString(temp));
					}
				}
			}
		}
		scan.close();
		return COORD;
    }

    //contig_name\tcontig_length\toffset_distance_from_last_contig\tcolumnlength\tcolumnlength_with_endline\n"
    //chr1    230218  6       60      61
    //chr2    813184  234067  60      61
    public boolean buildFASTAIndex(File fasta) throws IOException {
    	textArea.append(getTimeStamp() + "\nBuilding Genome Index...\n");
    	
    	boolean properFASTA = true;
    	ArrayList<String> IMPROPER_FASTA = new ArrayList<String>();
    	int counter = 0;

    	String contig = "";
    	int binaryOffset = 0;
    	int currentOffset = 0;
    	int contigLength = 0;
    	int column_Length = 0;
    	int untrimmed_Column_Length = 0;
    	    	
    	BufferedReader b_read = new BufferedReader(new FileReader(fasta));
    	LineReader reader = new LineReader(b_read);
    	PrintStream FAI = new PrintStream(fasta.getName() + ".fai");
    	
    	String strLine = "";
    	while(!(strLine = reader.readLine()).equals("")) {
    		//Pull parameters line
    		int current_untrimmed_Column_Length = strLine.length();
			int current_column_Length = strLine.trim().length();

			if(strLine.contains(">")) {
				if(IMPROPER_FASTA.size() > 1) {
					textArea.append("Unequal column size FASTA Line at:\n");
					for(int z = 0; z < IMPROPER_FASTA.size(); z++) {	textArea.append(contig + "\t" + IMPROPER_FASTA.get(z) + "\n");	}
					properFASTA = false;
					break;
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
    	
		if(properFASTA) textArea.append("Genome Index Built\n" + getTimeStamp() + "\n");
		else { new File(fasta.getName() + ".fai").delete(); }
		
		return properFASTA;
    }
    
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}