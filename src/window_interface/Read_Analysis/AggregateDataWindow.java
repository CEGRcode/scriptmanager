package window_interface.Read_Analysis;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import util.FileSelection;
import scripts.Read_Analysis.AggregateData;

@SuppressWarnings("serial")
public class AggregateDataWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));

	private File OUT_DIR = null;
	final DefaultListModel<String> expList;
	ArrayList<File> SUMFiles = new ArrayList<File>();

	private JButton btnLoad;
	private JButton btnRemoveCDT;
	private JButton btnConvert;
	private JButton btnOutput;
	private JProgressBar progressBar;
	private JLabel lblRowStart;
	private JLabel lblColumnStart;
	private JLabel lblCurrent;
	private JLabel lblDefaultToLocal;
	private JCheckBox chckbxMergeToOne;
	private JComboBox<String> cmbMethod;

	public Task task;
	private JTextField txtRow;
	private JTextField txtCol;

	class Task extends SwingWorker<Void, Void> {
		@Override
		public Void doInBackground() throws IOException {
			setProgress(0);
			try {
				if (Integer.parseInt(txtRow.getText()) < 1) {
					JOptionPane.showMessageDialog(null, "Invalid Start Row!!! Must be larger than 0 (1-based)");
				} else if (Integer.parseInt(txtCol.getText()) < 1) {
					JOptionPane.showMessageDialog(null, "Invalid Start Column!!! Must be larger than 0 (1-based)");
				} else {
					AggregateData script_obj = new AggregateData(SUMFiles, OUT_DIR, chckbxMergeToOne.isSelected(),
							Integer.parseInt(txtRow.getText()), Integer.parseInt(txtCol.getText()),
							cmbMethod.getSelectedIndex());
					script_obj.run();

// 					parse.addPropertyChangeListener("file", new PropertyChangeListener() {
// 						public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
// 							int temp = (Integer) propertyChangeEvent.getNewValue();
// 							int percentComplete = (int)(((double)(temp) / (SUMFiles.size())) * 100);
// 							setProgress(percentComplete);
// 						}
// 					 });
					setProgress(100);
					JOptionPane.showMessageDialog(null, script_obj.getMessage());
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

	public AggregateDataWindow() {
		setTitle("Aggregate Data");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 370);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane);

		expList = new DefaultListModel<String>();
		final JList<String> listExp = new JList<>(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(listExp);

		btnLoad = new JButton("Load Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 11, SpringLayout.SOUTH, btnLoad);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoad, 5, SpringLayout.WEST, contentPane);
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File[] newCDTFiles = FileSelection.getGenericFiles(fc);
				if (newCDTFiles != null) {
					for (int x = 0; x < newCDTFiles.length; x++) {
						SUMFiles.add(newCDTFiles[x]);
						expList.addElement(newCDTFiles[x].getName());
					}
				}
			}
		});
		contentPane.add(btnLoad);

		btnRemoveCDT = new JButton("Remove Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoad, 0, SpringLayout.NORTH, btnRemoveCDT);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveCDT, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveCDT, -5, SpringLayout.EAST, contentPane);
		btnRemoveCDT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while (listExp.getSelectedIndex() > -1) {
					SUMFiles.remove(listExp.getSelectedIndex());
					expList.remove(listExp.getSelectedIndex());
				}
			}
		});
		contentPane.add(btnRemoveCDT);

		lblRowStart = new JLabel("Start at Row:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblRowStart, 5, SpringLayout.WEST, contentPane);
		contentPane.add(lblRowStart);

		txtRow = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtRow, -2, SpringLayout.NORTH, lblRowStart);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtRow, 10, SpringLayout.EAST, lblRowStart);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtRow, 70, SpringLayout.EAST, lblRowStart);
		txtRow.setHorizontalAlignment(SwingConstants.CENTER);
		txtRow.setText("1");
		contentPane.add(txtRow);
		txtRow.setColumns(10);

		lblColumnStart = new JLabel("Start at Column:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblColumnStart, 0, SpringLayout.NORTH, lblRowStart);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblColumnStart, 60, SpringLayout.EAST, txtRow);
		contentPane.add(lblColumnStart);

		txtCol = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtCol, -2, SpringLayout.NORTH, lblColumnStart);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtCol, 10, SpringLayout.EAST, lblColumnStart);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtCol, 70, SpringLayout.EAST, lblColumnStart);
		txtCol.setHorizontalAlignment(SwingConstants.CENTER);
		txtCol.setText("2");
		contentPane.add(txtCol);
		txtCol.setColumns(10);

		btnConvert = new JButton("Parse Matrix");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -140, SpringLayout.NORTH, btnConvert);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnConvert, 165, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnConvert, -165, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnConvert, 0, SpringLayout.SOUTH, contentPane);
		contentPane.add(btnConvert);

		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnConvert);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -5, SpringLayout.EAST, contentPane);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);

		btnConvert.setActionCommand("start");

		lblCurrent = new JLabel("Current Output:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrent, 5, SpringLayout.WEST, contentPane);
		lblCurrent.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblCurrent);

		lblDefaultToLocal = new JLabel("Default to Local Directory");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, lblCurrent);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, lblCurrent);
		lblDefaultToLocal.setBackground(Color.WHITE);
		contentPane.add(lblDefaultToLocal);

		btnOutput = new JButton("Output Directory");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutput, 15, SpringLayout.SOUTH, lblColumnStart);
		btnOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OUT_DIR = FileSelection.getOutputDir(fc);
				if (OUT_DIR != null) {
					lblDefaultToLocal.setText(OUT_DIR.getAbsolutePath());
				}
			}
		});
		sl_contentPane.putConstraint(SpringLayout.WEST, btnOutput, 150, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnOutput, -150, SpringLayout.EAST, contentPane);
		contentPane.add(btnOutput);

		chckbxMergeToOne = new JCheckBox("Merge to one file");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrent, 10, SpringLayout.SOUTH, chckbxMergeToOne);
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxMergeToOne, 1, SpringLayout.NORTH, btnOutput);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxMergeToOne, 4, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, chckbxMergeToOne, -6, SpringLayout.WEST, btnOutput);
		chckbxMergeToOne.setSelected(true);
		contentPane.add(chckbxMergeToOne);

		// String[] function = {"Sum", "Average", "Median", "Mode", "Min", "Max"};
		cmbMethod = new JComboBox<>(new DefaultComboBoxModel<>(
				new String[] { "Sum", "Average", "Median", "Mode", "Min", "Max", "Positional Variance" }));
		sl_contentPane.putConstraint(SpringLayout.NORTH, cmbMethod, 8, SpringLayout.SOUTH, scrollPane);
		contentPane.add(cmbMethod);

		JLabel lblMathematicalFunction = new JLabel("Aggregation Method:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblRowStart, 20, SpringLayout.SOUTH, lblMathematicalFunction);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblMathematicalFunction, 12, SpringLayout.SOUTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblMathematicalFunction, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, cmbMethod, 6, SpringLayout.EAST, lblMathematicalFunction);
		contentPane.add(lblMathematicalFunction);

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