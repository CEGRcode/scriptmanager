package scripts.Coordinate_Manipulation.BED_Manipulation;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import util.GZipUtilities;

/**
 * Class that contains method for expanding (BED) coordinate intervals from the center/border by a user-defined direction and distance.
 * 
 * @author William KM Lai
 *
 */
public class ExpandBED {
	/**
	 * Self-contained method for expanding the BED-formatted intervals in a BED file by user-specified distance and strategy. This method accounts for even-sized BED interval expansion in midpoint calculations by using the strand-aware downstream nucleotide between the two center nucleotides.
	 * 
	 * @param out_filepath Filepath to save expanded BED-formatted files. If null, outputs to STDOUT.
	 * @param input Filepath to starting BED-formatted coordinates we want to shift. Supports automatic detection and handling of GZipped BED-formatted files. Must have at least 3 tab-delimited columns per BED specifications.
	 * @param SIZE Integer value indicating number of nucleotides to expand by (must be a positive integer).
	 * @param ExCenter Specifies expansion strategy: if true, size expansion will be performed from the midpoint of each BED interval, if false, size expansion will be performed from the border/edges of the BED intervals (default=true).
	 * @param gzOutput If this is true, the output file will be gzipped.
	 * @throws IOException
	 */
	public static void expandBEDBorders(File out_filepath, File input, int SIZE, boolean ExCenter, boolean gzOutput ) throws IOException {
		// Initialize output writer
		PrintStream OUT = System.out;
		if (out_filepath != null) {
			if (gzOutput) {
				OUT = new PrintStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(out_filepath))));
			} else {
				OUT = new PrintStream(new BufferedOutputStream(new FileOutputStream(out_filepath)));
			}
		}
		// Check if file is gzipped and instantiate appropriate BufferedReader
		BufferedReader br;
		if(GZipUtilities.isGZipped(input)) {
			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(input)), "UTF-8"));
		} else {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(input), "UTF-8"));
		}
		// Initialize line variable to loop through
		String line = br.readLine();
		while (line != null) {
			String[] temp = line.split("\t");
			if (temp[0].toLowerCase().contains("track") || temp[0].startsWith("#")) {
				OUT.println(String.join("\t", temp));
			} else {
				if (temp.length > 2) {
					if (Integer.parseInt(temp[1]) >= 0) {
						// Default to add to border
						int newstart = Integer.parseInt(temp[1]) - SIZE;
						int newstop = Integer.parseInt(temp[2]) + SIZE;
						if (ExCenter) { // Else expand from center
							boolean EVEN = ((Integer.parseInt(temp[2]) - Integer.parseInt(temp[1])) % 2 == 0);
							int CENTER = (int) ((Integer.parseInt(temp[1]) + Integer.parseInt(temp[2])) / 2);
							if (temp.length > 5) {
								if (!temp[5].equals("-") && !EVEN) {
									CENTER++;
								}
							}
							newstart = CENTER - (SIZE / 2);
							newstop = CENTER + (SIZE / 2);
						}
						OUT.print(temp[0] + "\t" + newstart + "\t" + newstop);
						for (int x = 3; x < temp.length; x++) {
							OUT.print("\t" + temp[x]);
						}
						OUT.println();
					} else {
						System.out.println("Invalid Coordinate in File!!! (coordinate must be >= 0)\n" + Arrays.toString(temp));
					}
				} else {
					System.out.println("Invalid Coordinate in File!!! (must have at least 3 columns)\n" + Arrays.toString(temp));
				}
			}
			line = br.readLine();
		}
		br.close();
		OUT.close();
	}
}
