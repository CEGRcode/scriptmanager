package scripts.Cluster_Scripts;

import java.util.Collections;
import java.util.Vector;

import scripts.Cluster_Scripts.kMeansClusterScripts.ClusterNode;
import util.SimilarityMetric;

public class QTCluster {
	private static Vector<Vector<Double>> CorrMatrix;
	private static Vector<Double> Score;
	private static SimilarityMetric COMPARE;
	
	private static Vector<ClusterNode> Data;
		
	public QTCluster(Vector<ClusterNode> dat) {
		Data = dat;
		COMPARE = new Metric(param.getMetric(), param.getWeight());
	}
	
	public Vector<Vector<Node>> QT() {
		CorrMatrix = new Vector<Vector<Double>>(Data.size());
		Score = new Vector<Double>((Data.size() * Data.size()) / 2);		
		for(int x = 0; x < Data.size(); x++) {
			Vector<Double> xLayer = new Vector<Double>();
			for(int y = 0; y < Data.size(); y++) {
				if(y <= x) {
					xLayer.add(new Double(Math.sqrt(-1)));
				}
				else if(y >= x + 1) {
					Double temp = new Double(COMPARE.getScore(Data.get(x), Data.get(y)));
					xLayer.add(temp);
					Score.add(temp);
				}
			}
			CorrMatrix.add(xLayer);
			System.out.println("Layer: " + x);
		}
		Collections.sort(Score);
		
		System.out.println("Min Score: " + Score.get(0) + "\nMax Score: " + Score.get(Score.size() - 1));
		//Determine Cutoff as 60th of all unique scores in matrix 
		int CutoffIndex = (int)((double)Score.size() * 0.6);
		double Cutoff = Score.get(CutoffIndex);
		System.out.println("Cutoff: " + Cutoff);
		
		//Determine minimum cluster size as square root of number of elements (minimum of 10)
		int minClusterSize = (int)Math.sqrt((double)Data.size());
		if(minClusterSize < 10) minClusterSize = 10;		
		System.out.println("Minimum Cluster Size: " + minClusterSize);
		
		//Clean up unused Score List
		Score = null;
		//Create boolean to insure that loop quits if there aren't any combos left
		boolean CLUSTERSPOSSIBLE = true;
		int counter = 0;
		
		//Create object to contain all the data in cluster form
		Vector<Vector<Node>> DATASET = new Vector<Vector<Node>>();
		
		while(CorrMatrix.size() >= minClusterSize && CLUSTERSPOSSIBLE) {
			counter++;
			int indexofSeed = -1;
			Vector<Integer> companionIndex = new Vector<Integer>();
			//Loop through matrix for best qt seed
			for(int x = 0; x < CorrMatrix.size(); x++) {
				Vector<Integer> tempIndex = new Vector<Integer>();
				for(int y = 0; y < CorrMatrix.get(x).size(); y++) {
					if(!Double.isNaN(CorrMatrix.get(x).get(y))) {
						if(CorrMatrix.get(x).get(y).doubleValue() >= Cutoff) {
							tempIndex.add(y);
						}
					} else if(!Double.isNaN(CorrMatrix.get(y).get(x))){
						if(CorrMatrix.get(y).get(x).doubleValue() >= Cutoff) {
							tempIndex.add(y);
						}
					}
				}
				if(tempIndex.size() > companionIndex.size()) {
					indexofSeed = x;
					companionIndex = tempIndex;
				}
			}
			//Output best seed and its components
			if(companionIndex.size() >= minClusterSize) {
				Vector<Node> newNodeArray = new Vector<Node>();
				newNodeArray.add(Data.get(indexofSeed));
				for(int x = 0; x < companionIndex.size(); x++) newNodeArray.add(Data.get(companionIndex.get(x)));
				DATASET.add(newNodeArray);
				
				//Then remove them from the matrix
				companionIndex.add(new Integer(indexofSeed));
				Collections.sort(companionIndex);
				for(int x = companionIndex.size() - 1; x > -1; x--) {
					removeComponent(CorrMatrix, Data, companionIndex.get(x));
				}
				System.out.println("Cluster: " + counter + "\tCluster Size: " + companionIndex.size());
			}
			else{
				//Check to see if remaining matrix is capable of producing another cluster
				CLUSTERSPOSSIBLE = false;
			}
		}
		//Output remaining sites as junk cluster
		System.out.println("Remaining Sites: " + Data.size());
		Vector<Node> newNodeArray = new Vector<Node>();
		for(int x = 0; x < Data.size(); x++) {
			newNodeArray.add(Data.get(x));
		}
		DATASET.add(newNodeArray);
		
		return DATASET;
	}
	
	public static void removeComponent(Vector<Vector<Double>> Matrix, Vector<Node> Data, int index) {
		Data.remove(index);
		Matrix.remove(index);
		for(int x = 0; x < Matrix.size(); x++) {
			Matrix.get(x).remove(index);
		}
	}

}
