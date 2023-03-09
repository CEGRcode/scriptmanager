package scriptmanager.charts;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;

public class HeatMap {
	
	public HeatMap() {
		
	}
	
	public static ChartPanel createCorrelationHeatmap(String[] labels, double[][] MATRIX, File output) {
        // create a paint-scale and a legend showing it
        LookupPaintScale paintScale = new LookupPaintScale(0, 1, Color.black);       
        paintScale.add(0.0, new Color(0, 0, 255));
        paintScale.add(0.11, new Color(63, 63, 255));
        paintScale.add(0.22, new Color(126, 126, 255));
        paintScale.add(0.33, new Color(189, 189, 255));
        paintScale.add(0.44, new Color(255, 255, 255));
        paintScale.add(0.55, new Color(255, 189, 189));
        paintScale.add(0.66, new Color(255, 126, 126));
        paintScale.add(0.77, new Color(255, 63, 63));
        paintScale.add(0.88, new Color(255, 0, 0));
		
		NumberAxis xAxis = new NumberAxis(null);
		xAxis.setVisible(false);
		String[] reverselabels = new String[labels.length];
		for(int x = 0; x < labels.length; x++) { reverselabels[labels.length - x - 1]  = labels[x]; }
        SymbolAxis yAxis = new SymbolAxis("Experiments", reverselabels);
        XYPlot plot = new XYPlot(createDataset(MATRIX), xAxis, yAxis, null);
        XYBlockRenderer r = new XYBlockRenderer();
        r.setPaintScale(paintScale);
        r.setBlockHeight(1.0f);
        r.setBlockWidth(1.0f);
        plot.setRenderer(r);
        
        JFreeChart chart = new JFreeChart("Correlation Matrix", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        NumberAxis scaleAxis = new NumberAxis("Scale");
        scaleAxis.setAxisLinePaint(Color.white);
        scaleAxis.setTickMarkPaint(Color.white);        
        PaintScaleLegend legend = new PaintScaleLegend(paintScale, scaleAxis);
        legend.setSubdivisionCount(128);
        legend.setAxisLocation(AxisLocation.TOP_OR_RIGHT);
        legend.setPadding(new RectangleInsets(10, 10, 10, 10));
        legend.setStripWidth(20);
        legend.setPosition(RectangleEdge.RIGHT);
        legend.setBackgroundPaint(Color.WHITE);
        
        chart.addSubtitle(legend);
        chart.setBackgroundPaint(Color.white);
        
        if(output!=null){
			int width = 640;
			int height = 480;
			try{ ChartUtils.saveChartAsPNG(output, chart, width, height); }
			catch( IOException e ){ e.printStackTrace(); }
        }
        
        return new ChartPanel(chart);
    }
	
    private static XYZDataset createDataset(double[][] MATRIX) {
    	//The heatmap is based on a standard XY scatterplot as as such, is built with 0,0 in the lower left
    	//The matrix must be rotated such that the final output is consistent
        DefaultXYZDataset dataset = new DefaultXYZDataset();
        int N = MATRIX.length;
        for (int i = 0; i < N; i++) {
            double[][] data = new double[3][N];
            for (int j = 0; j < N; j++) {
                data[0][j] = j;
                data[1][j] = (N - 1) - i;
                data[2][j] = MATRIX[i][j];
            }
            dataset.addSeries("Series" + i, data);
        }
//        for(int x = 0; x < dataset.getSeriesCount(); x++) {
//        	for(int y =0 ; y < dataset.getItemCount(x); y++) {
//        		System.out.println(x + "\t" + y + "\t" + dataset.getZValue(x, y));
//        	}
//        }
        return dataset;
    }
}
