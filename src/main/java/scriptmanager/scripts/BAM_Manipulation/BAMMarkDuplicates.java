package scriptmanager.scripts.BAM_Manipulation;

import picard.sam.markduplicates.MarkDuplicates;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import htsjdk.samtools.SAMException;

public class BAMMarkDuplicates {
		
	public static void mark(File bamFile, boolean removeDuplicates, File output, File metrics) throws IOException {
		//Check if BAI index file exists
		File f = new File(bamFile + ".bai");
		if(f.exists() && !f.isDirectory()) {
            final MarkDuplicates markDuplicates = new MarkDuplicates();
            final ArrayList<String> args = new ArrayList<>();
            args.add("INPUT=" + bamFile.getAbsolutePath());
            args.add("OUTPUT=" + output.getAbsolutePath());
            args.add("METRICS_FILE=" + metrics.getAbsolutePath());
            if(removeDuplicates) { args.add("REMOVE_DUPLICATES=true"); }
            else { args.add("REMOVE_DUPLICATES=false"); }
            markDuplicates.instanceMain(args.toArray(new String[args.size()]));
		} else {
			throw new SAMException("BAI Index File does not exist for: " + bamFile.getName() + "\n");
		}
	}
}