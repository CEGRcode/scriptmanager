package scriptmanager.scripts.BAM_Manipulation;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.util.IOUtil;
import picard.sam.SortSam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Erik Pavloski
 * The following code uses Picard's SortSam to sort a BAM file by coordinate
 */
public class BAMFileSort {
    public static File sort(File input, File output) throws IOException {
        // Tells user their File is being sorted
        System.out.println("Sorting Bam File...");
        try {
            output = new File(input.getCanonicalPath() + "sorted.bam");
            // Sorts the BAM file
            final SortSam sorter = new SortSam();
            final ArrayList<String> args = new ArrayList<>();
            args.add("INPUT=" + input.getAbsolutePath());
            args.add("OUTPUT=" + output.getAbsolutePath());
            args.add("SORT_ORDER=" + SAMFileHeader.SortOrder.coordinate);
            sorter.instanceMain(args.toArray(new String[args.size()]));
            System.out.println("BAM File Sorted");
            return output;
        } catch (htsjdk.samtools.SAMException exception) {
            System.out.println(exception.getMessage());
            output = null;
        }
        return output;
    }
}