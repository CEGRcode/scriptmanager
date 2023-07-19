package scriptmanager.cli.BAM_Format_Converter;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;

import scriptmanager.objects.ToolDescriptions;
import scriptmanager.util.ExtensionFileFilter;
import scriptmanager.scripts.BAM_Format_Converter.BAMtoGFF;

/**
	BAM_Format_ConverterCLI/SEStatsCLI
*/
@Command(name = "bam-to-gff", mixinStandardHelpOptions = true,
	description = ToolDescriptions.bam_to_gff_description,
	version = "ScriptManager "+ ToolDescriptions.VERSION,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class BAMtoGFFCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BAM file from which we generate a new file.")
	private File bamFile;
	
	@Option(names = {"-o", "--output"}, description = "specify output directory (name will be same as original with .gff ext)" )
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
		System.err.println( ">BAMtoGFFCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}
		
		BAMtoGFF script_obj = new BAMtoGFF(bamFile, output, STRAND, PAIR, MIN_INSERT, MAX_INSERT, null);
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
			if(STRAND==0){ output = new File( bamFile.getName().split("\\.")[0] + "_READ1.gff" ); }
			else if(STRAND==1){ output = new File( bamFile.getName().split("\\.")[0] + "_READ2.gff" ); }
			else if(STRAND==2){ output = new File( bamFile.getName().split("\\.")[0] + "_COMBINED.gff" ); }
			else if(STRAND==3){ output = new File( bamFile.getName().split("\\.")[0] + "_MIDPOINT.gff" ); }
			else if(STRAND==4){ output = new File( bamFile.getName().split("\\.")[0] + "_FRAGMENT.gff" ); }
			else { r += "(!)Somehow invalid STRAND!This error should never print. Check code if it does.\n"; }
		//check stdout and output not both selected
		}else if(stdout){
			if(output!=null){ r += "(!)Cannot use -s flag with -o.\n"; }
		//check output filename is valid
		}else{
			//check ext
			try{
				if(!"gff".equals(ExtensionFileFilter.getExtension(output))){
					r += "(!)Use GFF extension for output filename. Try: " + ExtensionFileFilter.stripExtension(output) + ".gff\n";
				}
			} catch( NullPointerException e){ r += "(!)Output filename must have extension: use GFF extension for output filename. Try: " + output + ".gff\n"; }
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
	public static String getCLIcommand(File BAM, File output, int pair, int strand, int min, int max){
		String command = "java -jar $SCRIPTMANAGER coordinate-manipulation bam-to-gff";
		command += " " + BAM.getAbsolutePath();
		command += " -o " + output.getAbsolutePath();
		if (strand == 0) {
			command += " -1 ";
		} else if (strand == 1) {
			command += " -2 ";
		} else if (strand == 2) {
			command += " -a ";
		} else if (strand == 3 )  {
			command += " -m ";
		} else if (strand == 4)  {
			command += " -f ";
		}
		if (pair != 0) {
			command += " -p";
		}
		command += " -n " + min;
		command += " -x " + max;
		return command;
	}
}