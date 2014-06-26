package components;

import filters.BAMFilter;
import filters.BEDFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;

import objects.BEDCoord;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import scripts.TagPileup;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

@SuppressWarnings("serial")
public class TagPileupWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	
	final DefaultListModel expList;
	Vector<File> BAMFiles = new Vector<File>();
	Vector<BEDCoord> COORD = null;
	private File INPUT = null;
	private File OUTPUT = null;
	private int READ = 0;
	private int STRAND = 0;
	private int SHIFT = 0;
	private int BIN = 1;
	
	private JButton btnPileup;
	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnOutputDirectory;
	private JRadioButton rdbtnRead1;
	private JRadioButton rdbtnRead2;
	private JRadioButton rdbtnCombined;
	private JRadioButton rdbtnForward;
	private JRadioButton rdbtnReverse;
	private JRadioButton rdbtnComb;
	private JLabel lblPleaseSelectWhich_1;
	private JLabel lblTagShift;
	private JLabel lblStdDevSize;
	private JLabel lblNumStd;
	private JLabel lblBEDFile;
	private JLabel lblDefaultToLocal;
	private JLabel lblCurrentOutput;
	private JTextField txtShift;
	private JTextField txtBin;
	private JTextField txtStdSize;
	private JTextField txtNumStd;
	private JCheckBox chckbxGaussianSmooth;
	private JCheckBox chckbxOutputData;
	
	JProgressBar progressBar;
	public Task task;

	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException, InterruptedException {
        	try {
        		if(Double.parseDouble(txtBin.getText()) < 1) {
        			JOptionPane.showMessageDialog(null, "Invalid Bin Size!!! Must be larger than 1 bp");
        		} else if(Double.parseDouble(txtStdSize.getText()) < 1) {
        			JOptionPane.showMessageDialog(null, "Invalid Standard Deviation Size!!! Must be larger than 0 bp");
        		} else if(Double.parseDouble(txtNumStd.getText()) < 1) {
        			JOptionPane.showMessageDialog(null, "Invalid Number of Standard Deviations!!! Must be larger than 0");
        		} else if(INPUT == null) {
        			JOptionPane.showMessageDialog(null, "BED File Not Loaded!!!");
        		} else if(BAMFiles.size() < 1) {
        			JOptionPane.showMessageDialog(null, "No BAM Files Loaded!!!");
        		} else {
		        	setProgress(0);
		        	if(rdbtnForward.isSelected()) { STRAND = 0; }
		        	else if(rdbtnReverse.isSelected()) { STRAND = 1; }
		        	else if(rdbtnComb.isSelected()) { STRAND = 2; }
		        	
		        	if(rdbtnRead1.isSelected()) { READ = 0; }
		        	else if(rdbtnRead2.isSelected()) { READ = 1; }
		        	else if(rdbtnCombined.isSelected()) { READ = 2; }
		        	
		        	loadCoord();
		        	
		        	if(OUTPUT == null) { OUTPUT = new File(System.getProperty("user.dir")); }
		        	if(!chckbxOutputData.isSelected()) { OUTPUT = null; }
		        	
	        		TagPileup pile = new TagPileup(COORD, BAMFiles, OUTPUT, READ, STRAND, SHIFT, BIN);
	        		pile.setVisible(true);
					pile.run();

		        	setProgress(100);
		        	return null;
        		}
        	} catch(NumberFormatException nfe){
				JOptionPane.showMessageDialog(null, "Invalid Input in Fields!!!");
			}
			return null;
        }
        
        public void done() {
    		massXable(contentPane, true);
    		if(!chckbxOutputData.isSelected()) { btnOutputDirectory.setEnabled(false); }
            setCursor(null); //turn off the wait cursor
        }
	}
	
	public TagPileupWindow() {
		setTitle("Tag Pileup");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 650, 550);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
	
		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -15, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
		btnLoad = new JButton("Load BAM Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, btnLoad);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 0, SpringLayout.WEST, scrollPane);
		btnLoad.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				fc.setFileFilter(new BAMFilter());
				fc.setMultiSelectionEnabled(true);
				fc.setDialogTitle("BAM File Selection");

				File[] newBAMFiles = getBAMFiles();
				if(newBAMFiles != null) {
					for(int x = 0; x < newBAMFiles.length; x++) { 
						BAMFiles.add(newBAMFiles[x]);
						expList.addElement(newBAMFiles[x].getName());
					}
				}
			}
		});
		contentPane.add(btnLoad);
		
      	expList = new DefaultListModel();
		final JList listExp = new JList(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listExp);
		
		btnRemoveBam = new JButton("Remove BAM");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 0, SpringLayout.NORTH, btnRemoveBam);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBam, -10, SpringLayout.EAST, contentPane);
		btnRemoveBam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(listExp.getSelectedIndex() > -1) {
					BAMFiles.remove(listExp.getSelectedIndex());
					expList.remove(listExp.getSelectedIndex());
				}
			}
		});		
		contentPane.add(btnRemoveBam);
		
		btnPileup = new JButton("Pile Tags");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnPileup, 250, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnPileup, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnPileup, -250, SpringLayout.EAST, contentPane);
		contentPane.add(btnPileup);
		
		rdbtnRead1 = new JRadioButton("Read 1");
		contentPane.add(rdbtnRead1);
		
		rdbtnRead2 = new JRadioButton("Read 2");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnRead2, 0, SpringLayout.NORTH, rdbtnRead1);
		contentPane.add(rdbtnRead2);
		
		rdbtnCombined = new JRadioButton("Combined");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnCombined, 0, SpringLayout.NORTH, rdbtnRead1);
		contentPane.add(rdbtnCombined);
		
		ButtonGroup OutputRead = new ButtonGroup();
        OutputRead.add(rdbtnRead1);
        OutputRead.add(rdbtnRead2);
        OutputRead.add(rdbtnCombined);
        rdbtnRead1.setSelected(true);
        
        JLabel lblPleaseSelectWhich = new JLabel("Please Select Which Read to Output:");
        sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -6, SpringLayout.NORTH, lblPleaseSelectWhich);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblPleaseSelectWhich, 10, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblPleaseSelectWhich, -6, SpringLayout.NORTH, rdbtnRead1);
        lblPleaseSelectWhich.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblPleaseSelectWhich);

        lblDefaultToLocal = new JLabel("Default to Local Directory");
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblDefaultToLocal, -6, SpringLayout.NORTH, btnPileup);
        sl_contentPane.putConstraint(SpringLayout.EAST, lblDefaultToLocal, 0, SpringLayout.EAST, contentPane);
        lblDefaultToLocal.setBackground(Color.WHITE);
        contentPane.add(lblDefaultToLocal);
        
        lblCurrentOutput = new JLabel("Current Output:");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrentOutput, 10, SpringLayout.WEST, contentPane);
        lblCurrentOutput.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, lblCurrentOutput);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblCurrentOutput, -35, SpringLayout.SOUTH, contentPane);
        contentPane.add(lblCurrentOutput);
		
        btnOutputDirectory = new JButton("Output Directory");
        sl_contentPane.putConstraint(SpringLayout.WEST, btnOutputDirectory, 250, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, btnOutputDirectory, -57, SpringLayout.SOUTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, btnOutputDirectory, -250, SpringLayout.EAST, contentPane);
        contentPane.add(btnOutputDirectory);
        
        progressBar = new JProgressBar();
        sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -15, SpringLayout.EAST, contentPane);
        progressBar.setStringPainted(true);
        sl_contentPane.putConstraint(SpringLayout.WEST, progressBar, -225, SpringLayout.EAST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, progressBar, -4, SpringLayout.SOUTH, contentPane);
        contentPane.add(progressBar);
        
        btnPileup.setActionCommand("start");
        
        lblPleaseSelectWhich_1 = new JLabel("Please Select Which Strand to Output:");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblPleaseSelectWhich_1, 10, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, rdbtnRead1, -6, SpringLayout.NORTH, lblPleaseSelectWhich_1);
        lblPleaseSelectWhich_1.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblPleaseSelectWhich_1);
        
        rdbtnForward = new JRadioButton("Forward");
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnRead1, 0, SpringLayout.WEST, rdbtnForward);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblPleaseSelectWhich_1, -6, SpringLayout.NORTH, rdbtnForward);
        rdbtnForward.setSelected(true);
        contentPane.add(rdbtnForward);
        
        rdbtnReverse = new JRadioButton("Reverse");
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnRead2, 0, SpringLayout.WEST, rdbtnReverse);
        sl_contentPane.putConstraint(SpringLayout.EAST, rdbtnForward, -96, SpringLayout.WEST, rdbtnReverse);
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnReverse, 0, SpringLayout.NORTH, rdbtnForward);
        contentPane.add(rdbtnReverse);
        
        rdbtnComb = new JRadioButton("Combined");
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnCombined, 0, SpringLayout.WEST, rdbtnComb);
        sl_contentPane.putConstraint(SpringLayout.EAST, rdbtnCombined, 3, SpringLayout.EAST, rdbtnComb);
        sl_contentPane.putConstraint(SpringLayout.EAST, rdbtnComb, -84, SpringLayout.EAST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, rdbtnReverse, -93, SpringLayout.WEST, rdbtnComb);
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnComb, 0, SpringLayout.NORTH, rdbtnForward);
        contentPane.add(rdbtnComb);
        
        ButtonGroup ReadStrand = new ButtonGroup();
        ReadStrand.add(rdbtnForward);
        ReadStrand.add(rdbtnReverse);
        ReadStrand.add(rdbtnComb);
        rdbtnForward.setSelected(true);
        
        lblTagShift = new JLabel("Tag Shift (bp):");
        sl_contentPane.putConstraint(SpringLayout.SOUTH, rdbtnForward, -19, SpringLayout.NORTH, lblTagShift);
        sl_contentPane.putConstraint(SpringLayout.EAST, lblTagShift, 0, SpringLayout.EAST, rdbtnForward);
        lblTagShift.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblTagShift);
        
        txtShift = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtShift, -6, SpringLayout.NORTH, lblTagShift);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtShift, 28, SpringLayout.EAST, lblTagShift);
        txtShift.setHorizontalAlignment(SwingConstants.CENTER);
        txtShift.setText("0");
        contentPane.add(txtShift);
        txtShift.setColumns(10);
        
        chckbxGaussianSmooth = new JCheckBox("Gaussian Smooth");
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblTagShift, -14, SpringLayout.NORTH, chckbxGaussianSmooth);
        sl_contentPane.putConstraint(SpringLayout.WEST, chckbxGaussianSmooth, 10, SpringLayout.WEST, contentPane);
        contentPane.add(chckbxGaussianSmooth);
        
        lblStdDevSize = new JLabel("Std Dev Size (bp):");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblStdDevSize, 6, SpringLayout.EAST, chckbxGaussianSmooth);
        sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxGaussianSmooth, -4, SpringLayout.NORTH, lblStdDevSize);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblStdDevSize, -6, SpringLayout.NORTH, btnOutputDirectory);
        lblStdDevSize.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblStdDevSize);
        
        lblNumStd = new JLabel("# of Std Deviations:");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblNumStd, 380, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblNumStd, -6, SpringLayout.NORTH, btnOutputDirectory);
        lblNumStd.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblNumStd);
        
        JLabel lblBinSizebp = new JLabel("Bin Size (bp):");
        sl_contentPane.putConstraint(SpringLayout.EAST, lblBinSizebp, -206, SpringLayout.EAST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtShift, -56, SpringLayout.WEST, lblBinSizebp);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblBinSizebp, 0, SpringLayout.NORTH, lblTagShift);
        lblBinSizebp.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblBinSizebp);
        
        txtBin = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtBin, -6, SpringLayout.NORTH, lblTagShift);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtBin, 36, SpringLayout.EAST, lblBinSizebp);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtBin, -98, SpringLayout.EAST, contentPane);
        txtBin.setText("1");
        txtBin.setHorizontalAlignment(SwingConstants.CENTER);
        txtBin.setColumns(10);
        contentPane.add(txtBin);
        
        txtStdSize = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtStdSize, -2, SpringLayout.NORTH, chckbxGaussianSmooth);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtStdSize, 13, SpringLayout.EAST, lblStdDevSize);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtStdSize, -20, SpringLayout.WEST, lblNumStd);
        txtStdSize.setHorizontalAlignment(SwingConstants.CENTER);
        txtStdSize.setText("5");
        contentPane.add(txtStdSize);
        txtStdSize.setColumns(10);
        
        txtNumStd = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtNumStd, -2, SpringLayout.NORTH, chckbxGaussianSmooth);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtNumStd, 7, SpringLayout.EAST, lblNumStd);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtNumStd, -50, SpringLayout.EAST, contentPane);
        txtNumStd.setHorizontalAlignment(SwingConstants.CENTER);
        txtNumStd.setText("3");
        contentPane.add(txtNumStd);
        txtNumStd.setColumns(10);
        
        txtStdSize.setEditable(false);
        txtNumStd.setEditable(false);
        txtStdSize.setForeground(Color.GRAY);
        txtNumStd.setForeground(Color.GRAY);
        lblStdDevSize.setForeground(Color.GRAY);
        lblNumStd.setForeground(Color.GRAY);
        
        chckbxGaussianSmooth.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		        if(chckbxGaussianSmooth.isSelected()) {
		        	txtStdSize.setEditable(true);
		            txtNumStd.setEditable(true);
		            txtStdSize.setForeground(Color.BLACK);
		            txtNumStd.setForeground(Color.BLACK);
		            lblStdDevSize.setForeground(Color.BLACK);
		            lblNumStd.setForeground(Color.BLACK);

		        } else {
		        	txtStdSize.setEditable(false);
		            txtNumStd.setEditable(false);
		            txtStdSize.setForeground(Color.GRAY);
		            txtNumStd.setForeground(Color.GRAY);
		            lblStdDevSize.setForeground(Color.GRAY);
		            lblNumStd.setForeground(Color.GRAY);		        	
		        }
		      }
		    });
        
        JButton btnLoadBedFile = new JButton("Load BED File");
        sl_contentPane.putConstraint(SpringLayout.WEST, btnLoadBedFile, 0, SpringLayout.WEST, scrollPane);
		contentPane.add(btnLoadBedFile);
        
        lblBEDFile = new JLabel("No BED File Loaded");
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveBam, 14, SpringLayout.SOUTH, lblBEDFile);
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoadBedFile, -6, SpringLayout.NORTH, lblBEDFile);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblBEDFile, 5, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblBEDFile, 12, SpringLayout.EAST, btnLoadBedFile);
        sl_contentPane.putConstraint(SpringLayout.EAST, lblBEDFile, 0, SpringLayout.EAST, contentPane);
        contentPane.add(lblBEDFile);
        
        chckbxOutputData = new JCheckBox("Output Data");
        chckbxOutputData.setSelected(true);
        sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxOutputData, 6, SpringLayout.SOUTH, chckbxGaussianSmooth);
        sl_contentPane.putConstraint(SpringLayout.WEST, chckbxOutputData, 0, SpringLayout.WEST, scrollPane);
        contentPane.add(chckbxOutputData);
        
        chckbxOutputData.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
			        if(chckbxOutputData.isSelected()) {
			        	btnOutputDirectory.setEnabled(true);
			        	lblDefaultToLocal.setForeground(Color.BLACK);
			        	lblCurrentOutput.setForeground(Color.BLACK);

			        } else {
			        	btnOutputDirectory.setEnabled(false);
			        	lblDefaultToLocal.setForeground(Color.GRAY);
			        	lblCurrentOutput.setForeground(Color.GRAY);
			        }
			      }
			    });
        
        btnLoadBedFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				fc.setFileFilter(new BEDFilter());
				fc.setDialogTitle("BED File Selection");

				File temp = getBEDFile();
				if(temp != null) {
					INPUT = temp;
					lblBEDFile.setText(INPUT.getName());
				}
			}
		});   
              
        btnOutputDirectory.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				OUTPUT = getOutputDir();
				if(OUTPUT != null) {
					lblDefaultToLocal.setText(OUTPUT.getAbsolutePath());
				}
			}
		});
        
        btnPileup.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		massXable(contentPane, false);
    	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
	}
	
	public void massXable(Container con, boolean status) {
		Component[] components = con.getComponents();
		for (Component component : components) {
			component.setEnabled(status);
			if (component instanceof Container) {
				massXable((Container)component, status);
			}
		}
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
	
    public void loadCoord() throws FileNotFoundException {
		Scanner scan = new Scanner(INPUT);
		COORD = new Vector<BEDCoord>();
		while (scan.hasNextLine()) {
			String[] temp = scan.nextLine().split("\t");
			if(temp.length > 2) { 
				if(!temp[0].contains("track") && !temp[0].contains("#")) {
					String name = "";
					if(temp.length > 3) { name = temp[3]; }
					else { name = temp[0] + "_" + temp[1] + "_" + temp[2]; }
					if(temp[5].equals("+")) {
						COORD.add(new BEDCoord(temp[0], Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), "+", name));
					}
					else {
						COORD.add(new BEDCoord(temp[0], Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), "-", name));
					}
				}
			}
		}
		scan.close();
    }
    
	public File[] getBAMFiles() {
		File[] bamFiles = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			bamFiles = fc.getSelectedFiles();
		}
		return bamFiles;
	}
	
	public File getBEDFile() {
		File bedFile = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			bedFile = fc.getSelectedFile();
		}
		return bedFile;
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


	
