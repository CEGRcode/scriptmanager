package scriptmanager.scripts.Peak_Analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import scriptmanager.objects.CoordinateObjects.BEDCoord;
import scriptmanager.util.ExtensionFileFilter;

public class FilterBEDbyProximity{
	
	private File INPUT;
	private File OUT_BASENAME = null;
	private int CUTOFF;
	private PrintStream PS = null;
	
	public FilterBEDbyProximity(File input, File output, int cutoff, PrintStream ps) {
		INPUT = input;
		OUT_BASENAME = output;
		CUTOFF = cutoff;
		PS = ps;
	}
	
	public void run() throws FileNotFoundException, IOException, InterruptedException {
		// Construct output name
		if(OUT_BASENAME == null) {
			OUT_BASENAME = new File(ExtensionFileFilter.stripExtension(INPUT) + "_" + Integer.toString(CUTOFF) + "bp");
		}
		PrintStream OUT_Filter = new PrintStream(new File(OUT_BASENAME.getAbsoluteFile() + "-FILTER" + ".bed")); 
		PrintStream OUT_Cluster = new PrintStream(new File(OUT_BASENAME.getAbsoluteFile() + "-CLUSTER" + ".bed"));
		// Print update
		printPS("Filtering BED file with a cutoff: " + CUTOFF + " in " + INPUT.getName());
		printPS("Starting: " + getTimeStamp());
		// Open input file as stream
		InputStream inputStream = new FileInputStream(INPUT);
	    BufferedReader lines = new BufferedReader(new InputStreamReader(inputStream), 100);
	    List<BEDCoord> bedArray = new ArrayList<BEDCoord>();
	    List<Integer> failArray = new ArrayList<Integer>();
		
		//load bed coords into bedArray
	    String line;
		while((line = lines.readLine()) != null) {
			bedArray.add(new BEDCoord(line));
			bedArray.get(bedArray.size() - 1).calcMid();
			failArray.add(Integer.valueOf(0));
		}
		Collections.sort(bedArray, BEDCoord.PeakMidpointComparator);
		Collections.sort(bedArray, BEDCoord.PeakChromComparator);
		
		//Check each coord pair for proximity
		for(int i = 0; i < bedArray.size(); i++) {
			//check coords behind
			int INDEX = i - 1;
			if(INDEX >= 0) {
				while((bedArray.get(i).getChrom().equals(bedArray.get(INDEX).getChrom())) && (Math.abs(bedArray.get(i).getMid() - bedArray.get(INDEX).getMid()) <= CUTOFF)) 
				{
					if(bedArray.get(i).getScore() > bedArray.get(INDEX).getScore()) 
					{
						failArray.set(INDEX, Integer.valueOf(1));
					}
					else if((bedArray.get(i).getScore() == bedArray.get(INDEX).getScore()) && (bedArray.get(INDEX).getMid() > bedArray.get(i).getMid()))
					{
						failArray.set(INDEX, Integer.valueOf(1));
					}
					else {
						failArray.set(i, Integer.valueOf(1));
					}
					INDEX--;
					if(INDEX < 0) { break; } // To avoid redundant pair-wise checks
				}
			}
			//check coords in front
			INDEX = i + 1;
	        if(INDEX < bedArray.size()) {
	        	while((bedArray.get(i).getChrom().equals(bedArray.get(INDEX).getChrom())) && (Math.abs(bedArray.get(i).getMid() - bedArray.get(INDEX).getMid()) <= CUTOFF)) {
	        		
	        		if(bedArray.get(i).getScore() > bedArray.get(INDEX).getScore())
	        		{
	        			failArray.set(INDEX, Integer.valueOf(1));
	        		}
	        		else if((bedArray.get(i).getScore() == bedArray.get(INDEX).getScore()) && (bedArray.get(INDEX).getMid() > bedArray.get(i).getMid()))
	        		{
	        			failArray.set(INDEX, Integer.valueOf(1));
	        		}
	        		else
	        		{
	        			failArray.set(i, Integer.valueOf(1));
	        		}
	        		INDEX++;
	        		if(INDEX == bedArray.size()){ break; } // To avoid redundant pair-wise checks
	        	}
	        }
		}
		
		//print bed coords to respective files based on fail array
		for(int x = 0; x < bedArray.size(); x++) {
			if(failArray.get(x).intValue() == 0) {
				OUT_Filter.println(bedArray.get(x).toString()); 
			} else {
				OUT_Cluster.println(bedArray.get(x).toString()); 
			}
		}
		// close streams
		inputStream.close();
		OUT_Filter.close();
		OUT_Cluster.close();
		// Print update
		printPS("Completing: " + getTimeStamp());
	}

	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
	
	private void printPS(String message){
		if(PS!=null) PS.println(message);
		System.err.println(message);
	}
}