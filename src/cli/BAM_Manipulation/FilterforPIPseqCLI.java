package cli.BAM_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;

/**
	BAM_ManipulatioCLIn/FilterforPIPseqCLI
*/
@Command(name = "filter-pip-seq", mixinStandardHelpOptions = true,
		description = "Filter BAM file by -1 nucleotide. Requires genome FASTA file.")
public class FilterforPIPseqCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BAM file from which we filter.")
	private File bamFile;
	
	@Option(names = {"-g", "--genome"}, description = "reference genome FASTA file")
	private File genome;
	@Option(names = {"-o", "--output"}, description = "specify output file ")
	private File output = new File( "output.bam" );
	@Option(names = {"-f", "--filter"}, description = "filter by upstream sequence (default seq ='T')")
	private String filterString = "T";
	@Option(names = {"-n", "--no-bai"}, description = "suppress re-generating BAI index for new BAM file.")
	private boolean baiFile = true;
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">FilterforPIPseqCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}
