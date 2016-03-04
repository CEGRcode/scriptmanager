package scripts.Tag_Analysis;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class VarianceCalc {
	public static void calculateFuzziness(File path, File cdt) throws IOException {
		//Open output file
		//Format "ID\tSum\tSampleStandardDeviation\n"
		String varOut_name = (cdt.getName()).substring(0,cdt.getName().length() - 4) + "_VARIANCE.out";
		PrintStream OUT = null;
	    if(path == null) OUT = new PrintStream(varOut_name);
	    else OUT = new PrintStream(path + File.separator + varOut_name);
	    
	    OUT.println("UniqID\tScore\tSampleStandardDeviation");
		//Parse CDT File
		Scanner scan = new Scanner(cdt);
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			String[] ID = line.split("\t");
			if(!ID[0].contains("YORF") && !ID[0].contains("NAME")) {
				ArrayList<Double> ref = new ArrayList<Double>();
				double SUM = 0;
				double AVG = 0;
				double N = 0;
				for(int x = 2; x < ID.length; x++) {
					SUM += Double.parseDouble(ID[x]);
					for(int z = 0; z < Double.parseDouble(ID[x]); z++) {
						ref.add(new Double(x));
						AVG += x;
						N++;
					}
				}
				AVG /= N;

				double VAR = 0;
				if(N > 1) {
					double SS = 0;
					for(int x = 0; x < ref.size(); x++) {
						SS += Math.pow(Math.abs(ref.get(x).doubleValue() - AVG) ,2);
					}
					VAR = Math.sqrt(SS / N) * Math.sqrt(N / (N - 1));
				}
				OUT.println(ID[0] + "\t" + SUM + "\t" + VAR);
			}
		}
		scan.close();
		OUT.close();
	}
}
