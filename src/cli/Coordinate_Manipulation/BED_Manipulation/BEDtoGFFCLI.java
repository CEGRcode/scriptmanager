package cli.Coordinate_Manipulation.BED_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;

import scripts.Coordinate_Manipulation.BED_Manipulation.BEDtoGFF;

/**
	Coordinate_ManipulationCLI/BEDtoGFFCLI
*/
@Command(name = "bed-to-gff", mixinStandardHelpOptions = true,
	description = "Converts BED file format to GFF file format")
public class BEDtoGFFCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "the BED file to convert")
	private File bedFile;
	
	@Option(names = {"-o", "--output"}, description = "specify output directory (name will be same as original with .gff ext)")
	private File output;
	
	@Override
	public Integer call() throws Exception {
		
		//if( validateInput()!=0 ){ System.err.println("Invalid input. Check usage using '-h' or '--help'"); }
		BEDtoGFF.convertBEDtoGFF(output, bedFile);
		System.err.println( "Conversion Complete" );
		
		return(0);
	}
	
}