package scriptmanager.window_interface.Figure_Generation;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import scriptmanager.cli.Figure_Generation.TwoColorHeatMapCLI;
import scriptmanager.objects.LogItem;
import scriptmanager.scripts.Figure_Generation.TwoColorHeatMap;
import scriptmanager.util.ExtensionFileFilter;

@SuppressWarnings("serial")
public class TwoColorHeatMapOutput extends JFrame {

	protected static ArrayList<File> SAMPLE = null;

	protected static int startROW = 1;
	protected static int startCOL = 2;
	protected static int pixelHeight = 600;
	protected static int pixelWidth = 200;

	protected static String scaleType = "treeview";
	protected static double quantile = 0.9;
	protected static double absolute = -999;

	public static Color MAXCOLOR = new Color(255, 0, 0);
	public boolean transparentBackground = false;

	protected static boolean OUTPUTSTATUS = false;
	protected static File OUT_DIR = null;

	public static double COLOR_RATIO = 1;

	JTabbedPane newpane;

	public TwoColorHeatMapOutput(ArrayList<File> in, Color c, int startR, int startC, int pHeight, int pWidth,
			String scale, double abs, double quant, File out_dir, boolean outstatus, boolean trans) {
		setTitle("Heatmap");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);

		newpane = new JTabbedPane(JTabbedPane.TOP);
		this.getContentPane().add(newpane);

		SAMPLE = in;
		MAXCOLOR = c;
		transparentBackground = trans;
		startROW = startR;
		startCOL = startC;
		pixelHeight = pHeight;
		pixelWidth = pWidth;
		scaleType = scale;

		absolute = abs;
		quantile = quant;

		OUT_DIR = out_dir;
		OUTPUTSTATUS = outstatus;
	}

	public void run() throws IOException {
		LogItem old_li = null;
		for (int x = 0; x < SAMPLE.size(); x++) {
			File OUTPUT = new File(ExtensionFileFilter.stripExtensionIgnoreGZ(SAMPLE.get(x)) + "_" + scaleType + ".png");
			if (OUT_DIR != null) {
				OUTPUT = new File(OUT_DIR.getCanonicalPath() + File.separator + OUTPUT.getName());
			}
			old_li = new LogItem("");
			// Initialize LogItem
			String command = TwoColorHeatMapCLI.getCLIcommand(SAMPLE.get(x), OUTPUT, MAXCOLOR, startROW, startCOL,
					pixelHeight, pixelWidth, scaleType, absolute, quantile, transparentBackground);
			LogItem new_li = new LogItem(command);
			firePropertyChange("log", old_li, new_li);
			// Execute script
			TwoColorHeatMap script_object = new TwoColorHeatMap(SAMPLE.get(x), MAXCOLOR, startROW, startCOL,
					pixelHeight, pixelWidth, scaleType, absolute, quantile, OUTPUT, OUTPUTSTATUS, transparentBackground);
			script_object.run();
			JLabel picLabel = script_object.getImg();
			// Update log item
			new_li.setStopTime(new Timestamp(new Date().getTime()));
			new_li.setStatus(0);
			old_li = new_li;
			// Output image/error to GUI
			newpane.addTab(OUTPUT.getName(), new JScrollPane(picLabel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
			firePropertyChange("heat", x, x + 1);
		}
		firePropertyChange("log", old_li, null);
		System.out.println("Program Complete");
		System.out.println(getTimeStamp());
	}

	private static String getTimeStamp() {
		Date date = new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}

}
