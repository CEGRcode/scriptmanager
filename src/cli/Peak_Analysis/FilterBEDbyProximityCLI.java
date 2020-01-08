package cli.Peak_Analysis;

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
	Peak_AnalysisCLI/FilterBEDbyProximityCLI
*/
@Command(name = "filter-bed", mixinStandardHelpOptions = true,
		description = "Filter BED file using user-specified exclusion zone using the score column to determine which peak to retain.")
public class FilterBEDbyProximityCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BED file we are filtering on")
	private File bedFile;
	
	@Option(names = {"-o", "--output"}, description = "specify output file ")
	private File output = new File("output.txt");
	@Option(names = {"-e", "--exclusion"}, description = "exclusion distance in bp (default=100)")
	private int exclusion = 100;
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">FilterBEDbyProximityCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}

