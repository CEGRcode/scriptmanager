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

import util.FileSelection;
import scripts.Sequence_Analysis.FASTARandomize;

@SuppressWarnings("serial")
public class RandomizeFASTAWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	
	final DefaultListModel<String> expList;
	ArrayList<File> FASTAFiles = new ArrayList<File>();
	private File OUTPUT_PATH = null;
	
	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnCalculate;
	private JButton btnOutput;
	private JLabel lblDefaultToLocal;
	private JLabel lblCurrent;
	private JProgressBar progressBar;

	public Task task;
	
	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException, InterruptedException {
        	if(FASTAFiles.size() < 1) {
        		JOptionPane.showMessageDialog(null, "No FASTA Files Loaded!!!");
        	} else {
        		setProgress(0);
        		
        		for(int x = 0; x < FASTAFiles.size(); x++) {
        			FASTARandomize.randomizeFASTA(FASTAFiles.get(x), OUTPUT_PATH);
        			int percentComplete = (int)(((double)(x + 1) / FASTAFiles.size()) * 100);
			       	setProgress(percentComplete);
        		}
        		setProgress(100);
    			JOptionPane.showMessageDialog(null, "Randomization Complete");
        	}
        	return null;
        }
        
        public void done() {
        	massXable(contentPane, true);
            setCursor(null); //turn off the wait cursor
        }
	}
	
	public RandomizeFASTAWindow() {
		setTitle("Randomize FASTA File");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 310);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
	
		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
      	expList = new DefaultListModel<String>();
		final JList<String> listExp = new JList<>(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listExp);
		
		btnLoad = new JButton("Load FASTA Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 13, SpringLayout.SOUTH, btnLoad);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 10, SpringLayout.WEST, contentPane);
		btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				File[] newFASTAFiles = FileSelection.getFiles(fc,"fa");
				if(newFASTAFiles != null) {
					for(int x = 0; x < newFASTAFiles.length; x++) { 
						FASTAFiles.add(newFASTAFiles[x]);
						expList.addElement(newFASTAFiles[x].getName());
					}
				}
			}
		});
		contentPane.add(btnLoad);
		
		btnRemoveBam = new JButton("Remove FASTA");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveBam, 0, SpringLayout.NORTH, btnLoad);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBam, -10, SpringLayout.EAST, contentPane);
		btnRemoveBam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(listExp.getSelectedIndex() > -1) {
					FASTAFiles.remove(listExp.getSelectedIndex());
					expList.remove(listExp.getSelectedIndex());
				}
			}
		});		
		contentPane.add(btnRemoveBam);
		
		btnCalculate = new JButton("Calculate");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnCalculate, 163, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnCalculate, -167, SpringLayout.EAST, contentPane);
		contentPane.add(btnCalculate);
		
		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnCalculate);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, 0, SpringLayout.EAST, scrollPane);
        progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
        btnCalculate.setActionCommand("start");
        
        lblCurrent = new JLabel("Current Output:");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrent, 10, SpringLayout.WEST, contentPane);
        lblCurrent.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblCurrent);
        
        lblDefaultToLocal = new JLabel("Default to Local Directory");
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnCalculate, 10, SpringLayout.SOUTH, lblDefaultToLocal);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, lblCurrent);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 10, SpringLayout.EAST, lblCurrent);
        lblDefaultToLocal.setBackground(Color.WHITE);
        contentPane.add(lblDefaultToLocal);
        
        btnOutput = new JButton("Output Directory");
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutput, 183, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.NORTH, btnOutput);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrent, 10, SpringLayout.SOUTH, btnOutput);
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
        contentPane.add(btnOutput);
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