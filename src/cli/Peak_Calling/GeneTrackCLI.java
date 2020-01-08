package cli.Peak_Calling;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;

/**
	Peak_CallingCLI/GeneTrackCLI
*/
@Command(name = "gene-track", mixinStandardHelpOptions = true,
		description = "Genetrack peak-calling algorithm")
public class GeneTrackCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "")
	private File inputFile;
	
	@Option(names = {"-o", "--output"}, description = "specify output file ")
	private File output = new File("output.matrix");
	@Option(names = {"-s", "--sigma"}, description = "sigma value (default=5)")
	private int sigma = 5;
	@Option(names = {"-e", "--exclusion"}, description = "exclusion zone (default=20)")
	private int exclusion = 20;
	@Option(names = {"-f", "--filter"}, description = "min tags per peak (default=1)")
	private int filter = 1;
	@Option(names = {"-u", "--width"}, description = "set peak up width (default=?????)")
	private int width = 1;
	@Option(names = {"-d", "--down-width"}, description = "set peak down width (default=?????)")
	private int downWidth = 1;
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">GeneTrackCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}
