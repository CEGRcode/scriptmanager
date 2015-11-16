package scripts.Sequence_Analysis;

import htsjdk.samtools.SAMException;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import objects.BEDCoord;
import util.FASTAUtilities;

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
			INDEX = FASTAUtilities.buildFASTAIndex(GENOME);
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
								seq = FASTAUtilities.RevComplement(seq);
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
}