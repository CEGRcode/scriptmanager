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
import java.util.Vector;
import java.util.Date;

import scripts.BAM_Statistics.BAMGenomeCorrelation;

	
/**
	BAM_StatisticsCLI/SEStats
*/
@Command(name = "bam-correlation", mixinStandardHelpOptions = true,
		description="Genome-Genome correlations for replicate comparisons given multiple sorted and indexed (BAI) BAM files.")
public class BAMGenomeCorrelationCLI implements Callable<Integer> {
	
	@Parameters( index = "0..", description = "The BAM file whose statistics we want.")
	private File[] bamFiles;

	@Option(names = {"-f", "--files"}, description = "Input list of BAM filenames to correlate")
	private File file_list;
	@Option(names = {"-o", "--output"}, description = "Specify output file ")
	private File output = new File("correlation_matrix");
	@Option(names = {"-1", "--read1"}, description = "correlate with read 1 (default)")
	private boolean read1 = false;
	@Option(names = {"-2", "--read2"}, description = "correlate with read 2")
	private boolean read2 = false;
	@Option(names = {"-a", "--all-reads"}, description = "correlate with all reads")
	private boolean allreads = false;
	@Option(names = {"-m", "--midpoint"}, description = "correlate with midpoint (requires PE)")
	private boolean midpoint = false;
	@Option(names = {"-t", "--tag-shift"}, description = "tag shift in bp (default 0)")
	private int tagshift = 0;
	@Option(names = {"-b", "--bin-size"}, description = "bin size in bp (default 10)")
	private int binSize = 10;
	@Option(names = {"-cpu", "--cpu"}, description = "CPUs to use (default 1)")
	private int cpu = 1;
	
	private int RTYPE;
	private Vector<File> vFiles;
	
	@Override
	public Integer call() throws Exception {
		System.out.println( ">BAMGenomeCorrelationCLI.call()" );
		
		if( validateInput()!=0 ){
			System.err.println("Invalid input. Check usage using '-h' or '--help'");
			return(1);
		}
		vFiles = getFileVector();
		
// 		System.out.println("blah3");
		BAMGenomeCorrelation b_coor = new BAMGenomeCorrelation( getFileVector(), output, true, tagshift, binSize, cpu, RTYPE);
		b_coor.getBAMGenomeCorrelation(null);
		System.err.println("Calculations Complete");
		
		return(0);
	}
	
	private Vector<File> getFileVector(){
		Vector<File> vFiles = new Vector<File>(bamFiles.length);
// 		System.out.println("blah0");
		for( int i=0; i<bamFiles.length; i++){
// 			System.out.println("blah1");
			vFiles.add(i,bamFiles[i]);
		}
// 		System.out.println("blah2");
		return( vFiles );
	}
	
	private Integer validateInput(){
		
		//Make sure only one of the -1 -2 -a -m  flags are used
		int corr_type;
		corr_type  = (read1) ? 1 : 0;
		corr_type += (read2) ? 1 : 0;
		corr_type += (allreads) ? 1 : 0;
		corr_type += (midpoint) ? 1 : 0;
		if( corr_type > 1 ){
			System.err.println( "!!! You can only use one of the correlation type tags: read1(-1), read2(-2), allreads(-a), midpoint(-m)!" );
			return(1);
		}
		System.out.println("corr_type=" + Integer.toString(corr_type));
		
		//Assign type value after validating 
		if(corr_type==0){ RTYPE = 0; }   //default assignment
		else if(read1) { RTYPE = 0; }
		else if(read2) { RTYPE = 1; }
		else if(allreads) { RTYPE = 2; }
		else if(midpoint) { RTYPE = 3; }
		
		//Check that positional file listings not used in conjunction with -f flag
		
		//Import files as Vector list (scan input file if -f flag used)
		
		return(0);
	}
	
}
	

