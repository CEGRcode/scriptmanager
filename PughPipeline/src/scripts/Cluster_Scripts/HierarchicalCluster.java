package scripts.Cluster_Scripts;

import java.io.File;
import java.io.IOException;

import scripts.Cluster_Scripts.CorrelationScripts.CCDendrogram;
import scripts.Cluster_Scripts.CorrelationScripts.CCHeatmap;
import scripts.Cluster_Scripts.CorrelationScripts.CCProcess;

/*
This script will calculate the pearson correlation for all against all

It produces three output files
1. the text file where it contains all the correlation coefficiency values
2. the dendrogram.pdf
3. Heatmap.pdf for the correlation coefficiency values

Created in Python by Kuangyu Yen on 2013-02-25.
Translated to Java by Geoffrey Billy on 2016-07-18.
Copyright (c) 2013 __PughLab@PSU__. All rights reserved.
*/

public class HierarchicalCluster {
	
	private File INPUT = null;
	private File OUTPUT = null;
		
	public HierarchicalCluster(File in, File out) {
		INPUT = in;
		OUTPUT = out;
	}
	
	public void execute() {
		String matrixfile_name = (INPUT.getName()).substring(0, INPUT.getName().length() - 4) + "_MATRIX.out";
		String heatmapfile_name = (INPUT.getName()).substring(0, INPUT.getName().length() - 4) + "_HEATMAP.svg";
		String dendrogramfile_name = (INPUT.getName()).substring(0, INPUT.getName().length() - 4) + "_DENDROGRAM.svg";

		File matrixfile = new File(matrixfile_name);
		File heatmapfile = new File(heatmapfile_name);
		File dendrogramfile = new File(dendrogramfile_name);
		int correlationmethod = 1;
		if(INPUT.isFile()) {
			//if (matrixfile.isFile()) {
			//	if (heatmapfile.getAbsolutePath().substring(heatmapfile.getAbsolutePath().length() - 4, heatmapfile.getAbsolutePath().length()).equals(".svg") && 
			//			dendrogramfile.getAbsolutePath().substring(dendrogramfile.getAbsolutePath().length() - 4, dendrogramfile.getAbsolutePath().length()).equals(".svg")) {
					while (!heatmapfile.isFile()) {
						try { heatmapfile.createNewFile(); }
						catch (IOException e) { e.printStackTrace(); }
					}
					while (!dendrogramfile.isFile()) {
						try { dendrogramfile.createNewFile(); }
						catch (IOException e) { e.printStackTrace(); }
					}
					
					CCProcess process = new CCProcess();
					process.fileParser(INPUT, matrixfile, correlationmethod);
					
					@SuppressWarnings("unused")
					CCHeatmap heatmap = new CCHeatmap("Heatmap for correlation coefficent", process.getCoeffdiff(), process.getSamples(), heatmapfile);
					
					@SuppressWarnings("unused")
					CCDendrogram dendrogram = new CCDendrogram("", process.getEucliddiff(), process.getSamples(), dendrogramfile);
				}
		//		else {
		//			System.out.println("ERROR: Output SVG image files do not have vaild paths to be saved to.");
		//		}
		//	} else {
		//		System.out.println("ERROR: Output text file is not a valid file to be saved to.");
		//	}
		//}
		else {
			System.out.println("ERROR: Input file is not a valid file.");
		}
	}
}
