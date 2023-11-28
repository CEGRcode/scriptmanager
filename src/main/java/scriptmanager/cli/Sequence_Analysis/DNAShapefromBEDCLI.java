package scriptmanager.cli.Sequence_Analysis;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import scriptmanager.objects.ToolDescriptions;
import scriptmanager.util.ExtensionFileFilter;
import scriptmanager.scripts.Sequence_Analysis.DNAShapefromBED;

/**
 * Command line interface for
 * {@link scriptmanager.scripts.Sequence_Analysis.DNAShapefromBED}
 * 
 * @author Olivia Lang
 */
@Command(name = "dna-shape-bed", mixinStandardHelpOptions = true, description = ToolDescriptions.dna_shape_from_bed_description, version = "ScriptManager "
		+ ToolDescriptions.VERSION, sortOptions = false, exitCodeOnInvalidInput = 1, exitCodeOnExecutionException = 1)
public class DNAShapefromBEDCLI implements Callable<Integer> {

	/**
	 * Creates a new DNAShapefromBEDCLI object
	 */
	public DNAShapefromBEDCLI(){}

	@Parameters(index = "0", description = "reference genome FASTA file")
	private File genomeFASTA;
	@Parameters(index = "1", description = "the BED file of sequences to extract")
	private File bedFile;

	@Option(names = { "-o",
			"--output" }, description = "Specify basename for output files, files for each shape indicated will share this name with a different suffix")
	private String outputBasename = null;
	@Option(names = {"-z", "--gzip"}, description = "gzip output (default=false)")
	private boolean gzOutput = false;
	@Option(names = { "--avg-composite" }, description = "Save average composite")
	private boolean avgComposite = false;
	@Option(names = { "-n", "--no-force" }, description = "don't force-strandedness (default is to force strandedness)")
	private boolean forceStrand = true;

	@ArgGroup(validate = false, heading = "Shape Options%n")
	ShapeType shape = new ShapeType();

	static class ShapeType {
		@Option(names = { "-g", "--groove" }, description = "output minor groove width")
		private boolean groove = false;
		@Option(names = { "-r", "--roll" }, description = "output roll")
		private boolean roll = false;
		@Option(names = { "-p", "--propeller" }, description = "output propeller twist")
		private boolean propeller = false;
		@Option(names = { "-l", "--helical" }, description = "output helical twist")
		private boolean helical = false;
		@Option(names = { "-a", "--all" }, description = "output groove, roll, propeller twist, and helical twist (equivalent to -grpl).")
		private boolean all = false;
	}

	private boolean[] OUTPUT_TYPE = new boolean[] { false, false, false, false };

	/**
	 * Runs when this subcommand is called, running script in respective script package with user defined arguments
	 * @throws IOException Invalid file or parameters
	 */
	@Override
	public Integer call() throws Exception {
		System.err.println(">DNAShapefromBEDCLI.call()");
		String validate = validateInput();
		if (!validate.equals("")) {
			System.err.println(validate);
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}

		// Print Composite Scores
		try {
			// Generate Composite Plot
			DNAShapefromBED script_obj = new DNAShapefromBED(genomeFASTA, bedFile, outputBasename, OUTPUT_TYPE,
					forceStrand, new PrintStream[] { null, null, null, null }, gzOutput);
			script_obj.run();

			if (avgComposite) {
				String[] headers = new String[] { "AVG_MGW", "AVG_PropT", "AVG_HelT", "AVG_Roll" };
				for (int t = 0; t < OUTPUT_TYPE.length; t++) {
					if (OUTPUT_TYPE[t]) {
						PrintStream COMPOSITE = new PrintStream(new File(outputBasename + "_" + headers[t] + ".out"));
						double[] AVG = script_obj.getAvg(t);
						// position vals
						for (int z = 0; z < AVG.length; z++) {
							COMPOSITE.print("\t" + z);
						}
						COMPOSITE.print("\n" + ExtensionFileFilter.stripExtension(bedFile) + "_" + headers[t]);
						// score vals
						for (int z = 0; z < AVG.length; z++) {
							COMPOSITE.print("\t" + AVG[z]);
						}
						COMPOSITE.println();
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		System.err.println("Shapes Calculated.");
		return (0);
	}

	/**
	 * Validate the input values before executing the script
	 * 
	 * @return a multi-line string describing input validation issues
	 * @throws IOException Invalid file or parameters
	 */
	private String validateInput() throws IOException {
		String r = "";

		// validate genome

		if (!genomeFASTA.exists()) {
			r += "(!)FASTA file does not exist: " + genomeFASTA.getName() + "\n";
		}
		if (!bedFile.exists()) {
			r += "(!)BED file does not exist: " + bedFile.getName() + "\n";
		}
		if (!r.equals("")) {
			return (r);
		}
		// set default output filename
		if (outputBasename == null) {
			outputBasename = ExtensionFileFilter.stripExtension(bedFile);
			// check output filename is valid
		} else {
			String outParent = new File(outputBasename).getParent();
			// no extension check
			// check directory
			if (outParent == null) {
// 				System.err.println("default to current directory");
			} else if (!new File(outParent).exists()) {
				r += "(!)Check output directory exists: " + outParent + "\n";
			}
		}

		// Check & set output_type
		if (!(shape.groove || shape.propeller || shape.helical || shape.roll || shape.all)) {
			r += "(!)Please select at least one of the shape flags.\n";
		} else if ((shape.groove || shape.propeller || shape.helical || shape.roll) && shape.all) {
			r += "(!)Please avoid mixing the \"-a\" flag with the other shape flags.\n";
		}

		if (shape.groove) {
			OUTPUT_TYPE[0] = true;
		}
		if (shape.propeller) {
			OUTPUT_TYPE[1] = true;
		}
		if (shape.helical) {
			OUTPUT_TYPE[2] = true;
		}
		if (shape.roll) {
			OUTPUT_TYPE[3] = true;
		}

		if (shape.all) {
			OUTPUT_TYPE = new boolean[] { true, true, true, true };
		}

		return (r);
	}

	/**
	 * Reconstruct CLI command
	 * 
	 * @param gen   the reference genome sequence in FASTA-format (FAI will be
	 *              automatically generated)
	 * @param input the BED-formatted coordinate intervals to extract sequence from
	 * @param out   the output file name base (to add _&lt;shapetype&gt;.cdt suffix
	 *              to)
	 * @param type  a four-element boolean list for specifying shape type to output
	 *              (no enforcement on size)
	 * @param str  force strandedness (true=forced, false=not forced)
	 * @return command line to execute with formatted inputs
	 */
	public static String getCLIcommand(File gen, File input, String out, boolean[] type, boolean str) {
		String command = "java -jar $SCRIPTMANAGER sequence-analysis dna-shape-bed";
		command += " -o " + out;
		command += type[0] ? " --groove" : "";
		command += type[1] ? " --propeller" : "";
		command += type[2] ? " --helical" : "";
		command += type[3] ? " --roll" : "";
		command += str ? "" : "--no-force";
		command += " " + gen;
		command += " " + input;
		return (command);
	}
}