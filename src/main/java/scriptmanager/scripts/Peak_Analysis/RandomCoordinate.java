package scriptmanager.scripts.Peak_Analysis;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import scriptmanager.objects.CoordinateObjects.BEDCoord;
import scriptmanager.objects.CoordinateObjects.GFFCoord;
import scriptmanager.objects.CoordinateObjects.GenericCoord;
import scriptmanager.objects.CustomExceptions.OptionException;
import scriptmanager.util.GenomeSizeReference;

/**
 * Class with static method for creating a coordinate file of random sites across a genome.
 * 
 * @author William KM Lai
 * @see scriptmanager.util.GenomeSizeReference
 * @see scriptmanager.cli.Peak_Analysis.RandomCoordinateCLI
 * @see scriptmanager.window_interface.Peak_Analysis.RandomCoordinateWindow
 */
public class RandomCoordinate {
	/**
	 * Create and write output BED/GFF file of random genomic intervals for a genome
	 * build. All entries will be "+" stranded, score="0.0", and the identifier
	 * column is a genomic coordinate string
	 * (&lt;chr&gt;_&lt;start&gt;_&lt;stop&gt;_&lt;dir&gt;). For the BED format, the
	 * identifier column will be the 4th column while the GFF format will use the
	 * 9th column for the identifier.
	 * 
	 * @param GENOME     the String encoding the genome build to tile (matches
	 *                   util.GenomeSizeReference)
	 * @param OUTPUT     the file to write the coordinate tile output to (if null, a
	 *                   default filename is determined using
	 *                   &lt;GENOME&gt;_&lt;numSites&gt;SITES_&lt;windowSize&gt;bp.&lt;ext&gt;)
	 * @param BEDout     coordinate file format of output where BED-format is used
	 *                   if true and GFF-format used if false
	 * @param numSites   the number of random coordinate sites to sample
	 * @param windowSize the base-pair length of each coordinate interval
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static void execute(String GENOME, File OUTPUT, boolean BEDout, int numSites, int windowSize) throws IOException, IllegalArgumentException, OptionException {
		GenomeSizeReference coord = new GenomeSizeReference(GENOME);
		String EXTENSION = BEDout ? ".bed" : ".gff";
		String fileName = GENOME + "_" + numSites + "SITES_" + windowSize + "bp" + EXTENSION;
		if (!coord.isSmaller(windowSize)) {
			throw new OptionException("Invalid Window Size Entered - window size is too large for selected genome!!!");
		} else {
			PrintStream OUT = null;
			if (OUTPUT == null) {
				OUT = new PrintStream(fileName);
			} else {
				OUT = new PrintStream(OUTPUT);
			}
			// Iterate each random sample
		    for(int x = 0; x < numSites; x++) {
		    	GenericCoord temp = coord.generateRandomCoord(windowSize);
		    	if(BEDout) {
		    		BEDCoord outcoord = new BEDCoord(temp.getChrom(), temp.getStart(), temp.getStop(), temp.getDir(), temp.getName());
		    		OUT.println(outcoord.toString());
		    	} else {
		    		GFFCoord outcoord = new GFFCoord(temp.getChrom(), temp.getStart(), temp.getStop(), temp.getDir(), temp.getName());
		    		OUT.println(outcoord.toString());
		    	}
		    }
			OUT.close();
		}
	}
}
