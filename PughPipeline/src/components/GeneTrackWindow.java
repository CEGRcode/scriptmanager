package components;

import filters.BAMFilter;

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
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.SwingWorker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JLabel;

import scripts.GeneTrack;

import javax.swing.JCheckBox;
import javax.swing.JRadioButton;

@SuppressWarnings("serial")
public class GeneTrackWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	
	final DefaultListModel expList;
	Vector<File> BAMFiles = new Vector<File>();
	
	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnGene;

	private JProgressBar progressBar;
	public Task task;
	private JTextField txtSigma;
	private JTextField txtExclusion;
	private JTextField txtUp;
	private JTextField txtDown;
	private JTextField txtFilter;
	private JLabel lblUpWidth;
	private JLabel lblDownWidth;
	private JLabel lblMinimumTagsPer;
	private JCheckBox chckbxPeakWidth;
	
	private JRadioButton rdbtnRead1;
	private JRadioButton rdbtnRead2;
	private JRadioButton rdbtnCombined;
	
	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException {
        	try {
				if(Integer.parseInt(txtSigma.getText()) < 1) {
					JOptionPane.showMessageDialog(null, "Invalid Sigma!!!");
				} else if(Integer.parseInt(txtExclusion.getText()) < 1) {
					JOptionPane.showMessageDialog(null, "Invalid Exclusion!!!");
				} else if(chckbxPeakWidth.isSelected() && Integer.parseInt(txtUp.getText()) < 1) {
					JOptionPane.showMessageDialog(null, "Invalid Upstream Window Width!!!");
				} else if(chckbxPeakWidth.isSelected() && Integer.parseInt(txtDown.getText()) < 1) {
					JOptionPane.showMessageDialog(null, "Invalid Downstream Window Width!!!");
				} else if(Integer.parseInt(txtFilter.getText()) < 0) {
					JOptionPane.showMessageDialog(null, "Invalid Peak Filtering Criteria!!!");
				} else {
					int SIGMA = Integer.parseInt(txtSigma.getText());
		        	int EXCLUSION = Integer.parseInt(txtExclusion.getText());
		        	int FILTER = Integer.parseInt(txtFilter.getText());
		        	int UP = EXCLUSION / 2;
	        		int DOWN = EXCLUSION / 2;
		        	if(chckbxPeakWidth.isSelected()) {
		        		UP = Integer.parseInt(txtUp.getText());
		        		DOWN = Integer.parseInt(txtDown.getText());
		        	}
		        	
		        	int READ = 0;
		        	if(rdbtnRead1.isSelected()) READ = 0;
		        	else if(rdbtnRead2.isSelected()) READ = 1;
		        	else if(rdbtnCombined.isSelected()) READ = 2;
		        	
		        	setProgress(0);
		        	for(int x = 0; x < BAMFiles.size(); x++) {       		
		        		GeneTrack track = new GeneTrack(BAMFiles.get(x), READ, SIGMA, EXCLUSION, UP, DOWN, FILTER);
		        		track.setVisible(true);
						track.run();
						int percentComplete = (int)(((double)(x + 1) / BAMFiles.size()) * 100);
		        		setProgress(percentComplete);
		        	}
		        	setProgress(100);
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
	
	public GeneTrackWindow() {
		setTitle("GeneTrack");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 500, 360);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
	
		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -143, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
      	expList = new DefaultListModel();
		final JList listExp = new JList(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listExp);
		
		btnLoad = new JButton("Load BAM Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 10, SpringLayout.SOUTH, btnLoad);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 10, SpringLayout.WEST, contentPane);
		btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				fc.setFileFilter(new BAMFilter());
				fc.setMultiSelectionEnabled(true);
				File[] newBAMFiles = getCoordFile();
				if(newBAMFiles != null) {
					for(int x = 0; x < newBAMFiles.length; x++) { 
						BAMFiles.add(newBAMFiles[x]);
						expList.addElement(newBAMFiles[x].getName());
					}
				}
			}
		});
		contentPane.add(btnLoad);
		
		btnRemoveBam = new JButton("Remove BAM");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveBam, 0, SpringLayout.NORTH, btnLoad);
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
		
		btnGene = new JButton("GeneTrack");
		sl_contentPane.putConstraint(SpringLayout.EAST, btnGene, -172, SpringLayout.EAST, contentPane);
		contentPane.add(btnGene);
		
		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.SOUTH, progressBar, -5, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnGene, 0, SpringLayout.SOUTH, progressBar);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -10, SpringLayout.EAST, contentPane);
        progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
        btnGene.setActionCommand("start");
        
        txtSigma = new JTextField();
        txtSigma.setHorizontalAlignment(SwingConstants.CENTER);
        txtSigma.setText("5");
        contentPane.add(txtSigma);
        txtSigma.setColumns(10);
        
        txtExclusion = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtExclusion, 0, SpringLayout.NORTH, txtSigma);
        txtExclusion.setHorizontalAlignment(SwingConstants.CENTER);
        txtExclusion.setText("20");
        contentPane.add(txtExclusion);
        txtExclusion.setColumns(10);
        
        txtUp = new JTextField();
        txtUp.setEnabled(false);
        txtUp.setHorizontalAlignment(SwingConstants.CENTER);
        txtUp.setText("10");
        contentPane.add(txtUp);
        txtUp.setColumns(10);
        
        txtDown = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtDown, 0, SpringLayout.NORTH, txtUp);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtDown, -22, SpringLayout.EAST, contentPane);
        txtDown.setEnabled(false);
        txtDown.setHorizontalAlignment(SwingConstants.CENTER);
        txtDown.setText("10");
        contentPane.add(txtDown);
        txtDown.setColumns(10);
        
        txtFilter = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.EAST, txtFilter, -22, SpringLayout.EAST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtDown, 0, SpringLayout.WEST, txtFilter);
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtFilter, 0, SpringLayout.NORTH, txtSigma);
        txtFilter.setHorizontalAlignment(SwingConstants.CENTER);
        txtFilter.setText("1");
        contentPane.add(txtFilter);
        txtFilter.setColumns(10);
        
        JLabel lblSigma = new JLabel("Sigma:");
        sl_contentPane.putConstraint(SpringLayout.WEST, txtSigma, 6, SpringLayout.EAST, lblSigma);
        sl_contentPane.putConstraint(SpringLayout.EAST, lblSigma, -424, SpringLayout.EAST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtSigma, -2, SpringLayout.NORTH, lblSigma);
        lblSigma.setToolTipText("Sigma to use when smoothing reads to call peaks");
        contentPane.add(lblSigma);
        
        JLabel lblExclusion = new JLabel("Exclusion Zone:");
        sl_contentPane.putConstraint(SpringLayout.EAST, lblExclusion, -259, SpringLayout.EAST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtExclusion, 5, SpringLayout.EAST, lblExclusion);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtSigma, -15, SpringLayout.WEST, lblExclusion);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblExclusion, 2, SpringLayout.NORTH, txtSigma);
        lblExclusion.setToolTipText("Exclusion zone around each peak that prevents others from being called");
        contentPane.add(lblExclusion);
        
        lblUpWidth = new JLabel("Up Width:");
        sl_contentPane.putConstraint(SpringLayout.WEST, btnGene, 0, SpringLayout.WEST, lblUpWidth);
        sl_contentPane.putConstraint(SpringLayout.EAST, lblUpWidth, -260, SpringLayout.EAST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtUp, 6, SpringLayout.EAST, lblUpWidth);
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtUp, -2, SpringLayout.NORTH, lblUpWidth);
        lblUpWidth.setToolTipText("Upstream width of called peaks (Default uses half exclusion)");
        lblUpWidth.setForeground(Color.GRAY);
        contentPane.add(lblUpWidth);
        
        lblDownWidth = new JLabel("Down Width:");
        sl_contentPane.putConstraint(SpringLayout.EAST, lblDownWidth, -6, SpringLayout.WEST, txtDown);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtUp, -43, SpringLayout.WEST, lblDownWidth);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblDownWidth, 2, SpringLayout.NORTH, txtUp);
        lblDownWidth.setToolTipText("Downstream width of called peaks (Default uses half exclusion)");
        lblDownWidth.setForeground(Color.GRAY);
        contentPane.add(lblDownWidth);
        
        lblMinimumTagsPer = new JLabel("Min Tags per Peak:");
        sl_contentPane.putConstraint(SpringLayout.EAST, lblMinimumTagsPer, -79, SpringLayout.EAST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtFilter, 6, SpringLayout.EAST, lblMinimumTagsPer);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtExclusion, -6, SpringLayout.WEST, lblMinimumTagsPer);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblMinimumTagsPer, 2, SpringLayout.NORTH, txtSigma);
        lblMinimumTagsPer.setToolTipText("Absolute read filter; outputs only peaks with larger read count");
        contentPane.add(lblMinimumTagsPer);
        
        chckbxPeakWidth = new JCheckBox("Set Peak Width");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblUpWidth, 4, SpringLayout.NORTH, chckbxPeakWidth);
        sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxPeakWidth, 6, SpringLayout.SOUTH, txtSigma);
        sl_contentPane.putConstraint(SpringLayout.WEST, chckbxPeakWidth, 0, SpringLayout.WEST, scrollPane);
        chckbxPeakWidth.setToolTipText("Default Peak Width is Half the Exclusion Zone");
        contentPane.add(chckbxPeakWidth);
        
        JLabel lblPleaseSelectRead = new JLabel("Please Select Read to Analyze:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblPleaseSelectRead, 6, SpringLayout.SOUTH, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblPleaseSelectRead, 0, SpringLayout.WEST, scrollPane);
        contentPane.add(lblPleaseSelectRead);
        
        rdbtnRead1 = new JRadioButton("Read 1");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblSigma, 6, SpringLayout.SOUTH, rdbtnRead1);
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnRead1, 6, SpringLayout.SOUTH, lblPleaseSelectRead);
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnRead1, 66, SpringLayout.WEST, contentPane);
        contentPane.add(rdbtnRead1);
        
        rdbtnRead2 = new JRadioButton("Read 2");
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnRead2, 66, SpringLayout.EAST, rdbtnRead1);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, rdbtnRead2, -4, SpringLayout.NORTH, txtExclusion);
        contentPane.add(rdbtnRead2);
        
        rdbtnCombined = new JRadioButton("Combined");
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnCombined, 66, SpringLayout.EAST, rdbtnRead2);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, rdbtnCombined, -4, SpringLayout.NORTH, txtFilter);
        contentPane.add(rdbtnCombined);
        
		ButtonGroup OutputRead = new ButtonGroup();
        OutputRead.add(rdbtnRead1);
        OutputRead.add(rdbtnRead2);
        OutputRead.add(rdbtnCombined);
        rdbtnRead1.setSelected(true);
        
        btnGene.addActionListener(this);
        chckbxPeakWidth.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		        if(chckbxPeakWidth.isSelected()) {
		        	txtUp.setEnabled(true);
		        	txtDown.setEnabled(true);
		        	lblUpWidth.setEnabled(true);
		        	lblDownWidth.setEnabled(true);

		        } else {
		        	txtUp.setEnabled(false);
		        	txtDown.setEnabled(false);
		        	lblUpWidth.setEnabled(false);
		        	lblDownWidth.setEnabled(false);
		        }
		      }
		    }); 
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
    
	public File[] getCoordFile(){
		File[] bamFiles = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			bamFiles = fc.getSelectedFiles();
		}
		return bamFiles;
	}
}


	
