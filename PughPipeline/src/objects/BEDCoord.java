package objects;

public class BEDCoord {
	private String CHROM = "";
	private int START = 0;
	private int STOP = 0;
	private String DIR = "+";
	
	private String NAME = "";
	private double SCORE = 0;

	private double[] Fstrand = null;
	private double[] Rstrand = null;
	
	public BEDCoord() {
		
	}
	
	public BEDCoord(String c, int sta, int sto, String di, String na) {
		CHROM = c;
		START = sta;
		STOP = sto;
		DIR = di;
		NAME = na;
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
}
