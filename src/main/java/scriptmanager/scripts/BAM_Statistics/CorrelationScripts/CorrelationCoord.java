package scripts.BAM_Statistics.CorrelationScripts;

import java.util.Vector;

public class CorrelationCoord {
	private String uniqID = "";
	private String CHROM = "";
	private int BP_START = 0;
	private int BP_STOP = 0;
	private Vector<double[]> tagPopulation;
	
	//Statistics
	private double Sx = 0;
	private double Sxx = 0;
	private double Sy = 0;
	private double Syy = 0;
	private double Sxy = 0;
	private double counter = 0;
	
	public CorrelationCoord () {
		tagPopulation = new Vector<double[]>();
	}

	public CorrelationCoord (String chr, int start, int stop) {
		CHROM = chr;
		BP_START = start;
		BP_STOP = stop;
		tagPopulation = new Vector<double[]>();
	}
	
	public void setCount(double count) {
		counter = count;
	}
	
	public void setSx(double newsx) {
		Sx = newsx;
	}
	
	public void setSxx(double newsxx) {
		Sxx = newsxx;
	}
	
	public void setSy(double newsy) {
		Sy = newsy;
	}
	
	public void setSyy(double newsyy) {
		Syy = newsyy;
	}
	
	public void setSxy(double newsxy) {
		Sxy = newsxy;
	}
	
	public double getCount() {
		return counter;
	}
	
	public double getSx() {
		return Sx;
	}
	
	public double getSxx() {
		return Sxx;
	}
	
	public double getSy() {
		return Sy;
	}
	
	public double getSyy() {
		return Syy;
	}
	
	public double getSxy() {
		return Sxy;
	}
	
	public void addData(double[] tags) {
		tagPopulation.add(tags);
	}
	
	public void setData(Vector<double[]> tags) {
		tagPopulation = tags;
	}
	
	public Vector<double[]> getData() {
		return tagPopulation;
	}
	
	public String getID() {
		return uniqID;		
	}
	
	public void setID(String newid) {
		uniqID = newid;		
	}
	
	public String getChrom() {
		return CHROM;		
	}
	
	public void setChrom(String newchr) {
		CHROM = newchr;		
	}
		
	public int getStop() {
		return BP_STOP;		
	}
	
	public void setStop(int newbp) {
		BP_STOP = newbp;		
	}
	
	public int getStart() {
		return BP_START;		
	}
	
	public void setStart(int newbp) {
		BP_START = newbp;		
	}
}
