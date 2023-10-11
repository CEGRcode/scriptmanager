package scriptmanager.scripts.BAM_Manipulation;

import picard.sam.markduplicates.MarkDuplicates;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Picard wrapper for MarkDuplicates
 * 
 * @author Erik Pavloski
 * @see scriptmanager.window_interface.BAM_Manipulation.BAMMarkDupWindow
 */
@SuppressWarnings("serial")
public class BAMMarkDuplicates extends JFrame {
	File bamFile = null;
	boolean removeDuplicates = true;
	File output = null;
	File metrics = null;
	
	/**
	 * Creates a new instance of BAMarkDuplicates with a single file
	 * @param in BAM file to be marked
	 * @param remove Removes duplicates instead of marking them if true
	 * @param out Output BAM file
	 * @param met .metrics file for outputting stats
	 */
	public BAMMarkDuplicates(File in, boolean remove, File out, File met) {
		bamFile = in;
		removeDuplicates = remove;
		output = out;
		metrics = met;
	}
	
	/**
	 * Runs MarkDuplicates picard tool
	 * @throws IOException Invalid file or parameters If BAM file doesn't have corresponding .BAI Index file
	 */
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