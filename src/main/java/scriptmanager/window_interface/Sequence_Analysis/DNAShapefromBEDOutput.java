package scriptmanager.window_interface.Sequence_Analysis;

import htsjdk.samtools.SAMException;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import scriptmanager.objects.CustomOutputStream;
import scriptmanager.scripts.Sequence_Analysis.DNAShapefromBED;

/**
 * Output wrapper for running
 * {@link scriptmanager.scripts.Sequence_Analysis.DNAShapefromBED} and reporting
 * composite results
 * 
 * @author William KM Lai
 * @see scriptmanager.scripts.Sequence_Analysis.DNAShapefromBED
 * @see scriptmanager.window_interface.Sequence_Analysis.DNAShapefromBEDWindow
 */
@SuppressWarnings("serial")
public class DNAShapefromBEDOutput extends JFrame {
	private File GENOME = null;
	private File OUT_DIR = null;
	private boolean[] OUTPUT_TYPE = null;
	private ArrayList<File> BED = null;
	private boolean OUTPUT_GZIP;

	private boolean STRAND = true;

	final JLayeredPane layeredPane;
	final JTabbedPane tabbedPane;
	final JTabbedPane tabbedPane_Scatterplot;
	final JTabbedPane tabbedPane_Statistics;

	/**
	 * Initialize a tabbed window with a tab for the charts and a tab for the
	 * statistics.
	 * 
	 * @param gen     the reference genome sequence in FASTA-format
	 * @param b       the BED-formatted coordinate intervals to extract sequence from
	 * @param out_dir the output directory to save output files to
	 * @param type    the information on the shape types to generate
	 * @param str     the force-strandedness to pass to the script
	 * @param gzOutput Whether to output compressed file
	 */
	public DNAShapefromBEDOutput(File gen, ArrayList<File> b, File out_dir, boolean[] type, boolean str, boolean gzOutput) {
		setTitle("DNA Shape Prediction Composite");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 800, 600);

		layeredPane = new JLayeredPane();
		getContentPane().add(layeredPane, BorderLayout.CENTER);
		SpringLayout sl_layeredPane = new SpringLayout();
		layeredPane.setLayout(sl_layeredPane);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		sl_layeredPane.putConstraint(SpringLayout.NORTH, tabbedPane, 6, SpringLayout.NORTH, layeredPane);
		sl_layeredPane.putConstraint(SpringLayout.WEST, tabbedPane, 6, SpringLayout.WEST, layeredPane);
		sl_layeredPane.putConstraint(SpringLayout.SOUTH, tabbedPane, -6, SpringLayout.SOUTH, layeredPane);
		sl_layeredPane.putConstraint(SpringLayout.EAST, tabbedPane, -6, SpringLayout.EAST, layeredPane);
		layeredPane.add(tabbedPane);

		tabbedPane_Scatterplot = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("DNA Shape Plot", null, tabbedPane_Scatterplot, null);

		tabbedPane_Statistics = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("DNA Shape Statistics", null, tabbedPane_Statistics, null);

		GENOME = gen;
		BED = b;
		OUT_DIR = out_dir;
		OUTPUT_TYPE = type;
		STRAND = str;
		OUTPUT_GZIP = gzOutput;
	}

	/**
	 * Call script on each BED file to calculate shape scores and append the values
	 * for each shape type under the "DNA Shape Statistics" tab and append each
	 * chart generated under the "DNA Shape Plot" tab.
	 * 
	 * @throws IOException Invalid file or parameters
	 * @throws InterruptedException Thrown when more than one script is run at the same time
	 */
	public void run() throws IOException, InterruptedException {
		try {
			// Move through each BED File
			for (int x = 0; x < BED.size(); x++) {
				// Initialize TextAreas and PrintStream wrappers
				JTextArea STATS_MGW = null;
				JTextArea STATS_PropT = null;
				JTextArea STATS_HelT = null;
				JTextArea STATS_Roll = null;
				PrintStream[] PS = { null, null, null, null };
				if (OUTPUT_TYPE[0]) {
					STATS_MGW = new JTextArea();
					STATS_MGW.setEditable(false);
					PS[0] = new PrintStream(new CustomOutputStream(STATS_MGW));
				}
				if (OUTPUT_TYPE[1]) {
					STATS_PropT = new JTextArea();
					STATS_PropT.setEditable(false);
					PS[1] = new PrintStream(new CustomOutputStream(STATS_PropT));
				}
				if (OUTPUT_TYPE[2]) {
					STATS_HelT = new JTextArea();
					STATS_HelT.setEditable(false);
					PS[2] = new PrintStream(new CustomOutputStream(STATS_HelT));
				}
				if (OUTPUT_TYPE[3]) {
					STATS_Roll = new JTextArea();
					STATS_Roll.setEditable(false);
					PS[3] = new PrintStream(new CustomOutputStream(STATS_Roll));
				}

				// Open Output File
				String BASENAME = BED.get(x).getName().split("\\.")[0];
				try {
					if (OUT_DIR != null) {
						BASENAME = OUT_DIR.getCanonicalPath() + File.separator + BASENAME;
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				// Initialize Script Object and execute calculations
				DNAShapefromBED script_obj = new DNAShapefromBED(GENOME, BED.get(x), BASENAME, OUTPUT_TYPE, STRAND, PS, OUTPUT_GZIP);
				script_obj.run();

				// Exit if FAI failed checks
				if (!script_obj.getFAIstatus()) {
					JOptionPane.showMessageDialog(null, "Genome FASTA file contains invalid lines!!!\n");
					break;
				}

				// Convert average and statistics to output tabs panes
				if (OUTPUT_TYPE[0]) {
					tabbedPane_Scatterplot.add("MGW", script_obj.getChartM());
					STATS_MGW.setCaretPosition(0);
					JScrollPane MGWpane = new JScrollPane(STATS_MGW, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
							JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					tabbedPane_Statistics.add("MGW", MGWpane);
				}
				if (OUTPUT_TYPE[1]) {
					tabbedPane_Scatterplot.add("Propeller Twist", script_obj.getChartP());
					STATS_PropT.setCaretPosition(0);
					JScrollPane PropTpane = new JScrollPane(STATS_PropT, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
							JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					tabbedPane_Statistics.add("PropT", PropTpane);
				}
				if (OUTPUT_TYPE[2]) {
					tabbedPane_Scatterplot.add("Helical Twist", script_obj.getChartH());
					STATS_HelT.setCaretPosition(0);
					JScrollPane HelTpane = new JScrollPane(STATS_HelT, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
							JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					tabbedPane_Statistics.add("HelT", HelTpane);
				}
				if (OUTPUT_TYPE[3]) {
					tabbedPane_Scatterplot.add("Roll", script_obj.getChartR());
					STATS_Roll.setCaretPosition(0);
					JScrollPane Rollpane = new JScrollPane(STATS_Roll, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
							JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					tabbedPane_Statistics.add("Roll", Rollpane);
				}
				firePropertyChange("fa", x, x + 1);
			}
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		} catch (SAMException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
}