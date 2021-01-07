package scripts.Sequence_Analysis;

import htsjdk.samtools.SAMException;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import objects.CoordinateObjects.BEDCoord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import util.FASTAUtilities;

public class FASTAExtract {
	private File GENOME = null;
	private File OUTFILE = null;
	private File BED = null;
	private PrintStream OUT = null;
	private PrintStream PS = null;
	private boolean STRAND = true;
	private boolean HEADER = true;
	private boolean INDEX = true;
	
	public FASTAExtract(File gen, File b, File out, boolean str, boolean head, PrintStream ps) {
		GENOME = gen;
		BED = b;
		OUTFILE = out;
		STRAND = str;
		HEADER = head;
		PS = ps;
	}
	
	public void run() throws IOException, InterruptedException {
		
		if(PS==null) PS = System.err;
		System.out.println("STRAND:" + STRAND);
		System.out.println("COORD:" + HEADER);
		
		File FAI = new File(GENOME + ".fai");
		//Check if FAI index file exists
		if(!FAI.exists() || FAI.isDirectory()) {
			PS.println("FASTA Index file not found.\nGenerating new one...\n");
			INDEX = FASTAUtilities.buildFASTAIndex(GENOME);
		}		
		if(INDEX) {
			try{			
				IndexedFastaSequenceFile QUERY = new IndexedFastaSequenceFile(GENOME);
				PS.println("Proccessing File: " + BED.getName());
				//Open Output File
				OUT = new PrintStream(OUTFILE);
				
				ArrayList<BEDCoord> BED_Coord = loadCoord(BED);
				
				for(int y = 0; y < BED_Coord.size(); y++) {
					try {
						String seq = new String(QUERY.getSubsequenceAt(BED_Coord.get(y).getChrom(), BED_Coord.get(y).getStart() + 1, BED_Coord.get(y).getStop()).getBases());
						if(STRAND && BED_Coord.get(y).getDir().equals("-")) {
							seq = FASTAUtilities.RevComplement(seq);
						}
						OUT.println(">" + BED_Coord.get(y).getName() + "\n" + seq);
					} catch (SAMException e) {
						PS.println("INVALID COORDINATE: " + BED_Coord.get(y).toString());
					}
				}
				OUT.close();
				QUERY.close();
			} catch(IllegalArgumentException e) {
				PS.println(e.getMessage());
			} catch(FileNotFoundException e) {
				PS.println(e.getMessage());
			} catch(SAMException e) {
				PS.println(e.getMessage());
			}
		} else {
			PS.println("Genome FASTA file contains invalid lines!!!");
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
										
					if(!HEADER) { //create genomic coordinate name if requested
						if(temp.length > 5) { name = temp[0] + ":" + temp[1] + "-" + temp[2] + "(" + temp[5] + ")"; }
						else { name = temp[0] + ":" + temp[1] + "-" + temp[2] + "(.)"; }
					} else { //else create name based on BED file name or create one if non-existent
						if(temp.length > 3) { name = temp[3]; }
						else { name = temp[0] + ":" + temp[1] + "-" + temp[2] + "(" + temp[5] + ")"; }
					}
					
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