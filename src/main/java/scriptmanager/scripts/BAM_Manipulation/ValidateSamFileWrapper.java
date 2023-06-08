package scriptmanager.scripts.BAM_Manipulation;
import htsjdk.samtools.SAMException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Erik Pavloski
 * @see scriptmanager.window_interface.BAM_Manipulation.ValidateSamWindow - class still missing code
 *
 */


public class ValidateSamFileWrapper {
    public static void run(File input) throws IOException, SAMException{
        /**
         * This method runs the picard tool validateSAMFile
         * @param input - The BAM/SAm file to be Validated
         * @throws IOException
         * @throws SAMException
         */
        System.out.println("Validating SAM/BAM file...");
            // Validates the SAM/BAM file
            final picard.sam.ValidateSamFile validateSam = new picard.sam.ValidateSamFile();
            final ArrayList<String> args = new ArrayList<>();
            args.add("INPUT=" + input.getAbsolutePath());
            validateSam.instanceMain(args.toArray(new String[args.size()]));
            System.out.println("SAM/BAM file validated");
    }
}
