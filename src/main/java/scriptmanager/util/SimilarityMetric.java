package util;

import java.text.DecimalFormat;

public class SimilarityMetric {
	String metrictype = "";
	
	public SimilarityMetric() {
		metrictype = "pearson";
	}
	
	public SimilarityMetric(String type) {
		metrictype = type;
	}
	
	public void setType(String type) {
		metrictype = type;
	}
	
	public String getType() {
		return metrictype;
	}
		
	public double getScore(double[] x, double[] y) {
		double[] arrayx = x;
		double[] arrayy = y;
		
		if(metrictype.equals("pearson")) {
			return Pearson(arrayx, arrayy);
		} else if(metrictype.equals("reflective")) {
			return Euclidean(arrayx, arrayy);
		} else if(metrictype.equals("spearman")) {
			return Spearman(arrayx, arrayy);
		} else if(metrictype.equals("euclidean")) {
			return Euclidean(arrayx, arrayy);
		} else if(metrictype.equals("manhattan")) {
			return Manhattan(arrayx, arrayy);
		}
		return 0;
	}
	
	public double Pearson(double[] arrayx, double[] arrayy) {
		double xmean = 0, ymean = 0;
		int arraycount = 0;
		for(int a = 0; a < arrayx.length; a++) {
			if(!Double.isNaN(arrayx[a]) && !Double.isNaN(arrayy[a])) {
				xmean += arrayx[a];
				ymean += arrayy[a];
				arraycount++;
			}
		}
		xmean /= arraycount;
		ymean /= arraycount;
		
		double t1 = 0, t2 = 0, xv = 0, yv = 0, s = 0;
		double correlation;
		for(int a = 0; a < arrayx.length; a++) {
			if(!Double.isNaN(arrayx[a]) && !Double.isNaN(arrayy[a])) {
				t1 = arrayx[a] - xmean;
				t2 = arrayy[a] - ymean;
				xv += Math.pow(t1, 2);
				yv += Math.pow(t2, 2);
				s += (t1 * t2);
			}
		}
		if (xv == 0 || yv == 0) { correlation = 0; }
		else { correlation = (s / (Math.sqrt(xv) * Math.sqrt(yv))); } 
		DecimalFormat df = new DecimalFormat("#.############");
		correlation = Double.parseDouble(df.format(correlation));
		return correlation;
	}
	
	private double Euclidean(double[] arrayx, double[] arrayy) {
		double distance = 0;
		for(int i = 0; i < arrayx.length; i++) {
			if(!Double.isNaN(arrayx[i]) && !Double.isNaN(arrayy[i])) { distance += Math.pow(arrayx[i] - arrayy[i], 2); }
		}
		return Math.sqrt(distance);
	}
	
	private double Manhattan(double[] arrayx, double[] arrayy) {
		double distance = 0;
		for(int i = 0; i < arrayx.length; i++) {
			if(!Double.isNaN(arrayx[i]) && !Double.isNaN(arrayy[i])) { distance += Math.abs(arrayx[i] - arrayy[i]); }
		}
		return distance;
	}
	
	private double Spearman(double[] arrayx, double[] arrayy) {
		double[] rankX = SpearRank(arrayx);
		double[] rankY = SpearRank(arrayy);
		return Pearson(rankX, rankY);
	}
	
	private double[] SpearRank(double[] x) {
		double[] newx = new double[x.length];
		int j, k, t, tmpi;
        double tmp;

        double[] r = new double[x.length];
        int[] c = new int[x.length];

        if(x.length <= 1) { newx[0] = 1; return newx; }
        for(int i = 0; i < x.length; i++) {
        	r[i] = x[i];
            c[i] = i;
        }
        for(int i = 2; i < x.length; i++) {
            t = i;
            while (t != 1) {
            	k = t / 2;
                if (r[k - 1] >= r[t - 1]) {
                	t = 1;
                } else {
                	tmp = r[k - 1];
                    r[k - 1] = r[t - 1];
                    r[t - 1] = tmp;
                    tmpi = c[k - 1];
                    c[k - 1] = c[t - 1];
                    c[t - 1] = tmpi;
                    t = k;
                	}
            }
        }
        for(int i = x.length - 1; i > 1; i--) {
        	tmp = r[i];
            r[i] = r[0];
            r[0] = tmp;
            tmpi = c[i];
            c[i] = c[0];
            c[0] = tmpi;
            t = 1;
            while (t != 0) {
            	k = 2 * t;
            	if (k > i) { t = 0; }
                else {
                	if (k < i) {
                		if (r[k] > r[k - 1]) { k++; }
                    }
                	if (r[t - 1] >= r[k - 1]) { t = 0; }
                    else {
                    	tmp = r[k - 1];
                        r[k - 1] = r[t - 1];
                        r[t - 1] = tmp;
                        tmpi = c[k - 1];
                        c[k - 1] = c[t - 1];
                        c[t - 1] = tmpi;
                        t = k;
                    }
                }
            }
        }
        int i = 0;
        while (i < x.length) {
        	j = i + 1;
        	while (j < x.length) {
        		if (r[j] != r[i]) { break; }
        		j++;
        	}
        	for (k = i; k <= j - 1; k++) {
        		r[k] = 1 + (double)(i + j - 1) / (double)(2);
        	}
        	i = j;
        }

		for (i = 0; i < x.length; i++) {
			newx[c[i]] = r[i];
		}
		return newx;
	}
	
	public String toString() {
		return metrictype;
	}
}