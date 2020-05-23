package cli.Read_Analysis;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.awt.Color;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import java.io.File;
import java.io.IOException;

import util.BAMUtilities;
import util.ExtensionFileFilter;
import objects.PileupParameters;
import scripts.Read_Analysis.TagPileup;

/**
	Read_AnalysisCLI/TagPileupCLI
*/
@Command(name = "tag-pileup", mixinStandardHelpOptions = true,
		description = "Pileup 5' ends of aligned tags given BED and BAM files according to user-defined parameters",
        descriptionHeading = "%nDescription:%n%n",
        parameterListHeading = "%nParameters:%n",
        optionListHeading = "%nGeneral Options:%n",
		sortOptions = false)
public class TagPileupCLI implements Callable<Integer> {
	
	//names = {"-r","--bed-input"}
	//names = {"-i","--bam-input"}
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
		private String outputComposite = null;
		@Option(names = {"-M", "--output-matrix"}, description = "specify output basename for matrix files (files each for sense and anti will be output)",
			arity="0..1")
		private ArrayList<String> outputMatrix = new ArrayList<String>(Arrays.asList("no","matrix","output"));
// 		@Option(names = {"-j", "--output-jtv"}, description = "output JTV file (default=false)")
// 		private boolean jtv = false;
		@Option(names = {"-z", "--gzip"}, description = "output compressed output (default=false)")
		private boolean zip = false;
		@Option(names = {"--cdt"}, description = "output compressed output (default=true)")
		private boolean cdt = false;
		@Option(names = {"--tab"}, description = "output compressed output (default=false)")
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
		@Option(names = {"-m", "--midpoint"}, description = "pile midpoint (require PE)")
		boolean midpoint = false;
		int finalRead = 0;
	}
	//Strand
	@ArgGroup(exclusive = true, multiplicity = "0..1", heading = "%nSelect Separate or Combined strands:%n\t@|fg(red) (select no more than one of these options)|@%n")
	StrandType strandType = new StrandType();
	static class StrandType {
		@Option(names = {"--separate"}, description = "select output strands as separate (default)")
		boolean sepStatus = false;
		@Option(names = {"--combined"}, description = "select output strands as combined")
		boolean combStatus = false;
		int finalStrand = 0;
	}
	
	//Smooth
	@ArgGroup(exclusive = true, multiplicity = "0..1", heading = "%nComposite Transformation/smoothing parameters:%n\t@|fg(red) (select no more than one of these options)|@%n")
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
	
	
// 	a		all-reads
// 	b		bin
// 	c		combined
// 	d		dry-run
// 	e
// 	f		blacklist-filter
// 	g		gauss-smooth
// 	G		gauss-val
// 	h
// 	i
// 	j		output-jtv
// 	k
// 	l
// 	m		midpoint
// 	M		output-matrix
// 	n		min-insert
//	N		no-smooth
// 	o		output-composite
// 	p		require-pe
// 	q
// 	r		
// 	s		shift
// 	t		standard (set tags to be equal)
// 	u
// 	v
// 	w		window-smooth
// 	W		window-val
// 	x		max-insert
// 	y
// 	z		gzip
// 	1		read1
// 	2		read2
// 	--cpu
	
	
	PileupParameters p;
	
	
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">TagPileupCLI.call()" );
		
		p = new PileupParameters();
		
		String validate = validateInput();
		if(validate.compareTo("")!=0){
			System.err.println(validate);
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
// 			CommandLine cmd = new CommandLine( this );
// 			cmd.usage(System.out);
			return(1);
		}
		
		if(dryRun){
			p.printAll();
			System.err.println(outputOptions.outputMatrix.get(0));
			return(1);
		}
		
		TagPileup script_obj = new TagPileup(bedFile, bamFile, p, null, outputOptions.outputMatrix.get(0), false);
		script_obj.run();
		
		System.err.println( "Calculations complete" );
		return(0);
	}
	
