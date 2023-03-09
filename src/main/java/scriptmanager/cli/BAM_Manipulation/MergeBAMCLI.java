package cli.BAM_Manipulation;

import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import objects.ToolDescriptions;

/**
	BAM_ManipulatioCLIn/MergeBAMCLI
*/
@Command(name = "merge-bam", mixinStandardHelpOptions = true,
	description = ToolDescriptions.merge_bam_description + "\n"+
		"@|bold **Please run the picard tool directly:**|@ \n"+
		"@|bold,yellow 'java -jar picard.jar MergeSamFiles'|@",
	version = "ScriptManager "+ ToolDescriptions.VERSION,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class MergeBAMCLI implements Callable<Integer> {
	@Override
	public Integer call() throws Exception {
		System.err.println("***Please use the original tool for this job***\n"+
							"\t'java -jar picard.jar MergeSamFiles'");
		System.exit(1);
		return(1);
	}
}