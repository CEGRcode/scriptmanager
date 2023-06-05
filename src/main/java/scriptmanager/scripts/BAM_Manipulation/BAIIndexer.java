package scriptmanager.scripts.BAM_Manipulation;

import htsjdk.samtools.*;
import picard.sam.BuildBamIndex;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import javax.swing.JOptionPane;

/**
 * @author Erik Pavloski
 * @version v0.14
 * The following class is designed to index a BAM file and output said index to a file of the same name with a .bai tag
 * The commented out code is the legacy code left there for my personal reference
 * It can be deleted once my code is verified
 */

/*
public class BAIIndexer {
	public static File generateIndex(File input) throws IOException {
		SamReaderFactory factory = SamReaderFactory.makeDefault().enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS, SamReaderFactory.Option.CACHE_FILE_BASED_INDEXES, SamReaderFactory.Option.VALIDATE_CRC_CHECKSUMS).validationStringency(ValidationStringency.LENIENT);
		File retVal = null;
		System.out.println("Generating New Index File...");
		try{
			String output = input.getCanonicalPath() + ".bai";
			retVal = new File(output);
			//Generate index
			SamReader inputSam = factory.open(input);
			BAMIndexer bamindex = new BAMIndexer(retVal, inputSam.getFileHeader());
			int counter = 0;
			for(SAMRecord record : inputSam) {
				if(counter % 1000000 == 0) {
					System.out.print("Tags processed: " + NumberFormat.getIntegerInstance().format(counter) + "\r");
					System.out.flush();
				}
				counter++;
				bamindex.processAlignment(record);
			}
			bamindex.finish();
			inputSam.close();
			System.out.println("\nIndex File Generated");
			return retVal;
		}
		catch(htsjdk.samtools.SAMException exception){
			System.out.println(exception.getMessage());
			JOptionPane.showMessageDialog(null, exception.getMessage());
			retVal = null;
		}
		return retVal;
	}
}
*/

public class BAIIndexer {
	public static File generateIndex(File input) throws IOException {
		File retVal = null;

		// Tells user that their file is being generated
		System.out.println("Generating Index File...");
		try {
			String output = input.getCanonicalPath() + ".bai";
			retVal = new File(output);

			// Generates the index
			final BuildBamIndex buildBamIndex = new BuildBamIndex();
			buildBamIndex.INPUT = input.toString();
			buildBamIndex.OUTPUT = retVal;
			buildBamIndex.instanceMain(new String[]{});
			System.out.println("Index File Generated");
			return retVal;
		} catch (htsjdk.samtools.SAMException exception) {
			System.out.println(exception.getMessage());
			retVal = null;
		}
		// Returns retVal
		return retVal;
	}
}
