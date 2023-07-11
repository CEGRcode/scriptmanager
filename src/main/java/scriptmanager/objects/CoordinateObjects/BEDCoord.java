package scriptmanager.objects.CoordinateObjects;

import java.util.Comparator;

/**
 * Object for storing lines of a BED file
 * @see 
 */
public class BEDCoord implements GenomicCoord {
	private String CHROM = "";
	private long START = 0;
	private long STOP = 0;
	private String DIR = "+";
	
	private String NAME = ".";
	private double SCORE = 0;

	private long MID = 0;
	
	private double[] Fstrand = null;
	private double[] Rstrand = null;
	
	public BEDCoord() {
		
	}
	
	/**
	 * Creates a new BEDCoord object with a given String
	 * @param line String to parse in order to create BEDCoord file
	 */
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
	
	/**
	 * Creates a new BEDCoord object
	 * @param c Chromosome name
	 * @param sta Start coordinate 
	 * @param sto Ending coordinate
	 * @param di Strand direction (+/-)
	 * @param na Name of coord
	 */
	public BEDCoord(String c, long sta, long sto, String di, String na) {
		CHROM = c;
		START = sta;
		STOP = sto;
		DIR = di;
		NAME = na;
	}
	
	/**
	 * Creates a new BEDCoord object
	 * @param c Chromosome name
	 * @param sta Start coordinate 
	 * @param sto Ending coordinate
	 * @param di Strand direction (+/-)
	 */
	public BEDCoord(String c, long sta, long sto, String di) {
		CHROM = c;
		START = sta;
		STOP = sto;
		DIR = di;
		NAME = CHROM + "_" + START + "_" + STOP + "_" + DIR;
	}
	
	/**
	 * Creates a new BEDCoord object with only a name and score
	 * @param na Name of coord
	 * @param sco Score
	 */
	public BEDCoord(String na, double sco) {
		NAME = na;
		SCORE = sco;
	}
	
	/**
	 * Calculates the middle position of a BED coordinate pair
	 */
	public void calcMid() {
		MID = (START + STOP) / 2;
	}
	
	/**
	 * Sets the middle position of a BED coordinate pair
	 * @param m New middle position
	 */
	public void setMid(int m) {
		MID = m;
	}
	
	/**
	 * Returns the middle position of a BED coordinate pair
	 * @return The middle position of a BED coordinate pair
	 */
	public long getMid() {
		return MID;
	}
	
	/**
	 * 
	 * @return
	 */
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
			long PeakStart1 = node1.getStart();
			long PeakStart2 = node2.getStart();
			if (PeakStart1 > PeakStart2) return 1;
			else if (PeakStart1 < PeakStart2) return -1;
			else return 0;
	}
	};
	
	public static Comparator<BEDCoord> PeakMidpointComparator = new Comparator<BEDCoord>() {
		public int compare(BEDCoord node1, BEDCoord node2) {
			long PeakMid1 = (node1.getStart() + node1.getStop()) / 2;
			long PeakMid2 = (node2.getStart() + node2.getStop()) / 2;
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
