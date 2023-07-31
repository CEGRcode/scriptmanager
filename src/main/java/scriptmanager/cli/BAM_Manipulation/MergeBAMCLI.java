package scriptmanager.cli.BAM_Manipulation;

import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import scriptmanager.objects.ToolDescriptions;

/**
 * Print a message redirecting user to the original CLI tool.
 * 
 * @author Olivia Lang
 * @see scriptmanager.scripts.BAM_Manipulation.MergeBAM
 */
@Command(name = "merge-bam", mixinStandardHelpOptions = true,
	description = ToolDescriptions.merge_bam_description + "\n"+
		"@|bold **Please run the picard tool directly:**|@ \n"+
		"@|bold,yellow 'java -jar picard.jar MergeSamFiles'|@",
	version = "ScriptManager "+ ToolDescriptions.VERSION,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class MergeBAMCLI implements Callable<Integer> {

	/**
	 * Creates a new MergeBAMCLI object
	 */
	public MergeBAMCLI(){}
	
	/**
	 * Runs when this subcommand is called, directing user to original tool
	 * @throws Exception Please use original CLI tool
	 */
	@Override
	public Integer call() throws Exception {
		System.err.println("***Please use the original tool for this job***\n"+
							"\t'java -jar picard.jar MergeSamFiles'");
		System.exit(1);
		return(1);
	}
}