package cli.File_Utilities;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;

import objects.ToolDescriptions;
import util.ExtensionFileFilter;
import scripts.File_Utilities.ConvertChrNames;

/**
	File_Utilities/ConvertBEDGenomeCLI
*/
@Command(name = "convert-gff-genome", mixinStandardHelpOptions = true,
	description = ToolDescriptions.convertGFFChrNamesDescription,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class ConvertGFFChrNamesCLI implements Callable<Integer> {

	@Parameters( index = "0", description = "the GFF coordinate file to convert")
	private File coordFile;

	@Option(names = {"-o", "--output"}, description = "specify output directory (name will be same as original with .gff ext)")
	private File output = null;

	@Option(names = {"-a", "--to-arabic"}, description = "switch converter to output arabic numeral chromsome names (default outputs roman numeral chrnames)")
	private boolean toArabic = false;
	
	@Option(names = {"-m", "--chrmt"}, description = "converter will map \"chrM\" --> \"chrmt\" (default with no flag is \"chrmt\" --> \"chrM\")")
	private boolean useChrmt = false;

	@Override
	public Integer call() throws Exception {
		System.err.println( ">ConvertGFFChrNamesCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}

		// load conversion hashmap
		if(toArabic) ConvertChrNames.convert_RomantoArabic(coordFile,output,useChrmt);
		else ConvertChrNames.convert_ArabictoRoman(coordFile,output,useChrmt);

		System.err.println("Conversion Complete");
		return(0);
	}

	private String validateInput() throws IOException {
		String r = "";

		//check inputs exist
		if(!coordFile.exists()){
			r += "(!)Coordinate file does not exist: " + coordFile.getName() + "\n";
			return(r);
		//check input extensions
		} else if(!"gff".equals(ExtensionFileFilter.getExtension(coordFile))){
			r += "(!)Is this a GFF file? Check extension: " + coordFile.getName() + "\n";
		}
		//set default output filename
		if(output==null){
			if(toArabic) output = new File(ExtensionFileFilter.stripExtension(coordFile) + "_toArabic.gff");
			else output = new File(ExtensionFileFilter.stripExtension(coordFile) + "_toRoman.gff");
		//check output filename is valid
		}else{
			//check ext
			try{
				if(!"gff".equals(ExtensionFileFilter.getExtension(output))){
					r += "(!)Use BED extension for output filename. Try: " + ExtensionFileFilter.stripExtension(output) + ".gff\n";
				}
			} catch( NullPointerException e){ r += "(!)Output filename must have extension: use GFF extension for output filename. Try: " + output + ".youroutputextensionhere\n"; }
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
