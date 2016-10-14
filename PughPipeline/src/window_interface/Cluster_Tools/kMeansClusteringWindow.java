package window_interface.Cluster_Tools;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import scripts.Cluster_Scripts.kMeansCluster;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import util.FileSelection;

@SuppressWarnings("serial")
public class kMeansClusteringWindow extends JFrame implements ActionListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	
	private File OUTPUT_PATH = null;
	private File Matrix_File = null;
	
	private JButton btnLoad;
	private JButton btnCluster;
	public Task task;
	private JCheckBox chckbxClusterRows;
	private JCheckBox chckbxClusterColumns;
	private JComboBox<String> cmbRowCluster;
	private JComboBox<String> cmbColCluster;
	private JLabel lblFileName;
	private JLabel lblCurrent;
	private JButton btnOutput;
	private JLabel lblDefaultToLocal;
	private JLabel lblClusterNumber;
	private JLabel lblIterations;
	private JTextField txtRowK;
	private JTextField txtColK;
	private JTextField txtRowI;
	private JTextField txtColI;

	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException {
        	try {
        		if(Matrix_File == null) {
	        		JOptionPane.showMessageDialog(null, "No File Loaded for Clustering!!!");
	        	} else if(chckbxClusterRows.isSelected() && Integer.parseInt(txtRowK.getText()) < 2) {
        			JOptionPane.showMessageDialog(null, "k for row clustering must be at least 2!!!");
        		} else if(chckbxClusterRows.isSelected() && Integer.parseInt(txtRowI.getText()) < 1) {
        			JOptionPane.showMessageDialog(null, "Iterations for row clustering must be at least 1!!!");
        		} else {
        			kMeansCluster kmean = new kMeansCluster(Matrix_File, OUTPUT_PATH);
        			kmean.load();
	        		if(chckbxClusterRows.isSelected()) { kmean.outputCluster(kmean.rowcluster(cmbRowCluster.getSelectedIndex(), Integer.parseInt(txtRowK.getText()), Integer.parseInt(txtRowI.getText()))); }
	        		//if(chckbxClusterColumns.isSelected()) { kmean.colcluster(cmbColCluster.getSelectedIndex(), Integer.parseInt(txtColK.getText()), Integer.parseInt(txtColI.getText())); }
	        		JOptionPane.showMessageDialog(null, "Clustering Complete");	
	       		}
	        	return null;
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
	
	public kMeansClusteringWindow() {
		setTitle("kMeans Clustering");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 575, 275);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
			
		btnLoad = new JButton("Load File");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 10, SpringLayout.WEST, contentPane);
		btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	File newMatrixFile = FileSelection.getGenericFile(fc, false);
				if(newMatrixFile != null) {
					Matrix_File = newMatrixFile;
					lblFileName.setText(Matrix_File.getName());
				}
			}
		});
		contentPane.add(btnLoad);
		
		btnCluster = new JButton("Cluster");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnCluster, 175, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnCluster, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnCluster, -175, SpringLayout.EAST, contentPane);
		contentPane.add(btnCluster);	
        btnCluster.setActionCommand("start");
        btnCluster.addActionListener(this);

        lblCurrent = new JLabel("Current Output:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrent, 150, SpringLayout.SOUTH, btnLoad);
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
        sl_contentPane.putConstraint(SpringLayout.SOUTH, btnOutput, -10, SpringLayout.NORTH, lblDefaultToLocal);
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
        
        
        lblFileName = new JLabel("No File Loaded");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblFileName, 6, SpringLayout.EAST, btnLoad);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblFileName, -5, SpringLayout.SOUTH, btnLoad);
        contentPane.add(lblFileName);
        
        JLabel lblSimilarityMetric = new JLabel("Similarity Metric:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblSimilarityMetric, 10, SpringLayout.SOUTH, lblFileName);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblSimilarityMetric, 150, SpringLayout.WEST, contentPane);
        contentPane.add(lblSimilarityMetric);
                
        chckbxClusterRows = new JCheckBox("Cluster Rows");
        chckbxClusterRows.setSelected(true);
        sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxClusterRows, 33, SpringLayout.SOUTH, btnLoad);
        sl_contentPane.putConstraint(SpringLayout.WEST, chckbxClusterRows, 0, SpringLayout.WEST, btnLoad);
        chckbxClusterRows.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
			        if(chckbxClusterRows.isSelected()) {
			        	txtRowK.setEnabled(true);
						txtRowI.setEnabled(true);
						cmbRowCluster.setEnabled(true);
			        } else {
			        	txtRowK.setEnabled(false);
						txtRowI.setEnabled(false);
						cmbRowCluster.setEnabled(false);
			        }
			      }
			    });
        contentPane.add(chckbxClusterRows);
        
        chckbxClusterColumns = new JCheckBox("Cluster Columns");
        sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxClusterColumns, 26, SpringLayout.SOUTH, chckbxClusterRows);
        sl_contentPane.putConstraint(SpringLayout.WEST, chckbxClusterColumns, 0, SpringLayout.WEST, btnLoad);
        chckbxClusterColumns.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
			        if(chckbxClusterColumns.isSelected()) {
			        	txtColK.setEnabled(true);
						txtColI.setEnabled(true);
						cmbColCluster.setEnabled(true);
			        } else {
			        	txtColK.setEnabled(false);
						txtColI.setEnabled(false);
						cmbColCluster.setEnabled(false);
			        }
			      }
			    });
        contentPane.add(chckbxClusterColumns);
        
        cmbRowCluster = new JComboBox<>(new DefaultComboBoxModel<>(new String[] {"Standard Pearson", "Reflective Pearson", "Spearman Rank", "Euclidean Distance", "Manhattan Distance"}));
        sl_contentPane.putConstraint(SpringLayout.NORTH, cmbRowCluster, -1, SpringLayout.NORTH, chckbxClusterRows);
        sl_contentPane.putConstraint(SpringLayout.WEST, cmbRowCluster, -2, SpringLayout.WEST, lblSimilarityMetric);
        contentPane.add(cmbRowCluster);

        cmbColCluster = new JComboBox<>(new DefaultComboBoxModel<>(new String[] {"Standard Pearson", "Reflective Pearson", "Spearman Rank", "Euclidean Distance", "Manhattan Distance"}));
        sl_contentPane.putConstraint(SpringLayout.WEST, cmbColCluster, 0, SpringLayout.WEST, cmbRowCluster);
        cmbColCluster.setEnabled(false);
        sl_contentPane.putConstraint(SpringLayout.NORTH, cmbColCluster, -1, SpringLayout.NORTH, chckbxClusterColumns);
        contentPane.add(cmbColCluster);
        
        lblClusterNumber = new JLabel("Cluster Number (k):");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblClusterNumber, 0, SpringLayout.NORTH, lblSimilarityMetric);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblClusterNumber, 60, SpringLayout.EAST, lblSimilarityMetric);
        contentPane.add(lblClusterNumber);
        
        lblIterations = new JLabel("Iterations");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblIterations, 0, SpringLayout.NORTH, lblSimilarityMetric);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblIterations, 40, SpringLayout.EAST, lblClusterNumber);
        contentPane.add(lblIterations);
        
        txtRowK = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.WEST, txtRowK, 25, SpringLayout.WEST, lblClusterNumber);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtRowK, 75, SpringLayout.WEST, lblClusterNumber);
        txtRowK.setHorizontalAlignment(SwingConstants.CENTER);
        txtRowK.setText("5");
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtRowK, 0, SpringLayout.NORTH, chckbxClusterRows);
        contentPane.add(txtRowK);
        txtRowK.setColumns(10);
        
        txtColK = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.WEST, txtColK, 25, SpringLayout.WEST, lblClusterNumber);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtColK, 75, SpringLayout.WEST, lblClusterNumber);
        txtColK.setEnabled(false);
        txtColK.setHorizontalAlignment(SwingConstants.CENTER);
        txtColK.setText("5");
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtColK, 0, SpringLayout.NORTH, chckbxClusterColumns);
        contentPane.add(txtColK);
        txtColK.setColumns(10);
        
        txtRowI = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.WEST, txtRowI, 0, SpringLayout.WEST, lblIterations);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtRowI, 60, SpringLayout.WEST, lblIterations);
        txtRowI.setHorizontalAlignment(SwingConstants.CENTER);
        txtRowI.setText("100");
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtRowI, 0, SpringLayout.NORTH, chckbxClusterRows);
        contentPane.add(txtRowI);
        txtRowI.setColumns(10);
        
        txtColI = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.WEST, txtColI, 0, SpringLayout.WEST, lblIterations);
        txtColI.setEnabled(false);
        txtColI.setHorizontalAlignment(SwingConstants.CENTER);
        txtColI.setText("100");
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtColI, 0, SpringLayout.NORTH, chckbxClusterColumns);
        sl_contentPane.putConstraint(SpringLayout.EAST, txtColI, 0, SpringLayout.EAST, txtRowI);
        contentPane.add(txtColI);
        txtColI.setColumns(10);
        
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
			if(!chckbxClusterRows.isSelected()) {
				txtRowK.setEnabled(false);
				txtRowI.setEnabled(false);
				cmbRowCluster.setEnabled(false);
			}
			if(!chckbxClusterColumns.isSelected()) {
				txtColK.setEnabled(false);
				txtColI.setEnabled(false);
				cmbColCluster.setEnabled(false);
			}
		}
	}
}


	
