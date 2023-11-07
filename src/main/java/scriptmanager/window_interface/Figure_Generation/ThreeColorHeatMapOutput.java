package scriptmanager.window_interface.Figure_Generation;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import scriptmanager.cli.Figure_Generation.ThreeColorHeatMapCLI;
import scriptmanager.objects.LogItem;
import scriptmanager.objects.CustomExceptions.OptionException;
import scriptmanager.scripts.Figure_Generation.ThreeColorHeatMap;
import scriptmanager.util.ExtensionFileFilter;

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

	protected static boolean OUTPUT_STATUS = false;
	protected static File OUT_DIR = null;

	public static double COLOR_RATIO = 1;

	JTabbedPane newpane;

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
		OUTPUT_STATUS = outstatus;
	}

	public void run() throws IOException, OptionException {
		LogItem old_li = null;
		for (int x = 0; x < SAMPLE.size(); x++) {
			// Construct output filename
			String NAME = ExtensionFileFilter.stripExtensionIgnoreGZ(SAMPLE.get(x)) + "_" + scaleType + ".png";
			File OUT_FILEPATH = new File(NAME);
			if (OUT_DIR != null) {
				OUT_FILEPATH = new File(OUT_DIR.getCanonicalPath() + File.separator + NAME);
			}
			// Initialize LogItem
			String command = ThreeColorHeatMapCLI.getCLIcommand(SAMPLE.get(x), MINCOLOR, MIDCOLOR, MAXCOLOR, NANCOLOR,
					startROW, startCOL, pixelHeight, pixelWidth, scaleType,
					percentileMin, percentileMid, percentileMax,
					MAXVAL, MIDVAL, MINVAL, excludeZeros, OUT_FILEPATH);
			LogItem new_li = new LogItem(command);
			if (OUTPUT_STATUS) { firePropertyChange("log", old_li, new_li); }
			// Execute script
			ThreeColorHeatMap script_object = new ThreeColorHeatMap(SAMPLE.get(x), MINCOLOR, MIDCOLOR, MAXCOLOR, NANCOLOR,
					startROW, startCOL, pixelHeight, pixelWidth, scaleType,
					percentileMin, percentileMid, percentileMax,
					MAXVAL, MIDVAL, MINVAL, excludeZeros, OUT_FILEPATH, OUTPUT_STATUS);
			script_object.run();
			// Update log item
			new_li.setStopTime(new Timestamp(new Date().getTime()));
			new_li.setStatus(0);
			old_li = new_li;
			// Output image
			newpane.addTab(OUT_FILEPATH.getName(), new JScrollPane(script_object.getImg(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
			// Update progress
			firePropertyChange("progress", x, x + 1);
		}
		// Update log at completion
		if (OUTPUT_STATUS) { firePropertyChange("log", old_li, null); }
	}
}
