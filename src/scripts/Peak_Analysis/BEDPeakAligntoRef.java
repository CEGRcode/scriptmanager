package scripts.Peak_Analysis;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class BEDPeakAligntoRef {
	private String peakPath = null;
	private String refPath = null;
	private PrintStream OUT = null;
	private PrintStream PS = null;
	
	public BEDPeakAligntoRef(File ref, File peak, File output, PrintStream ps) throws IOException {
		refPath = ref.getCanonicalPath();
		peakPath = peak.getCanonicalPath();
		PS = ps;
		
		try {OUT = new PrintStream(output); }
		catch (FileNotFoundException e) { e.printStackTrace(); }
	}
		
	public void run() throws IOException, InterruptedException {
		printPS("Mapping: " + peakPath + " to " + refPath);
		printPS("Starting: " + getTimeStamp());
		
		int counter = 0;
		InputStream inputStream = new FileInputStream(peakPath);
	    BufferedReader buff = new BufferedReader(new InputStreamReader(inputStream), 100); 
		
		String key ;
//--------------
		buff = new BufferedReader(new InputStreamReader(inputStream), 100);
		Map<String, List<String>> peakMap = new HashMap<>();
		for (String line; (line = buff.readLine()) != null; ) {
			key = line.split("\t")[0];
			if(!peakMap.containsKey(key)) {
				peakMap.put(key, new ArrayList<String>());
				peakMap.get(key).add(line);
			}
			else
			{
				peakMap.get(key).add(line + "\t");
			}
		}
		buff.close();
		inputStream.close();
	
//============
		inputStream = new FileInputStream(refPath);
	    buff = new BufferedReader(new InputStreamReader(inputStream), 100);
	    
	    for (String line; (line = buff.readLine()) != null; ) {
		    	String[] str = line.split("\t");
		    	String chr = str[0];
			String[] peakLine;
			int cdtLength = (Integer.parseInt(str[2])) - (Integer.parseInt(str[1]));
			int cdtArr[] = new int[cdtLength];
			if(peakMap.containsKey(chr))
			{
				for(int i = 0; i < peakMap.get(chr).size(); i++)
				{
					peakLine = peakMap.get(chr).get(i).split("\t");	
					if(Integer.parseInt(peakLine[1]) <= Integer.parseInt(str[2]) && Integer.parseInt(peakLine[1]) >= Integer.parseInt(str[1])) {
						int START = Integer.parseInt(peakLine[1]) - Integer.parseInt(str[1]);
						int STOP = START + (Integer.parseInt(peakLine[2]) - Integer.parseInt(peakLine[1]));
						for(int x = START; x <= STOP; x++) {
	    					if(x >= 0 && x < cdtLength) { cdtArr[x]++; }
	    					}
						}
					else if(Integer.parseInt(peakLine[2]) >= Integer.parseInt(str[1]) && Integer.parseInt(peakLine[2]) <= Integer.parseInt(str[2]))
					{
						int START = Integer.parseInt(peakLine[1]) - Integer.parseInt(str[1]);
						int STOP = START + (Integer.parseInt(peakLine[2]) - Integer.parseInt(peakLine[1]));
						for(int c = START; c <= STOP; c++) {
							if(c >= 0 && c < cdtLength) { cdtArr[c]++; }
							}
						}
					}
			}
			
			if(counter == 0) {
				OUT.print("YORF" + "\t" + "NAME");
				for(int j = 0; j < cdtLength; j++) { OUT.print("\t" + j); }
				OUT.print("\n");}
			OUT.print(str[3] + "\t" + str[3]);
			if(str[5].equalsIgnoreCase("+")) { for(int i = 0; i < cdtLength; i++) { OUT.print("\t" + cdtArr[i]); } }
			else { for(int j = cdtLength-1; j >= 0; j--) { OUT.print("\t" + cdtArr[j]); } }
			OUT.print("\n");
			counter++;
			
			if(counter % 1000 == 0) {
				printPS("Reference rows processed: " + counter);
			}
	    }
	    buff.close();
	    inputStream.close();
	    
		printPS("Completing: " + getTimeStamp());
	    }

	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
	
	private void printPS(String message){
		if(PS!=null) PS.println(message);
		System.err.println(message);
	}
}