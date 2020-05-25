package cli.BAM_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

/**
	BAM_ManipulatioCLI/SortBAMCLI
*/
@Command(name = "sort-bam", mixinStandardHelpOptions = true,
		description = "Sort BAM files in order to efficiently extract and manipulate.\n"+
			"@|bold **Please run the samtools tool directly:**|@ \n"+
			"@|bold,yellow 'samtools sort -o <output.bam> <input.bam>'|@")
public class SortBAMCLI implements Callable<Integer> {
	@Override
	public Integer call() throws Exception {
		System.err.println("***Please use the original tool for this job***\n"+
							"\t'samtools sort -o <output.bam> <input.bam>'");
		return(0);
	}
}
