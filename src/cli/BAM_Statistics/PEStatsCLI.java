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

import scripts.BAM_Statistics.PEStats;

	
/**
	BAM_StatisticsCLI/PEStatsCLI
*/
@Command(name = "pe-stat", mixinStandardHelpOptions = true,
		description="Generates Insert-size Histogram statistic (GEO requirement) and outputs BAM Header including alignment statistics and parameters given a sorted and indexed (BAI) paired-end BAM File.")
public class PEStatsCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BAM file whose statistics we want.")
	private File bamFile;

	@Option(names = {"-o", "--output"}, description = "specify output basename, default=\"output-bam-stats\"")
	private File output = new File( "output-bam-stats" );
	@Option(names = {"-n", "--min"}, description = "histogram range minimum (0 default)")
	private int min = 0;
	@Option(names = {"-x", "--max"}, description = "histogram range maximum (1000 default)")
	private int max = 1000;
	@Option(names = {"-s", "--summary"}, description = "write summary of insert histogram by chromosome (default false)")
	private boolean sum = false;
	@Option(names = {"-d", "--duplication-stats"}, description = "calculate duplication statistics if this flag is used (default false)")
	private boolean dup = false;
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">PEStatsCLI.call()" );
		
		if( validateInput()!=0 ){
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
		}
		
		PEStats.getPEStats( output, bamFile, dup, min, max, null, null, sum);
		System.err.println("Calculations Complete");
		
		return(0);
	}
	
	private Integer validateInput(){
		// Define default behavior
// 		if( !out && !dup ){
// 			//User using CLI should never get here...something wonky if they do...
// 			System.out.println( "!!!What's the point of me? You've set both insert and dup stats to false!" );
// 		}
		
		return(0);
	}
	
	
}
	

