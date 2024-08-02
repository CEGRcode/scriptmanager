package scriptmanager.window_interface.Peak_Analysis;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import scriptmanager.cli.Peak_Analysis.FilterBEDbyProximityCLI;
import scriptmanager.objects.CustomOutputStream;
import scriptmanager.objects.LogItem;
import scriptmanager.scripts.Peak_Analysis.FilterBEDbyProximity;
import scriptmanager.util.ExtensionFileFilter;

/**
 * Output wrapper for running
 * {@link scriptmanager.scripts.Peak_Analysis.FilterBEDbyProximity} and
 * reporting progress
 * 
 * @author William KM Lai
 * @see scriptmanager.scripts.Peak_Analysis.FilterBEDbyProximity
 * @see scriptmanager.window_interface.Peak_Analysis.BEDPeakAligntoRefWindow
 */
@SuppressWarnings({"serial"})
public class FilterBEDbyProximityOutput extends JFrame{
	
	private int CUTOFF;
	private File INPUT;
	private File OUT_DIR = null;
	private boolean OUTPUT_GZIP;
	
	private JTextArea textArea;

	public FilterBEDbyProximityOutput(File input, File odir, int cutoff, boolean gzOutput) throws IOException {
		setTitle("BED File Filter Progress");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		INPUT = input;
		CUTOFF = cutoff;
		OUT_DIR = odir;
		OUTPUT_GZIP = gzOutput;
	}

	/**
	 * Runs the FilterBEDbyProximity script
	 * 
	 * @throws IOException Invalid file or parameters
	 * @throws InterruptedException Thrown when more than one script is run at the same time
	 */
	public void run() throws IOException, InterruptedException {
		// Construct output basename
		String NAME = ExtensionFileFilter.stripExtensionIgnoreGZ(INPUT) + "_" + Integer.toString(CUTOFF) + "bp";
		File OUT_BASENAME = new File(NAME);
		if (OUT_DIR != null) {
			OUT_BASENAME = new File(OUT_DIR.getCanonicalPath() + File.separator + NAME);
		}
		// Initialize LogItem
		String command = FilterBEDbyProximityCLI.getCLIcommand(INPUT, OUT_BASENAME, CUTOFF, OUTPUT_GZIP);
		LogItem li = new LogItem(command);
		firePropertyChange("log", null, li);
		// Set-up display stream
		PrintStream PS = new PrintStream(new CustomOutputStream(textArea));
		// Execute script
		FilterBEDbyProximity script_obj = new FilterBEDbyProximity(INPUT, OUT_BASENAME, CUTOFF, PS, OUTPUT_GZIP);
		script_obj.run();
		// Update log item
		li.setStopTime(new Timestamp(new Date().getTime()));
		li.setStatus(0);
		firePropertyChange("log", li, null);
		// wait before disposing
		Thread.sleep(1000);
		dispose();
	}
}