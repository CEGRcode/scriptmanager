package scripts.Coordinate_Manipulation.BED_Manipulation;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

public class BEDtoGFF {
	public static void convertBEDtoGFF(File out_filepath, File input) throws IOException {
		// chr22 TeleGene enhancer 10000000 10001000 500 + . touch1

		Scanner scan = new Scanner(input);
		PrintStream OUT = System.out;
		if (out_filepath != null)
			OUT = new PrintStream(out_filepath);

		while (scan.hasNextLine()) {
			String[] temp = scan.nextLine().split("\t");
			if (temp[0].toLowerCase().contains("track") || temp[0].startsWith("#")) {
				OUT.println(String.join("\t", temp));
			} else {
				if (temp.length > 2) {
					String name = temp[0] + "_" + temp[1] + "_" + temp[2]; // Get or make name from BED file
					if (temp.length > 3) {
						name = temp[3];
					}
					String score = "0"; // Get or make direction
					if (temp.length > 4) {
						score = temp[4];
					}
					String dir = "+";
					if (temp.length > 5) {
						dir = temp[5];
					}

					// Make sure coordinate start is >= 0
					if (Integer.parseInt(temp[1]) >= 0) {
						int newstart = Integer.parseInt(temp[1]) + 1;
						OUT.println(temp[0] + "\tbed2gff\t" + name + "\t" + newstart + "\t" + temp[2] + "\t" + score
								+ "\t" + dir + "\t.\t" + name + ";");
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
