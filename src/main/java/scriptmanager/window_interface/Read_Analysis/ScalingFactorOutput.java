package scriptmanager.window_interface.Read_Analysis;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;

import scriptmanager.util.ExtensionFileFilter;
import scriptmanager.scripts.Read_Analysis.ScalingFactor;

/**
 * NCIS code adapted from Mahony Lab https://github.com/seqcode/seqcode-core
 * NCIS algorithm from Liang & Keles (BMC Bioinformatics 2012)
 */

@SuppressWarnings("serial")
public class ScalingFactorOutput extends JFrame {

	ArrayList<File> BAMFiles = null;
	private File BLACKLISTFile = null;
	private File CONTROL = null;
	private File OUT_DIR = null;
	private boolean OUTPUTSTATUS = false;

	private int scaleType = -1;
	private int windowSize = 500;
	private double minFraction = 0.75;

	private ArrayList<Double> SCALINGFACTORS = new ArrayList<Double>();

	final JLayeredPane layeredPane;
	final JTabbedPane tabbedPane;
	final JTabbedPane tabbedPane_CummulativeScatterplot;
	final JTabbedPane tabbedPane_MarginalScatterplot;

	public ScalingFactorOutput(ArrayList<File> b, File bl, File c, File out_dir, boolean out, int scale, int win,
			double min) {
		setTitle("Scaling Factor");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 800, 800);

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

		BAMFiles = b;
		BLACKLISTFile = bl;
		CONTROL = c;
		OUT_DIR = out_dir;
		OUTPUTSTATUS = out;
		scaleType = scale;
		windowSize = win;
		minFraction = min;

		tabbedPane_CummulativeScatterplot = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane_MarginalScatterplot = new JTabbedPane(JTabbedPane.TOP);
		if (scaleType != 1) {
			tabbedPane.addTab("Cumulative Count Scaling Ratio", null, tabbedPane_CummulativeScatterplot, null);
			tabbedPane.addTab("Marginal Signal/Control Ratio", null, tabbedPane_MarginalScatterplot, null);
		}
	}

	public void run() throws IOException {
		for (int z = 0; z < BAMFiles.size(); z++) {
			File SAMPLE = BAMFiles.get(z); // Pull current BAM file

			String OUTBASE = ExtensionFileFilter.stripExtension(SAMPLE);
			if(OUT_DIR != null) {
				OUTBASE = OUT_DIR.getAbsolutePath() + File.separator + OUTBASE;
			}

			ScalingFactor script_obj = new ScalingFactor(SAMPLE, BLACKLISTFile, CONTROL, OUTBASE, OUTPUTSTATUS,
					scaleType, windowSize, minFraction);
			script_obj.run();

			SCALINGFACTORS.add(script_obj.getScalingFactor());

			if (script_obj.getDialogMessage() != null) {
				JOptionPane.showMessageDialog(null, script_obj.getDialogMessage());
			} else if (scaleType == 2) {
				// Generate images
				tabbedPane_CummulativeScatterplot.add(SAMPLE.getName(), script_obj.getCCPlot());
				tabbedPane_MarginalScatterplot.add(SAMPLE.getName(), script_obj.getMPlot());
			} else if (scaleType == 3) {
				// Generate images
				tabbedPane_CummulativeScatterplot.add(SAMPLE.getName(), script_obj.getCCPlot());
				tabbedPane_MarginalScatterplot.add(SAMPLE.getName(), script_obj.getMPlot());
			}
			firePropertyChange("scale", z, (z + 1));
		}

		// Make frame visible at completion of correlations if not already visible
		if (!this.isVisible()) {
			this.setVisible(true);
		}
		tabbedPane.addTab("Scaling Factor", makeTablePanel(SCALINGFACTORS));
	}

	public JScrollPane makeTablePanel(ArrayList<Double> SCALE) {
		JTable table = new JTable(SCALE.size(), 2);
		table.setName("Scaling Factors");
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		for (int i = 0; i < SCALE.size(); i++) {
			table.setValueAt(BAMFiles.get(i).getName(), i, 0);
			table.setValueAt(SCALE.get(i).doubleValue(), i, 1);
		}
		table.getColumnModel().getColumn(0).setHeaderValue("Experiment");
		table.getColumnModel().getColumn(1).setHeaderValue("Scaling Factor");
		table.setPreferredSize(table.getPreferredSize());
		// Allow for the selection of multiple OR individual cells across either rows or
		// columns
		table.setCellSelectionEnabled(true);
		table.setColumnSelectionAllowed(true);
		table.setRowSelectionAllowed(true);

		JScrollPane pane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		pane.setPreferredSize(new Dimension(590, 590));
		return pane;
	}
}
