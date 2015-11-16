package window_interface.Data_Analysis;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
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

import objects.PileupParameters;
import scripts.Data_Analysis.TagPileup;
import util.FileSelection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JSeparator;

@SuppressWarnings("serial")
public class TagPileupWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	
	final DefaultListModel expList;
	Vector<File> BAMFiles = new Vector<File>();
	final DefaultListModel bedList;
	Vector<File> BEDFiles = new Vector<File>();
	private File OUTPUT = null;
	
	private JButton btnPileup;
	private JButton btnLoadBamFiles;
	private JButton btnRemoveBam;
	private JButton btnOutputDirectory;
	private JButton btnSenseColor;
	private JButton btnAntiColor;
	private JButton btnCombinedColor;
	private JRadioButton rdbtnRead1;
	private JRadioButton rdbtnRead2;
	private JRadioButton rdbtnCombined;
	private JRadioButton rdbtnSeperate;
	private JRadioButton rdbtnComb;
	private JRadioButton rdbtnNone;
	private JRadioButton rdbtnGaussianSmooth;
	private JRadioButton rdbtnSlidingWindow;
	private JRadioButton rdbtnTabdelimited;
	private JRadioButton rdbtnCdt;

	private JLabel lblPleaseSelectWhich_1;
	private JLabel lblWindowSizebin;
	private JLabel lblTagShift;
	private JLabel lblStdDevSize;
	private JLabel lblNumStd;
	private JLabel lblDefaultToLocal;
	private JLabel lblCurrentOutput;
	private JLabel lblPleaseSelectOutput;
	private JLabel lblCpusToUse;
	private JTextField txtShift;
	private JTextField txtBin;
	private JTextField txtSmooth;
	private JTextField txtStdSize;
	private JTextField txtNumStd;
	private JTextField txtCPU;
	private JTextField txtCompositeName;
	private JCheckBox chckbxOutputData;
	private JCheckBox chckbxOutputCompositeData;
	private JCheckBox chckbxTagStandard;
	
	JProgressBar progressBar;
	public Task task;

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
        		} else if(BEDFiles.size() < 1) {
        			JOptionPane.showMessageDialog(null, "No BED Files Loaded!!!");
        		} else if(BAMFiles.size() < 1) {
        			JOptionPane.showMessageDialog(null, "No BAM Files Loaded!!!");
        		} else {
		        	setProgress(0);
		        	
		        	//Load up parameters for the pileup into single object
		        	PileupParameters param = new PileupParameters();
		        	if(rdbtnSeperate.isSelected()) { 
		        		param.setStrand(0);
		        		param.setSenseColor(btnSenseColor.getForeground());
		        		param.setAntiColor(btnAntiColor.getForeground());
		        	} else if(rdbtnComb.isSelected()) {
		        		param.setStrand(1);
		        		param.setCombinedColor(btnCombinedColor.getForeground());
		        	}
		        	
		        	if(rdbtnRead1.isSelected()) { param.setRead(0); }
		        	else if(rdbtnRead2.isSelected()) { param.setRead(1); }
		        	else if(rdbtnCombined.isSelected()) { param.setRead(2); }
		        	
		        	if(rdbtnNone.isSelected()) { param.setTrans(0); }
		        	else if(rdbtnSlidingWindow.isSelected()) { param.setTrans(1); }
		        	else if(rdbtnGaussianSmooth.isSelected()) { param.setTrans(2); }
		        			        	
		        	if(!chckbxOutputData.isSelected()) { param.setOutputType(0); }
		        	if(!chckbxOutputData.isSelected() && !chckbxOutputCompositeData.isSelected()) {	param.setOutput(null); }
		        	else if(OUTPUT == null) { param.setOutput(new File(System.getProperty("user.dir"))); }
		        	else { param.setOutput(OUTPUT); }
		        	
		        	param.setOutputCompositeStatus(chckbxOutputCompositeData.isSelected()); //Outputs composite plots if check box is selected
		        	if(chckbxOutputCompositeData.isSelected()) { param.setCompositeFile(txtCompositeName.getText()); }
		        	
		        	if(chckbxOutputData.isSelected() && rdbtnTabdelimited.isSelected()) param.setOutputType(1);
		        	else if(chckbxOutputData.isSelected() && rdbtnCdt.isSelected()) param.setOutputType(2);
		        			        	
		        	if(chckbxTagStandard.isSelected()) param.setStandard(true);
		        	else param.setStandard(false);
		        	
		        	//SHIFT can be negative
		        	param.setShift(Integer.parseInt(txtShift.getText()));
		        	param.setBin(Integer.parseInt(txtBin.getText()));
		        	param.setSmooth(Integer.parseInt(txtSmooth.getText()));
		        	param.setStdSize(Integer.parseInt(txtStdSize.getText()));
		        	param.setStdNum(Integer.parseInt(txtNumStd.getText()));
		        	param.setCPU(Integer.parseInt(txtCPU.getText()));

		        	TagPileup pile = new TagPileup(BEDFiles, BAMFiles, param);
	        		
	        		pile.addPropertyChangeListener("tag", new PropertyChangeListener() {
					    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
					    	int temp = (Integer) propertyChangeEvent.getNewValue();
					    	int percentComplete = (int)(((double)(temp) / (BAMFiles.size() * BEDFiles.size())) * 100);
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
            setCursor(null); //turn off the wait cursor
        }
	}
	
	public TagPileupWindow() {
		setTitle("Tag Pileup");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 600, 870);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
	
		JScrollPane scrollPane_BAM = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane_BAM, 207, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane_BAM, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane_BAM, -10, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane_BAM);
		
		btnLoadBamFiles = new JButton("Load BAM Files");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoadBamFiles, 0, SpringLayout.WEST, scrollPane_BAM);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnLoadBamFiles, -6, SpringLayout.NORTH, scrollPane_BAM);
		btnLoadBamFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				File[] newBAMFiles = FileSelection.getBAMFiles(fc);
				if(newBAMFiles != null) {
					for(int x = 0; x < newBAMFiles.length; x++) { 
						BAMFiles.add(newBAMFiles[x]);
						expList.addElement(newBAMFiles[x].getName());
					}
				}
			}
		});
		contentPane.add(btnLoadBamFiles);
		
      	expList = new DefaultListModel();
		final JList listExp = new JList(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane_BAM.setViewportView(listExp);
		
		btnRemoveBam = new JButton("Remove BAM");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnRemoveBam, -6, SpringLayout.NORTH, scrollPane_BAM);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBam, 0, SpringLayout.EAST, scrollPane_BAM);
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
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnRead1, 125, SpringLayout.WEST, contentPane);
		contentPane.add(rdbtnRead1);
		
		rdbtnRead2 = new JRadioButton("Read 2");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnRead2, 0, SpringLayout.NORTH, rdbtnRead1);
		sl_contentPane.putConstraint(SpringLayout.EAST, rdbtnRead2, 340, SpringLayout.WEST, contentPane);
		contentPane.add(rdbtnRead2);
		
		rdbtnCombined = new JRadioButton("Combined");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnCombined, 0, SpringLayout.NORTH, rdbtnRead1);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnCombined, 125, SpringLayout.WEST, rdbtnRead2);
		contentPane.add(rdbtnCombined);
		
		ButtonGroup OutputRead = new ButtonGroup();
        OutputRead.add(rdbtnRead1);
        OutputRead.add(rdbtnRead2);
        OutputRead.add(rdbtnCombined);
        rdbtnRead1.setSelected(true);
        
        JLabel lblPleaseSelectWhich = new JLabel("Please Select Which Read to Output:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblPleaseSelectWhich, 353, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane_BAM, -5, SpringLayout.NORTH, lblPleaseSelectWhich);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblPleaseSelectWhich, 10, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnRead1, 6, SpringLayout.SOUTH, lblPleaseSelectWhich);
        lblPleaseSelectWhich.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblPleaseSelectWhich);

        lblDefaultToLocal = new JLabel("Default to Local Directory");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 0, SpringLayout.WEST, rdbtnRead1);
        sl_contentPane.putConstraint(SpringLayout.EAST, lblDefaultToLocal, -13, SpringLayout.EAST, contentPane);
        lblDefaultToLocal.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblDefaultToLocal.setBackground(Color.WHITE);
        contentPane.add(lblDefaultToLocal);
        
        lblCurrentOutput = new JLabel("Current Output:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, lblCurrentOutput);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrentOutput, 0, SpringLayout.WEST, scrollPane_BAM);
        lblCurrentOutput.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblCurrentOutput);
		
        btnOutputDirectory = new JButton("Output Directory");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrentOutput, 10, SpringLayout.SOUTH, btnOutputDirectory);
        contentPane.add(btnOutputDirectory);
        
        progressBar = new JProgressBar();
        sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnPileup);
        sl_contentPane.putConstraint(SpringLayout.WEST, progressBar, 99, SpringLayout.EAST, btnPileup);
        sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -10, SpringLayout.EAST, contentPane);
        progressBar.setStringPainted(true);
        contentPane.add(progressBar);
        
        btnPileup.setActionCommand("start");
        
        lblPleaseSelectWhich_1 = new JLabel("Please Select How to Output Strands:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblPleaseSelectWhich_1, 6, SpringLayout.SOUTH, rdbtnRead1);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblPleaseSelectWhich_1, 10, SpringLayout.WEST, contentPane);
        lblPleaseSelectWhich_1.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblPleaseSelectWhich_1);
        
        rdbtnSeperate = new JRadioButton("Seperate");
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnSeperate, 6, SpringLayout.SOUTH, lblPleaseSelectWhich_1);
        sl_contentPane.putConstraint(SpringLayout.EAST, rdbtnSeperate, 0, SpringLayout.EAST, lblPleaseSelectWhich);
        rdbtnSeperate.setSelected(true);
        rdbtnSeperate.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		    	  if(rdbtnSeperate.isSelected()) {
		    			btnSenseColor.setEnabled(true);
		    			btnAntiColor.setEnabled(true);
		    			btnCombinedColor.setEnabled(false);
		    	  }
		      }
        });
        contentPane.add(rdbtnSeperate);
        
        rdbtnComb = new JRadioButton("Combined");
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnComb, 0, SpringLayout.NORTH, rdbtnSeperate);
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnComb, 90, SpringLayout.EAST, rdbtnSeperate);
        rdbtnComb.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		    	  if(rdbtnComb.isSelected()) {
		    			btnSenseColor.setEnabled(false);
		    			btnAntiColor.setEnabled(false);
		    			btnCombinedColor.setEnabled(true);
		    	  }
		      }
        });
        contentPane.add(rdbtnComb);
        
        ButtonGroup ReadStrand = new ButtonGroup();
        ReadStrand.add(rdbtnSeperate);
        ReadStrand.add(rdbtnComb);
        rdbtnSeperate.setSelected(true);
        
        lblTagShift = new JLabel("Tag Shift (bp):");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblTagShift, 0, SpringLayout.WEST, scrollPane_BAM);
        lblTagShift.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblTagShift);
        
        txtShift = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtShift, -1, SpringLayout.NORTH, lblTagShift);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtShift, 10, SpringLayout.EAST, lblTagShift);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtShift, 80, SpringLayout.EAST, lblTagShift);
        txtShift.setHorizontalAlignment(SwingConstants.CENTER);
        txtShift.setText("0");
        contentPane.add(txtShift);
        txtShift.setColumns(10);
        
        lblStdDevSize = new JLabel("Std Dev Size (Bin #):");
        lblStdDevSize.setEnabled(false);
        lblStdDevSize.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblStdDevSize);
        
        lblNumStd = new JLabel("# of Std Deviations:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblNumStd, 0, SpringLayout.NORTH, lblStdDevSize);
        lblNumStd.setEnabled(false);
        lblNumStd.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblNumStd);
        
        JLabel lblBinSizebp = new JLabel("Bin Size (bp):");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblBinSizebp, 0, SpringLayout.NORTH, lblTagShift);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblBinSizebp, 26, SpringLayout.EAST, txtShift);
        lblBinSizebp.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblBinSizebp);
        
        txtBin = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtBin, -1, SpringLayout.NORTH, lblTagShift);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtBin, 10, SpringLayout.EAST, lblBinSizebp);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtBin, 80, SpringLayout.EAST, lblBinSizebp);
        txtBin.setText("1");
        txtBin.setHorizontalAlignment(SwingConstants.CENTER);
        txtBin.setColumns(10);
        contentPane.add(txtBin);
        
        txtStdSize = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.WEST, lblNumStd, 10, SpringLayout.EAST, txtStdSize);
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtStdSize, -1, SpringLayout.NORTH, lblStdDevSize);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtStdSize, 10, SpringLayout.EAST, lblStdDevSize);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtStdSize, 60, SpringLayout.EAST, lblStdDevSize);
        txtStdSize.setEnabled(false);
        txtStdSize.setHorizontalAlignment(SwingConstants.CENTER);
        txtStdSize.setText("5");
        contentPane.add(txtStdSize);
        txtStdSize.setColumns(10);
        
        txtNumStd = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtNumStd, -1, SpringLayout.NORTH, lblStdDevSize);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtNumStd, 10, SpringLayout.EAST, lblNumStd);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtNumStd, 60, SpringLayout.EAST, lblNumStd);
        txtNumStd.setEnabled(false);
        txtNumStd.setHorizontalAlignment(SwingConstants.CENTER);
        txtNumStd.setText("3");
        contentPane.add(txtNumStd);
        txtNumStd.setColumns(10);
        
        JButton btnLoadBedFile = new JButton("Load BED Files");
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoadBedFile, -1, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, btnLoadBedFile, 10, SpringLayout.WEST, contentPane);
		contentPane.add(btnLoadBedFile);
        
        chckbxOutputData = new JCheckBox("Output for Heatmap");
        sl_contentPane.putConstraint(SpringLayout.WEST, btnOutputDirectory, 28, SpringLayout.EAST, chckbxOutputData);
        sl_contentPane.putConstraint(SpringLayout.WEST, chckbxOutputData, 0, SpringLayout.WEST, scrollPane_BAM);
        chckbxOutputData.setSelected(true);
        contentPane.add(chckbxOutputData);
        
        rdbtnNone = new JRadioButton("None");
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnNone, 0, SpringLayout.WEST, scrollPane_BAM);
        contentPane.add(rdbtnNone);
        
        JLabel lblPleaseSelectComposite = new JLabel("Please Select Composite Transformation:");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblPleaseSelectComposite, 10, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnNone, 10, SpringLayout.SOUTH, lblPleaseSelectComposite);
        lblPleaseSelectComposite.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblPleaseSelectComposite);
        
        rdbtnGaussianSmooth = new JRadioButton("Gaussian Smooth");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblStdDevSize, 3, SpringLayout.NORTH, rdbtnGaussianSmooth);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblStdDevSize, 10, SpringLayout.EAST, rdbtnGaussianSmooth);
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnGaussianSmooth, 5, SpringLayout.SOUTH, rdbtnNone);
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnGaussianSmooth, 0, SpringLayout.WEST, scrollPane_BAM);
        contentPane.add(rdbtnGaussianSmooth);
        
        rdbtnSlidingWindow = new JRadioButton("Sliding Window");
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnSlidingWindow, 0, SpringLayout.NORTH, rdbtnNone);
        sl_contentPane.putConstraint(SpringLayout.EAST, rdbtnSlidingWindow, 0, SpringLayout.EAST, lblPleaseSelectWhich);
        contentPane.add(rdbtnSlidingWindow);
               
        ButtonGroup trans = new ButtonGroup();
        trans.add(rdbtnNone);
        trans.add(rdbtnSlidingWindow);
        trans.add(rdbtnGaussianSmooth);
        rdbtnNone.setSelected(true);
        
        lblWindowSizebin = new JLabel("Window Size (Bin #):");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblWindowSizebin, 3, SpringLayout.NORTH, rdbtnNone);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblWindowSizebin, 10, SpringLayout.EAST, rdbtnSlidingWindow);
        lblWindowSizebin.setEnabled(false);
        lblWindowSizebin.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblWindowSizebin);
        
        txtSmooth = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtSmooth, 2, SpringLayout.NORTH, rdbtnNone);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtSmooth, 10, SpringLayout.EAST, lblWindowSizebin);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtSmooth, 80, SpringLayout.EAST, lblWindowSizebin);
        txtSmooth.setHorizontalAlignment(SwingConstants.CENTER);
        txtSmooth.setEnabled(false);
        txtSmooth.setText("3");
        contentPane.add(txtSmooth);
        txtSmooth.setColumns(10);
        
        lblCpusToUse = new JLabel("CPU's to Use:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblCpusToUse, 0, SpringLayout.NORTH, lblTagShift);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblCpusToUse, 21, SpringLayout.EAST, txtBin);
        lblCpusToUse.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblCpusToUse);
        
        txtCPU = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtCPU, -1, SpringLayout.NORTH, lblTagShift);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtCPU, 10, SpringLayout.EAST, lblCpusToUse);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtCPU, 80, SpringLayout.EAST, lblCpusToUse);
        txtCPU.setHorizontalAlignment(SwingConstants.CENTER);
        txtCPU.setText("1");
        contentPane.add(txtCPU);
        txtCPU.setColumns(10);
        
        rdbtnTabdelimited = new JRadioButton("TAB-Delimited");
        sl_contentPane.putConstraint(SpringLayout.EAST, btnOutputDirectory, 0, SpringLayout.EAST, rdbtnTabdelimited);
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnTabdelimited, 0, SpringLayout.WEST, btnPileup);
        contentPane.add(rdbtnTabdelimited);
        rdbtnCdt = new JRadioButton("CDT");
        rdbtnCdt.setSelected(true);
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnCdt, 0, SpringLayout.NORTH, rdbtnTabdelimited);
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnCdt, 0, SpringLayout.WEST, txtSmooth);
        contentPane.add(rdbtnCdt);
        
        ButtonGroup output = new ButtonGroup();
        output.add(rdbtnTabdelimited);
        output.add(rdbtnCdt);
        
        lblPleaseSelectOutput = new JLabel("Please select Output File Format:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblPleaseSelectOutput, 10, SpringLayout.SOUTH, chckbxOutputData);
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnTabdelimited, -3, SpringLayout.NORTH, lblPleaseSelectOutput);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblPleaseSelectOutput, 0, SpringLayout.WEST, scrollPane_BAM);
        lblPleaseSelectOutput.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblPleaseSelectOutput);
        
        chckbxTagStandard = new JCheckBox("Set Tags to Be Equal");
        sl_contentPane.putConstraint(SpringLayout.EAST, chckbxTagStandard, 375, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxTagStandard, 10, SpringLayout.SOUTH, txtBin);
        sl_contentPane.putConstraint(SpringLayout.WEST, chckbxTagStandard, 210, SpringLayout.WEST, contentPane);
        contentPane.add(chckbxTagStandard);
        
        btnSenseColor = new JButton("Sense Color");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblTagShift, 10, SpringLayout.SOUTH, btnSenseColor);
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnSenseColor, 35, SpringLayout.SOUTH, lblPleaseSelectWhich_1);
        btnSenseColor.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		btnSenseColor.setForeground(JColorChooser.showDialog(btnSenseColor, "Select an Output Color", btnSenseColor.getForeground()));
        	}
        });
        btnSenseColor.setForeground(Color.BLUE);
        sl_contentPane.putConstraint(SpringLayout.WEST, btnSenseColor, 50, SpringLayout.WEST, contentPane);
        contentPane.add(btnSenseColor);
        
        btnAntiColor = new JButton("Anti Color");
        btnAntiColor.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		btnAntiColor.setForeground(JColorChooser.showDialog(btnAntiColor, "Select an Output Color", btnAntiColor.getForeground()));
        	}
        });
        btnAntiColor.setForeground(Color.RED);
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnAntiColor, 0, SpringLayout.NORTH, btnSenseColor);
        sl_contentPane.putConstraint(SpringLayout.WEST, btnAntiColor, 75, SpringLayout.EAST, btnSenseColor);
        contentPane.add(btnAntiColor);
        
        btnCombinedColor = new JButton("Combined Color");
        btnCombinedColor.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		btnCombinedColor.setForeground(JColorChooser.showDialog(btnCombinedColor, "Select an Output Color", btnCombinedColor.getForeground()));
        	}
        });
        btnCombinedColor.setEnabled(false);
        btnCombinedColor.setForeground(new Color(0, 100, 0));
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnCombinedColor, 0, SpringLayout.NORTH, btnSenseColor);
        sl_contentPane.putConstraint(SpringLayout.WEST, btnCombinedColor, 75, SpringLayout.EAST, btnAntiColor);
        contentPane.add(btnCombinedColor);
        
        JScrollPane scrollPane_BED = new JScrollPane();
        sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane_BED, 6, SpringLayout.SOUTH, btnLoadBedFile);
        sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane_BED, 10, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane_BED, -5, SpringLayout.NORTH, btnLoadBamFiles);
        sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane_BED, -10, SpringLayout.EAST, contentPane);
        contentPane.add(scrollPane_BED);
        
        bedList = new DefaultListModel();
		final JList listBed = new JList(bedList);
		listBed.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane_BED.setViewportView(listBed);
        
        JButton btnRemoveBed = new JButton("Remove BED");
        btnRemoveBed.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		while(listBed.getSelectedIndex() > -1) {
   					BEDFiles.remove(listBed.getSelectedIndex());
   					bedList.remove(listBed.getSelectedIndex());
   				}
        	}
        });
        sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBed, 0, SpringLayout.EAST, scrollPane_BAM);
        contentPane.add(btnRemoveBed);
        
        JSeparator sepOutput = new JSeparator();
        sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxOutputData, 10, SpringLayout.SOUTH, sepOutput);
        sl_contentPane.putConstraint(SpringLayout.NORTH, sepOutput, 10, SpringLayout.SOUTH, rdbtnGaussianSmooth);
        sl_contentPane.putConstraint(SpringLayout.WEST, sepOutput, 0, SpringLayout.WEST, scrollPane_BAM);
        sl_contentPane.putConstraint(SpringLayout.EAST, sepOutput, -10, SpringLayout.EAST, contentPane);
        sepOutput.setForeground(Color.BLACK);
        contentPane.add(sepOutput);
        
        JSeparator sepComposite = new JSeparator();
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblPleaseSelectComposite, 10, SpringLayout.SOUTH, sepComposite);
        sl_contentPane.putConstraint(SpringLayout.NORTH, sepComposite, 10, SpringLayout.SOUTH, chckbxTagStandard);
        sl_contentPane.putConstraint(SpringLayout.WEST, sepComposite, 10, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, sepComposite, -10, SpringLayout.EAST, contentPane);
        sepComposite.setForeground(Color.BLACK);
        contentPane.add(sepComposite);
        
        chckbxOutputCompositeData = new JCheckBox("Output for Composite");
        chckbxOutputCompositeData.setSelected(true);
        sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxOutputCompositeData, 10, SpringLayout.SOUTH, lblPleaseSelectOutput);
        sl_contentPane.putConstraint(SpringLayout.WEST, chckbxOutputCompositeData, 0, SpringLayout.WEST, scrollPane_BAM);
        contentPane.add(chckbxOutputCompositeData);
        
        txtCompositeName = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutputDirectory, 10, SpringLayout.SOUTH, txtCompositeName);
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtCompositeName, 2, SpringLayout.NORTH, chckbxOutputCompositeData);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtCompositeName, 10, SpringLayout.EAST, chckbxOutputCompositeData);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtCompositeName, 250, SpringLayout.EAST, chckbxOutputCompositeData);
        txtCompositeName.setText("composite_average.out");
        contentPane.add(txtCompositeName);
        txtCompositeName.setColumns(10);
        
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
			        	lblDefaultToLocal.setEnabled(true);
			        	lblCurrentOutput.setEnabled(true);
			        	lblPleaseSelectOutput.setEnabled(true);
						rdbtnTabdelimited.setEnabled(true);
						rdbtnCdt.setEnabled(true);

			        } else {
			        	lblPleaseSelectOutput.setEnabled(false);
						rdbtnTabdelimited.setEnabled(false);
						rdbtnCdt.setEnabled(false);
						if(!chckbxOutputCompositeData.isSelected()) {
							btnOutputDirectory.setEnabled(false);
							lblDefaultToLocal.setEnabled(false);
							lblCurrentOutput.setEnabled(false);
						}
			        }
			      }
			    });
        
        chckbxOutputCompositeData.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
			        if(chckbxOutputCompositeData.isSelected()) {
			        	txtCompositeName.setEnabled(true);
			        	btnOutputDirectory.setEnabled(true);
			        	lblDefaultToLocal.setEnabled(true);
			        	lblCurrentOutput.setEnabled(true);
			        } else {
			        	txtCompositeName.setEnabled(false);
			        	if(!chckbxOutputData.isSelected()) {
			        		btnOutputDirectory.setEnabled(false);
			        		lblDefaultToLocal.setEnabled(false);
				        	lblCurrentOutput.setEnabled(false);
			        	}
			        }
			      }
			    });
        
        btnLoadBedFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				File[] newBEDFiles = FileSelection.getBEDFiles(fc);
				if(newBEDFiles != null) {
					for(int x = 0; x < newBEDFiles.length; x++) { 
						BEDFiles.add(newBEDFiles[x]);
						bedList.addElement(newBEDFiles[x].getName());
					}
				}
			}
		});   
		
        btnOutputDirectory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				OUTPUT = FileSelection.getOutputDir(fc);
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
			if(rdbtnSeperate.isSelected()) {
				btnSenseColor.setEnabled(true);
    			btnAntiColor.setEnabled(true);
    			btnCombinedColor.setEnabled(false);
			} else {
				btnSenseColor.setEnabled(false);
	    		btnAntiColor.setEnabled(false);
	    		btnCombinedColor.setEnabled(true);
			}
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
			if(!chckbxOutputData.isSelected()) {
				lblPleaseSelectOutput.setEnabled(false);
				rdbtnTabdelimited.setEnabled(false);
				rdbtnCdt.setEnabled(false);
			}
			if(!chckbxOutputCompositeData.isSelected()) {
				txtCompositeName.setEnabled(false);
			}
			if(!chckbxOutputData.isSelected() && !chckbxOutputCompositeData.isSelected()) {
				btnOutputDirectory.setEnabled(false);
				lblCurrentOutput.setEnabled(false);
				lblDefaultToLocal.setEnabled(false);
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
}