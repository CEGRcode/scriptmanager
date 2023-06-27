package scriptmanager.scripts.BAM_Manipulation;

import htsjdk.samtools.SAMException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
/**
 * @author Erik Pavloski
 * @see scriptmanager.window_interface.BAM_Manipulation.DownsampleSamWindow
 * This code runs the Picard tool DownsampleSam
 *
 *
 */
public class DownsampleSamWrapper {
    /**
     * The following code uses Picard's downsampleSAM tool to shink the BAM/SAM file
     * and retain a random subset of the reads based on the probability parameter
     * Output reads = (probability) * (input reads)
     *
     * @param input the BAM/SAM file to be downsampled
     * @param output the downsampled file
     * @param probability the probability of keeping reads.
     *                    0.5 default -> 50% reduction in data.
     *                    Smaller number -> less data once down-sampled
     * @param seed the custom seed that the user entered.
     *             Defaults to null if no custom seed is entered which just spools a random seed
     * @throws IOException
     * @throws SAMException
     */
    public static void run(File input, File output, double probability, Long seed) throws IOException, SAMException {
        System.out.println("Downsampling SAM/BAM file...");

        // Downsamples the SAM/BAM file
        final picard.sam.DownsampleSam downsampleSam = new picard.sam.DownsampleSam();
        final ArrayList<String> args = new ArrayList<>();
        args.add("INPUT=" + input.getAbsolutePath());
        args.add("OUTPUT=" + output.getAbsolutePath());
        args.add("PROBABILITY=" + probability);
        args.add("RANDOM_SEED=" + seed);
        downsampleSam.instanceMain(args.toArray(new String[args.size()]));
        System.out.println("SAM/BAM file downsampled");
    }
}
