package scriptmanager.scripts.Sequence_Analysis;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import scriptmanager.charts.CompositePlot;
import scriptmanager.util.DNAShapeReference;
import scriptmanager.util.GZipUtilities;

/**
 * This script calculates the various aspects of DNA shape across a set of FASTA
 * sequences.
 * 
 * @author William KM Lai
 * @see scriptmanager.util.DNAShapeReference
 * @see scriptmanager.cli.Sequence_Analysis.DNAShapefromFASTACLI
 * @see scriptmanager.window_interface.Sequence_Analysis.DNAShapefromFASTAOutput
 * @see scriptmanager.window_interface.Sequence_Analysis.DNAShapefromFASTAWindow
 */
public class DNAShapefromFASTA {
	private String OUTBASENAME = null;
	private boolean[] OUTPUT_TYPE = null;
	private File FASTA = null;

	private PrintStream OUT_M = null;
	private PrintStream OUT_P = null;
	private PrintStream OUT_H = null;
	private PrintStream OUT_R = null;

	private PrintStream[] PS = null;

	static Map<String, List<Double>> STRUCTURE = null;

	double[] AVG_MGW = null;
	double[] AVG_PropT = null;
	double[] AVG_HelT = null;
	double[] AVG_Roll = null;

	Component chart_M = null;
	Component chart_P = null;
	Component chart_H = null;
	Component chart_R = null;

	/**
	 * Initialize object with script inputs for generating DNA shape reports.
	 * 
	 * @param fa   the FASTA-formatted file with a fixed sequence length
	 * @param out  the output file name base (to add _&lt;shapetype&gt;.cdt suffix to)
	 * @param type a four-element boolean list for specifying shape type to output
	 *             (no enforcement on size)
	 * @param ps   list of four PrintStream objects corresponding to each shape type
	 *             (for GUI)
	 */
	public DNAShapefromFASTA(File fa, String out, boolean[] type, PrintStream[] ps) {
		FASTA = fa;
		OUTBASENAME = out;
		OUTPUT_TYPE = type;
		PS = ps;

		STRUCTURE = DNAShapeReference.InitializeStructure();
	}

