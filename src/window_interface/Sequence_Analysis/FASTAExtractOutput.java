package window_interface.Sequence_Analysis;

import htsjdk.samtools.SAMException;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import objects.CustomOutputStream;
import objects.CustomExceptions.FASTAException;
import scripts.Sequence_Analysis.FASTAExtract;

@SuppressWarnings("serial")
public class FASTAExtractOutput extends JFrame {
	private File GENOME = null;
	private File OUT_DIR = null;
	private ArrayList<File> BED = null;
	private boolean STRAND = true;
	private boolean HEADER = true;

	private JTextArea textArea;

	public FASTAExtractOutput(File gen, ArrayList<File> b, File out_dir, boolean str, boolean head) {
		setTitle("FASTA Extraction Progress");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		GENOME = gen;
		BED = b;
		OUT_DIR = out_dir;
		STRAND = str;
		HEADER = head;
	}

	public void run() throws IOException, InterruptedException, FASTAException {

		PrintStream PS = new PrintStream(new CustomOutputStream(textArea));

		try {
			for (int x = 0; x < BED.size(); x++) {

				// Open Output File
				File OUTFILE;
				String NAME = BED.get(x).getName().split("\\.")[0] + ".fa";
				if (OUT_DIR != null) {
					OUTFILE = new File(OUT_DIR.getCanonicalPath() + File.separator + NAME);
				} else {
					OUTFILE = new File(NAME);
				}
				PS.println("Proccessing File: " + BED.get(x).getName());

				// Execute Script object
				FASTAExtract script_obj = new FASTAExtract(GENOME, BED.get(x), OUTFILE, STRAND, HEADER, PS);
				script_obj.run();

				firePropertyChange("fa", x, x + 1);
			}
			PS.println("Extraction Complete");
		} catch (IllegalArgumentException e) {
			PS.println(e.getMessage());
		} catch (FileNotFoundException e) {
			PS.println(e.getMessage());
		} catch (SAMException e) {
			PS.println(e.getMessage());
		}
	}
}