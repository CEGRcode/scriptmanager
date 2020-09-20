package cli.BAM_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.lang.NullPointerException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import objects.ToolDescriptions;
import util.FASTAUtilities;
import util.ExtensionFileFilter;
//import scripts.BAM_Manipulation.FilterforPIPseq;

/**
	BAM_ManipulatioCLIn/FilterforPIPseqCLI
*/
@Command(name = "filter-pip-seq", mixinStandardHelpOptions = true,
	description = ToolDescriptions.filter_pip_seq_description + "\n" +
				"Note this program does not index the resulting BAM file and user must use appropriate samtools command to generate BAI.",
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class FilterforPIPseqCLI implements Callable<Integer> {
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">FilterforPIPseqCLI.call()" );
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
