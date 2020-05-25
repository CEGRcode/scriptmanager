package cli.BAM_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

/**
	BAM_ManipulatioCLIn/BAIIndexerCLI
*/
@Command(name = "bam-indexer", mixinStandardHelpOptions = true,
		description = "Generates BAI Index for input BAM files.\n"+
			"@|bold **Please run the samtools tool directly:**|@ \n"+
			"@|bold,yellow 'samtools index <bam-file>'|@")
public class BAIIndexerCLI implements Callable<Integer> {
	@Override
	public Integer call() throws Exception {
		System.err.println("***Please use the original tool for this job***\n"+
							"\t'samtools index <bam-file>'");
		return(0);
	}
}