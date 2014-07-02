package objects;

import java.util.Comparator;

public class Peak {
	private String CHROM = "";
	private int BP = -1;
	private int TAG = -1;
	private double STDDEV = -1;
	private int START = -1;
	private int STOP = -1;
	private String DIR = "*";
	
	public Peak(String chr, int start, int stop, String dir, int t) {
		CHROM = chr;
		START = start;
		STOP = stop;
		DIR = dir;
		TAG = t;
	}
	
	public String getDir() {
		return DIR;
	}
	
	public void setDir(String dir) {
		DIR = dir;
	}
	
	public String getChrom() {
		return CHROM;
	}
	
	public int getBP() {
		return BP;
	}
	
	public int getTag() {
		return TAG;
	}
	
	public double getStd() {
		return STDDEV;
	}
	
	public int getStart() {
		return START;
	}
	
	public int getStop() {
		return STOP;
	}
	
	public void setChrom(String c) {
		CHROM = c;
	}
	
	public void setBP(int b) {
		BP = b;
	}
	
	public void setTag(int t) {
		TAG = t;
	}
	
	public void setStd(double s) {
		STDDEV = s;
	}
	
	public void setStart(int s) {
		START = s;
	}
	
	public void setStop(int s) {
		STOP = s;
	}
	
	public String toString() {
//		chr4    genetrack       .       51549   51559   48      +       .       stddev=1.46115797876
		String name = CHROM + "\tgenetrack\t.\t" + START + "\t" + STOP + "\t" + TAG + "\t" + DIR + "\t.\tstddev=" + STDDEV; 
		return name;
	}
		
	public static Comparator<Peak> PeakTagComparator = new Comparator<Peak>() {
		public int compare(Peak node1, Peak node2) {
			int PeakScore1 = node1.getTag();
			int PeakScore2 = node2.getTag();
			if (PeakScore1 < PeakScore2) return 1;
			else if (PeakScore1 > PeakScore2) return -1;
			else return 0;
	}
	};
}
