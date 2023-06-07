package scriptmanager.scripts.BAM_Manipulation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Erik Pavloski
 * This class merges BAM and/or SAM Files into a singular file
 */

public class MergeSamFiles {
	// Private constructor to prevent instantiation of the class
	private MergeSamFiles() {
	}

	public static void run(List<File> inputs, File output, boolean useMultipleCpus) throws IOException {
		// Tells the user their files are being merged
		System.out.println("Merging BAM Files...");
		// Merges the files
		try {
			final picard.sam.MergeSamFiles merger = new picard.sam.MergeSamFiles();
			final ArrayList<String> args = new ArrayList<>();
			for (File input : inputs) {
				args.add("INPUT=" + input.getAbsolutePath());
			}
			args.add("OUTPUT=" + output.getAbsolutePath());
			if (useMultipleCpus) {
				args.add("USE_THREADING=true");
			}
			merger.instanceMain(args.toArray(new String[args.size()]));
			System.out.println("BAM Files Merged");
		} catch (htsjdk.samtools.SAMException exception) {
			System.out.println(exception.getMessage());
		}
	}
}