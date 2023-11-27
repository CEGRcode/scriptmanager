package scriptmanager.cli.BAM_Manipulation;

import picocli.CommandLine.Command;

import java.io.IOException;
import java.util.concurrent.Callable;

import scriptmanager.objects.ToolDescriptions;

/**
 * Prints a message redirecting user to the original CLI tool (Picard
 * {@link picard.sam.SortSam})
 * 
 * @author Olivia Lang
 * @see scriptmanager.scripts.BAM_Manipulation.BAMFileSort
 */
@Command(name = "sort-bam", mixinStandardHelpOptions = true,
	description = ToolDescriptions.sort_bam_description + "\n"+
		"@|bold **Please run the samtools tool directly:**|@ \n"+
		"@|bold,yellow 'samtools sort -o <output.bam> <input.bam>'|@",
	version = "ScriptManager "+ ToolDescriptions.VERSION,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class SortBAMCLI implements Callable<Integer> {
	/**
	 * Runs when this subcommand is called, running script in respective script package with user defined arguments
	 * @throws IOException Invalid file or parameters
	 */
	@Override
	public Integer call() throws Exception {
		System.err.println("***Please use the original tool for this job***\n"+
							"\t'samtools sort -o <output.bam> <input.bam>'");
		System.exit(1);
		return(1);
	}
}
