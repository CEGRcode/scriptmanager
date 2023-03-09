package scriptmanager.objects.CoordinateObjects;

import java.util.Comparator;

public class GFFCoord implements GenomicCoord {
	private String CHROM = "";
	private String SOURCE = ".";
	private String FEATURE = ".";
	private long START = 0;
	private long STOP = 0;
	private double SCORE = 0;
	private String DIR = "+";
	private String FRAME = ".";
	private String ATTRIBUTE = ".";
	
	public String LINE = "";

	private double[] Fstrand = null;
	private double[] Rstrand = null;
	
	public GFFCoord() {
		
	}
	
	public GFFCoord(String line) {
		String[] gff = line.split("\t");
		if(gff.length > -1) CHROM = gff[0];
		if(gff.length > 0) SOURCE = gff[1];
		if(gff.length > 1) FEATURE = gff[2];
		if(gff.length > 2) START = Integer.parseInt(gff[3]);
		if(gff.length > 3) STOP = Integer.parseInt(gff[4]);
		if(gff.length > 4) {
			try { SCORE = Double.parseDouble(gff[5]); }
			catch (NumberFormatException e) { SCORE = 0; }
		}
		if(gff.length > 5) DIR = gff[6];
		if(gff.length > 6) FRAME = gff[7];
		if(gff.length > 7) ATTRIBUTE = gff[8];
		LINE = line;
	}
	
	public GFFCoord(String c, long sta, long sto, String di, String na) {
		CHROM = c;
		START = sta;
		STOP = sto;
		DIR = di;
		ATTRIBUTE = na;
	}
	
	public GFFCoord(String c, long sta, long sto, String di) {
		CHROM = c;
		START = sta;
		STOP = sto;
		DIR = di;
		ATTRIBUTE = CHROM + "_" + START + "_" + STOP + "_" + DIR;
	}
	
	public GFFCoord(String na, double sco) {
		ATTRIBUTE = na;
		SCORE = sco;
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
	
	public long getStart() {
		return START;
	}
	
	public void setStart(int sta) {
		START = sta;
	}
	
	public long getStop() {
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
		return ATTRIBUTE;
	}
	
	public void setName(String na) {
		ATTRIBUTE = na;
	}
	
	public double getScore() {
		return SCORE;
	}
	
	public void setScore(double sco) {
		SCORE = sco;
	}
	
	public String toString() {
		//chr1	cwpair	.	45524	45525	3067.0	.	.	cw_distance=26
		String line = CHROM + "\t" + SOURCE + "\t" + FEATURE + "\t" + START + "\t" + STOP + "\t" + SCORE + "\t" + DIR + "\t" + FRAME + "\t" + ATTRIBUTE;
		return line;
	}
	
	public static Comparator<GFFCoord> PeakChromComparator = new Comparator<GFFCoord>() {
		public int compare(GFFCoord node1, GFFCoord node2) {
			return node1.getChrom().compareTo(node2.getChrom());
	}
	};
		
	public static Comparator<GFFCoord> PeakPositionComparator = new Comparator<GFFCoord>() {
		public int compare(GFFCoord node1, GFFCoord node2) {
			long PeakStart1 = node1.getStart();
			long PeakStart2 = node2.getStart();
			if (PeakStart1 > PeakStart2) return 1;
			else if (PeakStart1 < PeakStart2) return -1;
			else return 0;
	}
	};
	
	public static Comparator<GFFCoord> ScoreComparator = new Comparator<GFFCoord>() {
		public int compare(GFFCoord node1, GFFCoord node2) {
			double PeakStart1 = node1.getScore();
			double PeakStart2 = node2.getScore();
			if (PeakStart1 < PeakStart2) return 1;
			else if (PeakStart1 > PeakStart2) return -1;
			else return 0;
	}
	};
}
