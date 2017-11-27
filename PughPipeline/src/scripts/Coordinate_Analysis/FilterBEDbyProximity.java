package scripts.Coordinate_Analysis;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import scripts.Coordinate_Analysis.FilterBEDbyProximity.Read;



@SuppressWarnings({ "serial", "unused" })
public class FilterBEDbyProximity extends JFrame{
	
	private int CUTOFF;
	private InputStream inputStream;
	private File OUTPUTPATH = null;
	private String INPUTFILE = null;
	private PrintStream OUT_Filter = null;
	private PrintStream OUT_Cluster = null;
	
	private JTextArea textArea;
	
	
	
	class Read implements Comparable<Read>{
		String line;
		String chr;
		int start;
		int end;
		int mid;
		String info;
		int score;
		int fail = 0;
		String strand;
		public Read(String read)
		{
			line = read;
			String[] tmp = read.split("\t");
			chr = tmp[0];
			start = Integer.parseInt(tmp[1]);
			end = Integer.parseInt(tmp[2]);
			info = tmp[3];
			score = Integer.parseInt(tmp[4]);
			fail = 0;
			strand = tmp[5];
			mid = (start+end)/2; 
		}
		@Override
		public int compareTo(Read other) {
			int chrComp = chr.compareTo(other.chr);
			if(chrComp == 0)
				return new Integer(this.mid).compareTo(other.mid);
			else
				return chrComp;
				
		}
	}
	
	
	
	public FilterBEDbyProximity(File input, int cutoff, File output) throws IOException {
		
		setTitle("BED File Filter Progress");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		CUTOFF = cutoff;
		inputStream = new FileInputStream(input);
		INPUTFILE = input.getName();
		
		OUTPUTPATH = output;
		String fname_f = INPUTFILE.substring(0, input.getName().lastIndexOf('.')) + "_" + Integer.toString(CUTOFF) + "FILTER" + ".bed"; 
		String fname_c = INPUTFILE.substring(0, input.getName().lastIndexOf('.')) + "_" + Integer.toString(CUTOFF) + "CLUSTER" + ".bed";
		
		if(OUTPUTPATH != null) 
		{
			try
			{
				OUT_Filter = new PrintStream(new File(OUTPUTPATH.getCanonicalPath() + File.separator + fname_f)); 
				OUT_Cluster = new PrintStream(new File(OUTPUTPATH.getCanonicalPath() + File.separator + fname_c)); 
				
			}
			catch (FileNotFoundException e) { e.printStackTrace(); }} 
		else
		{
			try 
			{
				OUT_Filter = new PrintStream(new File(fname_f)); 
				OUT_Cluster = new PrintStream(new File(fname_c));
			}
			catch (FileNotFoundException e) { e.printStackTrace(); }
			}
		}
	
	public void run() throws IOException, InterruptedException
	{
		System.out.println("Filtering BED file with a cutoff: " + CUTOFF + " in " + INPUTFILE);
		System.out.println("Starting: " + getTimeStamp());
		textArea.append("Filtering BED file with a cutoff: " + CUTOFF + " in " + INPUTFILE + "\n");
		textArea.append("Starting: " + getTimeStamp() + "\n");
		
		
	    BufferedReader lines = new BufferedReader(new InputStreamReader(inputStream), 100);
	    List<Read> linesArray = new ArrayList<Read>();
	    String line;
		while((line = lines.readLine()) != null) {
			linesArray.add(new Read(line));
		}
		
		Collections.sort(linesArray);
		
		for(int i = 0; i < linesArray.size(); i++) {
			int INDEX = i - 1;
			if(INDEX >= 0) {
				while((linesArray.get(i).chr.equals(linesArray.get(INDEX).chr)) && (Math.abs(linesArray.get(i).mid - linesArray.get(INDEX).mid) <= CUTOFF)) 
				{
					if(linesArray.get(i).score > linesArray.get(INDEX).score) 
					{
						linesArray.get(INDEX).fail = 1;
					} 
					else if((linesArray.get(i).score == linesArray.get(INDEX).score) && (linesArray.get(INDEX).mid > linesArray.get(i).mid)) {
						linesArray.get(INDEX).fail = 1;
					} else {
						linesArray.get(i).fail = 1;
					}
					INDEX--;
					if(INDEX < 0) { break; }
				}
			}
			INDEX = i + 1;
	        if(INDEX < linesArray.size()) {
	        	while((linesArray.get(i).chr.equals(linesArray.get(INDEX).chr)) && (Math.abs(linesArray.get(i).mid - linesArray.get(INDEX).mid) <= CUTOFF)) {
		                if(linesArray.get(i).score > linesArray.get(INDEX).score) {
		                		linesArray.get(INDEX).fail = 1;
	                	} else if((linesArray.get(i).score == linesArray.get(INDEX).score) && (linesArray.get(INDEX).mid > linesArray.get(i).mid)) {
	                                linesArray.get(INDEX).fail = 1;
	                        } else {
	                        	linesArray.get(i).fail = 1;
	                	}
				INDEX++;
				if(INDEX == linesArray.size()) {break; }
	        	}
	        }
	}
		
		
		for(int x = 0; x < linesArray.size(); x++) {
			if(linesArray.get(x).fail == 0) {
				OUT_Filter.println(linesArray.get(x).line); 
			} else {
				OUT_Cluster.println(linesArray.get(x).line); 
			}
		}
		OUT_Filter.close();
		OUT_Cluster.close();
			
		inputStream.close();
		System.out.println("Completing: " + getTimeStamp());
		textArea.append("Completing: " + getTimeStamp() + "\n");
		
		Thread.sleep(2000);
		dispose();
	}
	

private static String getTimeStamp() {
	Date date= new Date();
	String time = new Timestamp(date.getTime()).toString();
	return time;
}
}