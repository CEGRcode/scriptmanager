package components;

import filters.TABFilter;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JLabel;

import scripts.GeneTrack;

@SuppressWarnings("serial")
public class GeneTrackWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	
	final DefaultListModel expList;
	Vector<File> BAMFiles = new Vector<File>();
	
	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnGene;

	private JProgressBar progressBar;
	public Task task;
	private JTextField txtSigma;
	private JTextField txtExclusion;
	private JTextField txtUp;
	private JTextField txtDown;
	private JTextField txtFilter;
	private JLabel lblUpWidth;
	private JLabel lblDownWidth;
	private JLabel lblMinimumTagsPer;
	
	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException {
        	try {
				if(Integer.parseInt(txtSigma.getText()) < 1) {
					JOptionPane.showMessageDialog(null, "Invalid Sigma!!!");
				} else if(Integer.parseInt(txtExclusion.getText()) < 1) {
					JOptionPane.showMessageDialog(null, "Invalid Exclusion!!!");
				} else if(Integer.parseInt(txtUp.getText()) < 1) {
					JOptionPane.showMessageDialog(null, "Invalid Upstream Window Width!!!");
				} else if(Integer.parseInt(txtDown.getText()) < 1) {
					JOptionPane.showMessageDialog(null, "Invalid Downstream Window Width!!!");
				} else if(Integer.parseInt(txtFilter.getText()) < 1) {
					JOptionPane.showMessageDialog(null, "Invalid Peak Filtering Criteria!!!");
				} else {
					int SIGMA = Integer.parseInt(txtSigma.getText());
		        	int EXCLUSION = Integer.parseInt(txtExclusion.getText());
		        	int UP = Integer.parseInt(txtUp.getText());
		        	int DOWN = Integer.parseInt(txtDown.getText());
		        	int FILTER = Integer.parseInt(txtFilter.getText());
		        	
		        	setProgress(0);
		        	for(int x = 0; x < BAMFiles.size(); x++) {       		
		        		GeneTrack track = new GeneTrack(BAMFiles.get(x), SIGMA, EXCLUSION, UP, DOWN, FILTER);
		        		track.setVisible(true);
						track.run();
						int percentComplete = (int)(((double)(x + 1) / BAMFiles.size()) * 100);
		        		setProgress(percentComplete);
		        	}
		        	setProgress(100);
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
	
	public GeneTrackWindow() {
		setTitle("GeneTrack");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 345);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
	
		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -127, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -5, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
      	expList = new DefaultListModel();
		final JList listExp = new JList(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listExp);
		
		btnLoad = new JButton("Load BAM Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, btnLoad);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 11, SpringLayout.WEST, contentPane);
		btnLoad.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				fc.setFileFilter(new TABFilter());
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
		
		btnGene = new JButton("GeneTrack");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnGene, 171, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnGene, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnGene, -171, SpringLayout.EAST, contentPane);
		contentPane.add(btnGene);
		
		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.SOUTH, progressBar, -5, SpringLayout.SOUTH, btnGene);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -5, SpringLayout.EAST, contentPane);
        progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
        btnGene.setActionCommand("start");
        
        txtSigma = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtSigma, 6, SpringLayout.SOUTH, scrollPane);
        txtSigma.setHorizontalAlignment(SwingConstants.CENTER);
        txtSigma.setText("5");
        contentPane.add(txtSigma);
        txtSigma.setColumns(10);
        
        txtExclusion = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtExclusion, 6, SpringLayout.SOUTH, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtExclusion, 316, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtExclusion, -72, SpringLayout.EAST, contentPane);
        txtExclusion.setHorizontalAlignment(SwingConstants.CENTER);
        txtExclusion.setText("20");
        contentPane.add(txtExclusion);
        txtExclusion.setColumns(10);
        
        txtUp = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtUp, 35, SpringLayout.SOUTH, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtUp, 0, SpringLayout.EAST, txtSigma);
        txtUp.setHorizontalAlignment(SwingConstants.CENTER);
        txtUp.setText("10");
        contentPane.add(txtUp);
        txtUp.setColumns(10);
        
        txtDown = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtDown, 1, SpringLayout.SOUTH, txtExclusion);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtDown, 0, SpringLayout.WEST, txtExclusion);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtDown, 0, SpringLayout.EAST, txtExclusion);
        txtDown.setHorizontalAlignment(SwingConstants.CENTER);
        txtDown.setText("10");
        contentPane.add(txtDown);
        txtDown.setColumns(10);
        
        txtFilter = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.SOUTH, txtFilter, -1, SpringLayout.NORTH, btnGene);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtFilter, -150, SpringLayout.EAST, contentPane);
        txtFilter.setHorizontalAlignment(SwingConstants.CENTER);
        txtFilter.setText("1");
        contentPane.add(txtFilter);
        txtFilter.setColumns(10);
        
        JLabel lblSigma = new JLabel("Sigma:");
        sl_contentPane.putConstraint(SpringLayout.EAST, lblSigma, -316, SpringLayout.EAST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtSigma, 22, SpringLayout.EAST, lblSigma);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblSigma, 12, SpringLayout.SOUTH, scrollPane);
        contentPane.add(lblSigma);
        
        JLabel lblExclusion = new JLabel("Exclusion Zone:");
        sl_contentPane.putConstraint(SpringLayout.EAST, txtSigma, -10, SpringLayout.WEST, lblExclusion);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblExclusion, 12, SpringLayout.SOUTH, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, lblExclusion, -6, SpringLayout.WEST, txtExclusion);
        contentPane.add(lblExclusion);
        
        lblUpWidth = new JLabel("Up Width:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblUpWidth, 13, SpringLayout.SOUTH, lblSigma);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtUp, 6, SpringLayout.EAST, lblUpWidth);
        contentPane.add(lblUpWidth);
        
        lblDownWidth = new JLabel("Down Width:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblDownWidth, 13, SpringLayout.SOUTH, lblExclusion);
        sl_contentPane.putConstraint(SpringLayout.EAST, lblDownWidth, -6, SpringLayout.WEST, txtDown);
        contentPane.add(lblDownWidth);
        
        lblMinimumTagsPer = new JLabel("Minimum Tags per Peak:");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblUpWidth, 0, SpringLayout.WEST, lblMinimumTagsPer);
        sl_contentPane.putConstraint(SpringLayout.EAST, lblMinimumTagsPer, -207, SpringLayout.EAST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtFilter, 6, SpringLayout.EAST, lblMinimumTagsPer);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblMinimumTagsPer, -6, SpringLayout.NORTH, btnGene);
        contentPane.add(lblMinimumTagsPer);
        btnGene.addActionListener(this);
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


	
