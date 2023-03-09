package scriptmanager.window_interface.BAM_Statistics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import scriptmanager.util.FileSelection;

@SuppressWarnings("serial")
public class BAMGenomeCorrelationWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	private JCheckBox chckbxOutputStatistics;
	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnOutputDirectory;
	private JButton btnCorrelate;
	private JTextField txtShift;
	private JTextField txtBin;
	private JTextField txtCPU;
	private JLabel lblCurrentOutput;
	private JLabel lblDefaultToLocal;
	
	private JRadioButton rdbtnRead1;
	private JRadioButton rdbtnRead2;
	private JRadioButton rdbtnAllReads;	
	private JRadioButton rdbtnMidpoint;

	final DefaultListModel<String> expList;
	Vector<File> BAMFiles = new Vector<File>();
	private File OUTPUT_PATH = null;
	
	private int SHIFT = 0;
	private int BIN = 10;
	private int CPU = 1;
	
	JProgressBar progressBar;
	public Task task;

	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() {
        	setProgress(0);
        	try {
        		if(Integer.parseInt(txtBin.getText()) < 1) {
        			JOptionPane.showMessageDialog(null, "Invalid Bin Size!!! Must be greater than 0 bp");
        		} else if(Integer.parseInt(txtCPU.getText()) < 1) {
        			JOptionPane.showMessageDialog(null, "Invalid CPU's!!! Must be greater than 0");
        		} else {
        			SHIFT = Integer.parseInt(txtShift.getText());
        			BIN = Integer.parseInt(txtBin.getText());
        			CPU = Integer.parseInt(txtCPU.getText());
        			
        			int READ = 0;
        			if(rdbtnRead1.isSelected()) { READ = 0; }
		        	else if(rdbtnRead2.isSelected()) { READ = 1; }
		        	else if(rdbtnAllReads.isSelected()) { READ = 2; }
		        	else if(rdbtnMidpoint.isSelected()) { READ = 3; }
        			
        			BAMGenomeCorrelationOutput corr = new BAMGenomeCorrelationOutput(BAMFiles, OUTPUT_PATH, chckbxOutputStatistics.isSelected(), SHIFT, BIN, CPU, READ);
        			corr.addPropertyChangeListener("bam", new PropertyChangeListener() {
					    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
					    	int temp = (Integer) propertyChangeEvent.getNewValue();
					    	int percentComplete = (int) (((double)(temp) / (((BAMFiles.size() * BAMFiles.size()) - BAMFiles.size()) / 2)) * 100);
				        	setProgress(percentComplete);
					     }
					 });
        			corr.run();
        		}
			} catch(NumberFormatException nfe){
				JOptionPane.showMessageDialog(null, "Input Fields Must Contain Integers");
			} catch (IOException e) {
				e.printStackTrace();
			}
        	setProgress(100);
        	return null;
        }
        
        public void done() {
    		massXable(contentPane, true);
            setCursor(null); //turn off the wait cursor
        }
	}
	
	public BAMGenomeCorrelationWindow() {
		setTitle("BAM Genome Correlation");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 450);
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
		
		btnLoad = new JButton("Load BAM Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, btnLoad);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 5, SpringLayout.WEST, contentPane);
		btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				File[] newBAMFiles = FileSelection.getFiles(fc,"bam");
				if(newBAMFiles != null) {
					for(int x = 0; x < newBAMFiles.length; x++) { 
						BAMFiles.add(newBAMFiles[x]);
						expList.addElement(newBAMFiles[x].getName());
					}
				}
			}
		});
		contentPane.add(btnLoad);
		
		chckbxOutputStatistics = new JCheckBox("Output Statistics");
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxOutputStatistics, 0, SpringLayout.WEST, scrollPane);
		contentPane.add(chckbxOutputStatistics);
		
		btnRemoveBam = new JButton("Remove BAM");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveBam, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBam, -5, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 0, SpringLayout.NORTH, btnRemoveBam);
		btnRemoveBam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(listExp.getSelectedIndex() > -1) {
					BAMFiles.remove(listExp.getSelectedIndex());
					expList.remove(listExp.getSelectedIndex());
				}
			}
		});		
		contentPane.add(btnRemoveBam);
		
		btnCorrelate = new JButton("Correlate");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnCorrelate, 171, SpringLayout.WEST, contentPane);
		contentPane.add(btnCorrelate);
		
		JLabel lblHistogramRange = new JLabel("Genome Correlation Parameters:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblHistogramRange, 0, SpringLayout.WEST, scrollPane);
		contentPane.add(lblHistogramRange);
		
		JLabel lblShift = new JLabel("Tag Shift (bp):");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblShift, 10, SpringLayout.SOUTH, lblHistogramRange);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblShift, 0, SpringLayout.WEST, scrollPane);
		contentPane.add(lblShift);
		
		txtShift = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtShift, -2, SpringLayout.NORTH, lblShift);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtShift, 8, SpringLayout.EAST, lblShift);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtShift, 80, SpringLayout.EAST, lblShift);
		txtShift.setHorizontalAlignment(SwingConstants.CENTER);
		txtShift.setText("0");
		contentPane.add(txtShift);
		txtShift.setColumns(10);
		
		JLabel lblBin = new JLabel("Bin Size (bp):");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblBin, 0, SpringLayout.NORTH, lblShift);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblBin, 40, SpringLayout.EAST, txtShift);
		contentPane.add(lblBin);
		
		txtBin = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtBin, -2, SpringLayout.NORTH, lblShift);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtBin, 8, SpringLayout.EAST, lblBin);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtBin, -68, SpringLayout.EAST, contentPane);
		txtBin.setHorizontalAlignment(SwingConstants.CENTER);
		txtBin.setText("10");
		contentPane.add(txtBin);
		txtBin.setColumns(10);
		
		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -5, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnCorrelate, -3, SpringLayout.NORTH, progressBar);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnCorrelate, -18, SpringLayout.WEST, progressBar);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, progressBar, -10, SpringLayout.SOUTH, contentPane);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
		chckbxOutputStatistics.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		        if(chckbxOutputStatistics.isSelected()) {
		        	btnOutputDirectory.setEnabled(true);
		        	lblCurrentOutput.setEnabled(true);
		        	lblDefaultToLocal.setEnabled(true);
		        } else {
		        	btnOutputDirectory.setEnabled(false);
		        	lblCurrentOutput.setEnabled(false);	
		        	lblDefaultToLocal.setEnabled(false);
		        }
		      }
		    });
		
		btnCorrelate.setActionCommand("start");
		
		btnOutputDirectory = new JButton("Output Directory");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutputDirectory, 35, SpringLayout.SOUTH, txtShift);
		btnOutputDirectory.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
    			OUTPUT_PATH = FileSelection.getOutputDir(fc);
    			if(OUTPUT_PATH != null) {
    				lblDefaultToLocal.setText(OUTPUT_PATH.getAbsolutePath());
    			}
        	}
        });
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxOutputStatistics, 1, SpringLayout.NORTH, btnOutputDirectory);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnOutputDirectory, 150, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnOutputDirectory, -150, SpringLayout.EAST, contentPane);
		contentPane.add(btnOutputDirectory);
		btnOutputDirectory.setEnabled(false);

		lblCurrentOutput = new JLabel("Current Output:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrentOutput, 0, SpringLayout.WEST, scrollPane);
		lblCurrentOutput.setEnabled(false);
		lblCurrentOutput.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblCurrentOutput);
		
		lblDefaultToLocal = new JLabel("Default to Local Directory");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 10, SpringLayout.SOUTH, btnOutputDirectory);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 11, SpringLayout.EAST, lblCurrentOutput);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrentOutput, -1, SpringLayout.NORTH, lblDefaultToLocal);
		lblDefaultToLocal.setEnabled(false);
		lblDefaultToLocal.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblDefaultToLocal.setBackground(Color.WHITE);
		contentPane.add(lblDefaultToLocal);
		
		rdbtnRead1 = new JRadioButton("Read 1");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblHistogramRange, 6, SpringLayout.SOUTH, rdbtnRead1);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnRead1, 0, SpringLayout.WEST, scrollPane);
		contentPane.add(rdbtnRead1);
		
		rdbtnRead2 = new JRadioButton("Read 2");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnRead2, 0, SpringLayout.NORTH, rdbtnRead1);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnRead2, 10, SpringLayout.EAST, rdbtnRead1);
		contentPane.add(rdbtnRead2);
		
		rdbtnAllReads = new JRadioButton("All Reads");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnAllReads, 0, SpringLayout.NORTH, rdbtnRead1);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnAllReads, 10, SpringLayout.EAST, rdbtnRead2);
		contentPane.add(rdbtnAllReads);
		
		rdbtnMidpoint = new JRadioButton("Midpoint (Requires PE)");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnMidpoint, 0, SpringLayout.NORTH, rdbtnRead1);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnMidpoint, 10, SpringLayout.EAST, rdbtnAllReads);
		contentPane.add(rdbtnMidpoint);
		
		ButtonGroup CorrelateRead = new ButtonGroup();
		CorrelateRead.add(rdbtnRead1);
		CorrelateRead.add(rdbtnRead2);
		CorrelateRead.add(rdbtnAllReads);
		CorrelateRead.add(rdbtnMidpoint);
		rdbtnRead1.setSelected(true);
		
		JLabel lblPleaseSelectWhich = new JLabel("Please Select Which Read to Correlate:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblPleaseSelectWhich, 197, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -6, SpringLayout.NORTH, lblPleaseSelectWhich);
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnRead1, 6, SpringLayout.SOUTH, lblPleaseSelectWhich);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblPleaseSelectWhich, 0, SpringLayout.WEST, scrollPane);
		contentPane.add(lblPleaseSelectWhich);
		
		JLabel lblCpu = new JLabel("CPU's to Use:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCpu, 12, SpringLayout.SOUTH, lblShift);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCpu, 0, SpringLayout.WEST, scrollPane);
		contentPane.add(lblCpu);
		
		txtCPU = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtCPU, -2, SpringLayout.NORTH, lblCpu);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtCPU, 0, SpringLayout.WEST, txtShift);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtCPU, 0, SpringLayout.EAST, txtShift);
		txtCPU.setHorizontalAlignment(SwingConstants.CENTER);
		txtCPU.setText("1");
		contentPane.add(txtCPU);
		txtCPU.setColumns(10);
		
		btnCorrelate.addActionListener(this);
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
			if(!chckbxOutputStatistics.isSelected()) {
				btnOutputDirectory.setEnabled(false);
				lblCurrentOutput.setEnabled(false);
				lblDefaultToLocal.setEnabled(false);
			}
		}
	}
}


	
