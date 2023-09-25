package scriptmanager.window_interface.Peak_Analysis;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import scriptmanager.objects.CustomOutputStream;
import scriptmanager.scripts.Peak_Analysis.SortByRef;
import scriptmanager.util.ExtensionFileFilter;
import scriptmanager.cli.Peak_Analysis.SortByRefCLI;
import scriptmanager.objects.LogItem;

@SuppressWarnings("serial")
public class SortByRefOutput extends JFrame{
	private File PEAK = null;
	private File REF = null;
	private File OUTFILE = null;
	private Boolean PROPER_STRAND = false;
	private Boolean GZIP_OUTPUT = false;
	private Boolean GFF = false;
	private String UPSTREAM_BOUND = "";
	private String DOWNSTREAM_BOUND = "";
	
	private JTextArea textArea;
		
	public SortByRefOutput(File ref, File peak, File outpath, boolean properStrands, boolean gzOutput, boolean gff, String upstream, String downstream) throws IOException {
		setTitle("Align to Reference Progress");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		REF = ref;
		PEAK = peak;
		GFF = gff;
		PROPER_STRAND = properStrands;
		GZIP_OUTPUT = gzOutput;
		UPSTREAM_BOUND = upstream;
		DOWNSTREAM_BOUND = downstream;
		
		if(outpath != null) {
			OUTFILE = new File(outpath.getCanonicalPath() + File.separator + PEAK.getName().split("\\.")[0] + "_" + 
			REF.getName().split("\\.")[0] + "_Output." + ExtensionFileFilter.getExtensionIgnoreGZ(REF));
		} else {
			OUTFILE = new File(PEAK.getName().split("\\.")[0] + "_" + REF.getName().split("\\.")[0] + 
			"_Output." + ExtensionFileFilter.getExtensionIgnoreGZ(REF));
		}
	}
		
	public void run() throws IOException, InterruptedException {
		String command = SortByRefCLI.getCLIcommand(REF, PEAK, OUTFILE, GFF, GZIP_OUTPUT, 
		PROPER_STRAND, UPSTREAM_BOUND, DOWNSTREAM_BOUND);
		LogItem li = new LogItem(command);
		firePropertyChange("log", null, li);
		PrintStream PS = new PrintStream(new CustomOutputStream(textArea));
		SortByRef script_obj = new SortByRef(REF, PEAK, OUTFILE, PROPER_STRAND, GZIP_OUTPUT, PS, UPSTREAM_BOUND, DOWNSTREAM_BOUND);
		if (GFF) {	
			script_obj.sortGFF(); 
		} else {
			script_obj.sortBED(); 
		}

		li.setStatus(0);
		firePropertyChange("log", li, null);
		Thread.sleep(2000);
		dispose();
	}

	/**
	 * Invoked when task's progress property changes.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if ("log" == evt.getPropertyName()){
				firePropertyChange("log", evt.getOldValue(), evt.getNewValue());
		}
	}
}