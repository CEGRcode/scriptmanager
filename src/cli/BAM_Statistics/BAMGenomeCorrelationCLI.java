package cli.BAM_Statistics;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import htsjdk.samtools.AbstractBAMFileIndex;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Date;

// import scripts.BAM_Statistics.SEStats;

	
/**
	BAM_StatisticsCLI/SEStats
*/
@Command(name = "bam-correlation", mixinStandardHelpOptions = true,
		description="Genome-Genome correlations for replicate comparisons given multiple sorted and indexed (BAI) BAM files.")
public class BAMGenomeCorrelationCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BAM file whose statistics we want.")
	private File bamFile;

	@Option(names = {"-o", "--output"}, description = "Specify output file ")
	private File output = new File("output-bam-stats.txt");
	@Option(names = {"-1", "--read1"}, description = "correlate with read 1 (default)")
	private boolean read1 = true;
	@Option(names = {"-2", "--read2"}, description = "correlate with read 2")
	private boolean read2 = true;
	@Option(names = {"-a", "--all-reads"}, description = "correlate with all reads")
	private boolean allreads = true;
	@Option(names = {"-m", "--midpoint"}, description = "correlate with midpoint (requires PE)")
	private boolean midpoint = true;
	@Option(names = {"-t", "--tag-shift"}, description = "tag shift in bp (default 0)")
	private int tagshift = 0;
	@Option(names = {"-b", "--bin-size"}, description = "bin size in bp (default 10)")
	private int binSize = 10;
	@Option(names = {"-cpu", "--cpu"}, description = "CPUs to use (default 1)")
	private int cpu = 0;
	
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">BAMGenomeCorrelationCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}
	

