package objects;

public class GenetrackParameters {
	
	int SIGMA = 5;
	int EXCLUSION = 10;
	int FILTER = 1;
	int UP = -999;
	int DOWN = -999;
	int READ = 0;
	String NAME = "";
		
	public GenetrackParameters() {
		
	}
	
	public void setSigma(int s) {
		SIGMA = s;
	}
	
	public int getSigma() {
		return SIGMA;
	}
	
	public void setExclusion(int e) {
		EXCLUSION = e;
	}
	
	public int getExclusion() {
		return EXCLUSION;
	}
	
	public void setFilter(int f) {
		FILTER = f;
	}
	
	public int getFilter() {
		return FILTER;
	}
	
	public void setUp(int u) {
		UP = u;
	}
	
	public int getUp() {
		return UP;
	}
	
	public void setDown(int d) {
		DOWN = d;
	}
	
	public int getDown() {
		return DOWN;
	}
	
	public void setRead(int r) {
		READ = r;
	}
	
	public int getRead() {
		return READ;	
	}
	
	public void setName(String n) {
		NAME = n;
	}
	
	public String getName() {
		return NAME;
	}
	
	public String toString() {
		String temp = "genetrack_s" + SIGMA + "e" + EXCLUSION;
		if(UP != -999) temp += "u" + UP;
		if(DOWN != -999) temp += "d" + DOWN;
		temp += "F" + FILTER;
		return temp;
	}
}
