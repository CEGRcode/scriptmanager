package scriptmanager.window_interface.BAM_Statistics;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.Font;
import java.io.File;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import scriptmanager.util.FileSelection;

/**
 * GUI for collecting inputs to be processed by
 * {@link scriptmanager.scripts.BAM_Statistics.SEStats}
 * 
 * @author William KM Lai
 * @see scriptmanager.scripts.BAM_Statistics.SEStats
 * @see scriptmanager.window_interface.BAM_Statistics.SEStatWindow
 */
@SuppressWarnings("serial")
public class SEStatWindow extends JFrame {
	private JPanel contentPane;
	/**
	 * FileChooser which opens to user's directory
	 */
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
		
	private JTextField txtOutputName;
	private JLabel lblOutputName;
	private JCheckBox chckbxOutputStatistics;
	
	final DefaultListModel<String> expList;
	Vector<File> BAMFiles = new Vector<File>();
	private File OUTPUT_PATH = null;

	public SEStatWindow() {
		setTitle("BAM File Statistics");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 345);
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
		
		JButton btnLoad = new JButton("Load BAM Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, btnLoad);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 0, SpringLayout.WEST, scrollPane);
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
					
		JButton btnRemoveBam = new JButton("Remove BAM");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveBam, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 0, SpringLayout.NORTH, btnRemoveBam);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBam, 0, SpringLayout.EAST, scrollPane);
		btnRemoveBam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(listExp.getSelectedIndex() > -1) {
					BAMFiles.remove(listExp.getSelectedIndex());
					expList.remove(listExp.getSelectedIndex());
				}
			}
		});		
		contentPane.add(btnRemoveBam);
		
		lblOutputName = new JLabel("Output File Name:");
		lblOutputName.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		sl_contentPane.putConstraint(SpringLayout.WEST, lblOutputName, 0, SpringLayout.WEST, scrollPane);
		lblOutputName.setEnabled(false);
		contentPane.add(lblOutputName);

		chckbxOutputStatistics = new JCheckBox("Output Statistics");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxOutputStatistics, 199, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -6, SpringLayout.NORTH, chckbxOutputStatistics);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxOutputStatistics, 0, SpringLayout.WEST, scrollPane);
		contentPane.add(chckbxOutputStatistics);
				
		txtOutputName = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtOutputName, -2, SpringLayout.NORTH, lblOutputName);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtOutputName, 6, SpringLayout.EAST, lblOutputName);
		txtOutputName.setText("output_bam_stats.txt");
		txtOutputName.setColumns(10);
		txtOutputName.setEnabled(false);
		contentPane.add(txtOutputName);
				
		JLabel lblCurrentOutput = new JLabel("Current Output:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblOutputName, 10, SpringLayout.SOUTH, lblCurrentOutput);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrentOutput, 10, SpringLayout.SOUTH, chckbxOutputStatistics);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrentOutput, 0, SpringLayout.WEST, scrollPane);
		lblCurrentOutput.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblCurrentOutput.setEnabled(false);
		contentPane.add(lblCurrentOutput);
		
		JLabel lblDefaultToLocal = new JLabel("Default to Local Directory");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, lblCurrentOutput);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, lblCurrentOutput);
		lblDefaultToLocal.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblDefaultToLocal.setBackground(Color.WHITE);
		lblDefaultToLocal.setEnabled(false);
		contentPane.add(lblDefaultToLocal);
		
		JButton btnOutput = new JButton("Output Directory");
		sl_contentPane.putConstraint(SpringLayout.EAST, txtOutputName, 56, SpringLayout.EAST, btnOutput);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutput, 6, SpringLayout.SOUTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnOutput, 150, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnOutput, -150, SpringLayout.EAST, contentPane);
		btnOutput.setEnabled(false);
		btnOutput.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
    			OUTPUT_PATH = FileSelection.getOutputDir(fc);
    			if(OUTPUT_PATH != null) {
    				lblDefaultToLocal.setText(OUTPUT_PATH.getAbsolutePath());
    			}
        	}
        });
		contentPane.add(btnOutput);
		
		chckbxOutputStatistics.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		        if(chckbxOutputStatistics.isSelected()) {
		        	btnOutput.setEnabled(true);
		        	lblOutputName.setEnabled(true);
		        	txtOutputName.setEnabled(true);
		        	lblCurrentOutput.setEnabled(true);
		        	lblDefaultToLocal.setEnabled(true);
		        } else {
		        	btnOutput.setEnabled(false);
		        	lblOutputName.setEnabled(false);
		        	txtOutputName.setEnabled(false);
		        	lblCurrentOutput.setEnabled(false);
		        	lblDefaultToLocal.setEnabled(false);   	
		        }
		      }
		    });
		
		JButton btnRun = new JButton("Run");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRun, 90, SpringLayout.SOUTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnRun, 171, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRun, -171, SpringLayout.EAST, contentPane);
		btnRun.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				SEStatOutput stat;
				if(chckbxOutputStatistics.isSelected()) { 
					if(OUTPUT_PATH != null) { stat = new SEStatOutput(BAMFiles, new File(OUTPUT_PATH + File.separator + txtOutputName.getText())); }
					else { stat = new SEStatOutput(BAMFiles, new File(txtOutputName.getText())); }
				} else { stat = new SEStatOutput(BAMFiles, null); }
				stat.setVisible(true);
				stat.run();
			}
		});
		contentPane.add(btnRun);
	}
}