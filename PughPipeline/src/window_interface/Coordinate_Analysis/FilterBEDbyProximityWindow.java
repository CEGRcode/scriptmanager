package window_interface.Coordinate_Analysis;

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
import util.FileSelection;
import javax.swing.JScrollPane;
import scripts.Coordinate_Analysis.FilterBEDbyProximity;

@SuppressWarnings("serial")
public class FilterBEDbyProximityWindow extends JFrame implements ActionListener, PropertyChangeListener {

	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));
	
	final DefaultListModel<String> bedList;
	ArrayList<File> BEDFiles = new ArrayList<File>();
	private File OUTPUT_PATH = null;
	private int counter = 0;
	
	private JPanel contentPane;
	private JTextField txtCutoff;
	JProgressBar progressBar;
	
	
public Task task;
	
	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException, InterruptedException {
        	try {
        		if(BEDFiles.size() < 1) {
        			JOptionPane.showMessageDialog(null, "No BED Files Selected!!!");
        		} else if(txtCutoff.getText().isEmpty()) {
    				JOptionPane.showMessageDialog(null, "No Cutoff Value Entered!!!");
        		} else if(Integer.parseInt(txtCutoff.getText()) < 0) {
    				JOptionPane.showMessageDialog(null, "Invalid Cutoff Value Entered!!!");
        		} else {
        			setProgress(0);
        			FilterBEDbyProximity filter;
    				for(int gfile = 0; gfile < BEDFiles.size(); gfile++) {
    					filter = new FilterBEDbyProximity(BEDFiles.get(gfile), Integer.parseInt(txtCutoff.getText()), OUTPUT_PATH);	
    					filter.setVisible(true);
    					filter.run();
        	        		counter++;
        	        		int percentComplete = (int)(((double)(counter) / (BEDFiles.size())) * 100);
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
	
	public FilterBEDbyProximityWindow() {
		setTitle("Filter BED File by Proximity with a Cutoff");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 479, 445);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		JButton btnLoadBedFile = new JButton("Load BED File");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoadBedFile, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoadBedFile, 10, SpringLayout.WEST, contentPane);
		btnLoadBedFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				File[] newGenomeFiles = FileSelection.getFASTAFiles(fc);
				if(newGenomeFiles != null) {
					for(int x = 0; x < newGenomeFiles.length; x++) { 
						BEDFiles.add(newGenomeFiles[x]);
						bedList.addElement(newGenomeFiles[x].getName());
					}
				}
			}
		});
		contentPane.add(btnLoadBedFile);
		
		JButton btnRemoveBedFile = new JButton("Remove BED File");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveBedFile, 0, SpringLayout.NORTH, btnLoadBedFile);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBedFile, -10, SpringLayout.EAST, contentPane);
		contentPane.add(btnRemoveBedFile);
		
		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 27, SpringLayout.SOUTH, btnLoadBedFile);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, btnLoadBedFile);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, 156, SpringLayout.SOUTH, btnLoadBedFile);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, 0, SpringLayout.EAST, btnRemoveBedFile);
		contentPane.add(scrollPane);
		
		bedList = new DefaultListModel<String>();
		final JList<String> list = new JList<>(bedList);
		scrollPane.setColumnHeaderView(list);
		
		btnRemoveBedFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(list.getSelectedIndex() > -1) {
					BEDFiles.remove(list.getSelectedIndex());
					bedList.remove(list.getSelectedIndex());
				}}});
		
		
		JLabel lblEnterACutoff = new JLabel("Enter a Cutoff Distance(bp):");
		lblEnterACutoff.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblEnterACutoff, 22, SpringLayout.SOUTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblEnterACutoff, 10, SpringLayout.WEST, contentPane);
		contentPane.add(lblEnterACutoff);
		
		txtCutoff = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtCutoff, 6, SpringLayout.SOUTH, lblEnterACutoff);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtCutoff, 0, SpringLayout.WEST, btnLoadBedFile);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtCutoff, 186, SpringLayout.WEST, btnLoadBedFile);
		contentPane.add(txtCutoff);
		txtCutoff.setColumns(10);
		
		
		final JLabel lblDefaultToLocal = new JLabel("Default to Local Directory");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 0, SpringLayout.WEST, btnLoadBedFile);
		lblDefaultToLocal.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		contentPane.add(lblDefaultToLocal);
		
		JButton btnOutputDirectory = new JButton("Output Directory");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutputDirectory, 16, SpringLayout.SOUTH, txtCutoff);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnOutputDirectory, 41, SpringLayout.SOUTH, txtCutoff);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnOutputDirectory, -156, SpringLayout.EAST, contentPane);
		btnOutputDirectory.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		OUTPUT_PATH = FileSelection.getOutputDir(fc);
    			if(OUTPUT_PATH != null) {
    				lblDefaultToLocal.setText(OUTPUT_PATH.getAbsolutePath());
    			}
        	}
        });
		contentPane.add(btnOutputDirectory);
		
		JLabel lblCurrentOutputDirectory = new JLabel("Current Output:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 6, SpringLayout.SOUTH, lblCurrentOutputDirectory);
		lblCurrentOutputDirectory.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrentOutputDirectory, 59, SpringLayout.SOUTH, txtCutoff);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrentOutputDirectory, 0, SpringLayout.WEST, btnLoadBedFile);
		contentPane.add(lblCurrentOutputDirectory);
		
		
		JButton btnFilter = new JButton("Filter");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnFilter, 0, SpringLayout.WEST, btnOutputDirectory);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnFilter, -10, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnFilter, -153, SpringLayout.EAST, contentPane);
		contentPane.add(btnFilter);
		btnFilter.setActionCommand("start");
		btnFilter.addActionListener(this);
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 86, SpringLayout.SOUTH, btnOutputDirectory);
		sl_contentPane.putConstraint(SpringLayout.WEST, progressBar, 24, SpringLayout.EAST, btnFilter);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, progressBar, 0, SpringLayout.SOUTH, btnFilter);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -10, SpringLayout.EAST, contentPane);
		contentPane.add(progressBar);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		massXable(contentPane, false);
    	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
	}
	
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
