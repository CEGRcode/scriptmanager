package window_interface.BAM_Format_Converter;

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
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

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

import scripts.BAM_Format_Converter.BAMtoTAB;
import util.FileSelection;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class BAMtoTABWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	
	final DefaultListModel expList;
	Vector<File> BAMFiles = new Vector<File>();
	private File OUTPUT = null;
	private int STRAND = 0;
	private String FivePrimeSeq = "";
	private String ThreePrimeSeq = "";
	
	private JButton btnIndex;
	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnOutputDirectory;
	private JRadioButton rdbtnRead1;
	private JRadioButton rdbtnRead2;
	private JRadioButton rdbtnCombined;
	
	private JCheckBox chckbx5FilterEnd;
	private JCheckBox chckbx3FilterEnd;
	private JTextField txt5Seq;
	private JTextField txt3Seq;
	
	JProgressBar progressBar;
	public Task task;

	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException, InterruptedException {
        	if(chckbx5FilterEnd.isSelected() && !parseStringforNuc(txt5Seq.getText())) { 
        			JOptionPane.showMessageDialog(null, "Invalid 5' Sequence!!! Must be A/T/G/C");
        	} else if(chckbx3FilterEnd.isSelected() && !parseStringforNuc(txt3Seq.getText())) {
        			JOptionPane.showMessageDialog(null, "Invalid 3' Sequence!!! Must be A/T/G/C");
        	} else {
	        	setProgress(0);
	        	if(rdbtnRead1.isSelected()) { STRAND = 0; }
	        	else if(rdbtnRead2.isSelected()) { STRAND = 1; }
	        	else if(rdbtnCombined.isSelected()) { STRAND = 2; }
	        	
	        	if(chckbx5FilterEnd.isSelected()) { FivePrimeSeq = txt5Seq.getText(); }
	        	if(chckbx3FilterEnd.isSelected()) { ThreePrimeSeq = txt3Seq.getText(); }
	        	
	        	for(int x = 0; x < BAMFiles.size(); x++) {
	        		BAMtoTAB convert = new BAMtoTAB(BAMFiles.get(x), OUTPUT, STRAND, FivePrimeSeq, ThreePrimeSeq);
	        		convert.setVisible(true);
					convert.run();
	        		int percentComplete = (int)(((double)(x + 1) / BAMFiles.size()) * 100);
	        		setProgress(percentComplete);
	        	}
	        	setProgress(100);
        	}
        	return null;
        }
        
        public void done() {
        	massXable(contentPane, true);
            setCursor(null); //turn off the wait cursor
        }
	}
	
	public BAMtoTABWindow() {
		setTitle("BAM to TAB Converter");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 650, 440);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
	
		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -182, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
		btnLoad = new JButton("Load BAM Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 11, SpringLayout.SOUTH, btnLoad);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 11, SpringLayout.WEST, contentPane);
		btnLoad.addActionListener(new ActionListener() {
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
		contentPane.add(btnLoad);
		
      	expList = new DefaultListModel();
		final JList listExp = new JList(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listExp);
		
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
		
		btnIndex = new JButton("Convert");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnIndex, 250, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnIndex, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnIndex, -250, SpringLayout.EAST, contentPane);
		contentPane.add(btnIndex);
		
		rdbtnRead1 = new JRadioButton("Read 1");
		contentPane.add(rdbtnRead1);
		
		rdbtnRead2 = new JRadioButton("Read 2");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnRead2, 0, SpringLayout.NORTH, rdbtnRead1);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnRead2, 95, SpringLayout.EAST, rdbtnRead1);
		contentPane.add(rdbtnRead2);
		
		rdbtnCombined = new JRadioButton("Combined");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnCombined, 0, SpringLayout.NORTH, rdbtnRead1);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnCombined, 150, SpringLayout.WEST, rdbtnRead2);
		rdbtnCombined.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
			        if(rdbtnCombined.isSelected()) { 
			        	chckbx5FilterEnd.setEnabled(false);
			        	chckbx3FilterEnd.setEnabled(false);
			        	txt5Seq.setEnabled(false);
			        	txt3Seq.setEnabled(false);
			        }
			        else { 
			        	chckbx5FilterEnd.setEnabled(true);
			        	chckbx3FilterEnd.setEnabled(true);
			        	if(chckbx5FilterEnd.isSelected()) { txt5Seq.setEnabled(true); }
			        	if(chckbx3FilterEnd.isSelected()) { txt3Seq.setEnabled(true); }
			        }
		      }
        }); 
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

        final JLabel lblDefaultToLocal = new JLabel("Default to Local Directory");
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnRead1, 0, SpringLayout.WEST, lblDefaultToLocal);
        lblDefaultToLocal.setFont(new Font("Dialog", Font.PLAIN, 12));
        sl_contentPane.putConstraint(SpringLayout.EAST, lblDefaultToLocal, -15, SpringLayout.EAST, contentPane);
        lblDefaultToLocal.setBackground(Color.WHITE);
        contentPane.add(lblDefaultToLocal);
        
        JLabel lblCurrentOutput = new JLabel("Current Output:");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrentOutput, 10, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, lblCurrentOutput);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, lblCurrentOutput);
        lblCurrentOutput.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblCurrentOutput, -35, SpringLayout.SOUTH, contentPane);
        contentPane.add(lblCurrentOutput);
		
        btnOutputDirectory = new JButton("Output Directory");
        sl_contentPane.putConstraint(SpringLayout.WEST, btnOutputDirectory, 250, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, btnOutputDirectory, -57, SpringLayout.SOUTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, btnOutputDirectory, -250, SpringLayout.EAST, contentPane);
        contentPane.add(btnOutputDirectory);
        
        progressBar = new JProgressBar();
        sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnIndex);
        sl_contentPane.putConstraint(SpringLayout.WEST, progressBar, 83, SpringLayout.EAST, btnIndex);
        sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -10, SpringLayout.EAST, contentPane);
        progressBar.setStringPainted(true);
        contentPane.add(progressBar);
        
        btnIndex.setActionCommand("start");
        
        chckbx5FilterEnd = new JCheckBox("Filter 5' End by Sequence:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, chckbx5FilterEnd, 17, SpringLayout.SOUTH, rdbtnRead1);
        sl_contentPane.putConstraint(SpringLayout.WEST, chckbx5FilterEnd, 0, SpringLayout.WEST, scrollPane);
        chckbx5FilterEnd.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
			        if(chckbx5FilterEnd.isSelected()) { txt5Seq.setEnabled(true); }
			        else { txt5Seq.setEnabled(false); }
		      }
        });     
        contentPane.add(chckbx5FilterEnd);
        
        txt5Seq = new JTextField();
        txt5Seq.setHorizontalAlignment(SwingConstants.CENTER);
        sl_contentPane.putConstraint(SpringLayout.NORTH, txt5Seq, 2, SpringLayout.NORTH, chckbx5FilterEnd);
        sl_contentPane.putConstraint(SpringLayout.WEST, txt5Seq, 6, SpringLayout.EAST, chckbx5FilterEnd);
        txt5Seq.setEnabled(false);
        contentPane.add(txt5Seq);
        txt5Seq.setColumns(10);
        
        chckbx3FilterEnd = new JCheckBox("Filter 3' End by Sequence:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, chckbx3FilterEnd, 0, SpringLayout.NORTH, chckbx5FilterEnd);
        sl_contentPane.putConstraint(SpringLayout.WEST, chckbx3FilterEnd, 8, SpringLayout.EAST, txt5Seq);
        chckbx3FilterEnd.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
			        if(chckbx3FilterEnd.isSelected()) { txt3Seq.setEnabled(true); }
			        else { txt3Seq.setEnabled(false); }
		      }
        });
        contentPane.add(chckbx3FilterEnd);
        
        txt3Seq = new JTextField();
        txt3Seq.setHorizontalAlignment(SwingConstants.CENTER);
        sl_contentPane.putConstraint(SpringLayout.NORTH, txt3Seq, 2, SpringLayout.NORTH, chckbx5FilterEnd);
        sl_contentPane.putConstraint(SpringLayout.WEST, txt3Seq, 6, SpringLayout.EAST, chckbx3FilterEnd);
        txt3Seq.setEnabled(false);
        contentPane.add(txt3Seq);
        txt3Seq.setColumns(10);
        btnIndex.addActionListener(this);
        
        btnOutputDirectory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				OUTPUT = FileSelection.getOutputDir(fc);
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
		if(status) {
        	if(chckbx5FilterEnd.isSelected()) { txt5Seq.setEnabled(true); }
        	else { txt5Seq.setEnabled(false); }
        	if(chckbx3FilterEnd.isSelected()) { txt3Seq.setEnabled(true); }
        	else { txt3Seq.setEnabled(false); }
		}
	}
	
	public boolean parseStringforNuc(String seq) {
		seq = seq.toUpperCase();
		char[] check = seq.toCharArray();
		for(int x = 0; x < check.length; x++) {
			if(check[x] != 'A' && check[x] != 'T' && check[x] != 'G' && check[x] != 'C') {
				return false;
			}
		}
		return true;
	}
}


	
