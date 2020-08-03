package cli.Peak_Analysis;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;

import util.ExtensionFileFilter;
import scripts.Peak_Analysis.RandomCoordinate;

/**
	Peak_AnalysisCLI/RandomCoordinateCLI
*/
@Command(name = "rand-coord", mixinStandardHelpOptions = true,
		description = "Generate a coordinate file that tiles (non-overlapping) across an entire genome.",
		sortOptions = false)
public class RandomCoordinateCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "reference genome [sacCer3_cegr|hg19|hg19_contigs|mm10]")
	private String genomeName;
	
	@Option(names = {"-o", "--output"}, description = "Specify output directory (default = current working directory), file name will be random_coordinates_<genomeName>_<window>bp.<ext>")
	private File output = null;
	@Option(names = {"-f", "--gff"}, description = "file format output as GFF (default format as BED)")
	private boolean formatIsBed = true;
	@Option(names = {"-n", "--num-sites"}, description = "number of sites (default=1000)")
	private int numSites = 1000;
	@Option(names = {"-w", "--window"}, description = "window size in bp (default=200)")
	private int window = 200;
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">RandomCoordinateCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			return(1);
		}
		
		RandomCoordinate script_obj = new RandomCoordinate(genomeName, numSites, window, formatIsBed, output); 
		script_obj.execute();
		
		System.err.println( "Random Coordinate Generation Complete." );
		return(0);
	}
	
	private String validateInput() throws IOException {
		String r = "";
		
		//check input genomes are valid
		if(genomeName.equals("sacCer3_cegr") || genomeName.equals("hg19") || genomeName.equals("hg19_contigs") || genomeName.equals("mm10") ){
// 			System.err.println("Input genomeName is valid");
		}else{
			r += "(!)Invalid genomeName selected(" +genomeName+ "), please select from one of the provided genomes: sacCer3_cegr, hg19, hg19_contigs, and mm10\n";
		}
		String ext = "gff";
		if(formatIsBed){ ext = "bed"; }
		//set default output filename
		if(output==null){
			output = new File("random_coordinates_" + genomeName + "_" + numSites + "sites_" + window + "bp." + ext);
		//check output filename is valid
		}else{
			//check ext
			try{
				if(!ext.equals(ExtensionFileFilter.getExtension(output))){
					r += "(!)Use \"." + ext.toUpperCase() + "\" extension for output filename. Try: " + ExtensionFileFilter.stripExtension(output) + "." + ext + "\n";
				}
			} catch( NullPointerException e){ r += "(!)Output filename must have extension: use \"." + ext.toUpperCase() + "\" extension for output filename. Try: " + output + "." + ext + "\n"; }
			//check directory
			if(output.getParent()==null){
	// 			System.err.println("default to current directory");
			} else if(!new File(output.getParent()).exists()){
				r += "(!)Check output directory exists: " + output.getParent() + "\n";
			}
		}
		
		//check number of sites
		if( numSites<1 ){
			r += "(!)Number of sites needs to be a positive integer.\n";
		}
		//check window size
		if( window<1 ){
			r += "(!)Window size needs to be a positive integer.\n";
		}
		
		return(r);
	}
}