package scriptmanager.scripts.BAM_Manipulation;

import htsjdk.samtools.BAMIndexer;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import javax.swing.JOptionPane;

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
