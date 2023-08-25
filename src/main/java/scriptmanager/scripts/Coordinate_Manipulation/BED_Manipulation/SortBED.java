package scriptmanager.scripts.Coordinate_Manipulation.BED_Manipulation;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import scriptmanager.objects.CoordinateObjects.BEDCoord;
import scriptmanager.util.GZipUtilities;

/**
 * This class contains scripts for sorting coordinate intervals (BED/GFF) by the tag counts of a CDT matrix file.
 *
 * @author William KM Lai
 * @see scriptmanager.cli.Coordinate_Manipulation.BED_Manipulation.SortBEDCLI
 * @see scriptmanager.window_interface.Coordinate_Manipulation.BED_Manipulation.SortBEDWindow
 */
public class SortBED {
	/**
	 * Sort a BED file by the values from a CDT matrix file. Includes Gzip support.
	 * 
	 * @param outbase Filepath basename (without ext) to save the sorted BED (&lt;basename&gt;.bed) and sorted CDT (&lt;basename&gt;.cdt) files.
	 * @param bed input BED file to sort
	 * @param cdt input CDT file with values to sort by
	 * @param START_INDEX the start column to consider when summing values to sort
	 * @param STOP_INDEX
	 * @param gzOutput if true, the output files will be gzipped.
	 * @throws IOException
	 */
	public static void sortBEDbyCDT(String outbase, File bed, File cdt, int START_INDEX, int STOP_INDEX, boolean gzOutput ) throws IOException {
		ArrayList<BEDCoord> SORT = new ArrayList<BEDCoord>();
		HashMap<String, String> CDTFile = new HashMap<String, String>();
		String CDTHeader = "";
		// Check if file is gzipped and instantiate appropriate BufferedReader
		BufferedReader br = GZipUtilities.makeReader(cdt);
		// Initialize line variable to loop through
		String line = br.readLine();
		while (line != null) {
			String[] ID = line.split("\t");
			if (!ID[0].contains("YORF") && !ID[0].contains("NAME")) {
				double count = 0;
				for (int x = 2 + START_INDEX; x < STOP_INDEX + 2; x++) {
					count += Double.parseDouble(ID[x]);
				}
				SORT.add(new BEDCoord(ID[0], count));
				CDTFile.put(ID[0], line);
			} else {
				CDTHeader = line;
			}
			line = br.readLine();
		}
		br.close();
		// Sort by score
		Collections.sort(SORT, BEDCoord.ScoreComparator);

		PrintStream OUT;
		// Initialize output writer
		OUT = GZipUtilities.makePrintStream(new File(outbase + ".cdt"), gzOutput);
		// Output sorted CDT File
		OUT.println(CDTHeader);
		for (int x = 0; x < SORT.size(); x++) {
			OUT.println(CDTFile.get(SORT.get(x).getName()));
		}
		OUT.close();
		CDTFile = null; // Free up memory by getting CDT file out of memory

		// Match to bed file after
		HashMap<String, String> BEDFile = new HashMap<String, String>();

		// Check if file is gzipped and instantiate appropriate BufferedReader
		if(GZipUtilities.isGZipped(bed)) {
			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(bed)), "UTF-8"));
		} else {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(bed), "UTF-8"));
		}
		// Initialize line variable to loop through
		line = br.readLine();
		while (line != null) {
			String ID = line.split("\t")[3];
			if (!ID.contains("YORF") && !ID.contains("NAME")) {
				BEDFile.put(ID, line);
			}
			line = br.readLine();
		}
		br.close();

		// Initialize output writer
		if (gzOutput) {
			OUT = new PrintStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(outbase + ".bed.gz"))));
		} else {
			OUT = new PrintStream(new BufferedOutputStream(new FileOutputStream(outbase + ".bed")));
		}
		// Output sorted BED File
		for (int x = 0; x < SORT.size(); x++) {
			OUT.println(BEDFile.get(SORT.get(x).getName()));
		}
		OUT.close();
	}
}
