package scriptmanager.cli.Peak_Calling;

import picocli.CommandLine.Command;
import java.util.concurrent.Callable;

import java.io.IOException;

import scriptmanager.objects.ToolDescriptions;

/**
 * Command line interface class that provides access to the GeneTrack script
 * @see scriptmanager.scripts.Peak_Calling.GeneTrack
 * @see scriptmanager.window_interface.Peak_Calling.GeneTrackWindow
 */
@Command(name = "gene-track", mixinStandardHelpOptions = true,
	description = ToolDescriptions.gene_track_description,
	version = "ScriptManager "+ ToolDescriptions.VERSION,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class GeneTrackCLI implements Callable<Integer> {
	
	/**
	 * Runs when this subcommand is called, running script in respective script package with user defined arguments
	 * @throws IOException Invalid file or parameters
	 */
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
