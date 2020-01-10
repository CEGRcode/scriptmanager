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

import scripts.Peak_Analysis.TileGenome;

/**
	Peak_AnalysisCLI/TileGenomeCLI
*/
@Command(name = "tile-genome", mixinStandardHelpOptions = true,
		description = "Generate a coordinate file that tiles (non-overlapping) across an entire genome.")
public class TileGenomeCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "reference genome [sacCer3_cegr|hg19|hg19_contigs|mm10]")
	private String genome;
	
	@Option(names = {"-o", "--output"}, description = "specify output directory (name will be <genome>_<window>bp.<ext>)")
	private File output;
	@Option(names = {"-f", "--format"}, description = "[BED|GFF] input file format output (default=BED)")
	private String format = "BED";
	@Option(names = {"-w", "--window"}, description = "window size in bp (default=200)")
	private int window = 200;
	
	private boolean formatIsBed = true;
	
	@Override
	public Integer call() throws Exception {
		if( validateInputs()!=0 ){ System.err.println("Invalid input. Check usage using '-h' or '--help'"); }
		TileGenome tg = new TileGenome(genome, window, formatIsBed, output);
		tg.execute();
		return(0);
	}
	
	private Integer validateInputs() {
		
		int return_val = 0;
		// validation of outputs only directory for now
		if( output!=null && !output.isDirectory() ){
			System.err.println("Output must be a directory! Unable to specify specific name at this time.");
			return_val++;
		}
		// BED or GFF formats allowed
		if( format.compareTo("GFF")==0 ){
			formatIsBed = false;
		} else if( format.compareTo("BED")==0){
			formatIsBed = true;
		} else{ 
			System.err.println("!!!Check the format string");
			return_val++;
		}
		// window size
		if( window<1 ){
			System.err.println("!!!Window size needs to be a positive integer");
			return_val++;
		}
		return( return_val );
	}
}
