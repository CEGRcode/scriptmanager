package window_interface.Sequence_Analysis;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import scripts.Sequence_Analysis.FASTAExtract;
import util.FileSelection;

@SuppressWarnings("serial")
public class FASTAExtractWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	
	final DefaultListModel expList;
	ArrayList<File> BEDFiles = new ArrayList<File>();
	private File INPUT = null;
	private File OUTPUT_PATH = null;
	
	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnCalculate;
	private JButton btnOutput;
	private JLabel lblGenome;
	private JLabel lblDefaultToLocal;
	private JLabel lblCurrent;
	private JProgressBar progressBar;
	private JCheckBox chckbxStrand;

	public Task task;
	
	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException {
        	try {
        		if(INPUT == null) {
        			JOptionPane.showMessageDialog(null, "Genomic File Not Loaded!!!");
        		} else if(BEDFiles.size() < 1) {
        			JOptionPane.showMessageDialog(null, "No BAM Files Loaded!!!");
        		} else {
        			setProgress(0);
        			FASTAExtract signal = new FASTAExtract(INPUT, BEDFiles, OUTPUT_PATH, chckbxStrand.isSelected());
        				
        			signal.addPropertyChangeListener("fa", new PropertyChangeListener() {
					    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
					    	int temp = (Integer) propertyChangeEvent.getNewValue();
					    	int percentComplete = (int)(((double)(temp) / BEDFiles.size()) * 100);
				        	setProgress(percentComplete);
					     }
					 });
	        		
        			signal.setVisible(true);
        			signal.run();
					
        		}
        	} catch(NumberFormatException nfe){
				JOptionPane.showMessageDialog(null, "Invalid Input in Fields!!!");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	return null;
        }
        
        public void done() {
        	massXable(contentPane, true);
            setCursor(null); //turn off the wait cursor
        }
	}
	
	public FASTAExtractWindow() {
		setTitle("FASTA Extract from BED");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 350);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
	
		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 97, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
      	expList = new DefaultListModel();
		final JList listExp = new JList(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listExp);
		
		btnLoad = new JButton("Load BED Files");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 0, SpringLayout.WEST, scrollPane);
		btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				File[] newBEDFiles = FileSelection.getBEDFiles(fc);
				if(newBEDFiles != null) {
					for(int x = 0; x < newBEDFiles.length; x++) { 
						BEDFiles.add(newBEDFiles[x]);
						expList.addElement(newBEDFiles[x].getName());
					}
				}
			}
		});
		contentPane.add(btnLoad);
		
		btnRemoveBam = new JButton("Remove BED");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 0, SpringLayout.NORTH, btnRemoveBam);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnRemoveBam, -11, SpringLayout.NORTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBam, -10, SpringLayout.EAST, contentPane);
		btnRemoveBam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(listExp.getSelectedIndex() > -1) {
					BEDFiles.remove(listExp.getSelectedIndex());
					expList.remove(listExp.getSelectedIndex());
				}
			}
		});		
		contentPane.add(btnRemoveBam);
		
		btnCalculate = new JButton("Calculate");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -71, SpringLayout.NORTH, btnCalculate);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnCalculate, 165, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnCalculate, -165, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnCalculate, -10, SpringLayout.SOUTH, contentPane);
		contentPane.add(btnCalculate);
		
		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnCalculate);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -10, SpringLayout.EAST, contentPane);
        progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
        btnCalculate.setActionCommand("start");
        
        JButton btnLoadGenome = new JButton("Load Genome FASTA");
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoadGenome, 0, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, btnLoadGenome, 0, SpringLayout.WEST, scrollPane);
        contentPane.add(btnLoadGenome);
        
        lblGenome = new JLabel("No Genomic FASTA File Loaded");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblGenome, 10, SpringLayout.SOUTH, btnLoadGenome);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblGenome, 0, SpringLayout.WEST, btnLoad);
        sl_contentPane.putConstraint(SpringLayout.EAST, lblGenome, 0, SpringLayout.EAST, contentPane);
        contentPane.add(lblGenome);
        
        lblCurrent = new JLabel("Current Output:");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrent, 0, SpringLayout.WEST, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblCurrent, -45, SpringLayout.SOUTH, contentPane);
        lblCurrent.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblCurrent);
        
        lblDefaultToLocal = new JLabel("Default to Local Directory");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, lblCurrent);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, lblCurrent);
        lblDefaultToLocal.setBackground(Color.WHITE);
        contentPane.add(lblDefaultToLocal);
        
        btnOutput = new JButton("Output Directory");
        sl_contentPane.putConstraint(SpringLayout.WEST, btnOutput, 150, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, btnOutput, -150, SpringLayout.EAST, contentPane);
        btnOutput.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		OUTPUT_PATH = FileSelection.getOutputDir(fc);
    			if(OUTPUT_PATH != null) {
    				lblDefaultToLocal.setText(OUTPUT_PATH.getAbsolutePath());
    			}
        	}
        });
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutput, 6, SpringLayout.SOUTH, scrollPane);
        contentPane.add(btnOutput);
        
        chckbxStrand = new JCheckBox("Force Strandedness");
        sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxStrand, 1, SpringLayout.NORTH, btnLoad);
        sl_contentPane.putConstraint(SpringLayout.EAST, chckbxStrand, -17, SpringLayout.WEST, btnRemoveBam);
        chckbxStrand.setSelected(true);
        contentPane.add(chckbxStrand);

        btnLoadGenome.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		File temp = FileSelection.getFASTAFile(fc);
				if(temp != null) {
					INPUT = temp;
					lblGenome.setText(INPUT.getName());
				}
        	}
        });
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
}


	
