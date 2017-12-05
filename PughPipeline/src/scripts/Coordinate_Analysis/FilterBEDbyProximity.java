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

import objects.BEDCoord;

@SuppressWarnings({"serial"})
public class FilterBEDbyProximity extends JFrame{
	
	private int CUTOFF;
	private InputStream inputStream;
	private File OUTPUTPATH = null;
	private String INPUTFILE = null;
	private PrintStream OUT_Filter = null;
	private PrintStream OUT_Cluster = null;
	
	private JTextArea textArea;
	
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
		String fname_f = INPUTFILE.substring(0, input.getName().lastIndexOf('.')) + "_" + Integer.toString(CUTOFF) + "bp-FILTER" + ".bed"; 
		String fname_c = INPUTFILE.substring(0, input.getName().lastIndexOf('.')) + "_" + Integer.toString(CUTOFF) + "bp-CLUSTER" + ".bed";
		
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
	    List<BEDCoord> bedArray = new ArrayList<BEDCoord>();
	    List<Integer> failArray = new ArrayList<Integer>();

	    String line;
		while((line = lines.readLine()) != null) {
			bedArray.add(new BEDCoord(line));
			bedArray.get(bedArray.size() - 1).calcMid();
			failArray.add(new Integer(0));
		}
		Collections.sort(bedArray, BEDCoord.PeakMidpointComparator);
		Collections.sort(bedArray, BEDCoord.PeakChromComparator);
		
		for(int i = 0; i < bedArray.size(); i++) {
			int INDEX = i - 1;
			if(INDEX >= 0) {
				while((bedArray.get(i).getChrom().equals(bedArray.get(INDEX).getChrom())) && (Math.abs(bedArray.get(i).getMid() - bedArray.get(INDEX).getMid()) <= CUTOFF)) 
				{
					if(bedArray.get(i).getScore() > bedArray.get(INDEX).getScore()) 
					{
						failArray.set(INDEX, new Integer(1));
					} 
					else if((bedArray.get(i).getScore() == bedArray.get(INDEX).getScore()) && (bedArray.get(INDEX).getMid() > bedArray.get(i).getMid()))
					{
						failArray.set(INDEX, new Integer(1));
					}
					else {
						failArray.set(i, new Integer(1));
					}
					INDEX--;
					if(INDEX < 0) { break; }
				}
			}
			INDEX = i + 1;
	        if(INDEX < bedArray.size()) {
	        	while((bedArray.get(i).getChrom().equals(bedArray.get(INDEX).getChrom())) && (Math.abs(bedArray.get(i).getMid() - bedArray.get(INDEX).getMid()) <= CUTOFF)) {
		                if(bedArray.get(i).getScore() > bedArray.get(INDEX).getScore())
		                {
		                	failArray.set(INDEX, new Integer(1));
	                	}
		                else if((bedArray.get(i).getScore() == bedArray.get(INDEX).getScore()) && (bedArray.get(INDEX).getMid() > bedArray.get(i).getMid()))
		                {
		                	failArray.set(INDEX, new Integer(1));
	                    }
		                else
		                {
		                	failArray.set(i, new Integer(1));
	                	}
		                INDEX++;
		                if(INDEX == bedArray.size()) {break; }
	        	}
	        }
	}
		
		
		for(int x = 0; x < bedArray.size(); x++) {
			if(failArray.get(x).intValue() == 0) {
				OUT_Filter.println(bedArray.get(x).toString()); 
			} else {
				OUT_Cluster.println(bedArray.get(x).toString()); 
			}
		}
		OUT_Filter.close();
		OUT_Cluster.close();
			
		inputStream.close();
		System.out.println("Completing: " + getTimeStamp());
		textArea.append("Completing: " + getTimeStamp() + "\n");
		
		Thread.sleep(1000);
		dispose();
	}
	

	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}

}