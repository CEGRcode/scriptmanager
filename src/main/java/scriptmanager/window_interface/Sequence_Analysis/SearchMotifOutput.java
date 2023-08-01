package scriptmanager.window_interface.Sequence_Analysis;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import scriptmanager.cli.Figure_Generation.TwoColorHeatMapCLI;
import scriptmanager.cli.Sequence_Analysis.SearchMotifCLI;
import scriptmanager.objects.CustomOutputStream;
import scriptmanager.objects.LogItem;
import scriptmanager.scripts.Sequence_Analysis.SearchMotif;
import scriptmanager.util.ExtensionFileFilter;

/**
 * Graphical window for displaying progress as genome sequences are searched for
 * a given motif.
 * 
 * @author William KM Lai
 * @see scriptmanager.scripts.Sequence_Analysis.SearchMotif
 * @see scriptmanager.window_interface.Sequence_Analysis.SearchMotifWindow
 */
@SuppressWarnings("serial")
public class SearchMotifOutput extends JFrame {

	private int ALLOWED_MISMATCH;
	private String motif;
	private File INPUTFILE = null;
	private File OUT_DIR;
	private boolean gzOutput = false;

	private JTextArea textArea;

	/**
	 * Initialize a scrollable JTextArea window to display progress and save inputs
	 * for calling the script.
	 * 
	 * @param input
	 * @param mot
	 * @param num
	 * @param out_dir
	 * @param gz If this is true, the output file will be gzipped.
	 * @throws IOException
	 */
	public SearchMotifOutput(File input, String mot, int num, File out_dir, boolean gz) throws IOException {
		setTitle("Motif Search Progress");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		ALLOWED_MISMATCH = num;
		motif = mot;
		INPUTFILE = input;
		OUT_DIR = out_dir;
		gzOutput = gz;
	}

	/**
	 * Call script to search for motif instances and display progress by
	 * instantiating a scrollable JTextArea window that prints each
	 * sequence/chromosome name within the FASTA file and dispose the window after
	 * the script finishes.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void run() throws IOException, InterruptedException {
		LogItem old_li = null;
		PrintStream PS = new PrintStream(new CustomOutputStream(textArea));
		String BASENAME = motif + "_" + Integer.toString(ALLOWED_MISMATCH) + "Mismatch_"
				+ ExtensionFileFilter.stripExtension(INPUTFILE) + ".bed";
		if (OUT_DIR != null) {
			BASENAME = OUT_DIR.getCanonicalPath() + File.separator + BASENAME;
		}
		BASENAME += gzOutput ? ".gz" : "";
		old_li = new LogItem("");
		// Initialize LogItem
		String command = SearchMotifCLI.getCLIcommand(INPUTFILE, new File(BASENAME), motif, ALLOWED_MISMATCH, gzOutput);
		LogItem new_li = new LogItem(command);
		firePropertyChange("log", old_li, new_li);
		SearchMotif script_obj = new SearchMotif(INPUTFILE, motif, ALLOWED_MISMATCH, new File(BASENAME), PS, gzOutput);
		script_obj.run();
		// Update log item
		new_li.setStopTime(new Timestamp(new Date().getTime()));
		new_li.setStatus(0);
		old_li = new_li;
		Thread.sleep(2000);
		firePropertyChange("log", old_li, null);
		dispose();
	}
}