package cli.Coordinate_Manipulation.GFF_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;

/**
	Coordinate_ManipulationCLI/SortGFFCLI
*/
@Command(name = "sort-gff", mixinStandardHelpOptions = true,
	description = "Sort a GFF file")
public class SortGFFCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "the GFF file to sort")
	private File bedFile;
	
	@Option(names = {"-r", "--reference"}, description = "the reference CDT file to sort the input by")
	private File reference;
	@Option(names = {"-o", "--output"}, description = "Specify output file ")
	private File output = new File("output.gff");
	@Option(names = {"-c", "--center"}, description = "sort by center on the input size of expansion in bins (default=100)")
	private int center = 100;
	@Option(names = {"-x", "--index"}, description = "sort by index fromt he specified start to the specified stop",
		arity = "2")
	private int[] index = new int[]{9999,-9999};
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">SortGFFCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}
