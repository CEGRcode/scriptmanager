package cli.BAM_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.util.concurrent.Callable;

import objects.ToolDescriptions;

/**
	BAM_ManipulatioCLIn/MergeBAMCLI
*/
@Command(name = "merge-bam", mixinStandardHelpOptions = true,
	description = ToolDescriptions.merge_bam_description + "\n"+
		"@|bold **Please run the picard tool directly:**|@ \n"+
		"@|bold,yellow 'java -jar picard.jar MergeSamFiles'|@",
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class MergeBAMCLI implements Callable<Integer> {
	@Override
	public Integer call() throws Exception {
		System.err.println( ">MergeBAMCLI.call()" );
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