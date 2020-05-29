package scripts.Peak_Analysis;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JOptionPane;

import objects.CoordinateObjects.BEDCoord;
import objects.CoordinateObjects.GFFCoord;
import objects.CoordinateObjects.GenericCoord;
import util.GenomeSizeReference;

public class RandomCoordinate {
	private File OUTPUT = null;
	private String GENOME = "";
	private int numSites = 0;
	private int windowSize = 0;
	
	private boolean BEDout = true;
	private String EXTENSION = ".bed";
	
	public RandomCoordinate(String gen, int sites, int size, boolean bed, File out) {
		GENOME = gen;
		numSites = sites;
		windowSize = size;
		OUTPUT = out;
		BEDout = bed;
		if(BEDout) { EXTENSION = ".bed"; }
		else { EXTENSION = ".gff"; }
	}
	
	public void execute() throws IOException {
	    GenomeSizeReference coord = new GenomeSizeReference(GENOME);
	    if(!coord.isSmaller(windowSize)) {
	    	System.err.println("Window size is too large for selected genome!!!\n");
	    	JOptionPane.showMessageDialog(null, "Invalid Window Size Entered!!!");
	    } else {
		    String randomName = GENOME + "_" + numSites + "SITES_" + windowSize + "bp" + EXTENSION;
		    PrintStream OUT = null;
		    if(OUTPUT == null) OUT = new PrintStream(randomName);
		    else OUT = new PrintStream(OUTPUT);
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
