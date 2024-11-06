package scriptmanager.scripts.Sequence_Analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scriptmanager.objects.Exceptions.ScriptManagerException;
import scriptmanager.util.GZipUtilities;

/**
 * Create a BED coordinate file of every instance of the user-provided IUPAC
 * motif in a user-provided genomic sequence.
 * 
 * @author William KM Lai
 * @see scriptmanager.cli.Sequence_Analysis.SearchMotifCLI
 * @see scriptmanager.window_interface.Sequence_Analysis.SearchMotifOutput
 * @see scriptmanager.window_interface.Sequence_Analysis.SearchMotifWindow
 */
public class SearchMotif {

	private int ALLOWED_MISMATCH;
	private Map<String, String> IUPAC_HASH = new HashMap<>();
	private Map<String, String> RC_HASH = new HashMap<>();
	private String motif;
	private File input;
	private File out_filepath;
	private PrintStream PS;
	private boolean gzOutput;

	/**
	 * Initialize object with script inputs for generating the coordinates of the
	 * provided motif.
	 * 
	 * @param i      the reference genome sequence in FASTA-format
	 * @param mot    the IUPAC motif to search for [ATGCRYSWKMBDHVN]+
	 * @param num    the number of allowed mismatches in the motif
	 * @param output the location to save the BED-formatted coordinates of the found
	 *               motifs
	 * @param ps     where to stream the error/progress updates as the script
	 *               executes
	 * @param gz     If this is true, the output file will be gzipped.
	 */
	public SearchMotif(File i, String mot, int num, File output, PrintStream ps, boolean gz) {
		ALLOWED_MISMATCH = num;
		motif = mot;
		input = i;
		out_filepath = output;
		PS = ps;
		gzOutput = gz;

		IUPAC_HASH.put("N", "N");
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

		RC_HASH.put("N", "N");
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

	/**
	 * Execute script to search a genome for motifs. Print the header of each
	 * sequence (i.e. "chromosome" name) as they are procesed.
	 * 
	 * @throws IOException Invalid file or parameters
	 * @throws InterruptedException Thrown when more than one script is run at the same time
	 * @throws ScriptManagerException 
	 */
	public void run() throws IOException, InterruptedException, ScriptManagerException {
		PS.println("Searching motif: " + motif + " in " + input.getName());
		PS.println("Starting: " + getTimeStamp());

		char[] ORIG = motif.toUpperCase().toCharArray();
		List<String> MOTIF = new ArrayList<>();
		for (int i = 0; i < ORIG.length; i++) {
			MOTIF.add(IUPAC_HASH.get(Character.toString(ORIG[i])));
		}
		List<String> RCMOTIF = new ArrayList<>();
		for (int j = ORIG.length - 1; j >= 0; j--) {
			String key = RC_HASH.get(Character.toString(ORIG[j]));
			RCMOTIF.add(IUPAC_HASH.get(key));
		}
		
		if (RCMOTIF.size()!=MOTIF.size()) {
			throw new ScriptManagerException("Motif and reverse complement motif are different lengths.");
		}

		String currentChrom = "";
		String currentLine = "";
		int currentBP = 0;
		int currentEND = 0;

		// Initialize output writer
		PrintStream OUT = System.out;
		if (out_filepath != null) {
			OUT = GZipUtilities.makePrintStream(out_filepath, gzOutput);
		}

		// Check if file is gzipped and instantiate appropriate BufferedReader
		BufferedReader br = GZipUtilities.makeReader(input);
		// Initialize line variable to loop through
		String line = br.readLine();
		while (line != null) {
			line = line.trim();
			// Parse FASTA header
			if (line.startsWith(">")) {
				currentChrom = line.substring(1);
				currentLine = "";
				currentBP = 0;
				currentEND = currentBP + motif.length();
				PS.println("Proccessing: " + currentChrom);
			// Parse FASTA sequence
			} else {
				// Merge trailing sequence from last line with new line
				currentLine = currentLine + line;
				// Loop through each sequence nucleotide
				for (int x = 0; x < currentLine.length() - motif.length(); x++) {
					// Slice out motif-lengthed sequence to match against motif
					char[] SEQ = currentLine.substring(x, x + ORIG.length).toCharArray();
					// Track forward and reverse mismatches
					int MISMATCH = SEQ.length;
					int MISMATCH_RC = SEQ.length;
					// Slice out motif-lengthed sequence to match
					for (int i = 0; i < SEQ.length; i++) {
						for (int j = 0; j < MOTIF.get(i).length(); j++) {
							// Forward seq search
							if (SEQ[i] == MOTIF.get(i).charAt(j) || MOTIF.get(i).charAt(j) == 'N') {
								MISMATCH--;
							}
							// Reverse complement search
							if (SEQ[i] == RCMOTIF.get(i).charAt(j) || RCMOTIF.get(i).charAt(j) == 'N') {
								MISMATCH_RC--;
							}
						}
					}
					// Write forward if mismatch count passes threshold
					if (MISMATCH <= ALLOWED_MISMATCH) {
						String ID = currentChrom + "_" + Integer.toString(currentBP) + "_" + Integer.toString(currentEND) + "_+";
						OUT.println(String.join("\t", currentChrom, Integer.toString(currentBP), Integer.toString(currentEND), ID, Integer.toString(MISMATCH), "+"));
					}
					// Write rev comp if mismatch count passes threshold
					if (MISMATCH_RC <= ALLOWED_MISMATCH) {
						String ID = currentChrom + "_" + Integer.toString(currentBP) + "_" + Integer.toString(currentEND) + "_-";
						OUT.println(String.join("\t", currentChrom, Integer.toString(currentBP), Integer.toString(currentEND), ID, Integer.toString(MISMATCH_RC), "-"));
					}
					currentBP++;
					currentEND++;
				}
				// Save unsearched trailing sequence for next sequence line
				String tmp = currentLine.substring(currentLine.length() - motif.length());
				currentLine = tmp;
			}
			// Next line
			line = br.readLine();
		}
		br.close();
		OUT.close();
		PS.println("Completing: " + getTimeStamp());
	}

	/**
	 * Get the current timestamp.
	 * 
	 * @return current time as a String
	 */
	private static String getTimeStamp() {
		Date date = new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}