// 	private String strNotNull(Object o ){
// 		if(o!=null){ return( o.toString() ); }
// 		return( "null" );
// 	}
	
	private String validateInput() throws IOException {
		String r = "";
		
		//check input extensions
		if("bed".compareTo(ExtensionFileFilter.getExtension(bedFile))!=0){
			r += "(!)Is this a BED file? Check extension: " + bedFile.getName() +  "\n";
		}
		if("bam".compareTo(ExtensionFileFilter.getExtension(bamFile))!=0){
			r += "(!)Is this a BAM file? Check extension: " + bamFile.getName() +  "\n";
		}
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
		
		//set default output COMPOSITE filename
		if(outputOptions.outputComposite==null){
			outputOptions.outputComposite = "composite_average.out";
// 			String readString = "read1";
// 			if(readType.finalRead == 1) { readString = "read2"; }
// 			else if(readType.finalRead() == 2) { readString = "readc"; }
// 			outputOptions.outputMatrix[0] = ExtensionFileFilter.stripExtension(bedFile.getName()) + "_" + ExtensionFileFilter.stripExtension(bamFile.getName()) + "_" + readString;
		}
		//set default output MATRIX filename
		if(outputOptions.outputMatrix.size()>1){
			outputOptions.outputMatrix.set(0,null);
		} else if(outputOptions.outputMatrix.size()==0){
			String readString = "read1";
			if(readType.finalRead == 1) { readString = "read2"; }
			else if(readType.finalRead == 2) { readString = "readc"; }
			System.err.println(new File(bedFile.getName()));
			System.err.println(new File(bamFile.getName()));
			System.err.println(bedFile.getName());
			System.err.println(ExtensionFileFilter.stripExtension(new File(bedFile.getName())));
			outputOptions.outputMatrix.add( 
				ExtensionFileFilter.stripExtension(new File(bedFile.getName())) + "_" +
				ExtensionFileFilter.stripExtension(new File(bamFile.getName())) + "_" + readString);
		}
		/* <ADD CODE HERE> */ 
		//check output filename is valid
		/* <ADD CODE HERE> */
		//set default output filename (set within scripts/*/TagPileup if input null filenames)
		
		//check ReadType, interpret booleans for int value
		if(readType.read1){ readType.finalRead = 0; }
		else if(readType.read2){ readType.finalRead = 1; }
		else if(readType.allreads){ readType.finalRead = 2; }
		else if(readType.midpoint){ readType.finalRead = 3; }
		
		//check StrandType, interpret booleans for int value
		if(strandType.sepStatus==true){ strandType.finalStrand = 0; }
		else if(strandType.combStatus==true){ strandType.finalStrand = 1; }
		
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
		p.setCompositeFile(outputOptions.outputComposite);
		
		//Set READ
		p.setRead(readType.finalRead);
		//Set STRAND
		p.setStrand(strandType.finalStrand);
		
		//Set smooth type and parameters
		if(smoothType.noSmooth==true){			//default behavior
			p.setTrans(0);
		}else if(smoothType.winStatus==true){   //window default
			p.setTrans(1);
			p.setSmooth(3);
		}else if(smoothType.winVals!=-9999){
			p.setTrans(1);
			p.setSmooth(smoothType.winVals);
		}else if(smoothType.gaussStatus==true){   //gauss default
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
		p.setJTVstatus(false);//not available for CLI
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
		
// 		System.out.println( "SMOOOOOTH:" );
// 		System.out.println( "--no-smooth ==> " + strNotNull(p.getTrans()==0) );
// 		System.out.println( "--window-smooth ==> " + strNotNull(p.getTrans()==1) );
// 		System.out.println( "\tgetSmooth : " + strNotNull(p.getSmooth()) );
// 		System.out.println( "--gauss-smooth ==> " + strNotNull(p.getTrans()==2) );
// 		System.out.println( "\tgetStdSize : " + strNotNull(p.getStdSize()) );
// 		System.out.println( "\tgetStdNum : " + strNotNull(p.getStdNum()) );
		
// 	--separate <sense-color> <anti-color>	indicate we want to differentiate between sense and anti-sense strand piles (default, default=blue,red)
// 	--combined <combined-color>	indicate we want to combine sense and anti-sense strand piles and color (default=black)
		
		return(r);
	}
	
}


// 
// Tag Pileup
// 	command -i <BED1,...> [-1|-2|-a|-m] [-p] [-n <bp-insert-min>] [-x <bp-insert-max>] \
// 	[--separate <sense-rgb> <anti-rgb>|--combined <color-rgb>] [--cpu <num-cpus>]
// 	??????????
// 	-o <out-path-base> [-f CDT|TAB]
// 	
// 	-r	BED
// 	-i	BAM
// 	
// 	-1	pileup read 1 (default)
// 	-2	pileup read 2
// 	-a	pileup all reads
// 	-m	pileup midpoint (require PE)
// 	
// 	-p,--mate-pair	require proper paired ends
// 	-n,--min-insert	<bp-insert-min>	filter by minimum insert size in bp, require PE (default=0)
// 	-x,--max-insert	<bp-insert-max>	filter by maximum insert size in bp, require PE (default=10000??????????????)
// 	
// 	--separate <sense-color> <anti-color>	indicate we want to differentiate between sense and anti-sense strand piles (default, default=blue,red)
// 	--combined <combined-color>	indicate we want to combine sense and anti-sense strand piles and color (default=black)
// 	
// 	--set-tags-to-be-equal	(default=false)
// 	--bin-size <bp-size>	bin size in bp (default=1)
// 	--shift <bp-size>	bin size in bp (default=0)
// 	--cpu <num-cpu>	number of CPUs to use (default=1)
// 	
// 	--filter-blacklist <blacklist_fn>	blacklist and something about tags being equal???????????
// 	
// 	Smoothing options:
// 	--no-smoothing	default
// 	--sliding-window <bin-window-size>	use a sliding window of the indicated number of bins
// 	--gaussian-smooth <bin-size-sd> <bin-num-sd>	use a gaussian smoothing function with the indicated standard deviation size in number of bins and the indicated number of standard deviations (default-size=5,default-num=3)
// 	
// 	-o	output directory/base of composite plot and matrix files (if indicated)
// 	-f [CDT|TAB]	output matrix file format as either CDT or TAB-delimited (default no matrix file outputted)



