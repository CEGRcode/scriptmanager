package scriptmanager.window_interface.Figure_Generation;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import scriptmanager.objects.CustomExceptions.OptionException;
import scriptmanager.scripts.Figure_Generation.ThreeColorHeatMap;

/**
 * Output wrapper for running ThreeColorHeatMap script and reporting when script is completed
 * @see scriptmanager.scripts.Figure_Generation.ThreeColorHeatMap
 * @see scriptmanager.window_interface.Figure_Generation.ThreeColorHeatMapWindow
 */
@SuppressWarnings("serial")
public class ThreeColorHeatMapOutput extends JFrame {

	protected static ArrayList<File> SAMPLE = null;

	public static Color MAXCOLOR;
	public static Color MIDCOLOR;
	public static Color MINCOLOR;
	public static Color NANCOLOR;

	protected static int startROW = 1;
	protected static int startCOL = 2;
	protected static int pixelHeight = 600;
	protected static int pixelWidth = 200;
	protected static String scaleType = "treeview";

	protected boolean percentileMin = false;
	protected boolean percentileMid = false;
	protected boolean percentileMax = false;
	protected static double MAXVAL = 10;
	protected static double MIDVAL = 0;
	protected static double MINVAL = -10;
	protected static boolean excludeZeros = false;

	protected static boolean OUTPUTSTATUS = false;
	protected static File OUT_DIR = null;

	public static double COLOR_RATIO = 1;

	JTabbedPane newpane;

	/**
	 * Creates a new instance of a ThreeColorHeatMap with given attributes
	 * @param in CDT formatted matrix file
	 * @param c_min Color to represent minimum values
	 * @param c_mid Color to represent mid values
	 * @param c_max Color to represent maximum values
	 * @param c_nan Color to represent missing values
	 * @param startR Starting row of the CDT file (Zero indexed)
	 * @param startC Starting column of the CDT file (Zero indexed)
	 * @param pHeight Height of resulting heat map (# pixels)
	 * @param pWidth Width of resulting heat map (# pixels)
	 * @param scale Scale compression type
	 * @param minPstatus Minimum percentile value
	 * @param midPstatus Mid percentile value
	 * @param maxPstatus Maximum percentile value
	 * @param min_quant Minimum absolute value
	 * @param mid_quant Mid absolute value
	 * @param max_quant Maximum absolute value
	 * @param exZ Exclude zero's in percentile threshold calculations
	 * @param output Output file
	 * @param outstatus Whether a file should be output
	 */
	public ThreeColorHeatMapOutput(ArrayList<File> in,
			Color c_max, Color c_mid, Color c_min, Color c_nan,
			int startR, int startC, int pHeight, int pWidth, String scale,
			boolean pStatusMin, boolean pStatusMid, boolean pStatusMax,
			double max_quant, double mid_quant, double min_quant,
			boolean exZ, File out_dir, boolean outstatus) {
		setTitle("ThreeColorHeatMap");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);

		newpane = new JTabbedPane(JTabbedPane.TOP);
		this.getContentPane().add(newpane);

		SAMPLE = in;
		MAXCOLOR = c_max;
		MIDCOLOR = c_mid;
		MINCOLOR = c_min;
		NANCOLOR = c_nan;

		startROW = startR;
		startCOL = startC;

		pixelHeight = pHeight;
		pixelWidth = pWidth;
		scaleType = scale;

		percentileMin = pStatusMin;
		percentileMid = pStatusMid;
		percentileMax = pStatusMax;
		MAXVAL = max_quant;
		MIDVAL = mid_quant;
		MINVAL = min_quant;
		excludeZeros = exZ;

		OUT_DIR = out_dir;
		OUTPUTSTATUS = outstatus;
		System.out.println(OUTPUTSTATUS);
	}

	/**
	 * Runs the ThreeColorHeatmap script
	 * @throws IOException
	 */
	public void run() throws IOException {
		String postRunDialog = "";
		for (int x = 0; x < SAMPLE.size(); x++) {
			File OUTPUT = new File(SAMPLE.get(x).getName().split("\\.")[0] + "_" + scaleType + ".png");
			if (OUT_DIR != null) {
				OUTPUT = new File(OUT_DIR.getCanonicalPath() + File.separator + OUTPUT);
			}
			// Execute script
			try {
				ThreeColorHeatMap script_object = new ThreeColorHeatMap(SAMPLE.get(x),
						MINCOLOR, MIDCOLOR, MAXCOLOR, NANCOLOR,
						startROW, startCOL, pixelHeight, pixelWidth, scaleType,
						percentileMin, percentileMid, percentileMax,
						MAXVAL, MIDVAL, MINVAL, excludeZeros, OUTPUT, OUTPUTSTATUS);
				script_object.run();
				// Output image/error to GUI
				newpane.addTab(OUTPUT.getName(), new JScrollPane(script_object.getImg(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
			} catch (OptionException e) {
				postRunDialog += e.getMessage() + "\n";
			}
			firePropertyChange("heat", x, x + 1);
		}
		System.out.println("Program Complete");
		System.out.println(getTimeStamp());
		if (!postRunDialog.equals("")) {
			JOptionPane.showMessageDialog(null, postRunDialog);
		}
	}

	private static String getTimeStamp() {
		Date date = new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}

}

