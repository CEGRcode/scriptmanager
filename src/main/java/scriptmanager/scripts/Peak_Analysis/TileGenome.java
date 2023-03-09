package scripts.Peak_Analysis;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import objects.CoordinateObjects.BEDCoord;
import objects.CoordinateObjects.GFFCoord;
import util.GenomeSizeReference;

/**
 * Class with static method for creating a coordinate file of tiles across a genome.
 * 
 * @author William KM Lai
 * @see util.GenomeSizeReference
 */
	public class TileGenome {

	/**
	 * Create and write output BED/GFF file of genomic intervals tiling the genome.
	 * All entries will be "+" stranded, score="0.0", and the identifier column is a
	 * genomic coordinate string (\<chr\>_\<start\>_\<stop\>_\<dir\>). For the BED format,
	 * the identifier column will be the 4th column while the GFF format will use
	 * the 9th column for the identifier.
	 * 
	 * @param GENOME     the String encoding the genome build to tile (matches util.GenomeSizeReference)
	 * @param windowSize the base-pair length of the tiles
	 * @param BEDout     coordinate file format of output where BED-format is used if true and GFF-format used if false
	 * @param OUTPUT     the file to write the coordinate tile output to (if null, a default filename is determined using \<GENOME\>_\<windowSize\>bp.\<ext\>)
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static void execute(String GENOME, int windowSize, boolean BEDout, File OUTPUT) throws IOException, IllegalArgumentException {
		GenomeSizeReference coord = new GenomeSizeReference(GENOME);
		String EXTENSION = ".gff";
		if (BEDout) {
			EXTENSION = ".bed";
		}
		String fileName = GENOME + "_" + windowSize + "bp" + EXTENSION;
		PrintStream OUT = null;
		if (OUTPUT == null) {
			OUT = new PrintStream(new File(fileName));
		} else {
			OUT = new PrintStream(OUTPUT);
		}

		for(int x = 0; x < coord.getChrom().size(); x++) {
			String CHROM = coord.getChrom().get(x);
			long SIZE = coord.getChromSize().get(x);
			for(long y = 0; y < SIZE; y += windowSize) {
				long STOP = y + windowSize;
				//If beyond the edge of chromosome, set end to size
				if(STOP > SIZE) { STOP = SIZE; }
				if(BEDout) {
		    		BEDCoord outcoord = new BEDCoord(CHROM, y, STOP, "+");
		    		OUT.println(outcoord.toString());
		    	} else {
		    		GFFCoord outcoord = new GFFCoord(CHROM, y + 1, STOP, "+");
		    		OUT.println(outcoord.toString());
		    	}
			}
	    }
		OUT.close();
	}
}
