package scriptmanager.scripts.Read_Analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import scriptmanager.util.ArrayUtilities;
import scriptmanager.util.ExtensionFileFilter;
import scriptmanager.util.GZipUtilities;

/**
 * Combine multiple TAB/CDT files into site-specific scores using a given
 * operation
 * 
 * @author William KM Lai
 * @see scriptmanager.cli.Read_Analysis.AggregateDataCLI
 * @see scriptmanager.window_interface.Read_Analysis.AggregateDataWindow
 */
public class AggregateData {

	private ArrayList<File> INPUT = null;
	private File OUT_PATH = null;
	private boolean MERGE = true;
	private int ROWSTART = 1;
	private int COLSTART = 1;
	private int METRIC = 0;
	private PrintStream OUT;
	private String endMessage = "";
	private boolean OUTPUT_GZIP;

	/**
	 * Creates a new instance of the AggregateData script
	 * 
	 * @param in    ArrayList of TAB files to be processed
	 * @param out   Output directory
	 * @param m     Whether results should be merged into one file
	 * @param r     Starting row (1-indexed)
	 * @param c     Starting column (1-indexed)
	 * @param index Operation to be performed (0 = sum, 1 = average, 2 = median, 3 =
	 *              mode, 4 = min, 5 = max, 6 = positional variance)
	 * @param gzOutput   whether or not to gzip output
	 */
	public AggregateData(ArrayList<File> in, File out, boolean m, int r, int c, int index, boolean gzOutput) {
		INPUT = in;
		OUT_PATH = out;
		MERGE = m;
		ROWSTART = r;
		COLSTART = c;
		METRIC = index;
		OUTPUT_GZIP = gzOutput;
	}

	/**
	 * Runs the aggregation with the specified parameters
	 * 
	 * @throws IOException Invalid file or parameters
	 */
	public void run() throws IOException {
		if (!MERGE) {
			if (OUT_PATH == null || OUT_PATH.isDirectory()) {
				System.err.println(INPUT.get(0));
				for (int x = 0; x < INPUT.size(); x++) {
					outputFileScore(INPUT.get(x));
// 					firePropertyChange("file", x, x + 1);
				}
			} else if (INPUT.size() == 1) {
				outputFileScore(INPUT.get(0));
			} else {
				System.err.println("Cannot accept non-directory filename with multi-file input when merge is not flagged.");
			}
		} else {
			ArrayList<ArrayList<Double>> MATRIX = new ArrayList<ArrayList<Double>>();
			ArrayList<ArrayList<String>> MATRIXID = new ArrayList<ArrayList<String>>();
			for (int x = 0; x < INPUT.size(); x++) {
				// Check if file is gzipped and instantiate appropriate BufferedReader
				BufferedReader br = GZipUtilities.makeReader(INPUT.get(x));
				ArrayList<Double> scorearray = new ArrayList<Double>();
				ArrayList<String> idarray = new ArrayList<String>();
				int count = 0;
				String line;
				while ((line = br.readLine()) != null) {
					// Skip lines until desired row start
					while (count < ROWSTART) {
						line = br.readLine();
						count++;
					}

					// Split array, assume first element is ID, irrespective of column start
					String[] ID = line.split("\t");
					idarray.add(ID[0]);

					double[] numarray = new double[ID.length];
					for (int y = COLSTART - 1; y < ID.length; y++) {
						try {
							numarray[y] = Double.parseDouble(ID[y]);
						} catch (NumberFormatException nfe) {
							numarray[y] = Double.NaN;
						}
					}
					if (METRIC == 0) {
						scorearray.add(ArrayUtilities.getSum(numarray));
					} else if (METRIC == 1) {
						scorearray.add(ArrayUtilities.getAverage(numarray));
					} else if (METRIC == 2) {
						scorearray.add(ArrayUtilities.getMedian(numarray));
					} else if (METRIC == 3) {
						scorearray.add(ArrayUtilities.getMode(numarray));
					} else if (METRIC == 4) {
						scorearray.add(ArrayUtilities.getMin(numarray));
					} else if (METRIC == 5) {
						scorearray.add(ArrayUtilities.getMax(numarray));
					} else if (METRIC == 6) {
						scorearray.add(ArrayUtilities.getPositionalVariance(numarray));
					}
					count++;
				}
				br.close();
				MATRIX.add(scorearray);
				MATRIXID.add(idarray);
			}

			String name = "ALL_SCORES.out";
			if (OUT_PATH == null) {
				OUT = GZipUtilities.makePrintStream(new File(name + (OUTPUT_GZIP? ".gz": "")), OUTPUT_GZIP);
			} else if (!OUT_PATH.isDirectory()) {
				OUT = GZipUtilities.makePrintStream(OUT_PATH, OUTPUT_GZIP);
			} else {
				OUT = GZipUtilities.makePrintStream(new File(OUT_PATH.getCanonicalPath() + File.separator + name + (OUTPUT_GZIP? ".gz": "")), OUTPUT_GZIP);
			}

			// Check all arrays are the same size
			int ARRAYLENGTH = MATRIX.get(0).size();
			boolean ALLSAME = true;
			for (int x = 0; x < MATRIX.size(); x++) {
				if (MATRIX.get(x).size() != ARRAYLENGTH || MATRIXID.get(x).size() != ARRAYLENGTH) {
					ALLSAME = false;
					endMessage = "Different number of rows between:\n" + INPUT.get(0).getName() + "\n"
							+ INPUT.get(x).getName();
					return;
				}
			}

			System.err.println(getMessage());

			if (ALLSAME) {
				for (int x = 0; x < INPUT.size(); x++) {
					OUT.print("\t" + INPUT.get(x).getName());
				}
				OUT.println();
				for (int x = 0; x < MATRIX.get(0).size(); x++) {
					OUT.print(MATRIXID.get(0).get(x));
					for (int y = 0; y < MATRIX.size(); y++) {
						OUT.print("\t" + MATRIX.get(y).get(x));
					}
					OUT.println();
				}
			}
			OUT.close();
		}
		endMessage = "Data Parsed";
	}

