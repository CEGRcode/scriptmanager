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

import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import util.FileSelection;
import window_interface.Sequence_Analysis.SearchMotifOutput;

@SuppressWarnings("serial")
public class SearchMotifWindow extends JFrame implements ActionListener, PropertyChangeListener {

	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));
	
	final DefaultListModel<String> genomeList;
	ArrayList<File> GenomeFiles = new ArrayList<File>();
	private File OUTPUT_PATH = null;
	private int counter = 0;
	

	private JPanel contentPane;
	private JTextField txtMotif;
	private JTextField txtMismatch;
	private JProgressBar progressBar;

public Task task;
	
	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException, InterruptedException {
        	try {
        		if(GenomeFiles.size() < 1) {
        			JOptionPane.showMessageDialog(null, "No FASTA Files Selected!!!");
        		} else if(txtMotif.getText().isEmpty()) {
    				JOptionPane.showMessageDialog(null, "No Motif String Entered!!!");
        		} else if(!parseValidIUPACString(txtMotif.getText())) {
        			System.out.println(txtMotif.getText());
       				JOptionPane.showMessageDialog(null, "Invalid IUPAC Nucleotides detected!!!");
        		} else if(Integer.parseInt(txtMismatch.getText()) < 0) {
    				JOptionPane.showMessageDialog(null, "Invalid Number of Mismatches Entered!!!");
        		} else {
        			setProgress(0);
        			SearchMotifOutput search;
    				for(int gfile = 0; gfile < GenomeFiles.size(); gfile++) {
    						search = new SearchMotifOutput(GenomeFiles.get(gfile), txtMotif.getText(), Integer.parseInt(txtMismatch.getText()), OUTPUT_PATH);	
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
		setTitle("Search Motif in FASTA file with Mismatch");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		JButton btnLoadFASTA = new JButton("Load FASTA File");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoadFASTA, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoadFASTA, 10, SpringLayout.WEST, contentPane);
		contentPane.add(btnLoadFASTA);
		
		JButton btnRemoveFASTAFile = new JButton("Remove FASTA File");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveFASTAFile, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveFASTAFile, -10, SpringLayout.EAST, contentPane);
		contentPane.add(btnRemoveFASTAFile);
		
		
		final JLabel lblNewLabel = new JLabel("Default to Local Directory");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblNewLabel, 15, SpringLayout.WEST, contentPane);
		lblNewLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		contentPane.add(lblNewLabel);
		
		
		JButton btnOutputDirectory = new JButton("Output Directory");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnOutputDirectory, 150, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnOutputDirectory, -150, SpringLayout.EAST, contentPane);
		btnOutputDirectory.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		OUTPUT_PATH = FileSelection.getOutputDir(fc);
    			if(OUTPUT_PATH != null) {
    				lblNewLabel.setText(OUTPUT_PATH.getAbsolutePath());
    			}
        	}
        });
		contentPane.add(btnOutputDirectory);
		
		JLabel lblCurrentOutput = new JLabel("Current Output:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblNewLabel, 5, SpringLayout.SOUTH, lblCurrentOutput);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrentOutput, 6, SpringLayout.SOUTH, btnOutputDirectory);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrentOutput, 10, SpringLayout.WEST, contentPane);
		lblCurrentOutput.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblCurrentOutput);
		
		
		
		JButton btnSearch = new JButton("Search");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnSearch, 150, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnSearch, -150, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnSearch, -10, SpringLayout.SOUTH, contentPane);
		contentPane.add(btnSearch);
		btnSearch.setActionCommand("start");
		btnSearch.addActionListener(this);
		
		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.WEST, progressBar, -125, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, progressBar, -10, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -10, SpringLayout.EAST, contentPane);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
		JLabel lblEnterAMotif = new JLabel("Enter an IUPAC Motif:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblEnterAMotif, 10, SpringLayout.WEST, contentPane);
		lblEnterAMotif.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblEnterAMotif);
		
		txtMotif = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutputDirectory, 10, SpringLayout.SOUTH, txtMotif);
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtMotif, 6, SpringLayout.SOUTH, lblEnterAMotif);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtMotif, 10, SpringLayout.WEST, contentPane);
		contentPane.add(txtMotif);
		txtMotif.setColumns(10);
		
		JLabel lblEnterAMismatch = new JLabel("Enter Mismatches Allowed:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblEnterAMismatch, 0, SpringLayout.NORTH, lblEnterAMotif);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblEnterAMismatch, -10, SpringLayout.EAST, contentPane);
		lblEnterAMismatch.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblEnterAMismatch);
		
		txtMismatch = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.WEST, txtMismatch, 350, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtMismatch, -10, SpringLayout.EAST, contentPane);
		txtMismatch.setHorizontalAlignment(SwingConstants.CENTER);
		txtMismatch.setText("0");
		sl_contentPane.putConstraint(SpringLayout.EAST, txtMotif, -125, SpringLayout.WEST, txtMismatch);
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtMismatch, 0, SpringLayout.NORTH, txtMotif);
		contentPane.add(txtMismatch);
		txtMismatch.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -175, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblEnterAMotif, 10, SpringLayout.SOUTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, btnLoadFASTA);
		contentPane.add(scrollPane);
		
		btnLoadFASTA.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				File[] newGenomeFiles = FileSelection.getFASTAFiles(fc);
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
		sl_contentPane.putConstraint(SpringLayout.NORTH, list, 142, SpringLayout.SOUTH, btnLoadFASTA);
		sl_contentPane.putConstraint(SpringLayout.WEST, list, 20, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, list, -18, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, list, -7, SpringLayout.NORTH, lblEnterAMotif);
		
		btnRemoveFASTAFile.addActionListener(new ActionListener() {
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
	
	private boolean parseValidIUPACString(String nuc) {
		String[] array = nuc.toUpperCase().split("");
		for(int x = 1; x < array.length; x++) {
			boolean valid = false;
			if(array[x].equals("A")) { valid = true; }
			else if(array[x].equals("T")) { valid = true; }
			else if(array[x].equals("G")) { valid = true; }
			else if(array[x].equals("C")) { valid = true; }
			else if(array[x].equals("R")) { valid = true; }
			else if(array[x].equals("Y")) { valid = true; }
			else if(array[x].equals("S")) { valid = true; }
			else if(array[x].equals("W")) { valid = true; }
			else if(array[x].equals("K")) { valid = true; }
			else if(array[x].equals("M")) { valid = true; }
			else if(array[x].equals("B")) { valid = true; }
			else if(array[x].equals("D")) { valid = true; }
			else if(array[x].equals("H")) { valid = true; }
			else if(array[x].equals("V")) { valid = true; }
			else if(array[x].equals("N")) { valid = true; }
			if(!valid) { return valid; }
		}
		return true;
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