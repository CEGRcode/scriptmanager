package cli.BAM_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

/**
	BAM_ManipulatioCLIn/MergeBAMCLI
*/
@Command(name = "merge-bam", mixinStandardHelpOptions = true,
		description = "Merges multiple BAM files into a single BAM file.\n"+
			"@|bold **Please run the picard tool directly:**|@ \n"+
			"@|bold,yellow 'java -jar picard.jar MergeSamFiles'|@")
public class MergeBAMCLI implements Callable<Integer> {
	@Override
	public Integer call() throws Exception {
		System.err.println("***Please use the original tool for this job***\n"+
							"\t'java -jar picard.jar MergeSamFiles'");
		return(0);
	}
}