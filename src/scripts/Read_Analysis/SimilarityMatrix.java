package scripts.Read_Analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import util.SimilarityMetric;

public class SimilarityMatrix {
	
	private File INPUT = null;
	private File OUT_PATH = null;
	private int METRIC = 0;	
	private boolean COLUMN = true;
	
	public SimilarityMatrix(File in, File out_path, int m, boolean col) {
		INPUT = in;
		OUT_PATH = out_path;
		METRIC = m;
		COLUMN = col;
	}
	
	public void run() throws IOException {
		ArrayList<double[]> DATA = new ArrayList<double[]>();
		ArrayList<String> ID = new ArrayList<String>();	
		
		Scanner scan = new Scanner(INPUT);
		int counter = 0;
		boolean LOADFAIL = false;
		int columnsize = 0;		
		while (scan.hasNextLine()) {
			String[] temp = scan.nextLine().split("\t");
			if(counter == 0) {
				columnsize = temp.length;
				if(COLUMN) { for(int x = 1; x < temp.length; x++) { ID.add(temp[x]); } }
			} else {
				if(columnsize != temp.length) {
					LOADFAIL = true;
					scan.close();
				} 
				if(!COLUMN) { ID.add(temp[0]); }
				double[] score = new double[columnsize - 1];
				for(int x = 1; x < columnsize; x++) {
					if(temp[x].isEmpty()) {
						LOADFAIL = true;
						scan.close();
					} else {
						score[x - 1] = Double.parseDouble(temp[x]);
					}
				}
				DATA.add(score);
			}			
			counter++;
	    }
		scan.close();
						
		if(COLUMN) {
			ArrayList<double[]> newDATA = new ArrayList<double[]>();
			for(int x = 0; x < ID.size(); x++) {
				double[] array = new double[DATA.size()];
				for(int y = 0; y < DATA.size(); y++) {
					array[y] = DATA.get(y)[x];
				}
				newDATA.add(array);
			}
			DATA = newDATA;
		}
		
		if(LOADFAIL) {
			outputMatrixLoadFail();
			System.out.println(INPUT.getName() + "\tLoad failed due to non-numeric values in matrix");
		} else {
			double[][] MATRIX = calculateMatrix(DATA);
			outputMatrix(MATRIX, ID);
		}
	}
	
	public double[][] calculateMatrix(ArrayList<double[]> data) {
		SimilarityMetric corr = new SimilarityMetric();
		if(METRIC == 0) { corr.setType("pearson"); }
		else if(METRIC == 1) { corr.setType("reflective"); }
		else if(METRIC == 2) { corr.setType("spearman"); }
		else if(METRIC == 3) { corr.setType("euclidean"); }
		else if(METRIC == 4) { corr.setType("manhattan"); }
		
		double[][] matrix = new double[data.size()][data.size()];
		for(int x = 0; x < data.size(); x++) {
			for(int y = x; y < data.size(); y++) {
				double score = corr.getScore(data.get(x), data.get(y));
				matrix[x][y] = score;
				matrix[y][x] = score;
			}
		}
		return matrix;
	}
	
	public void outputMatrixLoadFail() throws FileNotFoundException, IOException {
		String[] name = INPUT.getName().split("\\.");
		String NEWNAME = "";
		for(int x = 0; x < name.length - 1; x++) {
			if(x == name.length - 2) { NEWNAME += (name[x]); }
			else { NEWNAME += (name[x] + "."); }
		}
		
		//Open Output File
		PrintStream OUT = null;
		if(OUT_PATH != null) { OUT = new PrintStream(new File(OUT_PATH.getCanonicalPath() + File.separator + NEWNAME + "_SIMMATRIX.out"));
		} else { OUT = new PrintStream(new File(NEWNAME + "_SIMMATRIX.out")); }
		OUT.println(INPUT.getName() + "\tLoad failed due to non-numeric values in matrix");
		OUT.close();
	}
	
	public void outputMatrix(double[][] matrix, ArrayList<String> id) throws IOException {
		String[] name = INPUT.getName().split("\\.");
		String NEWNAME = "";
		for(int x = 0; x < name.length - 1; x++) {
			if(x == name.length - 2) { NEWNAME += (name[x]); }
			else { NEWNAME += (name[x] + "."); }
		}
			
		//Open Output File
		PrintStream OUT = null;
		if(OUT_PATH != null) { OUT = new PrintStream(new File(OUT_PATH.getCanonicalPath() + File.separator + NEWNAME + "_SIMMATRIX.out"));
		} else { OUT = new PrintStream(new File(NEWNAME + "_SIMMATRIX.out")); }
		
		for(int x = 0; x < id.size(); x++) { OUT.print("\t" + id.get(x)); }
		OUT.println();
		for(int x = 0; x < matrix.length; x++) {
			OUT.print(id.get(x));
			for(int y = 0; y < matrix.length; y++) { OUT.print("\t" + matrix[x][y]); }
			OUT.println();
		}
		OUT.close();
	}
}
