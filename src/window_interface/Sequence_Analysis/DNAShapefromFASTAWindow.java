package window_interface.Sequence_Analysis;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import util.FileSelection;

@SuppressWarnings("serial")
public class DNAShapefromFASTAWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));

	final DefaultListModel<String> expList;
	ArrayList<File> FASTAFiles = new ArrayList<File>();
	private File OUT_DIR = null;

	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnCalculate;
	private JButton btnOutput;
	private JLabel lblDefaultToLocal;
	private JLabel lblCurrent;
	private JProgressBar progressBar;
	private JCheckBox chckbxAll;
	private JCheckBox chckbxMinorGrooveWidth;
	private JCheckBox chckbxRoll;
	private JCheckBox chckbxHelicalTwist;
	private JCheckBox chckbxPropellerTwist;

	public Task task;

	class Task extends SwingWorker<Void, Void> {
		@Override
		public Void doInBackground() throws IOException {
			try {
				if (FASTAFiles.size() < 1) {
					JOptionPane.showMessageDialog(null, "No FASTA Files Loaded!!!");
				} else if (!chckbxMinorGrooveWidth.isSelected() && !chckbxRoll.isSelected()
						&& !chckbxHelicalTwist.isSelected() && !chckbxPropellerTwist.isSelected()) {
					JOptionPane.showMessageDialog(null, "No Structural Predictions Selected!!!");
				} else {
					setProgress(0);
					boolean[] OUTPUT_TYPE = new boolean[4];
					OUTPUT_TYPE[0] = chckbxMinorGrooveWidth.isSelected();
					OUTPUT_TYPE[1] = chckbxPropellerTwist.isSelected();
					OUTPUT_TYPE[2] = chckbxHelicalTwist.isSelected();
					OUTPUT_TYPE[3] = chckbxRoll.isSelected();

					DNAShapefromFASTAOutput signal = new DNAShapefromFASTAOutput(FASTAFiles, OUT_DIR, OUTPUT_TYPE);

					signal.addPropertyChangeListener("fa", new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
							int temp = (Integer) propertyChangeEvent.getNewValue();
							int percentComplete = (int) (((double) (temp) / FASTAFiles.size()) * 100);
							setProgress(percentComplete);
						}
					});

					signal.setVisible(true);
					signal.run();

				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		public void done() {
			massXable(contentPane, true);
			setCursor(null); // turn off the wait cursor
		}
	}

	public DNAShapefromFASTAWindow() {
		setTitle("DNA Shape Predictions from FASTA");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 475, 380);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 48, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);

		expList = new DefaultListModel<String>();
		final JList<String> listExp = new JList<String>(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listExp);

		btnLoad = new JButton("Load FASTA Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 10, SpringLayout.WEST, contentPane);
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File[] newFASTAFiles = FileSelection.getFiles(fc, "fa");
				if (newFASTAFiles != null) {
					for (int x = 0; x < newFASTAFiles.length; x++) {
						FASTAFiles.add(newFASTAFiles[x]);
						expList.addElement(newFASTAFiles[x].getName());
					}
				}
			}
		});
		contentPane.add(btnLoad);

		btnRemoveBam = new JButton("Remove FASTA");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveBam, 0, SpringLayout.NORTH, btnLoad);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBam, -10, SpringLayout.EAST, contentPane);
		btnRemoveBam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while (listExp.getSelectedIndex() > -1) {
					FASTAFiles.remove(listExp.getSelectedIndex());
					expList.remove(listExp.getSelectedIndex());
				}
			}
		});
		contentPane.add(btnRemoveBam);

		btnCalculate = new JButton("Calculate");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -125, SpringLayout.NORTH, btnCalculate);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnCalculate, -5, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnCalculate, 165, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnCalculate, -165, SpringLayout.EAST, contentPane);
		contentPane.add(btnCalculate);

		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.SOUTH, progressBar, -10, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -10, SpringLayout.EAST, contentPane);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);

		btnCalculate.setActionCommand("start");

		lblCurrent = new JLabel("Current Output:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrent, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblCurrent, -45, SpringLayout.SOUTH, contentPane);
		lblCurrent.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblCurrent);

		lblDefaultToLocal = new JLabel("Default to Local Directory");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, lblCurrent);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, lblCurrent);
		lblDefaultToLocal.setBackground(Color.WHITE);
		contentPane.add(lblDefaultToLocal);

		btnOutput = new JButton("Output Directory");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnOutput, 150, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnOutput, -150, SpringLayout.EAST, contentPane);
		btnOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OUT_DIR = FileSelection.getOutputDir(fc);
				if (OUT_DIR != null) {
					lblDefaultToLocal.setText(OUT_DIR.getAbsolutePath());
				}
			}
		});
		contentPane.add(btnOutput);

		chckbxMinorGrooveWidth = new JCheckBox("Minor Groove Width");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxMinorGrooveWidth, 10, SpringLayout.SOUTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxMinorGrooveWidth, 10, SpringLayout.WEST, contentPane);
		chckbxMinorGrooveWidth.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (!chckbxMinorGrooveWidth.isSelected()) {
					chckbxAll.setSelected(false);
				}
			}
		});
		contentPane.add(chckbxMinorGrooveWidth);

		chckbxRoll = new JCheckBox("Roll");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxRoll, 0, SpringLayout.NORTH, chckbxMinorGrooveWidth);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxRoll, 0, SpringLayout.WEST, btnCalculate);
		chckbxRoll.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (!chckbxRoll.isSelected()) {
					chckbxAll.setSelected(false);
				}
			}
		});
		contentPane.add(chckbxRoll);

		chckbxHelicalTwist = new JCheckBox("Helical Twist");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxHelicalTwist, 0, SpringLayout.NORTH,
				chckbxMinorGrooveWidth);
		chckbxHelicalTwist.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (!chckbxHelicalTwist.isSelected()) {
					chckbxAll.setSelected(false);
				}
			}
		});
		contentPane.add(chckbxHelicalTwist);

		chckbxPropellerTwist = new JCheckBox("Propeller Twist");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxPropellerTwist, 0, SpringLayout.NORTH,
				chckbxMinorGrooveWidth);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxHelicalTwist, 6, SpringLayout.EAST, chckbxPropellerTwist);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxPropellerTwist, 6, SpringLayout.EAST, chckbxRoll);
		chckbxPropellerTwist.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (!chckbxPropellerTwist.isSelected()) {
					chckbxAll.setSelected(false);
				}
			}
		});
		contentPane.add(chckbxPropellerTwist);

		chckbxAll = new JCheckBox("All");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutput, 3, SpringLayout.SOUTH, chckbxAll);
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxAll, 5, SpringLayout.SOUTH, chckbxRoll);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxAll, 206, SpringLayout.WEST, contentPane);
		chckbxAll.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (chckbxAll.isSelected()) {
					chckbxMinorGrooveWidth.setSelected(true);
					chckbxRoll.setSelected(true);
					chckbxHelicalTwist.setSelected(true);
					chckbxPropellerTwist.setSelected(true);
				}
			}
		});
		contentPane.add(chckbxAll);
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
		for (Component c : con.getComponents()) {
			c.setEnabled(status);
			if (c instanceof Container) {
				massXable((Container) c, status);
			}
		}
	}
}