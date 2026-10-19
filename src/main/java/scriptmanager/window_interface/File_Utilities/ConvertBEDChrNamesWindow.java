package scriptmanager.window_interface.File_Utilities;

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
import java.sql.Timestamp;
import java.util.Date;
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
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import scriptmanager.objects.LogItem;
import scriptmanager.objects.ToolDescriptions;
import scriptmanager.util.ExtensionFileFilter;
import scriptmanager.util.FileSelection;

import scriptmanager.cli.File_Utilities.ConvertBEDChrNamesCLI;
import scriptmanager.scripts.File_Utilities.ConvertChrNames;
import scriptmanager.util.WindowLayout;

/**
 * GUI for collecting inputs to be processed by
 * {@link scriptmanager.scripts.File_Utilities.ConvertChrNames}
 * 
 * @author Olivia Lang
 * @see scriptmanager.scripts.File_Utilities.ConvertChrNames
 */
@SuppressWarnings("serial")
public class ConvertBEDChrNamesWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	/**
	 * FileChooser which opens to user's directory
	 */
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
	private JCheckBox chckbxGzipOutput;

	private JProgressBar progressBar;
	/**
	 * Used to run the script efficiently
	 */
	public Task task;
	private JLabel lblCurrentOutput;
	private JLabel lblDefaultToLocal;
	private JButton btnOutput;

	/**
	 * Organizes user inputs for calling script
	 */
	class Task extends SwingWorker<Void, Void> {
		@Override
		public Void doInBackground() {
			try {
				setProgress(0);
				LogItem old_li = null;
				// apply to each fil in vector
				for (int x = 0; x < BEDFiles.size(); x++) {
					File XBED = BEDFiles.get(x);
					// Construct output filename
					String NAME = ExtensionFileFilter.stripExtensionIgnoreGZ(XBED);
					NAME += rdbtnA2R.isSelected() ? "_toRoman.bed" : "_toArabic.bed";
					NAME += chckbxGzipOutput.isSelected() ? ".gz" : "";
					File OUT_FILEPATH = new File(NAME);
					if (OUT_DIR != null) {
						OUT_FILEPATH = new File(OUT_DIR.getCanonicalPath() + File.separator + NAME);
					}
					// Initialize LogItem
					String command = ConvertBEDChrNamesCLI.getCLIcommand(rdbtnR2A.isSelected(), XBED, OUT_FILEPATH, chckbxChrmt.isSelected(), chckbxGzipOutput.isSelected());
					LogItem new_li = new LogItem(command, ConvertBEDChrNamesCLI.VERSION);
					firePropertyChange("log", old_li, new_li);
					// Execute script
					if (rdbtnR2A.isSelected()) {
						ConvertChrNames.convert_RomantoArabic(XBED, OUT_FILEPATH, chckbxChrmt.isSelected(), chckbxGzipOutput.isSelected());
					} else {
						ConvertChrNames.convert_ArabictoRoman(XBED, OUT_FILEPATH, chckbxChrmt.isSelected(), chckbxGzipOutput.isSelected());
					}
					// Update log item
					new_li.setStopTime(new Timestamp(new Date().getTime()));
					new_li.setStatus(0);
					old_li = new_li;
					// Update progress
					int percentComplete = (int) (((double) (x + 1) / BEDFiles.size()) * 100);
					setProgress(percentComplete);
				}
				// Update log at completion
				firePropertyChange("log", old_li, null);
				setProgress(100);
				JOptionPane.showMessageDialog(null, "Conversion Complete");
			} catch (IOException ioe) {
				ioe.printStackTrace();
				JOptionPane.showMessageDialog(null, "I/O issues: " + ioe.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, ToolDescriptions.UNEXPECTED_EXCEPTION_MESSAGE + e.getMessage());
			}
			setProgress(100);
			return null;
		}

		public void done() {
			massXable(contentPane, true);
			setCursor(null); // turn off the wait cursor
		}
	}

	/**
	 * Creates a new ConvertBEDChrNamesWindow
	 */
	public ConvertBEDChrNamesWindow() {
		setTitle("Convert Yeast Reference Genome for BED Files");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Size the window from its content (see pack() at end of constructor) rather
		// than fixing the outer frame size, which includes OS-specific title bars and
		// borders and so leaves less room on Windows than on macOS.
		setLocation(125, 125);
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
				File[] newBEDFiles = FileSelection.getFiles(fc, "bed", true);
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
		// Centered at its preferred width; vertically chained below "Current Output".
		sl_contentPane.putConstraint(SpringLayout.HORIZONTAL_CENTER, btnConvert, 0, SpringLayout.HORIZONTAL_CENTER, contentPane);
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

		chckbxGzipOutput = new JCheckBox("Output GZip");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxGzipOutput, 0, SpringLayout.NORTH, chckbxChrmt);
		sl_contentPane.putConstraint(SpringLayout.EAST, chckbxGzipOutput, -10, SpringLayout.EAST, contentPane);
		contentPane.add(chckbxGzipOutput);

		btnOutput = new JButton("Output Directory");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutput, 10, SpringLayout.SOUTH, chckbxChrmt);
		sl_contentPane.putConstraint(SpringLayout.HORIZONTAL_CENTER, btnOutput, 0, SpringLayout.HORIZONTAL_CENTER, contentPane);
		contentPane.add(btnOutput);

		lblCurrentOutput = new JLabel("Current Output:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrentOutput, 6, SpringLayout.SOUTH, btnOutput);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrentOutput, 0, SpringLayout.WEST, scrollPane);
		lblCurrentOutput.setFont(lblCurrentOutput.getFont().deriveFont(Font.BOLD));
		contentPane.add(lblCurrentOutput);

		lblDefaultToLocal = new JLabel("Default to Local Directory");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, lblCurrentOutput);
		// Bounded so long output paths are elided rather than running under other components.
		sl_contentPane.putConstraint(SpringLayout.EAST, lblDefaultToLocal, -10, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, lblCurrentOutput);
		lblDefaultToLocal.setBackground(Color.WHITE);
		contentPane.add(lblDefaultToLocal);

		btnOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OUT_DIR = FileSelection.getOutputDir(fc);
				if (OUT_DIR != null) {
					lblDefaultToLocal.setText(OUT_DIR.getAbsolutePath());
					lblDefaultToLocal.setToolTipText(OUT_DIR.getAbsolutePath());
				}
			}
		});

		// Bottom row chained below the form.
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnConvert, 10, SpringLayout.SOUTH, lblCurrentOutput);
		btnConvert.addActionListener(this);

		// Size the window from its content: 440 px wide or whatever the chrM + GZip row
		// needs, whichever is larger; the file list takes any extra height when enlarged.
		Spring width = Spring.max(Spring.constant(440), Spring.sum(sl_contentPane.getConstraint(SpringLayout.EAST, chckbxChrmt),
				Spring.sum(Spring.constant(25), sl_contentPane.getConstraints(chckbxGzipOutput).getWidth())));
		WindowLayout.pack(this, sl_contentPane, contentPane, width, btnConvert, scrollPane);
	}

	/**
	 * Runs when a task is invoked, making window non-interactive and executing the task.
	 */
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

	/**
	 * Makes the content pane non-interactive If the window should be interactive data
	 * @param con Content pane to make non-interactive
	 * @param status If the window should be interactive
	 */
	public void massXable(Container con, boolean status) {
		for (Component c : con.getComponents()) {
			c.setEnabled(status);
			if (c instanceof Container) {
				massXable((Container) c, status);
			}
		}
	}
}
