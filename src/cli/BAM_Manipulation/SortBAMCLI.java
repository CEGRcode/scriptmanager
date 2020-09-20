package cli.BAM_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.util.concurrent.Callable;

import objects.ToolDescriptions;

/**
	BAM_ManipulatioCLI/SortBAMCLI
*/
@Command(name = "sort-bam", mixinStandardHelpOptions = true,
	description = ToolDescriptions.sort_bam_description + "\n"+
		"@|bold **Please run the samtools tool directly:**|@ \n"+
		"@|bold,yellow 'samtools sort -o <output.bam> <input.bam>'|@",
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class SortBAMCLI implements Callable<Integer> {
	@Override
	public Integer call() throws Exception {
		System.err.println( ">SortBAMCLI.call()" );
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