package scriptmanager.scripts.Coordinate_Manipulation.BED_Manipulation;

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

import scriptmanager.util.GZipUtilities;

/**
 * This script converts a BED-formatted coordinate file too the GFF-format.
 * 
 * @author William KM Lai
 * @see scriptmanager.cli.Coordinate_Manipulation.BED_Manipulation.BEDtoGFFCLI
 * @see scriptmanager.window_interface.Coordinate_Manipulation.BED_Manipulation.BEDtoGFFWindow
 */
public class BEDtoGFF {
	/**
	 * Read the BED-formatted input file and write it as a GFF-formatted output file.
	 * 
	 * @param outpath the filepath destination of the GFF-formatted output
	 * @param input the BED-formatted file to convert
	 * @param gzOutput If this is true, the output file will be gzipped.
	 * @throws IOException
	 */
	public static void convertBEDtoGFF(File outpath, File input, boolean gzOutput) throws IOException {
		// chr22 TeleGene enhancer 10000000 10001000 500 + . touch1
		// Initialize output writer
		PrintStream OUT = System.out;
		if (outpath != null) {
			if (gzOutput) {
				OUT = new PrintStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(outpath))));
			} else {
				OUT = new PrintStream(new BufferedOutputStream(new FileOutputStream(outpath)));
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
			line = br.readLine();
		}
		br.close();
		OUT.close();
	}
}
