package scripts.BAM_Manipulation;

import htsjdk.samtools.AbstractBAMFileIndex;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.samtools.util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class BAMDeDuplication extends JFrame {
	File bamFile = null;
	File output = null;
	
	public BAMDeDuplication(File in, File out) {
		bamFile = in;
		output = out;
	}
	
	public void run() throws IOException {
		//Check if BAI index file exists
		File f = new File(bamFile + ".bai");
		if(f.exists() && !f.isDirectory()) {
			
			IOUtil.assertFileIsReadable(bamFile);
			IOUtil.assertFileIsWritable(output);
			final SamReader reader = SamReaderFactory.makeDefault().open(bamFile);
			reader.getFileHeader().setSortOrder(SAMFileHeader.SortOrder.coordinate);
			final SAMFileWriter writer = new SAMFileWriterFactory().makeSAMOrBAMWriter(reader.getFileHeader(), false, output);
			//Code to get individual chromosome stats
			AbstractBAMFileIndex bai = (AbstractBAMFileIndex) reader.indexing().getIndex();
				
			//Variables to contain duplication rates
			HashMap<String, Integer> CHROM_COMPLEXITY = null;
				
			for (int z = 0; z < bai.getNumberOfReferences(); z++) {
				SAMSequenceRecord seq = reader.getFileHeader().getSequence(z);
				
				System.out.println(seq.getSequenceName());
				//Loop through each chromosome looking at each perfect F-R PE read
				CHROM_COMPLEXITY = new HashMap<String, Integer>();
				CloseableIterator<SAMRecord> iter = reader.query(seq.getSequenceName(), 0, seq.getSequenceLength(), false);
				while (iter.hasNext()) {
					//Create the record object 
					SAMRecord sr = iter.next();
									
					if(sr.getReadPairedFlag()) {
						if(sr.getProperPairFlag() && sr.getFirstOfPairFlag()) {
							//Unique ID
							String tagName = sr.getAlignmentStart() + "_" + sr.getMateAlignmentStart() + "_" + sr.getInferredInsertSize();
							//Duplication rate for each chrom determined
							if(CHROM_COMPLEXITY.isEmpty()) {
								writer.addAlignment(sr);
								CHROM_COMPLEXITY.put(tagName, new Integer(1));
							} else if(!CHROM_COMPLEXITY.containsKey(tagName)) {
								writer.addAlignment(sr);
								CHROM_COMPLEXITY.put(tagName, new Integer(1));
							}
						}
					}	
				}
				iter.close();
			}
			writer.close();
			reader.close();
			bai.close();

		} else {
			JOptionPane.showMessageDialog(null, "BAI Index File does not exist for: " + bamFile.getName() + "\n");
		}
	}
}
