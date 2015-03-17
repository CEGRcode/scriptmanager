package scripts.Visualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JSplitPane;

import org.jfree.chart.ChartPanel;

import charts.CompositePlot;
import util.MatrixStatistics;
import window_interface.Visualization.HeatMapOptions;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class HeatMapPlot extends JFrame {
	
	private File INPUT = null;
	public File OUTPUT = null;
		
	private HeatMapOptions option = null;
	public Color MINCOLOR = null;
	public Color MAXCOLOR = null;

	public ArrayList<Double> STATS = null;
	private Vector<double[]> CDT = null;
	public double COLOR_RATIO = 0;
	public boolean allowResize = true;
		
	private ChartPanel COMPOSITE_PLOT = null;
	public BufferedImage HEAT_PLOT = null;
	
	private JPanel backPanel;
	private JPanel pnlOptions;
	public JLabel heatLabel;
	private JScrollPane heatPane;
	private JSplitPane HsplitPane;
	private JSplitPane VsplitPane;
	private JButton btnCompositeSeriesColor;
	 
	public HeatMapPlot(File in, Color mi, Color ma, File out) {
		setTitle("Heatmap");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 1000, 600);
		getContentPane().setLayout(new BorderLayout());
		
		backPanel = new JPanel();
		getContentPane().add(backPanel);
		backPanel.setLayout(new BorderLayout());
		heatLabel = new JLabel();
		pnlOptions = new JPanel();
		
		btnCompositeSeriesColor = new JButton("Composite Options");
		btnCompositeSeriesColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				COMPOSITE_PLOT.getChart().getXYPlot().getRenderer().setSeriesPaint(0, JColorChooser.showDialog(btnCompositeSeriesColor, "Select Composite Series Color", Color.WHITE));
			}
		});
		pnlOptions.add(btnCompositeSeriesColor);
		JButton btnOptions = new JButton("HeatMap Options");
		btnOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
					option.setVisible(true);
			}
		});
		pnlOptions.add(btnOptions);
		
		HsplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		HsplitPane.setResizeWeight(0.5);
		HsplitPane.setOneTouchExpandable(true);
		HsplitPane.setDividerLocation(0.5);
		
		VsplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		VsplitPane.setResizeWeight(1.0);
		VsplitPane.setLeftComponent(HsplitPane);
		VsplitPane.setRightComponent(pnlOptions);
		VsplitPane.setOneTouchExpandable(true);
		backPanel.add(VsplitPane);
		
		INPUT = in;
		OUTPUT = out;
		MINCOLOR = mi;
		MAXCOLOR = ma;
	}
	
	public void run() throws IOException {
		//Load CDT File into memory && calculate basics stats
		CDT = MatrixStatistics.loadCDT(INPUT);
		STATS = MatrixStatistics.getStats(CDT);
		
		//Generate composite plot
		double[] COMPOSITE = MatrixStatistics.getComposite(CDT);
		double[] DOMAIN = new double[COMPOSITE.length];
		ArrayList<Color> COLOR = new ArrayList<Color>();
		COLOR.add(MAXCOLOR);
		for(int x = 0; x < DOMAIN.length; x++) { DOMAIN[x] = x; }
		COMPOSITE_PLOT = (ChartPanel) CompositePlot.createCompositePlot(DOMAIN, COMPOSITE, COLOR);
		
		
		//Generate heatmap
		COLOR_RATIO = 2 * STATS.get(3); //default max color is 2 * mean
		HEAT_PLOT = generateHeatMap(CDT, COLOR_RATIO);	//Keep copy of original in memory
		BufferedImage heatmap = resize(HEAT_PLOT, 300, 500);
		
		//push both into heatmap
		HsplitPane.setLeftComponent(COMPOSITE_PLOT);
		heatLabel.setIcon(new ImageIcon(heatmap));
		heatPane = new JScrollPane(heatLabel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		HsplitPane.setRightComponent(heatPane);

		//detect window resize and resize heatmap if appropriate
		heatPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
            	if(allowResize) { resizeLabel(); }
            }
        });
		
		System.out.println("Here");
		
		option = new HeatMapOptions(this);
		option.populate();
	}
	
	public BufferedImage generateHeatMap(Vector<double[]> CDT, double SCALE) throws FileNotFoundException {
		int width = 2;
		int height = 2;
		
		int pixwidth = CDT.get(0).length * width;
		int pixheight = CDT.size() * height;
		
		System.setProperty("java.awt.headless", "true");
		BufferedImage im = new BufferedImage(pixwidth, pixheight, BufferedImage.TYPE_INT_ARGB);
        Graphics g = im.getGraphics();
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.WHITE);
        g2.fillRect(0,0,pixwidth, pixheight);
        
        int count = 0;
        for (int x = 0; x < CDT.size(); x++){
        	double[] ID = CDT.get(x);

        	for (int j=0;j< ID.length;j++){
        		if(ID[j] > 0) {
        			double v = ID[j] / SCALE;
        			double sVal = v>1 ? 1 : (v<0 ? 0 : v);
            		int red = (int)(MAXCOLOR.getRed() * sVal + MINCOLOR.getRed() * (1 - sVal));
            	    int green = (int)(MAXCOLOR.getGreen() * sVal + MINCOLOR.getGreen() * (1 - sVal));
            	    int blue = (int)(MAXCOLOR.getBlue() *sVal + MINCOLOR.getBlue() * (1 - sVal));        			
        			
        			g.setColor(new Color(red, green, blue));
        		}
        		else g.setColor(Color.WHITE);
                g.fillRect(j*width, count*height, width, height);
        	}
            count++;
        }
        
        return im;
	}

	public void resizeLabel() {
	    heatLabel.setIcon(new ImageIcon(HEAT_PLOT.getScaledInstance(HsplitPane.getRightComponent().getWidth(), HsplitPane.getRightComponent().getHeight(), Image.SCALE_FAST)));
	}
	
	public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}  
}