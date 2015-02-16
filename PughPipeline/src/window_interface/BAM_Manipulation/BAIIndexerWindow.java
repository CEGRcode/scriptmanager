package window_interface.BAM_Manipulation;

import htsjdk.samtools.BAMIndexer;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
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
import javax.swing.JProgressBar;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import util.FileSelection;


@SuppressWarnings("serial")
public class BAIIndexerWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	
	final DefaultListModel expList;
	Vector<File> BAMFiles = new Vector<File>();
	
	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnIndex;

	private JProgressBar progressBar;
	public Task task;
	
	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException {
        	setProgress(0);
        	for(int x = 0; x < BAMFiles.size(); x++) {
				generateIndex(BAMFiles.get(x));
				int percentComplete = (int)(((double)(x + 1) / BAMFiles.size()) * 100);
        		setProgress(percentComplete);
        	}
        	setProgress(100);
			JOptionPane.showMessageDialog(null, "Indexing Complete");
        	return null;
        }
        
        public void done() {
        	massXable(contentPane, true);
            setCursor(null); //turn off the wait cursor
        }
	}
	
	public BAIIndexerWindow() {
		setTitle("BAM File Indexer");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 260);
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
		
		btnIndex = new JButton("Index");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.NORTH, btnIndex);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnIndex, 167, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnIndex, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnIndex, -175, SpringLayout.EAST, contentPane);
		contentPane.add(btnIndex);
		
		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnIndex);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, 0, SpringLayout.EAST, scrollPane);
        progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
        btnIndex.setActionCommand("start");
        btnIndex.addActionListener(this);
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
    
	public static File generateIndex(File input) throws IOException {
		SamReaderFactory factory = SamReaderFactory.makeDefault().enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS, SamReaderFactory.Option.CACHE_FILE_BASED_INDEXES, SamReaderFactory.Option.VALIDATE_CRC_CHECKSUMS).validationStringency(ValidationStringency.LENIENT);
		File retVal = null;
		System.out.println("Generating New Index File...");
		try{
			String output = input.getCanonicalPath() + ".bai";
			retVal = new File(output);
			//Generate index
			SamReader inputSam = factory.open(input);
			BAMIndexer bamindex = new BAMIndexer(retVal, inputSam.getFileHeader());
			int counter = 0;
			for(SAMRecord record : inputSam) {
				if(counter % 1000000 == 0) {
					System.out.print("Tags processed: " + NumberFormat.getIntegerInstance().format(counter) + "\r");
					System.out.flush();
				}
				counter++;
				bamindex.processAlignment(record);
			}
			bamindex.finish();
			inputSam.close();
			System.out.println("\nIndex File Generated");
			return retVal;
		}
		catch(htsjdk.samtools.SAMException exception){
			System.out.println(exception.getMessage());
			JOptionPane.showMessageDialog(null, exception.getMessage());
			retVal = null;
		}
		return retVal;
	}
}


	
