package components;

import filters.BAMFilter;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.SwingWorker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import scripts.BAMtoMidpoint;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class BAMtoMidpointWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	
	final DefaultListModel expList;
	Vector<File> BAMFiles = new Vector<File>();
	private File OUTPUT = null;
	
	private JButton btnIndex;
	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnOutputDirectory;
	
	JProgressBar progressBar;
	public Task task;
	private JTextField txtMin;
	private JTextField txtMax;

	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException, InterruptedException {
        	setProgress(0);
        	try {
	        	if(Integer.parseInt(txtMin.getText()) < 0) {
	    			JOptionPane.showMessageDialog(null, "Invalid Minimum Size!!! Must be larger than 0 bp");
	    		} else if(Integer.parseInt(txtMax.getText()) < 1) {
	    			JOptionPane.showMessageDialog(null, "Invalid Maximum Size!!! Must be larger than 0 bp");
	    		} else if(Integer.parseInt(txtMax.getText()) < Integer.parseInt(txtMin.getText())) {
	    			JOptionPane.showMessageDialog(null, "Invalid Maximum and Minimum Size!!! Maximum must be larger than Minimum");
	    		} else {
	    			int MIN = Integer.parseInt(txtMin.getText());
	    			int MAX = Integer.parseInt(txtMax.getText());
		        	for(int x = 0; x < BAMFiles.size(); x++) {
		        		BAMtoMidpoint convert = new BAMtoMidpoint(BAMFiles.get(x), OUTPUT, MIN, MAX);
		        		convert.setVisible(true);
						convert.run();
		        		int percentComplete = (int)(((double)(x + 1) / BAMFiles.size()) * 100);
		        		setProgress(percentComplete);
		        	}
	    		}
	    	} catch(NumberFormatException nfe){
				JOptionPane.showMessageDialog(null, "Invalid Input in Fields!!!");
			}
        	setProgress(100);
        	return null;
        }
        
        public void done() {
    		massXable(contentPane, true);
            setCursor(null); //turn off the wait cursor
        }
	}
	
	public BAMtoMidpointWindow() {
		setTitle("BAM to Midpoint Converter");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 600, 410);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
	
		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 41, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 15, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
		btnLoad = new JButton("Load BAM Files");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 0, SpringLayout.WEST, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnLoad, -6, SpringLayout.NORTH, scrollPane);
		btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				fc.setFileFilter(new BAMFilter());
				fc.setMultiSelectionEnabled(true);
				fc.setDialogTitle("BAM File Selection");

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
		
      	expList = new DefaultListModel();
		final JList listExp = new JList(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listExp);
		
		btnRemoveBam = new JButton("Remove BAM");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnRemoveBam, -6, SpringLayout.NORTH, scrollPane);
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
		
		btnIndex = new JButton("Convert");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnIndex, 250, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnIndex, -10, SpringLayout.SOUTH, contentPane);
		contentPane.add(btnIndex);
		
        final JLabel lblDefaultToLocal = new JLabel("Default to Local Directory");
        lblDefaultToLocal.setFont(new Font("Dialog", Font.PLAIN, 12));
        sl_contentPane.putConstraint(SpringLayout.EAST, lblDefaultToLocal, -15, SpringLayout.EAST, contentPane);
        lblDefaultToLocal.setBackground(Color.WHITE);
        contentPane.add(lblDefaultToLocal);
        
        JLabel lblCurrentOutput = new JLabel("Current Output:");
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblCurrentOutput, -43, SpringLayout.SOUTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, lblCurrentOutput);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, lblCurrentOutput);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrentOutput, 0, SpringLayout.WEST, scrollPane);
        lblCurrentOutput.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblCurrentOutput);
		
        btnOutputDirectory = new JButton("Output Directory");
        sl_contentPane.putConstraint(SpringLayout.WEST, btnOutputDirectory, -13, SpringLayout.WEST, btnIndex);
        sl_contentPane.putConstraint(SpringLayout.EAST, btnOutputDirectory, -193, SpringLayout.EAST, contentPane);
        contentPane.add(btnOutputDirectory);
        
        progressBar = new JProgressBar();
        sl_contentPane.putConstraint(SpringLayout.EAST, btnIndex, -30, SpringLayout.WEST, progressBar);
        sl_contentPane.putConstraint(SpringLayout.WEST, progressBar, 415, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -15, SpringLayout.EAST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, progressBar, 0, SpringLayout.SOUTH, btnIndex);
        progressBar.setStringPainted(true);
        contentPane.add(progressBar);
        
        btnIndex.setActionCommand("start");
        
        txtMin = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutputDirectory, 6, SpringLayout.SOUTH, txtMin);
        txtMin.setHorizontalAlignment(SwingConstants.CENTER);
        txtMin.setText("0");
        contentPane.add(txtMin);
        txtMin.setColumns(10);
        
        txtMax = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtMax, 31, SpringLayout.SOUTH, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtMax, 433, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtMax, -83, SpringLayout.EAST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtMin, 0, SpringLayout.NORTH, txtMax);
        txtMax.setHorizontalAlignment(SwingConstants.CENTER);
        txtMax.setText("1000");
        contentPane.add(txtMax);
        txtMax.setColumns(10);
        
        JLabel lblMaxSizebp = new JLabel("Max Size (bp):");
        sl_contentPane.putConstraint(SpringLayout.EAST, txtMin, -76, SpringLayout.WEST, lblMaxSizebp);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblMaxSizebp, 1, SpringLayout.NORTH, txtMin);
        sl_contentPane.putConstraint(SpringLayout.EAST, lblMaxSizebp, -17, SpringLayout.WEST, txtMax);
        lblMaxSizebp.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblMaxSizebp);
        
        JLabel lblMinSizebp = new JLabel("Min Size (bp):");
        sl_contentPane.putConstraint(SpringLayout.EAST, lblMinSizebp, -431, SpringLayout.EAST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtMin, 13, SpringLayout.EAST, lblMinSizebp);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblMinSizebp, 1, SpringLayout.NORTH, txtMin);
        lblMinSizebp.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblMinSizebp);
        
        JLabel lblEnterInsertSize = new JLabel("Please Enter Insert Size Range to Consider:");
        sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -6, SpringLayout.NORTH, lblEnterInsertSize);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblEnterInsertSize, 230, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblEnterInsertSize, 0, SpringLayout.WEST, scrollPane);
        lblEnterInsertSize.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblEnterInsertSize);
        btnIndex.addActionListener(this);
        
        btnOutputDirectory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				OUTPUT = getOutputDir();
				if(OUTPUT != null) {
					lblDefaultToLocal.setText(OUTPUT.getAbsolutePath());
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
    
	public File[] getCoordFile() {
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


	
