package scriptmanager.scripts.BAM_Manipulation;
import htsjdk.samtools.SAMException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Erik Pavloski
 * @see scriptmanager.window_interface.BAM_Manipulation.ValidateSamWindow
 * This code runs the picard tool validateSAMFile
 *
 */


public class ValidateSamWrapper {
    public static void run(File input, File output, boolean mode, File referenceGenome, int maxOutput) throws IOException, SAMException{
        /**
         *
         * @param input The BAM/SAM file to be Validated
         * @param output the output of the validation
         * @param mode Allows the user to select verbose or summary mode. True = verbose - false = summary
         * @param referenceGenome Allows the user to add reference sequence if needed
         * @param maxOutput Allows customization of the maximum number of outputs in verbose mode
         *
         * @throws IOException
         * @throws SAMException
          *
         */
        System.out.println("Validating SAM/BAM file...");
            // Validates the SAM/BAM file
            final picard.sam.ValidateSamFile validateSam = new picard.sam.ValidateSamFile();
            final ArrayList<String> args = new ArrayList<>();
            args.add("INPUT=" + input.getAbsolutePath());
            args.add("OUTPUT=" + output.getAbsolutePath());
            String modeString = mode ? "VERBOSE" : "SUMMARY";
            args.add("MODE=" + modeString);
            args.add("MAX_OUTPUT=" + maxOutput);
            if (referenceGenome != null) {
                args.add("REFERENCE_SEQUENCE=" + referenceGenome.getAbsolutePath());
            }
            validateSam.instanceMain(args.toArray(new String[args.size()]));
            System.out.println("SAM/BAM file validated");
    }
}
