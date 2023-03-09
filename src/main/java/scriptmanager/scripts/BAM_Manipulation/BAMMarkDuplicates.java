package scripts.BAM_Manipulation;

import picard.sam.markduplicates.MarkDuplicates;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class BAMMarkDuplicates extends JFrame {
	File bamFile = null;
	boolean removeDuplicates = true;
	File output = null;
	File metrics = null;
	
	public BAMMarkDuplicates(File in, boolean remove, File out, File met) {
		bamFile = in;
		removeDuplicates = remove;
		output = out;
		metrics = met;
	}
	
	public void run() throws IOException {
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
			JOptionPane.showMessageDialog(null, "BAI Index File does not exist for: " + bamFile.getName() + "\n");
		}
	}
}