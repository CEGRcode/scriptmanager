package cli.Figure_Generation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;
import java.util.List;

import java.io.File;

import scripts.Figure_Generation.MergeHeatMapPlot;

/**
	Figure_GenerationCLI/MergeHeatMapCLI
*/
@Command(name = "merge-heatmap", mixinStandardHelpOptions = true,
		description = "Merge Sense and Antisense png heatmaps")
public class MergeHeatMapCLI implements Callable<Integer> {
	
	
	@Option(names = {"-s", "--sense"}, required=true, description = "First(sense) heatmap to merge")
	private File senseFile = null;
	@Option(names = {"-a", "--anti"}, description = "Second(anti) heatmap to merge")
	private File antiFile = null;
	
	@Option(names = {"-o", "--output"}, description = "specify output file (be sure to use .png ext)")
	private File output = new File("merged.png");
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">MergeHeatMapCLI.call()" );
		
		if( validateInput()!=0 ){
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			return(1);
		}
		
		// Generate Merged HeatMap
		MergeHeatMapPlot.mergePNG( senseFile, antiFile, output );
				
		System.out.println( "Image Generated." );
			
		return(0);
	}
	
	private int validateInput(){
		
		if( senseFile==null && antiFile==null ){
			System.err.println("!!!What are we merging? You gave two null file inputs!");
			return(1);
		}
		
		//check input extensions
		
		//check outputbasename is valid
		
		//Print warnings if not named _anti and _sense
		
		//Possibly print warnings when image has color values that dont match red or blue?
		
		return(0);
	}
	
}
