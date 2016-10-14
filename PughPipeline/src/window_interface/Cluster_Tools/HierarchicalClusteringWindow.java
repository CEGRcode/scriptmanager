package window_interface.Cluster_Tools;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.JLabel;
import javax.swing.JCheckBox;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import scripts.Cluster_Scripts.HierarchicalCluster;
import util.FileSelection;

@SuppressWarnings("serial")
public class HierarchicalClusteringWindow extends JFrame implements ActionListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	
	private File OUTPUT_PATH = null;
	private File Matrix_File = null;
	
	private JButton btnLoad;
	private JButton btnCluster;
	public Task task;
	private JLabel lblMatrixFileName;
	private JLabel lblCurrent;
	private JLabel lblDefaultToLocal;
	private JButton btnOutput;
	
	private JCheckBox chckbxOutputData; 

	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException {
        	HierarchicalCluster calculate = new HierarchicalCluster(Matrix_File, OUTPUT_PATH);
        	calculate.execute();
        	return null;
        }
        
        public void done() {
        	massXable(contentPane, true);
            setCursor(null); //turn off the wait cursor
        }
	}
	
	public HierarchicalClusteringWindow() {
		setTitle("Hierarchical Clustering");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 600, 350);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
			
		btnLoad = new JButton("Load Matrix File");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 10, SpringLayout.WEST, contentPane);
		btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	File newMatrixFile = FileSelection.getGenericFile(fc, false);
				if(newMatrixFile != null) {
					Matrix_File = newMatrixFile;
					lblMatrixFileName.setText(Matrix_File.getName());
				}
			}
		});
		contentPane.add(btnLoad);
		
		btnCluster = new JButton("Cluster");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnCluster, 167, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnCluster, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnCluster, -175, SpringLayout.EAST, contentPane);
		contentPane.add(btnCluster);	
        btnCluster.setActionCommand("start");
        btnCluster.addActionListener(this);

        lblCurrent = new JLabel("Current Output:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrent, 230, SpringLayout.SOUTH, btnLoad);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrent, 5, SpringLayout.WEST, contentPane);
        lblCurrent.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblCurrent);
        
        lblDefaultToLocal = new JLabel("Default to Local Directory");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, lblCurrent);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, lblCurrent);
        lblDefaultToLocal.setBackground(Color.WHITE);
        contentPane.add(lblDefaultToLocal);
        
        btnOutput = new JButton("Output Directory");
        sl_contentPane.putConstraint(SpringLayout.WEST, btnOutput, 200, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, btnOutput, -6, SpringLayout.NORTH, lblDefaultToLocal);
        sl_contentPane.putConstraint(SpringLayout.EAST, btnOutput, -200, SpringLayout.EAST, contentPane);
        btnOutput.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
    			OUTPUT_PATH = FileSelection.getOutputDir(fc);
    			if(OUTPUT_PATH != null) {
    				lblDefaultToLocal.setText(OUTPUT_PATH.getAbsolutePath());
    			}
        	}
        });
        contentPane.add(btnOutput);
        
        chckbxOutputData = new JCheckBox("Output Data");
        sl_contentPane.putConstraint(SpringLayout.WEST, chckbxOutputData, 0, SpringLayout.WEST, lblCurrent);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, chckbxOutputData, -6, SpringLayout.NORTH, lblCurrent);
        chckbxOutputData.setSelected(true);
        contentPane.add(chckbxOutputData);
        
        chckbxOutputData.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
			        if(chckbxOutputData.isSelected()) {
			        	btnOutput.setEnabled(true);
			        	lblDefaultToLocal.setEnabled(true);
			        	lblCurrent.setEnabled(true);
			        } else {
			        	btnOutput.setEnabled(false);
			        	lblDefaultToLocal.setEnabled(false);
			        	lblCurrent.setEnabled(false);
			        }
			      }
			    });
        
        
        lblMatrixFileName = new JLabel("No Matrix File Loaded");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblMatrixFileName, 6, SpringLayout.EAST, btnLoad);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblMatrixFileName, -5, SpringLayout.SOUTH, btnLoad);
        contentPane.add(lblMatrixFileName);
        
        JLabel lblDendrogramSimilarityMetric = new JLabel("Clustering Method:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblDendrogramSimilarityMetric, 52, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblDendrogramSimilarityMetric, 317, SpringLayout.WEST, contentPane);
        contentPane.add(lblDendrogramSimilarityMetric);
                
        JCheckBox chckbxClusterRows = new JCheckBox("Cluster Rows");
        sl_contentPane.putConstraint(SpringLayout.SOUTH, chckbxClusterRows, -207, SpringLayout.SOUTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, chckbxClusterRows, 0, SpringLayout.WEST, btnLoad);
        contentPane.add(chckbxClusterRows);
        
        JCheckBox chckbxClusterColumns = new JCheckBox("Cluster Columns");
        sl_contentPane.putConstraint(SpringLayout.WEST, chckbxClusterColumns, 0, SpringLayout.WEST, btnLoad);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, chckbxClusterColumns, -41, SpringLayout.NORTH, chckbxOutputData);
        contentPane.add(chckbxClusterColumns);
                
        JComboBox<String> cmbRowCluster = new JComboBox<>();
        sl_contentPane.putConstraint(SpringLayout.WEST, cmbRowCluster, 325, SpringLayout.EAST, chckbxClusterRows);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, cmbRowCluster, 0, SpringLayout.SOUTH, chckbxClusterRows);
        contentPane.add(cmbRowCluster);

        JComboBox<String> cmbColCluster = new JComboBox<>();
        sl_contentPane.putConstraint(SpringLayout.WEST, cmbColCluster, 0, SpringLayout.WEST, cmbRowCluster);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, cmbColCluster, 0, SpringLayout.SOUTH, chckbxClusterColumns);
        contentPane.add(cmbColCluster);
        
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		massXable(contentPane, false);
    	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        task = new Task();
        task.execute();
	}
		
	public void massXable(Container con, boolean status) {
		for(Component c : con.getComponents()) {
			c.setEnabled(status);
			if(c instanceof Container) { massXable((Container)c, status); }
		}
		if(status) {
			if(!chckbxOutputData.isSelected()) {
				btnOutput.setEnabled(false);
	        	lblDefaultToLocal.setEnabled(false);
	        	lblCurrent.setEnabled(false);
			}
		}
	}
}


	
