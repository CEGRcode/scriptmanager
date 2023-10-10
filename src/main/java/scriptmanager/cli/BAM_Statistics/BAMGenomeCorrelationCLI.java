package scriptmanager.cli.BAM_Statistics;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;

import scriptmanager.charts.HeatMap;
import scriptmanager.objects.ToolDescriptions;
import scriptmanager.util.ExtensionFileFilter;
import scriptmanager.scripts.BAM_Statistics.BAMGenomeCorrelation;

/**
 * Command line interface for
 * {@link scriptmanager.scripts.BAM_Statistics.BAMGenomeCorrelation}
 * 
 * @author Olivia Lang
 */
@Command(name = "bam-correlation", mixinStandardHelpOptions = true,
	description = ToolDescriptions.bam_correlation_description,
	version = "ScriptManager "+ ToolDescriptions.VERSION,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class BAMGenomeCorrelationCLI implements Callable<Integer> {

	/**
	 * Creates a new BAMGenomeCorrelationCLI object
	 */
	public BAMGenomeCorrelationCLI(){}
	
	@Parameters( index = "0..", description = "The BAM file whose statistics we want.")
	private File[] inputFiles;

	@Option(names = {"-f", "--files"}, description = "Input file list of BAM filepaths to correlate (formatted so each path is on its own line)")
	private boolean fileList = false;
	@Option(names = {"-o", "--output"}, description = "Specify output file, default is \"correlation_matrix\" or the input filename if -f flag used")
	private File outputBasename = null;
	
	//Read
	@ArgGroup(exclusive = true, multiplicity = "0..1", heading = "%nSelect Read to output:%n\t@|fg(red) (select no more than one of these options)|@%n")
	ReadType readType = new ReadType();
	static class ReadType {
		@Option(names = {"-1", "--read1"}, description = "output read 1 (default)")
		boolean read1 = false;
		@Option(names = {"-2", "--read2"}, description = "output read 2")
		boolean read2 = false;
		@Option(names = {"-a", "--all-reads"}, description = "output combined")
		boolean allreads = false;
		@Option(names = {"-m", "--midpoint"}, description = "output midpoint (require PE)")
		boolean midpoint = false;
	}
	
	@Option(names = {"-t", "--tag-shift"}, description = "tag shift in bp (default 0)")
	private int tagshift = 0;
	@Option(names = {"-b", "--bin-size"}, description = "bin size in bp (default 10)")
	private int binSize = 10;
	@Option(names = {"--cpu"}, description = "CPUs to use (default 1)")
	private int cpu = 1;
	//ColorScale
	@ArgGroup(exclusive = true, multiplicity = "0..1", heading = "%nSelect Read to output:%n\t@|fg(red) (select no more than one of these options)|@%n")
	ColorType colorScheme = new ColorType();
	static class ColorType {
		@Option(names = {"--classic"}, description = "Use classic blue to white to red color scale (default)")
		boolean redwhiteblue = false;
		@Option(names = {"--jet-like"}, description = "Use rainbow \"jet-like\" color scale")
		boolean jetlike = false;
	}
	
	private int READ = 0;
	private short colorScale = HeatMap.BLUEWHITERED;
	private Vector<File> bamFiles = new Vector<File>();
	
	/**
	 * Runs when this subcommand is called, running script in respective script package with user defined arguments
	 * @throws IOException Invalid file or parameters
	 */
	@Override
	public Integer call() throws Exception {
		System.err.println( ">BAMGenomeCorrelationCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}
		
		BAMGenomeCorrelation script_obj = new BAMGenomeCorrelation( bamFiles, outputBasename, tagshift, binSize, cpu, READ, colorScale);
		script_obj.getBAMGenomeCorrelation(false);
		
		System.err.println("Calculations Complete");
		return(0);
	}
	
	private String validateInput() throws IOException {
		String r = "";
		
		if(inputFiles==null){
			r += "(!)Please indicate at least one file.\n";
			return(r);
		//Import files as Vector list (scan input file if -f flag used)
		}else if(fileList){		//load files from input filelist
			if(inputFiles.length>1){
				r += "(!)Please indicate only one file with bam filepaths when using the -f flag.\n";
				return(r);
			}else if(!inputFiles[0].exists()){
				r += "(!)File of list of file inputs does not exist: " + inputFiles[0].getCanonicalPath() + "\n";
				return(r);
			}else{
				Scanner scan = new Scanner(inputFiles[0]);
				while (scan.hasNextLine()) {
					bamFiles.add(new File(scan.nextLine().trim()));
				}
				scan.close();
			}
			if(outputBasename==null){
				outputBasename = new File(ExtensionFileFilter.stripExtension(inputFiles[0])+"_BAMCorr");
			}
		}else{		//load input files into bam vector
			for(int x=0; x<inputFiles.length; x++){
				bamFiles.add(inputFiles[x]);
			}
		}
		//check each file in Vector
		for(int x=0; x<bamFiles.size(); x++){
			File BAM = bamFiles.get(x);
			File BAI = new File(BAM+".bai");
			//check input exists
			if(!BAM.exists()|| BAM.isDirectory()){
				r += "(!)BAM[" + x + "] file does not exist: " + BAM.getName() + "\n";
			//check input extensions
			}else if(!"bam".equals(ExtensionFileFilter.getExtension(BAM))){
				r += "(!)Is this a BAM file? Check extension: " + BAM.getName() + "\n";
			//check BAI exists
			}else if(!BAI.exists() || BAI.isDirectory()){
				r += "(!)BAI Index File does not exist for: " + BAM.getName() + "\n";
			}
		}
		if(!r.equals("")){ return(r); }
		//set default output filename
		if(outputBasename==null){
			outputBasename = new File("correlation_matrix");
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
		
		//Assign type value after validating 
		if(readType.read1)			{ READ = 0; }
		else if(readType.read2)		{ READ = 1; }
		else if(readType.allreads)	{ READ = 2; }
		else if(readType.midpoint)	{ READ = 3; }
		
		//validate binSize, and CPU count
		if(binSize<1){ r += "(!)Please indicate a binSize of at least 1: " + binSize + "\n"; }
		if(cpu<1){ r += "(!)Cannot use less than 1 CPU: " + cpu + "\n"; }
		
		//Assign color scheme
		if (colorScheme.redwhiteblue) { colorScale = HeatMap.BLUEWHITERED; }
		else if (colorScheme.jetlike) { colorScale = HeatMap.JETLIKE; }
		
		return(r);
	}
}