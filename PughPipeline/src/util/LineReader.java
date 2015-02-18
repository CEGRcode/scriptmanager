package util;

import java.io.BufferedReader;
import java.io.IOException;

public class LineReader {   
	
	private boolean removeENDLINE;
	private BufferedReader READER;
  
	public LineReader(BufferedReader buffer) {
		READER = buffer;
	}
	
	//returns line containing string terminator characters intact
	public String readLine() throws IOException {
		StringBuilder string = new StringBuilder();
		int x;
		
		while((x = READER.read()) >= 0) {
			if (x == '\n') {
				if(removeENDLINE) {
					removeENDLINE = false;  
				} else {
					string.append((char)'\n');
					break;
				}
			} else {
				removeENDLINE = false;
				string.append((char)x);  
				if (x == '\r') {
					removeENDLINE = true;
					break;
				}
			}
		}
		return string.toString();
	}
}