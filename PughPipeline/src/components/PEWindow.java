package components;

import filters.BAMFilter;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.SwingWorker;

import scripts.PEStats;

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


@SuppressWarnings("serial")
public class PEWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
		
	private JTextField txtOutputName;
	private JLabel lblOutputName;
	private JCheckBox chckbxOutputStatistics;
	JButton btnLoad;
	JButton btnRemoveBam;
	JButton btnRun;
	
	final DefaultListModel expList;
	Vector<File> BAMFiles = new Vector<File>();
	private JTextField txtMin;
	private JTextField txtMax;
	
	JProgressBar progressBar;
	public Task task;

	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() {
        	setProgress(0);
        	try {
				int min = Integer.parseInt(txtMin.getText());
				int max = Integer.parseInt(txtMax.getText());	
				PEStats stat;
				if(chckbxOutputStatistics.isSelected()) { stat = new PEStats(BAMFiles, new File(txtOutputName.getText()), min, max); }
				else { stat = new PEStats(BAMFiles, null, min, max); }
				
				stat.addPropertyChangeListener("bam", new PropertyChangeListener() {
				    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
				    	int temp = (Integer) propertyChangeEvent.getNewValue();
				    	int percentComplete = (int)(((double)(temp) / BAMFiles.size()) * 100);
			        	setProgress(percentComplete);
				     }
				 });
				
				stat.setVisible(true);				
				stat.run();
			} catch(NumberFormatException nfe){
				JOptionPane.showMessageDialog(null, "Input Fields Must Contain Integers");
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
	
	public PEWindow() {
		setTitle("Paired-End BAM File Statistics");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 370);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
	
		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -5, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
      	expList = new DefaultListModel();
		final JList listExp = new JList(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listExp);
		
		btnLoad = new JButton("Load BAM Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, btnLoad);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 0, SpringLayout.WEST, scrollPane);
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
		
		lblOutputName = new JLabel("Output File Name:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblOutputName, 0, SpringLayout.WEST, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblOutputName, -49, SpringLayout.SOUTH, contentPane);
		contentPane.add(lblOutputName);
		
		chckbxOutputStatistics = new JCheckBox("Output Statistics");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -51, SpringLayout.NORTH, chckbxOutputStatistics);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxOutputStatistics, 0, SpringLayout.WEST, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, chckbxOutputStatistics, -71, SpringLayout.SOUTH, contentPane);
		chckbxOutputStatistics.setSelected(true);
		contentPane.add(chckbxOutputStatistics);
		
		btnRemoveBam = new JButton("Remove BAM");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveBam, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 0, SpringLayout.NORTH, btnRemoveBam);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBam, 0, SpringLayout.EAST, scrollPane);
		btnRemoveBam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(listExp.getSelectedIndex() > -1) {
					BAMFiles.remove(listExp.getSelectedIndex());
					expList.remove(listExp.getSelectedIndex());
				}
			}
		});		
		contentPane.add(btnRemoveBam);
		
		btnRun = new JButton("Run");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnRun, 171, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnRun, -10, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRun, -171, SpringLayout.EAST, contentPane);
		contentPane.add(btnRun);
		
		txtOutputName = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.WEST, txtOutputName, 120, SpringLayout.WEST, lblOutputName);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, txtOutputName, -43, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtOutputName, 0, SpringLayout.EAST, scrollPane);
		txtOutputName.setText("output_name.txt");
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtOutputName, -6, SpringLayout.NORTH, lblOutputName);
		contentPane.add(txtOutputName);
		txtOutputName.setColumns(10);
		
		JLabel lblHistogramRange = new JLabel("Histogram Range:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblHistogramRange, 6, SpringLayout.SOUTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblHistogramRange, 0, SpringLayout.WEST, scrollPane);
		contentPane.add(lblHistogramRange);
		
		JLabel lblMin = new JLabel("Min:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblMin, 6, SpringLayout.SOUTH, lblHistogramRange);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblMin, 0, SpringLayout.WEST, scrollPane);
		contentPane.add(lblMin);
		
		txtMin = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtMin, -6, SpringLayout.NORTH, lblMin);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtMin, 38, SpringLayout.EAST, lblMin);
		txtMin.setText("0");
		contentPane.add(txtMin);
		txtMin.setColumns(10);
		
		JLabel lblMax = new JLabel("Max:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblMax, 0, SpringLayout.NORTH, lblMin);
		contentPane.add(lblMax);
		
		txtMax = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.WEST, txtMax, 296, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblMax, -6, SpringLayout.WEST, txtMax);
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtMax, -6, SpringLayout.NORTH, lblMin);
		txtMax.setText("1000");
		contentPane.add(txtMax);
		txtMax.setColumns(10);
		
		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.SOUTH, progressBar, -5, SpringLayout.SOUTH, btnRun);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -6, SpringLayout.EAST, contentPane);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
		chckbxOutputStatistics.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		        if(chckbxOutputStatistics.isSelected()) {
		        	txtOutputName.setEnabled(true);
		        	lblOutputName.setEnabled(true);
		        } else {
		        	txtOutputName.setEnabled(false);
		        	lblOutputName.setEnabled(false);		        }
		      }
		    });
		
		btnRun.setActionCommand("start");
		btnRun.addActionListener(this);
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


	
