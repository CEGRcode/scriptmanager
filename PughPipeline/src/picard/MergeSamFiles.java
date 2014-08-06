package picard;

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

import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.io.IoUtil;
import net.sf.picard.sam.MergingSamRecordIterator;
import net.sf.picard.sam.SamFileHeaderMerger;
import net.sf.picard.util.Log;
import net.sf.picard.util.ProgressLogger;
import net.sf.samtools.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a SAM or BAM file and combines the output to one file
 *
 * @author Tim Fennell
 */
public class MergeSamFiles extends CommandLineProgram {
	
	/**
	 * A set of String constants in which the name of the constant (minus the _SHORT_NAME suffix)
	 * is the standard long Option name, and the value of the constant is the standard shortName.
	 */
	public class StandardOptionDefinitions {
	    public static final String INPUT_SHORT_NAME = "I";
	    public static final String OUTPUT_SHORT_NAME = "O";
	    public static final String REFERENCE_SHORT_NAME = "R";
	    public static final String SAMPLE_ALIAS_SHORT_NAME = "ALIAS";
	    public static final String LIBRARY_NAME_SHORT_NAME = "LIB";
	    public static final String EXPECTED_INSERT_SIZE_SHORT_NAME = "INSERT";
	    public static final String LANE_SHORT_NAME = "L";
	    public static final String SEQUENCE_DICTIONARY_SHORT_NAME = "SD";
	    public static final String METRICS_FILE_SHORT_NAME = "M";
	    public static final String ASSUME_SORTED_SHORT_NAME = "AS";
	    public static final String PF_READS_ONLY_SHORT_NAME = "PF";
	    public static final String MINIMUM_MAPPING_QUALITY_SHORT_NAME = "MQ";
	    public static final String READ_GROUP_ID_SHORT_NAME = "RG";
	    public static final String PROGRAM_RECORD_ID_SHORT_NAME = "PG";
	    public static final String MINIMUM_LOD_SHORT_NAME = "LOD";
	    public static final String SORT_ORDER_SHORT_NAME = "SO";
	    public static final String USE_ORIGINAL_QUALITIES_SHORT_NAME = "OQ";
	}
	
    private static final Log log = Log.getInstance(MergeSamFiles.class);

    // Usage and parameters
    @Usage
    public String USAGE = "Merges multiple SAM/BAM files into one file.\n";

    @Option(shortName="I", doc="SAM or BAM input file", minElements=1)
    public static List<File> INPUT = new ArrayList<File>();

    @Option(shortName="O", doc="SAM or BAM file to write merged result to")
    public static File OUTPUT;

    @Option(shortName=StandardOptionDefinitions.SORT_ORDER_SHORT_NAME, doc="Sort order of output file", optional=true)
    public SAMFileHeader.SortOrder SORT_ORDER = SAMFileHeader.SortOrder.coordinate;

    @Option(doc="If true, assume that the input files are in the same sort order as the requested output sort order, even if their headers say otherwise.",
    shortName = StandardOptionDefinitions.ASSUME_SORTED_SHORT_NAME)
    public boolean ASSUME_SORTED = false;

    @Option(shortName="MSD", doc="Merge the sequence dictionaries", optional=true)
    public boolean MERGE_SEQUENCE_DICTIONARIES = false;

    @Option(doc="Option to create a background thread to encode, " +
            "compress and write to disk the output file. The threaded version uses about 20% more CPU and decreases " +
            "runtime by ~20% when writing out a compressed BAM file.")
    public boolean USE_THREADING = false;

    @Option(doc="Comment(s) to include in the merged output file's header.", optional=true, shortName="CO")
    public List<String> COMMENT = new ArrayList<String>();

    private static final int PROGRESS_INTERVAL = 1000000;

//    /** Required main method implementation. */
//    public static void main(final String[] argv) {
//        System.exit(new MergeSamFiles().instanceMain(argv));
//    }
    
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
        final List<SAMFileReader> readers = new ArrayList<SAMFileReader>();
        final List<SAMFileHeader> headers = new ArrayList<SAMFileHeader>();
        {
            SAMSequenceDictionary dict = null; // Used to try and reduce redundant SDs in memory

            for (final File inFile : INPUT) {
                IoUtil.assertFileIsReadable(inFile);
                final SAMFileReader in = new SAMFileReader(inFile);
                readers.add(in);
                headers.add(in.getFileHeader());

                // A slightly hackish attempt to keep memory consumption down when merging multiple files with
                // large sequence dictionaries (10,000s of sequences). If the dictionaries are identical, then
                // replace the duplicate copies with a single dictionary to reduce the memory footprint. 
                if (dict == null) {
                    dict = in.getFileHeader().getSequenceDictionary();
                }
                else if (dict.equals(in.getFileHeader().getSequenceDictionary())) {
                    in.getFileHeader().setSequenceDictionary(dict);
                }
                matchedSortOrders = matchedSortOrders && in.getFileHeader().getSortOrder() == SORT_ORDER;
            }
        }

        // If all the input sort orders match the output sort order then just merge them and
        // write on the fly, otherwise setup to merge and sort before writing out the final file
        IoUtil.assertFileIsWritable(OUTPUT);
        final boolean presorted;
        final SAMFileHeader.SortOrder headerMergerSortOrder;
        final boolean mergingSamRecordIteratorAssumeSorted;

        if (matchedSortOrders || SORT_ORDER == SAMFileHeader.SortOrder.unsorted || ASSUME_SORTED) {
            log.info("Input files are in same order as output so sorting to temp directory is not needed.");
            headerMergerSortOrder = SORT_ORDER;
            mergingSamRecordIteratorAssumeSorted = ASSUME_SORTED;
            presorted = true;
        }
        else {
            log.info("Sorting input files using temp directory " + TMP_DIR);
            headerMergerSortOrder = SAMFileHeader.SortOrder.unsorted;
            mergingSamRecordIteratorAssumeSorted = false;
            presorted = false;
        }
        
        final SamFileHeaderMerger headerMerger = new SamFileHeaderMerger(headerMergerSortOrder, headers, MERGE_SEQUENCE_DICTIONARIES);
        final MergingSamRecordIterator iterator = new MergingSamRecordIterator(headerMerger, readers, mergingSamRecordIteratorAssumeSorted);
        final SAMFileHeader header = headerMerger.getMergedHeader();
        for (final String comment : COMMENT) {
            header.addComment(comment);
        }
        
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

    @Override
    protected String[] customCommandLineValidation() {
        if (CREATE_INDEX && SORT_ORDER != SAMFileHeader.SortOrder.coordinate) {
            return new String[]{"Can't CREATE_INDEX unless SORT_ORDER is coordinate"};
        }
        return null;
    }

}