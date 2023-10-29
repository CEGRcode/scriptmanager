package scriptmanager.cli.BAM_Manipulation;

import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import htsjdk.samtools.SAMFileHeader;

import java.io.File;

import scriptmanager.objects.ToolDescriptions;

/**
 * Print a message redirecting user to the original CLI tool.
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
	@Override
	public Integer call() throws Exception {
		System.err.println("***Please use the original tool for this job***\n"+
							"\t'samtools sort -o <output.bam> <input.bam>'");
		System.exit(1);
		return(1);
	}

	/**
	 * Reconstruct CLI command
	 * 
	 * @param input  the BAM file to be sorted (corresponds to INPUT)
	 * @param output the file to write the sorted BAM to (corresponds to OUTPUT)
	 * @return command line to execute with formatted inputs
	 */
	public static String getCLIcommand(File input, File output) {
		String command = "java -jar $PICARD SortSam";
		command += " INPUT=" + input.getAbsolutePath();
		command += " OUTPUT=" + output.getAbsolutePath();
		command += " SORT_ORDER=" + SAMFileHeader.SortOrder.coordinate;
		return(command);
	}
}
