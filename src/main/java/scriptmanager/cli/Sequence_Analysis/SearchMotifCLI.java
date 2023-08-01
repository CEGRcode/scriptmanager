package scriptmanager.cli.Sequence_Analysis;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;

import scriptmanager.objects.ToolDescriptions;
import scriptmanager.util.ExtensionFileFilter;
import scriptmanager.util.FASTAUtilities;
import scriptmanager.scripts.Sequence_Analysis.SearchMotif;

/**
 * Command line interface class for searching a genomic sequence for a motif by
 * calling the methods implemented in the scripts package.
 * 
 * @author Olivia Lang
 * @see scriptmanager.scripts.Sequence_Analysis.SearchMotif
 */
@Command(name = "search-motif", mixinStandardHelpOptions = true, description = ToolDescriptions.search_motif_description, version = "ScriptManager "
		+ ToolDescriptions.VERSION, sortOptions = false, exitCodeOnInvalidInput = 1, exitCodeOnExecutionException = 1)
public class SearchMotifCLI implements Callable<Integer> {

	@Parameters(index = "0", description = "The FASTA file in which to search for the motif.")
	private File fastaFile;

	@Option(names = { "-o", "--output" }, description = "Specify output filename (default = <motif>_<num>Mismatch_<fastaFilename>.bed)")
	private File output = null;
	@Option(names = {"-z", "--gzip"}, description = "gzip output (default=false)")
	private boolean gzOutput = false;
	@Option(names = { "-m", "--motif" }, required = true, description = "the IUPAC motif to search for")
	private String motif;
	@Option(names = { "-n", "--mismatches" }, description = "the number of mismatches allowed (default=0)")
	private int ALLOWED_MISMATCH = 0;

	@Override
	public Integer call() throws Exception {
		System.err.println(">SearchMotifCLI.call()");
		String validate = validateInput();
		if (!validate.equals("")) {
			System.err.println(validate);
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}

		SearchMotif script_obj = new SearchMotif(fastaFile, motif, ALLOWED_MISMATCH, output, System.err, gzOutput);
		script_obj.run();

		System.err.println("Search Complete.");
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

		// check inputs exist
		if (!fastaFile.exists()) {
			r += "(!)FASTA file does not exist: " + fastaFile.getName() + "\n";
			return (r);
		}
		// check input extensions
		ExtensionFileFilter faFilter = new ExtensionFileFilter("fa");
		if (!faFilter.accept(fastaFile)) {
			r += "(!)Is this a FASTA file? Check extension: " + fastaFile.getName() + "\n";
		}
		// set default output filename
		if (output == null) {
			String NAME = motif + "_" + Integer.toString(ALLOWED_MISMATCH) + "Mismatch_"
					+ ExtensionFileFilter.stripExtension(fastaFile) + ".bed";
			NAME += gzOutput ? ".gz" : "";
			output = new File(NAME);
			// check output filename is valid
		} else {
			// check ext
			try {
				if (!"bed".equals(ExtensionFileFilter.getExtension(output))) {
					r += "(!)Use BED extension for output filename. Try: " + ExtensionFileFilter.stripExtension(output)
							+ ".bed\n";
				}
			} catch (NullPointerException e) {
				r += "(!)Output filename must have extension: use BED extension for output filename. Try: " + output
						+ ".bed\n";
			}
			// check directory
			if (output.getParent() == null) {
// 				System.err.println("default to current directory");
			} else if (!new File(output.getParent()).exists()) {
				r += "(!)Check output directory exists: " + output.getParent() + "\n";
			}
		}

		// check filter string is valid IUPAC
		if (!FASTAUtilities.isValidIUPACString(motif)) {
			r += "(!)Motif string must be formatted as an IUPAC sequence.\n" + motif
					+ " is not a valid nucleotide sequence.\nExpected input string format: \"[ATGCRYSWKMBDHVN]+\"";
		}

		// check mismatch value
		if (ALLOWED_MISMATCH < 0) {
			r += "(!)Please use a non-negative integer for allowed mismatches.";
		}

		return (r);
	}

	public static String getCLIcommand(File input, File output, String motif, int mismatch, boolean gzOutput) {
		String command = "java -jar $SCRIPTMANAGER sequence-analysis SearchMotif";
		command += " " + input.getAbsolutePath();
		command += " -o " + output.getAbsolutePath();
		command += " -m " + motif;
		command += " -n " + mismatch;
		command += gzOutput ? " -z" : "";
		return command;
	}
}