package scriptmanager.window_interface.Figure_Generation;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import scriptmanager.cli.Figure_Generation.LabelHeatMapCLI;
import scriptmanager.cli.Figure_Generation.MergeHeatMapCLI;
import scriptmanager.objects.CustomOutputStream;
import scriptmanager.objects.CustomExceptions.OptionException;
import scriptmanager.objects.LogItem;
import scriptmanager.scripts.Figure_Generation.LabelHeatMap;

@SuppressWarnings("serial")
public class LabelHeatMapOutput extends JFrame {

	protected static ArrayList<File> SAMPLE = null;
	public static Color color = Color.BLACK;
	
	public static Integer borderWidth;
	public static Integer xTickHeight;
	public static Integer fontSize;

	public static String xLeftLabel;
	public static String xMidLabel;
	public static String xRightLabel;

	public static String xLabel;
	public static String yLabel;

	protected static File OUT_DIR = null;

	JTabbedPane newpane;
	
	public LabelHeatMapOutput(ArrayList<File> in, File out_dir, Color c,
			Integer bORDERWIDTH, Integer xTICKHEIGHT, Integer fONTSIZE,
			String xLEFTLABEL, String xMIDLABEL, String xRIGHTLABEL,
			String xLABEL, String yLABEL) {
		setTitle("Heatmap");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);

		newpane = new JTabbedPane(JTabbedPane.TOP);
		this.getContentPane().add(newpane);

		SAMPLE = in;
		OUT_DIR = out_dir;
		color = c;

		borderWidth = bORDERWIDTH;
		xTickHeight = xTICKHEIGHT;
		fontSize = fONTSIZE;
		
		xLeftLabel = xLEFTLABEL;
		xMidLabel = xMIDLABEL;
		xRightLabel = xRIGHTLABEL;
		
		xLabel = xLABEL;
		yLabel = yLABEL;
	}

	public void run() throws IOException, OptionException {
		LogItem old_li = null;
		for (int x = 0; x < SAMPLE.size(); x++) {
			File OUTPUT = new File(SAMPLE.get(x).getName().split("\\.")[0] + "_label.svg");
			if (OUT_DIR != null) {
				OUTPUT = new File(OUT_DIR.getCanonicalPath() + File.separator + OUTPUT.getName());
			}
			old_li = new LogItem("");
			JTextArea textArea = new JTextArea();
			// Output image/error to GUI
			newpane.addTab(OUTPUT.getName(), new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
			PrintStream jtxtPrintStream = new PrintStream(new CustomOutputStream(textArea));
			// Initialize LogItem
			String command = LabelHeatMapCLI.getCLIcommand(SAMPLE.get(x), OUTPUT, color,
					borderWidth, xTickHeight, fontSize,
					xLeftLabel, xMidLabel, xRightLabel,
					xLabel, yLabel);
			LogItem new_li = new LogItem(command);
			firePropertyChange("log", old_li, new_li);
			// Execute script
			LabelHeatMap script_obj = new LabelHeatMap(SAMPLE.get(x), OUTPUT, color,
					borderWidth, xTickHeight, fontSize,
					xLeftLabel, xMidLabel, xRightLabel,
					xLabel, yLabel, jtxtPrintStream);
			script_obj.run();
			// Update log item
			new_li.setStopTime(new Timestamp(new Date().getTime()));
			new_li.setStatus(0);
			old_li = new_li;
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
