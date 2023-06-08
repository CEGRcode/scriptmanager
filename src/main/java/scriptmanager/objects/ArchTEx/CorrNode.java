package scriptmanager.objects.ArchTEx;

import java.util.Vector;

/**
 * Object to help store ArchTEx Cross-Correlation analysis values.
 * <br>
 * Code largely sourced from ArchTEx.analysis.corr.CorrNode in <a href=
 * "https://github.com/WilliamKMLai/ArchTEx">https://github.com/WilliamKMLai/ArchTEx</a>
 * 
 * @author William KM Lai
 * @see scriptmanager.objects.ArchTEx.CorrExtract
 * @see scriptmanager.scripts.BAM_Statistics.CrossCorrelation
 */
public class CorrNode {
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
	private double[] Sxy = new double[1001];
	private double counter = 0;

	public CorrNode () {
		
	}

	public CorrNode (String chr, int start, int stop) {
		CHROM = chr;
		BP_START = start;
		BP_STOP = stop;
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

	public void setSxy(int index, double newsxy) {
		Sxy[index] = newsxy;
	}

	public void setSxy(double[] newsxy) {
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

	public double[] getSxy() {
		return Sxy;
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