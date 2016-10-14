package scripts.Cluster_Scripts.kMeansClusterScripts;

public class ClusterNode {
	private String uniqID = "";
	private double[] dataArray = null;
	
	public ClusterNode () {
		
	}
	
	public ClusterNode (double[] newarray) {
		dataArray = newarray;
	}
	
	public ClusterNode (String id, double[] newarray) {
		uniqID = id;
		dataArray = newarray;
	}
	
	public ClusterNode (ClusterNode orig) {
		uniqID = orig.getID();
		dataArray = new double[orig.getData().length];
		for(int x = 0; x < dataArray.length; x++) {
			dataArray[x] = orig.getData()[x];
		}
	}
	
	public String getID() {
		return uniqID;		
	}
	
	public void setID(String newid) {
		uniqID = newid;		
	}
	
	public double[] getData() {
		return dataArray;
	}
	
	public void setData(double[] newarray) {
		double[] dataArray = new double[newarray.length];
		for(int x = 0; x < dataArray.length; x++){
			dataArray[x] = newarray[x];
		}
	}
}
