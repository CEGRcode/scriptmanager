package scriptmanager.scripts.BAM_Manipulation;

import htsjdk.samtools.SAMException;

import java.io.IOException;
import java.io.File;

/**
 * @author Erik Pavloski
 * This code runs the FilterSamReads Picard tool
 * @see scriptmanager.window_interface.BAM_Manipulation.FilterSamReadsWindow
 */
public class FilterSamReadsWrapper {
    /**
     * @param input the file to be filtered
     * @param output the file to be outputted
     * @param filter which filter the user wants to use can be includeReadList or includePairIntervals
     *               true == includeReadList
     *               false == includePairIntervals
     * @param readListFile the txt file output for the read list if that is the chosen filter
     * @param intervalList the list of interval output file if that is the desired filter
     *
     * @throws IOException
     * @throws SAMException
     */
    public static void run(File input, File output, boolean filter, File readListFile, File intervalList) throws IOException, SAMException {

    }
}
