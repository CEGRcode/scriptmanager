package cli.Coordinate_Manipulation.BED_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;

import util.ExtensionFileFilter;
import scripts.Coordinate_Manipulation.BED_Manipulation.ExpandBED;

/**
	Coordinate_ManipulationCLI/ExpandBEDCLI
*/
@Command(name = "expand-bed", mixinStandardHelpOptions = true,
	description = "Expands input BED file by adding positions to the border or around the center",
	sortOptions = false)
public class ExpandBEDCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "the BED file to expand on")
	private File bedFile;
	
	@Option(names = {"-o", "--output"}, description = "specify output directory (name will be same as original with coordinate info appended)")
	private File output = null;
	@Option(names = {"-s", "--stdout"}, description = "output bed to STDOUT")
	private boolean stdout = false;
	
	@ArgGroup(validate = false, heading = "%nType of Expansion%n")
	ExpandType expandType = new ExpandType();
	static class ExpandType {
		@Option(names = {"-c", "--center"}, description = "expand from center (default, at 250bp)")
		private int center = -999;
		@Option(names = {"-b", "--border"}, description = "add to border")
		private int border = -999;
	}
	
	private boolean byCenter = true;
	private int SIZE = 250;
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">ExpandBEDCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			return(1);
		}
		
		ExpandBED.expandBEDBorders(output, bedFile, SIZE, byCenter);
		
		System.err.println("Expansion Complete");
		return(0);
	}
	
	private String validateInput() throws IOException {
		String r = "";
		
		//check inputs exist
		if(!bedFile.exists()){
			r += "(!)BED file does not exist: " + bedFile.getName() + "\n";
			return(r);
		}
		//check input extensions
		if(!"bed".equals(ExtensionFileFilter.getExtension(bedFile))){
			r += "(!)Is this a BED file? Check extension: " + bedFile.getName() + "\n";
		}
		//set default output filename
		if(output==null &&  !stdout){
			output = new File(ExtensionFileFilter.stripExtension(bedFile) + "_" + Integer.toString(SIZE) + "bp.bed");
		//check stdout and output not both selected
		}else if(stdout){
			if(output!=null){ r += "(!) Must choose either STDOUT or outputfilename, not both"; }
		//check output filename is valid
		}else{
			//check ext
			try{
				if(!"bed".equals(ExtensionFileFilter.getExtension(output))){
					r += "(!)Use BED extension for output filename. Try: " + ExtensionFileFilter.stripExtension(output) + ".bed\n";
				}
			} catch( NullPointerException e){ r += "(!)Output filename must have extension: use BED extension for output filename. Try: " + output + ".bed\n"; }
			//check directory
			if(output.getParent()==null){
	// 			System.err.println("default to current directory");
			} else if(!new File(output.getParent()).exists()){
				r += "(!)Check output directory exists: " + output.getParent() + "\n";
			}
		}
		
		// Define default behavior
		if(expandType.center==-999 && expandType.border==-999){
			SIZE = 250;
			byCenter = true;
		}else if(expandType.border==-999){
			SIZE = expandType.center;
			byCenter = false;
		}else if(expandType.center==-999){
			SIZE = expandType.border;
		}else{
			r += "(!) Both center and border are flagged. This should have been caught by Picocli.";
		}
		//check size of expansion is valid
		if(SIZE<=0){
			r += "(!) Invalid size input. Must be a positive integer greater than 0.";
		}
		
		return(r);
	}
}