package scripts.Peak_Analysis;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JOptionPane;

import objects.CoordinateObjects.BEDCoord;
import objects.CoordinateObjects.GFFCoord;
import objects.CoordinateObjects.GenericCoord;
import util.GenomeSizeReference;

/**
 * Class with static method for creating a coordinate file of random sites across a genome.
 * 
 * @author William KM Lai
 * @see util.GenomeSizeReference
 */
public class RandomCoordinate {
	/**
	 * Create and write output BED/GFF file of random genomic intervals for a genome
	 * build. All entries will be "+" stranded, score="0.0", and the identifier
	 * column is a genomic coordinate string (\<chr\>_\<start\>_\<stop\>_\<dir\>).
	 * For the BED format, the identifier column will be the 4th column while the
	 * GFF format will use the 9th column for the identifier.
	 * 
	 * @param GENOME     the String encoding the genome build to tile (matches util.GenomeSizeReference)
	 * @param numSites   the number of random coordinate sites to sample
	 * @param windowSize the base-pair length of each coordinate interval
	 * @param BEDout     coordinate file format of output where BED-format is used if true and GFF-format used if false
	 * @param OUTPUT     the file to write the coordinate tile output to (if null, a default filename is determined using \<GENOME\>_\<numSites\>SITES_\<windowSize\>bp.\<ext\>)
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static void execute(String GENOME, int numSites, int windowSize, boolean BEDout, File OUTPUT) throws IOException, IllegalArgumentException {
		GenomeSizeReference coord = new GenomeSizeReference(GENOME);
	    if(!coord.isSmaller(windowSize)) {
	    	System.err.println("Window size is too large for selected genome!!!\n");
	    	JOptionPane.showMessageDialog(null, "Invalid Window Size Entered!!!");
	    } else {
	    	String EXTENSION = ".gff";
			if (BEDout) {
				EXTENSION = ".bed";
			}
		    String randomName = GENOME + "_" + numSites + "SITES_" + windowSize + "bp" + EXTENSION;
		    PrintStream OUT = null;
			if (OUTPUT == null) {
				OUT = new PrintStream(randomName);
			} else {
				OUT = new PrintStream(OUTPUT);
			}
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
