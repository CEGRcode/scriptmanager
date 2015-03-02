package scripts.Visualization;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class HeatMapPlot extends JFrame {
	
	private File INPUT = null;
	private File OUTPUT = null;
	private Color HEATMAP = null;
	
	JTabbedPane newpane;
	
	public HeatMapPlot(File in, Color heat, File out) {
		setTitle("Heatmap");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 800, 600);
		
		newpane = new JTabbedPane(JTabbedPane.TOP);
		this.getContentPane().add(newpane);
		
		INPUT = in;
		OUTPUT = out;
		HEATMAP = heat;
	}
	
	public void run() throws IOException {
		int width = 2;
		int height = 2;
		
		List<String[]> seq = new ArrayList<String[]>();
		int maxLen = 0;

		Scanner scan = new Scanner(INPUT);
		while (scan.hasNextLine()) {
			String[] temp = scan.nextLine().split("\t");
			if(!temp[0].contains("YORF")) {
				if (maxLen < temp.length) maxLen = temp.length;
				seq.add(temp);
			}
		}
		scan.close();
		int pixwidth = maxLen * width;
		int pixheight = seq.size() * height;
		
		System.setProperty("java.awt.headless", "true");
		BufferedImage im = new BufferedImage(pixwidth, pixheight,BufferedImage.TYPE_INT_ARGB);
        Graphics g = im.getGraphics();
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.WHITE);
        g2.fillRect(0,0,pixwidth, pixheight);
        
        int count = 0;
        for (int x = 0; x < seq.size(); x++){
        	String[] ID = seq.get(x);

        	for (int j=2;j< ID.length;j++){
        		if(Double.parseDouble(ID[j]) > 0) g.setColor(HEATMAP);
        		else g.setColor(Color.WHITE);
                g.fillRect(j*width, count*height, width, height);
        	}
            count++;
        }
        try {
            ImageIO.write(im, "png", OUTPUT);
            newpane.addTab(OUTPUT.getName(),  new JScrollPane(new JLabel(new ImageIcon(im)), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        }  catch (IOException ex) {
            ex.printStackTrace();
        }
	}
}