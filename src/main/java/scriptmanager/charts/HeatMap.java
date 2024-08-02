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

/**
 * Collection of static methods for formatting and plotting data into heatmaps
 * of matrix values
 * 
 * @author William KM Lai
 * @see scriptmanager.scripts.BAM_Statistics.BAMGenomeCorrelation
 */
public class HeatMap {

    /**
     * Constant value encoding the blue to white to red color scale. (0)
     */
    public final static short BLUEWHITERED = 0;
    /**
     * Constant value encoding the jet-like color scale. (0)
     */
    public final static short JETLIKE = 1;

    /**
     * Create a multi-color color scale inspired by Matplotlib's "jet" color scheme.
     * 15 starting colors are set to 15 values.
     * 
     * @return jet-like paintscale
     */
    private static LookupPaintScale getJetLikeScale() {
        LookupPaintScale paintScale = new LookupPaintScale(0, 1, Color.black);
        paintScale.add(0.938, Color.decode("#75140C"));    //dark blue
        paintScale.add(0.871, Color.decode("#C0281B"));
        paintScale.add(0.804, Color.decode("#EB4826"));
        paintScale.add(0.737, Color.decode("#FF7500"));
        paintScale.add(0.670, Color.decode("#F4BC41"));
        paintScale.add(0.603, Color.decode("#F3FC53"));
        paintScale.add(0.536, Color.decode("#C6FD64"));
        paintScale.add(0.469, Color.decode("#9EFC8A"));
        paintScale.add(0.402, Color.decode("#81FBBB"));
        paintScale.add(0.335, Color.decode("#6DE9EF"));
        paintScale.add(0.268, Color.decode("#08A3FF"));
        paintScale.add(0.201, Color.decode("#255AF6"));
        paintScale.add(0.134, Color.decode("#0912F5"));
        paintScale.add(0.134, Color.decode("#0912F5"));
        paintScale.add(0.067, Color.decode("#0600C9"));
        paintScale.add(0.000, Color.decode("#020080"));    //dark red
        return paintScale;
    }

    /**
	 * Create a paint scale using original BAM Genome Correlation color scale scheme:
	 * blue (low) to white (mid) to red (high).
	 * 
	 * @return original blue-white-red paintscale
	 */
    private static LookupPaintScale getBlueWhiteRedScale() {
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
        return paintScale;
    }

	/**
	 * Get paint scale based on scale encoding value.
	 * 
	 * @param type scale encoding for one of the preset paint scales (e.g.
	 *             REDWHITEBLUE, JETLIKE).
	 * @return paintscale for the given encoding
	 */
    public static LookupPaintScale getPresetPaintscale(short type) {
        if (type == BLUEWHITERED) { return getBlueWhiteRedScale(); }
        else if (type == JETLIKE) { return getJetLikeScale(); }
        // Use original red-white-blue paintscale as default
        return getBlueWhiteRedScale();
    }

    /**
	 * Create a correlation heatmap plot based on a square matrix of inputs with a
	 * strict percentile-based blue-to-red paint scale (9 levels).
	 * 
	 * @param labels (sample names) for labeling the y-axis (symmetrical plot so
	 *               x-axis labels are implied)
	 * @param MATRIX the symmetrical 2D array of values
	 * @param output the file to write the PNG chart to. File not written if this
	 *               value is null.
	 * @return the resulting correlation heatmap chart
	 */
    public static ChartPanel createCorrelationHeatmap(String[] labels, double[][] MATRIX, File output, LookupPaintScale paintScale) {
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
        
        // Save PNG if output filepath given
        if (output!=null) {
            int width = 640;
            int height = 480;
            try{ ChartUtils.saveChartAsPNG(output, chart, width, height); }
            catch( IOException ioe ){ ioe.printStackTrace(); }
            catch( Exception e ){ e.printStackTrace(); }
        }

        return new ChartPanel(chart);
    }


	/**
	 * Helper method for formatting data into the XYZDatset type
	 * 
	 * @param MATRIX the 2-d array of values to convert
	 * @return the formatted data series
	 */
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
