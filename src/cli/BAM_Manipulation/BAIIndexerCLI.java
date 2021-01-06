package cli.BAM_Manipulation;

import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import objects.ToolDescriptions;

/**
	BAM_ManipulatioCLIn/BAIIndexerCLI
*/
@Command(name = "bam-indexer", mixinStandardHelpOptions = true,
	description = ToolDescriptions.bam_indexer_description + "\n"+
		"@|bold **Please run the samtools tool directly:**|@ \n"+
		"@|bold,yellow 'samtools index <bam-file>'|@",
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class BAIIndexerCLI implements Callable<Integer> {
	@Override
	public Integer call() throws Exception {
		System.err.println("***Please use the original tool for this job***\n"+
							"\t'samtools index <bam-file>'");
		System.exit(1);
		return(1);
	}
}