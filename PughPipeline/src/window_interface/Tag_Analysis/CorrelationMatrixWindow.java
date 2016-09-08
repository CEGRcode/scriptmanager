package window_interface.Tag_Analysis;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import scripts.Tag_Analysis.CorrelationScripts.CCMain;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;

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
public class CorrelationMatrixWindow extends JFrame implements ActionListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	
	private File OUTPUT_PATH = null;
	private File Matrix_File = null;
	
	private JButton btnLoad;
	private JButton btnCorrelate;
	public Task task;
	private JLabel lblMatrixFileName;
	private JLabel lblCurrent;
	private JLabel lblDefaultToLocal;
	private JButton btnOutput;
	
	private JCheckBox chckbxOutputMatrixData; 
	
	class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() throws IOException {
        	CCMain calculate = new CCMain(Matrix_File, OUTPUT_PATH);
        	calculate.execute();
        	return null;
        }
        
        public void done() {
        	massXable(contentPane, true);
            setCursor(null); //turn off the wait cursor
        }
	}
	
	public CorrelationMatrixWindow() {
		setTitle("Correlation Matrix Calculator");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 300);
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
		
		btnCorrelate = new JButton("Correlate");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnCorrelate, 167, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnCorrelate, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnCorrelate, -175, SpringLayout.EAST, contentPane);
		contentPane.add(btnCorrelate);
		
        btnCorrelate.setActionCommand("start");
        
        lblCurrent = new JLabel("Current Output:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrent, 185, SpringLayout.SOUTH, btnLoad);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrent, 5, SpringLayout.WEST, contentPane);
        lblCurrent.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        contentPane.add(lblCurrent);
        
        lblDefaultToLocal = new JLabel("Default to Local Directory");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, lblCurrent);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, lblCurrent);
        lblDefaultToLocal.setBackground(Color.WHITE);
        contentPane.add(lblDefaultToLocal);
        
        btnOutput = new JButton("Output Directory");
        sl_contentPane.putConstraint(SpringLayout.WEST, btnOutput, 150, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, btnOutput, -6, SpringLayout.NORTH, lblDefaultToLocal);
        sl_contentPane.putConstraint(SpringLayout.EAST, btnOutput, -150, SpringLayout.EAST, contentPane);
        btnOutput.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
    			OUTPUT_PATH = FileSelection.getOutputDir(fc);
    			if(OUTPUT_PATH != null) {
    				lblDefaultToLocal.setText(OUTPUT_PATH.getAbsolutePath());
    			}
        	}
        });
        contentPane.add(btnOutput);
        
        chckbxOutputMatrixData = new JCheckBox("Output Matrix Data");
        sl_contentPane.putConstraint(SpringLayout.SOUTH, chckbxOutputMatrixData, -6, SpringLayout.NORTH, btnOutput);
        chckbxOutputMatrixData.setSelected(true);
        sl_contentPane.putConstraint(SpringLayout.EAST, chckbxOutputMatrixData, 0, SpringLayout.EAST, btnOutput);
        contentPane.add(chckbxOutputMatrixData);
        
        chckbxOutputMatrixData.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
			        if(chckbxOutputMatrixData.isSelected()) {
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
        
        JLabel lblMatrixSimilarityMetric = new JLabel("Matrix Similarity Metric:");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblMatrixSimilarityMetric, 17, SpringLayout.SOUTH, btnLoad);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblMatrixSimilarityMetric, 10, SpringLayout.WEST, contentPane);
        contentPane.add(lblMatrixSimilarityMetric);
        
        JLabel lblDendrogramSimilarityMetric = new JLabel("Dendrogram Similarity Metric:");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblDendrogramSimilarityMetric, 0, SpringLayout.WEST, lblMatrixSimilarityMetric);
        contentPane.add(lblDendrogramSimilarityMetric);
        
        JRadioButton rdbtnPearsonMatrix = new JRadioButton("Pearson");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblDendrogramSimilarityMetric, 20, SpringLayout.SOUTH, rdbtnPearsonMatrix);
        rdbtnPearsonMatrix.setSelected(true);
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnPearsonMatrix, 6, SpringLayout.SOUTH, lblMatrixSimilarityMetric);
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnPearsonMatrix, 20, SpringLayout.WEST, btnLoad);
        contentPane.add(rdbtnPearsonMatrix);
        
        JRadioButton rdbtnEuclideanMatrix = new JRadioButton("Euclidean");
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnEuclideanMatrix, 6, SpringLayout.SOUTH, lblMatrixSimilarityMetric);
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnEuclideanMatrix, 50, SpringLayout.EAST, rdbtnPearsonMatrix);
        contentPane.add(rdbtnEuclideanMatrix);
        
        JRadioButton rdbtnSpearmanMatrix = new JRadioButton("Spearman");
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnSpearmanMatrix, 0, SpringLayout.NORTH, rdbtnPearsonMatrix);
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnSpearmanMatrix, 50, SpringLayout.EAST, rdbtnEuclideanMatrix);
        contentPane.add(rdbtnSpearmanMatrix);
        
        ButtonGroup matrixSim = new ButtonGroup();
        matrixSim.add(rdbtnPearsonMatrix);
        matrixSim.add(rdbtnEuclideanMatrix);
        matrixSim.add(rdbtnSpearmanMatrix);
        rdbtnPearsonMatrix.setSelected(true);
        
        JRadioButton rdbtnPearsonDendro = new JRadioButton("Pearson");
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnPearsonDendro, 6, SpringLayout.SOUTH, lblDendrogramSimilarityMetric);
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnPearsonDendro, 20, SpringLayout.WEST, btnLoad);
        contentPane.add(rdbtnPearsonDendro);
        
        JRadioButton rdbtnEuclideanDendro = new JRadioButton("Euclidean");
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnEuclideanDendro, 50, SpringLayout.EAST, rdbtnPearsonDendro);
        rdbtnEuclideanDendro.setSelected(true);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, rdbtnEuclideanDendro, 0, SpringLayout.SOUTH, rdbtnPearsonDendro);
        contentPane.add(rdbtnEuclideanDendro);
        
        JRadioButton rdbtnSpearmanDendro = new JRadioButton("Spearman");
        sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnSpearmanDendro, 0, SpringLayout.NORTH, rdbtnPearsonDendro);
        sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnSpearmanDendro, 50, SpringLayout.EAST, rdbtnEuclideanDendro);
        contentPane.add(rdbtnSpearmanDendro);
        btnCorrelate.addActionListener(this);
        
        ButtonGroup dendroSim = new ButtonGroup();
        dendroSim.add(rdbtnPearsonDendro);
        dendroSim.add(rdbtnEuclideanDendro);
        dendroSim.add(rdbtnSpearmanDendro);
        rdbtnEuclideanDendro.setSelected(true);
        
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
			if(!chckbxOutputMatrixData.isSelected()) {
				btnOutput.setEnabled(false);
	        	lblDefaultToLocal.setEnabled(false);
	        	lblCurrent.setEnabled(false);
			}
		}
	}
}


	
