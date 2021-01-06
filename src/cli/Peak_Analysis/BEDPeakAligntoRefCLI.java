package cli.Peak_Analysis;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;

import objects.ToolDescriptions;
import util.ExtensionFileFilter;
import scripts.Peak_Analysis.BEDPeakAligntoRef;
	
/**
	Peak_AnalysisCLI/BEDPeakAligntoRefCLI
*/
@Command(name = "peak-align-ref", mixinStandardHelpOptions = true,
	description = ToolDescriptions.peak_align_ref_description,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class BEDPeakAligntoRefCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BED peak file")
	private File peakBED;
	@Parameters( index = "1", description = "The BED reference file")
	private File refBED = null;
	
	@Option(names = {"-o", "--output"}, description = "Specify output file (default = <peakBED>_<refBED>_Output.cdt)")
	private File output = null;
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">BEDPeakAligntoRefCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}
		
		BEDPeakAligntoRef script_obj = new BEDPeakAligntoRef(refBED, peakBED, output, null);
		script_obj.run();
		
		System.err.println( "Peak Align Complete." );	
		return(0);
	}
	
	private String validateInput() throws IOException {
		String r = "";
		
		//check inputs exist
		if(!peakBED.exists()){
			r += "(!)BED-peak file does not exist: " + peakBED.getName() + "\n";
		}
		if(!refBED.exists()){
			r += "(!)BED-ref file does not exist: " + refBED.getName() + "\n";
		}
		if(!r.equals("")){ return(r); }
		//check input extensions
		if(!"bed".equals(ExtensionFileFilter.getExtension(peakBED))){
			r += "(!)Is this a BED file? Check extension: " + peakBED.getName() + "\n";
		}
		if(!"bed".equals(ExtensionFileFilter.getExtension(refBED))){
			r += "(!)Is this a BED file? Check extension: " + refBED.getName() + "\n";
		}
		//set default output filename
		if(output==null){
			output = new File(ExtensionFileFilter.stripExtension(peakBED) + "_" + ExtensionFileFilter.stripExtension(refBED) + "_Output.cdt");
		//check output filename is valid
		}else{
			//check ext
			try{
				if(!"cdt".equals(ExtensionFileFilter.getExtension(output))){
					r += "(!)Use CDT extension for output filename. Try: " + ExtensionFileFilter.stripExtension(output) + ".cdt\n";
				}
			} catch( NullPointerException e){ r += "(!)Output filename must have extension: use CDT extension for output filename. Try: " + output + ".cdt\n"; }
			//check directory
			if(output.getParent()==null){
// 				System.err.println("default to current directory");
			} else if(!new File(output.getParent()).exists()){
				r += "(!)Check output directory exists: " + output.getParent() + "\n";
			}
		}
		
		return(r);
	}
}