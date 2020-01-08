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
	BAM_StatisticsCLI/PEStatsCLI
*/
@Command(name = "pe-stat", mixinStandardHelpOptions = true,
		description="Generates Insert-size Histogram statistic (GEO requirement) and outputs BAM Header including alignment statistics and parameters given a sorted and indexed (BAI) paired-end BAM File.")
public class PEStatsCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BAM file whose statistics we want.")
	private File bamFile;

	@Option(names = {"-o", "--output"}, description = "specify output file")
	private File output = new File("output-bam-stats.txt");
	@Option(names = {"-n", "--min"}, description = "histogram range minimum (0 default)")
	private int min = 0;
	@Option(names = {"-x", "--max"}, description = "histogram range maximum (1000 default)")
	private int max = 0;
	@Option(names = {"-d", "--duplication-stats"}, description = "calculate duplication statistics if this flag is used (default false)")
	private boolean duplication = false;
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">PEStatsCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, outputFile );
		return(0);
	}
	
}
	

