package components;

import filters.GFFFilter;

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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;

import java.awt.Font;

@SuppressWarnings("serial")
public class PeakPairWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	
	final DefaultListModel expList;
	Vector<File> BAMFiles = new Vector<File>();
	
	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnPeak;

	private JProgressBar progressBar;
	public Task task;
	
	private JRadioButton rdbtnMode;
	private JRadioButton rdbtnClosest;
	private JRadioButton rdbtnLargest;
	private JRadioButton rdbtnAll;
	private JRadioButton rdbtnRelativeThreshold;
	private JRadioButton rdbtnAbsoluteThreshold;
	
	private JTextField txtUp;
	private JTextField txtDown;
	private JTextField txtBin;
	private JTextField txtRel;
	private JTextField txtAbs;
	
	private JLabel lblBinSizebp;
	private JLabel lblSort;
		
	private JComboBox cmboChrom;
	private JComboBox cmboScore;
	private JLabel lblScore;
	
	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException {
        	try {
				if(Integer.parseInt(txtUp.getText()) < 0) {
					JOptionPane.showMessageDialog(null, "Invalid Upstream Distance!!!");
				} else if(Integer.parseInt(txtDown.getText()) < 0) {
					JOptionPane.showMessageDialog(null, "Invalid Downstream Distance!!!");
				} else if(Integer.parseInt(txtBin.getText()) < 1) {
					JOptionPane.showMessageDialog(null, "Invalid Binning Size!!!");
				} else if(rdbtnAbsoluteThreshold.isSelected() && Integer.parseInt(txtAbs.getText()) < 1) {
					JOptionPane.showMessageDialog(null, "Invalid Absolute Threshold Size!!!");
				} else if(rdbtnRelativeThreshold.isSelected() && (Integer.parseInt(txtRel.getText()) < 1 || Integer.parseInt(txtRel.getText()) > 100)) {
					JOptionPane.showMessageDialog(null, "Invalid Relative Threshold Size!!!");
				} else {
					//int SIGMA = Integer.parseInt(txtSigma.getText());
		        			        	
		        	setProgress(0);
		        	for(int x = 0; x < BAMFiles.size(); x++) {       		
		        		//GeneTrack track = new GeneTrack(BAMFiles.get(x), SIGMA, EXCLUSION, UP, DOWN, FILTER);
		        		//track.setVisible(true);
						//track.run();
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
	
	public PeakPairWindow() {
		setTitle("Peak Pairing");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 500, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
	
		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -279, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
      	expList = new DefaultListModel();
		final JList listExp = new JList(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listExp);
		
		btnLoad = new JButton("Load GeneTrack Peaks");
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 5, SpringLayout.SOUTH, btnLoad);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 11, SpringLayout.WEST, contentPane);
		btnLoad.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				fc.setFileFilter(new GFFFilter());
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
		
		btnRemoveBam = new JButton("Remove Peak Files");
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
		
		btnPeak = new JButton("Peak Pair");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnPeak, 171, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnPeak, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnPeak, -171, SpringLayout.EAST, contentPane);
		contentPane.add(btnPeak);
		
		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 0, SpringLayout.NORTH, btnPeak);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, 0, SpringLayout.EAST, scrollPane);
        progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
        btnPeak.setActionCommand("start");
        
        rdbtnMode = new JRadioButton("Mode");
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnMode, 0, SpringLayout.WEST, scrollPane);
        contentPane.add(rdbtnMode);
        
        rdbtnClosest = new JRadioButton("Closest");
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnClosest, 0, SpringLayout.NORTH, rdbtnMode);
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnClosest, 67, SpringLayout.EAST, rdbtnMode);
        contentPane.add(rdbtnClosest);
        
        rdbtnLargest = new JRadioButton("Largest");
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnLargest, 0, SpringLayout.NORTH, rdbtnMode);
        contentPane.add(rdbtnLargest);
        
        rdbtnAll = new JRadioButton("All");
        sl_contentPane.putConstraint(SpringLayout.EAST, rdbtnLargest, -53, SpringLayout.WEST, rdbtnAll);
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnAll, 0, SpringLayout.NORTH, rdbtnMode);
        sl_contentPane.putConstraint(SpringLayout.EAST, rdbtnAll, 0, SpringLayout.EAST, scrollPane);
        contentPane.add(rdbtnAll);
        
        ButtonGroup METHOD = new ButtonGroup();
        METHOD.add(rdbtnMode);
        METHOD.add(rdbtnClosest);
        METHOD.add(rdbtnLargest);
        METHOD.add(rdbtnAll);
        rdbtnMode.setSelected(true);
        
        JLabel lblMethodOfPeak = new JLabel("Method of Peak Pairing:");
        lblMethodOfPeak.setToolTipText("Method of finding match");
        lblMethodOfPeak.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnMode, 6, SpringLayout.SOUTH, lblMethodOfPeak);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblMethodOfPeak, 6, SpringLayout.SOUTH, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblMethodOfPeak, 0, SpringLayout.WEST, scrollPane);
        contentPane.add(lblMethodOfPeak);
        
        JLabel lblUpstreamDistance = new JLabel("Upstream Distance (bp):");
        lblUpstreamDistance.setToolTipText("Distance upstream of a Watson peak to allow a Crick pair.");
        lblUpstreamDistance.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblUpstreamDistance, 16, SpringLayout.SOUTH, rdbtnMode);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblUpstreamDistance, 10, SpringLayout.WEST, contentPane);
        contentPane.add(lblUpstreamDistance);
        
        txtUp = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtUp, -6, SpringLayout.NORTH, lblUpstreamDistance);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtUp, 10, SpringLayout.WEST, btnPeak);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtUp, -255, SpringLayout.EAST, contentPane);
        txtUp.setHorizontalAlignment(SwingConstants.CENTER);
        txtUp.setText("50");
        contentPane.add(txtUp);
        txtUp.setColumns(10);
        
        JLabel lblDownstreamDistance = new JLabel("Downstream Distance (bp):");
        lblDownstreamDistance.setToolTipText("Distance downstream of a Watson peak to allow a Crick pair");
        lblDownstreamDistance.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblDownstreamDistance, 0, SpringLayout.NORTH, lblUpstreamDistance);
        contentPane.add(lblDownstreamDistance);
        
        txtDown = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.EAST, lblDownstreamDistance, -6, SpringLayout.WEST, txtDown);
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtDown, -6, SpringLayout.NORTH, lblUpstreamDistance);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtDown, 426, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtDown, 0, SpringLayout.EAST, scrollPane);
        txtDown.setHorizontalAlignment(SwingConstants.CENTER);
        txtDown.setText("100");
        contentPane.add(txtDown);
        txtDown.setColumns(10);
        
        lblBinSizebp = new JLabel("Bin Size (bp):");
        lblBinSizebp.setToolTipText("Width of bins for frequency plots and mode calculations");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblBinSizebp, 10, SpringLayout.WEST, contentPane);
        lblBinSizebp.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblBinSizebp);
        
        txtBin = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtBin, 6, SpringLayout.SOUTH, txtUp);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblBinSizebp, 6, SpringLayout.NORTH, txtBin);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtBin, 0, SpringLayout.WEST, txtUp);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtBin, 0, SpringLayout.EAST, txtUp);
        txtBin.setHorizontalAlignment(SwingConstants.CENTER);
        txtBin.setText("1");
        contentPane.add(txtBin);
        txtBin.setColumns(10);
        
        rdbtnRelativeThreshold = new JRadioButton("Relative Threshold");
        rdbtnRelativeThreshold.setToolTipText("Percentage of the 95 percentile value to filter below.");
        contentPane.add(rdbtnRelativeThreshold);
        
        rdbtnAbsoluteThreshold = new JRadioButton("Absolute Threshold");
        rdbtnAbsoluteThreshold.setToolTipText("Absolute value to filter below");
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnRelativeThreshold, 0, SpringLayout.NORTH, rdbtnAbsoluteThreshold);
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnAbsoluteThreshold, 0, SpringLayout.WEST, scrollPane);
        contentPane.add(rdbtnAbsoluteThreshold);
        
        ButtonGroup FILTER = new ButtonGroup();
        FILTER.add(rdbtnRelativeThreshold);
        FILTER.add(rdbtnAbsoluteThreshold);
        rdbtnAbsoluteThreshold.setSelected(true);
        
        rdbtnRelativeThreshold.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		        if(rdbtnRelativeThreshold.isSelected()) {
		        	txtRel.setEnabled(true);
		        	txtAbs.setEnabled(false);
		        }
		      }
		    });
        rdbtnAbsoluteThreshold.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		        if(rdbtnAbsoluteThreshold.isSelected()) {
		        	txtAbs.setEnabled(true);
		        	txtRel.setEnabled(false);
		        }
		      }
		    });
        
        txtRel = new JTextField();
        txtRel.setEnabled(false);
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtRel, 74, SpringLayout.SOUTH, txtDown);
        sl_contentPane.putConstraint(SpringLayout.EAST, rdbtnRelativeThreshold, -14, SpringLayout.WEST, txtRel);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtRel, 0, SpringLayout.WEST, txtDown);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtRel, 0, SpringLayout.EAST, scrollPane);
        txtRel.setHorizontalAlignment(SwingConstants.CENTER);
        txtRel.setText("0");
        contentPane.add(txtRel);
        txtRel.setColumns(10);
        
        txtAbs = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtAbs, 40, SpringLayout.SOUTH, txtBin);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtAbs, 0, SpringLayout.WEST, txtUp);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtAbs, 0, SpringLayout.EAST, txtUp);
        txtAbs.setHorizontalAlignment(SwingConstants.CENTER);
        txtAbs.setText("0");
        contentPane.add(txtAbs);
        txtAbs.setColumns(10);
        
        lblSort = new JLabel("Sort by:");
        lblSort.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        sl_contentPane.putConstraint(SpringLayout.WEST, lblSort, 0, SpringLayout.WEST, scrollPane);
        contentPane.add(lblSort);
        
        String[] chromSort = {"ascending", "descending"};
        cmboChrom = new JComboBox(chromSort);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, cmboChrom, -6, SpringLayout.NORTH, btnPeak);
        sl_contentPane.putConstraint(SpringLayout.EAST, cmboChrom, 14, SpringLayout.EAST, txtUp);
        contentPane.add(cmboChrom);
        
        String[] scoreSort = {"ascending", "descending", "none"};
        cmboScore = new JComboBox(scoreSort);
        cmboScore.setSelectedIndex(2);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, cmboScore, -6, SpringLayout.NORTH, btnPeak);
        sl_contentPane.putConstraint(SpringLayout.EAST, cmboScore, -44, SpringLayout.EAST, contentPane);
        contentPane.add(cmboScore);
                
        JLabel lblFilterBy = new JLabel("Filter by:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnAbsoluteThreshold, 12, SpringLayout.SOUTH, lblFilterBy);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblFilterBy, -126, SpringLayout.SOUTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblSort, 41, SpringLayout.SOUTH, lblFilterBy);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblFilterBy, 0, SpringLayout.WEST, scrollPane);
        lblFilterBy.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblFilterBy);
        
        JLabel lblChromosome = new JLabel("Chromosome:");
        lblChromosome.setToolTipText("Output files will be sorted by chromosome");
        sl_contentPane.putConstraint(SpringLayout.WEST, cmboChrom, 6, SpringLayout.EAST, lblChromosome);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblChromosome, 11, SpringLayout.SOUTH, lblSort);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblChromosome, 10, SpringLayout.WEST, lblSort);
        contentPane.add(lblChromosome);
        
        lblScore = new JLabel("Score:");
        lblScore.setToolTipText("Output files will be sorted by score");
        sl_contentPane.putConstraint(SpringLayout.WEST, cmboScore, 9, SpringLayout.EAST, lblScore);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblScore, 4, SpringLayout.NORTH, cmboChrom);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblScore, 0, SpringLayout.WEST, rdbtnRelativeThreshold);
        contentPane.add(lblScore);
        btnPeak.addActionListener(this);
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


	
