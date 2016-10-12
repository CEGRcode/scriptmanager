package scripts.Cluster_Scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import util.SimilarityMetric;

public class CorrelationMatrix {
	
	private File INPUT = null;
	private File OUT_PATH = null;
	private PrintStream OUTPUT = null;
	private int METRIC = 0;	
	
	public CorrelationMatrix(File in, File out_path, int m) {
		INPUT = in;
		OUT_PATH = out_path;
		METRIC = m;
	}
	
	public void run() {
		String[] name =INPUT.getName().split("\\.");
		String NEWNAME = "";
		for(int x = 0; x < name.length - 1; x++) {
			if(x == name.length - 2) { NEWNAME += (name[x] + "_RAND.fa"); }
			else { NEWNAME += (name[x] + "."); }
		}
		
		//Open Output File
		if(OUT_PATH != null) {
			try {
				OUTPUT = new PrintStream(new File(OUT_PATH.getCanonicalPath() + File.separator + NEWNAME + "_MATRIX.out"));
			} catch (FileNotFoundException e) { e.printStackTrace(); }
			catch (IOException e) {	e.printStackTrace(); }
		}
		
		SimilarityMetric corr = new SimilarityMetric();
		if(METRIC == 0) { corr.setType("pearson"); }
		else if(METRIC == 1) { corr.setType("reflective"); }
		else if(METRIC == 2) { corr.setType("spearman"); }
		else if(METRIC == 3) { corr.setType("euclidean"); }
		else if(METRIC == 4) { corr.setType("manhattan"); }

		ArrayList<double[]> MATRIX = loadMatrix(INPUT);
		
		
		
		OUTPUT.close();
	}
	
	public ArrayList<double[]> loadMatrix(File in) {
		ArrayList<double[]> matrix = new ArrayList<double[]>();
		
		return matrix;
	}
}
