package window_interface.Figure_Generation;

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

import objects.CustomExceptions.OptionException;
import scripts.Figure_Generation.ThreeColorHeatMap;

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
	protected static String FILEID = null;

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
		OUTPUTSTATUS = outstatus;
		System.out.println(OUTPUTSTATUS);
	}

	public void run() throws IOException {
		String postRunDialog = "";
		for (int x = 0; x < SAMPLE.size(); x++) {
			String OUTPUT = SAMPLE.get(x).getName().split("\\.")[0] + "_" + scaleType + ".png";
			if (OUT_DIR != null) {
				OUTPUT = OUT_DIR.getCanonicalPath() + File.separator + OUTPUT;
			}
			// Execute script
			try {
				ThreeColorHeatMap script_object = new ThreeColorHeatMap(SAMPLE.get(x),
						MINCOLOR, MIDCOLOR, MAXCOLOR, NANCOLOR,
						startROW, startCOL, pixelHeight, pixelWidth, scaleType,
						percentileMin, percentileMid, percentileMax,
						MAXVAL, MIDVAL, MINVAL, excludeZeros, new File(OUTPUT), OUTPUTSTATUS);
				script_object.run();
				// Output image/error to GUI
				newpane.addTab(FILEID, new JScrollPane(script_object.getImg(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
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

