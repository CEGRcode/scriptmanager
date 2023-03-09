package scripts.BAM_Statistics;

import htsjdk.samtools.AbstractBAMFileIndex;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.Date;

public class SEStats {
	
	public static void getSEStats( File out_filepath, File bamFile, PrintStream ps ) {
		
		final SamReaderFactory factory = SamReaderFactory.makeDefault().enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS, SamReaderFactory.Option.VALIDATE_CRC_CHECKSUMS).validationStringency(ValidationStringency.SILENT);
		
		//Check and set output files (STDOUT if not specified)
		PrintStream OUT = null;
		if(out_filepath != null) {
			try {
				OUT = new PrintStream(out_filepath);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{ OUT = System.out; }
		
		//Print TimeStamp
		String time = getTimeStamp();
		printBoth( ps, OUT, time );
		
		//Check if BAI index file exists
		File f = new File(bamFile + ".bai");
		if(f.exists() && !f.isDirectory()) {
			
			printBoth( ps, OUT, bamFile.getName() );
			printBoth( ps, OUT, "Chromosome_ID\tChromosome_Size\tAligned_Reads\tUnaligned_Reads" );
			
			//Code to get individual chromosome stats
			//reader = new SamReader(bamFiles.get(x), new File(bamFiles.get(x) + ".bai"));
			SamReader reader = factory.open(bamFile);
			
			AbstractBAMFileIndex bai = (AbstractBAMFileIndex) reader.indexing().getIndex();
			double totalTags = 0;
			double totalGenome = 0;
		
			for (int z = 0; z < bai.getNumberOfReferences(); z++) {
				SAMSequenceRecord seq = reader.getFileHeader().getSequence(z);
				double aligned = reader.indexing().getIndex().getMetaData(z).getAlignedRecordCount();
				double unaligned = reader.indexing().getIndex().getMetaData(z).getUnalignedRecordCount();
				printBoth( ps, OUT, seq.getSequenceName() + "\t" + seq.getSequenceLength() + "\t" + aligned + "\t" + unaligned );
				totalTags += aligned;
				totalGenome += seq.getSequenceLength();
			}
			
			printBoth( ps, OUT, "Total Genome Size: " + totalGenome + "\tTotal Aligned Tags: " + totalTags + "\n" );
			
			//Output replicates used to make bam file
			for( String comment : reader.getFileHeader().getComments()) { printBoth( ps, OUT, comment ); }
			
			//Output program used to align bam file
			for (int z = 0; z < reader.getFileHeader().getProgramRecords().size(); z++) {
				printBoth( ps, OUT, reader.getFileHeader().getProgramRecords().get(z).getId() + "\t" +
									reader.getFileHeader().getProgramRecords().get(z).getProgramVersion() );
				printBoth( ps, OUT, reader.getFileHeader().getProgramRecords().get(z).getCommandLine() );
			}
			
			printBoth( ps, OUT, "" );
			
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bai.close();
		
		//Print message reminder to index BAM files
		} else { printBoth( ps, OUT, "BAI Index File does not exist for: " + bamFile.getName() + "\n" ); }
		
		if(out_filepath != null) OUT.close();
		//BAMIndexMetaData.printIndexStats(bamFiles.get(x))
	}	
	
	//Helper method to de-clutter method above:
	//Prints output to both pop-up window (for GUI) and output file (GUI and CLI)
	private static void printBoth( PrintStream p, PrintStream out, String line ){
		out.println( line );
		if( p != null ){ p.println( line ); }
	}
	
	//Returns Timestamp for printing to the output
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}