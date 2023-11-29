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

		printPS("Mapping: " + peakBED.getName() + " to " + refBED.getName());
		printPS("Starting: " + new Timestamp(new Date().getTime()).toString());
		
		int counter = 0;
		//Checks if BED file is compressed, creates appropriate input stream
		BufferedReader br = GZipUtilities.makeReader(peakBED);
		
		String key ;
//--------------
		Map<String, List<String>> peakMap = new HashMap<>();
		for (String line; (line = br.readLine()) != null; ) {
			key = line.split("\t")[0];
			if(!peakMap.containsKey(key)) {
				peakMap.put(key, new ArrayList<String>());
				peakMap.get(key).add(line);
			} else {
				peakMap.get(key).add(line + "\t");
			}
		}
		br.close();
	
//============
		//Checks if BED file is compressed, creates appropriate input stream
		br = GZipUtilities.makeReader(refBED);
		for (String line; (line = br.readLine()) != null;) {
			String[] str = line.split("\t");
			String chr = str[0];
			String[] peakLine;
			int cdtLength = (Integer.parseInt(str[2])) - (Integer.parseInt(str[1]));
			int cdtArr[] = new int[cdtLength];
			if (peakMap.containsKey(chr)) {
				for (int i = 0; i < peakMap.get(chr).size(); i++) {
					peakLine = peakMap.get(chr).get(i).split("\t");
					if (Integer.parseInt(peakLine[1]) <= Integer.parseInt(str[2]) && Integer.parseInt(peakLine[1]) >= Integer.parseInt(str[1])) {
						int START = Integer.parseInt(peakLine[1]) - Integer.parseInt(str[1]);
						int STOP = START + (Integer.parseInt(peakLine[2]) - Integer.parseInt(peakLine[1]));
						for(int x = START; x <= STOP; x++) {
							if(x >= 0 && x < cdtLength) { cdtArr[x]++; }
						}
					} else if (Integer.parseInt(peakLine[2]) >= Integer.parseInt(str[1]) && Integer.parseInt(peakLine[2]) <= Integer.parseInt(str[2])) {
						int START = Integer.parseInt(peakLine[1]) - Integer.parseInt(str[1]);
						int STOP = START + (Integer.parseInt(peakLine[2]) - Integer.parseInt(peakLine[1]));
						for (int c = START; c <= STOP; c++) {
							if (c >= 0 && c < cdtLength) {
								cdtArr[c]++;
							}
						}
					}
				}
			}
			if (counter == 0) {
				OUT.print("YORF" + "\t" + "NAME");
				for (int j = 0; j < cdtLength; j++) {
					OUT.print("\t" + j);
				}
				OUT.print("\n");
			}
			OUT.print(str[3] + "\t" + str[3]);
			if (str[5].equalsIgnoreCase("+")) {
				for (int i = 0; i < cdtLength; i++) {
					OUT.print("\t" + cdtArr[i]);
				}
			} else {
				for (int j = cdtLength - 1; j >= 0; j--) {
					OUT.print("\t" + cdtArr[j]);
				}
			}
			OUT.print("\n");
			counter++;
			
			if(counter % 1000 == 0) {
				printPS("Reference rows processed: " + counter);
			}
		}
		br.close();
		OUT.close();
		// Print update
		printPS("Completing: " + new Timestamp(new Date().getTime()).toString());
	}

	private void printPS(String message){
		if(PS!=null) PS.println(message);
		System.err.println(message);
	}
}