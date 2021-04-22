package cli.Read_Analysis;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import util.BAMUtilities;
import util.ExtensionFileFilter;
import objects.PileupParameters;
import objects.ToolDescriptions;
import scripts.Read_Analysis.TagPileup;

/**
	Read_AnalysisCLI/TagPileupCLI
*/
@Command(name = "tag-pileup", mixinStandardHelpOptions = true,
	description = ToolDescriptions.tag_pileup_description,
	version = "ScriptManager "+ ToolDescriptions.VERSION,
	descriptionHeading = "%nDescription:%n%n",
	parameterListHeading = "%nParameters:%n",
	optionListHeading = "%nGeneral Options:%n",
	sortOptions = false,
	exitCodeOnInvalidInput = 1,
	exitCodeOnExecutionException = 1)
public class TagPileupCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BED file with reference coordinates to pileup on.")
	private File bedFile;
	@Parameters( index = "1", description = "The BAM file from which we remove duplicates. Make sure its indexed!")
	private File bamFile;
	
	@Option(names = {"-d", "--dry-run"}, description = "print all parameters without running anything")
	private boolean dryRun=false;
	
	//Output
	@ArgGroup(validate = false, heading = "%nOutput Options:%n")
	OutputOptions outputOptions = new OutputOptions();
	static class OutputOptions{
		@Option(names = {"-o", "--output-composite"}, description = "specify output file for composite values")
		private String outputComposite = "composite_average.out";
		@Option(names = {"-M", "--output-matrix"}, description = "specify output basename for matrix files (files each for sense and anti will be output)",
			arity="0..1")
		private ArrayList<String> outputMatrix = new ArrayList<String>(Arrays.asList("no","matrix","output"));
// 		@Option(names = {"-j", "--output-jtv"}, description = "output JTV file (default=false)")
// 		private boolean jtv = false;
		@Option(names = {"-z", "--gzip"}, description = "output compressed output (default=false)")
		private boolean zip = false;
		@Option(names = {"--cdt"}, description = "output matrix in cdt format (default)")
		private boolean cdt = false;
		@Option(names = {"--tab"}, description = "output matrix in tab format")
		private boolean tab = false;
	}
	
	//Read
	@ArgGroup(exclusive = true, multiplicity = "0..1", heading = "%nSelect Read to output:%n\t@|fg(red) (select no more than one of these options)|@%n")
	ReadType readType = new ReadType();
	static class ReadType {
		@Option(names = {"-1", "--read1"}, description = "pileup of read 1 (default)")
		boolean read1 = false;
		@Option(names = {"-2", "--read2"}, description = "pileup of read 2")
		boolean read2 = false;
		@Option(names = {"-a", "--all-reads"}, description = "pileup all reads")
		boolean allreads = false;
		@Option(names = {"-m", "--midpoint"}, description = "pile midpoint (require PE and combined, -p --combined)")
		boolean midpoint = false;
		int finalRead = 0;
	}
	
	//Strand
	@Option(names = {"--combined"}, description = "select output strands as combined (default separates into *_sense and *_anti)")
	boolean combStatus = false;
	
	//Smooth
	@ArgGroup(exclusive = true, multiplicity = "0..1", heading = "%nComposite Transformation/Smoothing Options:%n\t@|fg(red) (select no more than one of these options)|@%n")
	SmoothMethod smoothType = new SmoothMethod();
	static class SmoothMethod {
		@Option(names = {"-N","--no-smooth"}, description = "no smoothing applied to composite (default)")
		boolean noSmooth = false;
		@Option(names = {"-w", "--window-smooth"}, description = "sliding window smoothing applied to composite using default 3 bins for window size")
		boolean winStatus = false;
		@Option(names = {"-W", "--window-values"}, description = "sliding window smoothing applied to composite with user specified window size (in #bins)")
		int winVals = -9999;
		@Option(names = {"-g", "--gauss-smooth"}, description = "gauss smoothing applied to composite using default values: 5 bins and 3 standard deviations")
		boolean gaussStatus = false;
		@Option(names = {"-G", "--gauss-values"}, arity = "2", description = "gauss smoothing applied to composite with user specified standard deviation(SD) size (in #bins) followed by the number of SD")
		int[] gaussVals = new int[]{-9999,-9999};
	}
	
	@ArgGroup(validate = false, heading = "%nCalculation Options:%n")
	CalcOptions calcOptions = new CalcOptions();
	static class CalcOptions{	
		@Option(names = {"-s", "--shift"}, description = "set a shift in bp (default=0bp)")
		private int shift = 0;
		@Option(names = {"-b", "--bin-size"}, description = "set a bin size for the output (default=1bp)")
		private int binSize = 1;
		@Option(names = {"-t", "--standard"}, description = "set tags to be equal (default=false)")
		private boolean tagsEqual;
		@Option(names = {"--cpu"}, description = "set number of CPUs to use (default=1)")
		private int cpu = 1;
	}
	
	@ArgGroup(validate = false, heading = "%nFilter Options:%n")
	FilterOptions filterOptions = new FilterOptions();
	static class FilterOptions{
		@Option(names = {"-f", "--blacklist-filter"}, description = "specify a blacklist file to filter BED by, must use with -t flag")
		private File blacklistFilter = null;
		@Option(names = {"-p", "--require-pe"}, description = "require proper paired ends (default=false)\nautomatically turned on with any of flags -mnx")
		private boolean requirePE = false;
		@Option(names = {"-n", "--min-insert"}, description = "filter by minimum insert size in bp, require PE (default=no minimum)")
		private int MIN_INSERT = -9999;
		@Option(names = {"-x", "--max-insert"}, description = "filter by maximum insert size in bp, require PE (default=no maximum)")
		private int MAX_INSERT = -9999;
	}
	
	PileupParameters p;
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">TagPileupCLI.call()" );
		p = new PileupParameters();
		String validate = validateInput();
		if(!validate.equals("")){
			System.err.println(validate);
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			System.exit(1);
		}
		
		if(dryRun){
			p.printAll();
			System.err.println(outputOptions.outputMatrix.get(0));
			System.exit(1);
		}
		
		TagPileup script_obj = new TagPileup(bedFile, bamFile, p, null, outputOptions.outputMatrix.get(0));
		script_obj.run();
		
		System.err.println( "Calculations complete" );
		return(0);
	}
	
	private String validateInput() throws IOException {
		String r = "";
		
		//check ReadType, interpret booleans for int value
		if(readType.read1){ readType.finalRead = 0; }
		else if(readType.read2){ readType.finalRead = 1; }
		else if(readType.allreads){ readType.finalRead = 2; }
		else if(readType.midpoint){
			readType.finalRead = 3;
			filterOptions.requirePE = true;
			combStatus = true;
		}
		
		//check input extensions
		if(!"bed".equals(ExtensionFileFilter.getExtension(bedFile))){
			r += "(!)Is this a BED file? Check extension: " + bedFile.getName() +  "\n";
		}
		if(!"bam".equals(ExtensionFileFilter.getExtension(bamFile))){
			r += "(!)Is this a BAM file? Check extension: " + bamFile.getName() +  "\n";
		}
		if(!r.equals("")){ return(r); }
		//check inputs exist
		if(!bedFile.exists()){
			r += "(!)BED file does not exist: " + bedFile.getCanonicalPath() +  "\n";
		}
		if(!bamFile.exists()){
			r += "(!)BAM file does not exist: " + bamFile.getCanonicalPath() +  "\n";
		}
		//check BAI exists
		File f = new File(bamFile+".bai");
		if(!f.exists() || f.isDirectory()){
			r += "(!)BAI Index File does not exist for: " + bamFile.getName() +  "\n";
		}
		
		//set default output COMPOSITE filename (done by picocli)
		//check output COMPOSITE filename is valid
		if(outputOptions.outputComposite!="composite_average.out"){
			File output = new File(outputOptions.outputComposite);
			//no check ext
			//check directory
			if(output.getParent()==null){
// 				System.err.println("default to current directory");
			} else if(!new File(output.getParent()).exists()){
				r += "(!)Check output directory exists: " + output.getParent() + "\n";
			}
		}
		
		//set default output MATRIX (if output MATRIX not to be output)
		if(outputOptions.outputMatrix.size()>1){
			outputOptions.outputMatrix.set(0,null);
		//set default output MATRIX basename (allow scripts/*/TagPileup to generate ret of filename)
		} else if(outputOptions.outputMatrix.size()==0){  //generate default basename
			String readString = "read1";
			if(readType.finalRead == 1) { readString = "read2"; }
			else if(readType.finalRead == 2) { readString = "allreads"; }
			else if(readType.finalRead == 3) { readString = "midpoint"; }
			outputOptions.outputMatrix.add( 
				ExtensionFileFilter.stripExtension(new File(bedFile.getName())) + "_" +
				ExtensionFileFilter.stripExtension(new File(bamFile.getName())) + "_" + readString);
		//check output filename is valid
		}else{										//check basename
			File output = new File(outputOptions.outputMatrix.get(0));
			//no extension check b/c basename should have no extension
			//check directory
			if(output.getParent()==null){
// 				System.err.println("default to current directory");
			} else if(!new File(output.getParent()).exists()){
				r += "(!)Check output.MATRIX directory exists: " + output.getParent() + "\n";
			}
		}
		
		//validate smooth params
		if(smoothType.winVals!=-9999 && smoothType.winVals<1){ r += "(!)Invalid Smoothing Window Size. Must be larger than 0 bins, winSize=" + smoothType.winVals + "\n"; }
		if(smoothType.gaussVals[0]!=-9999 && smoothType.gaussVals[0]<1){ r += "(!)Invalid Standard Deviation Size. Must be larger than 0 bins, stdSize=" + smoothType.gaussVals[0] + "\n"; }
		if(smoothType.gaussVals[1]!=-9999 && smoothType.gaussVals[1]<1){ r += "(!)Invalid Number of Standard Deviations. Must be larger than 0 standard deviations, stdNum=" + smoothType.gaussVals[1] + "\n"; }
		
		//set require PE for appropriate flags
		if( filterOptions.MIN_INSERT!=-9999 || filterOptions.MAX_INSERT!=-9999){ filterOptions.requirePE = true; }
		if( readType.midpoint ){ filterOptions.requirePE = true; }
		
		//validate shift, binSize, and CPUs
		if(calcOptions.shift<0){  r += "(!)Invalid shift! Must be non-negative, shift=" + calcOptions.shift + "\n"; }
		if(calcOptions.binSize<1){  r += "(!)Invalid Bin Size! Must use at least 1bp, binSize=" + calcOptions.binSize + "\n"; }
		if(calcOptions.cpu<1){  r += "(!)Invalid Number of CPU's! Must use at least 1, CPU=" + calcOptions.cpu + "\n"; }
		
		//validate insert sizes
		if( filterOptions.MIN_INSERT<0 && filterOptions.MIN_INSERT!=-9999 ){ r += "(!)MIN_INSERT must be a positive integer value: " + p.getMinInsert() + "\n"; }
		if( filterOptions.MAX_INSERT<0 && filterOptions.MAX_INSERT!=-9999 ){ r += "(!)MAX_INSERT must be a positive integer value: " + p.getMaxInsert() + "\n"; }
		if( filterOptions.MIN_INSERT!=-9999 && filterOptions.MAX_INSERT!=-9999 && filterOptions.MAX_INSERT<filterOptions.MIN_INSERT ){
			r += "(!)MAX_INSERT must be larger/equal to MIN_INSERT: " + filterOptions.MIN_INSERT + "," + filterOptions.MAX_INSERT + "\n";
		}
		
		// LOAD UP PileupParameters OBJECT!
				
		//Set OUTPUT
		if(outputOptions.outputMatrix.size()<=1){
			p.setOutputType(2);														//default behavior
			//check output type
			if(outputOptions.cdt && outputOptions.tab) {							//both set? write error
				r += "(!)Cannot flag both --cdt and --tab. Please choose one.";
			} else if(outputOptions.tab){											//set tab
				p.setOutputType(1);
			}
		}else{
			outputOptions.outputMatrix.set(0,null);
			p.setOutputType(0);   //no matrix output
			if(outputOptions.cdt){
				p.setOutputType(2);
				outputOptions.outputMatrix.set(0,null);
			} else if(outputOptions.tab) {
				p.setOutputType(1);
				outputOptions.outputMatrix.set(0,null);
			}
		}
		p.setOutputCompositeStatus(true);
		
		//Set COMPOSITE file
		p.setCompositePrintStream(new PrintStream(outputOptions.outputComposite));
		
		//Set READ
		p.setRead(readType.finalRead);
		//Set STRAND
		p.setStrand(0);
		if(combStatus) { p.setStrand(1); }
		
		//Set smooth type and parameters
		if(smoothType.noSmooth){			//default behavior
			p.setTrans(0);
		}else if(smoothType.winStatus){   //window default
			p.setTrans(1);
			p.setSmooth(3);
		}else if(smoothType.winVals!=-9999){
			p.setTrans(1);
			p.setSmooth(smoothType.winVals);
		}else if(smoothType.gaussStatus){   //gauss default
			p.setTrans(2);
			p.setStdSize(5);
			p.setStdNum(3);
		}else if(smoothType.gaussVals[0]!=-9999 && smoothType.gaussVals[1]!=-9999){
			p.setTrans(2);
			p.setStdSize(smoothType.gaussVals[0]);
			p.setStdNum(smoothType.gaussVals[1]);
		}else{ p.setTrans(0); }      //default behavior
		
		//Set SHIFT, BIN, CPU
		p.setShift(calcOptions.shift);
		p.setBin(calcOptions.binSize);
		p.setCPU(calcOptions.cpu);
		
		//Set BLACKLIST & STANDARD
		p.setBlacklist(filterOptions.blacklistFilter);
		p.setStandard(calcOptions.tagsEqual);
		
		//Set PE requirement
		p.setPErequire(filterOptions.requirePE);
		
		//Set output statuses
		p.setJTVstatus(false);	//not available for CLI
		p.setGZIPstatus(outputOptions.zip);
		
		//Set Ratio (code to standardize tags sequenced to genome size (1 tag / 1 bp))
		if( p.getStandard() && filterOptions.blacklistFilter!=null ){
			p.setRatio(BAMUtilities.calculateStandardizationRatio(bamFile, filterOptions.blacklistFilter, p.getRead()));
		}else if( p.getStandard() ){
			p.setRatio(BAMUtilities.calculateStandardizationRatio(bamFile, p.getRead()));
		}
		
		//Set MIN_INSERT & MAX_INSERT
		p.setMinInsert(filterOptions.MIN_INSERT);
		p.setMaxInsert(filterOptions.MAX_INSERT);
		
		return(r);
	}
	
}