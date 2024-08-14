package scriptmanager.scripts.Sequence_Analysis;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import scriptmanager.charts.CompositePlot;
import scriptmanager.cli.Sequence_Analysis.DNAShapefromFASTACLI.ShapeType;
import scriptmanager.objects.Exceptions.ScriptManagerException;
import scriptmanager.util.GZipUtilities;
import scriptmanager.util.DNAShapeReference;
import scriptmanager.util.ExtensionFileFilter;

/**
 * Calculate and score various aspects of DNA shape across a set of FASTA
 * sequences.
 * 
 * @author William KM Lai
 * @see scriptmanager.util.DNAShapeReference
 * @see scriptmanager.cli.Sequence_Analysis.DNAShapefromFASTACLI
 * @see scriptmanager.window_interface.Sequence_Analysis.DNAShapefromFASTAOutput
 * @see scriptmanager.window_interface.Sequence_Analysis.DNAShapefromFASTAWindow
 */
public class DNAShapefromFASTA {
	private File FASTA = null;
	private File OUTBASENAME = null;

	private ShapeType OUTPUT_TYPE = null;
	private boolean OUTPUT_COMPOSITE = false;
	private short OUTPUT_MATRIX = 0;
	private boolean GZIP_OUTPUT;

	private PrintStream OUT_M = null;
	private PrintStream OUT_P = null;
	private PrintStream OUT_H = null;
	private PrintStream OUT_R = null;
	private PrintStream OUT_EP = null;
	private PrintStream OUT_STR = null;
	private PrintStream OUT_B = null;
	private PrintStream OUT_SHEAR = null;
	private PrintStream OUT_O = null;
	private PrintStream OUT_STA = null;
	private PrintStream OUT_T = null;
	private PrintStream OUT_SL = null;
	private PrintStream OUT_RI = null;
	private PrintStream OUT_SHIFT = null;


	private PrintStream[] PS = null;

	static Map<String, List<Double>> STRUCTURE = null;

	double[] AVG_MGW = null;
	double[] AVG_PropT = null;
	double[] AVG_HelT = null;
	double[] AVG_Roll = null;
	double[] AVG_EP = null;
	double[] AVG_Stretch = null;
	double[] AVG_Buckle = null;
	double[] AVG_Shear = null;
	double[] AVG_Opening = null;
	double[] AVG_Stagger = null;
	double[] AVG_Tilt = null;
	double[] AVG_Slide = null;
	double[] AVG_Rise = null;
	double[] AVG_Shift = null;

	Component chart_M = null;
	Component chart_P = null;
	Component chart_H = null;
	Component chart_R = null;

	public final static short NO_MATRIX = 0;
	public final static short TAB = 1;
	public final static short CDT = 2;

	public DNAShapefromFASTA(File input, File out, ShapeType type, boolean outputComposite, short outputMatrix, boolean gzOutput) {
		FASTA = input;
		OUTBASENAME = out;
		OUTPUT_TYPE = type;
		OUTPUT_COMPOSITE = outputComposite;
		OUTPUT_MATRIX = outputMatrix;
		GZIP_OUTPUT = gzOutput;
		PS = new PrintStream[] { null, null, null, null };

		STRUCTURE = DNAShapeReference.InitializeStructure();
	}

	/**
	 * Initialize object with script inputs for generating DNA shape reports.
	 * 
	 * @param input           the FASTA-formatted sequence to calculate shape for
	 * @param out             the output file name base (to add
	 *                        _&lt;shapetype&gt;.cdt suffix to)
	 * @param type            a four-element boolean list for specifying shape type
	 *                        to output (no enforcement on size) [MGW, PropT, HelT, Roll, EP, Stretch, Buckle, Shear, Opening, Stagger, Tilt, Slide, Rise]
	 * @param outputComposite whether to output a composite average output
	 * @param outputMatrix    value encoding not to write output matrix data, write
	 *                        matrix in CDT format, and write matrix in tab format
	 * @param gzOutput        whether to output compressed file
	 * @param ps              list of four PrintStream objects corresponding to each
	 *                        shape type (for GUI)
	 * @throws IOException Invalid file or parameters
	 */
	public DNAShapefromFASTA(File input, File out, ShapeType type, boolean outputComposite, short outputMatrix, boolean gzOutput, PrintStream[] ps) {
		FASTA = input;
		OUTBASENAME = out;
		OUTPUT_TYPE = type;
		OUTPUT_COMPOSITE = outputComposite;
		OUTPUT_MATRIX = outputMatrix;
		GZIP_OUTPUT = gzOutput;
		PS = ps;

		STRUCTURE = DNAShapeReference.InitializeStructure();
	}

