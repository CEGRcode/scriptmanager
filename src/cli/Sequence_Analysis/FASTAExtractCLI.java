package cli.Sequence_Analysis;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Date;
	
/**
	Sequence_AnalysisCLI/FASTAExtractCLI
*/
@Command(name = "fasta-extract", mixinStandardHelpOptions = true,
		description = "Generate FASTA file from indexed Genome FASTA file and BED file. Script will generate FAI index if not present in Genome FASTA folder.")
public class FASTAExtractCLI implements Callable<Integer> {

	@Option(names = {"-g", "--genome"}, description = "reference genome FASTA file",
			required = true)
	private File genome;
	@Option(names = {"-i", "--input"}, description = "the BED file of sequences to extract",
			required = true)
	private File input;
	@Option(names = {"-o", "--output"}, description = "Specify output file ")
	private File output = new File("output.txt");
	@Option(names = {"--bed-header"}, description = "use BED name for output FASTA header (default)")
	private boolean bedHeader = false;
	@Option(names = {"--coord-header"}, description = "use genome coordinate for output FASTA header")
	private boolean coordHeader = false;
	@Option(names = {"-f","--force"}, description = "force-strandedness (default)")
	private boolean force = true;
	
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">FASTAExtractCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}
