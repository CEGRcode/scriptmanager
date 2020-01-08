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
	Peak_AnalysisCLI/TileGenomeCLI
*/
@Command(name = "tile-genome", mixinStandardHelpOptions = true,
		description = "Generate a coordinate file that tiles (non-overlapping) across an entire genome.")
public class TileGenomeCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BAM file whose statistics we want.")
	private File bamFile;

	@Option(names = {"-g", "--genome"}, description = "reference genome [sacCer3_cegr|hg19|hg19_contigs|mm10]")
	private File genome = new File("sacCer3_cegr");
	@Option(names = {"-o", "--output"}, description = "specify output file ")
	private File output = new File("output.txt");
	@Option(names = {"-f", "--format"}, description = "[BED|GFF] input file format output (default=BED)")
	private String format = "BED";
	@Option(names = {"-w", "--window"}, description = "window size in bp (default=200)")
	private int window = 200;
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">TileGenomeCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}
