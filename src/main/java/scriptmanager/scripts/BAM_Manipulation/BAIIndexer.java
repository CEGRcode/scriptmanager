package scriptmanager.scripts.BAM_Manipulation;

import htsjdk.samtools.*;
import picard.sam.BuildBamIndex;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * @author Erik Pavloski
 * The following class is designed to index a BAM file and output said
 * index to a file of the same name with a .bai tag
 */

@SuppressWarnings("serial")
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
			final ArrayList<String> args = new ArrayList<>();
			args.add("INPUT=" + input.getAbsolutePath());
			args.add("OUTPUT=" + retVal.getAbsolutePath());
			buildBamIndex.instanceMain(args.toArray(new String[args.size()]));
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
