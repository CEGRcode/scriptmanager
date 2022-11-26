package scripts.Sequence_Analysis;

import htsjdk.samtools.SAMException;
import htsjdk.samtools.reference.FastaSequenceIndexCreator;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import objects.CoordinateObjects.BEDCoord;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import charts.CompositePlot;
import util.FASTAUtilities;
import util.DNAShapeReference;

/**
 * This script calculates various aspects of DNA shape across a set of BED
 * intervals.
 * 
 * @author William KM Lai
 * @see util.DNAShapeReference
 * @see cli.Sequence_Analysis.DNAShapefromBEDCLI
 * @see window_interface.Sequence_Analysis.DNAShapefromBEDOutput
 * @see window_interface.Sequence_Analysis.DNAShapefromBEDWindow
 */
public class DNAShapefromBED {
	private File GENOME = null;
	private String OUTBASENAME = null;
	private boolean[] OUTPUT_TYPE = null;
	private File BED = null;

	private boolean STRAND = true;
	private boolean INDEX = true;

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
	 * @param gen  the reference genome sequence in FASTA-format (FAI will be
	 *             automatically generated)
	 * @param b    the BED-formatted coordinate intervals to extract sequence from
	 * @param out  the output file name base (to add _<shapetype>.cdt suffix to)
	 * @param type a four-element boolean list for specifying shape type to output
	 *             (no enforcement on size)
	 * @param str  force strandedness (true=forced, false=not forced)
	 * @param ps   list of four PrintStream objects corresponding to each shape type
	 *             (for GUI)
	 * @throws IOException
	 */
	public DNAShapefromBED(File gen, File b, String out, boolean[] type, boolean str, PrintStream[] ps)
			throws IOException {
		GENOME = gen;
		BED = b;
		OUTBASENAME = out;
		OUTPUT_TYPE = type;
		STRAND = str;
		PS = ps;

		File FAI = new File(GENOME + ".fai");
		// Check if FAI index file exists
		if (!FAI.exists() || FAI.isDirectory()) {
			FastaSequenceIndexCreator.create(GENOME.toPath(), true);
		}

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
		try {
			IndexedFastaSequenceFile QUERY = new IndexedFastaSequenceFile(GENOME);

			String NAME = BED.getName().split("\\.")[0];
			String time = getTimeStamp(); // Generate TimeStamp
			for (int p = 0; p < PS.length; p++) {
				if (OUTPUT_TYPE[p] && PS[p] != null) {
					PS[p].println(time + "\n" + NAME);
				}
			}
			openOutputFiles();
			ArrayList<BEDCoord> BED_Coord = loadCoord(BED);

			for (int y = 0; y < BED_Coord.size(); y++) {
				try {
					String seq = new String(QUERY.getSubsequenceAt(BED_Coord.get(y).getChrom(),
							BED_Coord.get(y).getStart() + 1, BED_Coord.get(y).getStop()).getBases()).toUpperCase();
					if (!seq.contains("N")) {
						if (STRAND && BED_Coord.get(y).getDir().equals("-")) {
							seq = FASTAUtilities.RevComplement(seq);
						}
						// Populate array for each BED file
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
						} // Move through seq by window

						if (OUTPUT_TYPE[0]) {
							if (y == 0) {
								OUT_M.print("YORF\tNAME");
								for (int z = 0; z < MGW.size(); z++) {
									OUT_M.print("\t" + z);
								}
								OUT_M.println();
								AVG_MGW = new double[MGW.size()];
							}
							AVG_MGW = printVals(BED_Coord.get(y), MGW, AVG_MGW, OUT_M);
						}
						if (OUTPUT_TYPE[1]) {
							if (y == 0) {
								OUT_P.print("YORF\tNAME");
								for (int z = 0; z < PropT.size(); z++) {
									OUT_P.print("\t" + z);
								}
								OUT_P.println();
								AVG_PropT = new double[PropT.size()];
							}
							AVG_PropT = printVals(BED_Coord.get(y), PropT, AVG_PropT, OUT_P);
						}
						if (OUTPUT_TYPE[2]) {
							if (y == 0) {
								OUT_H.print("YORF\tNAME");
								for (int z = 0; z < HelT.size(); z++) {
									OUT_H.print("\t" + z);
								}
								OUT_H.println();
								AVG_HelT = new double[HelT.size()];
							}
							AVG_HelT = printVals(BED_Coord.get(y), HelT, AVG_HelT, OUT_H);
						}
						if (OUTPUT_TYPE[3]) {
							if (y == 0) {
								OUT_R.print("YORF\tNAME");
								for (int z = 0; z < Roll.size(); z++) {
									OUT_R.print("\t" + z);
								}
								OUT_R.println();
								AVG_Roll = new double[Roll.size()];
							}
							AVG_Roll = printVals(BED_Coord.get(y), Roll, AVG_Roll, OUT_R);
						}
					} // if seq contains 'N'
				} catch (SAMException e) {
					for (int p = 0; p < PS.length; p++) {
						if (OUTPUT_TYPE[p] && PS[p] != null) {
							PS[p].println("INVALID COORDINATE: " + BED_Coord.get(y).toString());
						}
					}
				}
			}

			// Convert average and statistics to output tabs panes
			if (OUTPUT_TYPE[0]) {
				OUT_M.close();
				double[] DOMAIN_MGW = new double[AVG_MGW.length];
				int temp = (int) (((double) AVG_MGW.length / 2.0) + 0.5);
				for (int z = 0; z < AVG_MGW.length; z++) {
					DOMAIN_MGW[z] = (double) (temp - (AVG_MGW.length - z));
					AVG_MGW[z] /= BED_Coord.size();
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
					AVG_PropT[z] /= BED_Coord.size();
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
					AVG_HelT[z] /= BED_Coord.size();
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
					AVG_Roll[z] /= BED_Coord.size();
					if (PS[3] != null) {
						PS[3].println(DOMAIN_Roll[z] + "\t" + AVG_Roll[z]);
					}
				}
				chart_R = CompositePlot.createCompositePlot(DOMAIN_Roll, AVG_Roll, NAME + " Roll");
			}
			QUERY.close();
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (SAMException e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * TODO: Likely leftover from previous implementations of checking and creating
	 * FAI. Need to remove this, remove calls of this, and instead handle the
	 * SAMException thrown by FastaSequenceIndexCreator to account for malformed
	 * FASTA file.
	 * 
	 * @return
	 */
	public boolean getFAIstatus() {
		return INDEX;
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
	 * Parse a BED-formatted file to load all coordinates into memory as a list of
	 * BEDCoord objects.
	 * 
	 * @param INPUT a BED-formatted file
	 * @return the parsed BED coordinate objects
	 * @throws FileNotFoundException
	 */
	public ArrayList<BEDCoord> loadCoord(File INPUT) throws FileNotFoundException {
		Scanner scan = new Scanner(INPUT);
		ArrayList<BEDCoord> COORD = new ArrayList<BEDCoord>();
		while (scan.hasNextLine()) {
			String[] temp = scan.nextLine().split("\t");
			if (temp.length > 2) {
				if (!temp[0].contains("track") && !temp[0].contains("#")) {
					String name = "";
					if (temp.length > 3) {
						name = temp[3];
					} else {
						name = temp[0] + "_" + temp[1] + "_" + temp[2];
					}
					if (Integer.parseInt(temp[1]) >= 0) {
						if (temp[5].equals("+")) {
							COORD.add(new BEDCoord(temp[0], Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), "+",
									name));
						} else {
							COORD.add(new BEDCoord(temp[0], Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), "-",
									name));
						}
					} else {
						System.out.println("Invalid Coordinate in File!!!\n" + Arrays.toString(temp));
					}
				}
			}
		}
		scan.close();
		return COORD;
	}

	/**
	 * Initialize output PrintStream objects for each DNA shape as needed.
	 */
	private void openOutputFiles() {
		if (OUTBASENAME == null) {
			OUTBASENAME = BED.getName().split("\\.")[0];
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
	 * BEDCoord name/id string occupying the first two tokens (or "columns"). Each
	 * score is simultaneously added to the AVG array in the matching position
	 * (parallel arrays).
	 * 
	 * @param b      the coordinate object whose name/id attribute is used for the
	 *               header
	 * @param SCORES an array of scores to print to a line
	 * @param AVG    an array with the same length as SCORES (if not longer)
	 * @param O      destination to print the line to
	 * @return SCORES that have been element-wise summed with AVG
	 */
	private double[] printVals(BEDCoord b, List<Double> SCORES, double[] AVG, PrintStream O) {
		O.print(b.getName() + "\t" + b.getName());
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