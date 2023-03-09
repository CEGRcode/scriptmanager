package cli.Peak_Calling;

import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import java.io.IOException;

import objects.ToolDescriptions;

/**
	Peak_CallingCLI/PeakPairCLI
*/
@Command(name = "peak-pair", mixinStandardHelpOptions = true,
	description = ToolDescriptions.peak_pairing_description,
	version = "ScriptManager "+ ToolDescriptions.VERSION,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class PeakPairCLI implements Callable<Integer> {
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">PeakPairCLI.call()" );
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
