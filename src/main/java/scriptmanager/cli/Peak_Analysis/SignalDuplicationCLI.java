package scriptmanager.cli.Peak_Analysis;

import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import java.io.IOException;

import scriptmanager.objects.ToolDescriptions;
	
/**
	Peak_AnalysisCLI/SignalDuplicationCLI
*/
@Command(name = "signal-dup", mixinStandardHelpOptions = true,
	description = ToolDescriptions.signal_dup_description,
	version = "ScriptManager "+ ToolDescriptions.VERSION,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class SignalDuplicationCLI implements Callable<Integer> {
	
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
