package window_interface.Sequence_Analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.SwingWorker;
import javax.swing.JProgressBar;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import scripts.Sequence_Analysis.FourColorPlot;
import util.FileSelection;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class FourColorSequenceWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	
	final DefaultListModel<String> expList;
	Vector<File> fastaFiles = new Vector<File>();
	private File OUTPUTPATH = null;
	
	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnGen;
	private JButton btnAColor;
	private JButton btnTColor;
	private JButton btnGColor;
	private JButton btnCColor;
	private JButton btnNColor;

	private JButton btnOutputDirectory;

	private JProgressBar progressBar;
	public Task task;
	private JLabel lblCurrentOutput;
	private JLabel lblDefaultToLocal;
	private JLabel lblPixelHeight;
	private JLabel lblPixelWidth;
	private JTextField txtHeight;
	private JTextField txtWidth;
	
	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException {
        	try {
        		setProgress(0);
	        	if(Integer.parseInt(txtHeight.getText()) < 1) {
	    			JOptionPane.showMessageDialog(null, "Invalid Height Size!!! Must be larger than 0");
	    		} else if(Integer.parseInt(txtWidth.getText()) < 1) {
	    			JOptionPane.showMessageDialog(null, "Invalid Width Size!!! Must be larger than 0");
	    		} else {
	        	   	ArrayList<Color> COLORS = new ArrayList<Color>();
		        	COLORS.add(btnAColor.getForeground());
		        	COLORS.add(btnTColor.getForeground());
		        	COLORS.add(btnGColor.getForeground());
		        	COLORS.add(btnCColor.getForeground());
		        	COLORS.add(btnNColor.getForeground());
		        	
		        	if(OUTPUTPATH == null) {
		        		OUTPUTPATH = new File(System.getProperty("user.dir"));
		        	}
		        	for(int x = 0; x < fastaFiles.size(); x++) {
		        		String[] out = fastaFiles.get(x).getName().split("\\.");
		        		FourColorPlot.generatePLOT(fastaFiles.get(x), new File(OUTPUTPATH + File.separator + out[0] + ".png"), COLORS, Integer.parseInt(txtHeight.getText()), Integer.parseInt(txtWidth.getText()));
						int percentComplete = (int)(((double)(x + 1) / fastaFiles.size()) * 100);
		        		setProgress(percentComplete);
		        	}
		        	setProgress(100);
					JOptionPane.showMessageDialog(null, "Plots Generated");
		        	return null;
	    		}
        	} catch(NumberFormatException nfe){
				JOptionPane.showMessageDialog(null, "Invalid Input in Fields!!!");
			}
			return null;
        }
        
        public void done() {
        	massXable(contentPane, true);
            setCursor(null); //turn off the wait cursor
        }
	}
	
	public FourColorSequenceWindow() {
		setTitle("Four Color Sequence Plot Generator");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 376);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
	
		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -5, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
      	expList = new DefaultListModel<String>();
		final JList<String> listExp = new JList<String>(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listExp);
		
		btnLoad = new JButton("Load FASTA Files");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, btnLoad);
		btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				File[] newFASTAFiles = FileSelection.getFASTAFiles(fc);
				if(newFASTAFiles != null) {
					for(int x = 0; x < newFASTAFiles.length; x++) { 
						fastaFiles.add(newFASTAFiles[x]);
						expList.addElement(newFASTAFiles[x].getName());
					}
				}
			}
		});
		contentPane.add(btnLoad);
		
		btnRemoveBam = new JButton("Remove FASTA Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 0, SpringLayout.NORTH, btnRemoveBam);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveBam, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBam, -5, SpringLayout.EAST, contentPane);
		btnRemoveBam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(listExp.getSelectedIndex() > -1) {
					fastaFiles.remove(listExp.getSelectedIndex());
					expList.remove(listExp.getSelectedIndex());
				}
			}
		});		
		contentPane.add(btnRemoveBam);
		
		btnGen = new JButton("Generate");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -148, SpringLayout.NORTH, btnGen);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnGen, 167, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnGen, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnGen, -175, SpringLayout.EAST, contentPane);
		contentPane.add(btnGen);
		
		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnGen);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -5, SpringLayout.EAST, contentPane);
        progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
        btnGen.setActionCommand("start");
        
        lblCurrentOutput = new JLabel("Current Output:");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrentOutput, 5, SpringLayout.WEST, contentPane);
        lblCurrentOutput.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblCurrentOutput);
        
        lblDefaultToLocal = new JLabel("Default to Local Directory");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 10, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, lblDefaultToLocal, -20, SpringLayout.EAST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblCurrentOutput, -6, SpringLayout.NORTH, lblDefaultToLocal);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblDefaultToLocal, -6, SpringLayout.NORTH, btnGen);
        lblDefaultToLocal.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblDefaultToLocal.setBackground(Color.WHITE);
        contentPane.add(lblDefaultToLocal);
        
        btnOutputDirectory = new JButton("Output Directory");
        sl_contentPane.putConstraint(SpringLayout.WEST, btnOutputDirectory, 145, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, btnOutputDirectory, -70, SpringLayout.SOUTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, btnOutputDirectory, -145, SpringLayout.EAST, contentPane);
        contentPane.add(btnOutputDirectory);
        
        btnAColor = new JButton("A Color");
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnAColor, 10, SpringLayout.SOUTH, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, btnAColor, 5, SpringLayout.WEST, contentPane);
        btnAColor.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		btnAColor.setForeground(JColorChooser.showDialog(btnAColor, "Select a Background Color", btnAColor.getForeground()));
        	}
        });
        btnAColor.setForeground(new Color(254, 25, 24));
        contentPane.add(btnAColor);
        
        btnTColor = new JButton("T Color");
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnTColor, 10, SpringLayout.SOUTH, scrollPane);
        btnTColor.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		btnTColor.setForeground(JColorChooser.showDialog(btnTColor, "Select a Background Color", btnTColor.getForeground()));
        	}
        });
        sl_contentPane.putConstraint(SpringLayout.WEST, btnTColor, 6, SpringLayout.EAST, btnAColor);
        btnTColor.setForeground(new Color(50, 204, 60));
        contentPane.add(btnTColor);
        
        btnGColor = new JButton("G Color");
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnGColor, 10, SpringLayout.SOUTH, scrollPane);
        btnGColor.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		btnGColor.setForeground(JColorChooser.showDialog(btnGColor, "Select a Background Color", btnGColor.getForeground()));
        	}
        });
        sl_contentPane.putConstraint(SpringLayout.WEST, btnGColor, 6, SpringLayout.EAST, btnTColor);
        btnGColor.setForeground(new Color(252, 252, 80));
        contentPane.add(btnGColor);
        
        btnCColor = new JButton("C Color");
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnCColor, 10, SpringLayout.SOUTH, scrollPane);
        btnCColor.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		btnCColor.setForeground(JColorChooser.showDialog(btnCColor, "Select a Background Color", btnCColor.getForeground()));
        	}
        });
        sl_contentPane.putConstraint(SpringLayout.WEST, btnCColor, 6, SpringLayout.EAST, btnGColor);
        btnCColor.setForeground(new Color(43, 49, 246));
        contentPane.add(btnCColor);
        
        btnNColor = new JButton("N Color");
        btnNColor.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		btnNColor.setForeground(JColorChooser.showDialog(btnNColor, "Select a Background Color", btnNColor.getForeground()));
        	}
        });
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnNColor, 0, SpringLayout.NORTH, btnAColor);
        sl_contentPane.putConstraint(SpringLayout.WEST, btnNColor, 6, SpringLayout.EAST, btnCColor);
        btnNColor.setForeground(Color.GRAY);
        contentPane.add(btnNColor);
        
        lblPixelHeight = new JLabel("Pixel Height:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblPixelHeight, 16, SpringLayout.SOUTH, btnAColor);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblPixelHeight, 50, SpringLayout.WEST, scrollPane);
        contentPane.add(lblPixelHeight);
        
        txtHeight = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.WEST, txtHeight, 10, SpringLayout.EAST, lblPixelHeight);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, txtHeight, 2, SpringLayout.SOUTH, lblPixelHeight);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtHeight, 76, SpringLayout.EAST, lblPixelHeight);
        txtHeight.setHorizontalAlignment(SwingConstants.CENTER);
        txtHeight.setText("1");
        contentPane.add(txtHeight);
        txtHeight.setColumns(10);
        
        lblPixelWidth = new JLabel("Pixel Width:");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblPixelWidth, 33, SpringLayout.EAST, txtHeight);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblPixelWidth, 0, SpringLayout.SOUTH, lblPixelHeight);
        contentPane.add(lblPixelWidth);
        
        txtWidth = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtWidth, -2, SpringLayout.NORTH, lblPixelHeight);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtWidth, 10, SpringLayout.EAST, lblPixelWidth);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtWidth, 76, SpringLayout.EAST, lblPixelWidth);
        txtWidth.setHorizontalAlignment(SwingConstants.CENTER);
        txtWidth.setText("1");
        contentPane.add(txtWidth);
        txtWidth.setColumns(10);
        
        btnOutputDirectory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	OUTPUTPATH = FileSelection.getOutputDir(fc);
				if(OUTPUTPATH != null) {
					lblDefaultToLocal.setText(OUTPUTPATH.getAbsolutePath());
				}
			}
		});
        
        btnGen.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		massXable(contentPane, false);
    	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
	}
	
	/**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        }
    }
	
	public void massXable(Container con, boolean status) {
		for(Component c : con.getComponents()) {
			c.setEnabled(status);
			if(c instanceof Container) { massXable((Container)c, status); }
		}
	}
}


	
