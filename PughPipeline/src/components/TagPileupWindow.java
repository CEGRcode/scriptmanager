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
import objects.PileupParameters;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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
	
	private JButton btnPileup;
	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnOutputDirectory;
	private JRadioButton rdbtnRead1;
	private JRadioButton rdbtnRead2;
	private JRadioButton rdbtnCombined;
	private JRadioButton rdbtnSeperate;
	private JRadioButton rdbtnComb;
	private JRadioButton rdbtnNone;
	private JRadioButton rdbtnGaussianSmooth;
	private JRadioButton rdbtnSlidingWindow;

	private JLabel lblPleaseSelectWhich_1;
	private JLabel lblWindowSizebin;
	private JLabel lblTagShift;
	private JLabel lblStdDevSize;
	private JLabel lblNumStd;
	private JLabel lblBEDFile;
	private JLabel lblDefaultToLocal;
	private JLabel lblCurrentOutput;
	private JTextField txtShift;
	private JTextField txtBin;
	private JTextField txtSmooth;
	private JTextField txtStdSize;
	private JTextField txtNumStd;
	private JCheckBox chckbxOutputData;
	
	JProgressBar progressBar;
	public Task task;
	private JLabel lblCpusToUse;
	private JTextField txtCPU;

	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException, InterruptedException {
        	try {
        		if(Double.parseDouble(txtBin.getText()) < 1) {
        			JOptionPane.showMessageDialog(null, "Invalid Bin Size!!! Must be larger than 0 bp");
        		} else if(rdbtnSlidingWindow.isSelected() && Double.parseDouble(txtSmooth.getText()) < 1) {
        			JOptionPane.showMessageDialog(null, "Invalid Smoothing Window Size!!! Must be larger than 0 bp");
        		} else if(rdbtnGaussianSmooth.isSelected() && Double.parseDouble(txtStdSize.getText()) < 1) {
        			JOptionPane.showMessageDialog(null, "Invalid Standard Deviation Size!!! Must be larger than 0 bp");
        		} else if(rdbtnGaussianSmooth.isSelected() && Double.parseDouble(txtNumStd.getText()) < 1) {
        			JOptionPane.showMessageDialog(null, "Invalid Number of Standard Deviations!!! Must be larger than 0");
        		} else if(Integer.parseInt(txtCPU.getText()) < 1) {
        			JOptionPane.showMessageDialog(null, "Invalid Number of CPU's!!! Must use at least 1");
        		} else if(INPUT == null) {
        			JOptionPane.showMessageDialog(null, "BED File Not Loaded!!!");
        		} else if(BAMFiles.size() < 1) {
        			JOptionPane.showMessageDialog(null, "No BAM Files Loaded!!!");
        		} else {
		        	setProgress(0);
		        	
		        	//Load up parameters for the pileup into single object
		        	PileupParameters param = new PileupParameters();
		        	if(rdbtnSeperate.isSelected()) { param.setStrand(0); }
		        	else if(rdbtnComb.isSelected()) { param.setStrand(1); }
		        	
		        	if(rdbtnRead1.isSelected()) { param.setRead(0); }
		        	else if(rdbtnRead2.isSelected()) { param.setRead(1); }
		        	else if(rdbtnCombined.isSelected()) { param.setRead(2); }
		        	
		        	if(rdbtnNone.isSelected()) { param.setTrans(0); }
		        	else if(rdbtnSlidingWindow.isSelected()) { param.setTrans(1); }
		        	else if(rdbtnGaussianSmooth.isSelected()) { param.setTrans(2); }
		        			        	
		        	if(!chckbxOutputData.isSelected()) { param.setOutput(null); }
		        	else if(OUTPUT == null) { param.setOutput(new File(System.getProperty("user.dir"))); }
		        	else { param.setOutput(OUTPUT); }
		        	
		        	//SHIFT can be negative
		        	param.setShift(Integer.parseInt(txtShift.getText()));
		        	param.setBin(Integer.parseInt(txtBin.getText()));
		        	param.setSmooth(Integer.parseInt(txtSmooth.getText()));
		        	param.setStdSize(Integer.parseInt(txtStdSize.getText()));
		        	param.setStdNum(Integer.parseInt(txtNumStd.getText()));
		        	param.setCPU(Integer.parseInt(txtCPU.getText()));
		        	
		        	loadCoord();
		        	
	        		TagPileup pile = new TagPileup(COORD, BAMFiles, param);
	        		
	        		pile.addPropertyChangeListener("tag", new PropertyChangeListener() {
					    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
					    	int temp = (Integer) propertyChangeEvent.getNewValue();
					    	int percentComplete = (int)(((double)(temp) / BAMFiles.size()) * 100);
				        	setProgress(percentComplete);
					     }
					 });
	        		
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

		setBounds(125, 125, 600, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
	
		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -318, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
		btnLoad = new JButton("Load BAM Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 11, SpringLayout.SOUTH, btnLoad);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 10, SpringLayout.WEST, contentPane);
		btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnRead1, 98, SpringLayout.WEST, contentPane);
		contentPane.add(rdbtnRead1);
		
		rdbtnRead2 = new JRadioButton("Read 2");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnRead2, 0, SpringLayout.NORTH, rdbtnRead1);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnRead2, 98, SpringLayout.EAST, rdbtnRead1);
		contentPane.add(rdbtnRead2);
		
		rdbtnCombined = new JRadioButton("Combined");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnCombined, 0, SpringLayout.NORTH, rdbtnRead1);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnCombined, 90, SpringLayout.EAST, rdbtnRead2);
		sl_contentPane.putConstraint(SpringLayout.EAST, rdbtnCombined, 0, SpringLayout.EAST, scrollPane);
		contentPane.add(rdbtnCombined);
		
		ButtonGroup OutputRead = new ButtonGroup();
        OutputRead.add(rdbtnRead1);
        OutputRead.add(rdbtnRead2);
        OutputRead.add(rdbtnCombined);
        rdbtnRead1.setSelected(true);
        
        JLabel lblPleaseSelectWhich = new JLabel("Please Select Which Read to Output:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnRead1, 6, SpringLayout.SOUTH, lblPleaseSelectWhich);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblPleaseSelectWhich, 6, SpringLayout.SOUTH, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblPleaseSelectWhich, 0, SpringLayout.WEST, scrollPane);
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
        sl_contentPane.putConstraint(SpringLayout.SOUTH, btnOutputDirectory, -6, SpringLayout.NORTH, lblDefaultToLocal);
        sl_contentPane.putConstraint(SpringLayout.EAST, btnOutputDirectory, -224, SpringLayout.EAST, contentPane);
        contentPane.add(btnOutputDirectory);
        
        progressBar = new JProgressBar();
        sl_contentPane.putConstraint(SpringLayout.WEST, progressBar, 0, SpringLayout.WEST, rdbtnCombined);
        sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -15, SpringLayout.EAST, contentPane);
        progressBar.setStringPainted(true);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, progressBar, -4, SpringLayout.SOUTH, contentPane);
        contentPane.add(progressBar);
        
        btnPileup.setActionCommand("start");
        
        lblPleaseSelectWhich_1 = new JLabel("Please Select How to Output Strands:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblPleaseSelectWhich_1, 6, SpringLayout.SOUTH, rdbtnRead1);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblPleaseSelectWhich_1, 0, SpringLayout.WEST, scrollPane);
        lblPleaseSelectWhich_1.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblPleaseSelectWhich_1);
        
        rdbtnSeperate = new JRadioButton("Seperate");
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnSeperate, 6, SpringLayout.SOUTH, lblPleaseSelectWhich_1);
        sl_contentPane.putConstraint(SpringLayout.EAST, rdbtnSeperate, 0, SpringLayout.EAST, lblPleaseSelectWhich);
        rdbtnSeperate.setSelected(true);
        contentPane.add(rdbtnSeperate);
        
        rdbtnComb = new JRadioButton("Combined");
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnComb, 0, SpringLayout.NORTH, rdbtnSeperate);
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnComb, 93, SpringLayout.EAST, rdbtnSeperate);
        contentPane.add(rdbtnComb);
        
        ButtonGroup ReadStrand = new ButtonGroup();
        ReadStrand.add(rdbtnSeperate);
        ReadStrand.add(rdbtnComb);
        rdbtnSeperate.setSelected(true);
        
        lblTagShift = new JLabel("Tag Shift (bp):");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblTagShift, 41, SpringLayout.SOUTH, lblPleaseSelectWhich_1);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblTagShift, 0, SpringLayout.WEST, scrollPane);
        lblTagShift.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblTagShift);
        
        txtShift = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtShift, -6, SpringLayout.NORTH, lblTagShift);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtShift, 6, SpringLayout.EAST, lblTagShift);
        txtShift.setHorizontalAlignment(SwingConstants.CENTER);
        txtShift.setText("0");
        contentPane.add(txtShift);
        txtShift.setColumns(10);
        
        lblStdDevSize = new JLabel("Std Dev Size (bp):");
        lblStdDevSize.setEnabled(false);
        lblStdDevSize.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblStdDevSize);
        
        lblNumStd = new JLabel("# of Std Deviations:");
        lblNumStd.setEnabled(false);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblStdDevSize, 0, SpringLayout.NORTH, lblNumStd);
        lblNumStd.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblNumStd);
        
        JLabel lblBinSizebp = new JLabel("Bin Size (bp):");
        sl_contentPane.putConstraint(SpringLayout.EAST, txtShift, -69, SpringLayout.WEST, lblBinSizebp);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblBinSizebp, 0, SpringLayout.NORTH, lblTagShift);
        lblBinSizebp.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblBinSizebp);
        
        txtBin = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtBin, 6, SpringLayout.SOUTH, rdbtnComb);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtBin, 6, SpringLayout.EAST, lblBinSizebp);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtBin, -213, SpringLayout.EAST, contentPane);
        txtBin.setText("1");
        txtBin.setHorizontalAlignment(SwingConstants.CENTER);
        txtBin.setColumns(10);
        contentPane.add(txtBin);
        
        txtStdSize = new JTextField();
        txtStdSize.setEnabled(false);
        sl_contentPane.putConstraint(SpringLayout.EAST, lblStdDevSize, -6, SpringLayout.WEST, txtStdSize);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtStdSize, 328, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtStdSize, -21, SpringLayout.WEST, lblNumStd);
        txtStdSize.setHorizontalAlignment(SwingConstants.CENTER);
        txtStdSize.setText("5");
        contentPane.add(txtStdSize);
        txtStdSize.setColumns(10);
        
        txtNumStd = new JTextField();
        txtNumStd.setEnabled(false);
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtNumStd, 147, SpringLayout.SOUTH, rdbtnCombined);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtNumStd, 531, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtNumStd, -10, SpringLayout.EAST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, lblNumStd, -6, SpringLayout.WEST, txtNumStd);
        txtNumStd.setHorizontalAlignment(SwingConstants.CENTER);
        txtNumStd.setText("3");
        contentPane.add(txtNumStd);
        txtNumStd.setColumns(10);
        
        JButton btnLoadBedFile = new JButton("Load BED File");
        sl_contentPane.putConstraint(SpringLayout.WEST, btnLoadBedFile, 10, SpringLayout.WEST, contentPane);
		contentPane.add(btnLoadBedFile);
        
        lblBEDFile = new JLabel("No BED File Loaded");
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveBam, 14, SpringLayout.SOUTH, lblBEDFile);
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoadBedFile, -6, SpringLayout.NORTH, lblBEDFile);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblBEDFile, 5, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblBEDFile, 12, SpringLayout.EAST, btnLoadBedFile);
        sl_contentPane.putConstraint(SpringLayout.EAST, lblBEDFile, 0, SpringLayout.EAST, contentPane);
        contentPane.add(lblBEDFile);
        
        chckbxOutputData = new JCheckBox("Output Data");
        sl_contentPane.putConstraint(SpringLayout.WEST, btnOutputDirectory, 101, SpringLayout.EAST, chckbxOutputData);
        sl_contentPane.putConstraint(SpringLayout.WEST, chckbxOutputData, 10, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, chckbxOutputData, -9, SpringLayout.NORTH, lblCurrentOutput);
        chckbxOutputData.setSelected(true);
        contentPane.add(chckbxOutputData);
        
        rdbtnNone = new JRadioButton("None");
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnNone, 0, SpringLayout.WEST, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, rdbtnNone, -124, SpringLayout.SOUTH, contentPane);
        contentPane.add(rdbtnNone);
        
        JLabel lblPleaseSelectComposite = new JLabel("Please Select Composite Transformation:");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblPleaseSelectComposite, 0, SpringLayout.WEST, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblPleaseSelectComposite, -6, SpringLayout.NORTH, rdbtnNone);
        lblPleaseSelectComposite.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblPleaseSelectComposite);
        
        rdbtnGaussianSmooth = new JRadioButton("Gaussian Smooth");
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnGaussianSmooth, 450, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnGaussianSmooth, 0, SpringLayout.WEST, scrollPane);
        contentPane.add(rdbtnGaussianSmooth);
        
        rdbtnSlidingWindow = new JRadioButton("Sliding Window");
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnSlidingWindow, 421, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnSlidingWindow, 0, SpringLayout.WEST, rdbtnRead1);
        contentPane.add(rdbtnSlidingWindow);
        
        
        ButtonGroup trans = new ButtonGroup();
        trans.add(rdbtnNone);
        trans.add(rdbtnSlidingWindow);
        trans.add(rdbtnGaussianSmooth);
        rdbtnNone.setSelected(true);
        
        lblWindowSizebin = new JLabel("Window Size (Bin #):");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblBinSizebp, 0, SpringLayout.WEST, lblWindowSizebin);
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtStdSize, 7, SpringLayout.SOUTH, lblWindowSizebin);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblWindowSizebin, 4, SpringLayout.NORTH, rdbtnNone);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblWindowSizebin, 6, SpringLayout.EAST, rdbtnSlidingWindow);
        lblWindowSizebin.setEnabled(false);
        lblWindowSizebin.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblWindowSizebin);
        
        txtSmooth = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblNumStd, 7, SpringLayout.SOUTH, txtSmooth);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtSmooth, -170, SpringLayout.EAST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtSmooth, -2, SpringLayout.NORTH, rdbtnNone);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtSmooth, 6, SpringLayout.EAST, lblWindowSizebin);
        txtSmooth.setHorizontalAlignment(SwingConstants.CENTER);
        txtSmooth.setEnabled(false);
        txtSmooth.setText("3");
        contentPane.add(txtSmooth);
        txtSmooth.setColumns(10);
        
        lblCpusToUse = new JLabel("CPU's to Use:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblCpusToUse, 0, SpringLayout.NORTH, lblTagShift);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblCpusToUse, 0, SpringLayout.WEST, rdbtnCombined);
        lblCpusToUse.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblCpusToUse);
        
        txtCPU = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtCPU, -6, SpringLayout.NORTH, lblTagShift);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtCPU, 3, SpringLayout.EAST, lblCpusToUse);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtCPU, -15, SpringLayout.EAST, contentPane);
        txtCPU.setHorizontalAlignment(SwingConstants.CENTER);
        txtCPU.setText("1");
        contentPane.add(txtCPU);
        txtCPU.setColumns(10);
        
        rdbtnNone.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		    	  if(rdbtnNone.isSelected()) {
		    		  lblWindowSizebin.setEnabled(false);
		    		  lblStdDevSize.setEnabled(false);
		    		  lblNumStd.setEnabled(false);
		    		  txtSmooth.setEnabled(false);
		    		  txtStdSize.setEnabled(false);
		    		  txtNumStd.setEnabled(false);
		    	  }
		      }
        });
        rdbtnSlidingWindow.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		    	  if(rdbtnSlidingWindow.isSelected()) {
		    		  lblWindowSizebin.setEnabled(true);
		    		  lblStdDevSize.setEnabled(false);
		    		  lblNumStd.setEnabled(false);
		    		  txtSmooth.setEnabled(true);
		    		  txtStdSize.setEnabled(false);
		    		  txtNumStd.setEnabled(false);  		  
		    	  }
		      }
        });
        rdbtnGaussianSmooth.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		    	  if(rdbtnGaussianSmooth.isSelected()) {
		    		  lblWindowSizebin.setEnabled(false);
		    		  lblStdDevSize.setEnabled(true);
		    		  lblNumStd.setEnabled(true);
		    		  txtSmooth.setEnabled(false);
		    		  txtStdSize.setEnabled(true);
		    		  txtNumStd.setEnabled(true); 		  
		    	  }
		      }
        });;
        
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
        
        btnLoadBedFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				fc.setFileFilter(new BEDFilter());
				fc.setDialogTitle("BED File Selection");

				File temp = getBEDFile();
				if(temp != null) {
					INPUT = temp;
					lblBEDFile.setText(INPUT.getName());
				}
			}
		});   
              
        btnOutputDirectory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
		if(status) {
			if(rdbtnNone.isSelected()) {
				lblWindowSizebin.setEnabled(false);
	    		lblStdDevSize.setEnabled(false);
	    		lblNumStd.setEnabled(false);
	    		txtSmooth.setEnabled(false);
	    		txtStdSize.setEnabled(false);
	    		txtNumStd.setEnabled(false);
			}
			if(rdbtnGaussianSmooth.isSelected()) {
				lblWindowSizebin.setEnabled(false);
	    		lblStdDevSize.setEnabled(true);
	    		lblNumStd.setEnabled(true);
	    		txtSmooth.setEnabled(false);
	    		txtStdSize.setEnabled(true);
	    		txtNumStd.setEnabled(true);
			}
			if(rdbtnSlidingWindow.isSelected()) {
				lblWindowSizebin.setEnabled(true);
	    		lblStdDevSize.setEnabled(false);
	    		lblNumStd.setEnabled(false);
	    		txtSmooth.setEnabled(true);
	    		txtStdSize.setEnabled(false);
	    		txtNumStd.setEnabled(false);
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


	
