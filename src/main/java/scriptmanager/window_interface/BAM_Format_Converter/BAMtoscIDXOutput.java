package scriptmanager.window_interface.BAM_Format_Converter;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import scriptmanager.objects.CustomOutputStream;
import scriptmanager.scripts.BAM_Format_Converter.BAMtoscIDX;

@SuppressWarnings("serial")
public class BAMtoscIDXOutput extends JFrame {
	private File BAM = null;
	private File OUT_DIR = null;
	private static boolean OUTPUT_GZIP = false;
	private int STRAND = 0;
	private String READ = "READ1";

	private static int PAIR = 1;
	private static int MIN_INSERT = -9999;
	private static int MAX_INSERT = -9999;

	private JTextArea textArea;

	public BAMtoscIDXOutput(File b, File out_dir, int s, int pair_status, int min_size, int max_size, boolean gzOutput) {
		setTitle("BAM to scIDX Progress");
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
		OUTPUT_GZIP = gzOutput;
	}

	public void run() throws IOException, InterruptedException {
		// Open Output File
		String OUTPUT = BAM.getName().split("\\.")[0] + "_" + READ + ".tab";
		if (OUT_DIR != null) {
			OUTPUT = OUT_DIR.getCanonicalPath() + File.separator + OUTPUT;
		}

		// Call script here, pass in ps and OUT
		PrintStream PS = new PrintStream(new CustomOutputStream(textArea));
		PS.println(OUTPUT);
		BAMtoscIDX script_obj = new BAMtoscIDX(BAM, new File(OUTPUT), STRAND, PAIR, MIN_INSERT, MAX_INSERT, PS, OUTPUT_GZIP);
		script_obj.run();

		Thread.sleep(2000);
		dispose();
	}
}