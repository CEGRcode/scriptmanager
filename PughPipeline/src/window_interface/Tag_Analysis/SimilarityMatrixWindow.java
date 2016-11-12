package window_interface.Tag_Analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.SwingWorker;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import util.FileSelection;
import scripts.Tag_Analysis.AggregateData;

@SuppressWarnings("serial")
public class SimilarityMatrixWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	
	private File OUTPUT_PATH = null;
	final DefaultListModel<String> expList;
	ArrayList<File> SUMFiles = new ArrayList<File>();
	
	private JButton btnLoad;
	private JButton btnRemoveCDT;
	private JButton btnConvert;
	private JButton btnOutput;
	private JProgressBar progressBar;
	private JLabel lblCurrent;
	private JLabel lblDefaultToLocal;
	private JCheckBox chckbxMergeToOne;
	private JCheckBox chckbxHeader;
	private JComboBox<String> cmbMethod;

	public Task task;
	
	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException {
        	setProgress(0);
        	
        	AggregateData parse = new AggregateData(SUMFiles, OUTPUT_PATH, chckbxMergeToOne.isSelected(), chckbxHeader.isSelected(), cmbMethod.getSelectedIndex());
        	
        	parse.addPropertyChangeListener("file", new PropertyChangeListener() {
			    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
			    	int temp = (Integer) propertyChangeEvent.getNewValue();
			    	int percentComplete = (int)(((double)(temp) / (SUMFiles.size())) * 100);
		        	setProgress(percentComplete);
			     }
			 });
        	
    		parse.run();
        	
        	setProgress(100);
			JOptionPane.showMessageDialog(null, "Data Parsed");
        	return null;
        }
        
        public void done() {
        	massXable(contentPane, true);
            setCursor(null); //turn off the wait cursor
        }
	}
	
	public SimilarityMatrixWindow() {
		setTitle("Aggregate Data");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 330);
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
		final JList<String> listExp = new JList<>(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listExp);
		
		btnLoad = new JButton("Load Files");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, btnLoad);
		btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				File[] newCDTFiles = FileSelection.getGenericFiles(fc);
				if(newCDTFiles != null) {
					for(int x = 0; x < newCDTFiles.length; x++) { 
						SUMFiles.add(newCDTFiles[x]);
						expList.addElement(newCDTFiles[x].getName());
					}
				}
			}
		});
		contentPane.add(btnLoad);
		
		btnRemoveCDT = new JButton("Remove Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 0, SpringLayout.NORTH, btnRemoveCDT);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveCDT, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveCDT, -5, SpringLayout.EAST, contentPane);
		btnRemoveCDT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(listExp.getSelectedIndex() > -1) {
					SUMFiles.remove(listExp.getSelectedIndex());
					expList.remove(listExp.getSelectedIndex());
				}
			}
		});		
		contentPane.add(btnRemoveCDT);
		
		btnConvert = new JButton("Parse Matrix");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnConvert, 165, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnConvert, -165, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -95, SpringLayout.NORTH, btnConvert);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnConvert, 0, SpringLayout.SOUTH, contentPane);
		contentPane.add(btnConvert);
		
		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnConvert);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -5, SpringLayout.EAST, contentPane);
        progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
        btnConvert.setActionCommand("start");
        
        lblCurrent = new JLabel("Current Output:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrent, 68, SpringLayout.SOUTH, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrent, 5, SpringLayout.WEST, contentPane);
        lblCurrent.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblCurrent);
        
        lblDefaultToLocal = new JLabel("Default to Local Directory");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, lblCurrent);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, lblCurrent);
        lblDefaultToLocal.setBackground(Color.WHITE);
        contentPane.add(lblDefaultToLocal);
        
        btnOutput = new JButton("Output Directory");
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutput, 38, SpringLayout.SOUTH, scrollPane);
        btnOutput.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
    			OUTPUT_PATH = FileSelection.getOutputDir(fc);
    			if(OUTPUT_PATH != null) {
    				lblDefaultToLocal.setText(OUTPUT_PATH.getAbsolutePath());
    			}
        	}
        });
        sl_contentPane.putConstraint(SpringLayout.WEST, btnOutput, 150, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, btnOutput, -150, SpringLayout.EAST, contentPane);
        contentPane.add(btnOutput);
        
        chckbxMergeToOne = new JCheckBox("Merge to one file");
        sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxMergeToOne, 1, SpringLayout.NORTH, btnOutput);
        sl_contentPane.putConstraint(SpringLayout.WEST, chckbxMergeToOne, 4, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, chckbxMergeToOne, -6, SpringLayout.WEST, btnOutput);
        chckbxMergeToOne.setSelected(true);
        contentPane.add(chckbxMergeToOne);
        
        //String[] function = {"Sum", "Average", "Median", "Mode", "Min", "Max"};
        cmbMethod = new JComboBox<>(new DefaultComboBoxModel<>(new String[] {"Sum", "Average", "Median", "Mode", "Min", "Max","Positional Variance"}));
        sl_contentPane.putConstraint(SpringLayout.NORTH, cmbMethod, 6, SpringLayout.SOUTH, scrollPane);
        contentPane.add(cmbMethod);
        
        JLabel lblMathematicalFunction = new JLabel("Aggregation Method:");
        sl_contentPane.putConstraint(SpringLayout.WEST, cmbMethod, 6, SpringLayout.EAST, lblMathematicalFunction);
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblMathematicalFunction, 10, SpringLayout.SOUTH, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblMathematicalFunction, 0, SpringLayout.WEST, scrollPane);
        contentPane.add(lblMathematicalFunction);
        
        chckbxHeader = new JCheckBox("Data has headers");
        sl_contentPane.putConstraint(SpringLayout.SOUTH, chckbxHeader, 0, SpringLayout.SOUTH, cmbMethod);
        sl_contentPane.putConstraint(SpringLayout.EAST, chckbxHeader, 0, SpringLayout.EAST, scrollPane);
        contentPane.add(chckbxHeader);
        btnConvert.addActionListener(this);
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


	
