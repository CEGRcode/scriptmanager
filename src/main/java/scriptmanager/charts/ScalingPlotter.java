package scriptmanager.charts;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

/**
 * Static methods for building charts for the Scaling Factor tool (particularly
 * NCIS-related plots).
 * 
 * @author William KM Lai
 * @see scriptmanager.scripts.Read_Analysis.ScalingFactor
 */
public class ScalingPlotter{

	/**
	 * Plot the list of (x,y) coordinates from two parallel data array inputs
	 * and decorate with customized figure labels.
	 * 
	 * @param Xaxis
	 *            list of x values
	 * @param Yaxis
	 *            list of y values
	 * @param scalingTotal
	 *            draw a red line at the y-axis for this value
	 * @param scalingRatio
	 *            draw a red line at the x-axis for this value
	 * @param title
	 *            title of chart
	 * @param xName
	 *            label for x-axis
	 * @param yName
	 *            label for y-axis
	 * @return the built scatter plot
	 */
	public static ChartPanel generateXYplot(List<Double> Xaxis, List<Double> Yaxis, double scalingTotal, double scalingRatio, String title, String xName, String yName){
		final JFreeChart chart = ChartFactory.createScatterPlot(title, "X", "Y", new DefaultXYDataset(), PlotOrientation.VERTICAL, false, false, false);
	        chart.setBackgroundPaint(Color.white);
	        chart.setBorderVisible(false);
	        
		    final XYPlot plot = chart.getXYPlot();
	        plot.setBackgroundPaint(Color.white);
	        plot.setOutlineVisible(false);
	        plot.setDomainGridlinesVisible(false);
	        plot.setRangeGridlinesVisible(false);
	        plot.setDomainZeroBaselineVisible(true);
	        plot.setRangeZeroBaselineVisible(true);
	        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

	        //Load XY Scatterplot dataset
		    DefaultXYDataset dataset = new DefaultXYDataset();
			double[][] data = new double[2][Xaxis.size()];
		    for(int x = 0; x < Xaxis.size(); x++) {
		    	data[0][x] = Xaxis.get(x);
		    	data[1][x] = Yaxis.get(x);
		    }
		    dataset.addSeries("DATA", data);
		    plot.setDataset(dataset);

		    //Set X-axis
		    plot.setDomainAxis(initializeLogAxis());
		    ValueAxis daxis=plot.getDomainAxis();   	
        	//Set Y-axis
		    plot.setRangeAxis(initializeLogAxis());
		    ValueAxis raxis=plot.getRangeAxis();

	        //Set axis labels
	        daxis.setLabel(xName);
	        raxis.setLabel(yName);
		    int xAxisLabelFontSize=28;
		    int yAxisLabelFontSize=28;
		    int xAxisTickLabelFontSize=14;
		    int yAxisTickLabelFontSize=14;
		    daxis.setLabelFont(new Font("Tahoma", Font.BOLD, xAxisLabelFontSize));
	        raxis.setLabelFont(new Font("Tahoma", Font.BOLD, yAxisLabelFontSize));
	        daxis.setTickLabelFont(new Font("Tahoma", Font.PLAIN, xAxisTickLabelFontSize));
	        raxis.setTickLabelFont(new Font("Tahoma", Font.PLAIN, yAxisTickLabelFontSize));

	        //Set datapoint appearance
	        int dotSize = 3;
	        Color dotColor = new Color(0, 0, 0);
	        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
	        renderer.setSeriesShape(0, new Ellipse2D.Double(0, 0, dotSize, dotSize));
			renderer.setSeriesPaint(0, dotColor);
			renderer.setSeriesLinesVisible(0, false);
			renderer.setSeriesShapesVisible(0, true);
			plot.setRenderer(renderer);
	        
			// Set red line at y-axis of scaling factor
			ValueMarker Dmarker = new ValueMarker(scalingTotal);  // position is the value on the axis
	    	Dmarker.setPaint(Color.red);
	    	plot.addDomainMarker(Dmarker);
	    	// Set red line at x-axis of scaling factor
	    	ValueMarker Rmarker = new ValueMarker(scalingRatio);  // position is the value on the axis
	    	Rmarker.setPaint(Color.red);
	    	plot.addRangeMarker(Rmarker);
	    	
	    	// Set chart size
		    int width = 800, height = 800;
			final ChartPanel chartPanel = new ChartPanel(chart);
			chartPanel.setPreferredSize(new java.awt.Dimension(width, height));
			return chartPanel;
		}

		/**
		 * Initialize a log-scale axis that is black and bounded from 1 to 1
		 * million (1e7). (helper method for generateXYplot)
		 * 
		 * @return the formatted log-scale axis
		 */
		public static ValueAxis initializeLogAxis() {
			ValueAxis axis = new LogAxis();
		    axis.setAxisLinePaint(Color.black);
		    axis.setLabelPaint(Color.black);
		    axis.setTickLabelPaint(Color.BLACK);
		    axis.setTickMarkPaint(Color.BLACK);
		    axis.setLowerBound(1.0);
		    axis.setUpperBound(1000000.0);
            if(axis instanceof org.jfree.chart.axis.LogAxis)
            	((LogAxis)axis).setTickUnit(new NumberTickUnit(1.0));
            axis.setAutoRange(true);
            return axis;
		}

		/**
		 * Return the minimum value in a list of Doubles
		 * 
		 * @param vector the list to search
		 * @return min value, Double.MAX_VALUE if empty list
		 */
	    public static double getMin(List<Double> vector){
		    	double min = Double.MAX_VALUE;
		    	for(Double d : vector) { if(d.doubleValue() < min) { min = d.doubleValue(); } }
		    	return min;
	    }

		/**
		 * Return the maximum value in a list of Doubles
		 * 
		 * @param vector the list to search
		 * @return max value, -Double.MAX_VALUE if empty list
		 */
		public static double getMax(List<Double> vector){
		    	double max = -Double.MAX_VALUE;
		    	for(Double d : vector) { if(d.doubleValue() > max) { max = d.doubleValue(); } }
		    	return max;
	    }
}
