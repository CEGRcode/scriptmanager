package cli.BAM_Format_Converter;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;

import objects.ToolDescriptions;
import util.ExtensionFileFilter;
import scripts.BAM_Format_Converter.BAMtoBED;

/**
	BAM_Format_ConverterCLI/SEStatsCLI
*/
@Command(name = "bam-to-bed", mixinStandardHelpOptions = true,
	description = ToolDescriptions.bam_to_bed_description,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class BAMtoBEDCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BAM file from which we generate a new file.")
	private File bamFile;
	
	@Option(names = {"-o", "--output"}, description = "specify output directory (name will be same as original with .bed ext)" )
	private File output = null;
	@Option(names = {"-s", "--stdout"}, description = "stream output file to STDOUT (cannot be used with \"-o\" flag)" )
	private boolean stdout = false;
	
	//Read
	@ArgGroup(exclusive = true, multiplicity = "0..1", heading = "%nSelect Read to output:%n\t@|fg(red) (select no more than one of these options)|@%n")
	ReadType readType = new ReadType();
	static class ReadType {
		@Option(names = {"-1", "--read1"}, description = "output read 1 (default)")
		boolean read1 = false;
		@Option(names = {"-2", "--read2"}, description = "output read 2")
		boolean read2 = false;
		@Option(names = {"-a", "--all-reads"}, description = "output combined")
		boolean combined = false;
		@Option(names = {"-m", "--midpoint"}, description = "output midpoint (require PE)")
		boolean midpoint = false;
		@Option(names = {"-f", "--fragment"}, description = "output fragment (requires PE)")
		private boolean fragment = false;
	}
	
	@Option(names = {"-p", "--mate-pair"}, description = "require proper mate pair (default not required)")
	private boolean matePair = false;
	@Option(names = {"-n", "--min-insert"}, description = "filter by min insert size in bp")
	private int MIN_INSERT = -9999;
	@Option(names = {"-x", "--max-insert"}, description = "filter by max insert size in bp")
	private int MAX_INSERT = -9999;	
	
	private int STRAND = -9999;
	private int PAIR;
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">BAMtoBEDCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}
		
		BAMtoBED script_obj = new BAMtoBED(bamFile, output, STRAND, PAIR, MIN_INSERT, MAX_INSERT, null);
		script_obj.run();
		
		System.err.println("Conversion Complete");
		return(0);
	}
	
	private String validateInput() throws IOException {
		String r = "";
		
		// set strand method
		if(readType.read1)			{ STRAND=0; }
		else if(readType.read2)		{ STRAND=1; }
		else if(readType.combined)	{ STRAND=2; }
		else if(readType.midpoint)	{ STRAND=3; }
		else if(readType.fragment)	{ STRAND=4; }
		else						{ STRAND=0; }
		// set PE defaults
		if(STRAND==3 || STRAND==4) { matePair=true; }
		
		//check inputs exist
		if(!bamFile.exists()){
			r += "(!)BAM file does not exist: " + bamFile.getName() + "\n";
			return(r);
		}
		//check input extensions
		if(!"bam".equals(ExtensionFileFilter.getExtension(bamFile))){
			r += "(!)Is this a BAM file? Check extension: " + bamFile.getName() + "\n";
		}
		//check BAI exists
		File f = new File(bamFile+".bai");
		if(!f.exists() || f.isDirectory()){
			r += "(!)BAI Index File does not exist for: " + bamFile.getName() + "\n";
		}
		//set default output filename
		if(output==null &&  !stdout){
			if(STRAND==0){ output = new File( bamFile.getName().split("\\.")[0] + "_READ1.bed" ); }
			else if(STRAND==1){ output = new File( bamFile.getName().split("\\.")[0] + "_READ2.bed" ); }
			else if(STRAND==2){ output = new File( bamFile.getName().split("\\.")[0] + "_COMBINED.bed" ); }
			else if(STRAND==3){ output = new File( bamFile.getName().split("\\.")[0] + "_MIDPOINT.bed" ); }
			else if(STRAND==4){ output = new File( bamFile.getName().split("\\.")[0] + "_FRAGMENT.bed" ); }
			else { r += "(!)Somehow invalid STRAND!This error should never print. Check code if it does.\n"; }
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
			} catch( NullPointerException e){ r += "(!)Output filename must have extension: use BED extension for output filename. Try: " + output + ".bed\n"; }
			//check directory
			if(output.getParent()==null){
	// 			System.err.println("default to current directory");
			} else if(!new File(output.getParent()).exists()){
				r += "(!)Check output directory exists: " + output.getParent() + "\n";
			}
		}
		
		// validate insert sizes
		if( MIN_INSERT<0 && MIN_INSERT!=-9999 ){ r += "MIN_INSERT must be a positive integer value: " + MIN_INSERT + "\n"; }
		if( MAX_INSERT<0 && MAX_INSERT!=-9999 ){ r += "MAX_INSERT must be a positive integer value: " + MAX_INSERT + "\n"; }
		if( MAX_INSERT<MIN_INSERT && MIN_INSERT!=-9999 && MAX_INSERT!=-9999){ r += "MAX_INSERT must be larger/equal to MIN_INSERT: " + MIN_INSERT + "," + MAX_INSERT + "\n"; }
		// turn pair status boolean into int
		PAIR = matePair ? 1 : 0;
		
		return(r);
	}
}