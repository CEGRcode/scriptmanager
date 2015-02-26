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

public class FourColorPlot {
	/**
	 * Visualize sequences as color pixels
	 * @param width, width of each base, in pixel
	 * @param height, height of each base, in pixel
	 */
	public static void generatePLOT(File input, File output) throws IOException {
		int width = 3;
		int height = 1;
		
		List<String> seq = new ArrayList<String>();
		int maxLen = 0;

		Scanner scan = new Scanner(input);
		while (scan.hasNextLine()) {
			String temp = scan.nextLine();
			if(!temp.contains(">")) {
				if (maxLen < temp.length()) maxLen = temp.length();
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
        	String s = seq.get(x);
        	char[] letters = s.toCharArray();
        	for (int j=0;j<letters.length;j++){
        		switch(letters[j]){
        		case 'A':
        		case 'a':
        			g.setColor(new Color(254, 25, 24));
        			break;
        		case 'C':
        		case 'c':
                    g.setColor(new Color(43, 49, 246));
        			break;
        		case 'G':
        		case 'g':
                    g.setColor(new Color(252, 252, 80));
        			break;
        		case 'T':
        		case 't':
                    g.setColor(new Color(50, 204, 60));
        			break;
        		case '-':
                    g.setColor(Color.WHITE);
        			break;
                default:
                	g.setColor(Color.GRAY);
        		}
                g.fillRect(j*width, count*height, width, height);
        	}
            count++;
        }
        try {
            ImageIO.write(im, "png", output);
        }  catch (IOException ex) {
            ex.printStackTrace();
        }
	}
}
