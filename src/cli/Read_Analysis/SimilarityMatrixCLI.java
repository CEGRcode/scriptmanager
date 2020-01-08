package cli.Read_Analysis;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;

/**
	Read_AnalysisCLI/SimilarityMatrixCLI
*/
@Command(name = "similarity-matrix", mixinStandardHelpOptions = true,
		description = "")
public class SimilarityMatrixCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "")
	private File inputFile;
	
	@Option(names = {"-o", "--output"}, description = "Specify output file ")
	private File output = new File("output.matrix");
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">SimilarityMatrixCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}
