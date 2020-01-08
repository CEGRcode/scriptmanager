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
	Sequence_AnalysisCLI/SearchMotifCLI
*/
@Command(name = "search-motif", mixinStandardHelpOptions = true,
		description = "Search for an IUPAC DNA sequence motif in FASTA files with mismatches allowed")
public class SearchMotifCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The FASTA file in which to search for the motif.")
	private File bamFile;
	
	@Option(names = {"-o", "--output"}, description = "specify output file")
	private File output = new File("output.txt");
	@Option(names = {"-m", "--motif"}, description = "the IUPAC motif to search for")
	private String motif = "";
	@Option(names = {"-n", "--mismatches"}, description = "the number of mismatches allowed (default=0)")
	private int numMismatch = 0;
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">SearchMotifCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}
