package scriptmanager.window_interface.Peak_Analysis;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import scriptmanager.objects.CustomOutputStream;
import scriptmanager.scripts.Peak_Analysis.BEDPeakAligntoRef;

/**
 * Output wrapper for running BEDPeakAligntoRef script and reporting when the process is completed
 * @see scriptmanager.scripts.Peak_Analysis.BEDPeakAligntoRef
 */
@SuppressWarnings("serial")
public class BEDPeakAligntoRefOutput extends JFrame{
	private File PEAK = null;
	private File REF = null;
	private File OUTFILE = null;

	private JTextArea textArea;
		
	/**
	 * Creates a new BEDPeakAligntoRefOutput with two BED files and an output directory
	 * @param ref Reference BAM file
	 * @param peak BAM file to be alligned
	 * @param outpath Output directory
	 * @throws IOException Invalid file or parameters
	 */
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
		
	/**
	 * Runs the BEDPeakAligntoRef script
	 * @throws IOException Invalid file or parameters
	 * @throws InterruptedException Thrown when more than one script is run at the same time
	 */
	public void run() throws IOException, InterruptedException {
		
		PrintStream PS = new PrintStream(new CustomOutputStream(textArea));
		BEDPeakAligntoRef script_obj = new BEDPeakAligntoRef(REF, PEAK, OUTFILE, PS);
		script_obj.run();
		
		Thread.sleep(2000);
		dispose();
	}
}