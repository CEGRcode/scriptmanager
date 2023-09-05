package scriptmanager.window_interface.Coordinate_Manipulation.BED_Manipulation;

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

import scriptmanager.objects.LogItem;
import scriptmanager.util.ExtensionFileFilter;
import scriptmanager.util.FileSelection;
import scriptmanager.cli.Coordinate_Manipulation.BED_Manipulation.BEDtoGFFCLI;
import scriptmanager.scripts.Coordinate_Manipulation.BED_Manipulation.BEDtoGFF;

/**
 * Graphical interface window for converting BED-formatted coordinates to
 * GFF-format by calling a script implemented in the scripts package.
 * 
 * @author William KM Lai
 * @see scriptmanager.scripts.Coordinate_Manipulation.BED_Manipulation.BEDtoGFF
 */
@SuppressWarnings("serial")
public class BEDtoGFFWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));

	private File OUT_DIR = null;
	final DefaultListModel<String> expList;
	Vector<File> BEDFiles = new Vector<File>();

	private JButton btnLoad;
	private JButton btnRemoveBED;
	private JButton btnConvert;

	private JProgressBar progressBar;
	public Task task;
	private JLabel lblCurrent;
	private JLabel lblDefaultToLocal;
	private JButton btnOutput;
	private static JCheckBox chckbxGzipOutput;

	/**
	 * Organize user inputs for calling script.
	 */
	class Task extends SwingWorker<Void, Void> {
		@Override
		public Void doInBackground() throws IOException {
			setProgress(0);
			LogItem old_li = new LogItem("");
			for (int x = 0; x < BEDFiles.size(); x++) {
				File XBED = BEDFiles.get(x);
				// Set outfilepath
				String OUTPUT = ExtensionFileFilter.stripExtension(XBED) + ".gff";
				if (OUT_DIR != null) {
					OUTPUT = OUT_DIR + File.separator + OUTPUT;
				}
				OUTPUT += chckbxGzipOutput.isSelected() ? ".gz" : "";
				// Initialize LogItem
				String command = BEDtoGFFCLI.getCLIcommand(new File(OUTPUT), XBED, chckbxGzipOutput.isSelected());
				LogItem new_li = new LogItem(command);
				firePropertyChange("log", old_li, new_li);
				// Execute conversion and update progress
				BEDtoGFF.convertBEDtoGFF(new File(OUTPUT), XBED, chckbxGzipOutput.isSelected());
				// Update log item
				new_li.setStopTime(new Timestamp(new Date().getTime()));
				new_li.setStatus(0);
				old_li = new_li;
				// Update progress
				int percentComplete = (int) (((double) (x + 1) / BEDFiles.size()) * 100);
				setProgress(percentComplete);
			}
			firePropertyChange("log", old_li, null);
			setProgress(100);
			JOptionPane.showMessageDialog(null, "Conversion Complete");
			return null;
		}

		public void done() {
			massXable(contentPane, true);
			setCursor(null); // turn off the wait cursor
		}
	}

	/**
	 * Instantiate window with graphical interface design.
	 */
	public BEDtoGFFWindow() {
		setTitle("BED to GFF File Converter");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 300);
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
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -62, SpringLayout.NORTH, btnConvert);
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

		lblCurrent = new JLabel("Current Output:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrent, 37, SpringLayout.SOUTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrent, 0, SpringLayout.WEST, scrollPane);
		lblCurrent.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblCurrent);

		lblDefaultToLocal = new JLabel("Default to Local Directory");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, lblCurrent);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, lblCurrent);
		lblDefaultToLocal.setBackground(Color.WHITE);
		contentPane.add(lblDefaultToLocal);

		btnOutput = new JButton("Output Directory");
		btnOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OUT_DIR = FileSelection.getOutputDir(fc);
				if (OUT_DIR != null) {
					lblDefaultToLocal.setText(OUT_DIR.getAbsolutePath());
				}
			}
		});
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutput, 6, SpringLayout.SOUTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnOutput, 150, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnOutput, -150, SpringLayout.EAST, contentPane);
		contentPane.add(btnOutput);
		btnConvert.addActionListener(this);

		chckbxGzipOutput = new JCheckBox("Output GZIP");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxGzipOutput, 0, SpringLayout.NORTH, btnOutput);
		sl_contentPane.putConstraint(SpringLayout.EAST, chckbxGzipOutput, -10, SpringLayout.EAST, contentPane);
		contentPane.add(chckbxGzipOutput);
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
		} else if ("log" == evt.getPropertyName()) {
			firePropertyChange("log", evt.getOldValue(), evt.getNewValue());
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