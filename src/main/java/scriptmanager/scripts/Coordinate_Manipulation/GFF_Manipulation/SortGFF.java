package scriptmanager.scripts.Coordinate_Manipulation.GFF_Manipulation;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import scriptmanager.objects.CoordinateObjects.GFFCoord;

/**
 * This class contains scripts for sorting coordinate intervals (BED/GFF) by the tag counts of a CDT matrix file.
 *
 * @author William KM Lai
 * @see scriptmanager.cli.Coordinate_Manipulation.GFF_Manipulation.SortGFFCLI
 * @see scriptmanager.window_interface.Coordinate_Manipulation.GFF_Manipulation.SortGFFWindow
 */
public class SortGFF {
	/**
	 * Sort a GFF file by the values from a CDT matrix file. 
	 * 
	 * @param outname Filepath basename (without ext) to save the sorted GFF (&lt;basename&gt;.gff) and sorted CDT (&lt;basename&gt;.cdt) files.
	 * @param gff input GFF file to sort
	 * @param cdt input CDT file with values to sort by
	 * @param START_INDEX the start column to consider when summing values to sort
	 * @param STOP_INDEX The last column to consider when summing values (non-inclusive)
	 */
	public static void sortGFFbyCDT(String outname, File gff, File cdt, int START_INDEX, int STOP_INDEX)
			throws IOException {
		ArrayList<GFFCoord> SORT = new ArrayList<GFFCoord>();
		HashMap<String, String> CDTFile = new HashMap<String, String>();
		String CDTHeader = "";
		// Parse CDT File first
		Scanner scan = new Scanner(cdt);
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			String[] ID = line.split("\t");
			if (!ID[0].contains("YORF") && !ID[0].contains("NAME")) {
				double count = 0;
				for (int x = 2 + START_INDEX; x < STOP_INDEX + 2; x++) {
					count += Double.parseDouble(ID[x]);
				}
				SORT.add(new GFFCoord(ID[0], count));
				CDTFile.put(ID[0], line);
			} else {
				CDTHeader = line;
			}
		}
		scan.close();
		// Sort by score
		Collections.sort(SORT, GFFCoord.ScoreComparator);

		// Output sorted CDT File
		String newCDT = outname + ".cdt";
		PrintStream OUT = new PrintStream(newCDT);
		OUT.println(CDTHeader);
		for (int x = 0; x < SORT.size(); x++) {
			OUT.println(CDTFile.get(SORT.get(x).getName()));
		}
		OUT.close();
		CDTFile = null; // Free up memory by getting CDT file out of memory

		// Match to gff file after
		HashMap<String, String> GFFFile = new HashMap<String, String>();
		scan = new Scanner(gff);
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			String ID = line.split("\t")[8].split(";")[0];
			if (!ID.contains("YORF") && !ID.contains("NAME")) {
				GFFFile.put(ID, line);
			}
		}
		scan.close();
		// Output sorted GFF File
		String newGFF = outname + ".gff";
		OUT = new PrintStream(newGFF);
		for (int x = 0; x < SORT.size(); x++) {
			OUT.println(GFFFile.get(SORT.get(x).getName()));
		}
		OUT.close();
	}

}