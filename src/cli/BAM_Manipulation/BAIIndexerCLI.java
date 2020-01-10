package cli.BAM_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;

import scripts.BAM_Manipulation.BAIIndexer;

/**
	BAM_ManipulatioCLIn/BAIIndexerCLI
*/
@Command(name = "bam-indexer", mixinStandardHelpOptions = true,
// 		description = "Generates BAI Index for input BAM files. Output BAI is the same directory as input BAM file.")
		description = "Generates BAI Index for input BAM files.\n@|bold **Please run the samtools sort directly:**|@ \n@|bold,yellow 'samtools index <bam-file>'|@")
public class BAIIndexerCLI implements Callable<Integer> {
	
// 	@Parameters( index = "0", description = "The BAM file from which we generate an index file.")
// 	private File bamFile;
	
	@Override
	public Integer call() throws Exception {
		System.err.println("***Please use the original tool for this job 'samtools index <bam-file>'***");
// 		System.out.println( ">BAIIndexerCLI.call()" );
// 		BAIIndexer.generateIndex(bamFile);
		return(0);
	}
	
}
	