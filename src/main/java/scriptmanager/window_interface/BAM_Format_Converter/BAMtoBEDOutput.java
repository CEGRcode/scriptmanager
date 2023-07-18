package scriptmanager.window_interface.BAM_Format_Converter;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import scriptmanager.objects.CustomOutputStream;
import scriptmanager.scripts.BAM_Format_Converter.BAMtoBED;

/**
 * Output window wrapper for executing the BAMtoBED script given arguments provided by BAMtoBEDWindow and displaying output
 * @see scriptmanager.scripts.BAM_Format_Converter.BAMtoBED
 * @see scriptmanager.window_interface.BAM_Format_Converter.BAMtoBEDWindow
 */
@SuppressWarnings("serial")
public class BAMtoBEDOutput extends JFrame {
	private File BAM = null;
	private File OUT_DIR = null;
	private int STRAND = 0;
	private String READ = "READ1";

	private static int PAIR = 1;
	private static int MIN_INSERT = -9999;
	private static int MAX_INSERT = -9999;

	private JTextArea textArea;

	/**
	 * Creates a new BAMtoBEDOutput window
	 * @param b BAM file
	 * @param out_dir Directory to output BED file to
	 * @param s Specifies which reads to output
	 * @param pair_status
	 * @param min_size
	 * @param max_size
	 */
	public BAMtoBEDOutput(File b, File out_dir, int s, int pair_status, int min_size, int max_size) {
		setTitle("BAM to BED Progress");
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
		} else if (STRAND == 4) {
			READ = "FRAGMENT";
		}
	}

	/**
	 * Runs the BAMtoBED script with the file passed in through the constructor
	 * @throws IOException Invalid file or parameters
	 * @throws InterruptedException
	 */
	public void run() throws IOException, InterruptedException {
		// Open Output File
		String OUTPUT = BAM.getName().split("\\.")[0] + "_" + READ + ".bed";
		if (OUT_DIR != null) {
			OUTPUT = OUT_DIR.getCanonicalPath() + File.separator + OUTPUT;
		}

		// Call script here, pass in ps and OUT
		PrintStream PS = new PrintStream(new CustomOutputStream(textArea));
		PS.println(OUTPUT);
		BAMtoBED script_obj = new BAMtoBED(BAM, new File(OUTPUT), STRAND, PAIR, MIN_INSERT, MAX_INSERT, PS);
		script_obj.run();

		Thread.sleep(2000);
		dispose();
	}
}