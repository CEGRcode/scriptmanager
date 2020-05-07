package window_interface.BAM_Statistics;

import htsjdk.samtools.AbstractBAMFileIndex;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;
import htsjdk.samtools.util.CloseableIterator;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.JLayeredPane;
import javax.swing.JTabbedPane;

// import charts.Histogram;
// import charts.LineChart;
import org.jfree.chart.ChartPanel;

import scripts.BAM_Statistics.PEStats;
import objects.CustomOutputStream;

@SuppressWarnings("serial")
public class PEStatOutput extends JFrame {
	
	Vector<File> bamFiles = null;
	private File OUTPUT_PATH = null;
	private boolean OUTPUT_STATUS = false;
	private boolean DUP_STATUS = false;
// 	PrintStream OUT = null;
// 	private File OUTPNG = null;
	private static int MIN_INSERT = 0;
	private static int MAX_INSERT = 1000;
	
	final JLayeredPane layeredPane;
	final JTabbedPane tabbedPane;
	final JTabbedPane tabbedPane_Histogram;
	final JTabbedPane tabbedPane_InsertStats;
	final JTabbedPane tabbedPane_Duplication;
	final JTabbedPane tabbedPane_DupStats;
		
	public PEStatOutput(Vector<File> input, File o, boolean out, boolean dup, int min, int max) {
		setTitle("BAM File Paired-end Statistics");
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
		
		bamFiles = input;
		OUTPUT_PATH = o;
		OUTPUT_STATUS = out;
		DUP_STATUS = dup;
		MIN_INSERT = min;
		MAX_INSERT = max;
		
		tabbedPane_Histogram = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane_InsertStats = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Insert Histogram", null, tabbedPane_Histogram, null);
		tabbedPane.addTab("PE Insert Stats", null, tabbedPane_InsertStats, null);
		
		tabbedPane_Duplication = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane_DupStats = new JTabbedPane(JTabbedPane.TOP);
		if(DUP_STATUS) {
			tabbedPane.addTab("Duplication Rate", null, tabbedPane_Duplication, null);
			tabbedPane.addTab("Duplication Stats", null, tabbedPane_DupStats, null);
		}
	}
	
	public void run() throws IOException {
		//Iterate through all BAM files in Vector	
		for(int x = 0; x < bamFiles.size(); x++) {
			
			// Construct Basename
			File NAME = null;
			if(OUTPUT_STATUS){
				try{
					NAME = new File( bamFiles.get(x).getName().split("\\.")[0] );
					if(OUTPUT_PATH != null){ NAME = new File( OUTPUT_PATH.getCanonicalPath() + File.separator + NAME.getCanonicalPath() ); }
				}
				catch (FileNotFoundException e) { e.printStackTrace(); }
// 				catch (IOException e) {	e.printStackTrace(); }
			}
			// Initialize PrintStream and TextArea for PE stats (insert sizes)
			PrintStream ps_insert = null;
			JTextArea PE_STATS = new JTextArea();
			PE_STATS.setEditable(false);
			ps_insert = new PrintStream(new CustomOutputStream( PE_STATS ));
			// Initialize PrintStream and TextArea for DUP stats
			PrintStream ps_dup = null;
			JTextArea DUP_STATS = new JTextArea();
			if(DUP_STATUS) {
				DUP_STATS.setEditable(false);
				ps_dup = new PrintStream(new CustomOutputStream( DUP_STATS ));
			}
			
			//Call public static method from scripts
			Vector<ChartPanel> charts = PEStats.getPEStats( NAME, bamFiles.get(x), DUP_STATUS, MIN_INSERT, MAX_INSERT, ps_insert, ps_dup, false );
			
			//Add pe stats to tabbed pane
			PE_STATS.setCaretPosition(0);
			JScrollPane pe_pane = new JScrollPane(PE_STATS, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			tabbedPane_InsertStats.add(bamFiles.get(x).getName(), pe_pane);
			tabbedPane_Histogram.add(bamFiles.get(x).getName(), charts.get(0));
			
			if(DUP_STATUS) {
				//Add duplication stats to tabbed pane
				DUP_STATS.setCaretPosition(0);
				JScrollPane dup_pane = new JScrollPane(DUP_STATS, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				tabbedPane_DupStats.add(bamFiles.get(x).getName(), dup_pane);
				tabbedPane_Duplication.add(bamFiles.get(x).getName(), charts.get(1));
			}
			
			ps_dup.close();
			ps_insert.close();
			
			firePropertyChange("bam",x, x + 1);	
		}
		
	}
	
}

