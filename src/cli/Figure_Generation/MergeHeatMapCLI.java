package cli.Figure_Generation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;

import scripts.Figure_Generation.MergeHeatMapPlot;

/**
	Figure_GenerationCLI/MergeHeatMapCLI
*/
@Command(name = "merge-heatmap", mixinStandardHelpOptions = true,
		description = "Merge Sense and Antisense png heatmaps",
		sortOptions = false)
public class MergeHeatMapCLI implements Callable<Integer> {
	
	
	@Option(names = {"-1", "--input1"}, required=true, description = "First(sense) heatmap to merge")
	private File senseFile = null;
	@Option(names = {"-2", "--input2"}, description = "Second(anti) heatmap to merge")
	private File antiFile = null;
	
	@Option(names = {"-o", "--output"}, description = "specify output file (be sure to use .png ext)")
	private File output = new File("merged.png");
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">MergeHeatMapCLI.call()" );
		String validate = validateInput();
		if( validate.compareTo("")!=0 ){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			return(1);
		}
		
		// Generate Merged HeatMap
		MergeHeatMapPlot.mergePNG( senseFile, antiFile, output );
				
		System.out.println( "Image Generated." );
		return(0);
	}
	
	private String validateInput(){
		String r = "";
		if( senseFile==null && antiFile==null ){
			r += "(!)What are we merging? You gave two null file inputs!";
		}
		
		//check input extensions
		/* <ADD CODE HERE> */
		//set default output filename
// 		String out = senseFile.substring(0, name.lastIndexOf("sense"));
		/* <ADD CODE HERE> */
		//check outputbasename is valid
		/* <ADD CODE HERE> */
		//set default output filename
		/* <ADD CODE HERE> */
		
		return(r);
	}
	
}
