package scripts.Data_Analysis;

import htsjdk.samtools.SAMException;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import objects.BEDCoord;
import util.DNAShapeReference;
import util.FASTAUtilities;

@SuppressWarnings("serial")
public class DNAShapePrediction extends JFrame {
	private File GENOME = null;
	private File OUTPUTPATH = null;
	private boolean[] OUTPUT_TYPE = null;
	private ArrayList<File> BED = null;

	private boolean STRAND = true;
	private boolean INDEX = true;
	
	private PrintStream OUT_M = null;
	private PrintStream OUT_P = null;
	private PrintStream OUT_H = null;
	private PrintStream OUT_R = null;
	
	static Map<String, List<Double>> STRUCTURE = null;
	
	private JTextArea textArea;
	
	public DNAShapePrediction(File gen, ArrayList<File> b, File out, boolean[] type, boolean str) {
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
		OUTPUT_TYPE = type;
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
				STRUCTURE = DNAShapeReference.InitializeStructure();
				
				for(int x = 0; x < BED.size(); x++) {
					textArea.append("Proccessing File: " + BED.get(x).getName() + "\n");
					//Open Output File
					String NAME = BED.get(x).getName().split("\\.")[0];
					if(OUTPUTPATH != null) {
						try {
							if(OUTPUT_TYPE[0]) { OUT_M = new PrintStream(new File(OUTPUTPATH.getCanonicalPath() + File.separator + NAME + "_MGW.cdt")); }
							if(OUTPUT_TYPE[1]) { OUT_P = new PrintStream(new File(OUTPUTPATH.getCanonicalPath() + File.separator + NAME + "_PTwist.cdt")); }
							if(OUTPUT_TYPE[2]) { OUT_H = new PrintStream(new File(OUTPUTPATH.getCanonicalPath() + File.separator + NAME + "_HTwist.cdt")); }
							if(OUTPUT_TYPE[3]) { OUT_R = new PrintStream(new File(OUTPUTPATH.getCanonicalPath() + File.separator + NAME + "_Roll.cdt")); }
						} catch (FileNotFoundException e) { e.printStackTrace(); }
						catch (IOException e) {	e.printStackTrace(); }
					} else {
						try {
							if(OUTPUT_TYPE[0]) { OUT_M = new PrintStream(new File(NAME + "_MGW.cdt")); }
							if(OUTPUT_TYPE[1]) { OUT_P = new PrintStream(new File(NAME + "_PropT.cdt")); }
							if(OUTPUT_TYPE[2]) { OUT_H = new PrintStream(new File(NAME + "_HelT.cdt")); }
							if(OUTPUT_TYPE[3]) { OUT_R = new PrintStream(new File(NAME + "_Roll.cdt")); }
						} catch (FileNotFoundException e) { e.printStackTrace(); }
					}
					ArrayList<BEDCoord> BED_Coord = loadCoord(BED.get(x));
					
					for(int y = 0; y < BED_Coord.size(); y++) {
						try {
							String seq = new String(QUERY.getSubsequenceAt(BED_Coord.get(y).getChrom(), BED_Coord.get(y).getStart() + 1, BED_Coord.get(y).getStop()).getBases());
							if(STRAND && BED_Coord.get(y).getDir().equals("-")) {
								seq = FASTAUtilities.RevComplement(seq);
							}
							
							List<Double> MGW = new ArrayList<Double>();
							List<Double> PropT = new ArrayList<Double>();
							List<Double> HelT = new ArrayList<Double>();
							List<Double> Roll = new ArrayList<Double>();
							
							for(int z = 0; z < seq.length() - 4; z++) {
								String key = seq.substring(z, z + 5);
								List<Double> SCORES = STRUCTURE.get(key);
								if(OUTPUT_TYPE[0]) { MGW.add(SCORES.get(0)); }
								if(OUTPUT_TYPE[1]) { PropT.add(SCORES.get(1)); }
								if(OUTPUT_TYPE[2]) { 
									if(z == 0) {
										HelT.add(SCORES.get(2));
										HelT.add(SCORES.get(3));
									} else {
										HelT.set(HelT.size() - 1, (HelT.get(HelT.size() - 1) + SCORES.get(2)) / 2);
										HelT.add(SCORES.get(3));
									}
								}
								if(OUTPUT_TYPE[3]) {
									if(z == 0) {
										Roll.add(SCORES.get(4));
										Roll.add(SCORES.get(5));
									} else {
										Roll.set(Roll.size() - 1, (Roll.get(Roll.size() - 1) + SCORES.get(4)) / 2);
										Roll.add(SCORES.get(5));
									}
								}
							}
							
							if(OUTPUT_TYPE[0]) {
								if(y == 0) {
									OUT_M.print("YORF\tNAME");
									for(int z = 0; z < MGW.size(); z++) { OUT_M.print("\t" + z); }
									OUT_M.println();
								}
								OUT_M.print(BED_Coord.get(y).getName() + "\t" + BED_Coord.get(y).getName());
								for(int z = 0; z < MGW.size(); z++) {
									OUT_M.print("\t" + MGW.get(z));							
								}
								OUT_M.println();
							}
							if(OUTPUT_TYPE[1]) {
								if(y == 0) {
									OUT_P.print("YORF\tNAME");
									for(int z = 0; z < PropT.size(); z++) { OUT_P.print("\t" + z); }
									OUT_P.println();
								}
								OUT_P.print(BED_Coord.get(y).getName() + "\t" + BED_Coord.get(y).getName());
								for(int z = 0; z < PropT.size(); z++) {
									OUT_P.print("\t" + PropT.get(z));
								}
								OUT_P.println();
							}
							if(OUTPUT_TYPE[2]) {
								if(y == 0) {
									OUT_H.print("YORF\tNAME");
									for(int z = 0; z < HelT.size(); z++) { OUT_H.print("\t" + z); }
									OUT_H.println();
								}
								OUT_H.print(BED_Coord.get(y).getName() + "\t" + BED_Coord.get(y).getName());
								for(int z = 0; z < HelT.size(); z++) {
									OUT_H.print("\t" + HelT.get(z));							
								}
								OUT_H.println();
							}
							if(OUTPUT_TYPE[3]) {
								if(y == 0) {
									OUT_R.print("YORF\tNAME");
									for(int z = 0; z < Roll.size(); z++) { OUT_R.print("\t" + z); }
									OUT_R.println();
								}
								OUT_R.print(BED_Coord.get(y).getName() + "\t" + BED_Coord.get(y).getName());
								for(int z = 0; z < Roll.size(); z++) {
									OUT_R.print("\t" + Roll.get(z));
								}
								OUT_R.println();
							}
										
						} catch (SAMException e) {
							textArea.append("INVALID COORDINATE: " + BED_Coord.get(y).toString() + "\n");
						}
					}
					if(OUT_M != null) { OUT_M.close(); }
					if(OUT_P != null) { OUT_P.close(); }
					if(OUT_H != null) { OUT_H.close(); }
					if(OUT_R != null) { OUT_R.close(); }
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