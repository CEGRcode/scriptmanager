package scriptmanager.scripts.BAM_Format_Converter;
import htsjdk.samtools.SAMException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Erik Pavloski
 * @see scriptmanager.window_interface.BAM_Format_Converter.SamFormatConverterWindow
 * This code runs the Picard tool SamFormatConverter
 * It can swap a SAM file to a BAM file and vice versa
 */

public class SamFormatConverterWrapper {
    public static void run(File input, File output) throws IOException, SAMException {
        /**
         * @param input the BAM/SAM file to be converted
         * @param output the output BAM/SAM file
         *
         * @throws IOException
         * @throws SAMException
         */
        System.out.println("Converting file...");
        // Converts the SAM/BAM file to fastq
        final picard.sam.SamFormatConverter samFormatConverter = new picard.sam.SamFormatConverter();
        final ArrayList<String> args = new ArrayList<>();
        args.add("INPUT=" + input.getAbsolutePath());
        args.add("OUTPUT=" + output.getAbsolutePath());
        samFormatConverter.instanceMain(args.toArray(new String[args.size()]));
        System.out.println("File converted");
    }
}
