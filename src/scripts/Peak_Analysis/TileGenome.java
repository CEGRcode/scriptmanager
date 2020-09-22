package scripts.Peak_Analysis;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import objects.CoordinateObjects.BEDCoord;
import objects.CoordinateObjects.GFFCoord;
import util.GenomeSizeReference;

public class TileGenome {
	private File OUTPUT = null;
	private String GENOME = "";
	private long windowSize = 0;
	
	private boolean BEDout = true;
	private String EXTENSION = ".bed";
	
	public TileGenome(String gen, int size, boolean bed, File out) {
		GENOME = gen;
		windowSize = size;
		OUTPUT = out;
		BEDout = bed;
		if(BEDout) { EXTENSION = ".bed"; }
		else { EXTENSION = ".gff"; }
	}
	
	public void execute() throws IOException {
	    GenomeSizeReference coord = new GenomeSizeReference(GENOME);
	    
	    String fileName = GENOME + "_" + windowSize + "bp" + EXTENSION;
		PrintStream OUT = null;
		if(OUTPUT == null) OUT = new PrintStream(new File(fileName));
	    else OUT = new PrintStream(OUTPUT);
		
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
