package util;

import java.io.File;
import java.io.IOException;

import htsjdk.samtools.AbstractBAMFileIndex;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.util.CloseableIterator;

public class BAMUtilities {
	
	public static double calculateStandardizationRatio(File BAM, int read) throws IOException {
		SamReader inputSam = SamReaderFactory.makeDefault().open(BAM);
		AbstractBAMFileIndex bai = (AbstractBAMFileIndex) inputSam.indexing().getIndex();
		double totalAligned = 0;
		double totalGenome = 0;
		
		for (int x = 0; x < bai.getNumberOfReferences(); x++) {
			SAMSequenceRecord seq = inputSam.getFileHeader().getSequence(x);
			totalAligned += inputSam.indexing().getIndex().getMetaData(x).getAlignedRecordCount();
			totalGenome += seq.getSequenceLength();
		}
		
		double READ1 = 0;
		double READ2 = 0;
		double MID = 0;
		CloseableIterator<SAMRecord> iter = inputSam.iterator();
		while (iter.hasNext()) {
			SAMRecord sr = iter.next();
			if(!sr.getReadUnmappedFlag()) { //Test for mappability
				if(sr.getReadPairedFlag()) { //Test for paired-end status
					if(sr.getSecondOfPairFlag()) { READ2++; } //count read 2
					else if(sr.getFirstOfPairFlag()) { READ1++; } // count read 1
					if(sr.getProperPairFlag() && sr.getFirstOfPairFlag()) { MID++; } //count properly paired reads
				} else { //If the read is mapped but not paired-end, default to read 1
					READ1++;
				}
			}
		}
		iter.close();
		inputSam.close();
		bai.close();
				
		//System.out.println("Genome Size: " + totalGenome + "\nTotal tags: " + totalAligned + "\nDetected Read 1: " + READ1 + "\nDetected Read 2: " + READ2 + "\nDetected Midpoints: " + MID);
		if(read == 0) { totalAligned = READ1; }
		else if(read == 1) { totalAligned = READ2; }
		else if(read == 2) { totalAligned = READ1 + READ2; }
		else if(read == 3) { totalAligned = MID; }

		if(totalAligned > 0) { return (totalAligned / totalGenome); }
		else { return 1; }
	}
}
