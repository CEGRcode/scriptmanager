package window_interface.Sequence_Analysis;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JProgressBar;
import javax.swing.JTextField;

import scripts.Sequence_Analysis.SearchMotif;
import util.FileSelection;

import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class SearchMotifWindow extends JFrame implements ActionListener, PropertyChangeListener {

	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));
	
	final DefaultListModel<String> genomeList;
	ArrayList<File> GenomeFiles = new ArrayList<File>();
	private File OUTPUT_PATH = null;
	private int counter = 0;
	

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JProgressBar progressBar;

public Task task;
	
	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException, InterruptedException {
        	try {
        		if(GenomeFiles.size() < 1 || GenomeFiles.size() < 1) {
        			JOptionPane.showMessageDialog(null, "No Genome Reference Files Selected!!!");
        			if(textField.getText().isEmpty())
        				JOptionPane.showMessageDialog(null, "No Motif String Entered!!!");
        			if(textField_1.getText().isEmpty())
        				JOptionPane.showMessageDialog(null, "No Number of Mismatches Entered!!!");
        		} else {
        			setProgress(0);
        			SearchMotif search;
    				for(int gfile = 0; gfile < GenomeFiles.size(); gfile++)
    				{
    						search = new SearchMotif(GenomeFiles.get(gfile), textField.getText(), Integer.parseInt(textField_1.getText()), OUTPUT_PATH.getCanonicalPath());	
    						search.setVisible(true);
    						search.run();
        	        			counter++;
						int percentComplete = (int)(((double)(counter) / (GenomeFiles.size())) * 100);
						setProgress(percentComplete);		
    			}
    				JOptionPane.showMessageDialog(null, "Search Complete");
					
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
	
	public SearchMotifWindow() {
		setTitle("Search Motif in Genome with Mismatch");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 452, 489);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		JButton btnLoadGenome = new JButton("Load Genome Reference");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoadGenome, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoadGenome, 10, SpringLayout.WEST, contentPane);
		contentPane.add(btnLoadGenome);
		
		JButton btnRemoveFile = new JButton("Remove File");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveFile, 0, SpringLayout.NORTH, btnLoadGenome);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveFile, -10, SpringLayout.EAST, contentPane);
		contentPane.add(btnRemoveFile);
		
		
		final JLabel lblNewLabel = new JLabel("Default to Local Directory");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblNewLabel, 0, SpringLayout.WEST, btnLoadGenome);
		lblNewLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		contentPane.add(lblNewLabel);
		
		
		JButton btnOutputDirectory = new JButton("Output Directory");
		btnOutputDirectory.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		OUTPUT_PATH = FileSelection.getOutputDir(fc);
    			if(OUTPUT_PATH != null) {
    				lblNewLabel.setText(OUTPUT_PATH.getAbsolutePath());
    			}
        	}
        });
		contentPane.add(btnOutputDirectory);
		
		JLabel lblCurrentDirectory = new JLabel("Current Directory");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblNewLabel, 2, SpringLayout.SOUTH, lblCurrentDirectory);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrentDirectory, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblCurrentDirectory, -126, SpringLayout.SOUTH, contentPane);
		lblCurrentDirectory.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblCurrentDirectory);
		
		
		
		JButton btnSearch = new JButton("Search");
		sl_contentPane.putConstraint(SpringLayout.EAST, btnOutputDirectory, 0, SpringLayout.EAST, btnSearch);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnSearch, -35, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnSearch, -283, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnSearch, -10, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnSearch, -153, SpringLayout.EAST, contentPane);
		contentPane.add(btnSearch);
		btnSearch.setActionCommand("start");
		btnSearch.addActionListener(this);
		
		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnSearch);
		sl_contentPane.putConstraint(SpringLayout.WEST, progressBar, 4, SpringLayout.EAST, btnSearch);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, 0, SpringLayout.EAST, btnRemoveFile);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
		JLabel lblEnterAMotif = new JLabel("Enter a Motif:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblEnterAMotif, 0, SpringLayout.WEST, btnLoadGenome);
		lblEnterAMotif.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblEnterAMotif);
		
		textField = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, textField, 6, SpringLayout.SOUTH, lblEnterAMotif);
		sl_contentPane.putConstraint(SpringLayout.WEST, textField, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, textField, -224, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutputDirectory, 47, SpringLayout.SOUTH, textField);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblEnterAMismatch = new JLabel("Enter a Mismatch Number:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblEnterAMismatch, 0, SpringLayout.NORTH, lblEnterAMotif);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblEnterAMismatch, -10, SpringLayout.EAST, contentPane);
		lblEnterAMismatch.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblEnterAMismatch);
		
		textField_1 = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.EAST, textField, -125, SpringLayout.WEST, textField_1);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, textField_1, 0, SpringLayout.SOUTH, textField);
		sl_contentPane.putConstraint(SpringLayout.NORTH, textField_1, 0, SpringLayout.NORTH, textField);
		sl_contentPane.putConstraint(SpringLayout.WEST, textField_1, 308, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, textField_1, 0, SpringLayout.EAST, btnRemoveFile);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblEnterAMotif, 15, SpringLayout.SOUTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, btnLoadGenome);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -297, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, btnLoadGenome);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -8, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
		btnLoadGenome.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				File[] newGenomeFiles = FileSelection.getBEDFiles(fc);
				if(newGenomeFiles != null) {
					for(int x = 0; x < newGenomeFiles.length; x++) { 
						GenomeFiles.add(newGenomeFiles[x]);
						genomeList.addElement(newGenomeFiles[x].getName());
					}
				}
			}
		});
		
		genomeList = new DefaultListModel<String>();
		final JList<String> list = new JList<>(genomeList);
		scrollPane.setViewportView(list);
		sl_contentPane.putConstraint(SpringLayout.NORTH, list, 142, SpringLayout.SOUTH, btnLoadGenome);
		sl_contentPane.putConstraint(SpringLayout.WEST, list, 20, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, list, -18, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, list, -7, SpringLayout.NORTH, lblEnterAMotif);
		
		btnRemoveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(list.getSelectedIndex() > -1) {
					GenomeFiles.remove(list.getSelectedIndex());
					genomeList.remove(list.getSelectedIndex());
				}}});	
		
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
