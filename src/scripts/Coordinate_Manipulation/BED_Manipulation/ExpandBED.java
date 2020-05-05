package scripts.Coordinate_Manipulation.BED_Manipulation;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

public class ExpandBED {
	public static void expandBEDBorders(File out_filepath, File input, int SIZE, boolean ExCenter) throws IOException {
		
		Scanner scan = new Scanner(input);
	    PrintStream OUT = System.out;
	    if( out_filepath != null ) OUT = new PrintStream(out_filepath);
	    
		while (scan.hasNextLine()) {
			String[] temp = scan.nextLine().split("\t");
			if(temp[0].toLowerCase().contains("track") || temp[0].startsWith("#")) { OUT.println(String.join("\t", temp)); }
			else {
				if(temp.length > 2) {
					if(Integer.parseInt(temp[1]) >= 0) {
						//Default to add to border
						int newstart = Integer.parseInt(temp[1]) - SIZE;
						int newstop = Integer.parseInt(temp[2]) + SIZE;
						if(ExCenter) { //Else expand from center
							boolean EVEN = ((Integer.parseInt(temp[2]) - Integer.parseInt(temp[1])) % 2 == 0);
				        	int CENTER = (int)((Integer.parseInt(temp[1]) + Integer.parseInt(temp[2])) / 2);
				        	if(temp.length > 5) { if(!temp[5].equals("-") && !EVEN) { CENTER++; } }
					        newstart = CENTER - (SIZE / 2);
					        newstop = CENTER + (SIZE / 2);
				        }
				        OUT.print(temp[0] + "\t" + newstart + "\t" + newstop);
				        for(int x = 3; x < temp.length; x++) {
				        	OUT.print("\t" + temp[x]);
				        }
				        OUT.println();
					} else {
						System.out.println("Invalid Coordinate in File!!!\n" + Arrays.toString(temp));
					}
				}
			}
	    }
		scan.close();
		OUT.close();
	}
}
