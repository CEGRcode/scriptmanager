package scripts.Figure_Generation;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import scripts.Figure_Generation.HeatmapScripts.HeatMapPanel;

@SuppressWarnings("serial")
public class HeatMapPlot extends JFrame {
	
	private Vector<File> INPUT = null;
	@SuppressWarnings("unused")
	private boolean MATCH_FILES = false;
	public File OUTPUT = null;
	
	final JLayeredPane layeredPane;
	final JTabbedPane tabbedPane;
	 
	public HeatMapPlot(Vector<File> in, boolean match) {
		setTitle("CDT File Visualization");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 1000, 600);
		getContentPane().setLayout(new BorderLayout());
		
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
		
		INPUT = in;
		MATCH_FILES = match;
	}
	
	public void run() throws IOException {
		for(int x = 0; x < INPUT.size(); x++) {
			HeatMapPanel newpanel = new HeatMapPanel(INPUT.get(x));
			tabbedPane.add(INPUT.get(x).getName(), newpanel.generatePanel());
		}
	}
}