package cli.BAM_Statistics;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.Vector;

import objects.ToolDescriptions;
import util.ExtensionFileFilter;
//import scripts.BAM_Statistics.SEStats;
	
/**
	BAM_StatisticsCLI/SEStatsCLI
	//java -jar build/libs/ScriptManager-0.12.jar bam-statistics se-stat <bam.in> [-o <output.filename>]
*/
@Command(name = "se-stat", mixinStandardHelpOptions = true,
	description = ToolDescriptions.se_stat_description,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class SEStatsCLI implements Callable<Integer> {
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">SEStatsCLI.call()" );
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
