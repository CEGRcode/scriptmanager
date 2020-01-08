package cli.BAM_Format_Converter;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

// import htsjdk.samtools.AbstractBAMFileIndex;
// import htsjdk.samtools.SAMSequenceRecord;
// import htsjdk.samtools.SamReader;
// import htsjdk.samtools.SamReaderFactory;
// import htsjdk.samtools.ValidationStringency;
// 
import java.io.File;
// import java.io.FileNotFoundException;
// import java.io.IOException;
// import java.io.PrintStream;
// import java.net.URISyntaxException;
// import java.sql.Timestamp;
// import java.util.Date;




	
/**
	BAM_Format_ConverterCLI/SEStatsCLI
*/
@Command(name = "bam-to-bed", mixinStandardHelpOptions = true,
		description = "Convert BAM file to BED file")
public class BAMtoBEDCLI implements Callable<Integer> {
	
	@Parameters( index = "0", description = "The BAM file from which we generate a new file.")
	private File bamFile;

	@Option(names = {"-o", "--output"}, description = "Specify output file ")
	private File output = new File("output-bed-from-bam.txt");
	@Option(names = {"-1", "--read1"}, description = "output read 1 (default)")
	private boolean read1 = true;
	@Option(names = {"-2", "--read2"}, description = "output read 2")
	private boolean read2 = true;
	@Option(names = {"-c", "--combined"}, description = "output combined")
	private boolean combined = true;
	@Option(names = {"-m", "--midpoint"}, description = "output midpoint (requires PE)")
	private boolean midpoint = true;
	@Option(names = {"-p", "--mate-pair"}, description = "require proper mate pair (default not required)")
	private boolean matePair = false;
	@Option(names = {"-b", "--bin-size"}, description = "bin size in bp (default 10)")
	private int binSize = 10;
	@Option(names = {"-cpu", "--cpu"}, description = "CPUs to use (default 1)")
	private int cpu = 0;
	@Option(names = {"-n", "--min-insert"}, description = "filter by min insert size in bp (default 0)")
	private int minInsert = 0;
	@Option(names = {"-x", "--max-insert"}, description = "filter by max insert size in bp (default 1000)")
	private int maxInsert = 0;	
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">BAMtoBEDCLI.call()" );
// 		SEStats stat = new SEStats( bamFile, output );		
		return(0);
	}
	
}
	

