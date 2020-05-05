package cli.BAM_Statistics;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;
import java.util.Vector;

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

import scripts.BAM_Statistics.SEStats;
	
/**
	BAM_StatisticsCLI/SEStatsCLI
	//java -jar build/libs/ScriptManager-0.12.jar bam-statistics se-stat <bam.in> [-o <output.filename>]
*/
@Command(name = "se-stat", mixinStandardHelpOptions = true,
		description = "Output BAM Header including alignment statistics and parameters given any indexed (BAI) BAM File.")
public class SEStatsCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BAM file whose statistics we want.")
	private File bamFile;
	
	@Option(names = {"-o", "--output"}, description = "Specify output file ")
	private File output;
	
	@Override
	public Integer call() throws Exception {
		System.err.println( ">SEStatsCLI.call()" );
		
		if( validateInput()!=0 ){
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
		}
		
		SEStats.getSEStats( output, bamFile, null );
		System.err.println("Stats Calculations Complete");
		
		return(0);
	}
	
	private Integer validateInput(){
		// Define default behavior
		return(0);
	}
	
}
	

