package scriptmanager.scripts.Sequence_Analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import scriptmanager.util.GZipUtilities;

/**
 * This script randomizes a FASTA sequence file by shuffling the nucleotides
 * within each FASTA sequence.
 * 
 * @author William KM Lai
 * @see scriptmanager.cli.Sequence_Analysis.RandomizeFASTACLI
 * @see scriptmanager.window_interface.Sequence_Analysis.RandomizeFASTAWindow
 */
public class RandomizeFASTA {

	/**
	 * Static method to call for randomizing FASTA sequences.
	 * 
	 * @param FASTA filepath to FASTA-formatted sequences to randomize
	 * @param RANDOUT filepath to write randomized sequences to
	 * @param seed set a random seed
	 * @param gzOutput Whether to output a compressed file
	 * @return name of output filename
	 * @throws IOException
	 */
	public static File randomizeFASTA(File FASTA, File RANDOUT, Integer seed, boolean gzOutput) throws IOException {
		Random randnum = new Random();
		if( seed != null) {
			System.err.println("Set Seed=" + seed);
			randnum.setSeed(seed);
		}
		PrintStream OUT = GZipUtilities.makePrintStream(RANDOUT, gzOutput);

		// Check if file is gzipped and instantiate appropriate BufferedReader
		BufferedReader br = GZipUtilities.makeReader(FASTA);
		String line;
		while ((line = br.readLine()) != null) {
			String HEADER = line;
			OUT.println(HEADER);
			if (HEADER.contains(">")) {
				String[] SEQ = br.readLine().split("");
				ArrayList<String> SEQ_ARRAY = new ArrayList<String>();
				for (int x = 0; x < SEQ.length; x++) {
					SEQ_ARRAY.add(SEQ[x]);
				}
				for (int x = 0; x < SEQ.length; x++) {
					int randIndex = randnum.nextInt(SEQ_ARRAY.size());
					OUT.print(SEQ_ARRAY.get(randIndex));
					SEQ_ARRAY.remove(randIndex);
				}
				OUT.println();
			} else {
				OUT.println("ERROR - NOT FASTA FORMAT");
				System.err.println("ERROR - NOT FASTA FORMAT");
			}
		}

		OUT.close();
		br.close();
		return RANDOUT;
	}

}