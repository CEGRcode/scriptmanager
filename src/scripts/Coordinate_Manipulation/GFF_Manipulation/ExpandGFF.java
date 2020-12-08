package scripts.Coordinate_Manipulation.GFF_Manipulation;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

public class ExpandGFF {
	public static void expandGFFBorders(File out_filepath, File input, int SIZE, boolean ExCenter) throws IOException {
		//GFF:	chr22  TeleGene enhancer  10000000  10001000  500 +  .  touch1
		//GFF:	chr12	bed2gff	chr12_384641_384659_+	384642	384659	42.6	+	.	chr12_384641_384659_+;
		
		Scanner scan = new Scanner(input);
	    PrintStream OUT = System.out;
	    if( out_filepath != null ) OUT = new PrintStream(out_filepath);
	    
		while (scan.hasNextLine()) {
			String[] temp = scan.nextLine().split("\t");
			if(temp[0].toLowerCase().contains("track") || temp[0].startsWith("#")) { OUT.println(String.join("\t", temp)); }
			else {
				if(temp.length == 9) {
					if(Integer.parseInt(temp[3]) >= 1) {
						//Default to add to border
						int newstart = Integer.parseInt(temp[3]) - SIZE;
						int newstop = Integer.parseInt(temp[4]) + SIZE;
				        if(ExCenter) { //Else expand from center
							boolean EVEN = ((Integer.parseInt(temp[4]) - Integer.parseInt(temp[3])) % 2 == 0);
				        	int CENTER = (int)((Integer.parseInt(temp[3]) + Integer.parseInt(temp[4])) / 2);
				        	if(!EVEN || !temp[6].equals("-")) {	CENTER++; }
				        	newstart = CENTER - (SIZE / 2);
					        newstop = CENTER + ((SIZE / 2) - 1);
				        }

				        OUT.print(temp[0] + "\t" + temp[1] + "\t" + temp[2] + "\t" + newstart + "\t" + newstop);
				        for(int x = 5; x < temp.length; x++) {
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
