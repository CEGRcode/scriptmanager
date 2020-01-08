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
	
	@Option(names = {"-o", "--output"}, description = "Specify output file ")
	private File output;
	@Option(names = {"-c", "--center"}, description = "expand from center (default=250)")
	private boolean center = false;
	@Option(names = {"-b", "--border"}, description = "add to border")
	private boolean border = false;
	@Option(names = {"-s", "--size"}, description = "num bp to expand BED file by")
	private int SIZE = 250;
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">ExpandBEDCLI.call()" );
		
		if( validateInput()!=0 ){ System.err.println("invalid Input"); }
		
		if( output == null){
			String new_out_dir = bedFile.getAbsoluteFile().getParent();
			output = new File( new_out_dir );
			System.out.println( output.isDirectory() );
			output = new File( new_out_dir + "/expanded_output.bed" );
		}
		
// 		ExpandBED.expandBEDBorders(OUTPUT_PATH, bedFile, SIZE, center);
		System.err.println("Conversion Complete");
		
		return(0);
	}
	
	public Integer validateInput(){
		
		assert SIZE < 1;
		assert border || center;
		assert !(border && center);
		
		return(0);
	}
	
}
	