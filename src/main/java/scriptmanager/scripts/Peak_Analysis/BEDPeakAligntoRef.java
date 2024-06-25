package scriptmanager.scripts.Peak_Analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scriptmanager.objects.CoordinateObjects.BEDCoord;
import scriptmanager.util.GZipUtilities;

/**
 * Align BED peaks to a reference BED file and create a CDT file
 * 
 * @author William KM Lai
 * @see scriptmanager.cli.Peak_Analysis.BEDPeakAligntoRefCLI
 * @see scriptmanager.window_interface.Peak_Analysis.BEDPeakAligntoRefOutput
 * @see scriptmanager.window_interface.Peak_Analysis.BEDPeakAligntoRefWindow
 */
public class BEDPeakAligntoRef {
	private File refBED = null;
	private File peakBED = null;
	private PrintStream PS = null;
	private PrintStream OUT = null;

	/**
	 * Create a new instance of a BEDPeakAligntoRef script
	 * 
	 * @param ref    Reference BAM file
	 * @param peak   BAM file to be alligned
	 * @param output Output CDT file
	 * @param ps     PrintStream for reporting process
	 * @param gzOutput    whether or not to gzip output
	 * @throws IOException Invalid file or parameters
	 */
	public BEDPeakAligntoRef(File ref, File peak, File output, PrintStream ps, boolean gzOutput) throws FileNotFoundException, IOException {
		refBED = ref;
		peakBED = peak;
		PS = ps;
		OUT = GZipUtilities.makePrintStream(output, gzOutput);
	}

	/**
	 * Runs the peak alignment, writing to output file and reporting progress
	 * 
	 * @throws IOException          Invalid file or parameters
	 * @throws InterruptedException Thrown when more than one script is run at the
	 *                              same time
	 */
	public void run() throws IOException, InterruptedException {

		// Write starting message
		printPS(PS, "Mapping: " + peakBED.getName() + " to " + refBED.getName());
		printPS(PS, "Starting: " + new Timestamp(new Date().getTime()).toString());


		// =====Make peakMap=====
		Map<String, List<String>> peakMap = new HashMap<>();
		String key ;
		//Checks if BED file is compressed, creates appropriate input stream
		BufferedReader br = GZipUtilities.makeReader(peakBED);
		for (String line; (line = br.readLine()) != null; ) {
			key = new BEDCoord(line).getChrom();
			if(!peakMap.containsKey(key)) {
				peakMap.put(key, new ArrayList<String>());
				peakMap.get(key).add(line);
			} else {
				peakMap.get(key).add(line);
			}
		}
		br.close();

		// =====Overlay on Reference=====
		int counter = 0; // for tracking progress
		//Checks if BED file is compressed, creates appropriate input stream
		br = GZipUtilities.makeReader(refBED);
		for (String line; (line = br.readLine()) != null;) {
			// Parse ref BED record and chromosome
			BEDCoord refCoord = new BEDCoord(line);
			String chr = refCoord.getChrom();
			// Initialize CDT row array
			int cdtLength = (int)(refCoord.getStop() - refCoord.getStart());
			int cdtArr[] = new int[cdtLength];
			// Process one chr at a time
			if (peakMap.containsKey(chr)) {
				for (int i = 0; i < peakMap.get(chr).size(); i++) {
					// Parse peak BED record
					BEDCoord peakCoord = new BEDCoord(peakMap.get(chr).get(i));
					// Skip peak if range does not overlap ref
					if (peakCoord.getStop() < refCoord.getStart() || refCoord.getStop() <= peakCoord.getStart()) {
						continue;
					}
					// Calculate coordinate range of CDT
					int START = (int)(peakCoord.getStart() - refCoord.getStart());
					int STOP = START + (int)(peakCoord.getStop() - peakCoord.getStart());
					// Loop through CDT range
					for(int x = START; x <= STOP; x++) {
						// Check if valid coordinate range
						if(x >= 0 && x < cdtLength) {
							cdtArr[x]++;
						}
					}
				}
			}
			// Print CDT header
			if (counter == 0) {
				OUT.print("YORF" + "\t" + "NAME");
				for (int j = 0; j < cdtLength; j++) {
					OUT.print("\t" + j);
				}
				OUT.print("\n");
			}
			// Print row header
			OUT.print(refCoord.getName() + "\t" + refCoord.getName());
			// Print array contents
			if (refCoord.getDir().equals("-")) {
				for (int j = cdtLength - 1; j >= 0; j--) {
					OUT.print("\t" + cdtArr[j]);
				}
			} else {
				for (int i = 0; i < cdtLength; i++) {
					OUT.print("\t" + cdtArr[i]);
				}
			}
			OUT.print("\n");
			counter++;
			
			if(counter % 1000 == 0) {
				printPS(PS, "Reference rows processed: " + counter);
			}
		}
		// Close file streams
		br.close();
		OUT.close();
		// Print update
		printPS(PS, "Completing: " + new Timestamp(new Date().getTime()).toString());
	}

	/**
	 * Write a message to both STDERR and the provided PrintStream if PS is non-null
	 * 
	 * @param PS      the object to attempt to write to
	 * @param message the message
	 */
	private static void printPS(PrintStream PS, String message) {
		if (PS != null) {
			PS.println(message);
		}
		System.err.println(message);
	}
}