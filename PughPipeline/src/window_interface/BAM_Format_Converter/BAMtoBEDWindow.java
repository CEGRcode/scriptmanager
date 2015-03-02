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
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.SwingWorker;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JLabel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import scripts.BAM_Format_Converter.BAMtoBED;
import util.FileSelection;

@SuppressWarnings("serial")
public class BAMtoBEDWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	
	final DefaultListModel expList;
	Vector<File> BAMFiles = new Vector<File>();
	private File OUTPUT = null;
	private int STRAND = 0;
	
	private JButton btnIndex;
	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnOutputDirectory;
	private JRadioButton rdbtnRead1;
	private JRadioButton rdbtnRead2;
	private JRadioButton rdbtnCombined;
	
	JProgressBar progressBar;
	public Task task;

	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException, InterruptedException {
        	setProgress(0);
        	if(rdbtnRead1.isSelected()) { STRAND = 0; }
        	else if(rdbtnRead2.isSelected()) { STRAND = 1; }
        	else if(rdbtnCombined.isSelected()) { STRAND = 2; }
        	
        	for(int x = 0; x < BAMFiles.size(); x++) {
        		BAMtoBED convert = new BAMtoBED(BAMFiles.get(x), OUTPUT, STRAND);
        		convert.setVisible(true);
				convert.run();
        		int percentComplete = (int)(((double)(x + 1) / BAMFiles.size()) * 100);
        		setProgress(percentComplete);
        	}
        	setProgress(100);
        	return null;
        }
        
        public void done() {
    		massXable(contentPane, true);
            setCursor(null); //turn off the wait cursor
        }
	}
	
	public BAMtoBEDWindow() {
		setTitle("BAM to BED Converter");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 650, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
	
		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 36, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -141, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
		btnLoad = new JButton("Load BAM Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 5, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 0, SpringLayout.WEST, scrollPane);
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
		
		btnIndex = new JButton("Convert");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnIndex, 250, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnIndex, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnIndex, -250, SpringLayout.EAST, contentPane);
		contentPane.add(btnIndex);
		
		rdbtnRead1 = new JRadioButton("Read 1");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnRead1, 30, SpringLayout.SOUTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnRead1, 123, SpringLayout.WEST, contentPane);
		contentPane.add(rdbtnRead1);
		
		rdbtnRead2 = new JRadioButton("Read 2");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnRead2, 0, SpringLayout.NORTH, rdbtnRead1);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnRead2, 98, SpringLayout.EAST, rdbtnRead1);
		contentPane.add(rdbtnRead2);
		
		rdbtnCombined = new JRadioButton("Combined");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnCombined, 0, SpringLayout.NORTH, rdbtnRead1);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnCombined, 111, SpringLayout.EAST, rdbtnRead2);
		contentPane.add(rdbtnCombined);
		
		ButtonGroup OutputRead = new ButtonGroup();
        OutputRead.add(rdbtnRead1);
        OutputRead.add(rdbtnRead2);
        OutputRead.add(rdbtnCombined);
        rdbtnRead1.setSelected(true);
        
        JLabel lblPleaseSelectWhich = new JLabel("Please Select Which Read to Output:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblPleaseSelectWhich, 10, SpringLayout.SOUTH, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblPleaseSelectWhich, 0, SpringLayout.WEST, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblPleaseSelectWhich, -6, SpringLayout.NORTH, rdbtnRead1);
        lblPleaseSelectWhich.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblPleaseSelectWhich);

        final JLabel lblDefaultToLocal = new JLabel("Default to Local Directory");
        lblDefaultToLocal.setFont(new Font("Dialog", Font.PLAIN, 12));
        sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 0, SpringLayout.WEST, rdbtnRead1);
        sl_contentPane.putConstraint(SpringLayout.EAST, lblDefaultToLocal, -15, SpringLayout.EAST, contentPane);
        lblDefaultToLocal.setBackground(Color.WHITE);
        contentPane.add(lblDefaultToLocal);
        
        JLabel lblCurrentOutput = new JLabel("Current Output:");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrentOutput, 0, SpringLayout.WEST, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblCurrentOutput, -35, SpringLayout.SOUTH, contentPane);
        lblCurrentOutput.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblCurrentOutput);
		
        btnOutputDirectory = new JButton("Output Directory");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 7, SpringLayout.SOUTH, btnOutputDirectory);
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
        
        btnIndex.setActionCommand("start");
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
	}
}


	
