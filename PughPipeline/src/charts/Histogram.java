package charts;

import java.awt.Color;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Histogram {
	
	public Histogram() {

	}
			
	public static ChartPanel createBarChart(double[] y, int[] x) throws IOException {
		final XYSeries series = new XYSeries("Frequency");
		for(int i = 0; i < x.length; i++) {
			series.add((double)x[i], (double)y[i]);
		}
		final XYSeriesCollection dataset = new XYSeriesCollection(series);
		
        JFreeChart chart = createChart(dataset);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		return chartPanel;				
	}
	
	private static JFreeChart createChart(IntervalXYDataset dataset) throws IOException {
        final JFreeChart chart = ChartFactory.createXYBarChart(
        		"Paired-End Insert Size Frequency Histogram",      // chart title
                "Insert Size (bp)",               // domain axis label
                false,
                "Frequency",                  // range axis label
                dataset,                  // data
                PlotOrientation.VERTICAL, // orientation
                true,                     // include legend
                true,                     // tooltips
                false                     // urls
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        //Set Histogram Color to Red
        plot.getRenderer().setSeriesPaint(0, Color.red);
        
        //Turn off Glossy 3D on bar chart
        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardXYBarPainter());

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
