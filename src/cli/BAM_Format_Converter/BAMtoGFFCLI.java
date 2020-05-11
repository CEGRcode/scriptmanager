package cli.BAM_Format_Converter;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;

import scripts.BAM_Format_Converter.BAMtoGFF;

/**
	BAM_Format_ConverterCLI/SEStatsCLI
*/
@Command(name = "bam-to-gff", mixinStandardHelpOptions = true,
		description = "Convert BAM file to GFF file",
		sortOptions = false)
public class BAMtoGFFCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BAM file from which we generate a new file.")
	private File bamFile;
	
	@Option(names = {"-o", "--output"}, description = "specify output directory (name will be same as original with .gff ext)" )
	private File output = null;
	@Option(names = {"-s", "--stdout"}, description = "stream output file to STDOUT (cannot be used with \"-o\" flag)" )
	private boolean stdout = false;
	@Option(names = {"-1", "--read1"}, description = "output read 1 (default)")
	private boolean read1 = false;
	@Option(names = {"-2", "--read2"}, description = "output read 2")
	private boolean read2 = false;
	@Option(names = {"-c", "--combined"}, description = "output combined")
	private boolean combined = false;
	@Option(names = {"-m", "--midpoint"}, description = "output midpoint (requires PE)")
	private boolean midpoint = false;
	@Option(names = {"-f", "--fragment"}, description = "output fragment (requires PE)")
	private boolean fragment = false;
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
		System.out.println( ">BAMtoGFFCLI.call()" );
		String validate = validateInput();
		if( validate.compareTo("")!=0 ){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			return(1);
		}
		
		BAMtoGFF script_obj;
		if(stdout){
			script_obj = new BAMtoGFF(bamFile, null, STRAND, PAIR, MIN_INSERT, MAX_INSERT, null);
		}else{
			script_obj = new BAMtoGFF(bamFile, output, STRAND, PAIR, MIN_INSERT, MAX_INSERT, null);
		}
		script_obj.run();
		
		System.err.println("Conversion Complete");
		
		return(0);
	}
	
	private String validateInput(){
		String r = "";
		// check only one strand method indicated
		int methodCounter=0;
		methodCounter += read1 ? 1 : 0;
		methodCounter += read2 ? 1 : 0;
		methodCounter += combined ? 1 : 0;
		methodCounter += midpoint ? 1 : 0;
		methodCounter += fragment ? 1 : 0;
		if( methodCounter>1){ r += "(!)Cannot use more than one of the four flag options [-1|-2|-c|-m]\n"; }
		// set strand method
		if( read1 )			{ STRAND=0; }
		else if( read2 )	{ STRAND=1; }
		else if( combined )	{ STRAND=2; }
		else if( midpoint )	{ STRAND=3; }
		else if( fragment )	{ STRAND=4; }
		else				{ STRAND=0; }
		// validate insert sizes
		if( MIN_INSERT<0 && MIN_INSERT!=-9999 ){ r += "MIN_INSERT must be a positive integer value: " + MIN_INSERT + "\n"; }
		if( MAX_INSERT<0 && MAX_INSERT!=-9999 ){ r += "MAX_INSERT must be a positive integer value: " + MAX_INSERT + "\n"; }
		if( MAX_INSERT<MIN_INSERT ){ r += "MAX_INSERT must be larger/equal to MIN_INSERT: " + MIN_INSERT + "," + MAX_INSERT + "\n"; }
		// turn pair status boolean into int
		PAIR = matePair ? 1 : 0;
		// check -s and -o not used together (pipe standard out vs output file)
		if( stdout && output!=null ){ r += "(!)Cannot use -s flag with -o.\n";}
		// set default output filename if no stdout
		else if( output== null ){
			if(STRAND==0){ output = new File( bamFile.getName().split("\\.")[0] + "_READ1.gff" ); }
			else if(STRAND==1){ output = new File( bamFile.getName().split("\\.")[0] + "_READ2.gff" ); }
			else if(STRAND==2){ output = new File( bamFile.getName().split("\\.")[0] + "_COMBINED.gff" ); }
			else if(STRAND==3){ output = new File( bamFile.getName().split("\\.")[0] + "_MIDPOINT.gff" ); }
			else if(STRAND==4){ output = new File( bamFile.getName().split("\\.")[0] + "_FRAGMENT.gff" ); }
			else { r += "(!)Somehow invalid STRAND!This error should never print.Check code if it does.\n"; }
		}
		return(r);
	}
	
}