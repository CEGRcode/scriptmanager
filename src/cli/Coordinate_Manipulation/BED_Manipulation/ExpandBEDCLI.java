package cli.Coordinate_Manipulation.BED_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;

import scripts.Coordinate_Manipulation.BED_Manipulation.ExpandBED;

/**
	Coordinate_ManipulationCLI/ExpandBEDCLI
*/
@Command(name = "expand-bed", mixinStandardHelpOptions = true,
	description = "Expands input BED file by adding positions to the border or around the center")
public class ExpandBEDCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "the BED file to expand on")
	private File bedFile;
	
	@Option(names = {"-o", "--output"}, description = "specify output directory (name will be same as original with coordinate info appended)")
	private File output;
	@Option(names = {"-c", "--center"}, description = "expand from center (default=250)")
	private boolean center;
	@Option(names = {"-b", "--border"}, description = "add to border")
	private boolean border;
	@Option(names = {"-s", "--size"}, description = "num bp to expand BED file by")
	private int SIZE = 250;
	
	@Override
	public Integer call() throws Exception {
		
		if( validateInput()!=0 ){
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
		}
		
		ExpandBED.expandBEDBorders(output, bedFile, SIZE, center);
		System.err.println("Expansion Complete");
		
		return(0);
	}
	
	private Integer validateInput(){
		
		int return_val = 0;
		
		// Define default behavior
		if( !center && !border ){ center = true; }
		else if( border && center ){
			System.err.println( "!!!Both border and center cannot be flagged at the same time!" );
			return_val++;
		}
		
		if( SIZE <= 0 ){
			System.err.println( "!!!Invalid size input. Must be a positive integer greater than 0." );
			return_val++;
		}
		
		return(return_val);
	}
	
}
	