	/**
	 * Execute script to calculate DNA shape for all types across the input
	 * sequence.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void run() throws IOException, InterruptedException {
		String NAME = FASTA.getName().split("\\.")[0];
		String time = getTimeStamp(); // Generate TimeStamp
		for (int p = 0; p < PS.length; p++) {
			if (OUTPUT_TYPE[p] && PS[p] != null) {
				PS[p].println(time + "\n" + NAME);
			}
		}
		openOutputFiles();

		int counter = 0;
		String line;
		// Check if file is gzipped and instantiate appropriate BufferedReader
		BufferedReader br;
		if(GZipUtilities.isGZipped(FASTA)) {
			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(FASTA)), "UTF-8"));
		} else {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(FASTA), "UTF-8"));
		}
		while ((line = br.readLine()) != null) {
			String HEADER = line;
			if (HEADER.contains(">")) {
				HEADER = HEADER.substring(1, HEADER.length());
				String seq = br.readLine();
				if (!seq.contains("N")) {
					// Populate array for each FASTA line
					List<Double> MGW = new ArrayList<Double>();
					List<Double> PropT = new ArrayList<Double>();
					List<Double> HelT = new ArrayList<Double>();
					List<Double> Roll = new ArrayList<Double>();
					for (int z = 0; z < seq.length() - 4; z++) {
						String key = seq.substring(z, z + 5);
						List<Double> SCORES = STRUCTURE.get(key);
						if (OUTPUT_TYPE[0]) {
							MGW.add(SCORES.get(0));
						}
						if (OUTPUT_TYPE[1]) {
							PropT.add(SCORES.get(1));
						}
						if (OUTPUT_TYPE[2]) {
							if (z == 0) {
								HelT.add(SCORES.get(2));
								HelT.add(SCORES.get(3));
							} else {
								HelT.set(HelT.size() - 1, (HelT.get(HelT.size() - 1) + SCORES.get(2)) / 2);
								HelT.add(SCORES.get(3));
							}
						}
						if (OUTPUT_TYPE[3]) {
							if (z == 0) {
								Roll.add(SCORES.get(4));
								Roll.add(SCORES.get(5));
							} else {
								Roll.set(Roll.size() - 1, (Roll.get(Roll.size() - 1) + SCORES.get(4)) / 2);
								Roll.add(SCORES.get(5));
							}
						}
					}

					if (OUTPUT_TYPE[0]) {
						if (counter == 0) {
							OUT_M.print("YORF\tNAME");
							for (int z = 0; z < MGW.size(); z++) {
								OUT_M.print("\t" + z);
							}
							OUT_M.println();
							AVG_MGW = new double[MGW.size()];
						}
						AVG_MGW = printVals(HEADER, MGW, AVG_MGW, OUT_M);
					}
					if (OUTPUT_TYPE[1]) {
						if (counter == 0) {
							OUT_P.print("YORF\tNAME");
							for (int z = 0; z < PropT.size(); z++) {
								OUT_P.print("\t" + z);
							}
							OUT_P.println();
							AVG_PropT = new double[PropT.size()];
						}
						AVG_PropT = printVals(HEADER, PropT, AVG_PropT, OUT_P);
					}
					if (OUTPUT_TYPE[2]) {
						if (counter == 0) {
							OUT_H.print("YORF\tNAME");
							for (int z = 0; z < HelT.size(); z++) {
								OUT_H.print("\t" + z);
							}
							OUT_H.println();
							AVG_HelT = new double[HelT.size()];
						}
						AVG_HelT = printVals(HEADER, HelT, AVG_HelT, OUT_H);
					}
					if (OUTPUT_TYPE[3]) {
						if (counter == 0) {
							OUT_R.print("YORF\tNAME");
							for (int z = 0; z < Roll.size(); z++) {
								OUT_R.print("\t" + z);
							}
							OUT_R.println();
							AVG_Roll = new double[Roll.size()];
						}
						AVG_Roll = printVals(HEADER, Roll, AVG_Roll, OUT_R);
					}
				}
				counter++;
			} else {
				System.out.println("ERROR: Invalid FASTA sequence\n" + HEADER);
			}
		}
		br.close();

		// Convert average and statistics to output tabs panes
		if (OUTPUT_TYPE[0]) {
			OUT_M.close();
			double[] DOMAIN_MGW = new double[AVG_MGW.length];
			int temp = (int) (((double) AVG_MGW.length / 2.0) + 0.5);
			for (int z = 0; z < AVG_MGW.length; z++) {
				DOMAIN_MGW[z] = (double) (temp - (AVG_MGW.length - z));
				AVG_MGW[z] /= counter;
				if (PS[0] != null) {
					PS[0].println(DOMAIN_MGW[z] + "\t" + AVG_MGW[z]);
				}
			}
			chart_M = CompositePlot.createCompositePlot(DOMAIN_MGW, AVG_MGW, NAME + " MGW");
		}
		if (OUTPUT_TYPE[1]) {
			OUT_P.close();
			double[] DOMAIN_PropT = new double[AVG_PropT.length];
			int temp = (int) (((double) AVG_PropT.length / 2.0) + 0.5);
			for (int z = 0; z < AVG_PropT.length; z++) {
				DOMAIN_PropT[z] = (double) (temp - (AVG_PropT.length - z));
				AVG_PropT[z] /= counter;
				if (PS[1] != null) {
					PS[1].println(DOMAIN_PropT[z] + "\t" + AVG_PropT[z]);
				}
			}
			chart_P = CompositePlot.createCompositePlot(DOMAIN_PropT, AVG_PropT, NAME + " PropT");
		}
		if (OUTPUT_TYPE[2]) {
			OUT_H.close();
			double[] DOMAIN_HelT = new double[AVG_HelT.length];
			int temp = (int) (((double) AVG_HelT.length / 2.0) + 0.5);
			for (int z = 0; z < AVG_HelT.length; z++) {
				DOMAIN_HelT[z] = (double) (temp - (AVG_HelT.length - z));
				AVG_HelT[z] /= counter;
				if (PS[2] != null) {
					PS[2].println(DOMAIN_HelT[z] + "\t" + AVG_HelT[z]);
				}
			}
			chart_H = CompositePlot.createCompositePlot(DOMAIN_HelT, AVG_HelT, NAME + " HelT");
		}
		if (OUTPUT_TYPE[3]) {
			OUT_R.close();
			double[] DOMAIN_Roll = new double[AVG_Roll.length];
			int temp = (int) (((double) AVG_Roll.length / 2.0) + 0.5);
			for (int z = 0; z < AVG_Roll.length; z++) {
				DOMAIN_Roll[z] = (double) (temp - (AVG_Roll.length - z));
				AVG_Roll[z] /= counter;
				if (PS[3] != null) {
					PS[3].println(DOMAIN_Roll[z] + "\t" + AVG_Roll[z]);
				}
			}
			chart_R = CompositePlot.createCompositePlot(DOMAIN_Roll, AVG_Roll, NAME + " Roll");
		}
	}

	/**
	 * Getter method for the swing component chart of the Minor Groove Width DNA
	 * shape type.
	 * 
	 * @return the chart for Minor Groove Width
	 */
	public Component getChartM() {
		return chart_M;
	}

