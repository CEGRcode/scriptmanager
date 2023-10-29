package scriptmanager.window_interface.BAM_Statistics;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import scriptmanager.cli.BAM_Statistics.SEStatsCLI;
import scriptmanager.objects.CustomOutputStream;
import scriptmanager.objects.LogItem;
import scriptmanager.scripts.BAM_Statistics.SEStats;
import scriptmanager.util.ExtensionFileFilter;

@SuppressWarnings("serial")
public class SEStatOutput extends JFrame {
	Vector<File> bamFiles = null;
	File OUT_DIR = null;

	private boolean OUTPUT_STATUS;

	final JLayeredPane layeredPane;
	final JTabbedPane tabbedPane;
	
	public SEStatOutput(Vector<File> input, File o, boolean o_status) {
		setTitle("BAM File Statistics");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);

		bamFiles = input;
		OUT_DIR = o;
		OUTPUT_STATUS = o_status;

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
	}
	
	public void run() throws IOException {
		LogItem old_li = null;
		// Execute on each BAM file in the list
		for(int x = 0; x < bamFiles.size(); x++) {
			// Construct output filename
			String NAME = ExtensionFileFilter.stripExtension(bamFiles.get(x).getName()) + "_SE-stats.txt";
			File OUT_FILEPATH = new File(NAME);
			if (OUT_DIR != null) {
				OUT_FILEPATH = new File( OUT_DIR.getCanonicalPath() + File.separator + NAME);
			}
			// Initialize PrintStream and TextArea for SE stats
			PrintStream ps_stats = null;
			JTextArea txtArea_Statistics = new JTextArea();
			txtArea_Statistics.setEditable(false);
			ps_stats = new PrintStream(new CustomOutputStream( txtArea_Statistics ));
			old_li = new LogItem("");
			// Initialize LogItem
			String command = SEStatsCLI.getCLIcommand(bamFiles.get(x), OUT_FILEPATH);
			LogItem new_li = new LogItem(command);
			firePropertyChange("log", old_li, new_li);
			// Use script and pass PrintStream object that sends to JTextArea
			SEStats.getSEStats(bamFiles.get(x), OUT_FILEPATH, OUTPUT_STATUS, ps_stats );
			// Update log item
			new_li.setStopTime(new Timestamp(new Date().getTime()));
			new_li.setStatus(0);
			old_li = new_li;
			// Add JTextArea to JScrollPane in JTabbedPane
			JScrollPane se_pane = new JScrollPane(txtArea_Statistics, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			tabbedPane.add(bamFiles.get(x).getName(), se_pane);
			// Execute script
			SEStats.getSEStats(bamFiles.get(x), OUT_FILEPATH, OUTPUT_STATUS, ps_stats);
			// Close stats PrintStream
			ps_stats.close();
			// Update progress
			firePropertyChange("progress", x-1, x);
		}
		//BAMIndexMetaData.printIndexStats(bamFiles.get(x))
		firePropertyChange("log", old_li, null);
	}

	public static void main(String[] args) {
		System.out.print("java -cp ");
		//Output full path of ScriptManager
		try { System.out.print(new File(SEStats.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath()); }
		catch (URISyntaxException e) { e.printStackTrace(); }
		System.out.println(" scripts.BAM_Statistics.SEStats");
	}
}