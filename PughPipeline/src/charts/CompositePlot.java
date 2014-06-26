package charts;

import java.awt.BasicStroke;
import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class CompositePlot {

	public CompositePlot() {
		
	}
	
	public static ChartPanel createCompositePlot(double[] x, double[] y){
		final XYSeries series1 = new XYSeries("Data");
		for (int i = 0; i < x.length; i++) {
			series1.add(x[i], y[i]);
		}
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series1);
		final JFreeChart chart = createChart(dataset, 1);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		return chartPanel;
	}
	
	private static JFreeChart createChart(final XYDataset dataset, int numseries) {
		//Call Chart
		final JFreeChart chart = ChartFactory.createXYLineChart(
				"Composite Plot", // chart title
				"Distance from Feature (bp)", // x axis label
				"Score", // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);

		chart.setBackgroundPaint(Color.white);
		final XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.black);
		plot.setRangeGridlinePaint(Color.black);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for(int x = 0; x < numseries; x++) {
			renderer.setSeriesLinesVisible(x, true);	//Spline visibility
			renderer.setSeriesShapesVisible(x, false);	//Data point dot visibility
			renderer.setSeriesStroke(x, new BasicStroke(3));
		}
		plot.setRenderer(renderer);
		// change the auto tick unit selection to integer units only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		final NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
		domainAxis.setAxisLineVisible(true);
		rangeAxis.setAxisLineVisible(true);
		return chart;
	}
}
