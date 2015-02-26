package window_interface.BAM_Manipulation;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JLabel;

import scripts.BAM_Manipulation.BAIIndexer;
import scripts.BAM_Manipulation.MergeSamFiles;
import util.FileSelection;

@SuppressWarnings("serial")
public class MergeBAMWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));
	
	final DefaultListModel expList;
	List<File> BAMFiles = new ArrayList<File>();
    private File OUTPUT = null;
	private File OUTPUT_PATH = null;

	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnMerge;
	private JButton btnOutput;
	private JTextField txtOutput;
	private JCheckBox chckbxUseMultipleCpus;
	private JCheckBox chckbxGenerateBaiindex;
	private JProgressBar progressBar;
	private JLabel lblDefaultToLocal;
	
	public Task task;

	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws Exception {
        	setProgress(0);
        	if(OUTPUT_PATH != null) { OUTPUT = new File(OUTPUT_PATH.getCanonicalPath() + File.separator + txtOutput.getText()); }
     	    else { OUTPUT = new File(txtOutput.getText()); }
        	MergeSamFiles merge = new MergeSamFiles(BAMFiles, OUTPUT, chckbxUseMultipleCpus.isSelected());
    		merge.run();

    		if(chckbxGenerateBaiindex.isSelected()) {
				BAIIndexer.generateIndex(OUTPUT);
			}	
			JOptionPane.showMessageDialog(null, "Merging Complete");
        	return null;
        }
        
        public void done() {
        	setProgress(100);
    		massXable(contentPane, true);
            setCursor(null); //turn off the wait cursor
        }
	}
	
	public MergeBAMWindow() {
		setTitle("BAM File Replicate Merger");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 350);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		

		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -5, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
      	expList = new DefaultListModel();
		final JList listExp = new JList(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listExp);
		
		btnLoad = new JButton("Load BAM Files");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, btnLoad);
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
		
		btnRemoveBam = new JButton("Remove BAM");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 0, SpringLayout.NORTH, btnRemoveBam);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveBam, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBam, -5, SpringLayout.EAST, contentPane);
		btnRemoveBam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(listExp.getSelectedIndex() > -1) {
					BAMFiles.remove(listExp.getSelectedIndex());
					expList.remove(listExp.getSelectedIndex());
				}
			}
		});		
		contentPane.add(btnRemoveBam);
		
		btnMerge = new JButton("Merge");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnMerge, 167, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnMerge, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnMerge, -175, SpringLayout.EAST, contentPane);
		contentPane.add(btnMerge);
        
		btnMerge.setActionCommand("start");
        btnMerge.addActionListener(this);
        
		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnMerge);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -5, SpringLayout.EAST, contentPane);
        progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
        txtOutput = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -65, SpringLayout.NORTH, txtOutput);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtOutput, -5, SpringLayout.EAST, contentPane);
        txtOutput.setText("merged_BAM.bam");
        contentPane.add(txtOutput);
        txtOutput.setColumns(10);
        
        JLabel lblOutputFileName = new JLabel("Output File Name:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtOutput, -2, SpringLayout.NORTH, lblOutputFileName);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtOutput, 6, SpringLayout.EAST, lblOutputFileName);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblOutputFileName, 5, SpringLayout.WEST, contentPane);
        contentPane.add(lblOutputFileName);
        
        chckbxUseMultipleCpus = new JCheckBox("Use Multiple CPU's");
        sl_contentPane.putConstraint(SpringLayout.SOUTH, chckbxUseMultipleCpus, -4, SpringLayout.NORTH, progressBar);
        sl_contentPane.putConstraint(SpringLayout.EAST, chckbxUseMultipleCpus, -5, SpringLayout.EAST, contentPane);
        chckbxUseMultipleCpus.setToolTipText("Increases Merging Speed on Computers with Multiple CPUs");
        contentPane.add(chckbxUseMultipleCpus);
        
        chckbxGenerateBaiindex = new JCheckBox("Generate BAI-Index");
        sl_contentPane.putConstraint(SpringLayout.WEST, chckbxGenerateBaiindex, 5, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblOutputFileName, -6, SpringLayout.NORTH, chckbxGenerateBaiindex);
        sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxGenerateBaiindex, 0, SpringLayout.NORTH, chckbxUseMultipleCpus);
        chckbxGenerateBaiindex.setSelected(true);
        contentPane.add(chckbxGenerateBaiindex);
               
        JLabel lblCurrent = new JLabel("Current Output:");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrent, 0, SpringLayout.WEST, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblCurrent, -15, SpringLayout.NORTH, lblOutputFileName);
        contentPane.add(lblCurrent);
        
        lblDefaultToLocal = new JLabel("Default to Local Directory");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 0, SpringLayout.NORTH, lblCurrent);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, lblCurrent);
        lblDefaultToLocal.setFont(new Font("Dialog", Font.PLAIN, 12));
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
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutput, 5, SpringLayout.SOUTH, scrollPane);
        contentPane.add(btnOutput);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		massXable(contentPane, false);
    	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
	}
	
	@Override
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