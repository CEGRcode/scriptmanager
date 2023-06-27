package scriptmanager.scripts.BAM_Format_Converter;
import htsjdk.samtools.SAMException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Erik Pavloski
 * @see scriptmanager.window_interface.BAM_Format_Converter.SamtoFastqWindow
 * This code runs the Picard tool SamtoFastq
 */

public class SamtoFastqWrapper {
public static void run(File input, File output, boolean compress, boolean perRG, File outputDir) throws IOException, SAMException {
        /**
         * @param input the BAM/SAM file to be converted
         * @param output the output fastq file
         * @param compress a boolean to determine whether to compress the output or not
         *                 default = false = do not compress
         * @param perRG a boolean to determine whether to output per read group. Compress does this as well
         * @param outputDir the output directory
         * @throws IOException
         * @throws SAMException
         */
        System.out.println("Converting file...");
            // Converts the SAM/BAM file to fastq
            final picard.sam.SamToFastq samToFastq = new picard.sam.SamToFastq();
            final ArrayList<String> args = new ArrayList<>();
            args.add("INPUT=" + input.getAbsolutePath());
            if (compress) {
                args.add("OUTPUT_DIR=" + outputDir.getCanonicalPath());
                args.add("COMPRESS_OUTPUTS_PER_RG=" + true);
            } else if (perRG) {
                args.add("OUTPUT_DIR=" + outputDir.getCanonicalPath());
                args.add("OUTPUT_PER_RG=" + true);
            } else {
                args.add("FASTQ=" + output.getAbsolutePath());
            }
            samToFastq.instanceMain(args.toArray(new String[args.size()]));
        System.out.println("File converted");
    }
}
