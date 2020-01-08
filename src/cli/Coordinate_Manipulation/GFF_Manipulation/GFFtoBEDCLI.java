package cli.Coordinate_Manipulation.GFF_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;

import scripts.Coordinate_Manipulation.GFF_Manipulation.GFFtoBED;

/**
	Coordinate_ManipulationCLI/GFFtoBEDCLI
*/
@Command(name = "gff-to-bed", mixinStandardHelpOptions = true,
	description = "Converts GFF file format to BED file format")
public class GFFtoBEDCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "the GFF file to convert")
	private File gffFile;
	
	@Option(names = {"-o", "--output"}, description = "Specify output file ")
	private File output = new File("output.bed");
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">GFFtoBEDCLI.call()" );
		
		
		if( validateInput()!=0 ){ System.err.println("invalid Input"); }
		
		// call scripts tools
		GFFtoBED.convertGFFtoBED(output, gffFile);
		System.err.println( "Conversion Complete" );
        
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
	public Integer validateInput(){
		
		// validation done within GFFtoBED script
		
		return(0);
	}
	
}