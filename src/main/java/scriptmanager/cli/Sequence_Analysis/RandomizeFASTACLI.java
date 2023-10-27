package scriptmanager.cli.Sequence_Analysis;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;

import scriptmanager.objects.ToolDescriptions;
import scriptmanager.util.ExtensionFileFilter;
import scriptmanager.scripts.Sequence_Analysis.RandomizeFASTA;

/**
 * Command line interface class for randomizing sequences (shuffling
 * nucleotides) in a FASTA file by calling the methods implemented in the
 * scripts package.
 * 
 * @author Olivia Lang
 * @see scriptmanager.scripts.Sequence_Analysis.RandomizeFASTA
 */
@Command(name = "randomize-fasta", mixinStandardHelpOptions = true, description = ToolDescriptions.randomize_fasta_description, version = "ScriptManager "
		+ ToolDescriptions.VERSION, sortOptions = false, exitCodeOnInvalidInput = 1, exitCodeOnExecutionException = 1)
public class RandomizeFASTACLI implements Callable<Integer> {

	@Parameters(index = "0", description = "the FASTA file ")
	private File fastaFile;

	@Option(names = { "-o",
			"--output" }, description = "Specify basename for output files (default = <fastaFilename>_RAND.fa)")
	private File output;
	@Option(names = {"-s", "--seed"}, description = "specify an integer seed for reproducible outputs")
	private Integer seed = null;

	@Override
	public Integer call() throws Exception {
		System.err.println(">RandomizeFASTACLI.call()");
		String validate = validateInput();
		if (!validate.equals("")) {
			System.err.println(validate);
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}

		RandomizeFASTA.randomizeFASTA(fastaFile, output, seed);

		System.err.println("Randomization Complete.");
		return (0);
	}

	/**
	 * Validate the input values before executing the script.
	 * 
	 * @return a multi-line string describing input validation issues
	 * @throws IOException
	 */
	private String validateInput() throws IOException {
		String r = "";

		// check input exists
		if (!fastaFile.exists()) {
			r += "(!)FASTA file does not exist: " + fastaFile.getName() + "\n";
			return (r);
		}
		// check input extension
		ExtensionFileFilter faFilter = new ExtensionFileFilter("fa");
		if (!faFilter.accept(fastaFile)) {
			r += "(!)Is this a FASTA file? Check extension: " + fastaFile.getName() + "\n";
		}
		// set default output filename
		if (output == null) {
			String NAME = ExtensionFileFilter.stripExtension(fastaFile) + "_RAND.fa";
			output = new File(NAME);
			// check output filename is valid
		} else {
			// check ext
			try {
				if (!faFilter.accept(output)) {
					r += "(!)Use FASTA extension for output filename. Try: "
							+ ExtensionFileFilter.stripExtension(output) + ".fa\n";
				}
			} catch (NullPointerException e) {
				r += "(!)Output filename must have extension: use \".fa\" extension for output filename. Try: " + output
						+ ".fa\n";
			}
			// check directory
			if (output.getParent() == null) {
				// System.err.println("default to current directory");
			} else if (!new File(output.getParent()).exists()) {
				r += "(!)Check output directory exists: " + output.getParent() + "\n";
			}
		}

		return (r);
	}

	/**
	 * Reconstruct CLI command
	 * 
	 * @param input  filepath to FASTA-formatted sequences to randomize
	 * @param output filepath to write randomized sequences to
	 * @param seed   set a random seed
	 * @return command line to execute with formatted inputs
	 */
	public static String getCLIcommand(File input, File output, Integer seed) {
		String command = "java -jar $SCRIPTMANAGER sequence-analysis randomize-fasta";
		command += " -o " + output.getAbsolutePath();
		command += " " + input.getAbsolutePath();
		return (command);
	}
}