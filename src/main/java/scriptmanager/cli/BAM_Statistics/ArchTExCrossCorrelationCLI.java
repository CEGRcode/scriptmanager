package scriptmanager.cli.BAM_Statistics;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import scriptmanager.objects.ToolDescriptions;
import scriptmanager.objects.ArchTEx.CorrParameter;
import scriptmanager.scripts.BAM_Statistics.ArchTExCrossCorrelation;
import scriptmanager.util.ExtensionFileFilter;

/**
 * Command line interface class for performing the ArchTEX cross correlation
 * analysis by calling a method implemented in the scripts package.
 * 
 * @author Olivia Lang
 * @see scriptmanager.objects.ArchTEx.CorrParameter
 * @see scriptmanager.scripts.BAM_Statistics.ArchTExCrossCorrelation
 */
@Command(name = "archtex-cross-corr", mixinStandardHelpOptions = true,
	description = ToolDescriptions.archtex_crosscorrelation_description,
	version = "ScriptManager "+ ToolDescriptions.VERSION,
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class ArchTExCrossCorrelationCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BAM file to perform the cross-correlation on")
	private File bamFile;

	@Option(names = {"-o", "--output"}, description = "specify output basename, default is the BAM input filename without extension")
	private File outputBasename = null;
	
	@ArgGroup(exclusive = true, multiplicity = "0..1", heading = "%nSelect correlation strategy:%n\t@|fg(red) (select no more than one of these options)|@%n")
	CorrType corrType = new CorrType();
	static class CorrType {
		@Option(names = {"-g", "--genome"}, description = "Use the full genome correlation method")
		private boolean corrGenome = false;
		@Option(names = {"-r", "--random"}, description = "Use the random sampling correlation method (default)")
		private boolean corrRandom = false;
	}

	@Option(names = {"-t", "--cpu"}, description = "set number of threads for performance tuning (default=1)")
	private int cpu = 1;

	@ArgGroup(exclusive = true, multiplicity = "0..1", heading = "%nRandom Sampling Options:%n\t@|fg(red) (ignored if full genome correlation method selected)|@%n")
	SamplingParams samplingParams = new SamplingParams();
	static class SamplingParams {
		@Option(names = {"-w", "--window"}, description = "set window frame size for each extraction (default=50kb)")
		private int windowSize = 50000;
		@Option(names = {"-i", "--iterations"}, description = "set number of random iterations per chromosome (default=10)")
		private int iterations = 10;
	}
	
	CorrParameter param = new CorrParameter();

	@Override
	public Integer call() throws Exception {
		System.err.println( ">ArchTExCrossCorrelationCLI.call()" );
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println( validate );
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}
		
		ArchTExCrossCorrelation.correlate( outputBasename, bamFile, param, null);
		
		System.err.println("Calculations Complete");
		return(0);
	}

	/**
	 * Validate the input values before executing the script.
	 * 
	 * @return a multi-line string describing input validation issues
	 * @throws IOException
	 */
	private String validateInput() throws IOException {
		String r = "";
		
		//check inputs exist
		if(!bamFile.exists()){
			r += "(!)BAM file does not exist: " + bamFile.getName() + "\n";
			return(r);
		}
		//check BAI exists
		File f = new File(bamFile+".bai");
		if(!f.exists() || f.isDirectory()){
			r += "(!)BAI Index File does not exist for: " + bamFile.getName() + "\n";
		}
		//set default output filename
		if(outputBasename==null){
// 			output = new File("output_bam_stats.txt");		//this default name mimics the gui
			outputBasename = new File(ExtensionFileFilter.stripExtension(bamFile) + "_CrossCorrelation.txt");
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
		
		//validate random sampling params
		if (corrType.corrGenome) {
			param.setCorrType(true);
		} else {
			param.setCorrType(false);
			if (samplingParams.windowSize<1) { r += "(!)Window size must be at least 1\n"; }
			if (samplingParams.iterations<1) { r += "(!)Num iterations must be at least 1\n"; }
			param.setWindow(samplingParams.windowSize);
		}
		// valiate CPU
		if (cpu<1) { r += "(!)CPU count must be at least 1\n"; }
		param.setThreads(cpu);

		return(r);
	}
}
