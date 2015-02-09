package charts;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class LineChart {
	
	public LineChart() {

	}
			
	public static ChartPanel createLineChart(ArrayList<Double> y, String[] x) throws IOException {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(int i = 0; i < x.length; i++) {
			dataset.addValue(y.get(i).doubleValue(), "Duplication Rate", x[i]);
		}
		
        JFreeChart chart = createChart(dataset);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		return chartPanel;				
	}
	
	public static ChartPanel createLineChart(ArrayList<Double> y1, ArrayList<Double> y2, String[] x) throws IOException {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(int i = 0; i < x.length; i++) {
			dataset.addValue(y1.get(i).doubleValue(), "Signal Duplication Rate", x[i]);
			dataset.addValue(y2.get(i).doubleValue(), "Genome Duplication Rate", x[i]);
		}
		
        JFreeChart chart = createChart(dataset);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		return chartPanel;				
	}
	
	private static JFreeChart createChart(CategoryDataset dataset) throws IOException {
        final JFreeChart chart = ChartFactory.createLineChart(
        		"Paired-End Duplication Rate",      // chart title
                "Level of Duplicate Molecules",               // domain axis label
                "Frequency of Duplication",                  // range axis label
                dataset,                  // data
                PlotOrientation.VERTICAL, // orientation
                true,                     // include legend
                true,                     // tooltips
                false                     // urls
        );
        
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        //Set Histogram Color to Red
        plot.getRenderer().setSeriesPaint(0, Color.blue);

        //Code to turn off shadows if 3D
        //renderer.setShadowVisible(false);

        //Code to set y-axis range color
        //final IntervalMarker target = new IntervalMarker(400.0, 700.0);
        //target.setLabelFont(new Font("SansSerif", Font.ITALIC, 11));
        //target.setLabelAnchor(RectangleAnchor.LEFT);
        //target.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
        //target.setPaint(new Color(222, 222, 255, 128));
        //plot.addRangeMarker(target, Layer.BACKGROUND);
                
        return chart;    
    }
}
