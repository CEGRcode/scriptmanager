package scripts.Visualization;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class MergeHeatMapPlot extends JFrame {
	private ArrayList<File> pngFiles = null;
	private ArrayList<File> senseFile = null;
	private ArrayList<File> antiFile = null;
	private File OUTPUT_PATH = null;
	
	JTabbedPane newpane;
	
	public MergeHeatMapPlot(ArrayList<File> in, File out) {
		setTitle("Merged Heatmap");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 800, 600);
		
		newpane = new JTabbedPane(JTabbedPane.TOP);
		this.getContentPane().add(newpane);

		pngFiles = in;
		OUTPUT_PATH = out;
	}
	
	public void run() throws IOException {
		senseFile = new ArrayList<File>();
		antiFile = new ArrayList<File>();
		for(int x = 0; x < pngFiles.size(); x++) {
			if(pngFiles.get(x).getName().toLowerCase().contains("sense")) {
				senseFile.add(pngFiles.get(x));
			} else if(pngFiles.get(x).getName().toLowerCase().contains("anti")) {
				antiFile.add(pngFiles.get(x));
			}
		}
		
		for(int x = 0; x < senseFile.size(); x++) {
			String name = senseFile.get(x).getName();
			String out = name.substring(0, name.indexOf("sense"));
			int matchIndex = -999;
			for(int y = 0; y < antiFile.size(); y++) {
				if(antiFile.get(y).getName().contains(out)) { matchIndex = y; }
			}
			out = out + "merge.png";
			if(OUTPUT_PATH != null) { out = OUTPUT_PATH.getCanonicalPath() + File.separator + out; }

			if(matchIndex != -999) { mergePNG(senseFile.get(x), antiFile.get(matchIndex), new File(out)); }
			else { mergePNG(senseFile.get(x), null, new File(out)); }
			firePropertyChange("merge", x, x + 1);
		}		
	}
	
	public void mergePNG(File INPUT1, File INPUT2, File OUTPUT) throws IOException {
		JLabel picLabel = null;
		if(INPUT2 == null) {
			picLabel = new JLabel("No match for file: " + INPUT1.getName());
		} else {
			BufferedImage image = ImageIO.read(INPUT1);
			BufferedImage overlay = ImageIO.read(INPUT2);
			if(image.getWidth() != overlay.getWidth()) {
				picLabel = new JLabel("Unequal Pixel Width!!!\n" + INPUT1.getName() + ":\t" + image.getWidth() + "\n" + INPUT2.getName() + ":\t" + overlay.getWidth());
			} else if(image.getHeight() != overlay.getHeight()) {
				picLabel = new JLabel("Unequal Pixel Height!!!\n" + INPUT1.getName() + ":\t" + image.getHeight() + "\n" + INPUT2.getName() + ":\t" + overlay.getHeight());
			} else {
				int w = Math.max(image.getWidth(), overlay.getWidth());
				int h = Math.max(image.getHeight(), overlay.getHeight());
				BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				for (int x = 0; x < combined.getWidth(); x++) {
					for (int y = 0; y < combined.getHeight(); y++) {
						int image_rgb = image.getRGB(x, y);
						int overlay_rgb = overlay.getRGB(x, y);
				
						int image_alpha = (image_rgb & 0xFF000000) >>> 24;
					    int image_red = (image_rgb & 0x00FF0000) >>> 16;
					    int image_green = (image_rgb & 0x0000FF00) >>> 8;
					    int image_blue  = (image_rgb & 0x000000FF) >>> 0;
								
						int overlay_alpha = (overlay_rgb & 0xFF000000) >>> 24;
					    int overlay_red = (overlay_rgb & 0x00FF0000) >>> 16;
					    int overlay_green = (overlay_rgb & 0x0000FF00) >>> 8;
					    int overlay_blue  = (overlay_rgb & 0x000000FF) >>> 0;
				
					    int new_rgb;
					    if(image_red == image_green && image_green == image_blue && image_red >= 250) {
					    	new_rgb = (overlay_alpha << 24) | (overlay_red << 16) | (overlay_green << 8) | overlay_blue;
					    } else if(overlay_red == overlay_green && overlay_green == overlay_blue && overlay_red >= 250) {
					    	new_rgb = (image_alpha << 24) | (image_red << 16) | (image_green << 8) | image_blue;
					    } else {
					    	int new_alpha = (image_alpha + overlay_alpha) / 2;
					    	int new_red = (image_red + overlay_red) / 2;
					    	int new_green = (image_green + overlay_green) / 2;
					    	int new_blue = (image_blue + overlay_blue) / 2;
				
					    	new_rgb = (new_alpha << 24) | (new_red << 16) | (new_green << 8) | new_blue;
					    }
						combined.setRGB(x, y, new_rgb);
					}
				}
				//Output new image
				ImageIO.write(combined, "PNG", OUTPUT);
				picLabel = new JLabel(new ImageIcon(combined));
			}
		}
		//Output image/error to GUI
		newpane.addTab(OUTPUT.getName(), new JScrollPane(picLabel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
	}
}