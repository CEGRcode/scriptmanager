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
 * Output wrapper for running
 * {@link scriptmanager.scripts.Peak_Analysis.BEDPeakAligntoRef} and reporting
 * progress
 * 
 * @author William KM Lai
 * @see scriptmanager.scripts.Peak_Analysis.BEDPeakAligntoRef
 * @see scriptmanager.window_interface.Peak_Analysis.BEDPeakAligntoRefWindow
 */
@SuppressWarnings("serial")
public class BEDPeakAligntoRefOutput extends JFrame{
	private File PEAK = null;
	private File REF = null;
	private File OUTFILE = null;
	private boolean OUTPUT_GZIP = false;

	private JTextArea textArea;

	public BEDPeakAligntoRefOutput(File ref, File peak, File outpath, boolean gzOutput) throws IOException {
	/**
	 * Creates a new BEDPeakAligntoRefOutput with two BED files and an output directory
	 * @param ref Reference BAM file
	 * @param peak BAM file to be alligned
	 * @param outpath Output directory
	 * @throws IOException Invalid file or parameters
	 */
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
		OUTPUT_GZIP = gzOutput;
		if(outpath != null) {
			OUTFILE = new File(outpath.getCanonicalPath() + File.separator + PEAK.getName().split("\\.")[0] + "_" + REF.getName().split("\\.")[0] + "_Output.cdt");
		} else {
			OUTFILE = new File(PEAK.getName().split("\\.")[0] + "_" + REF.getName().split("\\.")[0] + "_Output.cdt");
		}
		if (OUTPUT_GZIP){
			OUTFILE = new File(OUTFILE.getAbsolutePath() + ".gz");
		}
	}
		
	/**
	 * Runs the BEDPeakAligntoRef script
	 * @throws IOException Invalid file or parameters
	 * @throws InterruptedException Thrown when more than one script is run at the same time
	 */
	public void run() throws IOException, InterruptedException {
		
		PrintStream PS = new PrintStream(new CustomOutputStream(textArea));
		BEDPeakAligntoRef script_obj = new BEDPeakAligntoRef(REF, PEAK, OUTFILE, PS, OUTPUT_GZIP);
		script_obj.run();
		
		Thread.sleep(2000);
		dispose();
	}
}