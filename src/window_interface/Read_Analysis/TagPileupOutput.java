package window_interface.Read_Analysis;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
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

import objects.PileupParameters;
import objects.CustomOutputStream;
import scripts.Read_Analysis.TagPileup;
import util.BAMUtilities;

@SuppressWarnings("serial")
public class TagPileupOutput extends JFrame {
	Vector<File> BEDFiles = null;
	Vector<File> BAMFiles = null;
	
	PileupParameters PARAM = null;
	
// 	private int STRAND = 0;
// 	private int CPU = 1;
	
	PrintStream COMPOSITE = null;
	// Generic print stream to accept PrintStream of GZIPOutputStream
// 	Writer OUT_S1 = null;
// 	Writer OUT_S2 = null;
	
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
// 		STRAND = param.getStrand();
// 		CPU = param.getCPU();
		
	}
	
	public void run() throws IOException {
		if(PARAM.getOutputCompositeStatus()) {
			try { COMPOSITE = new PrintStream(PARAM.getOutput() + File.separator + PARAM.getCompositeFile());
			} catch (FileNotFoundException e) {	e.printStackTrace(); }
		}
		
		//Check if BAI index file exists for all BAM files
		boolean[] BAMvalid = new boolean[BAMFiles.size()];
		for(int z = 0; z < BAMFiles.size(); z++) {
			File BAM = BAMFiles.get(z);	//Pull current BAM file
			File f = new File(BAM + ".bai"); //Generate file name for BAI index file
			if(!f.exists() || f.isDirectory()) {
				BAMvalid[z] = false;
				JOptionPane.showMessageDialog(null, "BAI Index File does not exist for: " + BAM.getName());
				System.err.println("BAI Index File does not exist for: " + BAM.getName());
			} else { BAMvalid[z] = true; }
		}
		
		int PROGRESS = 0;
		for(int z = 0; z < BAMFiles.size(); z++) {
			File BAM = BAMFiles.get(z);	//Pull current BAM file
			if(BAMvalid[z]) {
				//Code to standardize tags sequenced to genome size (1 tag / 1 bp)
				if(PARAM.getStandard() && PARAM.getBlacklist() != null) { PARAM.setRatio(BAMUtilities.calculateStandardizationRatio(BAM, PARAM.getBlacklist(), PARAM.getRead())); }
				else if(PARAM.getStandard()) { PARAM.setRatio(BAMUtilities.calculateStandardizationRatio(BAM, PARAM.getRead())); }
				//System.out.println(PARAM.getRatio());
				
				for(int BED_Index = 0; BED_Index < BEDFiles.size(); BED_Index++) {
					System.err.println("Processing BAM: " + BAM.getName() + "\tCoordinate: " + BEDFiles.get(BED_Index).getName());

					JTextArea STATS = new JTextArea(); //Generate statistics object
					STATS.setEditable(false); //Make it un-editable
					PrintStream ps = new PrintStream(new CustomOutputStream(STATS));
					
					//Here we add script object: 1BAM x 1BED
					System.err.println("make script object...");
					TagPileup script_obj = new TagPileup(BEDFiles.get(BED_Index), BAM, PARAM, ps, null, true);
					script_obj.run();
					System.err.println("script object made...");
					
					tabbedPane_Scatterplot.add(BAM.getName(), script_obj.getCompositePlot());
					
					STATS.setCaretPosition(0);
					JScrollPane newpane = new JScrollPane(STATS, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					tabbedPane_Statistics.add(BAM.getName(), newpane);
			        firePropertyChange("tag", PROGRESS, PROGRESS + 1);
			        PROGRESS++;
				}
			}
		}		
	}

}
