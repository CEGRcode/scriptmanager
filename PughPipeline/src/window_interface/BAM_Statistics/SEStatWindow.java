package window_interface.BAM_Statistics;

import java.io.File;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JList;

import scripts.SEStats;
import util.FileSelection;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

@SuppressWarnings("serial")
public class SEStatWindow extends JFrame {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
		
	private JTextField txtOutputName;
	private JLabel lblOutputName;
	private JCheckBox chckbxOutputStatistics;
	
	final DefaultListModel expList;
	Vector<File> BAMFiles = new Vector<File>();

	public SEStatWindow() {
		setTitle("BAM File Statistics");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 312);
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
		
		JButton btnLoad = new JButton("Load BAM Files");
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
		
		lblOutputName = new JLabel("Output File Name:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblOutputName, 0, SpringLayout.WEST, scrollPane);
		contentPane.add(lblOutputName);
		
		chckbxOutputStatistics = new JCheckBox("Output Statistics");
		chckbxOutputStatistics.setSelected(true);
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxOutputStatistics, 199, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -6, SpringLayout.NORTH, chckbxOutputStatistics);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxOutputStatistics, 0, SpringLayout.WEST, scrollPane);
		contentPane.add(chckbxOutputStatistics);
		
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
		
		JButton btnRun = new JButton("Run");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRun, 61, SpringLayout.SOUTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnRun, 171, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRun, -171, SpringLayout.EAST, contentPane);
		contentPane.add(btnRun);
		
		txtOutputName = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtOutputName, 4, SpringLayout.SOUTH, chckbxOutputStatistics);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtOutputName, 16, SpringLayout.EAST, lblOutputName);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtOutputName, -15, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblOutputName, 2, SpringLayout.NORTH, txtOutputName);
		txtOutputName.setText("output_name.txt");
		contentPane.add(txtOutputName);
		txtOutputName.setColumns(10);
		
		chckbxOutputStatistics.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		        if(chckbxOutputStatistics.isSelected()) {
		        	txtOutputName.setEditable(true);
		        	txtOutputName.setForeground(Color.BLACK);
		        	lblOutputName.setForeground(Color.BLACK);

		        } else {
		        	txtOutputName.setEditable(false);
		        	txtOutputName.setForeground(Color.GRAY);
		        	lblOutputName.setForeground(Color.GRAY);		        	
		        }
		      }
		    });
		
		btnRun.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				SEStats stat;
				if(chckbxOutputStatistics.isSelected()) { stat = new SEStats(BAMFiles, new File(txtOutputName.getText())); }
				else { stat = new SEStats(BAMFiles, null); }
				stat.setVisible(true);
				stat.run();
			}
		});
	}
}


	
