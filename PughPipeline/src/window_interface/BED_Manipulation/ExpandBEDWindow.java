package window_interface.BED_Manipulation;

import file_filters.BEDFilter;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.SwingWorker;
import javax.swing.JProgressBar;
import javax.swing.JLabel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class ExpandBEDWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	
	private File OUTPUT_PATH = null;
	private static int SIZE = -999;
	final DefaultListModel expList;
	Vector<File> BEDFiles = new Vector<File>();
	
	private JButton btnLoad;
	private JButton btnRemoveBED;
	private JButton btnConvert;

	private JProgressBar progressBar;
	public Task task;
	private JLabel lblCurrent;
	private JLabel lblDefaultToLocal;
	private JButton btnOutput;
	private JTextField txtSize;
	
	private static JRadioButton rdbtnExpandFromCenter;
	private static JRadioButton rdbtnAddToBorder;
	
	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException {
        	try {
    			SIZE = Integer.parseInt(txtSize.getText());
    			if(SIZE < 1) {
        			JOptionPane.showMessageDialog(null, "Invalid Expansion Size!!! Must be larger than 0 bp");
        		} else {
		        	setProgress(0);
		        	for(int x = 0; x < BEDFiles.size(); x++) {
						expandBEDBorders(OUTPUT_PATH, BEDFiles.get(x));
						int percentComplete = (int)(((double)(x + 1) / BEDFiles.size()) * 100);
		        		setProgress(percentComplete);
		        	}
		        	setProgress(100);
					JOptionPane.showMessageDialog(null, "Conversion Complete");
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
	
	public ExpandBEDWindow() {
		setTitle("Expand BED File");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 345);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
	
		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 36, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
      	expList = new DefaultListModel();
		final JList listExp = new JList(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listExp);
		
		btnLoad = new JButton("Load BED Files");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 0, SpringLayout.WEST, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnLoad, -6, SpringLayout.NORTH, scrollPane);
		btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				File[] newBEDFiles = getCoordFile();
				if(newBEDFiles != null) {
					for(int x = 0; x < newBEDFiles.length; x++) { 
						BEDFiles.add(newBEDFiles[x]);
						expList.addElement(newBEDFiles[x].getName());
					}
				}
			}
		});
		contentPane.add(btnLoad);
		
		btnRemoveBED = new JButton("Remove BED");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnRemoveBED, -6, SpringLayout.NORTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBED, 0, SpringLayout.EAST, scrollPane);
		btnRemoveBED.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(listExp.getSelectedIndex() > -1) {
					BEDFiles.remove(listExp.getSelectedIndex());
					expList.remove(listExp.getSelectedIndex());
				}
			}
		});		
		contentPane.add(btnRemoveBED);
		
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
        lblCurrent.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblCurrent);
        
        lblDefaultToLocal = new JLabel("Default to Local Directory");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, lblCurrent);
        lblDefaultToLocal.setBackground(Color.WHITE);
        contentPane.add(lblDefaultToLocal);
        
        btnOutput = new JButton("Output Directory");
        sl_contentPane.putConstraint(SpringLayout.WEST, btnOutput, 143, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, btnOutput, -157, SpringLayout.EAST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, lblDefaultToLocal, 0, SpringLayout.EAST, btnOutput);
        btnOutput.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
    			OUTPUT_PATH = getOutputDir();
    			if(OUTPUT_PATH != null) {
    				lblDefaultToLocal.setText(OUTPUT_PATH.getAbsolutePath());
    			}
        	}
        });
        contentPane.add(btnOutput);
        
        rdbtnExpandFromCenter = new JRadioButton("Expand from Center");
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnExpandFromCenter, 6, SpringLayout.SOUTH, scrollPane);
        contentPane.add(rdbtnExpandFromCenter);
        
        rdbtnAddToBorder = new JRadioButton("Add to Border");
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnAddToBorder, 231, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, rdbtnExpandFromCenter, -6, SpringLayout.WEST, rdbtnAddToBorder);
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnAddToBorder, 6, SpringLayout.SOUTH, scrollPane);
        contentPane.add(rdbtnAddToBorder);
        
		ButtonGroup ExpansionType = new ButtonGroup();
		ExpansionType.add(rdbtnExpandFromCenter);
        ExpansionType.add(rdbtnAddToBorder);
        rdbtnExpandFromCenter.setSelected(true);
        
        
        txtSize = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutput, 6, SpringLayout.SOUTH, txtSize);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtSize, -107, SpringLayout.EAST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtSize, 6, SpringLayout.SOUTH, rdbtnAddToBorder);
        txtSize.setHorizontalAlignment(SwingConstants.CENTER);
        txtSize.setText("250");
        contentPane.add(txtSize);
        txtSize.setColumns(10);
        
        JLabel lblSizeOfExpansion = new JLabel("Size of Expansion (bp):");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrent, 40, SpringLayout.SOUTH, lblSizeOfExpansion);
        sl_contentPane.putConstraint(SpringLayout.EAST, lblSizeOfExpansion, -205, SpringLayout.EAST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtSize, 6, SpringLayout.EAST, lblSizeOfExpansion);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblSizeOfExpansion, 2, SpringLayout.NORTH, txtSize);
        contentPane.add(lblSizeOfExpansion);
        btnConvert.addActionListener(this);
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
    
	public static void expandBEDBorders(File out_path, File input) throws IOException {
		
	    String newName = (input.toString()).substring(0,input.toString().length() - 4) + "_" + Integer.toString(SIZE) +"bp.bed";
	    Scanner scan = new Scanner(input);
	    PrintStream OUT = null;
	    if(out_path == null) OUT = new PrintStream(newName);
	    else OUT = new PrintStream(out_path + File.separator + newName);
	    
		while (scan.hasNextLine()) {
			String[] temp = scan.nextLine().split("\t");
			if(temp.length > 2) {
				if(!temp[0].contains("track") && !temp[0].contains("#")) {
					if(Integer.parseInt(temp[1]) >= 0) {
						//Default to add to border
						int newstart = Integer.parseInt(temp[1]) - SIZE;
						int newstop = Integer.parseInt(temp[2]) + SIZE;
				        if(rdbtnExpandFromCenter.isSelected()) { //Else expand from center
				        	int CENTER = (int)((Integer.parseInt(temp[1]) + Integer.parseInt(temp[2])) / 2);
					        if(temp[5].equals("-")) { CENTER++; }
					        newstart = CENTER - (SIZE / 2);
					        newstop = CENTER + (SIZE / 2);
				        }

				        OUT.print(temp[0] + "\t" + newstart + "\t" + newstop);
				        for(int x = 3; x < temp.length; x++) {
				        	OUT.print("\t" + temp[x]);
				        }
				        OUT.println();
					} else {
						System.out.println("Invalid Coordinate in File!!!\n" + Arrays.toString(temp));
					}
				}
			}
	    }
		scan.close();
		OUT.close();
	}
	
	public File[] getCoordFile() {
		fc.setFileFilter(new BEDFilter());
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);
		fc.setDialogTitle("BED File Selection");
		File[] bamFiles = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			bamFiles = fc.getSelectedFiles();
		}
		return bamFiles;
	}
	
	public File getOutputDir() {
		fc.setDialogTitle("Output Directory");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		if (fc.showOpenDialog(fc) == JFileChooser.APPROVE_OPTION) { 
			return fc.getSelectedFile();
		} else {
			return null;
		}	
	}
}


	
