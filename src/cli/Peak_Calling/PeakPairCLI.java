package cli.Peak_Calling;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;

/**
	Peak_CallingCLI/PeakPairCLI
*/
@Command(name = "peak-pair", mixinStandardHelpOptions = true,
		description = "Peak-pairing algorithm")
public class PeakPairCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "")
	private File inputFile;
	
	@Option(names = {"-o", "--output"}, description = "Specify output file ")
	private File output = new File("output.matrix");
	
// 	GOTTA FIGURE OUT USAGE...
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">PeakPairCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}
