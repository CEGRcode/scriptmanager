package cli.Peak_Calling;

import picocli.CommandLine.Command;
import java.util.concurrent.Callable;

import java.io.IOException;

import objects.ToolDescriptions;

/**
	Peak_CallingCLI/GeneTrackCLI
*/
@Command(name = "gene-track", mixinStandardHelpOptions = true,
	description = ToolDescriptions.gene_track_description,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class GeneTrackCLI implements Callable<Integer> {
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">GeneTrackCLI.call()" );
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
		return(r);
	}
}
