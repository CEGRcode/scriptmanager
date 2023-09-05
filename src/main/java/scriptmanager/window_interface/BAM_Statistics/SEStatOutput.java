package scriptmanager.window_interface.BAM_Statistics;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import scriptmanager.cli.BAM_Statistics.SEStatsCLI;
import scriptmanager.objects.CustomOutputStream;
import scriptmanager.objects.LogItem;
import scriptmanager.scripts.BAM_Statistics.SEStats;

@SuppressWarnings("serial")
public class SEStatOutput extends JFrame {
	Vector<File> bamFiles = null;
	File output = null;
	
	PrintStream OUT = null;
	
	private JTextArea textArea;
	private PrintStream jtxtPrintStream;
	
	public SEStatOutput(Vector<File> input, File o) {
		setTitle("BAM File Statistics");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);

		bamFiles = input;
		output = o;
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		jtxtPrintStream = new PrintStream( new CustomOutputStream(textArea) );
	}
	
	public void run() {
		LogItem old_li = null;
		if(output != null) {
			try {
				OUT = new PrintStream(output);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Execute on each BAM file in the list
		for(int x = 0; x < bamFiles.size(); x++) {
			old_li = new LogItem("");
			// Initialize LogItem
			String command = SEStatsCLI.getCLIcommand(bamFiles.get(x), output);
			LogItem new_li = new LogItem(command);
			firePropertyChange("log", old_li, new_li);
			// Use script and pass PrintStream object that sends to JTextArea
			SEStats.getSEStats( output, bamFiles.get(x), jtxtPrintStream );
			// Update log item
			new_li.setStopTime(new Timestamp(new Date().getTime()));
			new_li.setStatus(0);
			old_li = new_li;
		}
		if(output != null) OUT.close();
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