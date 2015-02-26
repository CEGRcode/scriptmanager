package scripts.Coordinate_Manipulation.BED_Manipulation;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

public class ExpandBED {
	public static void expandBEDBorders(File out_path, File input, int SIZE, boolean ExCenter) throws IOException {
		String newName = (input.getName()).substring(0,input.getName().length() - 4) + "_" + Integer.toString(SIZE) +"bp.bed";
	    Scanner scan = new Scanner(input);
	    PrintStream OUT = null;
	    if(out_path == null) OUT = new PrintStream(newName);
	    else OUT = new PrintStream(out_path + File.separator + newName);
	    
		while (scan.hasNextLine()) {
			String[] temp = scan.nextLine().split("\t");
			if(temp.length > 2) {
				if(!temp[0].contains("track") && !temp[0].contains("#")) {
					if(Integer.parseInt(temp[1]) >= 0) {
						//Default to add to border
						int newstart = Integer.parseInt(temp[1]) - SIZE;
						int newstop = Integer.parseInt(temp[2]) + SIZE;
				        if(ExCenter) { //Else expand from center
							boolean EVEN = ((Integer.parseInt(temp[2]) - Integer.parseInt(temp[1])) % 2 == 0);
				        	int CENTER = (int)((Integer.parseInt(temp[1]) + Integer.parseInt(temp[2])) / 2);
				        	if(!temp[5].equals("-") && !EVEN) { CENTER++; }
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
