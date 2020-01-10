package cli.BAM_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;

import scripts.BAM_Manipulation.BAMFileSort;


/**
	BAM_ManipulatioCLI/SortBAMCLI
*/
@Command(name = "sort-bam", mixinStandardHelpOptions = true,
		description = "Sort BAM files in order to efficiently extract and manipulate.\n@|bold **Please run the samtools sort directly:**|@ \n@|bold,yellow 'samtools sort -o <output.bam> <input.bam>'|@")
public class SortBAMCLI implements Callable<Integer> {
	
	@Override
	public Integer call() throws Exception {
		System.err.println("***Please use the original tool for this job 'samtools sort -o <output.bam> <input.bam>'***");
		return(0);
	}
	
}