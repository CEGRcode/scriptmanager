package scriptmanager.scripts.BAM_Manipulation;

import htsjdk.samtools.SAMException;
import scriptmanager.window_interface.BAM_Manipulation.DownsampleSamFileWindow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
/**
 * @author Erik Pavloski
 * @see DownsampleSamFileWindow
 * This code runs the Picard tool DownsampleSam
 *
 */
public class DownsampleSamFile {
    /**
     * The following code uses Picard's downsampleSAM tool to shink the BAM/SAM file
     * and retain a random subset of the reads based on the probability parameter
     * Output reads = (probability) * (input reads)
     *
     * @param input - the BAM/SAM file to be down-sampled
     * @param probability - the probability of keeping reads.
     *                    0.5 default -> 50% reduction in data.
     *                    Smaller number -> less data once down-sampled
     * @throws IOException
     * @throws SAMException
     */
    public static void run(File input, double probability) throws IOException, SAMException {
        File retVal;
        System.out.println("Down-sampling SAM/BAM file...");

        String output = input.getAbsolutePath() + ".downsampled.bam";
        retVal = new File(output);

        // Downsamples the SAM/BAM file
        final picard.sam.DownsampleSam downsampleSam = new picard.sam.DownsampleSam();
        final ArrayList<String> args = new ArrayList<>();
        args.add("INPUT=" + input.getAbsolutePath());
        args.add("OUTPUT=" + retVal.getAbsolutePath());
        args.add("PROBABILITY=" + probability);
        downsampleSam.instanceMain(args.toArray(new String[args.size()]));

        System.out.println("SAM/BAM file down-sampled");
    }
}
