package scriptmanager.window_interface.Peak_Analysis;

import java.awt.event.ActionListener;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import scriptmanager.cli.Peak_Analysis.BEDPeakAligntoRefCLI;
import scriptmanager.objects.LogItem;
import scriptmanager.util.FileSelection;

@SuppressWarnings("serial")
public class BEDPeakAligntoRefWindow extends JFrame implements ActionListener, PropertyChangeListener {

	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));
	
	final DefaultListModel<String> peakList;
	final DefaultListModel<String> refList;
	ArrayList<File> PeakFiles = new ArrayList<File>();
	ArrayList<File> RefFiles = new ArrayList<File>();
	private File OUT_DIR = null;
	
	private JButton btnLoadPeakBed;
	private JButton btnRemoveBedFile;
	private JButton btnLoadRefBed;
	private JButton btnRemoveRefBed;
	private JButton btnOutputDirectory;
	private JButton btnCalculate;
	private JLabel lblCurrentOutput;
	private JLabel lblDefaultToLocal;
	private JProgressBar progressBar;
	
	public Task task;
	
	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() {
        	try {
        		if(PeakFiles.size() < 1 || RefFiles.size() < 1) {
        			JOptionPane.showMessageDialog(null, "No BED Files Loaded!!!");
        		} else {
        			setProgress(0);
        			int counter = 0;
					for (int r = 0; r < RefFiles.size(); r++) {
						for (int p=0; p < PeakFiles.size(); p++) {
							// Execute script
							BEDPeakAligntoRefOutput output_obj = new BEDPeakAligntoRefOutput(RefFiles.get(r), PeakFiles.get(p), OUT_DIR);
							output_obj.addPropertyChangeListener("log", new PropertyChangeListener() {
								public void propertyChange(PropertyChangeEvent evt) {
									firePropertyChange("log", evt.getOldValue(), evt.getNewValue());
								}
							});
							output_obj.setVisible(true);
							output_obj.run();
							// Update progress
							counter++;
							int percentComplete = (int) (((double) (counter) / (PeakFiles.size() * RefFiles.size())) * 100);
							setProgress(percentComplete);
						}
					}
					setProgress(100);
					JOptionPane.showMessageDialog(null, "Alignment Complete");
				}
			} catch (NumberFormatException nfe){
				JOptionPane.showMessageDialog(null, "Invalid Input in Fields!!!");
			} catch (InterruptedException ie) {
				JOptionPane.showMessageDialog(null, ie.getMessage());
			} catch (IOException ioe) {
				ioe.printStackTrace();
				JOptionPane.showMessageDialog(null, "I/O issues: " + ioe.getMessage());
			}
			setProgress(100);
			return null;
        }
        
        public void done() {
        	massXable(contentPane, true);
            setCursor(null); //turn off the wait cursor
        }
	}
	
	public BEDPeakAligntoRefWindow() {
		setTitle("BED Peaks Alignment");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 505);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		//Initialize buttons and list for peak BED files
		btnLoadPeakBed = new JButton("Load Peak BED");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoadPeakBed, 5, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoadPeakBed, 5, SpringLayout.WEST, contentPane);
		
		btnRemoveBedFile = new JButton("Remove Peak BED");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveBedFile, 5, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBedFile, -5, SpringLayout.EAST, contentPane);

		JScrollPane scrollPane_Peak = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane_Peak, 12, SpringLayout.SOUTH, btnLoadPeakBed);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane_Peak, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane_Peak, -5, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane_Peak);
		
		peakList = new DefaultListModel<String>();
		final JList<String> listPeak = new JList<String>(peakList);
		listPeak.setForeground(Color.BLACK);
		listPeak.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		scrollPane_Peak.setViewportView(listPeak);
		
		btnLoadPeakBed.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				File[] newBEDFiles = FileSelection.getFiles(fc,"bed");
				if(newBEDFiles != null) {
					for(int x = 0; x < newBEDFiles.length; x++) { 
						PeakFiles.add(newBEDFiles[x]);
						peakList.addElement(newBEDFiles[x].getName());
					}
				}
			}
		});
		contentPane.add(btnLoadPeakBed);
		
		btnRemoveBedFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(listPeak.getSelectedIndex() > -1) {
					PeakFiles.remove(listPeak.getSelectedIndex());
					peakList.remove(listPeak.getSelectedIndex());
				}
			}
		});	
		contentPane.add(btnRemoveBedFile);
		
		//Initialize buttons and list for reference BED files
		btnLoadRefBed = new JButton("Load Reference BED");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoadRefBed, 10, SpringLayout.SOUTH, scrollPane_Peak);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoadRefBed, 5, SpringLayout.WEST, contentPane);
		
		btnRemoveRefBed = new JButton("Remove Reference BED");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveRefBed, 10, SpringLayout.SOUTH, scrollPane_Peak);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveRefBed, -5, SpringLayout.EAST, contentPane);
		
		JScrollPane scrollPane_Ref = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane_Ref, 10, SpringLayout.SOUTH, btnLoadRefBed);
		//sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane_Ref, -100, SpringLayout.SOUTH, btnLoadRefBed);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane_Ref, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane_Ref, -5, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane_Ref);
		
		refList = new DefaultListModel<String>();
		final JList<String> listRef = new JList<String>(refList);
		listRef.setForeground(Color.BLACK);
		listRef.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		scrollPane_Ref.setViewportView(listRef);
		
		btnLoadRefBed.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				File[] newBEDFiles = FileSelection.getFiles(fc,"bed");
				if(newBEDFiles != null) {
					for(int x = 0; x < newBEDFiles.length; x++) { 
						RefFiles.add(newBEDFiles[x]);
						refList.addElement(newBEDFiles[x].getName());
					}
				}
			}
		});
		contentPane.add(btnLoadRefBed);
		
		btnRemoveRefBed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(listRef.getSelectedIndex() > -1) {
					RefFiles.remove(listRef.getSelectedIndex());
					refList.remove(listRef.getSelectedIndex());
				}
			}
		});
		contentPane.add(btnRemoveRefBed);

		//Initialize output directory
		btnOutputDirectory = new JButton("Output Directory");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnOutputDirectory, 175, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnOutputDirectory, -175, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutputDirectory, 10, SpringLayout.SOUTH, scrollPane_Ref);
		btnOutputDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OUT_DIR = FileSelection.getOutputDir(fc);
				if(OUT_DIR != null) {
					lblDefaultToLocal.setText(OUT_DIR.getAbsolutePath());
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
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		} else if ("log" == evt.getPropertyName()) {
			firePropertyChange("log", evt.getOldValue(), evt.getNewValue());
		}
	}

	public void massXable(Container con, boolean status) {
		for(Component c : con.getComponents()) {
			c.setEnabled(status);
			if(c instanceof Container) { massXable((Container)c, status); }
		}
	}
}