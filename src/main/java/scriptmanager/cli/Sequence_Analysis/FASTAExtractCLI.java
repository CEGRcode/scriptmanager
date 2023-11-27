package scriptmanager.cli.Sequence_Analysis;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;
import java.io.File;
import java.io.IOException;

import scriptmanager.objects.ToolDescriptions;
import scriptmanager.util.ExtensionFileFilter;
import scriptmanager.scripts.Sequence_Analysis.FASTAExtract;

/**
 * Command line interface for
 * {@link scriptmanager.scripts.Sequence_Analysis.FASTAExtract}
 * 
 * @author Olivia Lang
 */
@Command(name = "fasta-extract", mixinStandardHelpOptions = true,
	description = ToolDescriptions.fasta_extract_description,
	version = "ScriptManager " + ToolDescriptions.VERSION,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class FASTAExtractCLI implements Callable<Integer> {

	@Parameters(index = "0", description = "reference genome FASTA file")
	private File genomeFASTA;
	@Parameters(index = "1", description = "the BED file of sequences to extract")
	private File bedFile;

	@Option(names = { "-o", "--output" }, description = "Specify output file (default = <bedFilename>.fa)")
	private File output = null;
	@Option(names = { "-c", "--coord-header" }, description = "use genome coordinate for output FASTA header (default is to use bed file headers)")
	private boolean bedHeader = true;
	@Option(names = { "-n", "--no-force" }, description = "don't force-strandedness (default is to force strandedness)")
	private boolean forceStrand = true;
	@Option(names = {"-z", "--gzip"}, description = "gzip output (default=false)")
	private boolean gzOutput = false;

	/**
	 * Runs when this subcommand is called, running script in respective script package with user defined arguments
	 * @throws IOException Invalid file or parameters
	 */
	@Override
	public Integer call() throws Exception {
		System.err.println(">FASTAExtractCLI.call()");
		String validate = validateInput();
		if (!validate.equals("")) {
			System.err.println(validate);
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}

		FASTAExtract script_obj = new FASTAExtract(genomeFASTA, bedFile, output, forceStrand, bedHeader, System.err, gzOutput);
		script_obj.run();

		System.err.println("Extraction Complete.");
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

		// check inputs exist
		if (!genomeFASTA.exists()) {
			r += "(!)FASTA genome ref file does not exist: " + genomeFASTA.getName() + "\n";
			return (r);
		}
		if (!bedFile.exists()) {
			r += "(!)BED file does not exist: " + bedFile.getName() + "\n";
			return (r);
		}
		// set default output filename
		if (output == null) {
			String NAME = ExtensionFileFilter.stripExtension(bedFile) + ".fa";
			output = new File(NAME);
			// check output filename is valid
		} else {
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