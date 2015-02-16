package window_interface.Data_Analysis;

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
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.SwingWorker;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import scripts.SignalDuplication;
import util.ExtensionFileFilter;


@SuppressWarnings("serial")
public class SignalDuplicationWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	
	final DefaultListModel expList;
	Vector<File> BAMFiles = new Vector<File>();
	private File INPUT = null;
	
	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnCalculate;
	private JLabel lblGFFFile;

	private JProgressBar progressBar;
	public Task task;
	private JTextField txtWindow;
	
	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException {
        	try {
        		if(Double.parseDouble(txtWindow.getText()) < 1) {
        			JOptionPane.showMessageDialog(null, "Invalid Bin Size!!! Must be larger than 0 bp");
        		} else if(INPUT == null) {
        			JOptionPane.showMessageDialog(null, "GFF File Not Loaded!!!");
        		} else if(BAMFiles.size() < 1) {
        			JOptionPane.showMessageDialog(null, "No BAM Files Loaded!!!");
        		} else {
        			setProgress(0);
        			SignalDuplication signal = new SignalDuplication(INPUT, BAMFiles, Double.parseDouble(txtWindow.getText()));
        				
        			signal.addPropertyChangeListener("tag", new PropertyChangeListener() {
					    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
					    	int temp = (Integer) propertyChangeEvent.getNewValue();
					    	int percentComplete = (int)(((double)(temp) / BAMFiles.size()) * 100);
				        	setProgress(percentComplete);
					     }
					 });
	        		
        			signal.setVisible(true);
        			signal.run();
					
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
	
	public SignalDuplicationWindow() {
		setTitle("Signal Duplication");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 293);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
	
		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
      	expList = new DefaultListModel();
		final JList listExp = new JList(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listExp);
		
		btnLoad = new JButton("Load BAM Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 9, SpringLayout.SOUTH, btnLoad);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, btnLoad);
		btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
		
		btnCalculate = new JButton("Calculate");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -6, SpringLayout.NORTH, btnCalculate);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnCalculate, 166, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnCalculate, -10, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnCalculate, -176, SpringLayout.EAST, contentPane);
		contentPane.add(btnCalculate);
		
		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnCalculate);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -10, SpringLayout.EAST, contentPane);
        progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
        btnCalculate.setActionCommand("start");
        
        JButton btnLoadGFF = new JButton("Load GFF");
        sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 0, SpringLayout.WEST, btnLoadGFF);
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoadGFF, 0, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, btnLoadGFF, 5, SpringLayout.WEST, contentPane);
        contentPane.add(btnLoadGFF);
        
        lblGFFFile = new JLabel("No GFF File Loaded");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblGFFFile, 5, SpringLayout.NORTH, btnLoadGFF);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblGFFFile, 6, SpringLayout.EAST, btnLoadGFF);
        sl_contentPane.putConstraint(SpringLayout.EAST, lblGFFFile, -10, SpringLayout.EAST, contentPane);
        contentPane.add(lblGFFFile);

        btnLoadGFF.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		File temp = getGFFFile();
				if(temp != null) {
					INPUT = temp;
					lblGFFFile.setText(INPUT.getName());
				}
        	}
        });
        
        txtWindow = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.EAST, txtWindow, -105, SpringLayout.EAST, contentPane);
        txtWindow.setHorizontalAlignment(SwingConstants.CENTER);
        txtWindow.setText("100");
        contentPane.add(txtWindow);
        txtWindow.setColumns(10);
        
        JLabel lblSizeOfSignal = new JLabel("Size of Signal Window around Center (bp):");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblSizeOfSignal, 5, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 6, SpringLayout.SOUTH, lblSizeOfSignal);
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtWindow, -2, SpringLayout.NORTH, lblSizeOfSignal);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtWindow, 6, SpringLayout.EAST, lblSizeOfSignal);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblSizeOfSignal, 6, SpringLayout.SOUTH, btnLoadGFF);
        contentPane.add(lblSizeOfSignal);
        btnCalculate.addActionListener(this);
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
    
	public File getGFFFile() {
		fc.setFileFilter(new ExtensionFileFilter("gff"));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setSelectedFile(new File(""));
		fc.setDialogTitle("GFF File Selection");
		File gffFile = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			gffFile = fc.getSelectedFile();
		}
		return gffFile;
	}
	
	public File[] getBAMFiles(){
		fc.setFileFilter(new ExtensionFileFilter("bam"));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);
		fc.setSelectedFile(new File(""));
		fc.setDialogTitle("BAM File Selection");
		File[] bamFiles = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			bamFiles = fc.getSelectedFiles();
		}
		return bamFiles;
	}
}


	
