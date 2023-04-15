package scriptmanager.window_interface.Figure_Generation;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import org.jfree.chart.ChartPanel;

import scriptmanager.scripts.Figure_Generation.PlotComposite;

/**
 * Call script on each input and display chart results in a tabbed window where
 * all composite data images can be viewed.
 * 
 * @author Olivia Lang
 * @see scriptmanager.scripts.Figure_Generation.PlotComposite
 * @see scriptmanager.window_interface.Figure_Generation.PlotCompositeWindow
 *
 */
@SuppressWarnings("serial")
public class PlotCompositeOutput extends JFrame {

	protected static ArrayList<File> SAMPLE = null;
	protected static File OUT_DIR;
	protected static boolean outputImg;
	
	protected static boolean includeLegend = true;
	protected static int pxHeight;
	protected static int pxWidth;

	JTabbedPane newpane;

	/**
	 * Store inputs and initialize a tabbed pane to display the results.
	 * 
	 * @param in list of files to make composites from (one chart per file)
	 * @param o_dir output directory save composites to
	 * @param o whether to save composites or not (save = true, don't save = false)
	 * @param l whether or not to include a legend (include legend = true, don't include = false)
	 * @param ph height of image to save (in pixels)
	 * @param pw width of image to save (in pixels)
	 */
	public PlotCompositeOutput(ArrayList<File> in, File o_dir, boolean o, boolean l, int ph, int pw) {
		setTitle("Composite");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 800, 600);

		newpane = new JTabbedPane(JTabbedPane.TOP);
		this.getContentPane().add(newpane);

		SAMPLE = in;
		OUT_DIR = o_dir;
		outputImg = o;
		includeLegend = l;
		pxHeight = ph;
		pxWidth = pw;
	}

	/**
	 * Loop through and call the script on each input file to generate the line
	 * charts and add them to the tabbed pane to be displayed.
	 * <br>
	 * Each chart's title will be the filename. Legend will be included if
	 * indicated by input. Default colors will be used for the chart (see
	 * scripts.Figure_Generation.PlotComposite for details). If specified by the
	 * input parameter values, a PNG image is saved to the indicated output
	 * directory with the passed pixel values for the saved file dimensions.
	 * 
	 * @throws IOException
	 */
	public void run() throws IOException {
		for (int x = 0; x < SAMPLE.size(); x++) {
			try {
				// Execute script
				ChartPanel chart = new ChartPanel(PlotComposite.plotCompositeFile(SAMPLE.get(x), OUT_DIR, outputImg, SAMPLE.get(x).getName(), null, includeLegend, pxHeight, pxWidth));
				chart.setPreferredSize(new java.awt.Dimension(500, 270));
				// Output image/error to GUI
				newpane.addTab(SAMPLE.get(x).getName(), chart);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
			
			firePropertyChange("composite", x, x + 1);
		}
		System.out.println("Program Complete");
		System.out.println(getTimeStamp());
	}

	private static String getTimeStamp() {
		Date date = new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}
