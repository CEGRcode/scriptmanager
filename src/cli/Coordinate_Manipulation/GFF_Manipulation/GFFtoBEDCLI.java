package cli.Coordinate_Manipulation.GFF_Manipulation;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;

import objects.ToolDescriptions;
import util.ExtensionFileFilter;
import scripts.Coordinate_Manipulation.GFF_Manipulation.GFFtoBED;

/**
	Coordinate_ManipulationCLI/GFFtoBEDCLI
*/
@Command(name = "gff-to-bed", mixinStandardHelpOptions = true,
	description = ToolDescriptions.gff_to_bed_description,
	version = "ScriptManager "+ ToolDescriptions.VERSION,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class GFFtoBEDCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "the GFF file to convert")
	private File gffFile;
	
	@Option(names = {"-o", "--output"}, description = "specify output directory (name will be same as original with .bed ext)")
	private File output = null;
	@Option(names = {"-s", "--stdout"}, description = "output bed to STDOUT")
	private boolean stdout = false;
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">GFFtoBEDCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}
		
		GFFtoBED.convertGFFtoBED(output, gffFile);
		
		System.err.println("Conversion Complete");
		return(0);
	}
	
	private String validateInput() throws IOException {
		String r = "";
		
		//check inputs exist
		if(!gffFile.exists()){
			r += "(!)GFF file does not exist: " + gffFile.getName() + "\n";
			return(r);
		}
		//check input extensions
		if(!"gff".equals(ExtensionFileFilter.getExtension(gffFile))){
			r += "(!)Is this a GFF file? Check extension: " + gffFile.getName() + "\n";
		}
		//set default output filename
		if(output==null && !stdout){
			output = new File(ExtensionFileFilter.stripExtension(gffFile) + ".bed");
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
			} catch( NullPointerException e){ r += "(!)Output filename must have extension: use GFF extension for output filename. Try: " + output + ".gff\n"; }
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