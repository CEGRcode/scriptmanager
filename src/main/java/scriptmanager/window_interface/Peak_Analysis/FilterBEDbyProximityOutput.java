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

@SuppressWarnings({"serial"})
public class FilterBEDbyProximityOutput extends JFrame{
	
	private int CUTOFF;
	private File INPUT;
	private File OUT_DIR = null;
	
	private JTextArea textArea;
	
	public FilterBEDbyProximityOutput(File input, File output, int cutoff) {
		setTitle("BED File Filter Progress");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		CUTOFF = cutoff;
		INPUT = input;
		OUT_DIR = output;
	}
	
	public void run() throws IOException, InterruptedException {
		// Construct output basename
		String NAME = ExtensionFileFilter.stripExtensionIgnoreGZ(INPUT) + "_" + Integer.toString(CUTOFF) + "bp";
		File OUT_BASENAME = new File(NAME);
		if (OUT_DIR != null) {
			OUT_BASENAME = new File(OUT_DIR.getCanonicalPath() + File.separator + NAME);
		}
		// Initialize LogItem
		String command = FilterBEDbyProximityCLI.getCLIcommand(INPUT, OUT_BASENAME, CUTOFF);
		LogItem li = new LogItem(command);
		firePropertyChange("log", null, li);
		// Set-up display stream
		PrintStream PS = new PrintStream(new CustomOutputStream(textArea));
		// Execute script
		FilterBEDbyProximity script_obj = new FilterBEDbyProximity(INPUT, OUT_BASENAME, CUTOFF, PS);
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