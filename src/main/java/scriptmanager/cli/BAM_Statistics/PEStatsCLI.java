package scriptmanager.cli.BAM_Statistics;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import scriptmanager.objects.ToolDescriptions;
import scriptmanager.util.ExtensionFileFilter;
import scriptmanager.scripts.BAM_Statistics.PEStats;

/**
 * Command line interface for
 * {@link scriptmanager.scripts.BAM_Statistics.PEStats}
 * 
 * @author Olivia Lang
 */
@Command(name = "pe-stat", mixinStandardHelpOptions = true,
	description = ToolDescriptions.pe_stat_description,
	version = "ScriptManager "+ ToolDescriptions.VERSION,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class PEStatsCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BAM file whose statistics we want.")
	private File bamFile;

	@Option(names = {"-o", "--output"}, description = "specify output basename, default is the BAM input filename without extension")
	private File outputBasename = null;
	
	@Option(names = {"-n", "--min"}, description = "histogram range minimum (0 default)")
	private int MIN_INSERT = 0;
	@Option(names = {"-x", "--max"}, description = "histogram range maximum (1000 default)")
	private int MAX_INSERT = 1000;
	@Option(names = {"-s", "--summary"}, description = "write summary of insert histogram by chromosome (default false)")
	private boolean sum = false;
	@Option(names = {"-d", "--duplication-stats"}, description = "calculate duplication statistics if this flag is used (default false)")
	private boolean dup = false;
	
	/**
	 * Runs when this subcommand is called, running script in respective script package with user defined arguments
	 * @throws IOException Invalid file or parameters
	 */
	@Override
	public Integer call() throws Exception {
		System.err.println( ">PEStatsCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}
		
		PEStats.getPEStats( outputBasename, bamFile, dup, MIN_INSERT, MAX_INSERT, null, null, sum);
		
		System.err.println("Calculations Complete");
		return(0);
	}
	
	private String validateInput() throws IOException {
		String r = "";
		
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
		if(outputBasename==null){
// 			output = new File("output_bam_stats.txt");		//this default name mimics the gui
			outputBasename = new File(ExtensionFileFilter.stripExtension(bamFile));
		//check output filename is valid
		}else{
			//no check ext
			//check directory
			if(outputBasename.getParent()==null){
// 				System.err.println("default to current directory");
			} else if(!new File(outputBasename.getParent()).exists()){
				r += "(!)Check output directory exists: " + outputBasename.getParent() + "\n";
			}
		}
		
		//validate insert sizes
		if(MIN_INSERT<0){ r += "(!)MIN_INSERT must be non-negative\n"; }
		if(MAX_INSERT<0){ r += "(!)MAX_INSERT must be non-negative\n"; }
		if(MAX_INSERT<MIN_INSERT){ r += "(!)MAX_INSERT must be greater than or equal to MIN_INSERT\n"; }
		
		return(r);
	}
}