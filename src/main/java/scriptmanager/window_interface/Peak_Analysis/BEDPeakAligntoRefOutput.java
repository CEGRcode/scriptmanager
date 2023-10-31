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

import scriptmanager.cli.Peak_Analysis.BEDPeakAligntoRefCLI;
import scriptmanager.objects.CustomOutputStream;
import scriptmanager.objects.LogItem;
import scriptmanager.scripts.Peak_Analysis.BEDPeakAligntoRef;

@SuppressWarnings("serial")
public class BEDPeakAligntoRefOutput extends JFrame{
	private File PEAK = null;
	private File REF = null;
	private File OUTFILE = null;

	private JTextArea textArea;
		
	public BEDPeakAligntoRefOutput(File ref, File peak, File outpath) throws IOException {
		setTitle("BED Align to Reference Progress");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		REF = ref;
		PEAK = peak;
		
		if(outpath != null) {
			OUTFILE = new File(outpath.getCanonicalPath() + File.separator + PEAK.getName().split("\\.")[0] + "_" + REF.getName().split("\\.")[0] + "_Output.cdt");
		} else {
			OUTFILE = new File(PEAK.getName().split("\\.")[0] + "_" + REF.getName().split("\\.")[0] + "_Output.cdt");
		}
	}
		
	public void run() throws IOException, InterruptedException {
		// Initialize LogItem
		String command = BEDPeakAligntoRefCLI.getCLIcommand(REF, PEAK, OUTFILE);
		LogItem li = new LogItem(command);
		firePropertyChange("log", null, li);
		// Set-up display stream
		PrintStream PS = new PrintStream(new CustomOutputStream(textArea));
		// Execute script
		BEDPeakAligntoRef script_obj = new BEDPeakAligntoRef(REF, PEAK, OUTFILE, PS);
		script_obj.run();
		// Update log item
		li.setStopTime(new Timestamp(new Date().getTime()));
		li.setStatus(0);
		firePropertyChange("log", li, null);
		// wait before disposing
		Thread.sleep(2000);
		dispose();
	}
}