package scriptmanager.cli.Peak_Analysis;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import javax.swing.JOptionPane;

import java.io.File;
import java.io.IOException;

import scriptmanager.objects.ToolDescriptions;
import scriptmanager.util.ExtensionFileFilter;
import scriptmanager.scripts.Peak_Analysis.SortByRef;
	
/**
	Peak_AnalysisCLI/SortByRefCLI
*/
@Command(name = "sort-by-ref", mixinStandardHelpOptions = true,
	description = ToolDescriptions.peak_align_ref_description,
	version = "ScriptManager "+ ToolDescriptions.VERSION,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class SortByRefCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BED peak file")
	private File peak = null;
	@Parameters( index = "1", description = "The BED reference file")
	private File ref = null;
	
	@Option(names = {"-o", "--output"}, description = "Specify output file (default = <peak>_<ref>_Output.bed/gff)")
	private File output = null;
	@Option(names = {"-lb"}, description = "Maximum distance to left of peak (positive integer, default = no maximum)")
	private String leftBound = "";
	@Option(names = {"-rb"}, description = "Maximum distance to right of peak (positive integer, default = no maximum)")
	private String rightBound = "";
	@Option(names = {"-p", "--proper-strands"}, description = "Require proper strand direction" )
	private boolean properStrands = false;
	@Option(names = {"-z", "--compression"}, description = "Output compressed GFF file" )
	private boolean gzOutput = false;
	@Option(names = {"--gff"}, description = "input is GFF format (default=BED format)")
	private boolean isGFF = false;
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">SortByRefCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}

		SortByRef script_obj = new SortByRef(ref, peak, output, properStrands, gzOutput, null, leftBound, rightBound);
		if (isGFF){
			script_obj.sortGFF();
		} else { 
			script_obj.sortBED();
		}
		
		System.err.println( "Sorting Complete." );	
		return(0);
	}
	
	private String validateInput() throws IOException {
		String r = "";

		//check inputs exist
		if(!peak.exists()){
			r += "(!)Coordinate-peak file does not exist: " + peak.getName() + "\n";
		}
		if(!ref.exists()){
			r += "(!)Coordinate-ref file does not exist: " + ref.getName() + "\n";
		}
		if(!r.equals("")){ return(r); }
		//check input extensions
		if(!("bed".equals(ExtensionFileFilter.getExtensionIgnoreGZ(peak)) || "gff".equals(ExtensionFileFilter.getExtensionIgnoreGZ(peak)))){
			r += "(!)Is this a coordinate file? Check extension: " + peak.getName() + "\n";
		}
		if(!("bed".equals(ExtensionFileFilter.getExtensionIgnoreGZ(peak)) || "gff".equals(ExtensionFileFilter.getExtensionIgnoreGZ(peak)))){
			r += "(!)Is this a coordinate file? Check extension: " + ref.getName() + "\n";
		}
		if(!ExtensionFileFilter.getExtensionIgnoreGZ(peak).equals(ExtensionFileFilter.getExtensionIgnoreGZ(ref))){
			r += "(!)Format of the peak and reference don't match \n";
		}
		//set default output filename
		if(output==null){
			output = new File(ExtensionFileFilter.stripExtension(peak) + "_" + ExtensionFileFilter.stripExtension(ref) + "_Output." + ExtensionFileFilter.getExtensionIgnoreGZ(ref));
		//check output filename is valid
		}else{
			//check ext
			try{
				if(!ExtensionFileFilter.getExtensionIgnoreGZ(ref).equals(ExtensionFileFilter.getExtensionIgnoreGZ(output))){
					r += "(!)Output extensions does not match coordinate files";
				}
			} catch( NullPointerException e){ r += "(!)Output extensions does not match coordinate files"; }
			//check directory
			if(output.getParent()==null){
// 				System.err.println("default to current directory");
			} else if(!new File(output.getParent()).exists()){
				r += "(!)Check output directory exists: " + output.getParent() + "\n";
			}
		}

		//check bounds
		boolean validLeftBound = leftBound.equals("") || Integer.parseInt(leftBound) >= 0;
		boolean validRightBound = rightBound.equals("") || Integer.parseInt(rightBound) >= 0;
		if (!validLeftBound || !validRightBound){
			r += "Bounds must be positive integers";
		}
		
		return(r);
	}
}
