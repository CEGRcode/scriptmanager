package scripts.BAM_Manipulation;

/*
 * The MIT License
 *
 * Copyright (c) 2009 The Broad Institute
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import htsjdk.samtools.MergingSamRecordIterator;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SamFileHeaderMerger;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.util.IOUtil;
import htsjdk.samtools.util.Log;
import htsjdk.samtools.util.ProgressLogger;
import picard.cmdline.CommandLineProgram;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a SAM or BAM file and combines the output to one file
 *
 * @author Tim Fennell
 */
public class MergeSamFiles extends CommandLineProgram {
	private static final Log log = Log.getInstance(MergeSamFiles.class);
	public List<File> INPUT = new ArrayList<File>();
	public File OUTPUT;
	public SAMFileHeader.SortOrder SORT_ORDER = SAMFileHeader.SortOrder.coordinate;
	public boolean ASSUME_SORTED = false;
	public boolean MERGE_SEQUENCE_DICTIONARIES = false;
	public boolean USE_THREADING = false;
	public List<String> COMMENT = new ArrayList<String>();

	private static final int PROGRESS_INTERVAL = 1000000;

	public MergeSamFiles(List<File> in, File out) {
		INPUT = in;
		OUTPUT = out;
	}

	public MergeSamFiles(List<File> in, File out, boolean thread) {
		INPUT = in;
		OUTPUT = out;
		USE_THREADING = thread;
	}

	public int run() {
		doWork();
		return 0;
	}

	/** Combines multiple SAM/BAM files into one. */
	protected int doWork() {
		boolean matchedSortOrders = true;
		// ASSUME_SORTED = true;

		// Open the files for reading and writing
		final List<SamReader> readers = new ArrayList<SamReader>();
		final List<SAMFileHeader> headers = new ArrayList<SAMFileHeader>();
		{
			SAMSequenceDictionary dict = null; // Used to try and reduce redundant SDs in memory

			for (final File inFile : INPUT) {
				IOUtil.assertFileIsReadable(inFile);
				final SamReader in = SamReaderFactory.makeDefault().referenceSequence(REFERENCE_SEQUENCE).open(inFile);
				readers.add(in);
				headers.add(in.getFileHeader());

				// A slightly hackish attempt to keep memory consumption down when merging
				// multiple files with
				// large sequence dictionaries (10,000s of sequences). If the dictionaries are
				// identical, then
				// replace the duplicate copies with a single dictionary to reduce the memory
				// footprint.
				if (dict == null) {
					dict = in.getFileHeader().getSequenceDictionary();
				} else if (dict.equals(in.getFileHeader().getSequenceDictionary())) {
					in.getFileHeader().setSequenceDictionary(dict);
				}
				matchedSortOrders = matchedSortOrders && in.getFileHeader().getSortOrder() == SORT_ORDER;
			}
		}

		// If all the input sort orders match the output sort order then just merge them
		// and
		// write on the fly, otherwise setup to merge and sort before writing out the
		// final file
		IOUtil.assertFileIsWritable(OUTPUT);
		final boolean presorted;
		final SAMFileHeader.SortOrder headerMergerSortOrder;
		final boolean mergingSamRecordIteratorAssumeSorted;

		if (matchedSortOrders || SORT_ORDER == SAMFileHeader.SortOrder.unsorted || ASSUME_SORTED) {
			log.info("Input files are in same order as output so sorting to temp directory is not needed.");
			headerMergerSortOrder = SORT_ORDER;
			mergingSamRecordIteratorAssumeSorted = ASSUME_SORTED;
			presorted = true;
		} else {
			log.info("Sorting input files using temp directory " + TMP_DIR);
			headerMergerSortOrder = SAMFileHeader.SortOrder.unsorted;
			mergingSamRecordIteratorAssumeSorted = false;
			presorted = false;
		}

		final SamFileHeaderMerger headerMerger = new SamFileHeaderMerger(headerMergerSortOrder, headers,
				MERGE_SEQUENCE_DICTIONARIES);
		final MergingSamRecordIterator iterator = new MergingSamRecordIterator(headerMerger, readers,
				mergingSamRecordIteratorAssumeSorted);
		final SAMFileHeader header = headerMerger.getMergedHeader();
		for (final String comment : COMMENT) {
			header.addComment(comment);
		}

		// Unique to this build
		for (final File inFile : INPUT) {
			header.addComment("@CO\tReplicate:" + inFile.getName());
		}

		header.setSortOrder(SORT_ORDER);
		final SAMFileWriterFactory samFileWriterFactory = new SAMFileWriterFactory();
		if (USE_THREADING) {
			samFileWriterFactory.setUseAsyncIo(true);
		}
		final SAMFileWriter out = samFileWriterFactory.makeSAMOrBAMWriter(header, presorted, OUTPUT);

		// Lastly loop through and write out the records
		final ProgressLogger progress = new ProgressLogger(log, PROGRESS_INTERVAL);
		while (iterator.hasNext()) {
			final SAMRecord record = iterator.next();
			out.addAlignment(record);
			progress.record(record);
		}

		log.info("Finished reading inputs.");
		iterator.close();
		out.close();
		return 0;
	}
}