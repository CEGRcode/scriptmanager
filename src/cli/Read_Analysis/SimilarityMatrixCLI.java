package cli.Read_Analysis;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;

/**
	Read_AnalysisCLI/SimilarityMatrixCLI
*/
@Command(name = "similarity-matrix", mixinStandardHelpOptions = true,
		description = "",
		sortOptions = false)
public class SimilarityMatrixCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "")
	private File inputFile;
	
	@Option(names = {"-o", "--output"}, description = "Specify output file ")
	private File output = new File("output.matrix");
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">SimilarityMatrixCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			return(1);
		}
		
		return(0);
	}
	
	private String validateInput() throws IOException {
		String r = "";
		
		return(r);
	}
	
}
