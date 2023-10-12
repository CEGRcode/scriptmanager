package scriptmanager.window_interface.Read_Analysis;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import scriptmanager.util.ExtensionFileFilter;
import scriptmanager.util.FileSelection;
import scriptmanager.scripts.Read_Analysis.ScaleMatrix;

/**
 * GUI for collecting inputs to be processed by
 * {@link scriptmanager.scripts.Read_Analysis.ScaleMatrix}
 * 
 * @author William KM Lai
 * @see scriptmanager.scripts.Read_Analysis.ScalingFactor
 * @see scriptmanager.window_interface.Read_Analysis.ScalingFactorOutput
 */
@SuppressWarnings("serial")
public class ScaleMatrixWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	/**
	 * FileChooser which opens to user's directory
	 */
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));

	ArrayList<File> TABFiles = new ArrayList<File>();
	private File OUT_DIR = null;

	private JButton btnLoad;
	private JButton btnRemoveBam;
	private JButton btnCalculate;
	private JButton btnOutput;
	private JLabel lblDefaultToLocal;
	private JLabel lblCurrent;
	private JProgressBar progressBar;

	private JLabel lblUniformScalingFactor;
	private JTextField txtUniform;
	private JTextField txtRow;
	private JTextField txtCol;

	private JRadioButton rdbtnFilespecifcScaling;
	private JRadioButton rdbtnUniformScaling;

	private DefaultTableModel expTable;

	/**
	 * Used to run the script efficiently
	 */
	public Task task;

	/**
	 * Organizes user inputs for calling script
	 */
	class Task extends SwingWorker<Void, Void> {
		@Override
		public Void doInBackground() throws IOException {
			try {
				if (TABFiles.size() < 1) {
					JOptionPane.showMessageDialog(null, "No Files Loaded!!!");
				} else if (Integer.parseInt(txtRow.getText()) < 0) {
					JOptionPane.showMessageDialog(null, "Invalid Column Index Selected!!! Must be at least 0");
				} else if (Integer.parseInt(txtCol.getText()) < 0) {
					JOptionPane.showMessageDialog(null, "Invalid Row Index Selected!!! Must be at least 0");
				} else {
					// Check that all scaling numbers are valid
					boolean ALLNUM = true;
					for (int x = 0; x < TABFiles.size(); x++) {
						try {
							Double.parseDouble(expTable.getValueAt(x, 1).toString());
						} catch (NumberFormatException e) {
							JOptionPane.showMessageDialog(null,
									TABFiles.get(x).getName() + " possesses an invalid scaling factor!!!");
							ALLNUM = false;
						}
					}

					if (ALLNUM) {
						setProgress(0);
						double SCALE = 0;
						if (rdbtnUniformScaling.isSelected()) {
							SCALE = Double.parseDouble(txtUniform.getText());
						}

						for (int x = 0; x < TABFiles.size(); x++) {
							if (rdbtnFilespecifcScaling.isSelected()) {
								SCALE = Double.parseDouble(expTable.getValueAt(x, 1).toString());
							}

							File XTAB = TABFiles.get(x);
							String OUTPUT = ExtensionFileFilter.stripExtension(XTAB) + "_SCALE."
									+ ExtensionFileFilter.getExtension(XTAB);
							if (OUT_DIR != null) {
								OUTPUT = OUT_DIR + File.separator + OUTPUT;
							}

							ScaleMatrix scale = new ScaleMatrix(XTAB, new File(OUTPUT), SCALE,
									Integer.parseInt(txtRow.getText()), Integer.parseInt(txtCol.getText()));
							scale.run();

							int percentComplete = (int) (((double) (x + 1) / TABFiles.size()) * 100);
							setProgress(percentComplete);
						}
						setProgress(100);
						JOptionPane.showMessageDialog(null, "All Matrices Scaled");
					}
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid Scaling Factor!!! Must be number");
			}
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
	public ScaleMatrixWindow() {
		setTitle("Apply Scaling Factor");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		String[] TableHeader = { "Experiment", "Scaling Factor" };
		expTable = new DefaultTableModel(null, TableHeader) {
			@Override
			public Class<?> getColumnClass(int col) {
				return getValueAt(0, col).getClass();
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				if (column == 0) {
					return false;
				} else {
					return true;
				}
			}
		};
		JTable tableScale = new JTable(expTable);
		// Allow for the selection of multiple OR individual cells across either rows or
		// columns
		tableScale.setCellSelectionEnabled(true);
		tableScale.setColumnSelectionAllowed(true);
		tableScale.setRowSelectionAllowed(true);
		@SuppressWarnings("unused")
		ExcelAdapter myAd = new ExcelAdapter(tableScale);

		JScrollPane scrollPane = new JScrollPane(tableScale);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -190, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);

		btnLoad = new JButton("Load TAB Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 6, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 10, SpringLayout.SOUTH, btnLoad);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 10, SpringLayout.WEST, contentPane);
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File[] newTABFiles = FileSelection.getGenericFiles(fc);
				if (newTABFiles != null) {
					for (int x = 0; x < newTABFiles.length; x++) {
						TABFiles.add(newTABFiles[x]);
						expTable.addRow(new Object[] { newTABFiles[x].getName(), "0" });
					}
				}
			}
		});
		contentPane.add(btnLoad);

		btnRemoveBam = new JButton("Remove TAB");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveBam, 0, SpringLayout.NORTH, btnLoad);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBam, -10, SpringLayout.EAST, contentPane);
		btnRemoveBam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int[] index = tableScale.getSelectedRows();
				for (int x = index.length - 1; x >= 0; x--) {
					TABFiles.remove(index[x]);
					expTable.removeRow(index[x]);
				}
			}
		});
		contentPane.add(btnRemoveBam);

		btnCalculate = new JButton("Scale");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnCalculate, 165, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnCalculate, -165, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnCalculate, -10, SpringLayout.SOUTH, contentPane);
		contentPane.add(btnCalculate);
		btnCalculate.setActionCommand("start");
		btnCalculate.addActionListener(this);

		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnCalculate);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -10, SpringLayout.EAST, contentPane);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);

		lblCurrent = new JLabel("Current Output:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrent, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblCurrent, -45, SpringLayout.SOUTH, contentPane);
		lblCurrent.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblCurrent);

		lblDefaultToLocal = new JLabel("Default to Local Directory");
		lblDefaultToLocal.setFont(new Font("Dialog", Font.PLAIN, 12));
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, lblCurrent);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, lblCurrent);
		lblDefaultToLocal.setBackground(Color.WHITE);
		contentPane.add(lblDefaultToLocal);

		btnOutput = new JButton("Output Directory");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnOutput, 0, SpringLayout.WEST, btnCalculate);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnOutput, -10, SpringLayout.NORTH, lblDefaultToLocal);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnOutput, -135, SpringLayout.EAST, contentPane);
		btnOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OUT_DIR = FileSelection.getOutputDir(fc);
				if (OUT_DIR != null) {
					lblDefaultToLocal.setText(OUT_DIR.getAbsolutePath());
				}
			}
		});
		contentPane.add(btnOutput);

		rdbtnFilespecifcScaling = new JRadioButton("File-specifc Scaling");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnFilespecifcScaling, 8, SpringLayout.SOUTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, rdbtnFilespecifcScaling, -66, SpringLayout.EAST, contentPane);
		contentPane.add(rdbtnFilespecifcScaling);

		rdbtnUniformScaling = new JRadioButton("Uniform Scaling");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnUniformScaling, 0, SpringLayout.NORTH,
				rdbtnFilespecifcScaling);
		sl_contentPane.putConstraint(SpringLayout.EAST, rdbtnUniformScaling, -32, SpringLayout.WEST,
				rdbtnFilespecifcScaling);
		contentPane.add(rdbtnUniformScaling);

		ButtonGroup SCALETYPE = new ButtonGroup();
		SCALETYPE.add(rdbtnFilespecifcScaling);
		SCALETYPE.add(rdbtnUniformScaling);
		rdbtnFilespecifcScaling.setSelected(true);

		lblUniformScalingFactor = new JLabel("Uniform Scaling Factor:");
		lblUniformScalingFactor.setEnabled(false);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblUniformScalingFactor, 0, SpringLayout.WEST,
				rdbtnUniformScaling);
		contentPane.add(lblUniformScalingFactor);

		txtUniform = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtUniform, 8, SpringLayout.SOUTH, rdbtnFilespecifcScaling);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblUniformScalingFactor, 2, SpringLayout.NORTH, txtUniform);
		txtUniform.setEnabled(false);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtUniform, 22, SpringLayout.EAST, lblUniformScalingFactor);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtUniform, -142, SpringLayout.EAST, contentPane);
		txtUniform.setHorizontalAlignment(SwingConstants.CENTER);
		txtUniform.setText("1");
		contentPane.add(txtUniform);
		txtUniform.setColumns(10);

		JLabel lblRow = new JLabel("Start at Row:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblRow, 10, SpringLayout.SOUTH, lblUniformScalingFactor);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblRow, 10, SpringLayout.WEST, contentPane);
		contentPane.add(lblRow);

		JLabel lblCol = new JLabel("Start at Column:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCol, 0, SpringLayout.NORTH, lblRow);
		contentPane.add(lblCol);

		txtRow = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.WEST, txtRow, 8, SpringLayout.EAST, lblRow);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCol, 31, SpringLayout.EAST, txtRow);
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtRow, -2, SpringLayout.NORTH, lblRow);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtRow, 76, SpringLayout.EAST, lblRow);
		txtRow.setHorizontalAlignment(SwingConstants.CENTER);
		txtRow.setText("1");
		contentPane.add(txtRow);
		txtRow.setColumns(10);

		txtCol = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtCol, -2, SpringLayout.NORTH, lblRow);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtCol, 8, SpringLayout.EAST, lblCol);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtCol, 76, SpringLayout.EAST, lblCol);
		txtCol.setHorizontalAlignment(SwingConstants.CENTER);
		txtCol.setText("2");
		contentPane.add(txtCol);
		txtCol.setColumns(10);
		;

		rdbtnUniformScaling.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (rdbtnUniformScaling.isSelected()) {
					lblUniformScalingFactor.setEnabled(true);
					txtUniform.setEnabled(true);
				}
			}
		});
		rdbtnFilespecifcScaling.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (rdbtnFilespecifcScaling.isSelected()) {
					lblUniformScalingFactor.setEnabled(false);
					txtUniform.setEnabled(false);
				}
			}
		});

	}

	public void activateOutput(boolean activate) {
		btnOutput.setEnabled(activate);
		lblDefaultToLocal.setEnabled(activate);
		lblCurrent.setEnabled(activate);
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
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
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
		if (status) {
			if (rdbtnUniformScaling.isSelected()) {
				lblUniformScalingFactor.setEnabled(true);
				txtUniform.setEnabled(true);
			} else {
				lblUniformScalingFactor.setEnabled(false);
				txtUniform.setEnabled(false);
			}
		}
	}

	/**
	 * Adapted from:
	 * https://www.javaworld.com/article/2077579/learn-java/java-tip-77--enable-copy-and-paste-functionality-between-swing-s-jtables-and-excel.html
	 * ExcelAdapter enables Copy-Paste Clipboard functionality on JTables. The
	 * clipboard data format used by the adapter is compatible with the clipboard
	 * format used by Excel. This provides for clipboard interoperability between
	 * enabled JTables and Excel.
	 */
	public class ExcelAdapter implements ActionListener {
		private String rowstring, value;
		private Clipboard system;
		private StringSelection stsel;
		private JTable jTable1;

		/**
		 * The Excel Adapter is constructed with a JTable on which it enables Copy-Paste
		 * and acts as a Clipboard listener.
		 */
		public ExcelAdapter(JTable myJTable) {
			jTable1 = myJTable;
			KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
			// Identifying the copy KeyStroke user can modify this to copy on some other Key
			// combination.
			KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);
			// Identifying the Paste KeyStroke user can modify this to copy on some other
			// Key combination.
			jTable1.registerKeyboardAction(this, "Copy", copy, JComponent.WHEN_FOCUSED);
			jTable1.registerKeyboardAction(this, "Paste", paste, JComponent.WHEN_FOCUSED);
			system = Toolkit.getDefaultToolkit().getSystemClipboard();
		}

		/**
		 * Public Accessor methods for the Table on which this adapter acts.
		 */
		public JTable getJTable() {
			return jTable1;
		}

		public void setJTable(JTable jTable1) {
			this.jTable1 = jTable1;
		}

		/**
		 * This method is activated on the Keystrokes we are listening to in this
		 * implementation. Here it listens for Copy and Paste ActionCommands. Selections
		 * comprising non-adjacent cells result in invalid selection and then copy
		 * action cannot be performed. Paste is done by aligning the upper left corner
		 * of the selection with the 1st element in the current selection of the JTable.
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().compareTo("Copy") == 0) {
				StringBuffer sbf = new StringBuffer();
				// Check to ensure we have selected only a contiguous block of cells
				int numcols = jTable1.getSelectedColumnCount();
				int numrows = jTable1.getSelectedRowCount();
				int[] rowsselected = jTable1.getSelectedRows();
				int[] colsselected = jTable1.getSelectedColumns();
				if (!((numrows - 1 == rowsselected[rowsselected.length - 1] - rowsselected[0]
						&& numrows == rowsselected.length)
						&& (numcols - 1 == colsselected[colsselected.length - 1] - colsselected[0]
								&& numcols == colsselected.length))) {
					JOptionPane.showMessageDialog(null, "Invalid Copy Selection", "Invalid Copy Selection",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				for (int i = 0; i < numrows; i++) {
					for (int j = 0; j < numcols; j++) {
						sbf.append(jTable1.getValueAt(rowsselected[i], colsselected[j]));
						if (j < numcols - 1) {
							sbf.append("\t");
						}
					}
					sbf.append("\n");
				}
				stsel = new StringSelection(sbf.toString());
				system = Toolkit.getDefaultToolkit().getSystemClipboard();
				system.setContents(stsel, stsel);
			}

			if (e.getActionCommand().compareTo("Paste") == 0) {
				// System.out.println("Trying to Paste");
				int startRow = (jTable1.getSelectedRows())[0];
				int startCol = (jTable1.getSelectedColumns())[0];
				try {
					String trstring = (String) (system.getContents(this).getTransferData(DataFlavor.stringFlavor));
					// System.out.println("String is:"+trstring);
					StringTokenizer st1 = new StringTokenizer(trstring, "\n");
					for (int i = 0; st1.hasMoreTokens(); i++) {
						rowstring = st1.nextToken();
						StringTokenizer st2 = new StringTokenizer(rowstring, "\t");
						for (int j = 0; st2.hasMoreTokens(); j++) {
							value = (String) st2.nextToken();
							if (startRow + i < jTable1.getRowCount() && startCol + j < jTable1.getColumnCount()
									&& jTable1.isCellEditable(startRow + i, startCol + j)) {
								jTable1.setValueAt(value, startRow + i, startCol + j);
							}
							// System.out.println("Putting "+ value+"at
							// row="+startRow+i+"column="+startCol+j);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}