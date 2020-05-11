package window_interface.BAM_Format_Converter;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import objects.CustomOutputStream;
import scripts.BAM_Format_Converter.BAMtobedGraph;

@SuppressWarnings("serial")
public class BAMtobedGraphOutput extends JFrame {
	private File BAM = null;
	private File OUTPUTPATH = null;
	private int STRAND = 0;
	private String READ = "READ1";
	
	private static int PAIR = 1;
	private static int MIN_INSERT = -9999;
	private static int MAX_INSERT = -9999;

	private JTextArea textArea;
	
	public BAMtobedGraphOutput(File b, File o, int s, int pair_status, int min_size, int max_size) {
		setTitle("BAM to bedGraph Progress");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		BAM = b;
		OUTPUTPATH = o;
		STRAND = s;
		PAIR = pair_status;
		MIN_INSERT = min_size;
		MAX_INSERT = max_size;
		if(STRAND == 0) { READ = "READ1"; }
		else if(STRAND == 1) { READ = "READ2"; }
		else if(STRAND == 2) { READ = "COMBINED"; }
		else if(STRAND == 3) { READ = "MIDPOINT"; }
	}
	
	public void run() throws IOException, InterruptedException {
		System.err.println(getTimeStamp());
		
		//Open Output File
		File OUTBASENAME;
		String NAME = BAM.getName().split("\\.")[0] + "_" + READ;
		if(OUTPUTPATH != null) {
			OUTBASENAME = new File( OUTPUTPATH.getCanonicalPath() + File.separator + NAME );
		} else {
			OUTBASENAME = new File( NAME );
		}
		
		//Call script here, pass in ps and OUT
		PrintStream PS = new PrintStream( new CustomOutputStream(textArea) );
		PS.println(NAME);
		BAMtobedGraph script_obj = new BAMtobedGraph(BAM, OUTBASENAME, STRAND, PAIR, MIN_INSERT, MAX_INSERT, PS);
		script_obj.run();
		
		Thread.sleep(2000);
		dispose();
		
		System.err.println(getTimeStamp());
	}
	
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}