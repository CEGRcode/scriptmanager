package objects.CoordinateObjects;

import java.util.Comparator;

import objects.CoordinateObjects.GenomicCoord;

public class BEDCoord implements GenomicCoord {
	private String CHROM = "";
	private int START = 0;
	private int STOP = 0;
	private String DIR = "+";
	
	private String NAME = ".";
	private double SCORE = 0;

	private int MID = 0;
	
	private double[] Fstrand = null;
	private double[] Rstrand = null;
	
	public BEDCoord() {
		
	}
	
	public BEDCoord(String line) {
		String[] bed = line.split("\t");
		if(bed.length > -1) CHROM = bed[0];
		if(bed.length > 0) START = Integer.parseInt(bed[1]);
		if(bed.length > 1) STOP = Integer.parseInt(bed[2]);
		if(bed.length > 2) NAME = bed[3];
		if(bed.length > 3) {
			try { SCORE = Double.parseDouble(bed[4]); }
			catch (NumberFormatException e) { SCORE = 0; }
		}
		if(bed.length > 4) DIR = bed[5];
	}
	
	
	public BEDCoord(String c, int sta, int sto, String di, String na) {
		CHROM = c;
		START = sta;
		STOP = sto;
		DIR = di;
		NAME = na;
	}
	
	public BEDCoord(String na, double sco) {
		NAME = na;
		SCORE = sco;
	}
	
	public void calcMid() {
		MID = (START + STOP) / 2;
	}
	
	public void setMid(int m) {
		MID = m;
	}
	
	public int getMid() {
		return MID;
	}
	
	public double[] getFStrand() {
		return Fstrand;
	}
	
	public double[] getRStrand() {
		return Rstrand;
	}
	
	public void setFstrand(double[] f) {
		Fstrand = f;
	}
	
	public void setRstrand(double[] r) {
		Rstrand = r;
	}
	
	public String getChrom() {
		return CHROM;
	}
	
	public void setChrom(String chr) {
		CHROM = chr;
	}
	
	public int getStart() {
		return START;
	}
	
	public void setStart(int sta) {
		START = sta;
	}
	
	public int getStop() {
		return STOP;
	}
	
	public void setStop(int sto) {
		STOP = sto;
	}
	
	public String getDir() {
		return DIR;
	}
	
	public void setDir(String di) {
		DIR = di;
	}
	
	public String getName() {
		return NAME;
	}
	
	public void setName(String na) {
		NAME = na;
	}
	
	public double getScore() {
		return SCORE;
	}
	
	public void setScore(double sco) {
		SCORE = sco;
	}
	
	public String toString() {
		String line = CHROM + "\t" + START + "\t" + STOP + "\t" + NAME + "\t" + SCORE + "\t" + DIR;
		return line;
	}
	
	public static Comparator<BEDCoord> PeakChromComparator = new Comparator<BEDCoord>() {
		public int compare(BEDCoord node1, BEDCoord node2) {
			return node1.getChrom().compareTo(node2.getChrom());
	}
	};
		
	public static Comparator<BEDCoord> PeakPositionComparator = new Comparator<BEDCoord>() {
		public int compare(BEDCoord node1, BEDCoord node2) {
			int PeakStart1 = node1.getStart();
			int PeakStart2 = node2.getStart();
			if (PeakStart1 > PeakStart2) return 1;
			else if (PeakStart1 < PeakStart2) return -1;
			else return 0;
	}
	};
	
	public static Comparator<BEDCoord> PeakMidpointComparator = new Comparator<BEDCoord>() {
		public int compare(BEDCoord node1, BEDCoord node2) {
			int PeakMid1 = (node1.getStart() + node1.getStop()) / 2;
			int PeakMid2 = (node2.getStart() + node2.getStop()) / 2;
			if (PeakMid1 > PeakMid2) return 1;
			else if (PeakMid1 < PeakMid2) return -1;
			else return 0;
	}
	};
	
	public static Comparator<BEDCoord> ScoreComparator = new Comparator<BEDCoord>() {
		public int compare(BEDCoord node1, BEDCoord node2) {
			double PeakStart1 = node1.getScore();
			double PeakStart2 = node2.getScore();
			if (PeakStart1 < PeakStart2) return 1;
			else if (PeakStart1 > PeakStart2) return -1;
			else return 0;
	}
	};
}
