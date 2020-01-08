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

import scripts.BAM_Statistics.SEStats;

	
/**
	BAM_StatisticsCLI/SEStatsCLI
*/
@Command(name = "se-stat", mixinStandardHelpOptions = true,
		description = "Output BAM Header including alignment statistics and parameters given any indexed (BAI) BAM File.")
public class SEStatsCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BAM file whose statistics we want.")
	private File bamFile;

	@Option(names = {"-o", "--output"}, description = "Specify output file ")
	private File output = new File("output-bam-stats.txt");
	
	final SamReaderFactory factory = SamReaderFactory.makeDefault().enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS, SamReaderFactory.Option.VALIDATE_CRC_CHECKSUMS).validationStringency(ValidationStringency.SILENT);
	SamReader reader;
	PrintStream OUT = null;
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">SEStatsCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}
	

