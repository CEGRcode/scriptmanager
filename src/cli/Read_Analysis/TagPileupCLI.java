package cli.Read_Analysis;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import java.io.File;

/**
	Read_AnalysisCLI/TagPileupCLI
*/
@Command(name = "tag-pileup", mixinStandardHelpOptions = true,
		description = "Pileup 5' ends of aligned tags given BED and BAM files according to user-defined parameters")
public class TagPileupCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BAM file from which we remove duplicates.")
	private File bamFile;
	
	@Option(names = {"-o", "--output"}, description = "Specify output file ")
	private File output = new File("output.bam");
	@Option(names = {"-f", "--type"}, description = "specify output file type [")
	private File blacklistFilter = null;
	
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">TagPileupCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}

// 
// 
// Tag Pileup
// 	command -i <BED1,...> [-1|-2|-a|-m] [-p] [-n <bp-insert-min>] [-x <bp-insert-max>] \
// 	[--separate <sense-rgb> <anti-rgb>|--combined <color-rgb>] [--cpu <num-cpus>]
// 	??????????
// 	-o <out-path-base> [-f CDT|TAB]
// 	
// 	-r	BED or list of BED files describing region to pileup within
// 	-i	BAM or list of BAM files
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
// 	--tag-shift <bp-shift>	tag shift in bp (default=0)
// 	--bin-size <bp-size>	bin size in bp (default=1)
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
// 	