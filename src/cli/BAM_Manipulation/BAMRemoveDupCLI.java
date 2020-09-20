package cli.BAM_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.util.concurrent.Callable;

import objects.ToolDescriptions;

/**
	BAM_ManipulatioCLIn/BAMRemoveDupCLI
*/
@Command(name = "remove-duplicates", mixinStandardHelpOptions = true,
	description = ToolDescriptions.remove_duplicates_description + "\n"+
		"@|bold **Please run the picard/samtools tools directly:**|@ \n"+
		"@|bold,yellow 'java -jar picard.jar MarkDuplicates I=<in.bam> O=<marked.bam>\n"+
		"samtools view -F 1024 <marked.bam> > <out.bam>'|@",
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class BAMRemoveDupCLI implements Callable<Integer> {
	@Override
	public Integer call() throws Exception {
		System.err.println( ">BAMRemoveDupCLI.call()" );
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