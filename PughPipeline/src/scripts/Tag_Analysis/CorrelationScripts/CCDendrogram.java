package scripts.Tag_Analysis.CorrelationScripts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;
import org.jfree.ui.ApplicationFrame;

@SuppressWarnings("serial")
public class CCDendrogram extends ApplicationFrame {
	
    public CCDendrogram(String title, double[][] eucliddiff, String[] labels, File dendrogram) {
        super(title);

        double[][] dendrogramdataset = pointFinder(formatDataset(eucliddiff));
        
        JPanel chartPanel = createGraphic(dendrogramdataset, labels); 
        setContentPane(chartPanel);
        JFrame frame = new JFrame();
        frame.add(chartPanel);
        frame.setSize(1000, 1000);
        frame.setVisible(true);
        
	    JFreeChart corrmatrix = createChart(dendrogramdataset, labels);
        SVGGraphics2D SVGheatmap = new SVGGraphics2D(1000, 1000);
        Rectangle rectangleheatmap = new Rectangle(0, 0, 1000, 1000);
        corrmatrix.draw(SVGheatmap, rectangleheatmap);
        
        //saves the heatmap SVG file to the disk
        try {
			SVGUtils.writeToSVG(dendrogram, SVGheatmap.getSVGElement());
		} 
        catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private static double ymax = 0;
    private static double[] orderlist;
    
	private ArrayList<double[]> formatDataset(double[][] eucliddiff) {
				
    	ArrayList<double[]> dendrogram = new ArrayList<double[]>();
    	int moretomerge = -1;
    	while (moretomerge != 0) {
    		moretomerge = 0;
        	double[] nextmerge = {-1, -1, -1};
			for (int x = 0; x < eucliddiff.length; x ++) {
				for (int y = 0; y < x; y ++) {
					if (eucliddiff[x][y] > 0) {
						moretomerge ++;
						if (nextmerge[2] == -1 || nextmerge[2] > eucliddiff[x][y]) {
							nextmerge[0] = Math.min(x, y);
							nextmerge[1] = Math.max(x, y);
							nextmerge[2] = eucliddiff[x][y];
						}
					}
				}
			}
			
			if (nextmerge[2] != -1) {
				eucliddiff = mergeXY(eucliddiff, nextmerge);
	    		dendrogram.add(nextmerge);
	    		eucliddiff[(int) nextmerge[0]][(int) nextmerge[1]] = 0;
	    		eucliddiff[(int) nextmerge[1]][(int) nextmerge[0]]= 0;
			}
    	}
    	System.out.println();
		for (double[] dd : dendrogram) {
			for (double d : dd) {
				System.out.print(d + "\t");
			}
			System.out.println();
		}
    	return dendrogram;
    }
        
    private double[][] pointFinder(ArrayList<double[]> mergeddataset) {
    	    	
    	double[][] dendrogrampoints = new double[mergeddataset.size()][5];
    	orderlist = new double[mergeddataset.size() + 1];
    	
    	// index number, midpoint of the top range contained, max height of the range
    	ArrayList<double[]> cluster = new ArrayList<double[]>();
    	    	
    	int order = 0;
		double[] zeroarray = {-1, -1, 0};

    	for (int zero = 0; zero < mergeddataset.size() + 1; zero ++) {
    		cluster.add(zeroarray);
    	}
    	    
    	for (int merge = 0; merge < mergeddataset.size(); merge ++) {
    		//update order of mergepoints with the next available index
    		for (int mergepoints = 0; mergepoints < 2; mergepoints ++) {
				if (cluster.get((int) mergeddataset.get(merge)[mergepoints])[0] < 0) {
					orderlist[order] = mergeddataset.get(merge)[mergepoints];
					double[] orderarray = {order, order, 0};
					cluster.set((int) mergeddataset.get(merge)[mergepoints], orderarray);
					order ++;
				}
    		}
    		//point 1 y value
    		dendrogrampoints[merge][2] = cluster.get((int) mergeddataset.get(merge)[0])[2];
    		//point 4 y value
    		dendrogrampoints[merge][3] = cluster.get((int) mergeddataset.get(merge)[1])[2];
    		//x value of point 1 and point 2
    		dendrogrampoints[merge][0] = cluster.get((int) mergeddataset.get(merge)[0])[1];
    		//x value of point 3 and point 4
    		dendrogrampoints[merge][1] = cluster.get((int) mergeddataset.get(merge)[1])[1]; 
    		
    		//sets the new cluster to the new midpoint of the range and the max height
    		double[] newmidpoint = {cluster.get((int) mergeddataset.get(merge)[0])[0], (cluster.get((int) mergeddataset.get(merge)[0])[1] + cluster.get((int) mergeddataset.get(merge)[1])[1]) / 2, mergeddataset.get(merge)[2]};
    		cluster.set((int) mergeddataset.get(merge)[0], (newmidpoint));
    		cluster.set((int) mergeddataset.get(merge)[1], (newmidpoint));
    		//point 2 and 3 y value
    		dendrogrampoints[merge][4] = cluster.get((int) mergeddataset.get(merge)[0])[2];
    		//ymax to graph by
    		ymax = mergeddataset.get(merge)[2];
      	} 
		return dendrogrampoints;
	}
    
	private double[][] mergeXY(double[][] eucliddiff, double[] nextmerge) {
		
		eucliddiff = newDistances(eucliddiff, nextmerge);
		
		for (int merge = 0; merge < eucliddiff.length; merge ++) {
			eucliddiff[(int) nextmerge[1]][merge] = 0;
		}
		for (int merge = 0; merge < eucliddiff.length; merge ++) {
			eucliddiff[merge][(int) nextmerge[1]] = 0;
		}
		
		return eucliddiff;
	}
	

	private double[][] newDistances(double[][] eucliddiff, double[] merge) {
		int xneutral = 0;
		int yneutral = 0;
		while (merge[0] == xneutral || merge[1] == xneutral) {
			xneutral ++;
		}
		while (merge[0] == yneutral || merge[1] == yneutral) {
			yneutral ++;
		}
		for (int adjust = 0; adjust < eucliddiff.length; adjust ++) {
			
			eucliddiff[(int) merge[0]][adjust] = 
					0.5 * (eucliddiff[(int) merge[0]][adjust]) + 
					0.5 * (eucliddiff[(int) merge[1]][adjust]) -
					0.5 * Math.abs((eucliddiff[(int) merge[0]][adjust]) - (eucliddiff[(int) merge[1]][adjust]));
			
			eucliddiff[adjust][(int) merge[0]] =
					0.5 * (eucliddiff[(int) merge[0]][adjust]) + 
					0.5 * (eucliddiff[(int) merge[1]][adjust]) -
					0.5 * Math.abs((eucliddiff[(int) merge[0]][adjust]) - (eucliddiff[(int) merge[1]][adjust]));;
		}
		return eucliddiff;
	}
	

	private static JFreeChart createChart(double[][] data, String[] labels) {
    	    	
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    	
        ValueAxis xAxis = new SymbolAxis("", arrangeYAxis(labels));
        xAxis.setLowerBound(-0.5);
        xAxis.setUpperBound(data.length + 1 - 0.5);
        xAxis.setVisible(true);
        
        xAxis.setMinorTickMarksVisible(false);
        xAxis.setTickMarksVisible(false);
        xAxis.setAxisLineVisible(false);
                
        NumberAxis yAxis = new NumberAxis();
        //centers the dataset along the y axis
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(ymax + ymax * 0.1);
        System.out.println(ymax);
        yAxis.setVisible(true);
        yAxis.setTickLabelsVisible(true);
        
        yAxis.setMinorTickMarksVisible(false);
        yAxis.setTickMarksVisible(true);
        yAxis.setAxisLineVisible(false);
        
        XYSeriesCollection dataset = new XYSeriesCollection();  
        for (int sample = 0; sample < data.length; sample ++) {
	        XYSeries series = new XYSeries(labels[sample]);
	        
	        if (data[sample][0] == Math.min(data[sample][0], data[sample][1])) {
		       	series.add(data[sample][0], data[sample][2]);
		       	series.add(data[sample][0], data[sample][4]);
		       	series.add(data[sample][1], data[sample][4]);
		       	series.add(data[sample][1], data[sample][3]);
	        }
	        
	        else {
		       	series.add(data[sample][1], data[sample][3]);
		       	series.add(data[sample][1], data[sample][4]);
		       	series.add(data[sample][0], data[sample][4]);
	        	series.add(data[sample][0], data[sample][2]);
	        }
	        
	        dataset.addSeries(series);
	        renderer.setSeriesStroke(sample, new BasicStroke(2.35f));
	        renderer.setSeriesPaint(sample, Color.BLACK);
        }
        
        renderer.setBaseShapesVisible(false);
        
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
                  
        plot.setForegroundAlpha((float) 1.0);
        plot.setBackgroundAlpha((float) 0.0);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        JFreeChart chart = new JFreeChart("Heatmap for correlation coefficent", plot);
        
        chart.removeLegend();
                
        return chart;
    }
	
	private static String[] arrangeYAxis(String[] samples) {
		String[] orderersamples = new String[samples.length];
		for (int order = 0; order < orderlist.length; order ++) {
			orderersamples[order] = samples[(int) orderlist[order]];
		}
		return orderersamples;
	}
        
    public JPanel createGraphic(double[][] correlation, String[] labels) {
        return new ChartPanel(createChart(correlation, labels));
    }
}
