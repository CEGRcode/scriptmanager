package scriptmanager.cli.Peak_Analysis;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

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
	
	@Parameters( index = "0", description = "The coordinate peak file")
	private File peak = null;
	@Parameters( index = "1", description = "The coordinate reference file")
	private File ref = null;
	
	@Option(names = {"-o", "--output"}, description = "Specify output file (default = <peak>_<ref>_Output.bed/gff)")
	private File output = null;
	@Option(names = {"-u"}, description = "Maximum distance to upstream of peak (negative integer, default = no maximum)")
	private String upstreamBound = "n/a";
	@Option(names = {"-d"}, description = "Maximum distance to downstream of peak (positive integer, default = no maximum)")
	private String downstreamBound = "n/a";
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

		SortByRef script_obj = new SortByRef(ref, peak, output, properStrands, gzOutput, null, upstreamBound, downstreamBound);
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
		boolean validUpstream = upstreamBound.equals("n/a");
		if (!upstreamBound.equals("n/a")){
			validUpstream = Integer.parseInt(upstreamBound) <= 0;
		}
		if (!validUpstream){
			r += "Upstream bound must be a negative integer or or \"n/a\"";
		}
		boolean validDownstream = downstreamBound.equals("n/a");
		if (!downstreamBound.equals("n/a")){
			validDownstream = Integer.parseInt(downstreamBound) >= 0;
		}		
		if (!validDownstream){
			r += "Downstream bound must be a positive integer or \"n/a\"";
		}
		
		return(r);
	}

	public static String getCLIcommand(File ref, File peak, File out, boolean gff, boolean gzOutput, boolean properStrand, String upstream, String downstream){
		String command = "java -jar $SCRIPTMANAGER peak-analysis sort-by-ref";
		command += gff? " --gff": "";
		command += gzOutput? " -z": "";
		command += properStrand? " -p":"";
		command += " -u " + ((upstream.equals(""))? "n/a": upstream);
		command += " -d " + ((downstream.equals(""))? "n/a": downstream);
		command += " -o " + out.getAbsolutePath();
		command += " " + peak.getAbsolutePath();
		command += " " + ref.getAbsolutePath();
		return command;
	}
}