	/**
	 * Execute script to calculate DNA shape for all types across the input
	 * sequence.
	 * 
	 * @throws ScriptManagerException thrown when FASTA parsing encounters N-containing sequence
	 * @throws FileNotFoundException
	 * @throws IOException Invalid file or parameters
	 * @throws InterruptedException Thrown when more than one script is run at the same time
	 */
	public void run() throws ScriptManagerException, FileNotFoundException, IOException, InterruptedException {
		String NAME = ExtensionFileFilter.stripExtension(FASTA);
		String time = new Timestamp(new Date().getTime()).toString();
		for (int p = 0; p < PS.length; p++) {
			if (PS[p] != null) {
				PS[p].println(time + "\n" + NAME);
			}
		}
		openOutputFiles();

		int counter = 0;
		String line;
		// Check if file is gzipped and instantiate appropriate BufferedReader
		BufferedReader br = GZipUtilities.makeReader(FASTA);
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
					List<Double> EP = new ArrayList<Double>();
					List<Double> Stretch = new ArrayList<Double>();
					List<Double> Buckle = new ArrayList<Double>();
					List<Double> Shear = new ArrayList<Double>();
					List<Double> Opening = new ArrayList<Double>();
					List<Double> Stagger = new ArrayList<Double>();
					List<Double> Tilt = new ArrayList<Double>();
					List<Double> Slide = new ArrayList<Double>();
					List<Double> Rise = new ArrayList<Double>();
					List<Double> Shift = new ArrayList<Double>();
					for (int z = 0; z < seq.length() - 4; z++) {
						String key = seq.substring(z, z + 5);
						List<Double> SCORES = STRUCTURE.get(key);
						if (OUTPUT_TYPE.groove) {
							MGW.add(SCORES.get(0));
						}
						if (OUTPUT_TYPE.propeller) {
							PropT.add(SCORES.get(1));
						}
						if (OUTPUT_TYPE.helical) {
							if (z == 0) {
								HelT.add(SCORES.get(2));
								HelT.add(SCORES.get(3));
							} else {
								HelT.set(HelT.size() - 1, (HelT.get(HelT.size() - 1) + SCORES.get(2)) / 2);
								HelT.add(SCORES.get(3));
							}
						}
						if (OUTPUT_TYPE.roll) {
							if (z == 0) {
								Roll.add(SCORES.get(4));
								Roll.add(SCORES.get(5));
							} else {
								Roll.set(Roll.size() - 1, (Roll.get(Roll.size() - 1) + SCORES.get(4)) / 2);
								Roll.add(SCORES.get(5));
							}
						}
						if (OUTPUT_TYPE.ep){
							EP.add(SCORES.get(6));
						}
						if (OUTPUT_TYPE.stretch){
							Stretch.add(SCORES.get(7));
						}
						if (OUTPUT_TYPE.buckle){
							Buckle.add(SCORES.get(8));
						}
						if (OUTPUT_TYPE.shear){
							Shear.add(SCORES.get(9));
						}
						if (OUTPUT_TYPE.opening){
							Opening.add(SCORES.get(10));
						}
						if (OUTPUT_TYPE.stagger){
							Stagger.add(SCORES.get(11));
						}
						if (OUTPUT_TYPE.tilt){
							if (z == 0) {
								Tilt.add(-1 * SCORES.get(12));
								Tilt.add(SCORES.get(13));
							} else {
								Tilt.set(Tilt.size() - 1, (Tilt.get(Tilt.size() - 1) + SCORES.get(12)) / -2);
								Tilt.add(SCORES.get(13));
							}
						}
						if (OUTPUT_TYPE.slide){
							if (z == 0) {
								Slide.add(SCORES.get(14));
								Slide.add(SCORES.get(15));
							} else {
								Slide.set(Slide.size() - 1, (Slide.get(Slide.size() - 1) + SCORES.get(14)) / 2);
								Slide.add(SCORES.get(15));
							}
						}
						if (OUTPUT_TYPE.rise){
							if (z == 0) {
								Rise.add(SCORES.get(16));
								Rise.add(SCORES.get(17));
							} else {
								Rise.set(Rise.size() - 1, (Rise.get(Rise.size() - 1) + SCORES.get(16)) / 2);
								Rise.add(SCORES.get(17));
							}
						}
						if (OUTPUT_TYPE.shift){
							if (z == 0) {
								Shift.add(SCORES.get(18));
								Shift.add(SCORES.get(19));
							} else {
								Shift.set(Shift.size() - 1, (Shift.get(Shift.size() - 1) + SCORES.get(18)) / -2);
								Shift.add(SCORES.get(19));
							}
						}
					}

					if (OUTPUT_TYPE.groove) {
						if (counter == 0) {
							// Don't print matrix info if user specifies no matrix output
							if (OUTPUT_MATRIX != DNAShapefromBED.NO_MATRIX) {
								// print header
								OUT_M.print("YORF");
								if (OUTPUT_MATRIX == DNAShapefromBED.CDT) {
									OUT_M.print("\tNAME");
								}
								// print domain
								for (int z = 0; z < MGW.size(); z++) {
									OUT_M.print("\t" + z);
								}
								OUT_M.println();
							}
						}
						// Initialize AVG storage object
						AVG_MGW = new double[MGW.size()];
						// print matrix data and store avg data
						AVG_MGW = printVals(HEADER, MGW, AVG_MGW, OUT_M);
					}
					if (OUTPUT_TYPE.propeller) {
						if (counter == 0) {
							// Don't print matrix info if user specifies no matrix output
							if (OUTPUT_MATRIX != DNAShapefromBED.NO_MATRIX) {
								// print header
								OUT_P.print("YORF");
								if (OUTPUT_MATRIX == DNAShapefromBED.CDT) {
									OUT_P.print("\tNAME");
								}
								// print domain
								for (int z = 0; z < PropT.size(); z++) {
									OUT_P.print("\t" + z);
								}
								OUT_P.println();
							}
						}
						// Initialize AVG storage object
						AVG_PropT = new double[PropT.size()];
						// print matrix data and store avg data
						AVG_PropT = printVals(HEADER, PropT, AVG_PropT, OUT_P);
					}
					if (OUTPUT_TYPE.helical) {
						if (counter == 0) {
							// Don't print matrix info if user specifies no matrix output
							if (OUTPUT_MATRIX != DNAShapefromBED.NO_MATRIX) {
								// print header
								OUT_H.print("YORF");
								if (OUTPUT_MATRIX == DNAShapefromBED.CDT) {
									OUT_H.print("\tNAME");
								}
								// print domain
								for (int z = 0; z < HelT.size(); z++) {
									OUT_H.print("\t" + z);
								}
								OUT_H.println();
							}
						}
						// Initialize AVG storage object
						AVG_HelT = new double[HelT.size()];
						// print matrix data and store avg data
						AVG_HelT = printVals(HEADER, HelT, AVG_HelT, OUT_H);
					}
					if (OUTPUT_TYPE.roll) {
						if (counter == 0) {
							// Don't print matrix info if user specifies no matrix output
							if (OUTPUT_MATRIX != DNAShapefromBED.NO_MATRIX) {
								// print header
								OUT_R.print("YORF");
								if (OUTPUT_MATRIX == DNAShapefromBED.CDT) {
									OUT_R.print("\tNAME");
								}
								// print domain
								for (int z = 0; z < Roll.size(); z++) {
									OUT_R.print("\t" + z);
								}
								OUT_R.println();
							}
						}
						// Initialize AVG storage object
						AVG_Roll = new double[Roll.size()];
						// print matrix data and store avg data
						AVG_Roll = printVals(HEADER, Roll, AVG_Roll, OUT_R);
					}
					if (OUTPUT_TYPE.ep) {
						if (counter == 0) {
							// Don't print matrix info if user specifies no matrix output
							if (OUTPUT_MATRIX != DNAShapefromBED.NO_MATRIX) {
								// print header
								OUT_EP.print("YORF");
								if (OUTPUT_MATRIX == DNAShapefromBED.CDT) {
									OUT_EP.print("\tNAME");
								}
								// print domain
								for (int z = 0; z < EP.size(); z++) {
									OUT_EP.print("\t" + z);
								}
								OUT_EP.println();
							}
						}
						// Initialize AVG storage object
						AVG_EP = new double[EP.size()];
						// print matrix data and store avg data
						AVG_EP = printVals(HEADER, EP, AVG_EP, OUT_EP);
					}
					if (OUTPUT_TYPE.stretch) {
						if (counter == 0) {
							// Don't print matrix info if user specifies no matrix output
							if (OUTPUT_MATRIX != DNAShapefromBED.NO_MATRIX) {
								// print header
								OUT_STR.print("YORF");
								if (OUTPUT_MATRIX == DNAShapefromBED.CDT) {
									OUT_STR.print("\tNAME");
								}
								// print domain
								for (int z = 0; z < Stretch.size(); z++) {
									OUT_STR.print("\t" + z);
								}
								OUT_STR.println();
							}
						}
						// Initialize AVG storage object
						AVG_Stretch = new double[Stretch.size()];
						// print matrix data and store avg data
						AVG_Stretch = printVals(HEADER, Stretch, AVG_Stretch, OUT_STR);
					}
					if (OUTPUT_TYPE.buckle) {
						if (counter == 0) {
							// Don't print matrix info if user specifies no matrix output
							if (OUTPUT_MATRIX != DNAShapefromBED.NO_MATRIX) {
								// print header
								OUT_B.print("YORF");
								if (OUTPUT_MATRIX == DNAShapefromBED.CDT) {
									OUT_B.print("\tNAME");
								}
								// print domain
								for (int z = 0; z < Buckle.size(); z++) {
									OUT_B.print("\t" + z);
								}
								OUT_B.println();
							}
						}
						// Initialize AVG storage object
						AVG_Buckle = new double[Buckle.size()];
						// print matrix data and store avg data
						AVG_Buckle = printVals(HEADER, Buckle, AVG_Buckle, OUT_B);
					}
					if (OUTPUT_TYPE.shear) {
						if (counter == 0) {
							// Don't print matrix info if user specifies no matrix output
							if (OUTPUT_MATRIX != DNAShapefromBED.NO_MATRIX) {
								// print header
								OUT_SHEAR.print("YORF");
								if (OUTPUT_MATRIX == DNAShapefromBED.CDT) {
									OUT_SHEAR.print("\tNAME");
								}
								// print domain
								for (int z = 0; z < Shear.size(); z++) {
									OUT_SHEAR.print("\t" + z);
								}
								OUT_SHEAR.println();
							}
						}
						// Initialize AVG storage object
						AVG_Shear = new double[Shear.size()];
						// print matrix data and store avg data
						AVG_Shear = printVals(HEADER, Shear, AVG_Shear, OUT_SHEAR);
					}
					if (OUTPUT_TYPE.opening) {
						if (counter == 0) {
							// Don't print matrix info if user specifies no matrix output
							if (OUTPUT_MATRIX != DNAShapefromBED.NO_MATRIX) {
								// print header
								OUT_O.print("YORF");
								if (OUTPUT_MATRIX == DNAShapefromBED.CDT) {
									OUT_O.print("\tNAME");
								}
								// print domain
								for (int z = 0; z < Opening.size(); z++) {
									OUT_O.print("\t" + z);
								}
								OUT_O.println();
							}
						}
						// Initialize AVG storage object
						AVG_Opening = new double[Opening.size()];
						// print matrix data and store avg data
						AVG_Opening = printVals(HEADER, Opening, AVG_Opening, OUT_O);
					}
					if (OUTPUT_TYPE.stagger) {
						if (counter == 0) {
							// Don't print matrix info if user specifies no matrix output
							if (OUTPUT_MATRIX != DNAShapefromBED.NO_MATRIX) {
								// print header
								OUT_STA.print("YORF");
								if (OUTPUT_MATRIX == DNAShapefromBED.CDT) {
									OUT_STA.print("\tNAME");
								}
								// print domain
								for (int z = 0; z < Stagger.size(); z++) {
									OUT_STA.print("\t" + z);
								}
								OUT_STA.println();
							}
						}
						// Initialize AVG storage object
						AVG_Stagger = new double[Stagger.size()];
						// print matrix data and store avg data
						AVG_Stagger = printVals(HEADER, Stagger, AVG_Stagger, OUT_STA);
					}
					if (OUTPUT_TYPE.tilt) {
						if (counter == 0) {
							// Don't print matrix info if user specifies no matrix output
							if (OUTPUT_MATRIX != DNAShapefromBED.NO_MATRIX) {
								// print header
								OUT_T.print("YORF");
								if (OUTPUT_MATRIX == DNAShapefromBED.CDT) {
									OUT_T.print("\tNAME");
								}
								// print domain
								for (int z = 0; z < Tilt.size(); z++) {
									OUT_T.print("\t" + z);
								}
								OUT_T.println();
							}
						}
						// Initialize AVG storage object
						AVG_Tilt = new double[Tilt.size()];
						// print matrix data and store avg data
						AVG_Tilt = printVals(HEADER, Tilt, AVG_Tilt, OUT_T);
					}
					if (OUTPUT_TYPE.slide) {
						if (counter == 0) {
							// Don't print matrix info if user specifies no matrix output
							if (OUTPUT_MATRIX != DNAShapefromBED.NO_MATRIX) {
								// print header
								OUT_SL.print("YORF");
								if (OUTPUT_MATRIX == DNAShapefromBED.CDT) {
									OUT_SL.print("\tNAME");
								}
								// print domain
								for (int z = 0; z < Slide.size(); z++) {
									OUT_SL.print("\t" + z);
								}
								OUT_SL.println();
							}
						}
						// Initialize AVG storage object
						AVG_Slide = new double[Slide.size()];
						// print matrix data and store avg data
						AVG_Slide = printVals(HEADER, Slide, AVG_Slide, OUT_SL);
					}
					if (OUTPUT_TYPE.rise) {
						if (counter == 0) {
							// Don't print matrix info if user specifies no matrix output
							if (OUTPUT_MATRIX != DNAShapefromBED.NO_MATRIX) {
								// print header
								OUT_RI.print("YORF");
								if (OUTPUT_MATRIX == DNAShapefromBED.CDT) {
									OUT_RI.print("\tNAME");
								}
								// print domain
								for (int z = 0; z < Rise.size(); z++) {
									OUT_RI.print("\t" + z);
								}
								OUT_RI.println();
							}
						}
						// Initialize AVG storage object
						AVG_Rise = new double[Rise.size()];
						// print matrix data and store avg data
						AVG_Rise = printVals(HEADER, Rise, AVG_Rise, OUT_RI);
					}
					if (OUTPUT_TYPE.shift) {
						if (counter == 0) {
							// Don't print matrix info if user specifies no matrix output
							if (OUTPUT_MATRIX != DNAShapefromBED.NO_MATRIX) {
								// print header
								OUT_SHIFT.print("YORF");
								if (OUTPUT_MATRIX == DNAShapefromBED.CDT) {
									OUT_SHIFT.print("\tNAME");
								}
								// print domain
								for (int z = 0; z < Rise.size(); z++) {
									OUT_SHIFT.print("\t" + z);
								}
								OUT_SHIFT.println();
							}
						}
						// Initialize AVG storage object
						AVG_Shift = new double[Rise.size()];
						// print matrix data and store avg data
						AVG_Shift = printVals(HEADER, Rise, AVG_Shift, OUT_SHIFT);
					}
					} // if seq contains 'N's
					counter++;
				} else {
					throw new ScriptManagerException("Invalid FASTA sequence (" + HEADER + ")...DNAShape does not support N-containing sequences");
				}
			}
			br.close();
			if (OUT_M != null) { OUT_M.close(); }
			if (OUT_P != null) { OUT_P.close(); }
			if (OUT_H != null) { OUT_H.close(); }
			if (OUT_R != null) { OUT_R.close(); }
			if (OUT_EP != null) { OUT_EP.close(); }
			if (OUT_STR != null) { OUT_STR.close(); }
			if (OUT_B != null) { OUT_B.close(); }
			if (OUT_SHEAR != null) { OUT_SHEAR.close(); }
			if (OUT_O != null) { OUT_O.close(); }
			if (OUT_STA != null) { OUT_STA.close(); }
			if (OUT_T != null) { OUT_T.close(); }
			if (OUT_SL != null) { OUT_SL.close(); }
			if (OUT_RI != null) { OUT_RI.close(); }	
			if (OUT_SHIFT != null) { OUT_SHIFT.close(); }

			// Convert average and statistics to output tabs panes
			if (OUTPUT_TYPE.groove) {
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
				// Write output composite file
				if (OUTPUT_COMPOSITE) {
					PrintStream COMPOSITE = new PrintStream(new File(OUTBASENAME + "_MGW-Composite.out"));
					for (int z = 0; z < AVG_MGW.length; z++) {
						COMPOSITE.print("\t" + DOMAIN_MGW[z]);
					}
					COMPOSITE.println();
					COMPOSITE.print(NAME + "MGW-Composite");
					for (int z = 0; z < AVG_MGW.length; z++) {
						COMPOSITE.print("\t" + AVG_MGW[z]);
					}
					COMPOSITE.println();
				}
			}
			if (OUTPUT_TYPE.propeller) {
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
				// Write output composite file
				if (OUTPUT_COMPOSITE) {
					PrintStream COMPOSITE = new PrintStream(new File(OUTBASENAME + "_PropT-Composite.out"));
					for (int z = 0; z < AVG_PropT.length; z++) {
						COMPOSITE.print("\t" + DOMAIN_PropT[z]);
					}
					COMPOSITE.println();
					COMPOSITE.print(NAME + "PropT-Composite");
					for (int z = 0; z < AVG_PropT.length; z++) {
						COMPOSITE.print("\t" + AVG_PropT[z]);
					}
					COMPOSITE.println();
				}
			}
			if (OUTPUT_TYPE.helical) {
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
				// Write output composite file
				if (OUTPUT_COMPOSITE) {
					PrintStream COMPOSITE = new PrintStream(new File(OUTBASENAME + "_HelT-Composite.out"));
					for (int z = 0; z < AVG_HelT.length; z++) {
						COMPOSITE.print("\t" + DOMAIN_HelT[z]);
					}
					COMPOSITE.println();
					COMPOSITE.print(NAME + "HelT-Composite");
					for (int z = 0; z < AVG_HelT.length; z++) {
						COMPOSITE.print("\t" + AVG_HelT[z]);
					}
					COMPOSITE.println();
				}
			}
			if (OUTPUT_TYPE.roll) {
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
				// Write output composite file
				if (OUTPUT_COMPOSITE) {
					PrintStream COMPOSITE = new PrintStream(new File(OUTBASENAME + "_Roll-Composite.out"));
					for (int z = 0; z < AVG_Roll.length; z++) {
						COMPOSITE.print("\t" + DOMAIN_Roll[z]);
					}
					COMPOSITE.println();
					COMPOSITE.print(NAME + "Roll-Composite");
					for (int z = 0; z < AVG_Roll.length; z++) {
						COMPOSITE.print("\t" + AVG_Roll[z]);
					}
					COMPOSITE.println();
				}
			}
			if (OUTPUT_TYPE.ep) {
				double[] DOMAIN_EP = new double[AVG_EP.length];
				int temp = (int) (((double) AVG_EP.length / 2.0) + 0.5);
				for (int z = 0; z < AVG_EP.length; z++) {
					DOMAIN_EP[z] = (double) (temp - (AVG_EP.length - z));
					AVG_EP[z] /= counter;
					if (PS[3] != null) {
						PS[3].println(DOMAIN_EP[z] + "\t" + AVG_EP[z]);
					}
				}
				chart_R = CompositePlot.createCompositePlot(DOMAIN_EP, AVG_EP, NAME + " EP");
				// Write output composite file
				if (OUTPUT_COMPOSITE) {
					PrintStream COMPOSITE = new PrintStream(new File(OUTBASENAME + "_EP-Composite.out"));
					for (int z = 0; z < AVG_EP.length; z++) {
						COMPOSITE.print("\t" + DOMAIN_EP[z]);
					}
					COMPOSITE.println();
					COMPOSITE.print(NAME + "EP-Composite");
					for (int z = 0; z < AVG_EP.length; z++) {
						COMPOSITE.print("\t" + AVG_EP[z]);
					}
					COMPOSITE.println();
				}
			}
			if (OUTPUT_TYPE.stretch) {
				double[] DOMAIN_Stretch = new double[AVG_Stretch.length];
				int temp = (int) (((double) AVG_Stretch.length / 2.0) + 0.5);
				for (int z = 0; z < AVG_Stretch.length; z++) {
					DOMAIN_Stretch[z] = (double) (temp - (AVG_Stretch.length - z));
					AVG_Stretch[z] /= counter;
					if (PS[3] != null) {
						PS[3].println(DOMAIN_Stretch[z] + "\t" + AVG_Stretch[z]);
					}
				}
				chart_R = CompositePlot.createCompositePlot(DOMAIN_Stretch, AVG_Stretch, NAME + " Stretch");
				// Write output composite file
				if (OUTPUT_COMPOSITE) {
					PrintStream COMPOSITE = new PrintStream(new File(OUTBASENAME + "_Stretch-Composite.out"));
					for (int z = 0; z < AVG_Stretch.length; z++) {
						COMPOSITE.print("\t" + DOMAIN_Stretch[z]);
					}
					COMPOSITE.println();
					COMPOSITE.print(NAME + "Stretch-Composite");
					for (int z = 0; z < AVG_Stretch.length; z++) {
						COMPOSITE.print("\t" + AVG_Stretch[z]);
					}
					COMPOSITE.println();
				}
			}
			if (OUTPUT_TYPE.buckle) {
				double[] DOMAIN_Buckle = new double[AVG_Buckle.length];
				int temp = (int) (((double) AVG_Buckle.length / 2.0) + 0.5);
				for (int z = 0; z < AVG_Buckle.length; z++) {
					DOMAIN_Buckle[z] = (double) (temp - (AVG_Buckle.length - z));
					AVG_Buckle[z] /= counter;
					if (PS[3] != null) {
						PS[3].println(DOMAIN_Buckle[z] + "\t" + AVG_Buckle[z]);
					}
				}
				chart_R = CompositePlot.createCompositePlot(DOMAIN_Buckle, AVG_Buckle, NAME + " Buckle");
				// Write output composite file
				if (OUTPUT_COMPOSITE) {
					PrintStream COMPOSITE = new PrintStream(new File(OUTBASENAME + "_Buckle-Composite.out"));
					for (int z = 0; z < AVG_Buckle.length; z++) {
						COMPOSITE.print("\t" + DOMAIN_Buckle[z]);
					}
					COMPOSITE.println();
					COMPOSITE.print(NAME + "Buckle-Composite");
					for (int z = 0; z < AVG_Buckle.length; z++) {
						COMPOSITE.print("\t" + AVG_Buckle[z]);
					}
					COMPOSITE.println();
				}
			}
			if (OUTPUT_TYPE.shear) {
				double[] DOMAIN_Shear = new double[AVG_Shear.length];
				int temp = (int) (((double) AVG_Shear.length / 2.0) + 0.5);
				for (int z = 0; z < AVG_Shear.length; z++) {
					DOMAIN_Shear[z] = (double) (temp - (AVG_Shear.length - z));
					AVG_Shear[z] /= counter;
					if (PS[3] != null) {
						PS[3].println(DOMAIN_Shear[z] + "\t" + AVG_Shear[z]);
					}
				}
				chart_R = CompositePlot.createCompositePlot(DOMAIN_Shear, AVG_Shear, NAME + " Shear");
				// Write output composite file
				if (OUTPUT_COMPOSITE) {
					PrintStream COMPOSITE = new PrintStream(new File(OUTBASENAME + "_Shear-Composite.out"));
					for (int z = 0; z < AVG_Shear.length; z++) {
						COMPOSITE.print("\t" + DOMAIN_Shear[z]);
					}
					COMPOSITE.println();
					COMPOSITE.print(NAME + "Shear-Composite");
					for (int z = 0; z < AVG_Shear.length; z++) {
						COMPOSITE.print("\t" + AVG_Shear[z]);
					}
					COMPOSITE.println();
				}
			}
			if (OUTPUT_TYPE.opening) {
				double[] DOMAIN_Opening = new double[AVG_Opening.length];
				int temp = (int) (((double) AVG_Opening.length / 2.0) + 0.5);
				for (int z = 0; z < AVG_Opening.length; z++) {
					DOMAIN_Opening[z] = (double) (temp - (AVG_Opening.length - z));
					AVG_Opening[z] /= counter;
					if (PS[3] != null) {
						PS[3].println(DOMAIN_Opening[z] + "\t" + AVG_Opening[z]);
					}
				}
				chart_R = CompositePlot.createCompositePlot(DOMAIN_Opening, AVG_Opening, NAME + " Opening");
				// Write output composite file
				if (OUTPUT_COMPOSITE) {
					PrintStream COMPOSITE = new PrintStream(new File(OUTBASENAME + "_Opening-Composite.out"));
					for (int z = 0; z < AVG_Opening.length; z++) {
						COMPOSITE.print("\t" + DOMAIN_Opening[z]);
					}
					COMPOSITE.println();
					COMPOSITE.print(NAME + "Opening-Composite");
					for (int z = 0; z < AVG_Opening.length; z++) {
						COMPOSITE.print("\t" + AVG_Opening[z]);
					}
					COMPOSITE.println();
				}
			}
			if (OUTPUT_TYPE.stagger) {
				double[] DOMAIN_Stagger = new double[AVG_Stagger.length];
				int temp = (int) (((double) AVG_Stagger.length / 2.0) + 0.5);
				for (int z = 0; z < AVG_Stagger.length; z++) {
					DOMAIN_Stagger[z] = (double) (temp - (AVG_Stagger.length - z));
					AVG_Stagger[z] /= counter;
					if (PS[3] != null) {
						PS[3].println(DOMAIN_Stagger[z] + "\t" + AVG_Stagger[z]);
					}
				}
				chart_R = CompositePlot.createCompositePlot(DOMAIN_Stagger, AVG_Stagger, NAME + " Stagger");
				// Write output composite file
				if (OUTPUT_COMPOSITE) {
					PrintStream COMPOSITE = new PrintStream(new File(OUTBASENAME + "_Stagger-Composite.out"));
					for (int z = 0; z < AVG_Stagger.length; z++) {
						COMPOSITE.print("\t" + DOMAIN_Stagger[z]);
					}
					COMPOSITE.println();
					COMPOSITE.print(NAME + "Stagger-Composite");
					for (int z = 0; z < AVG_Stagger.length; z++) {
						COMPOSITE.print("\t" + AVG_Stagger[z]);
					}
					COMPOSITE.println();
				}
			}
			if (OUTPUT_TYPE.tilt) {
				double[] DOMAIN_Tilt = new double[AVG_Tilt.length];
				int temp = (int) (((double) AVG_Tilt.length / 2.0) + 0.5);
				for (int z = 0; z < AVG_Tilt.length; z++) {
					DOMAIN_Tilt[z] = (double) (temp - (AVG_Tilt.length - z));
					AVG_Tilt[z] /= counter;
					if (PS[3] != null) {
						PS[3].println(DOMAIN_Tilt[z] + "\t" + AVG_Tilt[z]);
					}
				}
				chart_R = CompositePlot.createCompositePlot(DOMAIN_Tilt, AVG_Tilt, NAME + " Tilt");
				// Write output composite file
				if (OUTPUT_COMPOSITE) {
					PrintStream COMPOSITE = new PrintStream(new File(OUTBASENAME + "_Tilt-Composite.out"));
					for (int z = 0; z < AVG_Tilt.length; z++) {
						COMPOSITE.print("\t" + DOMAIN_Tilt[z]);
					}
					COMPOSITE.println();
					COMPOSITE.print(NAME + "Tilt-Composite");
					for (int z = 0; z < AVG_Tilt.length; z++) {
						COMPOSITE.print("\t" + AVG_Tilt[z]);
					}
					COMPOSITE.println();
				}
			}
			if (OUTPUT_TYPE.slide) {
				double[] DOMAIN_Slide = new double[AVG_Slide.length];
				int temp = (int) (((double) AVG_Slide.length / 2.0) + 0.5);
				for (int z = 0; z < AVG_Slide.length; z++) {
					DOMAIN_Slide[z] = (double) (temp - (AVG_Slide.length - z));
					AVG_Slide[z] /= counter;
					if (PS[3] != null) {
						PS[3].println(DOMAIN_Slide[z] + "\t" + AVG_Slide[z]);
					}
				}
				chart_R = CompositePlot.createCompositePlot(DOMAIN_Slide, AVG_Slide, NAME + " Slide");
				// Write output composite file
				if (OUTPUT_COMPOSITE) {
					PrintStream COMPOSITE = new PrintStream(new File(OUTBASENAME + "_Slide-Composite.out"));
					for (int z = 0; z < AVG_Slide.length; z++) {
						COMPOSITE.print("\t" + DOMAIN_Slide[z]);
					}
					COMPOSITE.println();
					COMPOSITE.print(NAME + "Slide-Composite");
					for (int z = 0; z < AVG_Slide.length; z++) {
						COMPOSITE.print("\t" + AVG_Slide[z]);
					}
					COMPOSITE.println();
				}
			}
			if (OUTPUT_TYPE.rise) {
				double[] DOMAIN_Rise = new double[AVG_Rise.length];
				int temp = (int) (((double) AVG_Rise.length / 2.0) + 0.5);
				for (int z = 0; z < AVG_Rise.length; z++) {
					DOMAIN_Rise[z] = (double) (temp - (AVG_Rise.length - z));
					AVG_Rise[z] /= counter;
					if (PS[3] != null) {
						PS[3].println(DOMAIN_Rise[z] + "\t" + AVG_Rise[z]);
					}
				}
				chart_R = CompositePlot.createCompositePlot(DOMAIN_Rise, AVG_Rise, NAME + " Rise");
				// Write output composite file
				if (OUTPUT_COMPOSITE) {
					PrintStream COMPOSITE = new PrintStream(new File(OUTBASENAME + "_Rise-Composite.out"));
					for (int z = 0; z < AVG_Rise.length; z++) {
						COMPOSITE.print("\t" + DOMAIN_Rise[z]);
					}
					COMPOSITE.println();
					COMPOSITE.print(NAME + "Rise-Composite");
					for (int z = 0; z < AVG_Rise.length; z++) {
						COMPOSITE.print("\t" + AVG_Rise[z]);
					}
					COMPOSITE.println();
				}
			}
			if (OUTPUT_TYPE.shift) {
				double[] DOMAIN_Shift = new double[AVG_Shift.length];
				int temp = (int) (((double) AVG_Shift.length / 2.0) + 0.5);
				for (int z = 0; z < AVG_Shift.length; z++) {
					DOMAIN_Shift[z] = (double) (temp - (AVG_Shift.length - z));
					AVG_Shift[z] /= counter;
					if (PS[3] != null) {
						PS[3].println(DOMAIN_Shift[z] + "\t" + AVG_Shift[z]);
					}
				}
				chart_R = CompositePlot.createCompositePlot(DOMAIN_Shift, AVG_Shift, NAME + " Shift");
				// Write output composite file
				if (OUTPUT_COMPOSITE) {
					PrintStream COMPOSITE = new PrintStream(new File(OUTBASENAME + "_Shift-Composite.out"));
					for (int z = 0; z < AVG_Shift.length; z++) {
						COMPOSITE.print("\t" + DOMAIN_Shift[z]);
					}
					COMPOSITE.println();
					COMPOSITE.print(NAME + "Shift-Composite");
					for (int z = 0; z < AVG_Shift.length; z++) {
						COMPOSITE.print("\t" + AVG_Shift[z]);
					}
					COMPOSITE.println();
				}
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
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void openOutputFiles() throws FileNotFoundException, IOException {
		if (OUTBASENAME == null) {
			OUTBASENAME = new File(ExtensionFileFilter.stripExtension(FASTA));
		}
		// Open Output File
		if (OUTPUT_MATRIX > 0) {
			String SUFFIX = (OUTPUT_MATRIX==DNAShapefromBED.CDT ? ".cdt" : ".tab") + (GZIP_OUTPUT? ".gz": "");
			if (OUTPUT_TYPE.groove) { OUT_M = GZipUtilities.makePrintStream(new File(OUTBASENAME + "_MGW" + SUFFIX), GZIP_OUTPUT); }
			if (OUTPUT_TYPE.propeller) { OUT_P = GZipUtilities.makePrintStream(new File(OUTBASENAME + "_PropT" + SUFFIX), GZIP_OUTPUT); }
			if (OUTPUT_TYPE.helical) { OUT_H = GZipUtilities.makePrintStream(new File(OUTBASENAME + "_HelT" + SUFFIX), GZIP_OUTPUT); }
			if (OUTPUT_TYPE.roll) { OUT_R = GZipUtilities.makePrintStream(new File(OUTBASENAME + "_Roll" + SUFFIX), GZIP_OUTPUT); }
			if (OUTPUT_TYPE.ep) { OUT_EP = GZipUtilities.makePrintStream(new File(OUTBASENAME + "_EP" + SUFFIX), GZIP_OUTPUT); }
			if (OUTPUT_TYPE.stretch) { OUT_STR = GZipUtilities.makePrintStream(new File(OUTBASENAME + "_Stretch" + SUFFIX), GZIP_OUTPUT); }
			if (OUTPUT_TYPE.buckle) { OUT_B = GZipUtilities.makePrintStream(new File(OUTBASENAME + "_Buckle" + SUFFIX), GZIP_OUTPUT); }
			if (OUTPUT_TYPE.shear) { OUT_SHEAR = GZipUtilities.makePrintStream(new File(OUTBASENAME + "_Shear" + SUFFIX), GZIP_OUTPUT); }
			if (OUTPUT_TYPE.opening) { OUT_O = GZipUtilities.makePrintStream(new File(OUTBASENAME + "_Opening" + SUFFIX), GZIP_OUTPUT); }
			if (OUTPUT_TYPE.stagger) { OUT_STA = GZipUtilities.makePrintStream(new File(OUTBASENAME + "_Stagger" + SUFFIX), GZIP_OUTPUT); }
			if (OUTPUT_TYPE.tilt) { OUT_T = GZipUtilities.makePrintStream(new File(OUTBASENAME + "_Tilt" + SUFFIX), GZIP_OUTPUT); }
			if (OUTPUT_TYPE.slide) { OUT_SL = GZipUtilities.makePrintStream(new File(OUTBASENAME + "_Slide" + SUFFIX), GZIP_OUTPUT); }
			if (OUTPUT_TYPE.rise) { OUT_RI = GZipUtilities.makePrintStream(new File(OUTBASENAME + "_Rise" + SUFFIX), GZIP_OUTPUT); }
			if (OUTPUT_TYPE.shift) { OUT_SHIFT = GZipUtilities.makePrintStream(new File(OUTBASENAME + "_Shift" + SUFFIX), GZIP_OUTPUT); }
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
		// print header
		if (O != null) {
			O.print(header);
			if (OUTPUT_MATRIX == DNAShapefromBED.CDT) {
				O.print("\t" + header);
			}
		}
		System.out.println(SCORES);
		for (int z = 0; z < SCORES.size(); z++) {
			// print values
			if (O != null) { O.print("\t" + SCORES.get(z)); }
			// build avg
			System.out.println(z);
			AVG[z] += SCORES.get(z);
		}
		// print new line
		if (O != null) { O.println(); }
		// return avg
		return (AVG);
	}
}