package scriptmanager.objects.CoordinateObjects;

import java.util.Comparator;

/**
 * Object for storing reads of a BED file
 * @see scriptmanager.objects.CoordinateObjects.GenomicCoord
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
	 * Creates a new BEDCoord object with a given line of a BED file
	 * @param line Line to parse in order to create BEDCoord file
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
	 * Creates a new BEDCoord object with a name and score
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
	 * Returns the Pileup scores for the forward strand
	 * @return The Pileup scores for the forward strand
	 */
	public double[] getFStrand() {
		return Fstrand;
	}
	
	/**
	 * Returns the Pileup scores for the reverse strand
	 * @return The Pileup scores for the reverse strand
	 */
	public double[] getRStrand() {
		return Rstrand;
	}
	
	/**
	 * Sets the Pileup scores for the forward strand
	 * @param f The Pileup scores for the forward strand
	 */
	public void setFstrand(double[] f) {
		Fstrand = f;
	}
	
	/**
	 * Sets the Pileup scores for the reverse strand
	 * @param r The Pileup scores for the reverse strand
	 */
	public void setRstrand(double[] r) {
		Rstrand = r;
	}
	
	/**
	 * Returns the chromosome name
	 * @return the stored chr name
	 */
	public String getChrom() {
		return CHROM;
	}
	
	/**
	 * Sets the chromosome name
	 * @param chr The new chr name
	 */
	public void setChrom(String chr) {
		CHROM = chr;
	}
	
	/**
	 * Returns the starting coordinate of the BEDCoord
	 * @return the starting coordinate of the BEDCord
	 */
	public long getStart() {
		return START;
	}
	
	/**
	 * Sets the starting coordinate of the BEDCoord
	 * @param sta The starting coordinate of the BEDCord
	 */
	public void setStart(int sta) {
		START = sta;
	}
	
	/**
	 * Returns the ending coordinate of the BEDCoord
	 * @return the ending coordinate of the BEDCoord
	 */
	public long getStop() {
		return STOP;
	}
	
	/**
	 * Sets the ending coordinate of the BEDCoord
	 * @param sto The ending coordinate of the BEDCoord
	 */
	public void setStop(int sto) {
		STOP = sto;
	}
	
	/**
	 * Returns the strand direction
	 * @return the strand direction (+/-)
	 */
	public String getDir() {
		return DIR;
	}
	
	/**
	 * Sets the strand direction
	 * @param di The strand direction (+/-)
	 */
	public void setDir(String di) {
		DIR = di;
	}
	
	/**
	 * Returns the name of the BEDCoord
	 * @return The name of the BEDCoord
	 */
	public String getName() {
		return NAME;
	}
	
	/**
	 * Sets the name of the BEDCoord
	 * @param na The name of the BEDCoord
	 */
	public void setName(String na) {
		NAME = na;
	}
	
	/**
	 * Returns the score of the BEDCoord
	 * @return The score of the BEDCoord
	 */
	public double getScore() {
		return SCORE;
	}

	/**
	 * Sets the score of the BEDCoord
	 * @param sco The new score of the BEDCoord
	 */
	public void setScore(double sco) {
		SCORE = sco;
	}
	
	/**
	 * Returns the different variables of a BEDCoord represented by a string
	 * @return The different variables of a BEDCoord represented by a string
	 */
	public String toString() {
		String line = CHROM + "\t" + START + "\t" + STOP + "\t" + NAME + "\t" + SCORE + "\t" + DIR;
		return line;
	}
	
	/**
	 * Creates a new comparator which compares BEDCoords based on their chromosomes
	 */
	public static Comparator<BEDCoord> PeakChromComparator = new Comparator<BEDCoord>() {
		/**
		 * Compares two BEDCoords based on their chromosomes
		 * @param node1 First BEDCoord to comapare
		 * @param node2 Second BEDCoord to compare
		 * @return A negative value if node1 comes before node2, 0 if they have the same coordinate, and 1 if node1 comes after
		 */
		public int compare(BEDCoord node1, BEDCoord node2) {
			return node1.getChrom().compareTo(node2.getChrom());
	}
	};

	/**
	 * Creates a new comparator which compares BEDCoords based the coordinates on starting coordinates
	 */
	public static Comparator<BEDCoord> PeakPositionComparator = new Comparator<BEDCoord>() {
		/**
		 * Compares two BEDCoords based on starting coordinates
		 * @param node1 First BEDCoord to compare
		 * @param node2 Second BEDCoord to compare
		 * @return A negative value if node1 comes before node2, 0 if they have the same start, and 1 if node1 comes after
		 */
		public int compare(BEDCoord node1, BEDCoord node2) {
			long PeakStart1 = node1.getStart();
			long PeakStart2 = node2.getStart();
			if (PeakStart1 > PeakStart2) return 1;
			else if (PeakStart1 < PeakStart2) return -1;
			else return 0;
	}
	};
	
	/**
	 * Creates a new comparator which compares BEDCoords based the coordinates on their mid point
	 */
	public static Comparator<BEDCoord> PeakMidpointComparator = new Comparator<BEDCoord>() {
		/**
		 * Compares two BEDCoords based on the position of their mid point
		 * @param node1 First BEDCoord to compare
		 * @param node2 Second BEDCoord to compare
		 * @return A negative value if mid point of node1 comes before node2's, 0 if they have the same mid point, and 1 if node1's mid point comes after
		 */
		public int compare(BEDCoord node1, BEDCoord node2) {
			long PeakMid1 = (node1.getStart() + node1.getStop()) / 2;
			long PeakMid2 = (node2.getStart() + node2.getStop()) / 2;
			if (PeakMid1 > PeakMid2) return 1;
			else if (PeakMid1 < PeakMid2) return -1;
			else return 0;
	}
	};
	
	/**
	 * Creates a new comparator which compares BEDCoords based the coordinates on their score
	 */
	public static Comparator<BEDCoord> ScoreComparator = new Comparator<BEDCoord>() {
		/**
		 * Compares two BEDCoords based on the position of their score
		 * @param node1 First BEDCoord to compare
		 * @param node2 Second BEDCoord to compare
		 * @return A negative value if node1 has a higher score than node2, zero if they have equal scores, and a positive value if node1 has a lower score than node2
		 */
		public int compare(BEDCoord node1, BEDCoord node2) {
			double PeakStart1 = node1.getScore();
			double PeakStart2 = node2.getScore();
			if (PeakStart1 < PeakStart2) return 1;
			else if (PeakStart1 > PeakStart2) return -1;
			else return 0;
	}
	};
}
