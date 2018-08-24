package scripts.Figure_Generation.HeatmapScripts;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SpringLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class HeatMapOptions extends JFrame {
	private HeatMapPanel ORIGIN;
	private String NAME = "";
	private JPanel contentPane;
	
	private JLabel lblHeatmapHeightpixels;
	private JLabel lblHeatmapWidthpixels;
	private JLabel lblMin_Text;
	private JLabel lblMin;
	private JLabel lblMax_Text;
	private JLabel lblMax;
	private JLabel lblMedian_Text;
	private JLabel lblMedian;
	private JLabel lblMean_Text;
	private JLabel lblMean;
	private JLabel lblMode_Text;
	private JLabel lblMode;
	private JLabel lblSetMaxColor;
	private JLabel lblColorBar1;
	private JButton btnMaxHeatmapColor;
	private JButton btnMinHeatmapColor;
	private JButton btnApply;
	private JCheckBox chckbxLock;
	private JLabel lblMaxColor;
	private JLabel lblMinColor;
	private JTextField txtMax;
	public JTextField txtHeight;
	public JTextField txtWidth;

	private JButton btnSaveAsHighres;
	private JButton btnSaveAsPng;
	private JButton btnClose;
	private JLabel lblColorBar2;
	private JLabel lblColorBar3;
	private JLabel lblColorBar4;
	private JLabel lblColorBar5;
	private JLabel lblColorBar6;
	private JLabel lblColorBar7;
	private JLabel lblColorBar8;
	private JLabel lblColorBar9;
	private JLabel lblColorBar10;
	private JLabel lblColorBar1_Score;
	private JLabel lblColorBar10_Score;

	public HeatMapOptions(HeatMapPanel home) {
		ORIGIN = home;
		setTitle("Heatmap Settings");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 350);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		chckbxLock = new JCheckBox("Lock Heatmap Auto-Resizing");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxLock, 75, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxLock, 110, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, chckbxLock, 335, SpringLayout.WEST, contentPane);
		chckbxLock.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
			        if(chckbxLock.isSelected()) { ORIGIN.allowResize = false; }
			        else { ORIGIN.allowResize = true; }
		      }
		});  
		getContentPane().add(chckbxLock, BorderLayout.NORTH);
				
		lblHeatmapHeightpixels = new JLabel("Heatmap Height (pixels):");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblHeatmapHeightpixels, 16, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblHeatmapHeightpixels, 16, SpringLayout.WEST, contentPane);
		contentPane.add(lblHeatmapHeightpixels);
		
		lblHeatmapWidthpixels = new JLabel("Heatmap Width (pixels):");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblHeatmapWidthpixels, 14, SpringLayout.SOUTH, lblHeatmapHeightpixels);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblHeatmapWidthpixels, 0, SpringLayout.WEST, lblHeatmapHeightpixels);
		contentPane.add(lblHeatmapWidthpixels);
		
		txtHeight = new JTextField();
		txtHeight.setHorizontalAlignment(SwingConstants.CENTER);
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtHeight, -2, SpringLayout.NORTH, lblHeatmapHeightpixels);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtHeight, 6, SpringLayout.EAST, lblHeatmapHeightpixels);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtHeight, 96, SpringLayout.EAST, lblHeatmapHeightpixels);
		txtHeight.setText(Integer.toString(ORIGIN.heatLabel.getHeight()));
		contentPane.add(txtHeight);
		txtHeight.setColumns(10);
		
		txtWidth = new JTextField();
		txtWidth.setHorizontalAlignment(SwingConstants.CENTER);
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtWidth, -2, SpringLayout.NORTH, lblHeatmapWidthpixels);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtWidth, 0, SpringLayout.WEST, txtHeight);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtWidth, 101, SpringLayout.EAST, lblHeatmapWidthpixels);
		txtWidth.setText(Integer.toString(ORIGIN.heatLabel.getWidth()));
		contentPane.add(txtWidth);
		txtWidth.setColumns(10);
				
		btnMaxHeatmapColor = new JButton("Max Heatmap Color");
		btnMaxHeatmapColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color temp = JColorChooser.showDialog(btnMaxHeatmapColor, "Select a Background Color", lblMaxColor.getBackground());
		        if(temp != null) {
		        	lblMaxColor.setBackground(temp);
		        	paintColorBar();
		        }
			}
		});	
		contentPane.add(btnMaxHeatmapColor);
				
		lblMin_Text = new JLabel("Min:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblMin_Text, 90, SpringLayout.SOUTH, lblHeatmapWidthpixels);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblMin_Text, 0, SpringLayout.WEST, lblHeatmapHeightpixels);
		contentPane.add(lblMin_Text);

		lblMax_Text = new JLabel("Max:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblMax_Text, 6, SpringLayout.SOUTH, lblMin_Text);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblMax_Text, 0, SpringLayout.WEST, lblHeatmapHeightpixels);
		contentPane.add(lblMax_Text);
		
		lblMedian_Text = new JLabel("Median:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblMedian_Text, 6, SpringLayout.SOUTH, lblMax_Text);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblMedian_Text, 0, SpringLayout.WEST, lblHeatmapHeightpixels);
		contentPane.add(lblMedian_Text);
		
		lblMean_Text = new JLabel("Mean:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblMean_Text, 0, SpringLayout.WEST, lblHeatmapHeightpixels);
		contentPane.add(lblMean_Text);
	
		lblMode_Text = new JLabel("Mode:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblMean_Text, 6, SpringLayout.SOUTH, lblMode_Text);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblMode_Text, 6, SpringLayout.SOUTH, lblMedian_Text);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblMode_Text, 0, SpringLayout.WEST, lblHeatmapHeightpixels);
		contentPane.add(lblMode_Text);

		btnClose = new JButton("Close");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnClose, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnClose, -10, SpringLayout.SOUTH, contentPane);
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		contentPane.add(btnClose);
						
		btnSaveAsPng = new JButton("Save PNG");
		btnSaveAsPng.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser saveFile = new JFileChooser();
				saveFile.setSelectedFile(new File(NAME + ".png"));
                saveFile.showSaveDialog(null);
                try { ImageIO.write(ORIGIN.resize(ORIGIN.HEAT_PLOT, ORIGIN.heatLabel.getIcon().getIconWidth(), ORIGIN.heatLabel.getIcon().getIconHeight()), "PNG", saveFile.getSelectedFile()); }
                catch (IOException e) { e.printStackTrace(); }
			}
		});
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnSaveAsPng, 0, SpringLayout.NORTH, btnClose);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnSaveAsPng, 6, SpringLayout.EAST, btnClose);
		contentPane.add(btnSaveAsPng);
		
		lblSetMaxColor = new JLabel("Set Max Color Score:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblSetMaxColor, 15, SpringLayout.NORTH, lblMin_Text);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblSetMaxColor, 140, SpringLayout.WEST, contentPane);
		contentPane.add(lblSetMaxColor);
		
		txtMax = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtMax, 14, SpringLayout.NORTH, lblMin_Text);
		txtMax.setHorizontalAlignment(SwingConstants.CENTER);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtMax, 6, SpringLayout.EAST, lblSetMaxColor);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtMax, 81, SpringLayout.EAST, lblSetMaxColor);
		contentPane.add(txtMax);
		txtMax.setColumns(10);
		
		btnSaveAsHighres = new JButton("Save High-Res PNG");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnSaveAsHighres, 0, SpringLayout.NORTH, btnClose);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnSaveAsHighres, 6, SpringLayout.EAST, btnSaveAsPng);
		btnSaveAsHighres.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser saveFile = new JFileChooser();
				saveFile.setSelectedFile(new File(NAME + ".png"));
                saveFile.showSaveDialog(null);
				try { ImageIO.write(ORIGIN.HEAT_PLOT, "PNG", saveFile.getSelectedFile()); }
				catch (IOException e1) { e1.printStackTrace(); }
			}
		});
		contentPane.add(btnSaveAsHighres);
				
		lblMin = new JLabel("-999");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblMin, 0, SpringLayout.NORTH, lblMin_Text);
		contentPane.add(lblMin);
		
		lblMax = new JLabel("-999");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblMin, 0, SpringLayout.WEST, lblMax);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblMax, 0, SpringLayout.NORTH, lblMax_Text);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblMax, 26, SpringLayout.EAST, lblMax_Text);
		contentPane.add(lblMax);
		
		lblMedian = new JLabel("-999");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblMedian, 0, SpringLayout.NORTH, lblMedian_Text);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblMedian, 6, SpringLayout.EAST, lblMedian_Text);
		contentPane.add(lblMedian);
		
		lblMode = new JLabel("-999");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblMode, 0, SpringLayout.NORTH, lblMode_Text);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblMode, 0, SpringLayout.WEST, lblMin);
		contentPane.add(lblMode);
		
		lblMean = new JLabel("-999");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblMean, 0, SpringLayout.WEST, lblMin);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblMean, 0, SpringLayout.SOUTH, lblMean_Text);
		contentPane.add(lblMean);
		
		btnMinHeatmapColor = new JButton("Min Heatmap Color");
		btnMinHeatmapColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color temp = JColorChooser.showDialog(btnMaxHeatmapColor, "Select a Background Color", lblMinColor.getBackground());
        		if(temp != null) {
        			lblMinColor.setBackground(temp);
        			paintColorBar();
        		}
			}
		});
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnMaxHeatmapColor, 0, SpringLayout.NORTH, btnMinHeatmapColor);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnMaxHeatmapColor, 52, SpringLayout.EAST, btnMinHeatmapColor);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnMinHeatmapColor, 10, SpringLayout.SOUTH, chckbxLock);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnMinHeatmapColor, 0, SpringLayout.WEST, lblHeatmapHeightpixels);
		contentPane.add(btnMinHeatmapColor);
		
		lblColorBar1 = new JLabel("");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblColorBar1, 25, SpringLayout.SOUTH, lblSetMaxColor);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblColorBar1, 0, SpringLayout.WEST, lblSetMaxColor);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblColorBar1, 25, SpringLayout.SOUTH, lblMedian_Text);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblColorBar1, 30, SpringLayout.WEST, lblSetMaxColor);
		lblColorBar1.setOpaque(true);
		contentPane.add(lblColorBar1);
		
		lblMinColor = new JLabel("");
		lblMinColor.setOpaque(true);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblMinColor, 0, SpringLayout.NORTH, btnMinHeatmapColor);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblMinColor, 180, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblMinColor, 0, SpringLayout.SOUTH, btnMinHeatmapColor);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblMinColor, 40, SpringLayout.EAST, btnMinHeatmapColor);
		contentPane.add(lblMinColor);
		
		lblMaxColor = new JLabel("");
		sl_contentPane.putConstraint(SpringLayout.EAST, lblMaxColor, 40, SpringLayout.EAST, btnMaxHeatmapColor);
		lblMaxColor.setOpaque(true);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblMaxColor, 0, SpringLayout.NORTH, btnMaxHeatmapColor);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblMaxColor, 10, SpringLayout.EAST, btnMaxHeatmapColor);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblMaxColor, 0, SpringLayout.SOUTH, btnMaxHeatmapColor);
		contentPane.add(lblMaxColor);
		
		lblColorBar2 = new JLabel("");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblColorBar2, 25, SpringLayout.SOUTH, lblSetMaxColor);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblColorBar2, 0, SpringLayout.EAST, lblColorBar1);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblColorBar2, 25, SpringLayout.SOUTH, lblMedian_Text);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblColorBar2, 30, SpringLayout.EAST, lblColorBar1);
		lblColorBar2.setOpaque(true);
		contentPane.add(lblColorBar2);
		
		lblColorBar3 = new JLabel("");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblColorBar3, 25, SpringLayout.SOUTH, lblSetMaxColor);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblColorBar3, 0, SpringLayout.EAST, lblColorBar2);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblColorBar3, 0, SpringLayout.SOUTH, lblColorBar2);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblColorBar3, 30, SpringLayout.EAST, lblColorBar2);
		lblColorBar3.setOpaque(true);
		contentPane.add(lblColorBar3);
		
		lblColorBar4 = new JLabel("");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblColorBar4, 25, SpringLayout.SOUTH, lblSetMaxColor);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblColorBar4, 0, SpringLayout.EAST, lblColorBar3);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblColorBar4, 0, SpringLayout.SOUTH, lblColorBar3);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblColorBar4, 30, SpringLayout.EAST, lblColorBar3);
		lblColorBar4.setOpaque(true);
		contentPane.add(lblColorBar4);
		
		lblColorBar5 = new JLabel("");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblColorBar5, 0, SpringLayout.NORTH, lblColorBar4);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblColorBar5, 0, SpringLayout.EAST, lblColorBar4);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblColorBar5, 25, SpringLayout.SOUTH, lblMedian_Text);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblColorBar5, 30, SpringLayout.EAST, lblColorBar4);
		lblColorBar5.setOpaque(true);
		contentPane.add(lblColorBar5);
		
		lblColorBar6 = new JLabel("");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblColorBar6, 0, SpringLayout.NORTH, lblColorBar5);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblColorBar6, 0, SpringLayout.EAST, lblColorBar5);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblColorBar6, 25, SpringLayout.SOUTH, lblMedian_Text);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblColorBar6, 30, SpringLayout.EAST, lblColorBar5);
		lblColorBar6.setOpaque(true);
		contentPane.add(lblColorBar6);
		
		lblColorBar7 = new JLabel("");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblColorBar7, 0, SpringLayout.NORTH, lblColorBar6);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblColorBar7, 0, SpringLayout.EAST, lblColorBar6);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblColorBar7, 25, SpringLayout.SOUTH, lblMedian_Text);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblColorBar7, 30, SpringLayout.EAST, lblColorBar6);
		lblColorBar7.setOpaque(true);
		contentPane.add(lblColorBar7);
		
		lblColorBar8 = new JLabel("");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblColorBar8, 0, SpringLayout.NORTH, lblColorBar7);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblColorBar8, 0, SpringLayout.EAST, lblColorBar7);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblColorBar8, 25, SpringLayout.SOUTH, lblMedian_Text);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblColorBar8, 30, SpringLayout.EAST, lblColorBar7);
		lblColorBar8.setOpaque(true);
		contentPane.add(lblColorBar8);
		
		lblColorBar9 = new JLabel("");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblColorBar9, 0, SpringLayout.NORTH, lblColorBar8);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblColorBar9, 0, SpringLayout.EAST, lblColorBar8);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblColorBar9, 25, SpringLayout.SOUTH, lblMedian_Text);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblColorBar9, 30, SpringLayout.EAST, lblColorBar8);
		lblColorBar9.setOpaque(true);
		contentPane.add(lblColorBar9);
		
		lblColorBar10 = new JLabel("");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblColorBar10, 0, SpringLayout.NORTH, lblColorBar9);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblColorBar10, 0, SpringLayout.EAST, lblColorBar9);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblColorBar10, 25, SpringLayout.SOUTH, lblMedian_Text);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblColorBar10, 30, SpringLayout.EAST, lblColorBar9);
		lblColorBar10.setOpaque(true);
		contentPane.add(lblColorBar10);
		
		lblColorBar1_Score = new JLabel("0");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblColorBar1_Score, 0, SpringLayout.SOUTH, lblColorBar1);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblColorBar1_Score, 0, SpringLayout.WEST, lblColorBar1);
		contentPane.add(lblColorBar1_Score);
		
		lblColorBar10_Score = new JLabel("0");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblColorBar10_Score, 0, SpringLayout.SOUTH, lblColorBar10);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblColorBar10_Score, 0, SpringLayout.WEST, lblColorBar10);
		contentPane.add(lblColorBar10_Score);
		
		btnApply = new JButton("Apply");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnApply, 0, SpringLayout.NORTH, btnClose);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnApply, 6, SpringLayout.EAST, btnSaveAsHighres);
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(Integer.parseInt(txtWidth.getText()) < 1) {
        			JOptionPane.showMessageDialog(null, "Invalid Pixel Width Number!!! Must be larger than 0");
				} else if(Integer.parseInt(txtHeight.getText()) < 1) {
        			JOptionPane.showMessageDialog(null, "Invalid Pixel Height Number!!! Must be larger than 0");
				} else if(Double.parseDouble(txtMax.getText()) < 0) {
        			JOptionPane.showMessageDialog(null, "Invalid Maximum Score!!! Must be greater than or equal to 0");
				} else if(ORIGIN.MAXCOLOR != lblMaxColor.getBackground() || ORIGIN.MINCOLOR != lblMinColor.getBackground() || ORIGIN.COLOR_RATIO != Double.parseDouble(txtMax.getText())) {
					
					ORIGIN.MAXCOLOR = lblMaxColor.getBackground();
					ORIGIN.MINCOLOR = lblMinColor.getBackground();
					ORIGIN.COLOR_RATIO = Double.parseDouble(txtMax.getText());
					
					paintColorBar();
					
					try {
						ORIGIN.HEAT_PLOT = ORIGIN.generateHeatMap();
						ORIGIN.heatLabel.setIcon(new ImageIcon(ORIGIN.HEAT_PLOT.getScaledInstance(Integer.parseInt(txtWidth.getText()), Integer.parseInt(txtHeight.getText()), Image.SCALE_FAST)));
					}
					catch (NumberFormatException e) { e.printStackTrace(); }
					catch (FileNotFoundException e) { e.printStackTrace(); }
					
					if(Integer.parseInt(txtWidth.getText()) != ORIGIN.heatLabel.getWidth() || Integer.parseInt(txtHeight.getText()) != ORIGIN.heatLabel.getHeight()) { chckbxLock.setSelected(true); }
					
				} else if(Integer.parseInt(txtWidth.getText()) != ORIGIN.heatLabel.getWidth() || Integer.parseInt(txtHeight.getText()) != ORIGIN.heatLabel.getHeight()) {
					ORIGIN.heatLabel.setIcon(new ImageIcon(ORIGIN.HEAT_PLOT.getScaledInstance(Integer.parseInt(txtWidth.getText()), Integer.parseInt(txtHeight.getText()), Image.SCALE_FAST)));
					chckbxLock.setSelected(true);
				} 
			}
		});
		contentPane.add(btnApply);
	}
	
	public void paintColorBar() {
		lblColorBar1.setBackground(calcFracColor(0.1));
		lblColorBar1_Score.setText(Double.toString(ORIGIN.COLOR_RATIO * 0.1));
		lblColorBar2.setBackground(calcFracColor(0.2));
		lblColorBar3.setBackground(calcFracColor(0.3));
		lblColorBar4.setBackground(calcFracColor(0.4));
		lblColorBar5.setBackground(calcFracColor(0.5));
		lblColorBar6.setBackground(calcFracColor(0.6));
		lblColorBar7.setBackground(calcFracColor(0.7));
		lblColorBar8.setBackground(calcFracColor(0.8));
		lblColorBar9.setBackground(calcFracColor(0.9));
		lblColorBar10.setBackground(calcFracColor(1.0));
		lblColorBar10_Score.setText(Double.toString(ORIGIN.COLOR_RATIO));
	}
	
	private Color calcFracColor(double v){
		Color c;
		Color maxColor = lblMaxColor.getBackground();
		Color minColor = lblMinColor.getBackground();
		
		double sVal = v>1 ? 1 : (v<0 ? 0 : v);
		int red = (int)(maxColor.getRed() * sVal + minColor.getRed() * (1 - sVal));
	    int green = (int)(maxColor.getGreen() * sVal + minColor.getGreen() * (1 - sVal));
	    int blue = (int)(maxColor.getBlue() *sVal + minColor.getBlue() * (1 - sVal));
	    c = new Color(red, green, blue);
		return(c);
	}

	public void populate() {	
		lblMinColor.setBackground(ORIGIN.MINCOLOR);
		lblMaxColor.setBackground(ORIGIN.MAXCOLOR);
		
		txtMax.setText(Double.toString(ORIGIN.COLOR_RATIO));
		txtHeight.setText(Integer.toString(ORIGIN.heatLabel.getHeight()));
		txtWidth.setText(Integer.toString(ORIGIN.heatLabel.getWidth()));
		
		NumberFormat nf = new DecimalFormat("#.##");
		lblMin.setText(nf.format(ORIGIN.STATS.get(0)));
		lblMax.setText(nf.format(ORIGIN.STATS.get(1)));
		lblMedian.setText(nf.format(ORIGIN.STATS.get(2)));
		lblMean.setText(nf.format(ORIGIN.STATS.get(3)));
		lblMode.setText(nf.format(ORIGIN.STATS.get(4)));
		
		paintColorBar();
		
		NAME = ORIGIN.INPUT.getName().split("\\.")[0];
	}
}
