package scriptmanager.window_interface.Peak_Analysis;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
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
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import scriptmanager.util.FileSelection;

@SuppressWarnings("serial")
public class SortByRefWindow extends JFrame implements ActionListener, PropertyChangeListener {

	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));
	
	final DefaultListModel<String> peakBEDList;
	final DefaultListModel<String> peakGFFList;
	final DefaultListModel<String> refBEDList;
	final DefaultListModel<String> refGFFList;
	ArrayList<File> PeakBEDFiles = new ArrayList<File>();
	ArrayList<File> PeakGFFFiles = new ArrayList<File>();
	ArrayList<File> RefBEDFiles = new ArrayList<File>();
	ArrayList<File> RefGFFFiles = new ArrayList<File>();
	private File OUTPUT_PATH = null;
	
	private JButton btnLoadPeakBed;
	private JButton btnLoadPeakGff;
	private JButton btnRemovePeakBed;
	private JButton btnRemovePeakGff;
	private JButton btnLoadRefBed;
	private JButton btnLoadRefGff;
	private JButton btnRemoveRefBed;
	private JButton btnRemoveReGff;
	private JButton btnOutputDirectory;
	private JButton btnCalculate;
	private JCheckBox chckbxProperStrands;
	private JCheckBox chckbxGzipOutput;
	private JLabel lblCurrentOutput;
	private JLabel lblDefaultToLocal;
	private JProgressBar progressBar;

	private JRadioButton rdbtnBed;
	private JRadioButton rdbtnGff;
	
	public Task task;
	
	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException, InterruptedException {
        	try {
			if (rdbtnBed.isSelected()) {
					if(PeakBEDFiles.size() < 1 || RefBEDFiles.size() < 1) {
						JOptionPane.showMessageDialog(null, "No BED Files Loaded!!!");
					} else {
						setProgress(0);
						SortByRefOutput align;
						int counter = 0;

						for(int r = 0; r < RefBEDFiles.size(); r++)
						{
							for(int p=0; p < PeakBEDFiles.size(); p++)
							{
								align = new SortByRefOutput(RefBEDFiles.get(r), PeakBEDFiles.get(p), OUTPUT_PATH, chckbxProperStrands.isSelected(),
								chckbxGzipOutput.isSelected(), false);	
								align.setVisible(true);
								align.run();
								counter++;
							int percentComplete = (int)(((double)(counter) / (PeakBEDFiles.size()*RefBEDFiles.size())) * 100);
							setProgress(percentComplete);	
							}	
					}
						JOptionPane.showMessageDialog(null, "Alignment Complete");
					}
				} else {
					if(PeakGFFFiles.size() < 1 || RefGFFFiles.size() < 1) {
						JOptionPane.showMessageDialog(null, "No BED Files Loaded!!!");
					} else {
						setProgress(0);
						SortByRefOutput align;
						int counter = 0;

						for(int r = 0; r < RefGFFFiles.size(); r++)
						{
							for(int p=0; p < PeakGFFFiles.size(); p++)
							{
								align = new SortByRefOutput(RefGFFFiles.get(r), PeakGFFFiles.get(p), OUTPUT_PATH, chckbxProperStrands.isSelected(), 
								chckbxGzipOutput.isSelected(), true);	
								align.setVisible(true);
								align.run();
								counter++;
							int percentComplete = (int)(((double)(counter) / (PeakGFFFiles.size()*RefGFFFiles.size())) * 100);
							setProgress(percentComplete);	
							}	
					}
						JOptionPane.showMessageDialog(null, "Alignment Complete");
					}
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
	
	public SortByRefWindow() {
		setTitle("Sort Coordinate By Reference");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 570);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		rdbtnBed = new JRadioButton("BED input");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnBed, 6, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnBed, 155, SpringLayout.WEST, contentPane);
		contentPane.add(rdbtnBed);

		rdbtnGff = new JRadioButton("GFF input");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnGff, 0, SpringLayout.NORTH, rdbtnBed);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnGff, 10, SpringLayout.EAST, rdbtnBed);
		contentPane.add(rdbtnGff);

		ButtonGroup InputType = new ButtonGroup();
		InputType.add(rdbtnBed);
		InputType.add(rdbtnGff);
		rdbtnBed.setSelected(true);

		JPanel inputCards = new JPanel(new CardLayout());
		sl_contentPane.putConstraint(SpringLayout.NORTH, inputCards, 10, SpringLayout.SOUTH, rdbtnBed);
		sl_contentPane.putConstraint(SpringLayout.WEST, inputCards, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, inputCards, 0, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, inputCards, -145, SpringLayout.SOUTH, contentPane);
		contentPane.add(inputCards);

		JPanel bedInputPane = new JPanel();
		SpringLayout sl_bedInputPane = new SpringLayout();
		bedInputPane.setLayout(sl_bedInputPane);
		inputCards.add(bedInputPane, "bed");

		JPanel gffInputPane = new JPanel();
		SpringLayout sl_gffInputPane = new SpringLayout();
		gffInputPane.setLayout(sl_gffInputPane);
		inputCards.add(gffInputPane, "gff");
		
		//Initialize buttons and list for peak BED files
		btnLoadPeakBed = new JButton("Load Peak BED");
		sl_bedInputPane.putConstraint(SpringLayout.NORTH, btnLoadPeakBed, 5, SpringLayout.NORTH, bedInputPane);
		sl_bedInputPane.putConstraint(SpringLayout.WEST, btnLoadPeakBed, 5, SpringLayout.WEST, bedInputPane);
		
		btnRemovePeakBed = new JButton("Remove Peak BED");
		sl_bedInputPane.putConstraint(SpringLayout.NORTH, btnRemovePeakBed, 5, SpringLayout.NORTH, bedInputPane);
		sl_bedInputPane.putConstraint(SpringLayout.EAST, btnRemovePeakBed, -5, SpringLayout.EAST, bedInputPane);

		JScrollPane scrollPaneBedPeak = new JScrollPane();
		sl_bedInputPane.putConstraint(SpringLayout.NORTH, scrollPaneBedPeak, 12, SpringLayout.SOUTH, btnLoadPeakBed);
		sl_bedInputPane.putConstraint(SpringLayout.WEST, scrollPaneBedPeak, 5, SpringLayout.WEST, bedInputPane);
		sl_bedInputPane.putConstraint(SpringLayout.EAST, scrollPaneBedPeak, -5, SpringLayout.EAST, bedInputPane);
		bedInputPane.add(scrollPaneBedPeak);
		
		peakBEDList = new DefaultListModel<String>();
		final JList<String> listPeak = new JList<String>(peakBEDList);
		listPeak.setForeground(Color.BLACK);
		listPeak.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		scrollPaneBedPeak.setViewportView(listPeak);
		
		btnLoadPeakBed.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				File[] newBEDFiles = FileSelection.getFiles(fc,"bed");
				if(newBEDFiles != null) {
					for(int x = 0; x < newBEDFiles.length; x++) { 
						PeakBEDFiles.add(newBEDFiles[x]);
						peakBEDList.addElement(newBEDFiles[x].getName());
					}
				}
			}
		});
		bedInputPane.add(btnLoadPeakBed);
		
		btnRemovePeakBed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(listPeak.getSelectedIndex() > -1) {
					PeakBEDFiles.remove(listPeak.getSelectedIndex());
					peakBEDList.remove(listPeak.getSelectedIndex());
				}
			}
		});	
		bedInputPane.add(btnRemovePeakBed);
		
		//Initialize buttons and list for reference BED files
		btnLoadRefBed = new JButton("Load Reference BED");
		sl_bedInputPane.putConstraint(SpringLayout.NORTH, btnLoadRefBed, 10, SpringLayout.SOUTH, scrollPaneBedPeak);
		sl_bedInputPane.putConstraint(SpringLayout.WEST, btnLoadRefBed, 5, SpringLayout.WEST, bedInputPane);
		
		btnRemoveRefBed = new JButton("Remove Reference BED");
		sl_bedInputPane.putConstraint(SpringLayout.NORTH, btnRemoveRefBed, 10, SpringLayout.SOUTH, scrollPaneBedPeak);
		sl_bedInputPane.putConstraint(SpringLayout.EAST, btnRemoveRefBed, -5, SpringLayout.EAST, bedInputPane);
		
		JScrollPane scrollPaneBedRef = new JScrollPane();
		sl_bedInputPane.putConstraint(SpringLayout.NORTH, scrollPaneBedRef, 10, SpringLayout.SOUTH, btnLoadRefBed);
		//sl_bedInputPane.putConstraint(SpringLayout.SOUTH, scrollPaneBedRef, -100, SpringLayout.SOUTH, btnLoadRefBed);
		sl_bedInputPane.putConstraint(SpringLayout.WEST, scrollPaneBedRef, 5, SpringLayout.WEST, bedInputPane);
		sl_bedInputPane.putConstraint(SpringLayout.EAST, scrollPaneBedRef, -5, SpringLayout.EAST, bedInputPane);
		bedInputPane.add(scrollPaneBedRef);
		
		refBEDList = new DefaultListModel<String>();
		final JList<String> listRef = new JList<String>(refBEDList);
		listRef.setForeground(Color.BLACK);
		listRef.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		scrollPaneBedRef.setViewportView(listRef);
		
		btnLoadRefBed.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				File[] newBEDFiles = FileSelection.getFiles(fc,"bed");
				if(newBEDFiles != null) {
					for(int x = 0; x < newBEDFiles.length; x++) { 
						RefBEDFiles.add(newBEDFiles[x]);
						refBEDList.addElement(newBEDFiles[x].getName());
					}
				}
			}
		});
		bedInputPane.add(btnLoadRefBed);
		
		btnRemoveRefBed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(listRef.getSelectedIndex() > -1) {
					RefBEDFiles.remove(listRef.getSelectedIndex());
					refBEDList.remove(listRef.getSelectedIndex());
				}
			}
		});
		bedInputPane.add(btnRemoveRefBed);

		//Initialize buttons and list for peak GFF files
		btnLoadPeakGff = new JButton("Load Peak GFF");
		sl_gffInputPane.putConstraint(SpringLayout.NORTH, btnLoadPeakGff, 5, SpringLayout.NORTH, gffInputPane);
		sl_gffInputPane.putConstraint(SpringLayout.WEST, btnLoadPeakGff, 5, SpringLayout.WEST, gffInputPane);
		
		btnRemovePeakGff = new JButton("Remove Peak GFF");
		sl_gffInputPane.putConstraint(SpringLayout.NORTH, btnRemovePeakGff, 5, SpringLayout.NORTH, gffInputPane);
		sl_gffInputPane.putConstraint(SpringLayout.EAST, btnRemovePeakGff, -5, SpringLayout.EAST, gffInputPane);

		JScrollPane scrollPaneGffPeak = new JScrollPane();
		sl_gffInputPane.putConstraint(SpringLayout.NORTH, scrollPaneGffPeak, 12, SpringLayout.SOUTH, btnLoadPeakGff);
		sl_gffInputPane.putConstraint(SpringLayout.WEST, scrollPaneGffPeak, 5, SpringLayout.WEST, gffInputPane);
		sl_gffInputPane.putConstraint(SpringLayout.EAST, scrollPaneGffPeak, -5, SpringLayout.EAST, gffInputPane);
		gffInputPane.add(scrollPaneGffPeak);
		
		peakGFFList = new DefaultListModel<String>();
		final JList<String> listGFFPeak = new JList<String>(peakGFFList);
		listGFFPeak.setForeground(Color.BLACK);
		listGFFPeak.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		scrollPaneGffPeak.setViewportView(listGFFPeak);
		
		btnLoadPeakGff.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				File[] newGFFFiles = FileSelection.getFiles(fc,"GFF");
				if(newGFFFiles != null) {
					for(int x = 0; x < newGFFFiles.length; x++) { 
						PeakGFFFiles.add(newGFFFiles[x]);
						peakGFFList.addElement(newGFFFiles[x].getName());
					}
				}
			}
		});
		gffInputPane.add(btnLoadPeakGff);
		
		btnRemovePeakGff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(listGFFPeak.getSelectedIndex() > -1) {
					PeakGFFFiles.remove(listGFFPeak.getSelectedIndex());
					peakGFFList.remove(listGFFPeak.getSelectedIndex());
				}
			}
		});	
		gffInputPane.add(btnRemovePeakGff);
		
		//Initialize buttons and list for reference GFF files
		btnLoadRefGff = new JButton("Load Reference GFF");
		sl_gffInputPane.putConstraint(SpringLayout.NORTH, btnLoadRefGff, 10, SpringLayout.SOUTH, scrollPaneGffPeak);
		sl_gffInputPane.putConstraint(SpringLayout.WEST, btnLoadRefGff, 5, SpringLayout.WEST, gffInputPane);
		
		btnRemoveReGff = new JButton("Remove Reference GFF");
		sl_gffInputPane.putConstraint(SpringLayout.NORTH, btnRemoveReGff, 10, SpringLayout.SOUTH, scrollPaneGffPeak);
		sl_gffInputPane.putConstraint(SpringLayout.EAST, btnRemoveReGff, -5, SpringLayout.EAST, gffInputPane);
		
		JScrollPane scrollPaneGffRef = new JScrollPane();
		sl_gffInputPane.putConstraint(SpringLayout.NORTH, scrollPaneGffRef, 10, SpringLayout.SOUTH, btnLoadRefGff);
		//sl_gffInputPane.putConstraint(SpringLayout.SOUTH, scrollPaneGffRef, -100, SpringLayout.SOUTH, btnLoadRefGff);
		sl_gffInputPane.putConstraint(SpringLayout.WEST, scrollPaneGffRef, 5, SpringLayout.WEST, gffInputPane);
		sl_gffInputPane.putConstraint(SpringLayout.EAST, scrollPaneGffRef, -5, SpringLayout.EAST, gffInputPane);
		gffInputPane.add(scrollPaneGffRef);
		
		refGFFList = new DefaultListModel<String>();
		final JList<String> listGFFRef = new JList<String>(refGFFList);
		listGFFRef.setForeground(Color.BLACK);
		listGFFRef.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		scrollPaneGffRef.setViewportView(listGFFRef);
		
		btnLoadRefGff.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				File[] newGFFFiles = FileSelection.getFiles(fc,"GFF");
				if(newGFFFiles != null) {
					for(int x = 0; x < newGFFFiles.length; x++) { 
						RefGFFFiles.add(newGFFFiles[x]);
						refGFFList.addElement(newGFFFiles[x].getName());
					}
				}
			}
		});
		gffInputPane.add(btnLoadRefGff);
		
		btnRemoveReGff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(listGFFRef.getSelectedIndex() > -1) {
					RefGFFFiles.remove(listGFFRef.getSelectedIndex());
					refGFFList.remove(listGFFRef.getSelectedIndex());
				}
			}
		});
		gffInputPane.add(btnRemoveReGff);

		//Initialize proper strandedness checkbox
		chckbxProperStrands = new JCheckBox("Require Proper Strand Direction");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxProperStrands, 6, SpringLayout.SOUTH, inputCards);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxProperStrands, 130, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, chckbxProperStrands, -130, SpringLayout.EAST, contentPane);
 		contentPane.add(chckbxProperStrands);

		//Initialize output directory
		btnOutputDirectory = new JButton("Output Directory");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnOutputDirectory, 175, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnOutputDirectory, -175, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutputDirectory, 6, SpringLayout.SOUTH, chckbxProperStrands);
		btnOutputDirectory.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		OUTPUT_PATH = FileSelection.getOutputDir(fc);
    			if(OUTPUT_PATH != null) {
    				lblDefaultToLocal.setText(OUTPUT_PATH.getAbsolutePath());
    			}
        	}
        });
		contentPane.add(btnOutputDirectory);
		
		lblCurrentOutput = new JLabel("Current Output:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrentOutput, 3, SpringLayout.SOUTH, btnOutputDirectory);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrentOutput, 5, SpringLayout.WEST, contentPane);
		lblCurrentOutput.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblCurrentOutput.setForeground(Color.BLACK);
		contentPane.add(lblCurrentOutput);		
		
		lblDefaultToLocal = new JLabel("Default to Local Directory");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 5, SpringLayout.SOUTH, lblCurrentOutput);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 15, SpringLayout.WEST, contentPane);
		lblDefaultToLocal.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblDefaultToLocal.setForeground(Color.BLACK);
		contentPane.add(lblDefaultToLocal);
		
		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.WEST, progressBar, -150, SpringLayout.EAST, progressBar);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, progressBar, -5, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -5, SpringLayout.EAST, contentPane);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
		btnCalculate = new JButton("Align");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnCalculate, -5, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnCalculate, 175, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnCalculate, -175, SpringLayout.EAST, contentPane);
		contentPane.add(btnCalculate);
		btnCalculate.setActionCommand("start");

		chckbxGzipOutput = new JCheckBox("Output GZIP");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxGzipOutput, 0, SpringLayout.NORTH, btnCalculate);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxGzipOutput, 31, SpringLayout.WEST, contentPane);
		contentPane.add(chckbxGzipOutput);
		
		rdbtnBed.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (rdbtnBed.isSelected()) {
					CardLayout cl = (CardLayout)(inputCards.getLayout());
					cl.show(inputCards, "bed");
				}
			}
		});
		rdbtnGff.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (rdbtnGff.isSelected()) {
					CardLayout cl = (CardLayout)(inputCards.getLayout());
					cl.show(inputCards, "gff");
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