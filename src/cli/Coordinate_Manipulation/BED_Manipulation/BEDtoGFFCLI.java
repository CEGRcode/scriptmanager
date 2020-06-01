package cli.Coordinate_Manipulation.BED_Manipulation;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;

import util.ExtensionFileFilter;
import scripts.Coordinate_Manipulation.BED_Manipulation.BEDtoGFF;

/**
	Coordinate_ManipulationCLI/BEDtoGFFCLI
*/
@Command(name = "bed-to-gff", mixinStandardHelpOptions = true,
	description = "Converts BED file format to GFF file format",
	sortOptions = false)
public class BEDtoGFFCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "the BED file to convert")
	private File bedFile;
	
	@Option(names = {"-o", "--output"}, description = "specify output directory (name will be same as original with .gff ext)")
	private File output = null;
	@Option(names = {"-s", "--stdout"}, description = "output gff to STDOUT")
	private boolean stdout = false;
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">BEDtoGFFCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			return(1);
		}
		
		BEDtoGFF.convertBEDtoGFF(output, bedFile);
		
		System.err.println("Conversion Complete");
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
		if(output==null && !stdout){
			output = new File(ExtensionFileFilter.stripExtension(bedFile) + ".gff");
		//check stdout and output not both selected
		}else if(stdout){
			if(output!=null){ r += "(!)Cannot use -s flag with -o.\n"; }
		//check output filename is valid
		}else{
			//check ext
			try{
				if(!"bed".equals(ExtensionFileFilter.getExtension(output))){
					r += "(!)Use BED extension for output filename. Try: " + ExtensionFileFilter.stripExtension(output) + ".bed\n";
				}
			} catch( NullPointerException e){ r += "(!)Output filename must have extension: use BED extension for output filename. Try: " + output + ".youroutputextensionhere\n"; }
			//check directory
			if(output.getParent()==null){
	// 			System.err.println("default to current directory");
			} else if(!new File(output.getParent()).exists()){
				r += "(!)Check output directory exists: " + output.getParent() + "\n";
			}
		}
		
		return(r);
	}
}