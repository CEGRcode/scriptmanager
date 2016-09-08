package scripts.Tag_Analysis.CorrelationScripts;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;

@SuppressWarnings("serial")
public class CCHeatmap extends ApplicationFrame {
		
    public CCHeatmap(String title, double[][] correlation, String[] labels, File heatmapfile) {
        super(title);

		//creates a different copy of the data matrix in a new window
        JPanel chartPanel = createGraphic(correlation, labels); 
        setContentPane(chartPanel);
        JFrame frame = new JFrame();
        frame.add(chartPanel);
        frame.setSize(995, 900);
        frame.setVisible(true);
        
        //prepares the heatmap for saving
	    JFreeChart corrmatrix = createChart(convertDataset(correlation), labels, correlation.length, correlation);
        SVGGraphics2D SVGheatmap = new SVGGraphics2D(995, 900);
        Rectangle rectangleheatmap = new Rectangle(0, 0, 995, 900);
        corrmatrix.draw(SVGheatmap, rectangleheatmap);
        
        //saves the heatmap SVG file to the disk
        try {
			SVGUtils.writeToSVG(heatmapfile, SVGheatmap.getSVGElement());
		} 
        catch (IOException e) {
			e.printStackTrace();
		}

//        //creates dendrogram using the clustering library
//        ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
//        Cluster cluster = alg.performClustering(diff, labels, new AverageLinkageStrategy());
//        DendrogramPanel dp = new DendrogramPanel();
//        dp.setModel(cluster);
//        JFrame clusterframe = new JFrame();
//        clusterframe.add(dp);
//        clusterframe.setSize(1000, 1000);
//        clusterframe.setVisible(true);
        
        
//        //saves the dendrogram SVG file to the disk
//        try {
//			SVGUtils.writeToSVG(clusterfile, SVGcluster.getSVGElement());
//		} 
//        catch (IOException e) {
//			e.printStackTrace();
//		}
    }
    
    private static Color[] heatmapcolors; 
    
    private static JFreeChart createChart(XYZDataset dataset, String[] labels, int length, double[][] correlation) {
    	
        XYBlockRenderer renderer = new XYBlockRenderer();
    	
        ValueAxis xAxis = new SymbolAxis("", labels);
        //centers the dataset along the x axis
        xAxis.setLowerBound(-0.5);
        xAxis.setUpperBound(length - 0.5);
        xAxis.setVisible(true);
        xAxis.setTickLabelsVisible(true);
        
        //removes unnecessary graphic components
        xAxis.setAxisLineVisible(false);
        xAxis.setMinorTickMarksVisible(false);
        xAxis.setTickMarksVisible(false);
        
        SymbolAxis yAxis = new SymbolAxis("", reverseLabels(labels));
        //centers the dataset along the y axis
        yAxis.setLowerBound(-0.5);
        yAxis.setUpperBound(length - 0.5);
        yAxis.setVisible(true);
        yAxis.setTickLabelsVisible(true);
        
        yAxis.setAxisLineVisible(false);
        yAxis.setMinorTickMarksVisible(false);
        yAxis.setTickMarksVisible(false);
        
        //CHANGE LATER, creates the paintscale to base the data reads around (0 to 240 degrees or RED to BLUE)
        LookupPaintScale scale = new LookupPaintScale(0, 1.0000001, Color.WHITE);
        for (int paint = 0; paint < 240; paint ++) {
        	scale.add(((float) paint) / 240, Color.getHSBColor((240 - (float) paint) / 360, 1, 1));
        }
        renderer.setPaintScale(scale);
        
        heatmapcolors = new Color[correlation.length];
        
        for (int addpaint = 1; addpaint < correlation.length; addpaint ++) {
            heatmapcolors[addpaint] = 
            		(Color) scale.getPaint(correlation[addpaint][0]);
        }
        
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        
        plot.setBackgroundPaint(Color.WHITE);
        JFreeChart chart = new JFreeChart("Heatmap for correlation coefficent", plot);
        
        chart.removeLegend();
        chart.setBackgroundPaint(Color.WHITE);
        
        NumberAxis scaleAxis = new NumberAxis();
        PaintScaleLegend psl = new PaintScaleLegend(scale, scaleAxis);
        
        TickUnits units = new TickUnits();
        for (float tickadd = 0; tickadd <= 1; tickadd += 0.1) {
            units.add(new NumberTickUnit(tickadd));
        }
        
        scaleAxis.setStandardTickUnits(units);
        
        psl.setPadding(30, 30, 50, 30);
        psl.setPosition(RectangleEdge.RIGHT);
        psl.setStripWidth(35);
        chart.addSubtitle(psl);
        return chart;
    }
    
    private static String[] reverseLabels(String[] labels) {
    	String[] reversed = new String[labels.length];
		for (int stringindex = 0; stringindex < labels.length; stringindex ++) {
			reversed[stringindex] = labels[labels.length - 1 - stringindex];
		}
		return reversed;
	}

	private XYZDataset convertDataset(double[][] correlation) {
    	//converts an n x n matrix of z read values to integer x y values and the corresponding z value
        double[][] data  = new double[3][(correlation.length * correlation.length)];
    	
    	for (int x = 0; x < correlation.length; x ++) {
			for (int y = 0; y < correlation.length; y ++) {
				data[0][x + y * correlation.length] = x;
				data[1][x + y * correlation.length] = y;
				data[2][x + y * correlation.length] = correlation[x][correlation.length - 1 - y];
			}
		}
    	
    	//adds the converted series to the dataset to be made into the heatmap image
        DefaultXYZDataset dataset = new DefaultXYZDataset();
        dataset.addSeries("Heatmap series", data);
        return dataset;
    }
    
    public JPanel createGraphic(double[][] correlation, String[] labels) {
    	///returns JPanel image of the dataset in heatmap form
        return new ChartPanel(createChart(convertDataset(correlation), labels, correlation.length, correlation));
    }

	public Color[] getColors() {
		return heatmapcolors;
	}       	    
}
