package charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

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
	
	public static ChartPanel createCompositePlot(double[] x, double[] y1, double[] y2, String name, ArrayList<Color> COLORS){
		final XYSeriesCollection dataset = new XYSeriesCollection();
		final XYSeries seriesF = new XYSeries("Sense Strand");
		final XYSeries seriesR = new XYSeries("Anti Strand");
		
		for(int i = 0; i < x.length; i++) {
			seriesF.add(x[i], y1[i]);
			seriesR.add(x[i], y2[i]);
		}
		dataset.addSeries(seriesF);
		dataset.addSeries(seriesR);

		final JFreeChart chart = createChart(dataset, name, COLORS);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		return chartPanel;
	}
	
	public static Component createCompositePlot(double[] x, double[] y1, String name, ArrayList<Color> COLORS){
		final XYSeriesCollection dataset = new XYSeriesCollection();
		final XYSeries seriesC = new XYSeries("Data");
		for(int i = 0; i < x.length; i++) {
			seriesC.add(x[i], y1[i]);
		}
		dataset.addSeries(seriesC);
		
		final JFreeChart chart = createChart(dataset, name, COLORS);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		return chartPanel;
	}
	
	public static Component createCompositePlot(double[] x, double[] y1, String name){
		ArrayList<Color> COLORS = new ArrayList<Color>();
		COLORS.add(Color.BLACK);
		return(createCompositePlot(x, y1, name, COLORS));
	}
		
	public static JFreeChart createChart(final XYDataset dataset, String TITLE, ArrayList<Color> COLORS) {
		return(createChart(dataset, TITLE, COLORS, true));
	}
	
	public static JFreeChart createChart(final XYDataset dataset, String TITLE, ArrayList<Color> COLORS, boolean legend) {
		//Call Chart
		final JFreeChart chart = ChartFactory.createXYLineChart(
				TITLE, // chart title
				"Distance from Feature (bp)", // x axis label
				"Score", // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, legend, // include legend
				true, // tooltips
				false // urls
				);

		chart.setBackgroundPaint(Color.white);
		final XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.black);
		plot.setRangeGridlinePaint(Color.black);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for(int x = 0; x < COLORS.size(); x++) {
			renderer.setSeriesLinesVisible(x, true);	//Spline visibility
			renderer.setSeriesShapesVisible(x, false);	//Data point dot visibility
			renderer.setSeriesStroke(x, new BasicStroke(3));
			renderer.setSeriesPaint(x, COLORS.get(x));
		}
// 		renderer.setSeriesPaint(0, COLORS.get(0));
// 		if(COLORS.size() == 2) { renderer.setSeriesPaint(1, COLORS.get(1)); }
		
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
