package scripts.BAM_Manipulation;

import htsjdk.samtools.AbstractBAMFileIndex;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.samtools.util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import objects.CustomExceptions.FASTAException;
import util.FASTAUtilities;

public class FilterforPIPseq {
	File bamFile = null;
	File genome = null;
	File output = null;
	String SEQ = "";

	private PrintStream PS = null;

	public FilterforPIPseq(File in, File gen, File out, String s, PrintStream ps) throws IOException, FASTAException {
		bamFile = in;
		genome = gen;
		output = out;
		SEQ = s.toUpperCase();
		PS = ps;

		File FAI = new File(genome + ".fai");
		// Check if FAI index file exists
		if (!FAI.exists() || FAI.isDirectory()) {
			FASTAUtilities.buildFASTAIndex(genome);
		}
	}

	public void run() throws IOException, InterruptedException {
		IndexedFastaSequenceFile QUERY = new IndexedFastaSequenceFile(genome);

		IOUtil.assertFileIsReadable(bamFile);
		IOUtil.assertFileIsWritable(output);
		final SamReader reader = SamReaderFactory.makeDefault().open(bamFile);
		reader.getFileHeader().setSortOrder(SAMFileHeader.SortOrder.coordinate);
		final SAMFileWriter writer = new SAMFileWriterFactory().makeSAMOrBAMWriter(reader.getFileHeader(), false,
				output);

		printBoth(bamFile.getName()); // output file name to textarea

		// Code to get individual chromosome stats
		AbstractBAMFileIndex bai = (AbstractBAMFileIndex) reader.indexing().getIndex();
		for (int z = 0; z < bai.getNumberOfReferences(); z++) {
			SAMSequenceRecord seq = reader.getFileHeader().getSequence(z);

			printBoth(seq.getSequenceName());

			CloseableIterator<SAMRecord> iter = reader.query(seq.getSequenceName(), 0, seq.getSequenceLength(), false);
			while (iter.hasNext()) {
				// Create the record object
				SAMRecord sr = iter.next();
				if (sr.getReadPairedFlag()) {
					if (sr.getProperPairFlag() && sr.getFirstOfPairFlag()) {
						String filter = "";
						// if on the positive strand
						if (!sr.getReadNegativeStrandFlag()) {
							if (sr.getUnclippedStart() - 1 > 0) {
								filter = new String(QUERY.getSubsequenceAt(seq.getSequenceName(),
										sr.getUnclippedStart() - SEQ.length(), sr.getUnclippedStart() - 1).getBases());
							}
						} else {
							if (sr.getUnclippedEnd() + 1 <= seq.getSequenceLength()) {
								filter = new String(QUERY.getSubsequenceAt(seq.getSequenceName(),
										sr.getUnclippedEnd() + 1, sr.getUnclippedEnd() + SEQ.length()).getBases());
								filter = FASTAUtilities.RevComplement(filter);
							}
						}
						// System.out.println(sr.getReadString() + "\t" + seq.getSequenceName() + "\t" +
						// sr.getUnclippedStart() + "\t" + sr.getUnclippedEnd() + "\t" +
						// sr.getReadNegativeStrandFlag() + "\t" + filter);
						if (filter.toUpperCase().equals(SEQ)) {
							writer.addAlignment(sr);
						}
					}
				} else {
					String filter = "";
					// if on the positive strand
					if (!sr.getReadNegativeStrandFlag()) {
						filter = new String(QUERY.getSubsequenceAt(seq.getSequenceName(),
								sr.getUnclippedStart() - SEQ.length(), sr.getUnclippedStart() - 1).getBases());
					} else {
						filter = new String(QUERY.getSubsequenceAt(seq.getSequenceName(), sr.getUnclippedEnd() + 1,
								sr.getUnclippedEnd() + SEQ.length()).getBases());
						filter = FASTAUtilities.RevComplement(filter);
					}
					// System.out.println(sr.getReadString() + "\t" + seq.getSequenceName() + "\t" +
					// sr.getUnclippedStart() + "\t" + sr.getUnclippedEnd() + "\t" +
					// sr.getReadNegativeStrandFlag() + "\t" + filter);
					if (filter.toUpperCase().equals(SEQ)) {
						writer.addAlignment(sr);
					}
				}
			}
			iter.close();
		}
		QUERY.close();
		writer.close();
		reader.close();
		bai.close();
	}

	private void printBoth(String message) {
		if (PS != null) {
			PS.println(message);
		}
		System.err.println(message);
	}
}
