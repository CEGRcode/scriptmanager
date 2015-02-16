package window_interface.BAM_Manipulation;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
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

import picard.MergeSamFiles;
import util.FileSelection;


@SuppressWarnings("serial")
public class MergeBAMWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));
	
	final DefaultListModel expList;
	List<File> BAMFiles = new ArrayList<File>();
    private File OUTPUT = null;

	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnMerge;
	private JTextField txtOutput;
	private JCheckBox chckbxUseMultipleCpus;
	private JCheckBox chckbxGenerateBaiindex;

	private JProgressBar progressBar;
	public Task task;

	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws Exception {
        	setProgress(0);
        	OUTPUT = new File(txtOutput.getText());
			mergeBAM();
			if(chckbxGenerateBaiindex.isSelected()) {
				BAIIndexerWindow.generateIndex(OUTPUT);
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

		setBounds(125, 125, 450, 310);
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
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, btnLoad);
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
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, 0, SpringLayout.EAST, scrollPane);
        progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
        txtOutput = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.NORTH, txtOutput);
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
	
	protected void mergeBAM() {
		MergeSamFiles merge = new MergeSamFiles(BAMFiles, OUTPUT, chckbxUseMultipleCpus.isSelected());
		merge.run();
    }
}