	/**
	 * Returns "Data Parsed," or an error message if script failed
	 * @return "Data Parsed," or an error message if script failed
	 */
	public String getMessage() {
		return (endMessage);
	}

	/**
	 * Calls {@link AggregateData#outputFileScore(File, PrintStream)} but outputs results to a new file if a PrintStream is not provided
	 * @param IN Input file (used to generate output file's name)
	 * @throws FileNotFoundException Script could not find valid input file
	 * @throws IOException Invalid file or parameters
	 */
	public void outputFileScore(File IN) throws FileNotFoundException, IOException {
		String NEWNAME = ExtensionFileFilter.stripExtension(IN) + "_SCORES.out";
		if (OUT_PATH != null) {
			OUT = GZipUtilities.makePrintStream(new File(OUT_PATH.getAbsolutePath() + File.separator + NEWNAME + (OUTPUT_GZIP? ".gz": "")), OUTPUT_GZIP);
		} else {
			OUT = GZipUtilities.makePrintStream(new File(NEWNAME + (OUTPUT_GZIP? ".gz": "")), OUTPUT_GZIP);
		}
		outputFileScore(IN, OUT);
	}

	/**
	 * Outputs the first value in a given row and the result of the selected operation for each line of a file
	 * @param IN TAB file to be scored
	 * @param OUT Print stream to output scores
	 * @throws FileNotFoundException Script could not find valid input file
	 * @throws IOException Invalid file or parameters
	 */
	public void outputFileScore(File IN, PrintStream OUT) throws FileNotFoundException, IOException {
		if (METRIC == 0) {
			OUT.println("\tSum");
		} else if (METRIC == 1) {
			OUT.println("\tAverage");
		} else if (METRIC == 2) {
			OUT.println("\tMedian");
		} else if (METRIC == 3) {
			OUT.println("\tMode");
		} else if (METRIC == 4) {
			OUT.println("\tMin");
		} else if (METRIC == 5) {
			OUT.println("\tMax");
		} else if (METRIC == 6) {
			OUT.println("\tPositionalVariance");
		}

		// Check if file is gzipped and instantiate appropriate BufferedReader
		BufferedReader br = GZipUtilities.makeReader(IN);
		int count = 0;
		String line;
		while ((line = br.readLine()) != null) {
			// Skip lines until desired row start
			while (count < ROWSTART) {
				line = br.readLine();
				count++;
			}

			String[] ID = line.split("\t");
			double[] numarray = new double[ID.length];
			for (int x = COLSTART - 1; x < ID.length; x++) {
				try {
					numarray[x] = Double.parseDouble(ID[x]);
				} catch (NumberFormatException nfe) {
					numarray[x] = Double.NaN;
				}
			}
			if (METRIC == 0) {
				OUT.println(ID[0] + "\t" + ArrayUtilities.getSum(numarray));
			} else if (METRIC == 1) {
				OUT.println(ID[0] + "\t" + ArrayUtilities.getAverage(numarray));
			} else if (METRIC == 2) {
				OUT.println(ID[0] + "\t" + ArrayUtilities.getMedian(numarray));
			} else if (METRIC == 3) {
				OUT.println(ID[0] + "\t" + ArrayUtilities.getMode(numarray));
			} else if (METRIC == 4) {
				OUT.println(ID[0] + "\t" + ArrayUtilities.getMin(numarray));
			} else if (METRIC == 5) {
				OUT.println(ID[0] + "\t" + ArrayUtilities.getMax(numarray));
			} else if (METRIC == 6) {
				OUT.println(ID[0] + "\t" + ArrayUtilities.getPositionalVariance(numarray));
			}
			count++;
		}
		br.close();
		OUT.close();
	}
}