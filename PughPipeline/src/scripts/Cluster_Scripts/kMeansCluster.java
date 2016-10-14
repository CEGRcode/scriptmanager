package scripts.Cluster_Scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import scripts.Cluster_Scripts.kMeansClusterScripts.ClusterNode;
import util.SimilarityMetric;

public class kMeansCluster {
	private File INPUT;
	private File OUT_PATH = null;
	
	private ArrayList<ClusterNode> DATA;

	private Random rg;
	
	private int kClusters = -1;
	private int iter = -1;
	private SimilarityMetric compare;
	private ClusterNode[] centroids;

    public kMeansCluster(File in, File out) {
    	INPUT = in;
    	OUT_PATH = out;
    	rg = new Random(System.currentTimeMillis());
    }
    
    public void load() throws FileNotFoundException {
    	DATA = new ArrayList<ClusterNode>();
    	Scanner scan = new Scanner(INPUT);
		int counter = 0;
		int columnsize = 0;		
		boolean LOADFAIL = false;
    	while (scan.hasNextLine()) {
			String line = scan.nextLine();
			if(counter == 0) { //TODO currently hard-coded header is present
				line = scan.nextLine();
				counter++;
			}
			String[] temp = line.split("\t");
			if(counter == 1) { 	columnsize = temp.length; }
			
			if(columnsize != temp.length) {
					LOADFAIL = true;
					scan.close();
			} else {
				double[] numarray = new double[columnsize - 1];
				for(int y = 0; y < numarray.length; y++) {
					try { numarray[y] = Double.parseDouble(temp[y]); }
					catch(NumberFormatException nfe) { numarray[y] = Double.NaN; }
				}
				DATA.add(new ClusterNode(temp[0], numarray));
			}			
			counter++;
	    }
		scan.close();
		//TODO
		if(LOADFAIL) {
			System.out.println("Matrix does not possess the same numver of columns!!!");
			System.exit(1);
		}
    }

    public ArrayList<ArrayList<ClusterNode>> rowcluster(int metric, int k, int it) {
    	if(metric == 0) { compare = new SimilarityMetric("pearson"); }
    	else if(metric == 1) { compare = new SimilarityMetric("reflective"); }
    	else if(metric == 2) { compare = new SimilarityMetric("spearman"); }
    	else if(metric == 3) { compare = new SimilarityMetric("euclidean"); }
    	else if(metric == 4) { compare = new SimilarityMetric("manhattan"); }
    	
    	kClusters = k;
    	iter = it;
    	
        // Place K points into the space represented by the objects that are
        // being clustered. These points represent the initial group of
        // centroids.
        // DatasetTools.
    	ClusterNode max = maxAttributes(DATA);
    	ClusterNode min = minAttributes(DATA);
        centroids = new ClusterNode[kClusters];        
        
        // initialize centroids to random instances
        int instanceLength = DATA.get(0).getData().length;
        for (int j = 0; j < kClusters; j++) {
        	int randomInstance = (int)(rg.nextDouble() * DATA.size());
        	centroids[j] = new ClusterNode(DATA.get(randomInstance));
        }
        
        // Main k-means algorithm
        int iterationCount = 0;
        boolean centroidsChanged = true;
        boolean randomCentroids = true;
        while (randomCentroids || (iterationCount < this.iter && centroidsChanged)) {
        	System.out.println(iterationCount);
        	if(iterationCount % 10 == 0) { System.out.println("Iteration: " + iterationCount); }
            iterationCount++;
            
            // Assign each object to the group that has the closest centroid.
            int[] assignment = new int[DATA.size()];
            for (int i = 0; i < DATA.size(); i++) {
                int tmpCluster = 0;
                double minDistance = compare.getScore(centroids[0].getData(), DATA.get(i).getData());
                for (int j = 1; j < centroids.length; j++) {
                    double dist = compare.getScore(centroids[j].getData(), DATA.get(i).getData());
                    if (dist < minDistance) {
                        minDistance = dist;
                        tmpCluster = j;
                    }
                }
                assignment[i] = tmpCluster;
            }
            
            // When all objects have been assigned, recalculate the positions of the K centroids and start over.
            // The new position of the centroid is the weighted center of the current cluster.
            ArrayList<ClusterNode> avgCentroid = new ArrayList<ClusterNode>();
            for(int x = 0; x < kClusters; x++) { avgCentroid.add(new ClusterNode(new double[instanceLength])); }
            
            centroidsChanged = false;
            randomCentroids = false;
            int[] countPosition = new int[kClusters];
            for (int x = 0; x < DATA.size(); x++) {
            	ClusterNode in = DATA.get(x);
            	for(int y = 0; y < instanceLength; y++) {
            		avgCentroid.get(assignment[x]).getData()[y] += in.getData()[y];
                }
                countPosition[assignment[x]]++;
            }
            System.out.println("summing new centroid assignments");
            
            for (int i = 0; i < kClusters; i++) {
                if (countPosition[i] > 0) {
                	ClusterNode newCentroid = avgCentroid.get(i);
                    for(int x = 0; x < instanceLength; x++) {
                    	newCentroid.getData()[x] /= countPosition[i];
                    }
                    if(compare.getScore(newCentroid.getData(), centroids[i].getData()) > 0.0001) {
                        centroidsChanged = true;
                        centroids[i] = newCentroid;
                    }
                } else {
                	ClusterNode randNode = new ClusterNode(DATA.get(0));
                    for(int x = 0; x < instanceLength; x++) {
                   		double dist = Math.abs(max.getData()[x] - min.getData()[x]);
                    	randNode.getData()[x] = (float) (min.getData()[x] + rg.nextDouble() * dist);
                    }
                    randomCentroids = true;
                    centroids[i] = randNode;
                }

            }
        }

        ArrayList<ArrayList<ClusterNode>> output = new ArrayList<ArrayList<ClusterNode>>();
        for (int i = 0; i < centroids.length; i++)
            output.add(new ArrayList<ClusterNode>());
        for (int i = 0; i < DATA.size(); i++) {
            int tmpCluster = 0;
            double minDistance = compare.getScore(centroids[0].getData(), DATA.get(i).getData());
            for (int j = 0; j < centroids.length; j++) {
                double dist = compare.getScore(centroids[j].getData(), DATA.get(i).getData());
                if (dist < minDistance) {
                    minDistance = dist;
                    tmpCluster = j;
                }
            }
            output.get(tmpCluster).add(DATA.get(i));

        }
        return output;
    }
    

