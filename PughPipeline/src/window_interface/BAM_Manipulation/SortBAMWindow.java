package window_interface.BAM_Manipulation;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.util.IOUtil;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Color;
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
import javax.swing.JLabel;

import util.FileSelection;

@SuppressWarnings("serial")
public class SortBAMWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));
	
	final DefaultListModel expList;
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

	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws Exception {
        	setProgress(0);
        	for(int x = 0; x < BAMFiles.size(); x++) {
        		String[] NAME = BAMFiles.get(x).getName().split("\\.");
        	    File OUTPUT = null;
        	    if(OUTPUT_PATH != null) { OUTPUT = new File(OUTPUT_PATH.getCanonicalPath() + File.separator + NAME[0] + "_sorted.bam"); }
        	    else { OUTPUT = new File(NAME[0] + "_sorted.bam"); }
        	    sort(BAMFiles.get(x), OUTPUT);
        		int percentComplete = (int)(((double)(x + 1) / BAMFiles.size()) * 100);
        		setProgress(percentComplete);
        	}
        	setProgress(100);
			JOptionPane.showMessageDialog(null, "Sorting Complete");
        	return null;
        }
        
        public void done() {
        	massXable(contentPane, true);
            setCursor(null); //turn off the wait cursor
        }
	}
	
	public SortBAMWindow() {
		setTitle("BAM File Sort");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 300);
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
		
		btnSort = new JButton("Sort");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnSort, 167, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnSort, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnSort, -175, SpringLayout.EAST, contentPane);
		contentPane.add(btnSort);
        
		btnSort.setActionCommand("start");
        btnSort.addActionListener(this);
        
		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnSort);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -10, SpringLayout.EAST, contentPane);
        progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
		btnOutput = new JButton("Output Directory");
		btnOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OUTPUT_PATH = FileSelection.getOutputDir(fc);
				if(OUTPUT_PATH != null) {
					lblDefaultToLocal.setText(OUTPUT_PATH.getAbsolutePath());
				}
			}
		});
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutput, 193, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -3, SpringLayout.NORTH, btnOutput);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnOutput, 146, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnOutput, -50, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnOutput, -146, SpringLayout.EAST, contentPane);
		contentPane.add(btnOutput);
		
		label = new JLabel("Current Output:");
		sl_contentPane.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, label, -30, SpringLayout.SOUTH, contentPane);
		label.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(label);
		
		lblDefaultToLocal = new JLabel("Default to Local Directory");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, label);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, label);
		lblDefaultToLocal.setBackground(Color.WHITE);
		contentPane.add(lblDefaultToLocal);
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
	
	public void sort(File INPUT, File OUTPUT) {
		IOUtil.assertFileIsReadable(INPUT);
        IOUtil.assertFileIsWritable(OUTPUT);
        final SamReader reader = SamReaderFactory.makeDefault().open(INPUT);
        reader.getFileHeader().setSortOrder(SAMFileHeader.SortOrder.coordinate);
        final SAMFileWriter writer = new SAMFileWriterFactory().makeSAMOrBAMWriter(reader.getFileHeader(), false, OUTPUT);
        for (final SAMRecord rec: reader) {
            writer.addAlignment(rec);
        }
        writer.close();
	}

}