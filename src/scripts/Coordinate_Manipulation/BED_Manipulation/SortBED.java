package scripts.Coordinate_Manipulation.BED_Manipulation;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import objects.BEDCoord;
import util.JTVOutput;

public class SortBED {
	public static void sortBEDbyCDT(String outname, File bed, File cdt, int START_INDEX, int STOP_INDEX) throws IOException {
		ArrayList<BEDCoord> SORT = new ArrayList<BEDCoord>();
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
				SORT.add(new BEDCoord(ID[0], count));
				CDTFile.put(ID[0], line);
			} else { CDTHeader = line; }
		}
		scan.close();
		//Sort by score
		Collections.sort(SORT, BEDCoord.ScoreComparator);
		
		//Output sorted CDT File
		String newCDT = outname + ".cdt";
		PrintStream OUT = null;
	    OUT = new PrintStream(newCDT);
	    OUT.println(CDTHeader);
	    for(int x = 0; x < SORT.size(); x++) {
	    	OUT.println(CDTFile.get(SORT.get(x).getName()));
	    }
	    OUT.close();
		CDTFile = null; //Free up memory by getting CDT file out of memory
		JTVOutput.outputJTV(outname, "green");
		
		//Match to bed file after
		HashMap<String, String> BEDFile = new HashMap<String, String>();
		scan = new Scanner(bed);
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			String ID = line.split("\t")[3];
			if(!ID.contains("YORF") && !ID.contains("NAME")) {
				BEDFile.put(ID, line);
			}
		}
		scan.close();
		//Output sorted BED File
		String newBED = outname +".bed";    
	    OUT = new PrintStream(newBED);
	    for(int x = 0; x < SORT.size(); x++) {
	    	OUT.println(BEDFile.get(SORT.get(x).getName()));
	    }
	    OUT.close();
	}
}
