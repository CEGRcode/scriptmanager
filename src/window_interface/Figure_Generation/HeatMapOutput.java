package window_interface.Figure_Generation;

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

import scripts.Figure_Generation.HeatmapPlot;

@SuppressWarnings("serial")
public class HeatMapOutput extends JFrame {

	protected static ArrayList<File> SAMPLE = null;
	
	protected static int startROW = 1;
	protected static int startCOL = 2;	
	protected static int pixelHeight = 600;
	protected static int pixelWidth = 200;
	
	protected static String scaleType = "treeview";
	protected static double quantile = 0.9;
	protected static double absolute = -999;
	
	public static Color MINCOLOR = new Color(255, 255, 255);
	public static Color MAXCOLOR = new Color(255, 0, 0);
	
	protected static boolean OUTPUTSTATUS = false;
	protected static File OUTPUTPATH = null;
	protected static String FILEID = null;	

	private static ArrayList<double[]> MATRIX = null;
	public static double COLOR_RATIO = 1;	
	
	JTabbedPane newpane;

	public HeatMapOutput(ArrayList<File> in, Color c, int startR, int startC, int pHeight, int pWidth, String scale, double abs, double quant, File OUT, boolean outstatus) {
		setTitle("Heatmap");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);
		
		newpane = new JTabbedPane(JTabbedPane.TOP);
		this.getContentPane().add(newpane);

		SAMPLE = in;
		MAXCOLOR = c;
		startROW = startR;
		startCOL = startC;
		pixelHeight = pHeight;
		pixelWidth = pWidth;
		scaleType = scale;
		
		absolute = abs;
		quantile = quant;
		
		OUTPUTPATH = OUT;
		OUTPUTSTATUS = outstatus;
		System.out.println(OUTPUTSTATUS);
	}
	
	public void run() throws IOException {	
		for(int x = 0; x < SAMPLE.size(); x++) {
			
			String FILEID = SAMPLE.get(x).getName().split("\\.")[0] + "_" + scaleType + ".png";
			String OUTPUT = FILEID;
			if(OUTPUTPATH != null) { OUTPUT = OUTPUTPATH.getCanonicalPath() + File.separator + FILEID; }
			
			//Execute script
			HeatmapPlot script_object = new HeatmapPlot(SAMPLE.get(x), MAXCOLOR, startROW, startCOL, pixelHeight, pixelWidth, scaleType, absolute, quantile, new File(OUTPUT), OUTPUTSTATUS );
			script_object.run();
			JLabel picLabel = script_object.getImg();
			
			//Output image/error to GUI
			newpane.addTab(FILEID, new JScrollPane(picLabel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
			firePropertyChange("heat", x, x + 1);
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
