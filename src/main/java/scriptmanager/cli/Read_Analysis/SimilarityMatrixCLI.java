package scriptmanager.cli.Read_Analysis;

import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import scriptmanager.objects.ToolDescriptions;

import java.io.IOException;

/**
 * Command line interface class for generating a similarity matrix
 * @see scriptmanager.scripts.Read_Analysis.SimilarityMatrix
 */
@Command(name = "similarity-matrix", mixinStandardHelpOptions = true,
	description = "",
	version = "ScriptManager "+ ToolDescriptions.VERSION,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class SimilarityMatrixCLI implements Callable<Integer> {
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">SimilarityMatrixCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}
		
		//SEStats.getSEStats( output, bamFile, null );
		
		//System.err.println("Calculations Complete");
		return(0);
	}
	
	private String validateInput() throws IOException {
		String r = "";
		//validate input here
		//append messages to the user to `r`
		r += "(!)This tool is deactivated to reflect the GUI.";
		
		return(r);
	}
}
