package cli.Sequence_Analysis;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;
import java.io.File;
import java.io.IOException;

import objects.ToolDescriptions;
import util.ExtensionFileFilter;
import scripts.Sequence_Analysis.FASTAExtract;

/**
 * Sequence_AnalysisCLI/FASTAExtractCLI
 */
@Command(name = "fasta-extract", mixinStandardHelpOptions = true, description = ToolDescriptions.fasta_extract_description, version = "ScriptManager "
		+ ToolDescriptions.VERSION, sortOptions = false, exitCodeOnInvalidInput = 1, exitCodeOnExecutionException = 1)
public class FASTAExtractCLI implements Callable<Integer> {

	@Parameters(index = "0", description = "reference genome FASTA file")
	private File genomeFASTA;
	@Parameters(index = "1", description = "the BED file of sequences to extract")
	private File bedFile;

	@Option(names = { "-o", "--output" }, description = "Specify output file (default = <bedFilename>.fa)")
	private File output = null;
	@Option(names = { "-c",
			"--coord-header" }, description = "use genome coordinate for output FASTA header (default is to use bed file headers)")
	private boolean bedHeader = true;
	@Option(names = { "-n", "--no-force" }, description = "don't force-strandedness (default is to force strandedness)")
	private boolean forceStrand = true;

	@Override
	public Integer call() throws Exception {
		System.err.println(">FASTAExtractCLI.call()");
		String validate = validateInput();
		if (!validate.equals("")) {
			System.err.println(validate);
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}

		FASTAExtract script_obj = new FASTAExtract(genomeFASTA, bedFile, output, forceStrand, bedHeader, System.err);
		script_obj.run();

		System.err.println("Extraction Complete.");
		return (0);
	}

	private String validateInput() throws IOException {
		String r = "";

		// check inputs exist
		if (!genomeFASTA.exists()) {
			r += "(!)FASTA genome ref file does not exist: " + genomeFASTA.getName() + "\n";
			return (r);
		}
		if (!bedFile.exists()) {
			r += "(!)BED file does not exist: " + bedFile.getName() + "\n";
			return (r);
		}
		// check input extensions
		ExtensionFileFilter faFilter = new ExtensionFileFilter("fa");
		if (!faFilter.accept(genomeFASTA)) {
			r += "(!)Is this a FASTA file? Check extension: " + genomeFASTA.getName() + "\n";
		}
		if (!"bed".equals(ExtensionFileFilter.getExtension(bedFile))) {
			r += "(!)Is this a BED file? Check extension: " + bedFile.getName() + "\n";
		}
		// set default output filename
		if (output == null) {
			output = new File(ExtensionFileFilter.stripExtension(bedFile) + ".fa");
			// check output filename is valid
		} else {
			// check ext
			try {
				if (!faFilter.accept(output)) {
					r += "(!)Use FASTA extension for output filename. Try: "
							+ ExtensionFileFilter.stripExtension(output) + ".fa\n";
				}
			} catch (NullPointerException e) {
				r += "(!)Output filename must have extension: use FASTA extension for output filename. Try: " + output
						+ ".fa\n";
			}
			// check directory
			if (output.getParent() == null) {
// 				System.err.println("default to current directory");
			} else if (!new File(output.getParent()).exists()) {
				r += "(!)Check output directory exists: " + output.getParent() + "\n";
			}
		}

		return (r);
	}
}