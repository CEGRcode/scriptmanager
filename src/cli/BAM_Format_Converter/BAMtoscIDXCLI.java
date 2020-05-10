package cli.BAM_Format_Converter;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;
import java.lang.InterruptedException;

import scripts.BAM_Format_Converter.BAMtoscIDX;

/**
	BAM_Format_ConverterCLI/BAMtosciIDXCLI
*/
@Command(name = "bam-to-scidx", mixinStandardHelpOptions = true,
		description = "Convert BAM file to scIDX file")
public class BAMtoscIDXCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BAM file from which we generate a new file.")
	private File bamFile;
	
	@Option(names = {"-o", "--output"}, description = "specify output directory (name will be same as original with .gff ext)" )
	private File output = null;
	@Option(names = {"-1", "--read1"}, description = "output read 1 (default)")
	private boolean read1 = false;
	@Option(names = {"-2", "--read2"}, description = "output read 2")
	private boolean read2 = false;
	@Option(names = {"-c", "--combined"}, description = "output combined")
	private boolean combined = false;
	@Option(names = {"-m", "--midpoint"}, description = "output midpoint (requires PE)")
	private boolean midpoint = false;
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
		System.err.println( ">BAMtoscIDXCLI.call()" );
		String validate = validateInput();
		if( validate.compareTo("")!=0 ){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			return(1);
		}
		
		BAMtoscIDX script_obj = new BAMtoscIDX(bamFile, output, STRAND, PAIR, MIN_INSERT, MAX_INSERT, null);
		script_obj.run();
		
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
		if( methodCounter>1){ r += "(!)Cannot use more than one of the four flag options [-1|-2|-c|-m]\n"; }
		// set strand method
		if( read1 )			{ STRAND=0; }
		else if( read2 )	{ STRAND=1; }
		else if( combined )	{ STRAND=2; }
		else if( midpoint )	{ STRAND=3; }
		else				{ STRAND=0; }
		// validate insert sizes
		if( MIN_INSERT<0 && MIN_INSERT!=-9999 ){ r += "MIN_INSERT must be a positive integer value: " + Integer.toString(MIN_INSERT) + "\n"; }
		if( MAX_INSERT<0 && MAX_INSERT!=-9999 ){ r += "MAX_INSERT must be a positive integer value: " + Integer.toString(MAX_INSERT) + "\n"; }
		// turn pair status boolean into int
		PAIR = matePair ? 1 : 0;
		return(r);
	}
	
}
	

