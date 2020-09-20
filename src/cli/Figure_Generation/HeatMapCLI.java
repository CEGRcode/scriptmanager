package cli.Figure_Generation;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.lang.NullPointerException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import objects.ToolDescriptions;
import util.ExtensionFileFilter;
//import scripts.Figure_Generation.HeatmapPlot;

/**
	Figure_GenerationCLI/HeatMapCLI
*/
@Command(name = "heatmap", mixinStandardHelpOptions = true,
	description = ToolDescriptions.heatmap_description,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class HeatMapCLI implements Callable<Integer> {
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">HeatMapCLI.call()" );
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
