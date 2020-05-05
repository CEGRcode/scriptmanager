package cli.Coordinate_Manipulation.GFF_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.FileNotFoundException;

import scripts.Coordinate_Manipulation.GFF_Manipulation.GFFtoBED;

/**
	Coordinate_ManipulationCLI/GFFtoBEDCLI
*/
@Command(name = "gff-to-bed", mixinStandardHelpOptions = true,
	description = "Converts GFF file format to BED file format")
public class GFFtoBEDCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "the GFF file to convert")
	private File gffFile;
	
	@Option(names = {"-o", "--output"}, description = "specify output directory (name will be same as original with .bed ext)")
	private File output;
	
	@Override
	public Integer call() throws Exception {
		
		//if( validateInput()!=0 ){ System.err.println("Invalid input. Check usage using '-h' or '--help'"); }
		GFFtoBED.convertGFFtoBED(output, gffFile);
		System.err.println( "Conversion Complete" );
        	
		return(0);
	}
}