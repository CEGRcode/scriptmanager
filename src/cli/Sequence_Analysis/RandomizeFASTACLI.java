package cli.Sequence_Analysis;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;

import objects.ToolDescriptions;
import util.ExtensionFileFilter;
import scripts.Sequence_Analysis.RandomizeFASTA;

/**
 * Sequence_AnalysisCLI/RandomizeFASTACLI
 */
@Command(name = "randomize-fasta", mixinStandardHelpOptions = true, description = ToolDescriptions.randomize_fasta_description, version = "ScriptManager "
		+ ToolDescriptions.VERSION, sortOptions = false, exitCodeOnInvalidInput = 1, exitCodeOnExecutionException = 1)
public class RandomizeFASTACLI implements Callable<Integer> {

	@Parameters(index = "0", description = "the FASTA file ")
	private File fastaFile;

	@Option(names = { "-o",
			"--output" }, description = "Specify basename for output files (default = <fastaFilename>_RAND.fa)")
	private File output;

	@Override
	public Integer call() throws Exception {
		System.err.println(">RandomizeFASTACLI.call()");
		String validate = validateInput();
		if (!validate.equals("")) {
			System.err.println(validate);
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}

		RandomizeFASTA.randomizeFASTA(fastaFile, output);

		System.err.println("Randomization Complete.");
		return (0);
	}

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
}