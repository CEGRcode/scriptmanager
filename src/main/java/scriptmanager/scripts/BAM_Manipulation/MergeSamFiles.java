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
	// These are the 3 fields for the class
	private List<File> inputs;
	private File output;
	private boolean useMultipleCpus;
	// Constructor
	public MergeSamFiles(List<File> inputs, File output, boolean useMultipleCpus) {
		this.inputs = inputs;
		this.output = output;
		this.useMultipleCpus = useMultipleCpus;
	}
	// This is the main method of the class. Does the sorting
	public void run() throws IOException {
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