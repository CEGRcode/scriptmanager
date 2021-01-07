package window_interface.Figure_Generation;

// import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import scripts.Figure_Generation.MergeHeatMapPlot;

@SuppressWarnings("serial")
public class MergeHeatMapOutput extends JFrame {
	private ArrayList<File> pngFiles = null;
	private ArrayList<File> senseFile = null;
	private ArrayList<File> antiFile = null;
	private File OUTPUT_PATH = null;

	JTabbedPane newpane;

	public MergeHeatMapOutput(ArrayList<File> in, File out) {
		setTitle("Merged Heatmap");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);

		newpane = new JTabbedPane(JTabbedPane.TOP);
		this.getContentPane().add(newpane);

		pngFiles = in;
		OUTPUT_PATH = out;
	}

	public void run() throws IOException {
		senseFile = new ArrayList<File>();
		antiFile = new ArrayList<File>();
		for (int x = 0; x < pngFiles.size(); x++) {
			if (pngFiles.get(x).getName().toLowerCase().contains("sense")) {
				senseFile.add(pngFiles.get(x));
			} else if (pngFiles.get(x).getName().toLowerCase().contains("anti")) {
				antiFile.add(pngFiles.get(x));
			}
		}

		for (int x = 0; x < senseFile.size(); x++) {
			String name = senseFile.get(x).getName();
			String out = name.substring(0, name.lastIndexOf("sense"));
			int matchIndex = -999;
			for (int y = 0; y < antiFile.size(); y++) {
				if (antiFile.get(y).getName().contains(out)) {
					matchIndex = y;
				}
			}
			out = out + "merge.png";
			if (OUTPUT_PATH != null) {
				out = OUTPUT_PATH.getCanonicalPath() + File.separator + out;
			}

			// Store results in JFrame window
			File OUT = new File(out);
			if (matchIndex != -999) {
				// Execute script
				JLabel pic = MergeHeatMapPlot.mergePNG(senseFile.get(x), antiFile.get(matchIndex), OUT);
				addImage(OUT.getName(), pic);
			} else {
				JLabel pic = MergeHeatMapPlot.mergePNG(senseFile.get(x), null, OUT);
				addImage(OUT.getName(), pic);
			}
			firePropertyChange("merge", x, x + 1);
		}
	}

	private void addImage(String name, JLabel pic) {
		newpane.addTab(name, new JScrollPane(pic, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
	}

}