package window_interface.Coordinate_Manipulation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.border.EmptyBorder;
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import scripts.Coordinate_Manipulation.ShiftCoord;
import util.ExtensionFileFilter;
import util.FileSelection;

/**
 * Graphical interface window for calling static gzip compressing method implemented in the scripts package.
 * 
 * @author Olivia Lang
 *
 */
@SuppressWarnings("serial")
public class ShiftIntervalWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	private JProgressBar progressBar;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));

	private File OUT_DIR = null;
	final DefaultListModel<String> expBEDList;
	final DefaultListModel<String> expGFFList;
	ArrayList<File> BEDFiles = new ArrayList<File>();
	ArrayList<File> GFFFiles = new ArrayList<File>();

	private JButton btnLoadBED;
	private JButton btnRemoveBED;
	private JButton btnLoadGFF;
	private JButton btnRemoveGFF;

	private JButton btnExecute;

	public Task task;
	private JLabel lblCurrent;
	private JLabel lblDefaultToLocal;
	private JButton btnOutput;
	private JTextField txtShift;

	private JRadioButton rdbtnBed;
	private JRadioButton rdbtnGff;
	
	private static JCheckBox chckbxGzipOutput;
	private static JCheckBox chckbxStranded;

	class Task extends SwingWorker<Void, Void> {
		@Override
		public Void doInBackground() throws IOException {
			try {
				int SHIFT = Integer.parseInt(txtShift.getText());
				if (SHIFT == 0) {
					JOptionPane.showMessageDialog(null, "These shifts of 0 bp will generate identical files...");
				}
				setProgress(0);
				if (rdbtnBed.isSelected()) {
					for (int x = 0; x < BEDFiles.size(); x++) {
						// Save current BED to temp variable
						File XBED = BEDFiles.get(x);
						// Set suffix format
						String SUFFIX = SHIFT < 0 ? "_shift" + txtShift.getText() + "bp.bed" : "_shift+" + txtShift.getText() + "bp.bed";
						SUFFIX += chckbxGzipOutput.isSelected() ? ".gz" : "";
						// Set output filepath with name and output directory
						String OUTPUT = ExtensionFileFilter.stripExtension(XBED);
						if (OUT_DIR != null) {
							OUTPUT = OUT_DIR + File.separator + OUTPUT;
						}
						// Strip second extension if input has ".gz" first extension
						if (XBED.getName().endsWith(".bed.gz")) {
							OUTPUT = ExtensionFileFilter.stripExtensionPath(new File(OUTPUT)) ;
						}
						System.out.println(XBED.getName());
						System.out.println(OUTPUT);
						System.out.println(SUFFIX);
						// Execute expansion and update progress
						ShiftCoord.shiftBEDInterval(new File(OUTPUT + SUFFIX), XBED, SHIFT, chckbxStranded.isSelected(), chckbxGzipOutput.isSelected());
						
						// Update progress bar
						int percentComplete = (int) (((double) (x + 1) / BEDFiles.size()) * 100);
						setProgress(percentComplete);
					}
				}
				setProgress(100);
				JOptionPane.showMessageDialog(null, "Shift Complete");
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

	/**
	 * Instantiate window with graphical interface design.
	 */
	public ShiftIntervalWindow() {
		setTitle("Shift Coordinate Interval");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 450, 345);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		rdbtnBed = new JRadioButton("BED input");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnBed, 6, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnBed, 120, SpringLayout.WEST, contentPane);
		contentPane.add(rdbtnBed);
		
		rdbtnGff = new JRadioButton("GFF input");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnGff, 0, SpringLayout.NORTH, rdbtnBed);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnGff, 10, SpringLayout.EAST, rdbtnBed);
		contentPane.add(rdbtnGff);
		
		ButtonGroup InputType = new ButtonGroup();
		InputType.add(rdbtnBed);
		InputType.add(rdbtnGff);
		rdbtnBed.setSelected(true);
		
		JPanel inputCards = new JPanel(new CardLayout());
		sl_contentPane.putConstraint(SpringLayout.NORTH, inputCards, 0, SpringLayout.SOUTH, rdbtnBed);
		sl_contentPane.putConstraint(SpringLayout.WEST, inputCards, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, inputCards, 0, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, inputCards, -110, SpringLayout.SOUTH, contentPane);
		contentPane.add(inputCards);

		JPanel bedInputPane = new JPanel();
		SpringLayout sl_bedInputPane = new SpringLayout();
		bedInputPane.setLayout(sl_bedInputPane);
		bedInputPane.setBackground(Color.BLUE);
		inputCards.add(bedInputPane, "bed");

		JScrollPane scrollPaneBED = new JScrollPane();
		sl_bedInputPane.putConstraint(SpringLayout.NORTH, scrollPaneBED, 36, SpringLayout.NORTH, bedInputPane);
		sl_bedInputPane.putConstraint(SpringLayout.WEST, scrollPaneBED, 10, SpringLayout.WEST, bedInputPane);
		sl_bedInputPane.putConstraint(SpringLayout.EAST, scrollPaneBED, -10, SpringLayout.EAST, bedInputPane);
		sl_bedInputPane.putConstraint(SpringLayout.SOUTH, scrollPaneBED, -3, SpringLayout.SOUTH, bedInputPane);
		bedInputPane.add(scrollPaneBED);

		expBEDList = new DefaultListModel<String>();
		final JList<String> listBEDExp = new JList<String>(expBEDList);
		listBEDExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPaneBED.setViewportView(listBEDExp);

		btnLoadBED = new JButton("Load BED Files");
		sl_bedInputPane.putConstraint(SpringLayout.SOUTH, btnLoadBED, -6, SpringLayout.NORTH, scrollPaneBED);
		sl_bedInputPane.putConstraint(SpringLayout.WEST, btnLoadBED, 0, SpringLayout.WEST, scrollPaneBED);
		btnLoadBED.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File[] newBEDFiles = FileSelection.getFiles(fc, "bed", true);
				if (newBEDFiles != null) {
					for (int x = 0; x < newBEDFiles.length; x++) {
						BEDFiles.add(newBEDFiles[x]);
						expBEDList.addElement(newBEDFiles[x].getName());
					}
				}
			}
		});
		bedInputPane.add(btnLoadBED);

		btnRemoveBED = new JButton("Remove BED");
		sl_bedInputPane.putConstraint(SpringLayout.SOUTH, btnRemoveBED, -6, SpringLayout.NORTH, scrollPaneBED);
		sl_bedInputPane.putConstraint(SpringLayout.EAST, btnRemoveBED, 0, SpringLayout.EAST, scrollPaneBED);
		btnRemoveBED.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while (listBEDExp.getSelectedIndex() > -1) {
					BEDFiles.remove(listBEDExp.getSelectedIndex());
					expBEDList.remove(listBEDExp.getSelectedIndex());
				}
			}
		});
		bedInputPane.add(btnRemoveBED);
		
		
		JPanel gffInputPane = new JPanel();
		SpringLayout sl_gffInputPane = new SpringLayout();
		gffInputPane.setLayout(sl_gffInputPane);
		gffInputPane.setBackground(Color.CYAN);
		inputCards.add(gffInputPane, "gff");

		JScrollPane scrollPaneGFF = new JScrollPane();
		sl_gffInputPane.putConstraint(SpringLayout.NORTH, scrollPaneGFF, 36, SpringLayout.NORTH, gffInputPane);
		sl_gffInputPane.putConstraint(SpringLayout.WEST, scrollPaneGFF, 10, SpringLayout.WEST, gffInputPane);
		sl_gffInputPane.putConstraint(SpringLayout.EAST, scrollPaneGFF, -10, SpringLayout.EAST, gffInputPane);
		sl_bedInputPane.putConstraint(SpringLayout.SOUTH, scrollPaneGFF, -3, SpringLayout.SOUTH, gffInputPane);
		gffInputPane.add(scrollPaneGFF);

		expGFFList = new DefaultListModel<String>();
		final JList<String> listGFFExp = new JList<String>(expGFFList);
		listGFFExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPaneGFF.setViewportView(listGFFExp);

		btnLoadGFF = new JButton("Load GFF Files");
		sl_gffInputPane.putConstraint(SpringLayout.SOUTH, btnLoadGFF, -6, SpringLayout.NORTH, scrollPaneGFF);
		sl_gffInputPane.putConstraint(SpringLayout.WEST, btnLoadGFF, 0, SpringLayout.WEST, scrollPaneGFF);
		btnLoadGFF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File[] newGFFFiles = FileSelection.getFiles(fc, "gff", true);
				if (newGFFFiles != null) {
					for (int x = 0; x < newGFFFiles.length; x++) {
						GFFFiles.add(newGFFFiles[x]);
						expGFFList.addElement(newGFFFiles[x].getName());
					}
				}
			}
		});
		gffInputPane.add(btnLoadGFF);

		btnRemoveGFF = new JButton("Remove GFF");
		sl_gffInputPane.putConstraint(SpringLayout.SOUTH, btnRemoveGFF, -6, SpringLayout.NORTH, scrollPaneGFF);
		sl_gffInputPane.putConstraint(SpringLayout.EAST, btnRemoveGFF, 0, SpringLayout.EAST, scrollPaneGFF);
		btnRemoveBED.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while (listGFFExp.getSelectedIndex() > -1) {
					GFFFiles.remove(listGFFExp.getSelectedIndex());
					expGFFList.remove(listGFFExp.getSelectedIndex());
				}
			}
		});
		gffInputPane.add(btnRemoveGFF);

		btnExecute = new JButton("Transform");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnExecute, 160, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnExecute, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnExecute, -160, SpringLayout.EAST, contentPane);
		contentPane.add(btnExecute);

		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnExecute);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -5, SpringLayout.EAST, contentPane);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);

		btnExecute.setActionCommand("start");

		JLabel lblSizeOfShift = new JLabel("Size of Shift (bp):");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblSizeOfShift, 6, SpringLayout.SOUTH, inputCards);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblSizeOfShift, 10, SpringLayout.WEST, contentPane);
		contentPane.add(lblSizeOfShift);

		txtShift = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtShift, -3, SpringLayout.NORTH, lblSizeOfShift);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtShift, 10, SpringLayout.EAST, lblSizeOfShift);
		txtShift.setToolTipText("Must be integer. Negative values for upstream shift, positive values for downstream shift");
		txtShift.setHorizontalAlignment(SwingConstants.CENTER);
		txtShift.setColumns(10);
		txtShift.setText("0");
		contentPane.add(txtShift);

		chckbxStranded = new JCheckBox("Use stranded info");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxStranded, 0, SpringLayout.NORTH, lblSizeOfShift);
		sl_contentPane.putConstraint(SpringLayout.EAST, chckbxStranded, -20, SpringLayout.EAST, contentPane);
		chckbxStranded.setSelected(true);
		contentPane.add(chckbxStranded);

		chckbxGzipOutput = new JCheckBox("Gzip output");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxGzipOutput, 0, SpringLayout.SOUTH, chckbxStranded);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxGzipOutput, 0, SpringLayout.WEST, chckbxStranded);
		contentPane.add(chckbxGzipOutput);

		btnOutput = new JButton("Output Directory");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutput, 30, SpringLayout.SOUTH, inputCards);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnOutput, 10, SpringLayout.WEST, contentPane);
		btnOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OUT_DIR = FileSelection.getOutputDir(fc);
				if (OUT_DIR != null) {
					lblDefaultToLocal.setText(OUT_DIR.getAbsolutePath());
				}
			}
		});
		contentPane.add(btnOutput);

		lblCurrent = new JLabel("Current Output:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrent, 0, SpringLayout.WEST, btnOutput);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrent, 6, SpringLayout.SOUTH, btnOutput);
		lblCurrent.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblCurrent);

		lblDefaultToLocal = new JLabel("Default to Local Directory");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, lblCurrent);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 6, SpringLayout.EAST, lblCurrent);
		lblDefaultToLocal.setBackground(Color.WHITE);
		contentPane.add(lblDefaultToLocal);

		rdbtnBed.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (rdbtnBed.isSelected()) {
					CardLayout cl = (CardLayout)(inputCards.getLayout());
					cl.show(inputCards, "bed");
				}
			}
		});
		rdbtnGff.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (rdbtnGff.isSelected()) {
					CardLayout cl = (CardLayout)(inputCards.getLayout());
					cl.show(inputCards, "gff");
				}
			}
		});
		
		btnExecute.addActionListener(this);
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
			if (c instanceof Container) { massXable((Container) c, status); }
		}
	}

}