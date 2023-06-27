package scriptmanager.window_interface.BAM_Manipulation;

import java.awt.Component;
import java.awt.Container;
import java.awt.Color;
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
import javax.swing.JCheckBox;
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

import scriptmanager.util.FileSelection;
import scriptmanager.scripts.BAM_Manipulation.BAIIndexerWrapper;
import scriptmanager.scripts.BAM_Manipulation.BAMMarkDuplicates;

@SuppressWarnings("serial")
public class BAMMarkDupWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));
	
	final DefaultListModel<String> expList;
	private File OUTPUT_PATH = null;
	List<File> BAMFiles = new ArrayList<File>();

	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnSort;

	private JProgressBar progressBar;
	public Task task;
	private JButton btnOutput;
	private JLabel label;
	private JLabel lblDefaultToLocal;
	private JCheckBox chckbxGenerateBaiIndex;
	private JCheckBox chckbxRemoveDuplicates;

	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws Exception {
        	setProgress(0);
        	for(int x = 0; x < BAMFiles.size(); x++) {
        		String[] NAME = BAMFiles.get(x).getName().split("\\.");
        	    File OUTPUT = null;
        	    File METRICS = null;
        	    if(OUTPUT_PATH != null) {
        	    	OUTPUT = new File(OUTPUT_PATH.getCanonicalPath() + File.separator + NAME[0] + "_dedup.bam");
        	    	METRICS = new File(OUTPUT_PATH.getCanonicalPath() + File.separator + NAME[0] + "_dedup.metrics");
        	    } else {
        	    	OUTPUT = new File(NAME[0] + "_dedup.bam");
        	    	METRICS = new File(NAME[0] + "_dedup.metrics");
        	    }
        	    
        	    BAMMarkDuplicates dedup = new BAMMarkDuplicates(BAMFiles.get(x), chckbxRemoveDuplicates.isSelected(), OUTPUT, METRICS);
        	    dedup.run();
        	    
        	    if(chckbxGenerateBaiIndex.isSelected()) { BAIIndexerWrapper.generateIndex(OUTPUT);	}
        	    
        	    int percentComplete = (int)(((double)(x + 1) / BAMFiles.size()) * 100);
        		setProgress(percentComplete);
        	}
        	setProgress(100);
			JOptionPane.showMessageDialog(null, "Mark Duplicates Complete");
        	return null;
        }
        
        public void done() {
        	massXable(contentPane, true);
            setCursor(null); //turn off the wait cursor
        }
	}
	
	public BAMMarkDupWindow() {
		setTitle("BAM MarkDuplicates (picard)");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 360);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		

		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -5, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
      	expList = new DefaultListModel<String>();
		final JList<String> listExp = new JList<String>(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listExp);
		
		btnLoad = new JButton("Load BAM Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, btnLoad);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 5, SpringLayout.WEST, contentPane);
		btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				File[] newBAMFiles = FileSelection.getFiles(fc,"bam");
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
		
		btnSort = new JButton("Mark Duplicates");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnSort, 160, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnSort, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnSort, -160, SpringLayout.EAST, contentPane);
		contentPane.add(btnSort);
        
		btnSort.setActionCommand("start");
        btnSort.addActionListener(this);
        
		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnSort);
		sl_contentPane.putConstraint(SpringLayout.WEST, progressBar, 0, SpringLayout.WEST, btnRemoveBam);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -10, SpringLayout.EAST, contentPane);
        progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
		btnOutput = new JButton("Output Directory");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -72, SpringLayout.NORTH, btnOutput);
		btnOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OUTPUT_PATH = FileSelection.getOutputDir(fc);
				if(OUTPUT_PATH != null) {
					lblDefaultToLocal.setText(OUTPUT_PATH.getAbsolutePath());
				}
			}
		});
		sl_contentPane.putConstraint(SpringLayout.WEST, btnOutput, 146, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnOutput, -50, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnOutput, -146, SpringLayout.EAST, contentPane);
		contentPane.add(btnOutput);
		
		label = new JLabel("Current Output:");
		sl_contentPane.putConstraint(SpringLayout.WEST, label, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, label, -30, SpringLayout.SOUTH, contentPane);
		label.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(label);
		
		lblDefaultToLocal = new JLabel("Default to Local Directory");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, label);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, label);
		lblDefaultToLocal.setBackground(Color.WHITE);
		contentPane.add(lblDefaultToLocal);
		
		chckbxGenerateBaiIndex = new JCheckBox("Generate BAI Index for new BAM file");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxGenerateBaiIndex, 40, SpringLayout.SOUTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxGenerateBaiIndex, 5, SpringLayout.WEST, contentPane);
		chckbxGenerateBaiIndex.setSelected(true);
		contentPane.add(chckbxGenerateBaiIndex);
		
		chckbxRemoveDuplicates = new JCheckBox("Remove Duplicates");
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxRemoveDuplicates, 0, SpringLayout.WEST, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, chckbxRemoveDuplicates, -6, SpringLayout.NORTH, chckbxGenerateBaiIndex);
		chckbxRemoveDuplicates.setSelected(true);
		contentPane.add(chckbxRemoveDuplicates);
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