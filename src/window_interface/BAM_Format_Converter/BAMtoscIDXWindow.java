package window_interface.BAM_Format_Converter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import util.FileSelection;

@SuppressWarnings("serial")
public class BAMtoscIDXWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));

	final DefaultListModel<String> expList;
	Vector<File> BAMFiles = new Vector<File>();
	private File OUT_DIR = null;
	private int STRAND = 0;

	private JButton btnIndex;
	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnOutputDirectory;
	private JRadioButton rdbtnRead1;
	private JRadioButton rdbtnRead2;
	private JRadioButton rdbtnCombined;
	private JRadioButton rdbtnMidpoint;

	private JCheckBox chckbxRequireProperMatepair;
	private JCheckBox chckbxFilterByMaximum;
	private JCheckBox chckbxFilterByMinimum;
	private JTextField txtMin;
	private JTextField txtMax;

	JProgressBar progressBar;
	public Task task;

	class Task extends SwingWorker<Void, Void> {
		@Override
		public Void doInBackground() throws IOException, InterruptedException {
			try {
				if (chckbxFilterByMinimum.isSelected() && Integer.parseInt(txtMin.getText()) < 0) {
					JOptionPane.showMessageDialog(null,
							"Invalid Minimum Insert Size!!! Must be greater than or equal to 0 bp");
				} else if (chckbxFilterByMaximum.isSelected() && Integer.parseInt(txtMax.getText()) < 0) {
					JOptionPane.showMessageDialog(null,
							"Invalid Maximum Insert Size!!! Must be greater than or equal to 0 bp");
				} else if (chckbxFilterByMinimum.isSelected() && chckbxFilterByMaximum.isSelected()
						&& Integer.parseInt(txtMax.getText()) < Integer.parseInt(txtMin.getText())) {
					JOptionPane.showMessageDialog(null,
							"Invalid Maximum & Minimum Insert Sizes!!! Maximum must be larger/equal to Minimum!");
				} else {
					setProgress(0);
					if (rdbtnRead1.isSelected()) {
						STRAND = 0;
					} else if (rdbtnRead2.isSelected()) {
						STRAND = 1;
					} else if (rdbtnCombined.isSelected()) {
						STRAND = 2;
					} else if (rdbtnMidpoint.isSelected()) {
						STRAND = 3;
					}

					int PAIR = 0;
					if (chckbxRequireProperMatepair.isSelected()) {
						PAIR = 1;
					}
					int MIN = -9999;
					if (chckbxFilterByMinimum.isSelected()) {
						MIN = Integer.parseInt(txtMin.getText());
					}
					int MAX = -9999;
					if (chckbxFilterByMaximum.isSelected()) {
						MAX = Integer.parseInt(txtMax.getText());
					}

					for (int x = 0; x < BAMFiles.size(); x++) {
						BAMtoscIDXOutput convert = new BAMtoscIDXOutput(BAMFiles.get(x), OUT_DIR, STRAND, PAIR, MIN,
								MAX);
						convert.setVisible(true);
						convert.run();
						int percentComplete = (int) (((double) (x + 1) / BAMFiles.size()) * 100);
						setProgress(percentComplete);
					}
					setProgress(100);
					return null;
				}
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(null, "Invalid Input in Fields!!!");
			}
			return null;
		}

		public void done() {
			massXable(contentPane, true);
			setCursor(null); // turn off the wait cursor
		}
	}

	public BAMtoscIDXWindow() {
		setTitle("BAM to scIDX Converter");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 650, 475);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -203, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);

		btnLoad = new JButton("Load BAM Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 5, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 10, SpringLayout.SOUTH, btnLoad);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 10, SpringLayout.WEST, contentPane);
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File[] newBAMFiles = FileSelection.getFiles(fc, "bam");
				if (newBAMFiles != null) {
					for (int x = 0; x < newBAMFiles.length; x++) {
						BAMFiles.add(newBAMFiles[x]);
						expList.addElement(newBAMFiles[x].getName());
					}
				}
			}
		});
		contentPane.add(btnLoad);

		expList = new DefaultListModel<String>();
		final JList<String> listExp = new JList<String>(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listExp);

		btnRemoveBam = new JButton("Remove BAM");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveBam, 0, SpringLayout.NORTH, btnLoad);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBam, -10, SpringLayout.EAST, contentPane);
		btnRemoveBam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while (listExp.getSelectedIndex() > -1) {
					BAMFiles.remove(listExp.getSelectedIndex());
					expList.remove(listExp.getSelectedIndex());
				}
			}
		});
		contentPane.add(btnRemoveBam);

		btnIndex = new JButton("Convert");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnIndex, 250, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnIndex, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnIndex, -250, SpringLayout.EAST, contentPane);
		contentPane.add(btnIndex);
		btnIndex.addActionListener(this);

		rdbtnRead1 = new JRadioButton("Read 1");
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnRead1, 40, SpringLayout.WEST, contentPane);
		contentPane.add(rdbtnRead1);

		rdbtnRead2 = new JRadioButton("Read 2");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnRead2, 0, SpringLayout.NORTH, rdbtnRead1);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnRead2, 60, SpringLayout.EAST, rdbtnRead1);
		contentPane.add(rdbtnRead2);

		rdbtnCombined = new JRadioButton("Combined");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnCombined, 0, SpringLayout.NORTH, rdbtnRead1);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnCombined, 60, SpringLayout.EAST, rdbtnRead2);
		contentPane.add(rdbtnCombined);

		rdbtnMidpoint = new JRadioButton("Midpoint (Require PE)");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnMidpoint, 0, SpringLayout.NORTH, rdbtnRead1);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnMidpoint, 60, SpringLayout.EAST, rdbtnCombined);
		rdbtnMidpoint.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (rdbtnMidpoint.isSelected()) {
					chckbxRequireProperMatepair.setSelected(true);
					chckbxRequireProperMatepair.setEnabled(false);
				} else if (!chckbxFilterByMinimum.isSelected() && !chckbxFilterByMaximum.isSelected()) {
					chckbxRequireProperMatepair.setEnabled(true);
				}
			}
		});
		contentPane.add(rdbtnMidpoint);

		ButtonGroup OutputRead = new ButtonGroup();
		OutputRead.add(rdbtnRead1);
		OutputRead.add(rdbtnRead2);
		OutputRead.add(rdbtnCombined);
		OutputRead.add(rdbtnMidpoint);
		rdbtnRead1.setSelected(true);

		JLabel lblPleaseSelectWhich = new JLabel("Please Select Which Read to Output:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnRead1, 6, SpringLayout.SOUTH, lblPleaseSelectWhich);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblPleaseSelectWhich, 6, SpringLayout.SOUTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblPleaseSelectWhich, 10, SpringLayout.WEST, contentPane);
		lblPleaseSelectWhich.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblPleaseSelectWhich);

		final JLabel lblDefaultToLocal = new JLabel("Default to Local Directory");
		lblDefaultToLocal.setFont(new Font("Dialog", Font.PLAIN, 12));
		sl_contentPane.putConstraint(SpringLayout.EAST, lblDefaultToLocal, -15, SpringLayout.EAST, contentPane);
		lblDefaultToLocal.setBackground(Color.WHITE);
		contentPane.add(lblDefaultToLocal);

		JLabel lblCurrentOutput = new JLabel("Current Output:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrentOutput, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, lblCurrentOutput);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, lblCurrentOutput);
		lblCurrentOutput.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblCurrentOutput, -35, SpringLayout.SOUTH, contentPane);
		contentPane.add(lblCurrentOutput);

		btnOutputDirectory = new JButton("Output Directory");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnOutputDirectory, 250, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnOutputDirectory, -57, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnOutputDirectory, -250, SpringLayout.EAST, contentPane);
		contentPane.add(btnOutputDirectory);

		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnIndex);
		sl_contentPane.putConstraint(SpringLayout.WEST, progressBar, 83, SpringLayout.EAST, btnIndex);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -10, SpringLayout.EAST, contentPane);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);

		btnIndex.setActionCommand("start");

		chckbxRequireProperMatepair = new JCheckBox("Require Proper Mate-Pair");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxRequireProperMatepair, 6, SpringLayout.SOUTH,
				rdbtnRead2);
		contentPane.add(chckbxRequireProperMatepair);

		chckbxFilterByMinimum = new JCheckBox("Filter by Min Insert Size (bp)");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxFilterByMinimum, 35, SpringLayout.SOUTH, rdbtnRead1);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxFilterByMinimum, 10, SpringLayout.WEST, contentPane);
		chckbxFilterByMinimum.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (chckbxFilterByMinimum.isSelected()) {
					txtMin.setEnabled(true);
					chckbxRequireProperMatepair.setSelected(true);
					chckbxRequireProperMatepair.setEnabled(false);
				} else {
					txtMin.setEnabled(false);
					if (!chckbxFilterByMaximum.isSelected() && !rdbtnMidpoint.isSelected()) {
						chckbxRequireProperMatepair.setEnabled(true);
					}
				}
			}
		});
		contentPane.add(chckbxFilterByMinimum);

		txtMin = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxRequireProperMatepair, 0, SpringLayout.WEST, txtMin);
		txtMin.setEnabled(false);
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtMin, 2, SpringLayout.NORTH, chckbxFilterByMinimum);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtMin, 6, SpringLayout.EAST, chckbxFilterByMinimum);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtMin, 75, SpringLayout.EAST, chckbxFilterByMinimum);
		txtMin.setHorizontalAlignment(SwingConstants.CENTER);
		txtMin.setText("0");
		contentPane.add(txtMin);
		txtMin.setColumns(10);

		chckbxFilterByMaximum = new JCheckBox("Filter by Max Insert Size (bp)");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxFilterByMaximum, 35, SpringLayout.SOUTH, rdbtnCombined);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxFilterByMaximum, 25, SpringLayout.EAST, txtMin);
		chckbxFilterByMaximum.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (chckbxFilterByMaximum.isSelected()) {
					txtMax.setEnabled(true);
					chckbxRequireProperMatepair.setSelected(true);
					chckbxRequireProperMatepair.setEnabled(false);
				} else {
					txtMax.setEnabled(false);
					if (!chckbxFilterByMinimum.isSelected() && !rdbtnMidpoint.isSelected()) {
						chckbxRequireProperMatepair.setEnabled(true);
					}
				}
			}
		});
		contentPane.add(chckbxFilterByMaximum);

		txtMax = new JTextField();
		txtMax.setEnabled(false);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtMax, 6, SpringLayout.EAST, chckbxFilterByMaximum);
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtMax, 2, SpringLayout.NORTH, chckbxFilterByMinimum);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtMax, 75, SpringLayout.EAST, chckbxFilterByMaximum);
		txtMax.setHorizontalAlignment(SwingConstants.CENTER);
		txtMax.setText("1000");
		contentPane.add(txtMax);
		txtMax.setColumns(10);

		btnOutputDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OUT_DIR = FileSelection.getOutputDir(fc);
				if (OUT_DIR != null) {
					lblDefaultToLocal.setText(OUT_DIR.getAbsolutePath());
				}
			}
		});
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
		for (Component c : con.getComponents()) {
			c.setEnabled(status);
			if (c instanceof Container) {
				massXable((Container) c, status);
			}
		}
		if (status) {
			if (chckbxFilterByMaximum.isSelected()) {
				txtMax.setEnabled(true);
			} else {
				txtMax.setEnabled(false);
			}
			if (chckbxFilterByMinimum.isSelected()) {
				txtMin.setEnabled(true);
			} else {
				txtMin.setEnabled(false);
			}
		}
	}
}
