package scripts.File_Utilities;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.String;
import java.util.HashMap;
import java.util.Scanner;

public class ConvertChrNames {

	public static HashMap<String, String> getR2A() {
		HashMap<String,String> R2A = new HashMap<String, String>();
		R2A.put("chrXVI", "chr16");
		R2A.put("chrXV", "chr15");
		R2A.put("chrXIV", "chr14");
		R2A.put("chrXIII", "chr13");
		R2A.put("chrXII", "chr12");
		R2A.put("chrXI", "chr11");
		R2A.put("chrX", "chr10");
		R2A.put("chrIX", "chr9");
		R2A.put("chrVIII", "chr8");
		R2A.put("chrVII", "chr7");
		R2A.put("chrVI", "chr6");
		R2A.put("chrV", "chr5");
		R2A.put("chrIV", "chr4");
		R2A.put("chrIII", "chr3");
		R2A.put("chrII", "chr2");
		R2A.put("chrI", "chr1");
		return (R2A);
	}

	public static HashMap<String, String> getA2R() {
		HashMap<String,String> A2R = new HashMap<String, String>();
		A2R.put("chr16", "chrXVI");
		A2R.put("chr15", "chrXV");
		A2R.put("chr14", "chrXIV");
		A2R.put("chr13", "chrXIII");
		A2R.put("chr12", "chrXII");
		A2R.put("chr11", "chrXI");
		A2R.put("chr10", "chrX");
		A2R.put("chr9", "chrIX");
		A2R.put("chr8", "chrVIII");
		A2R.put("chr7", "chrVII");
		A2R.put("chr6", "chrVI");
		A2R.put("chr5", "chrV");
		A2R.put("chr4", "chrIV");
		A2R.put("chr3", "chrIII");
		A2R.put("chr2", "chrII");
		A2R.put("chr1", "chrI");
		return (A2R);
	}

	// Coordinate File wrappers
	public static void convert_RomantoArabic(File input, File out_filepath) throws IOException {
		convertCoordinateFile(out_filepath, getR2A(), input);
	}

	public static void convert_ArabictoRoman(File input, File out_filepath) throws IOException {
		convertCoordinateFile(out_filepath, getA2R(), input);
	}

	public static void convertCoordinateFile(File out_filepath, HashMap<String, String> chrMap, File input)
			throws IOException {
		// BED Coords:
		// chr1 87116 87156 NS500168:175:H5LJJBGXY:1:11107:3505:14769 40 +
		// chr1 87124 87164 NS500168:175:H5LJJBGXY:1:23303:23713:17862 40 -
		// GFF Coords:
		// chr22 TeleGene enhancer 10000000 10001000 500 + . touch1
		// chr12 bed2gff chr12_384641_384659_+ 384642 384659 42.6 + .
		// chr12_384641_384659_+;
		Scanner scan = new Scanner(input);
		PrintStream OUT = System.out;
		if (out_filepath != null)
			OUT = new PrintStream(out_filepath);

		while (scan.hasNextLine()) {
			String newline = scan.nextLine();
			if (newline.startsWith("#")) {
				OUT.println(newline);
			} else {
				// Split line, replace first chromosome string token, and write new line
				String[] temp = newline.split("\t");
				if (chrMap.containsKey(temp[0])) temp[0] = chrMap.get(temp[0]);
				OUT.println(String.join("\t", temp));
			}
		}
		scan.close();
		OUT.close();
	}
}
