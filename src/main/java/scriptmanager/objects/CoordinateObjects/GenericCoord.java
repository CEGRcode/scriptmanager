package scriptmanager.objects.CoordinateObjects;

import java.util.Comparator;

public class GenericCoord implements GenomicCoord {
	private String CHROM = "";
	private long START = 0;
	private long STOP = 0;
	private String DIR = "+";
	
	private String NAME = ".";
	private double SCORE = 0;

	public GenericCoord() {
		
	}
	
	public GenericCoord(String c, long sta, long sto, String di) {
		CHROM = c;
		START = (int)sta;
		STOP = (int)sto;
		DIR = di;
		NAME = CHROM + "_" + START + "_" + STOP + "_" + DIR;
	}
	
	public GenericCoord(String c, int sta, int sto, String di, String na) {
		CHROM = c;
		START = sta;
		STOP = sto;
		DIR = di;
		NAME = na;
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
	
	public static Comparator<GenericCoord> PeakChromComparator = new Comparator<GenericCoord>() {
		public int compare(GenericCoord node1, GenericCoord node2) {
			return node1.getChrom().compareTo(node2.getChrom());
	}
	};
		
	public static Comparator<GenericCoord> PeakPositionComparator = new Comparator<GenericCoord>() {
		public int compare(GenericCoord node1, GenericCoord node2) {
			long PeakStart1 = node1.getStart();
			long PeakStart2 = node2.getStart();
			if (PeakStart1 > PeakStart2) return 1;
			else if (PeakStart1 < PeakStart2) return -1;
			else return 0;
	}
	};
	
	public static Comparator<GenericCoord> ScoreComparator = new Comparator<GenericCoord>() {
		public int compare(GenericCoord node1, GenericCoord node2) {
			double PeakStart1 = node1.getScore();
			double PeakStart2 = node2.getScore();
			if (PeakStart1 < PeakStart2) return 1;
			else if (PeakStart1 > PeakStart2) return -1;
			else return 0;
	}
	};
}
