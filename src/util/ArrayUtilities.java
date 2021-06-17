package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ArrayUtilities {
	
	public static double[] scaleArray(double[] orig, double ratio) {
		double[] Sarray = new double[orig.length];
		for(int x = 0; x < orig.length; x++) {
			Sarray[x] = orig[x] / ratio;
		}
		return Sarray;
	}
	
	public static double[] reverseArray(double[] orig) {
		if(orig != null) {
			double[] reverse = new double[orig.length];		
			for(int x = orig.length - 1; x >= 0; x--) {
				reverse[orig.length - 1 - x] = orig[x];
			}
			return reverse;
		}
		return null;
	}
	
	public static double[] windowSmooth(double[] orig, int win) {
		int window = win / 2;
		double[] Sarray = new double[orig.length];
		for(int x = 0; x < orig.length; x++) {
			double score = 0;
			double weight = 0;
			for(int y = x - window; y <= x + window; y++) {
				if(y < 0) y = -1;
				else if(y < orig.length) {
					score += orig[y];
					weight++;
				} else y = x + window + 1;
			}
			if(weight != 0) Sarray[x] = score / weight;
		}
		return Sarray;
	}
	
	public static double[] gaussSmooth(double[] orig, int size, int num) {
		double[] Garray = new double[orig.length];
		int window = size * num;
		double SDSize = (double)size;
		for(int x = 0; x < orig.length; x++) {
	         double score = 0;
	         double weight = 0;
	         for(int y = x - window; y <= x + window; y++) {
	                if(y < 0) y = -1;
	                else if(y < orig.length) {
	                	double HEIGHT = Math.exp(-1 * Math.pow((y - x), 2) / (2 * Math.pow(SDSize, 2)));
	                	score += (HEIGHT * orig[y]);
	                	weight += HEIGHT;
	                } else y = x + window + 1;
	         }
	         if(weight != 0) Garray[x] = score / weight;
		}
		return Garray;
	 }
	
	public static double getMax(double[] array) {
		double max = 0;
		for(int x = 0; x < array.length; x++) {
			if(!Double.isNaN(array[x])) { if(array[x] > max) { max = array[x]; } }
		}
		return max;
	}
	
	public static double getMin(double[] array) {
		double min = Double.NaN;
		for(int x = 0; x < array.length; x++) {
			if(!Double.isNaN(array[x])) {
				if(Double.isNaN(min)) { min = array[x]; }
				else if(array[x] < min) { min = array[x]; }
			}
		}
		return min;
	}
	
	public static double getMedian(double[] array) {
		ArrayList<Double> values = new ArrayList<Double>();
		for(int x = 0; x < array.length; x++) {
			if(!Double.isNaN(array[x])) { values.add(array[x]);	}
		}
		if(values.size() > 0) {
			Collections.sort(values);
		    double pos1 = Math.floor((values.size() - 1.0) / 2.0);
		    double pos2 = Math.ceil((values.size() - 1.0) / 2.0);
		      if (pos1 == pos2 ) { return values.get((int)pos1); }
		      else { return (values.get((int)pos1) + values.get((int)pos2)) / 2.0 ; }
		} else { return Double.NaN; }
	}
	
	public static double getAverage(double[] array) {
		double average = 0;
		int count = 0;
		for(int x = 0; x < array.length; x++) {
			if(!Double.isNaN(array[x])) {
				average += array[x];
				count++;
			}
		}
		if(count != 0) return (average / count);
		else return Double.NaN;		
	}
	
	public static double getSum(double[] array) {
		double sum = 0;
		int count = 0;
		for(int x = 0; x < array.length; x++) {
			if(!Double.isNaN(array[x])) {
				sum += array[x];
				count++;
			}
		}
		if(count != 0) return sum;
		else return Double.NaN;		
	}
	
	public static double getMode(double[] array) {
		double mode = 0;
		int modecount = 0;
		Map<Double, Integer> count = new HashMap<Double, Integer>();
		for(int x = 0; x < array.length; x++) {
			if(!Double.isNaN(array[x])) {
				Integer n = count.get(array[x]);
				if(n == null) {
					count.put(array[x], 1);
				} else {
					count.put(array[x], n + 1);
				}
			}
		}
		if(count.isEmpty()) { return Double.NaN; }
		Set<Double> keys = count.keySet();
		for(Double key : keys) {
			if(count.get(key) > modecount) {
				modecount = count.get(key);
				mode = key;
				}
		}
		return mode;
	}
	
	public static double getPositionalVariance(double[] array) {
		ArrayList<Double> ref = new ArrayList<Double>();
		double AVG = 0;
		double N = 0;
		for(int x = 0; x < array.length; x++) {
			if(!Double.isNaN(array[x])) {
				for(int z = 0; z < array[x]; z++) {
					ref.add(Double.valueOf(x));
					AVG += x;
					N++;
				}
			}
		}
		AVG /= N;

		double VAR = Double.NaN;
		if(N > 1) {
			double SS = 0;
			for(int x = 0; x < ref.size(); x++) {
				SS += Math.pow(Math.abs(ref.get(x).doubleValue() - AVG) ,2);
			}
			VAR = Math.sqrt(SS / N) * Math.sqrt(N / (N - 1));
		} else if (N > 0) { VAR = 0; }
		return VAR;
	}
}