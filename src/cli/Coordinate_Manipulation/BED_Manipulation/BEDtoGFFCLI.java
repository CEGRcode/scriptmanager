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
	
	@Option(names = {"-o", "--output"}, description = "Specify output file ")
	private File output = null;
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">BEDtoGFFCLI.call()" );
		
		if( validateInput()!=0 ){ System.err.println("invalid Input"); }
		
		// call scripts tools
		BEDtoGFF.convertBEDtoGFF(output, bedFile);
		System.err.println( "Conversion Complete" );
		
		return(0);
	}
	
	private Integer validateInput(){
		
		// validation done within BEDtoGFF script
		
		return(0);
	}
	
}

