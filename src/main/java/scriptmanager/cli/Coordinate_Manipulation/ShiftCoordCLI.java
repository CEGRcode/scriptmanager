package scriptmanager.cli.Coordinate_Manipulation;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;

import scriptmanager.objects.ToolDescriptions;
import scriptmanager.util.ExtensionFileFilter;
import scriptmanager.scripts.Coordinate_Manipulation.ShiftCoord;

/**
 * Command line interface class for shifting coordinate intervals up/downstream by calling method implemented in the scripts package.
 * 
 * @author Olivia Lang
 * @see scriptmanager.scripts.Coordinate_Manipulation.ShiftCoord
 */
@Command(name = "shift-coord", mixinStandardHelpOptions = true,
	description = ToolDescriptions.shift_coordinate_description,
	version = "ScriptManager "+ ToolDescriptions.VERSION,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class ShiftCoordCLI implements Callable<Integer> {

	@Parameters( index = "0", description = "the coordinate file (BED/GFF format) to shift")
	private File input;

	@Option(names = {"-o", "--output"}, description = "specify output filepath (default input filename with _shiftXXXbp.bed/gff)")
	private String outputFilepath = null;
	@Option(names = {"-t", "--shift"}, description = "shift distance in bp, upstream  < 0 and downstream > 0 (default=0)")
	private int shift = 0;
	@Option(names = {"-u", "--unstranded"}, description = "don't force strandedness (default=forced)")
	private boolean stranded = true;
	@Option(names = {"-z", "--gzip"}, description = "gzip output (default=false)")
	private boolean gzOutput = false;
	@Option(names = {"--gff"}, description = "input is GFF format (default=BED format)")
	private boolean isGFF = false;

	@Override
	public Integer call() throws Exception {
		System.err.println( ">ShiftCoord.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}

		if(isGFF) {
			ShiftCoord.shiftGFFInterval(new File(outputFilepath), input, shift, stranded, gzOutput);
		} else {
			ShiftCoord.shiftBEDInterval(new File(outputFilepath), input, shift, stranded, gzOutput);
		}

		System.err.println("Shift Complete");
		return(0);
	}

	private String validateInput() throws IOException {
		String r = "";

		//check inputs exist
		if(!input.exists()){
			r += "(!)BED/GFF file does not exist: " + input.getName() + "\n";
		}
		if(!"".equals(r)){ return(r); }

		//set default output filename
		if(outputFilepath==null){
			String SUFFIX = shift < 0 ? "_shift" + shift + "bp." : "_shift+" + shift + "bp.";
			SUFFIX += isGFF ? "gff" : "bed";
			SUFFIX += gzOutput ? ".gz" : "";
			outputFilepath = ExtensionFileFilter.stripExtension(input) + SUFFIX;
		//check output filename is valid
		}else{
			//no extension check
			//check directory
			File BASEFILE = new File(outputFilepath);
			if(BASEFILE.getParent()==null){
// 				System.err.println("default to current directory");
			} else if(!new File(BASEFILE.getParent()).exists()){
				r += "(!)Check output directory exists: " + BASEFILE.getParent() + "\n";
			}
		}
		return(r);
	}
}
