package scripts.Coordinate_Manipulation.GFF_Manipulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import objects.CoordinateObjects.GFFCoord;
import util.JTVOutput;

public class SortGFF {
	public static void sortGFFbyCDT(String outname, File gff, File cdt, int START_INDEX, int STOP_INDEX) throws IOException {
		ArrayList<GFFCoord> SORT = new ArrayList<GFFCoord>();
		HashMap<String, String> CDTFile = new HashMap<String, String>();
		String CDTHeader = "";
		//Parse CDT File first
		Scanner scan = new Scanner(cdt);
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			String[] ID = line.split("\t");
			if(!ID[0].contains("YORF") && !ID[0].contains("NAME")) {
				double count = 0;
				for(int x = 2 + START_INDEX; x < STOP_INDEX + 2; x++) {
					count += Double.parseDouble(ID[x]);
				}
				SORT.add(new GFFCoord(ID[0], count));
				CDTFile.put(ID[0], line);
			} else { CDTHeader = line; }
		}
		scan.close();
		//Sort by score
		Collections.sort(SORT, GFFCoord.ScoreComparator);
		
		//Output sorted CDT File
		String newCDT = outname + ".cdt";
		PrintStream OUT = new PrintStream(newCDT);
	    OUT.println(CDTHeader);
	    for(int x = 0; x < SORT.size(); x++) {
	    	OUT.println(CDTFile.get(SORT.get(x).getName()));
	    }
	    OUT.close();
		CDTFile = null; //Free up memory by getting CDT file out of memory
		JTVOutput.outputJTV(outname, "green");
		
		//Match to gff file after
		HashMap<String, String> GFFFile = new HashMap<String, String>();
		scan = new Scanner(gff);
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			String ID = line.split("\t")[8];
			if(!ID.contains("YORF") && !ID.contains("NAME")) {
				GFFFile.put(ID, line);
			}
		}
		scan.close();
		//Output sorted GFF File
		String newGFF = outname +".gff";    
	    OUT = new PrintStream(newGFF);
	    for(int x = 0; x < SORT.size(); x++) {
	    	OUT.println(GFFFile.get(SORT.get(x).getName()));
	    }
	    OUT.close();
	}
	
	// This function is almost exactly copied from window/*/SortBEDWindow & window/*/SortGFFWindow & scripts/*/SortBED...good practice to merge at some point.
	public static Integer parseCDTFile(File CDT) throws FileNotFoundException {
		Scanner scan = new Scanner(CDT);
		int currentSize = -999;
		boolean consistentSize = true;
		int currentRow = 1;
		while (scan.hasNextLine()) {
			String[] temp = scan.nextLine().split("\t");
			if(!temp[0].contains("YORF") && !temp[0].contains("NAME")) {
				int tempsize = temp.length - 2;
				if(currentSize == -999) { currentSize = tempsize; }
				else if(currentSize != tempsize) {
					System.err.println("Invalid Row at Index: " + currentRow);
					consistentSize = false;
					scan.close();
				}
				currentRow++;
			}
		}
		scan.close();
		if(consistentSize) { return currentSize; }
		else { return -999; }
	}
}
