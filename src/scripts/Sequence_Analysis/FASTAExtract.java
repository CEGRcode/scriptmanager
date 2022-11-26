package scripts.Sequence_Analysis;

import htsjdk.samtools.SAMException;
import htsjdk.samtools.reference.FastaSequenceIndexCreator;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import objects.CoordinateObjects.BEDCoord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import util.FASTAUtilities;
import util.BEDUtilities;

public class FASTAExtract {
	private File GENOME = null;
	private File OUTFILE = null;
	private File BED = null;
	private PrintStream OUT = null;
	private PrintStream PS = null;
	private boolean STRAND = true;
	private boolean HEADER = true;

	public FASTAExtract(File gen, File b, File out, boolean str, boolean head, PrintStream ps)
			throws IOException {
		GENOME = gen;
		BED = b;
		OUTFILE = out;
		STRAND = str;
		HEADER = head;
		PS = ps;

		File FAI = new File(GENOME + ".fai");
		// Check if FAI index file exists
		if (!FAI.exists() || FAI.isDirectory()) {
			FastaSequenceIndexCreator.create(GENOME.toPath(), true);
		}
	}

	public void run() throws IOException, InterruptedException {
		PS.println("STRAND:" + STRAND);
		PS.println("COORD:" + HEADER);

		try {
			IndexedFastaSequenceFile QUERY = new IndexedFastaSequenceFile(GENOME);
			PS.println("Proccessing File: " + BED.getName());
			// Open Output File
			OUT = new PrintStream(OUTFILE);

			ArrayList<BEDCoord> BED_Coord = BEDUtilities.loadCoord(BED, HEADER);

			for (int y = 0; y < BED_Coord.size(); y++) {
				try {
					String seq = new String(QUERY.getSubsequenceAt(BED_Coord.get(y).getChrom(),
							BED_Coord.get(y).getStart() + 1, BED_Coord.get(y).getStop()).getBases());
					if (STRAND && BED_Coord.get(y).getDir().equals("-")) {
						seq = FASTAUtilities.RevComplement(seq);
					}
					OUT.println(">" + BED_Coord.get(y).getName() + "\n" + seq);
				} catch (SAMException e) {
					PS.println("INVALID COORDINATE: " + BED_Coord.get(y).toString());
				}
			}
			OUT.close();
			QUERY.close();
		} catch (IllegalArgumentException e) {
			PS.println(e.getMessage());
		} catch (FileNotFoundException e) {
			PS.println(e.getMessage());
		} catch (SAMException e) {
			PS.println(e.getMessage());
		}
	}
}