	/**
	 * Create an instance that contains all the maximum values for the
	 * attributes.
	 * 
	 * @param data
	 *            data set to find minimum attribute values for
	 * @return Instance representing the minimum values for each attribute
	 */
	public static ClusterNode maxAttributes(ArrayList<ClusterNode> data) {
		double[] maxarray = new double[data.get(0).getData().length];
		for(int x = 0; x < data.size(); x++) {
			for(int y = 0; y < maxarray.length; y++) {
				if(x == 0 || maxarray[y] < data.get(x).getData()[y]) { maxarray[y] = data.get(x).getData()[y]; }
			}
		}
		return new ClusterNode(maxarray);
	}

	/**
	 * Create an instance that contains all the minimum values for the
	 * attributes.
	 * 
	 * @param data
	 *            data set to calculate minimum attribute values for
	 * @return Instance representing all minimum attribute values
	 */
	public static ClusterNode minAttributes(ArrayList<ClusterNode> data) {
		double[] minarray = new double[data.get(0).getData().length];
		for(int x = 0; x < data.size(); x++) {
			for(int y = 0; y < minarray.length; y++) {
				if(x == 0 || minarray[y] > data.get(x).getData()[y]) { minarray[y] = data.get(x).getData()[y]; }
			}
		}
		return new ClusterNode(minarray);
	}
	
	public void outputCluster(ArrayList<ArrayList<ClusterNode>> Final) throws IOException {
		String[] name = INPUT.getName().split("\\.");
		String NEWNAME = "";
		for(int x = 0; x < name.length - 1; x++) {
			if(x == name.length - 2) { NEWNAME += (name[x]); }
			else { NEWNAME += (name[x] + "."); }
		}
			
		//Open Output File
		PrintStream OUT = null;
		if(OUT_PATH != null) { OUT = new PrintStream(new File(OUT_PATH.getCanonicalPath() + File.separator + NEWNAME + "_KMEANS.out"));
		} else { OUT = new PrintStream(new File(NEWNAME + "_KMEANS.out")); }
		
		for(int x = 0; x < Final.size(); x++) {
			ArrayList<ClusterNode> CurrentK = Final.get(x);
			for(int y = 0; y < CurrentK.size(); y++) {
				ClusterNode CurrentNode = CurrentK.get(y);
				OUT.print(CurrentNode.getID());
				double[] temp = CurrentNode.getData();
				for(int z = 0; z < CurrentNode.getData().length; z++) {
					OUT.print("\t" + temp[z]);
				}
				OUT.println();
			}
			OUT.println("Cluster End\nCluster Start");
		}
		OUT.close();
	}
}
