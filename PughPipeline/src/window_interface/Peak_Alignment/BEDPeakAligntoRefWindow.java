package window_interface.Peak_Alignment;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import scripts.Peak_Alignment.BEDPeakAligntoRef;
import util.FileSelection;

@SuppressWarnings("serial")
public class BEDPeakAligntoRefWindow extends JFrame implements ActionListener, PropertyChangeListener {

	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));
	

	/**
	 * Create the frame.
	 */
	
	final DefaultListModel expList1;
	final DefaultListModel expList2;
	ArrayList<File> PeakFiles = new ArrayList<File>();
	ArrayList<File> RefFiles = new ArrayList<File>();
	private File OUTPUT_PATH = null;
	
	private JButton btnLoadPeakBed;
	private JButton btnOutputDirectory;
	private JButton btnRemoveBedFile;
	private JLabel lblCurrentOutput;
	private JLabel lblDefaultToLocal;
	private JButton btnLoad;
	private JButton btnCalculate;
	private JProgressBar progressBar;
	private int counter = 0;
	
public Task task;
	
	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException, InterruptedException {
        	try {	
        		if(PeakFiles.size() < 1 || RefFiles.size() < 1) {
        			JOptionPane.showMessageDialog(null, "No BED Files Loaded!!!");
        		} else {
        			setProgress(0);
        			BEDPeakAligntoRef align;
    				for(int r = 0; r < RefFiles.size(); r++)
    				{
    					for(int p=0; p < PeakFiles.size(); p++)
    					{
    						align = new BEDPeakAligntoRef(RefFiles.get(r), PeakFiles.get(p), OUTPUT_PATH.getCanonicalPath());	
        	        			align.setVisible(true);
        	        			align.run();
        	        			counter++;
						int percentComplete = (int)(((double)(counter) / (PeakFiles.size()*RefFiles.size())) * 100);
					    setProgress(percentComplete);	
    					}	
    			}
    				JOptionPane.showMessageDialog(null, "Alignment Complete");
					
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
	
	public BEDPeakAligntoRefWindow() {
		setTitle("BED Peaks Alignment");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 587, 607);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		btnLoadPeakBed = new JButton("Load Peak BED");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoadPeakBed, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoadPeakBed, 5, SpringLayout.WEST, contentPane);
		btnLoadPeakBed.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
					File[] newBEDFiles = FileSelection.getBEDFiles(fc);
					if(newBEDFiles != null) {
						for(int x = 0; x < newBEDFiles.length; x++) { 
							PeakFiles.add(newBEDFiles[x]);
							expList1.addElement(newBEDFiles[x].getName());
						}
					}
				}
			});
		contentPane.add(btnLoadPeakBed);
		
		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 12, SpringLayout.SOUTH, btnLoadPeakBed);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -5, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);
		
		expList1 = new DefaultListModel();
		final JList listpeak = new JList(expList1);
		listpeak.setForeground(Color.BLACK);
		listpeak.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		scrollPane.setViewportView(listpeak);
		
		btnOutputDirectory = new JButton("Output Directory");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnOutputDirectory, 0, SpringLayout.WEST, btnLoadPeakBed);
		btnOutputDirectory.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		OUTPUT_PATH = FileSelection.getOutputDir(fc);
    			if(OUTPUT_PATH != null) {
    				lblDefaultToLocal.setText(OUTPUT_PATH.getAbsolutePath());
    			}
        	}
        });
		contentPane.add(btnOutputDirectory);
		
		btnRemoveBedFile = new JButton("Remove BED");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnRemoveBedFile, 227, SpringLayout.WEST, contentPane);
		btnRemoveBedFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(listpeak.getSelectedIndex() > -1) {
					PeakFiles.remove(listpeak.getSelectedIndex());
					expList1.remove(listpeak.getSelectedIndex());
				}
			}
		});	
		contentPane.add(btnRemoveBedFile);
		
		lblCurrentOutput = new JLabel("Current Output ");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrentOutput, 4, SpringLayout.NORTH, btnOutputDirectory);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrentOutput, 12, SpringLayout.EAST, btnOutputDirectory);
		lblCurrentOutput.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblCurrentOutput.setForeground(Color.BLACK);
		contentPane.add(lblCurrentOutput);
		
		lblDefaultToLocal = new JLabel("Default to Local Directory");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 5, SpringLayout.NORTH, btnOutputDirectory);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, lblCurrentOutput);
		lblDefaultToLocal.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblDefaultToLocal.setForeground(Color.BLACK);
		contentPane.add(lblDefaultToLocal);
		
		btnLoad = new JButton("Load Reference BED");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -29, SpringLayout.NORTH, btnLoad);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 0, SpringLayout.WEST, btnLoadPeakBed);
		btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				File[] newBEDFiles = FileSelection.getBEDFiles(fc);
				if(newBEDFiles != null) {
					for(int x = 0; x < newBEDFiles.length; x++) { 
						RefFiles.add(newBEDFiles[x]);
						expList2.addElement(newBEDFiles[x].getName());
					}
				}
			}
		});
		contentPane.add(btnLoad);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnLoad, -17, SpringLayout.NORTH, scrollPane_1);
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane_1, 256, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane_1, -181, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveBedFile, 17, SpringLayout.SOUTH, scrollPane_1);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane_1, 0, SpringLayout.WEST, btnLoadPeakBed);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane_1, -5, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane_1);
		
		expList2 = new DefaultListModel();
		final JList listref = new JList(expList2);
		listref.setForeground(Color.BLACK);
		listref.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		scrollPane_1.setViewportView(listref);
		
		btnRemoveBedFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(listref.getSelectedIndex() > -1) {
					RefFiles.remove(listref.getSelectedIndex());
					expList2.remove(listref.getSelectedIndex());
				}
			}
		});	
		
		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnOutputDirectory, -58, SpringLayout.NORTH, progressBar);
		sl_contentPane.putConstraint(SpringLayout.WEST, progressBar, 0, SpringLayout.WEST, btnLoadPeakBed);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, progressBar, -10, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -78, SpringLayout.EAST, scrollPane);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
		btnCalculate = new JButton("Align");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnCalculate, 6, SpringLayout.EAST, progressBar);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnCalculate, -10, SpringLayout.SOUTH, contentPane);
		
		contentPane.add(btnCalculate);
		
		btnCalculate.setActionCommand("start");
		
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
