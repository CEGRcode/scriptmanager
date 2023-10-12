package scriptmanager.window_interface.BAM_Format_Converter;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import scriptmanager.objects.CustomOutputStream;
import scriptmanager.scripts.BAM_Format_Converter.BAMtobedGraph;

/**
 * Output wrapper for running
 * {@link scriptmanager.scripts.BAM_Format_Converter.BAMtobedGraph} and
 * reporting progress
 * 
 * @author William KM Lai
 * @see scriptmanager.scripts.BAM_Format_Converter.BAMtobedGraph
 * @see scriptmanager.window_interface.BAM_Format_Converter.BAMtobedGraphWindow
 */
@SuppressWarnings("serial")
public class BAMtobedGraphOutput extends JFrame {
	private File BAM = null;
	private File OUT_DIR = null;
	private int STRAND = 0;
	private String READ = "READ1";

	private static int PAIR = 1;
	private static int MIN_INSERT = -9999;
	private static int MAX_INSERT = -9999;

	private JTextArea textArea;

	/**
	 * Creates a new instance of a BAMtobedGraph script with a single BAM file
	 * @param b BAM file
	 * @param out_dir Output directory
	 * @param s Specifies which reads to output
	 * @param pair_status Specifies if proper pairs are required (0 = not required, !0 = required)
	 * @param min_size Minimum acceptable insert size
	 * @param max_size Maximum acceptable insert size
	 */
	public BAMtobedGraphOutput(File b, File out_dir, int s, int pair_status, int min_size, int max_size) {
		setTitle("BAM to bedGraph Progress");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		BAM = b;
		OUT_DIR = out_dir;
		STRAND = s;
		PAIR = pair_status;
		MIN_INSERT = min_size;
		MAX_INSERT = max_size;
		if (STRAND == 0) {
			READ = "READ1";
		} else if (STRAND == 1) {
			READ = "READ2";
		} else if (STRAND == 2) {
			READ = "COMBINED";
		} else if (STRAND == 3) {
			READ = "MIDPOINT";
		}
	}

	/**
	 * Runs the BAMtoBED script with the file passed in through the constructor
	 * @throws IOException Invalid file or parameters
	 * @throws InterruptedException Thrown when more than one script is run at the same time
	 */
	public void run() throws IOException, InterruptedException {
		// Open Output File
		String OUTBASENAME = BAM.getName().split("\\.")[0] + "_" + READ;
		if (OUT_DIR != null) {
			OUTBASENAME = OUT_DIR.getCanonicalPath() + File.separator + OUTBASENAME;
		}

		// Call script here, pass in ps and OUT
		PrintStream PS = new PrintStream(new CustomOutputStream(textArea));
		PS.println(OUTBASENAME);
		BAMtobedGraph script_obj = new BAMtobedGraph(BAM, OUTBASENAME, STRAND, PAIR, MIN_INSERT, MAX_INSERT, PS);
		script_obj.run();

		Thread.sleep(2000);
		dispose();
	}
}