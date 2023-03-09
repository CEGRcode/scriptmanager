package cli.Coordinate_Manipulation.BED_Manipulation;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;

import objects.ToolDescriptions;
import util.ExtensionFileFilter;
import scripts.Coordinate_Manipulation.BED_Manipulation.BEDtoGFF;

/**
 * Command line interface class for converting BED-formatted coordinates to
 * GFF-format by calling a script implemented in the scripts package.
 * 
 * @author Olivia Lang
 * @see scripts.Coordinate_Manipulation.BED_Manipulation.BEDtoGFF
 */
@Command(name = "bed-to-gff", mixinStandardHelpOptions = true,
	description = ToolDescriptions.bed_to_gff_description,
	version = "ScriptManager "+ ToolDescriptions.VERSION,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class BEDtoGFFCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "the BED file to convert")
	private File bedFile;
	
	@Option(names = {"-o", "--output"}, description = "specify output filename (name will be same as original with .gff ext)")
	private File output = null;
	@Option(names = {"-s", "--stdout"}, description = "output gff to STDOUT")
	private boolean stdout = false;
	@Option(names = {"-z", "--gzip"}, description = "gzip output (default=false)")
	private boolean gzOutput = false;
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">BEDtoGFFCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}
		
		BEDtoGFF.convertBEDtoGFF(output, bedFile, gzOutput);
		
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
		//set default output filename
		if(output==null && !stdout){
			String NAME = ExtensionFileFilter.stripExtension(bedFile) + ".gff";
			NAME += gzOutput ? ".gz" : "";
			output = new File(NAME);
		//check stdout and output not both selected
		}else if(stdout){
			if(output!=null){ r += "(!)Cannot use -s flag with -o.\n"; }
		//check output filename is valid
		}else{
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
