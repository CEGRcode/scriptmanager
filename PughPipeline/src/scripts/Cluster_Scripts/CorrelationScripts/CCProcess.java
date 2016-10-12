package scripts.Cluster_Scripts.CorrelationScripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CCProcess {
	
	private String[] labels;
	private double[][] eucliddiff;
	private double[][] coeffdiff;

	private int linecount;
	private int columncount;
	private int inversionmethod;
		
	public void fileParser(File infile, File outfile, int correlationmethod) {
		
		linecount = 0;
		columncount = 0;
		inversionmethod = correlationmethod;
		
		//count the number of lines to preallocate an array for them
		try {
			BufferedReader linecounter = new BufferedReader(new FileReader(infile));
			columncount = linecounter.readLine().trim().split("\\s+").length;
			while (linecounter.readLine() != null) {
				linecount ++;
			}
			linecounter.close();
		} catch (IOException e) {
			System.out.println("ERROR: Invalid input file.");
			e.printStackTrace();
		}
		
		int[][] data = new int[linecount][columncount];
		labels = new String[columncount];
		int linefill = 0;
		
		String line;
		String[] splitline;
		
		//read and transpose the data into the array
		try {
			BufferedReader read = new BufferedReader(new FileReader(infile));
			labels = read.readLine().trim().split("\\s+");
			while ((line = read.readLine()) != null) {	  
				splitline = (line.split("\\s"));
				for (int column = 0; column < columncount; column ++) {
					data[linefill][column] = Integer.parseInt(splitline[column + 1]);
				}
				linefill ++;
			}
			read.close();
		}
		
		catch (IOException e) {
			System.out.println("ERROR: Invalid input file.");
			e.printStackTrace();
		}
		
		eucliddiff = euclideanFinder(data);
		coeffdiff =  fileWriter(coeffFinder(data), outfile);
		eucliddiff = invertCorrelation(eucliddiff);
	}
	
	private double[][] invertCorrelation(double[][] diff) {
		for (int invertx = 0; invertx < diff.length; invertx ++) {
			for (int inverty = 0; inverty < diff.length; inverty ++) {
				
				if (inversionmethod == -1) {
					if (diff[invertx][inverty] != 0) {
						diff[invertx][inverty] = 1 / coeffdiff[invertx][inverty];
					}
				}
				
				else if (inversionmethod == 0) {
					diff[invertx][inverty] = 1 - coeffdiff[invertx][inverty];
				}
				
				else if (inversionmethod == 1) {
					diff[invertx][inverty] = (1 - coeffdiff[invertx][inverty]) / 2;
				}
				
				else {
					System.out.println("ERROR: Did not match a dendrogram inversion method.");
					return coeffdiff;
				}
			}
		}
		return diff;
	}

	private double[][] coeffFinder(int[][] data) {
				
		double[][] correlation = new double[columncount][columncount];
		
		double avg[] = new double[columncount];
		int tempavg = 0;
		
		double xysum = 0;
		double xsum = 0;
		double ysum = 0;
		double xxsum = 0;
		double yysum = 0;
		
		//find avg for each column
		for (int avgcolumn = 0; avgcolumn < columncount; avgcolumn ++) {
			
			for (int avgfind = 0; avgfind < linecount; avgfind ++) {
				tempavg += data[avgfind][avgcolumn];
			}
			avg[avgcolumn] = ((double) tempavg) / linecount;
		}
						
		//calculate Pearson R correlation coefficents
		for (int xcolumn = 0; xcolumn < columncount; xcolumn ++) {
			
			for (int ycolumn = xcolumn; ycolumn < columncount; ycolumn ++) {
				
				if (xcolumn != ycolumn) {
					
					for (int i = 0; i < linecount; i ++) {
						xysum += (data[i][xcolumn] * data[i][ycolumn]);
						xsum += (data[i][xcolumn]);
						ysum += (data[i][ycolumn]);
						xxsum += Math.pow((data[i][xcolumn]), 2);
						yysum += Math.pow((data[i][ycolumn]), 2);
					}
					correlation[xcolumn][ycolumn] = ((linecount * xysum) - (xsum * ysum)) /
							Math.pow(
									(linecount * xxsum - Math.pow(xsum, 2)) *
									(linecount * yysum - Math.pow(ysum, 2)), 
							0.5);
					correlation[ycolumn][xcolumn] = correlation[xcolumn][ycolumn];
					
					xysum = 0;
					xsum = 0;
					ysum = 0;
					xxsum = 0;
					yysum = 0;
				}
				
				else {
					correlation[xcolumn][ycolumn] = (double) 1.0;
				}
			}
		}
			
		return correlation;
	}
	
	private double[][] euclideanFinder(int[][] data) {
		
		double[][] differences = new double[data[0].length][data[0].length];
		int diffsum = 0;
		
		for (int zero = 0; zero < data[0].length; zero ++) {
			differences[zero][zero] = 0;
		}
		for (int sampleA = 0; sampleA < data[0].length; sampleA ++) {
			for (int sampleB = 0; sampleB < data[0].length; sampleB ++) {
				if (sampleA != sampleB) {
					for (int index = 0; index < data.length; index ++) {
						diffsum += Math.pow(data[index][sampleA] - data[index][sampleB], 2);
					}
					differences[sampleA][sampleB] = Math.pow(diffsum, 0.5);
					diffsum = 0;
				}
			}
		}
//		for (double[] jj : differences) {
//			for (double j : jj) {
//				System.out.print(j + "\t");
//			}
//			System.out.println();
//		}
		return differences;
	}
						
	private double[][] fileWriter(double[][] correlation, File outfile) {
				
		try {
			BufferedWriter write = new BufferedWriter(new FileWriter(outfile));
			
			write.write("gene\t");
			
			for (String sample : labels) {
				write.write(sample + "\t");
			}
			
			for (int line = 0; line < columncount; line ++) {
				
				write.write("\n" + labels[line] + "\t");
				
				for (double coefficents : correlation[line]) {
					write.write(coefficents + "\t");
				}
				
			}
			write.close();
		}
		
		catch (IOException e) {
			System.out.println("ERROR: Invalid output file.");
			e.printStackTrace();
		}
		return correlation;
	}

	public String[] getSamples() {
		return labels;
	}
	
	public double[][] getEucliddiff() {
		return eucliddiff;
	}

	public double[][] getCoeffdiff() {
		return coeffdiff;
	}
}
