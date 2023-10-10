package scriptmanager.scripts.Coordinate_Manipulation.GFF_Manipulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import scriptmanager.objects.CoordinateObjects.GFFCoord;
import scriptmanager.util.GZipUtilities;

public class SortGFF {
	public static void sortGFFbyCDT(String outname, File gff, File cdt, int START_INDEX, int STOP_INDEX, boolean gzOutput)
			throws IOException {
		ArrayList<GFFCoord> SORT = new ArrayList<GFFCoord>();
		HashMap<String, String> CDTFile = new HashMap<String, String>();
		String CDTHeader = "";
		// Check if file is gzipped and instantiate appropriate BufferedReader
		BufferedReader br = GZipUtilities.makeReader(cdt);
		// Parse CDT File first
		String line = br.readLine();
		while (line != null) {
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
			line = br.readLine();
		}
		br.close();
		// Sort by score
		Collections.sort(SORT, GFFCoord.ScoreComparator);

		// Output sorted CDT File
		PrintStream OUT = GZipUtilities.makePrintStream(new File(outname + ".cdt"), gzOutput);
		OUT.println(CDTHeader);
		for (int x = 0; x < SORT.size(); x++) {
			OUT.println(CDTFile.get(SORT.get(x).getName()));
		}
		OUT.close();
		CDTFile = null; // Free up memory by getting CDT file out of memory

		// Match to gff file after
		HashMap<String, String> GFFFile = new HashMap<String, String>();
		// Check if file is gzipped and instantiate appropriate BufferedReader
		br = GZipUtilities.makeReader(gff);
		// Initialize line variable to loop through
		line = br.readLine();
		while (line != null) {
			String ID = line.split("\t")[3];
			if (!ID.contains("YORF") && !ID.contains("NAME")) {
				GFFFile.put(ID, line);
			}
			line = br.readLine();
		}
		br.close();

		// Output sorted GFF File
		OUT = GZipUtilities.makePrintStream(new File(outname + ".gff"), gzOutput);
		for (int x = 0; x < SORT.size(); x++) {
			OUT.println(GFFFile.get(SORT.get(x).getName()));
		}
		OUT.close();
	}

}