	/**
	 * Getter method for the swing component chart of the Propeller Twist DNA shape type.
	 * 
	 * @return the chart for Propeller Twist
	 */
	public Component getChartP() {
		return chart_P;
	}

	/**
	 * Getter method for the swing component chart of the Helical Twist DNA shape
	 * type.
	 * 
	 * @return the chart for Helical Twist
	 */
	public Component getChartH() {
		return chart_H;
	}

	/**
	 * Getter method for the swing component chart of the Roll DNA shape type.
	 * 
	 * @return the chart for Roll
	 */
	public Component getChartR() {
		return chart_R;
	}

	/**
	 * Getter method for average scores of each DNA shape type.
	 * 
	 * @param shapeType indicate shape type to return (0=MGW, 1=PropT, 2=HelT, 3=Roll).
	 * @return the array of shape scores
	 */
	public double[] getAvg(int shapeType) {
		if (shapeType == 0) {
			return AVG_MGW;
		} else if (shapeType == 1) {
			return AVG_PropT;
		} else if (shapeType == 2) {
			return AVG_HelT;
		} else if (shapeType == 3) {
			return AVG_Roll;
		} else {
			return null;
		}
	}

	/**
	 * Initialize output PrintStream objects for each DNA shape as needed.
	 */
	private void openOutputFiles() {
		if (OUTBASENAME == null) {
			OUTBASENAME = FASTA.getName().split("\\.")[0];
		}
		// Open Output File
		try {
			if (OUTPUT_TYPE[0]) {
				OUT_M = new PrintStream(new File(OUTBASENAME + "_MGW.cdt"));
			}
			if (OUTPUT_TYPE[1]) {
				OUT_P = new PrintStream(new File(OUTBASENAME + "_PropT.cdt"));
			}
			if (OUTPUT_TYPE[2]) {
				OUT_H = new PrintStream(new File(OUTBASENAME + "_HelT.cdt"));
			}
			if (OUTPUT_TYPE[3]) {
				OUT_R = new PrintStream(new File(OUTBASENAME + "_Roll.cdt"));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Print a row of scores in a tab-delimited manner using the CDT format with the
	 * header string occupying the first two tokens (or "columns"). Each score is
	 * simultaneously added to the AVG array in the matching position (parallel
	 * arrays).
	 * 
	 * @param header the string to print for the first two tab-delimited tokens
	 *               preceeding the scores
	 * @param SCORES an array of scores to print to a line
	 * @param AVG    an array with the same length as SCORES (if not longer)
	 * @param O      destination to print the line to
	 * @return SCORES that have been element-wise summed with AVG
	 */
	private double[] printVals(String header, List<Double> SCORES, double[] AVG, PrintStream O) {
		O.print(header + "\t" + header);
		for (int z = 0; z < SCORES.size(); z++) {
			O.print("\t" + SCORES.get(z));
			AVG[z] += SCORES.get(z);
		}
		O.println();
		return (AVG);
	}

	/**
	 * Get the current timestamp
	 * 
	 * @return current time as a String
	 */
	private static String getTimeStamp() {
		Date date = new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}