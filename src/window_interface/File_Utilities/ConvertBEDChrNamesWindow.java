package window_interface.File_Utilities;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import util.ExtensionFileFilter;
import util.FileSelection;
import scripts.File_Utilities.ConvertChrNames;

@SuppressWarnings("serial")
public class ConvertBEDChrNamesWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));

	private File OUT_DIR = null;
	final DefaultListModel<String> expList;
	Vector<File> BEDFiles = new Vector<File>();

	private JButton btnLoad;
	private JButton btnRemoveBED;
	private JButton btnConvert;

	private JRadioButton rdbtnA2R;
	private JRadioButton rdbtnR2A;
	private JCheckBox chckbxChrmt;

	private JProgressBar progressBar;
	public Task task;
	private JLabel lblCurrentOutput;
	private JLabel lblDefaultToLocal;
	private JButton btnOutput;

	class Task extends SwingWorker<Void, Void> {
		@Override
		public Void doInBackground() throws IOException {
			setProgress(0);
			// apply to each fil in vector
			for (int x = 0; x < BEDFiles.size(); x++) {
				File XBED = BEDFiles.get(x);
				// Set outfilepath
				String NAME;
				if (rdbtnA2R.isSelected()) {
					NAME = ExtensionFileFilter.stripExtension(XBED) + "_toRoman."
							+ ExtensionFileFilter.getExtension(XBED);
				} else {
					NAME = ExtensionFileFilter.stripExtension(XBED) + "_toArabic."
							+ ExtensionFileFilter.getExtension(XBED);
				}
				File OUT_FILE = null;
				if (OUT_DIR == null)
					OUT_FILE = new File(NAME);
				else
					OUT_FILE = new File(OUT_DIR + File.separator + NAME);
				// Execute conversion and update progresss
				if (rdbtnA2R.isSelected()) {
					ConvertChrNames.convert_ArabictoRoman(XBED, OUT_FILE, chckbxChrmt.isSelected());
				} else {
					ConvertChrNames.convert_RomantoArabic(XBED, OUT_FILE, chckbxChrmt.isSelected());
				}
				// Update progress bar
				int percentComplete = (int) (((double) (x + 1) / BEDFiles.size()) * 100);
				setProgress(percentComplete);
			}
			setProgress(100);
			JOptionPane.showMessageDialog(null, "Conversion Complete");
			return null;
		}

		public void done() {
			massXable(contentPane, true);
			setCursor(null); // turn off the wait cursor
		}
	}

	public ConvertBEDChrNamesWindow() {
		setTitle("Convert Yeast Reference Genome for BED Files");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 360);
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

		btnLoad = new JButton("Load BED Files");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, btnLoad);
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File[] newBEDFiles = FileSelection.getFiles(fc, "bed");
				if (newBEDFiles != null) {
					for (int x = 0; x < newBEDFiles.length; x++) {
						BEDFiles.add(newBEDFiles[x]);
						expList.addElement(newBEDFiles[x].getName());
					}
				}
			}
		});
		contentPane.add(btnLoad);

		btnRemoveBED = new JButton("Remove BED");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 0, SpringLayout.NORTH, btnRemoveBED);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveBED, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBED, -5, SpringLayout.EAST, contentPane);
		btnRemoveBED.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while (listExp.getSelectedIndex() > -1) {
					BEDFiles.remove(listExp.getSelectedIndex());
					expList.remove(listExp.getSelectedIndex());
				}
			}
		});
		contentPane.add(btnRemoveBED);

		btnConvert = new JButton("Convert");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnConvert, 167, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnConvert, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnConvert, -175, SpringLayout.EAST, contentPane);
		contentPane.add(btnConvert);

		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnConvert);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -5, SpringLayout.EAST, contentPane);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);

		btnConvert.setActionCommand("start");

		rdbtnA2R = new JRadioButton("Arabic to Roman");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnA2R, 10, SpringLayout.SOUTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnA2R, 0, SpringLayout.WEST, scrollPane);
		contentPane.add(rdbtnA2R);

		rdbtnR2A = new JRadioButton("Roman to Arabic");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnR2A, 0, SpringLayout.NORTH, rdbtnA2R);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnR2A, 10, SpringLayout.EAST, rdbtnA2R);
		contentPane.add(rdbtnR2A);

		ButtonGroup ConvertDirection = new ButtonGroup();
		ConvertDirection.add(rdbtnA2R);
		ConvertDirection.add(rdbtnR2A);
		rdbtnA2R.setSelected(true);
		
		chckbxChrmt = new JCheckBox("Use \"chrmt\" instead of default \"chrM\"");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxChrmt, 10, SpringLayout.SOUTH, rdbtnA2R);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxChrmt, 0, SpringLayout.WEST, rdbtnA2R);
		contentPane.add(chckbxChrmt);

		btnOutput = new JButton("Output Directory");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutput, -85, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnOutput, 150, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnOutput, -150, SpringLayout.EAST, contentPane);
		contentPane.add(btnOutput);

		lblCurrentOutput = new JLabel("Current Output:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrentOutput, 6, SpringLayout.SOUTH, btnOutput);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblCurrentOutput, 0, SpringLayout.NORTH, btnConvert);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrentOutput, 0, SpringLayout.WEST, scrollPane);
		lblCurrentOutput.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblCurrentOutput);

		lblDefaultToLocal = new JLabel("Default to Local Directory");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 6, SpringLayout.NORTH, lblCurrentOutput);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblDefaultToLocal, 6, SpringLayout.SOUTH, lblCurrentOutput);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblDefaultToLocal, 0, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 0, SpringLayout.EAST, lblCurrentOutput);
		lblDefaultToLocal.setBackground(Color.WHITE);
		contentPane.add(lblDefaultToLocal);

		btnOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OUT_DIR = FileSelection.getOutputDir(fc);
				if (OUT_DIR != null) {
					lblDefaultToLocal.setText(OUT_DIR.getAbsolutePath());
				}
			}
		});

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
		for (Component c : con.getComponents()) {
			c.setEnabled(status);
			if (c instanceof Container) {
				massXable((Container) c, status);
			}
		}
	}
}
