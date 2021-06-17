package cli.BAM_Manipulation;

import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import objects.ToolDescriptions;

/**
	BAM_ManipulatioCLI/SortBAMCLI
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
}
