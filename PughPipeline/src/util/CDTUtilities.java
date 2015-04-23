package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

public class CDTUtilities {
	public static Vector<double[]> loadCDT(File input) throws FileNotFoundException {
		Vector<double[]> matrix = new Vector<double[]>();
		Scanner scan = new Scanner(input);
		while (scan.hasNextLine()) {
			String[] temp = scan.nextLine().split("\t");
			if(!temp[0].contains("YORF")) {
				double[] ARRAY = new double[temp.length - 2];
				for(int x = 0; x < ARRAY.length; x++) {
					ARRAY[x] = Double.parseDouble(temp[x + 2]);
				}
				matrix.add(ARRAY);
			}
		}		
		return matrix;
	}
	
	public static double[] getComposite(Vector<double[]> CDT) {
		double[] AVG = new double[CDT.get(0).length];
		double COUNT = 0;
		for(int x = 0; x < CDT.size(); x++) {
			for(int y = 0; y < CDT.get(x).length; y++) {
				AVG[y] += CDT.get(x)[y];
			}
			COUNT++;
		}
		
		for(int x = 0; x < AVG.length; x++) { AVG[x] /= COUNT; }
		return AVG;
	}
	
	public static ArrayList<Double> getStats(Vector<double[]> CDT) {
		ArrayList<Double> STATS = new ArrayList<Double>();
		ArrayList<Double> values = new ArrayList<Double>();
		
		double max = 0, min = 0, average = 0, median = 0, mode = 0;
		int count = 0, modecount = 0;
		Map<Double, Integer> mode_hash = new HashMap<Double, Integer>();

		for(int x = 0; x < CDT.size(); x++) {
			for(int y = 0; y < CDT.get(x).length; y++) {
				if(CDT.get(x)[y] != 0) {
					if(CDT.get(x)[y] > max) { max = CDT.get(x)[y]; }
					if(min == 0) { min = CDT.get(x)[y]; }
					else if(CDT.get(x)[y] < min) { min = CDT.get(x)[y]; }
					
					average += CDT.get(x)[y];
					count++;
					values.add(CDT.get(x)[y]);

					Integer n = mode_hash.get(CDT.get(x)[y]);
					if(n == null) { mode_hash.put(CDT.get(x)[y], 1); }
					else { mode_hash.put(CDT.get(x)[y], n + 1); }
				}
			}
		}
		Collections.sort(values);

		if(count != 0) { average /= count; }

		double pos1 = Math.floor((values.size() - 1.0) / 2.0);
	    double pos2 = Math.ceil((values.size() - 1.0) / 2.0);
	    if (pos1 == pos2 ) { median = values.get((int)pos1); }
	    else { median = (values.get((int)pos1) + values.get((int)pos2)) / 2.0 ; }

	    Set<Double> keys = mode_hash.keySet();
		for(Double key : keys) {
			if(mode_hash.get(key) > modecount) {
				modecount = mode_hash.get(key);
				mode = key;
				}
		}
		
		STATS.add(min);
		STATS.add(max);
		STATS.add(average);
		STATS.add(median);
		STATS.add(mode);
		return STATS;
	}
	
	public static Double getMax(Vector<double[]> CDT) {
		double max = 0;
		for(int x = 0; x < CDT.size(); x++) {
			for(int y = 0; y < CDT.get(x).length; y++) {
				if(CDT.get(x)[y] != 0) {
					if(CDT.get(x)[y] > max) { max = CDT.get(x)[y]; }
				}
			}
		}
		return max;
	}
	
	public static Double getMin(Vector<double[]> CDT) {
		double min = 0;
		for(int x = 0; x < CDT.size(); x++) {
			for(int y = 0; y < CDT.get(x).length; y++) {
				if(CDT.get(x)[y] != 0) {
					if(min == 0) { min = CDT.get(x)[y]; }
					else if(CDT.get(x)[y] < min) { min = CDT.get(x)[y]; }
				}
			}
		}
		return min;
	}
	
	public static Double getMedian(Vector<double[]> CDT) {
		ArrayList<Double> values = new ArrayList<Double>();
		for(int x = 0; x < CDT.size(); x++) {
			for(int y = 0; y < CDT.get(x).length; y++) {
				if(CDT.get(x)[y] != 0) {
					values.add(CDT.get(x)[y]);
				}
			}
		}
		Collections.sort(values);

	    double pos1 = Math.floor((values.size() - 1.0) / 2.0);
	    double pos2 = Math.ceil((values.size() - 1.0) / 2.0);
	      if (pos1 == pos2 ) {
	         return values.get((int)pos1);
	      } else {
	         return (values.get((int)pos1) + values.get((int)pos2)) / 2.0 ;
	      }
	}
	
	public static Double getAverage(Vector<double[]> CDT) {
		double average = 0;
		int count = 0;
		for(int x = 0; x < CDT.size(); x++) {
			for(int y = 0; y < CDT.get(x).length; y++) {
				if(CDT.get(x)[y] != 0) {
					average += CDT.get(x)[y];
					count++;
				}
			}
		}
		if(count != 0) return (average / count);
		else return 0.0;
	}
	
	public static Double getMode(Vector<double[]> CDT) {
		double mode = 0;
		int modecount = 0;
		Map<Double, Integer> count = new HashMap<Double, Integer>();
		for(int x = 0; x < CDT.size(); x++) {
			for(int y = 0; y < CDT.get(x).length; y++) {
				if(CDT.get(x)[y] != 0) {
					Integer n = count.get(CDT.get(x)[y]);
					if(n == null) {
						count.put(CDT.get(x)[y], 1);
					} else {
						count.put(CDT.get(x)[y], n + 1);
					}
				}
			}
		}
		Set<Double> keys = count.keySet();
		for(Double key : keys) {
			if(count.get(key) > modecount) {
				modecount = count.get(key);
				mode = key;
				}
		}
		return mode;
	}
}
