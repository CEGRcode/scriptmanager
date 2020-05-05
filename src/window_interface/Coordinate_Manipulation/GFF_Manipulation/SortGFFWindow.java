package window_interface.Coordinate_Manipulation.GFF_Manipulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import scripts.Coordinate_Manipulation.GFF_Manipulation.SortGFF;
import util.FileSelection;

@SuppressWarnings("serial")
public class SortGFFWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	
	private static File OUTPUT_PATH = null;
	private File GFF_File = null;
	private File CDT_File = null;
	
	private boolean CDT_VALID = false;
	private int CDT_SIZE = -999;
	private static int START_INDEX = -999;
	private static int STOP_INDEX = -999;
	
	private JButton btnLoadGFFFile;
	private JButton btnLoadCdtFile;
	private JButton btnOutput;
	private JButton btnConvert;

	private JProgressBar progressBar;
	public Task task;
	private JLabel lblCurrent;
	private JLabel lblDefaultToLocal;
	
	private static JRadioButton rdbtnSortbyCenter;
	private static JRadioButton rdbtnSortbyIndex;
	private JTextField txtOutput;
	private JTextField txtMid;
	private JTextField txtStart;
	private JTextField txtStop;
	private JLabel lblOutputFileName;
	private JLabel lblSizeOfExpansion;
	private JLabel lblGFFFile;
	private JLabel lblCDTFile;
	private JLabel lblCdtFileStatistics;
	private JLabel lblColumnCount;
	private JLabel lblIndexStart;
	private JLabel lblIndexStop;
	
	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException {
        	try {
        		if(rdbtnSortbyCenter.isSelected() && Integer.parseInt(txtMid.getText()) > CDT_SIZE) {
        			JOptionPane.showMessageDialog(null, "Sort Size is larger than CDT File!!!");
        		} else if(rdbtnSortbyIndex.isSelected() && Integer.parseInt(txtStart.getText()) < 0) {
        			JOptionPane.showMessageDialog(null, "Start Index is smaller than 0!!!");
        		} else if(rdbtnSortbyIndex.isSelected() && Integer.parseInt(txtStop.getText()) > CDT_SIZE) {
        			JOptionPane.showMessageDialog(null, "Stop Index is larger than CDT row size!!!");
        		} else {
        			if(rdbtnSortbyCenter.isSelected()) {
            			START_INDEX = (CDT_SIZE / 2) - (Integer.parseInt(txtMid.getText()) / 2);
            			STOP_INDEX = (CDT_SIZE / 2) + (Integer.parseInt(txtMid.getText()) / 2);
            		} else {
            			START_INDEX = Integer.parseInt(txtStart.getText());
            			STOP_INDEX = Integer.parseInt(txtStop.getText());
            		}
        			
        			String OUTPUT = txtOutput.getText();
        			if(OUTPUT_PATH != null) { OUTPUT = OUTPUT_PATH.getCanonicalPath() + File.separator + txtOutput.getText(); }
        			
		        	setProgress(0);
		        	SortGFF.sortGFFbyCDT(OUTPUT, GFF_File, CDT_File, START_INDEX, STOP_INDEX);
					setProgress(100);
					JOptionPane.showMessageDialog(null, "Sort Complete");
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
	
	public SortGFFWindow() {
		setTitle("Sort GFF File");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 345);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		btnConvert = new JButton("Convert");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnConvert, 167, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnConvert, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnConvert, -175, SpringLayout.EAST, contentPane);
		contentPane.add(btnConvert);
		
		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnConvert);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -5, SpringLayout.EAST, contentPane);
        progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
        btnConvert.setActionCommand("start");
        
        lblCurrent = new JLabel("Current Output:");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrent, 10, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblCurrent, -33, SpringLayout.SOUTH, contentPane);
        lblCurrent.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblCurrent);
        
        lblDefaultToLocal = new JLabel("Default to Local Directory");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, lblCurrent);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, lblCurrent);
        lblDefaultToLocal.setBackground(Color.WHITE);
        contentPane.add(lblDefaultToLocal);
        
        btnOutput = new JButton("Output Directory");
        sl_contentPane.putConstraint(SpringLayout.WEST, btnOutput, 150, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, btnOutput, -6, SpringLayout.NORTH, lblDefaultToLocal);
        sl_contentPane.putConstraint(SpringLayout.EAST, btnOutput, -150, SpringLayout.EAST, contentPane);
        btnOutput.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
    			OUTPUT_PATH = FileSelection.getOutputDir(fc);
    			if(OUTPUT_PATH != null) {
    				lblDefaultToLocal.setText(OUTPUT_PATH.getAbsolutePath());
    			}
        	}
        });
        contentPane.add(btnOutput);
        
        rdbtnSortbyCenter = new JRadioButton("Sort by Center");
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnSortbyCenter, 129, SpringLayout.NORTH, contentPane);
        contentPane.add(rdbtnSortbyCenter);
        
        rdbtnSortbyIndex = new JRadioButton("Sort by Index");
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnSortbyIndex, 15, SpringLayout.SOUTH, rdbtnSortbyCenter);
        contentPane.add(rdbtnSortbyIndex);
        
		ButtonGroup ExpansionType = new ButtonGroup();
		ExpansionType.add(rdbtnSortbyCenter);
        ExpansionType.add(rdbtnSortbyIndex);
        rdbtnSortbyCenter.setSelected(true);

        txtMid = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtMid, 2, SpringLayout.NORTH, rdbtnSortbyCenter);
        txtMid.setHorizontalAlignment(SwingConstants.CENTER);
        txtMid.setText("100");
        contentPane.add(txtMid);
        txtMid.setColumns(10);
        
        lblSizeOfExpansion = new JLabel("Size of Expansion (bins):");
        sl_contentPane.putConstraint(SpringLayout.WEST, txtMid, 6, SpringLayout.EAST, lblSizeOfExpansion);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtMid, 59, SpringLayout.EAST, lblSizeOfExpansion);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblSizeOfExpansion, 4, SpringLayout.NORTH, rdbtnSortbyCenter);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblSizeOfExpansion, 0, SpringLayout.WEST, btnOutput);
        contentPane.add(lblSizeOfExpansion);
        
        txtOutput = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.EAST, txtOutput, 0, SpringLayout.EAST, progressBar);
        txtOutput.setEnabled(false);
        contentPane.add(txtOutput);
        txtOutput.setColumns(10);
        
        lblOutputFileName = new JLabel("Output File Name:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtOutput, -2, SpringLayout.NORTH, lblOutputFileName);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtOutput, 10, SpringLayout.EAST, lblOutputFileName);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblOutputFileName, 16, SpringLayout.SOUTH, rdbtnSortbyIndex);
        contentPane.add(lblOutputFileName);
           
        lblGFFFile = new JLabel("No GFF File Loaded");
        contentPane.add(lblGFFFile);
        
        lblCDTFile = new JLabel("No CDT File Loaded");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblCDTFile, 0, SpringLayout.WEST, lblGFFFile);
        contentPane.add(lblCDTFile);
        
        lblCdtFileStatistics = new JLabel("CDT File Statistics:");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblCdtFileStatistics, 0, SpringLayout.WEST, rdbtnSortbyCenter);
        contentPane.add(lblCdtFileStatistics);
        
        lblColumnCount = new JLabel("No CDT File Loaded");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblColumnCount, 0, SpringLayout.NORTH, lblCdtFileStatistics);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblColumnCount, 0, SpringLayout.WEST, lblGFFFile);
        contentPane.add(lblColumnCount);
        
        txtStart = new JTextField();
        txtStart.setEnabled(false);
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtStart, 2, SpringLayout.NORTH, rdbtnSortbyIndex);
        contentPane.add(txtStart);
        txtStart.setColumns(10);
        
        txtStop = new JTextField();
        txtStop.setEnabled(false);
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtStop, 2, SpringLayout.NORTH, rdbtnSortbyIndex);
        contentPane.add(txtStop);
        txtStop.setColumns(10);
        
        lblIndexStart = new JLabel("Index Start:");
        sl_contentPane.putConstraint(SpringLayout.EAST, txtStart, 59, SpringLayout.EAST, lblIndexStart);
        lblIndexStart.setEnabled(false);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtStart, 6, SpringLayout.EAST, lblIndexStart);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblIndexStart, 4, SpringLayout.NORTH, rdbtnSortbyIndex);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblIndexStart, 0, SpringLayout.WEST, btnOutput);
        contentPane.add(lblIndexStart);
        
        lblIndexStop = new JLabel("Index Stop:");
        sl_contentPane.putConstraint(SpringLayout.EAST, txtStop, 59, SpringLayout.EAST, lblIndexStop);
        lblIndexStop.setEnabled(false);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtStop, 6, SpringLayout.EAST, lblIndexStop);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblIndexStop, 4, SpringLayout.NORTH, rdbtnSortbyIndex);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblIndexStop, 0, SpringLayout.WEST, progressBar);
        contentPane.add(lblIndexStop);
        btnConvert.addActionListener(this);
        
        rdbtnSortbyCenter.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		    	  if(rdbtnSortbyCenter.isSelected()) {
		    		  txtMid.setEnabled(true);
		    		  lblSizeOfExpansion.setEnabled(true);
		    		  txtStart.setEnabled(false);
		    		  lblIndexStart.setEnabled(false);
		    		  txtStop.setEnabled(false);
		    		  lblIndexStop.setEnabled(false);
		    	  }
		      }
        });
        
        rdbtnSortbyIndex.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		    	  if(rdbtnSortbyIndex.isSelected()) {
		    		  txtMid.setEnabled(false);
		    		  lblSizeOfExpansion.setEnabled(false);
		    		  txtStart.setEnabled(true);
		    		  lblIndexStart.setEnabled(true);
		    		  txtStop.setEnabled(true);
		    		  lblIndexStop.setEnabled(true);
		    	  }
		      }
        });
        
		btnLoadGFFFile = new JButton("Load GFF File");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblGFFFile, 5, SpringLayout.NORTH, btnLoadGFFFile);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblGFFFile, 14, SpringLayout.EAST, btnLoadGFFFile);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoadGFFFile, 10, SpringLayout.NORTH, contentPane);
		btnLoadGFFFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				File newBEDFile = FileSelection.getBEDFile(fc);
				if(newBEDFile != null) {
					GFF_File = newBEDFile;
					lblGFFFile.setText(GFF_File.getName());
					txtOutput.setEnabled(true);
				    String sortName = (GFF_File.getName()).substring(0, GFF_File.getName().length() - 4) + "_SORT";
				    txtOutput.setText(sortName);	
				}
			}
		});
		contentPane.add(btnLoadGFFFile);
		
		btnLoadCdtFile = new JButton("Load CDT File");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCdtFileStatistics, 20, SpringLayout.SOUTH, btnLoadCdtFile);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoadCdtFile, 16, SpringLayout.SOUTH, btnLoadGFFFile);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCDTFile, 5, SpringLayout.NORTH, btnLoadCdtFile);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoadCdtFile, 0, SpringLayout.WEST, rdbtnSortbyCenter);
		btnLoadCdtFile.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		File newCDTFile = FileSelection.getCDTFile(fc);
				if(newCDTFile != null) {
					CDT_File = newCDTFile;
					lblCDTFile.setText(CDT_File.getName());
					
					try {
						CDT_SIZE = parseCDTFile(CDT_File);
						CDT_VALID = (CDT_SIZE!=-999);
					} catch (FileNotFoundException e1) { e1.printStackTrace(); }
					
					if(CDT_VALID) {
						lblColumnCount.setText("Column Count: " + CDT_SIZE);
					} else {
						lblColumnCount.setText("CDT File does not possess equal number of columns!!!");
					}
				}
	    	}
		});
	    contentPane.add(btnLoadCdtFile);
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
		if(status) {
			if(rdbtnSortbyCenter.isSelected()) {
				txtMid.setEnabled(true);
				lblSizeOfExpansion.setEnabled(true);
				txtStart.setEnabled(false);
				lblIndexStart.setEnabled(false);
				txtStop.setEnabled(false);
				lblIndexStop.setEnabled(false);
			}
			if(rdbtnSortbyIndex.isSelected()) {
				txtMid.setEnabled(false);
				lblSizeOfExpansion.setEnabled(false);
				txtStart.setEnabled(true);
				lblIndexStart.setEnabled(true);
				txtStop.setEnabled(true);
				lblIndexStop.setEnabled(true);
			}
		}
	}
    
	
	// This function is almost exactly copied from window/*/SortBEDWindow & scripts/*/SortBED & scripts/*/SortGFF...good practice to merge at some point.
	public Integer parseCDTFile(File CDT) throws FileNotFoundException {
		Scanner scan = new Scanner(CDT);
		int currentSize = -999;
		boolean consistentSize = true;
		int currentRow = 1;
		while (scan.hasNextLine()) {
			String[] temp = scan.nextLine().split("\t");
			if(!temp[0].contains("YORF") && !temp[0].contains("NAME")) {
				int tempsize = temp.length - 2;
				if(currentSize == -999) { currentSize = tempsize; }
				else if(currentSize != tempsize) {
					JOptionPane.showMessageDialog(null, "Invalid Row at Index: " + currentRow);      // This is the line that is different
					consistentSize = false;
					scan.close();
				}
				currentRow++;
			}
		}
		scan.close();
		if(consistentSize) { return currentSize; }
		else { return -999; }
	}
}


	
