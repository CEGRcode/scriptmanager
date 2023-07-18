package scriptmanager.cli.Peak_Analysis;

import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import java.io.IOException;

import scriptmanager.objects.ToolDescriptions;
	
/**
 * Command line interface class for creating a coordinate file of random sites across a genome with SignalDuplication script
 * @see scriptmanager.scripts.Peak_Analysis.SignalDuplication
 */
@Command(name = "signal-dup", mixinStandardHelpOptions = true,
	description = ToolDescriptions.signal_dup_description,
	version = "ScriptManager "+ ToolDescriptions.VERSION,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class SignalDuplicationCLI implements Callable<Integer> {
	
	/**
	 * Runs when this subcommand is called, running script in respective script package with user defined arguments
	 * @throws IOException Invalid file or parameters
	 */
	@Override
	public Integer call() throws Exception {
		System.err.println( ">SignalDuplicationCLI.call()" );
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
