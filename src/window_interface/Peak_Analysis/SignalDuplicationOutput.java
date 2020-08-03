package window_interface.Peak_Analysis;

// import htsjdk.samtools.AbstractBAMFileIndex;
// import htsjdk.samtools.SAMRecord;
// import htsjdk.samtools.SAMSequenceRecord;
// import htsjdk.samtools.SamReader;
// import htsjdk.samtools.SamReaderFactory;
// import htsjdk.samtools.ValidationStringency;
// import htsjdk.samtools.util.CloseableIterator;
// 
import java.awt.BorderLayout;
import java.io.File;
// import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import org.jfree.chart.ChartPanel;
// import java.sql.Timestamp;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Iterator;
// import java.util.Scanner;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.JLayeredPane;
import javax.swing.JTabbedPane;

// import charts.LineChart;
// import objects.CoordinateObjects.GFFCoord;
import objects.CustomOutputStream;
import scripts.Peak_Analysis.SignalDuplication;

@SuppressWarnings("serial")
public class SignalDuplicationOutput extends JFrame {
	Vector<File> bamFiles = null;
// 	ArrayList<GFFCoord> COORD = null;
// 	ArrayList<GFFCoord> GENOME = null;
	
	File input = null;
	private double WINDOW = 0;
	
// 	SamReader reader;
// 	final SamReaderFactory factory = SamReaderFactory.makeDefault().enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS, SamReaderFactory.Option.VALIDATE_CRC_CHECKSUMS).validationStringency(ValidationStringency.SILENT);

	PrintStream OUT = null;
	
	final JLayeredPane layeredPane;
	final JTabbedPane tabbedPane;
	final JTabbedPane tabbedPane_Duplication;
	final JTabbedPane tabbedPane_DupStats;
	
	public SignalDuplicationOutput(File in, Vector<File> bam, double w) {
		setTitle("Signal Duplication Rate");
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
			
		tabbedPane_Duplication = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Duplication Rate", null, tabbedPane_Duplication, null);
		
		tabbedPane_DupStats = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Duplication Stats", null, tabbedPane_DupStats, null);

		bamFiles = bam;
		input = in;
		WINDOW = w;
	}
	
	public void run() throws IOException {
		//Print TimeStamp
// 		String time = getTimeStamp();
// 		if(OUT != null) OUT.println(time);
		
		//Load up GFF file into COORD
// 		loadCoord();
// 		Collections.sort(COORD, GFFCoord.PeakPositionComparator);
// 		Collections.sort(COORD, GFFCoord.PeakChromComparator);
		
		for(int x = 0; x < bamFiles.size(); x++) {
			JTextArea DUP_STATS = new JTextArea();
			DUP_STATS.setEditable(false);
			PrintStream ps = new PrintStream(new CustomOutputStream(DUP_STATS));
			
			//Check if BAI index file exists
			File f = new File(bamFiles.get(x) + ".bai");
			if(f.exists() && !f.isDirectory()) {
				
				SignalDuplication script_obj = new SignalDuplication(input, bamFiles.get(x), WINDOW, ps);
				script_obj.run();
				
				//Add duplication stats to tabbed pane
				DUP_STATS.setCaretPosition(0);
				JScrollPane dup_pane = new JScrollPane(DUP_STATS, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				tabbedPane_DupStats.add(bamFiles.get(x).getName(), dup_pane);
				tabbedPane_Duplication.add(bamFiles.get(x).getName(), script_obj.getLineChart());
				
		        firePropertyChange("bam",x, x + 1);

			} else {
				if(OUT != null) OUT.println("BAI Index File does not exist for: " + bamFiles.get(x).getName() + "\n");
				DUP_STATS.append("BAI Index File does not exist for: " + bamFiles.get(x).getName() + "\n\n");
			}
		}
	}
}
