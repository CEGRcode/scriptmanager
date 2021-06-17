package window_interface.Read_Analysis;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLayeredPane;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import charts.CompositePlot;
import objects.PileupParameters;
import objects.CustomOutputStream;
import scripts.Read_Analysis.TagPileup;
import util.BAMUtilities;

@SuppressWarnings("serial")
public class TagPileupOutput extends JFrame {
	Vector<File> BEDFiles = null;
	Vector<File> BAMFiles = null;

	PileupParameters PARAM = null;
	PrintStream COMPOSITE = null;

	final JLayeredPane layeredPane;
	final JTabbedPane tabbedPane;
	final JTabbedPane tabbedPane_Scatterplot;
	final JTabbedPane tabbedPane_Statistics;

	public TagPileupOutput(Vector<File> be, Vector<File> ba, PileupParameters param) {
		setTitle("Tag Pileup Composite");
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
		tabbedPane.addTab("Pileup Plot", null, tabbedPane_Scatterplot, null);

		tabbedPane_Statistics = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Pileup Statistics", null, tabbedPane_Statistics, null);

		BEDFiles = be;
		BAMFiles = ba;
		PARAM = param;
	}

	public void run() throws IOException {
		// Check if BAI index file exists for all BAM files
		boolean[] BAMvalid = new boolean[BAMFiles.size()];
		for (int z = 0; z < BAMFiles.size(); z++) {
			File BAM = BAMFiles.get(z); // Pull current BAM file
			File f = new File(BAM + ".bai"); // Generate file name for BAI index file
			if (!f.exists() || f.isDirectory()) {
				BAMvalid[z] = false;
				JOptionPane.showMessageDialog(null, "BAI Index File does not exist for: " + BAM.getName());
				System.err.println("BAI Index File does not exist for: " + BAM.getName());
			} else {
				BAMvalid[z] = true;
			}
		}

		int PROGRESS = 0;
		for (int z = 0; z < BAMFiles.size(); z++) {
			File BAM = BAMFiles.get(z); // Pull current BAM file
			if (BAMvalid[z]) {
				// Code to standardize tags sequenced to genome size (1 tag / 1 bp)
				if (PARAM.getStandard() && PARAM.getBlacklist() != null) {
					PARAM.setRatio(
							BAMUtilities.calculateStandardizationRatio(BAM, PARAM.getBlacklist(), PARAM.getRead()));
				} else if (PARAM.getStandard()) {
					PARAM.setRatio(BAMUtilities.calculateStandardizationRatio(BAM, PARAM.getRead()));
				}

				for (int BED_Index = 0; BED_Index < BEDFiles.size(); BED_Index++) {
					System.err.println(
							"Processing BAM: " + BAM.getName() + "\tCoordinate: " + BEDFiles.get(BED_Index).getName());

					JTextArea STATS = new JTextArea(); // Generate statistics object
					STATS.setEditable(false); // Make it un-editable
					PrintStream ps = new PrintStream(new CustomOutputStream(STATS));

					TagPileup script_obj = new TagPileup(BEDFiles.get(BED_Index), BAM, PARAM, ps, null);
					script_obj.run();

					// Make composite plots
					if (PARAM.getStrand() == 0) {
						tabbedPane_Scatterplot.add(BAM.getName(), CompositePlot.createCompositePlot(script_obj.DOMAIN, script_obj.AVG_S1, script_obj.AVG_S2, BEDFiles.get(BED_Index).getName(), PARAM.getColors()));
					} else {
						tabbedPane_Scatterplot.add(BAM.getName(), CompositePlot.createCompositePlot(script_obj.DOMAIN, script_obj.AVG_S1, BEDFiles.get(BED_Index).getName(), PARAM.getColors()));
					}

					STATS.setCaretPosition(0);
					JScrollPane newpane = new JScrollPane(STATS, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
							JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					tabbedPane_Statistics.add(BAM.getName(), newpane);
					firePropertyChange("tag", PROGRESS, PROGRESS + 1);
					PROGRESS++;
				}
			}
		}
	}

}
