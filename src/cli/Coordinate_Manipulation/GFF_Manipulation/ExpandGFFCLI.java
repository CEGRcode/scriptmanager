package cli.Coordinate_Manipulation.GFF_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;

import scripts.Coordinate_Manipulation.GFF_Manipulation.ExpandGFF;

/**
	Coordinate_ManipulationCLI/ExpandGFFCLI
*/
@Command(name = "expand-gff", mixinStandardHelpOptions = true,
	description = "Expands input GFF file by adding positions to the border or around the center")
public class ExpandGFFCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "the GFF file to expand on")
	private File bedFile;
	
	@Option(names = {"-o", "--output"}, description = "specify output directory (name will be same as original with coordinate info appended)")
	private File output;
	@Option(names = {"-c", "--center"}, description = "expand from center (default=250)")
	private boolean center;
	@Option(names = {"-b", "--border"}, description = "add to border")
	private boolean border;
	@Option(names = {"-s", "--size"}, description = "num bp to expand GFF file by")
	private int SIZE = 250;
	
	@Override
	public Integer call() throws Exception {
		
		if( validateInput()!=0 ){
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
		}
// 		System.err.println("center="+Boolean.toString(center)+"\nborder="+Boolean.toString(border)+"\nsize="+Integer.toString(SIZE));
		ExpandGFF.expandGFFBorders(output, bedFile, SIZE, center);
		System.err.println("Expansion Complete");
		
		return(0);
	}
	
	private Integer validateInput(){
		
		int return_val = 0;
		// Define default behavior
		if( !center && !border ){ center = true; }
		// Check for invalid inputs
		if( output!=null && !output.isDirectory() ){
			System.err.println("!!!Output must be a directory! Unable to specify specific name at this time.");
			return_val++;
		}
		if( border && center ){
			System.err.println( "!!!Both border and center cannot be flagged at the same time!" );
			return_val++;
		}
		if( SIZE > 0 ){
			System.err.println( "!!!Invalid size input. Must be a positive integer greater than 0." );
			return_val++;
		}
		
		return(return_val);
	}
	
}
	