package cli.Coordinate_Manipulation.GFF_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;

/**
	Coordinate_ManipulationCLI/ExpandGFFCLI
*/
@Command(name = "expand-gff", mixinStandardHelpOptions = true,
	description = "Expands input GFF file by adding positions to the border or around the center")
public class ExpandGFFCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "the GFF file to expand onn")
	private File bedFile;
	
	@Option(names = {"-o", "--output"}, description = "Specify output file ")
	private File output = new File("output.gff");
	@Option(names = {"-c", "--center"}, description = "expand from center (default=250)")
	private int center = -9999;
	@Option(names = {"-b", "--border"}, description = "add to border")
	private int border = -9999;
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">ExpandGFFCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}
	