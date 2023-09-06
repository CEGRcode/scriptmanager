package scriptmanager.scripts.Peak_Analysis;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;

import java.util.Date;

public class SortByRef {
	private PrintStream PS = null;
	
	public SortByRef(File ref, File peak, File outpath, boolean properStrands, boolean gzOutput, PrintStream ps) throws IOException {
	}
		
	public void sortGFF() throws IOException, InterruptedException {
	    }

	public void sortBED() throws IOException, InterruptedException {
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