package window_interface.Read_Analysis;

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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import objects.PileupParameters;
import util.FileSelection;

@SuppressWarnings("serial")
public class TagPileupWindow extends JFrame implements ActionListener, PropertyChangeListener {
	private JPanel contentPane;
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));

	final DefaultListModel<String> expList;
	Vector<File> BAMFiles = new Vector<File>();
	final DefaultListModel<String> bedList;
	Vector<File> BEDFiles = new Vector<File>();
	private File OUT_DIR = new File(System.getProperty("user.dir"));
	
	private JButton btnPileup;
	private JButton btnLoadBamFiles;
	private JButton btnRemoveBam;
	private JButton btnOutputDirectory;
	private JButton btnSenseColor;
	private JButton btnAntiColor;
	private JButton btnCombinedColor;
	private JButton btnLoadBlacklistFilter;
	private JButton btnRemoveBlacklistfilter;
	private JRadioButton rdbtnFivePrime;
	private JRadioButton rdbtnThreePrime;
	private JRadioButton rdbtnMidpoint;
	private JRadioButton rdbtnRead1;
	private JRadioButton rdbtnRead2;
	private JRadioButton rdbtnAllReads;
	private JRadioButton rdbtnSeperate;
	private JRadioButton rdbtnComb;
	private JRadioButton rdbtnNone;
	private JRadioButton rdbtnGaussianSmooth;
	private JRadioButton rdbtnSlidingWindow;
	private JRadioButton rdbtnTabdelimited;
	private JRadioButton rdbtnCdt;

	private JLabel lblSelectOutput;
	private JLabel lblWindowSizebin;
	private JLabel lblTagShift;
	private JLabel lblStdDevSize;
	private JLabel lblNumStd;
	private JLabel lblDefaultToLocal;
	private JLabel lblCurrentOutput;
	private JLabel lblOutputMatrixFormat;
	private JLabel lblCurrentBlacklist;
	private JLabel lblCpusToUse;
	private JLabel lblNoBlacklistLoaded;
	private JTextField txtShift;
	private JTextField txtBin;
	private JTextField txtSmooth;
	private JTextField txtStdSize;
	private JTextField txtNumStd;
	private JTextField txtCPU;
	private JTextField txtCompositeName;
	private JCheckBox chckbxOutputData;
	private JCheckBox chckbxOutputCompositeData;
	private JCheckBox chckbxTagStandard;
	private JCheckBox chckbxRequireProperPe;
	private JCheckBox chckbxFilterByMin;
	private JCheckBox chckbxFilterByMax;
	private JCheckBox chckbxOutputGzip;

	JProgressBar progressBar;
	public Task task;
	private JTextField txtMin;
	private JTextField txtMax;
	private JSeparator sepReadManip;
	private JLabel lblReadManipulation;

	private File BLACKLIST = null;

	class Task extends SwingWorker<Void, Void> {
		@Override
		public Void doInBackground() throws IOException, InterruptedException {
			try {
				if (Integer.parseInt(txtBin.getText()) < 1) {
					JOptionPane.showMessageDialog(null, "Invalid Bin Size!!! Must be larger than 0 bp");
				} else if (rdbtnSlidingWindow.isSelected() && Integer.parseInt(txtSmooth.getText()) < 1) {
					JOptionPane.showMessageDialog(null, "Invalid Smoothing Window Size!!! Must be larger than 0 bp");
				} else if (rdbtnGaussianSmooth.isSelected() && Integer.parseInt(txtStdSize.getText()) < 1) {
					JOptionPane.showMessageDialog(null, "Invalid Standard Deviation Size!!! Must be larger than 0 bp");
				} else if (rdbtnGaussianSmooth.isSelected() && Integer.parseInt(txtNumStd.getText()) < 1) {
					JOptionPane.showMessageDialog(null, "Invalid Number of Standard Deviations!!! Must be larger than 0");
				} else if (Integer.parseInt(txtCPU.getText()) < 1) {
					JOptionPane.showMessageDialog(null, "Invalid Number of CPU's!!! Must use at least 1");
				} else if (chckbxFilterByMin.isSelected() && Integer.parseInt(txtMin.getText()) < 0) {
					JOptionPane.showMessageDialog(null, "Invalid Minimum Insert Size!!! Must be greater than or equal to 0 bp");
				} else if (chckbxFilterByMax.isSelected() && Integer.parseInt(txtMax.getText()) < 0) {
					JOptionPane.showMessageDialog(null, "Invalid Maximum Insert Size!!! Must be greater than or equal to 0 bp");
				} else if (chckbxFilterByMin.isSelected() && chckbxFilterByMax.isSelected() && Integer.parseInt(txtMax.getText()) < Integer.parseInt(txtMin.getText())) {
					JOptionPane.showMessageDialog(null, "Invalid Maximum & Minimum Insert Sizes!!! Maximum must be larger/equal to Minimum!");
				} else if (BEDFiles.size() < 1) {
					JOptionPane.showMessageDialog(null, "No BED Files Loaded!!!");
				} else if (BAMFiles.size() < 1) {
					JOptionPane.showMessageDialog(null, "No BAM Files Loaded!!!");
				} else {
					setProgress(0);
					// Load up parameters for the pileup into single object
					PileupParameters param = new PileupParameters();
					ArrayList<Color> colors = new ArrayList<Color>();
					if (rdbtnSeperate.isSelected()) {
						param.setStrand(0);
						colors.add(btnSenseColor.getForeground());
						colors.add(btnAntiColor.getForeground());
					} else if (rdbtnComb.isSelected()) {
						param.setStrand(1);
						colors.add(btnCombinedColor.getForeground());
					}

					if (rdbtnFivePrime.isSelected()) { param.setAspect(0); }
					else if (rdbtnThreePrime.isSelected()) { param.setAspect(1); }
					else if (rdbtnMidpoint.isSelected()) { param.setAspect(2); }

					if (rdbtnRead1.isSelected()) { param.setRead(0); }
					else if (rdbtnRead2.isSelected()) { param.setRead(1); }
					else if (rdbtnAllReads.isSelected()) { param.setRead(2); }

					if (chckbxRequireProperPe.isSelected()) { param.setPErequire(true); }
					else { param.setPErequire(false); }

					if (chckbxFilterByMin.isSelected()) { param.setMinInsert(Integer.parseInt(txtMin.getText())); }
					if (chckbxFilterByMax.isSelected()) { param.setMaxInsert(Integer.parseInt(txtMax.getText())); }

					if (rdbtnNone.isSelected()) { param.setTrans(0); }
					else if (rdbtnSlidingWindow.isSelected()) { param.setTrans(1); }
					else if (rdbtnGaussianSmooth.isSelected()) { param.setTrans(2); }

					if(!chckbxOutputData.isSelected() && !chckbxOutputCompositeData.isSelected()) {	param.setOutputDirectory(null); }
					else { param.setOutputDirectory(OUT_DIR); }

					param.setOutputCompositeStatus(chckbxOutputCompositeData.isSelected()); //Outputs composite plots if check box is selected
					if(chckbxOutputCompositeData.isSelected()) {
						try {
							param.setCompositePrintStream(new PrintStream(OUT_DIR + File.separator + txtCompositeName.getText()));
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}

					if (chckbxOutputData.isSelected()) {
						if (rdbtnTabdelimited.isSelected()) { param.setOutputType(1); }
						else if (rdbtnCdt.isSelected()) { param.setOutputType(2); }
						if (chckbxOutputGzip.isSelected()) { param.setGZIPstatus(true); }
					} else { param.setOutputType(0); }

					if (chckbxTagStandard.isSelected()) {
						if (BLACKLIST != null) { param.setBlacklist(BLACKLIST); }
						param.setStandard(true);
					} else {
						param.setStandard(false);
					}

					// SHIFT can be negative
					param.setShift(Integer.parseInt(txtShift.getText()));
					param.setBin(Integer.parseInt(txtBin.getText()));
					param.setSmooth(Integer.parseInt(txtSmooth.getText()));
					param.setStdSize(Integer.parseInt(txtStdSize.getText()));
					param.setStdNum(Integer.parseInt(txtNumStd.getText()));
					param.setCPU(Integer.parseInt(txtCPU.getText()));

					TagPileupOutput pile = new TagPileupOutput(BEDFiles, BAMFiles, param, colors);

					pile.addPropertyChangeListener("tag", new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
							int temp = (Integer) propertyChangeEvent.getNewValue();
							int percentComplete = (int) (((double) (temp) / (BAMFiles.size() * BEDFiles.size())) * 100);
							setProgress(percentComplete);
						}
					});


					pile.setVisible(true);
					pile.run();

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

	public TagPileupWindow() {
		setTitle("Tag Pileup");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(125, 125, 950, 585);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		JButton btnLoadBedFile = new JButton("Load BED Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoadBedFile, 4, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoadBedFile, 10, SpringLayout.WEST, contentPane);
		btnLoadBedFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File[] newBEDFiles = FileSelection.getFiles(fc, "bed");
				if (newBEDFiles != null) {
					for (int x = 0; x < newBEDFiles.length; x++) {
						BEDFiles.add(newBEDFiles[x]);
						bedList.addElement(newBEDFiles[x].getName());
					}
				}
			}
		});
		contentPane.add(btnLoadBedFile);

		JScrollPane scrollPane_BED = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane_BED, 10, SpringLayout.SOUTH, btnLoadBedFile);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane_BED, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane_BED, 185, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane_BED, -525, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane_BED);

		bedList = new DefaultListModel<String>();
		final JList<String> listBed = new JList<>(bedList);
		listBed.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane_BED.setViewportView(listBed);

		JButton btnRemoveBed = new JButton("Remove BED");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveBed, 4, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBed, 0, SpringLayout.EAST, scrollPane_BED);
		btnRemoveBed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				while (listBed.getSelectedIndex() > -1) {
					BEDFiles.remove(listBed.getSelectedIndex());
					bedList.remove(listBed.getSelectedIndex());
				}
			}
		});
		contentPane.add(btnRemoveBed);

		btnLoadBamFiles = new JButton("Load BAM Files");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoadBamFiles, 10, SpringLayout.SOUTH, scrollPane_BED);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoadBamFiles, 10, SpringLayout.WEST, contentPane);
		btnLoadBamFiles.addActionListener(new ActionListener() {
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
		contentPane.add(btnLoadBamFiles);

		JScrollPane scrollPane_BAM = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane_BAM, 10, SpringLayout.SOUTH, btnLoadBamFiles);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane_BAM, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane_BAM, 380, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane_BAM, -525, SpringLayout.EAST, contentPane);
		contentPane.add(scrollPane_BAM);

		expList = new DefaultListModel<String>();
		final JList<String> listExp = new JList<>(expList);
		listExp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane_BAM.setViewportView(listExp);

		btnRemoveBam = new JButton("Remove BAM");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveBam, 0, SpringLayout.NORTH, btnLoadBamFiles);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemoveBam, 0, SpringLayout.EAST, scrollPane_BAM);
		btnRemoveBam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while (listExp.getSelectedIndex() > -1) {
					BAMFiles.remove(listExp.getSelectedIndex());
					expList.remove(listExp.getSelectedIndex());
				}
			}
		});
		contentPane.add(btnRemoveBam);

		JLabel lblAspectRead = new JLabel("Select Read Aspect to Analyze:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblAspectRead, 8, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblAspectRead, 6, SpringLayout.EAST, btnRemoveBed);
		lblAspectRead.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblAspectRead);

		rdbtnFivePrime = new JRadioButton("5' End");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnFivePrime, 6, SpringLayout.SOUTH, lblAspectRead);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnFivePrime, 10, SpringLayout.EAST, scrollPane_BED);
		contentPane.add(rdbtnFivePrime);

		rdbtnThreePrime = new JRadioButton("3' End");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnThreePrime, 0, SpringLayout.NORTH, rdbtnFivePrime);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnThreePrime, 10, SpringLayout.EAST, rdbtnFivePrime);
		contentPane.add(rdbtnThreePrime);

		rdbtnMidpoint = new JRadioButton("Midpoint (Require PE)");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnMidpoint, 0, SpringLayout.NORTH, rdbtnFivePrime);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnMidpoint, 10, SpringLayout.EAST, rdbtnThreePrime);
		contentPane.add(rdbtnMidpoint);

		ButtonGroup AspectRead = new ButtonGroup();
		AspectRead.add(rdbtnFivePrime);
		AspectRead.add(rdbtnThreePrime);
		AspectRead.add(rdbtnMidpoint);
		rdbtnFivePrime.setSelected(true);
		
		JLabel lblOutputRead = new JLabel("Select Read to Output:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblOutputRead, 5, SpringLayout.SOUTH, rdbtnFivePrime);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblOutputRead, 0, SpringLayout.WEST, lblAspectRead);
		lblOutputRead.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblOutputRead);

		rdbtnRead1 = new JRadioButton("Read 1");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnRead1, 0, SpringLayout.NORTH, lblOutputRead);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnRead1, 20, SpringLayout.EAST, lblOutputRead);
		contentPane.add(rdbtnRead1);

		rdbtnRead2 = new JRadioButton("Read 2");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnRead2, 0, SpringLayout.NORTH, rdbtnRead1);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnRead2, 10, SpringLayout.EAST, rdbtnRead1);
		contentPane.add(rdbtnRead2);

		rdbtnAllReads = new JRadioButton("All Reads");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnAllReads,0, SpringLayout.NORTH, rdbtnRead1);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnAllReads, 10, SpringLayout.EAST, rdbtnRead2);
		contentPane.add(rdbtnAllReads);

		ButtonGroup OutputRead = new ButtonGroup();
		OutputRead.add(rdbtnRead1);
		OutputRead.add(rdbtnRead2);
		OutputRead.add(rdbtnAllReads);
		rdbtnRead1.setSelected(true);

		chckbxRequireProperPe = new JCheckBox("Require Proper Paired-End");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxRequireProperPe, 10, SpringLayout.SOUTH, lblOutputRead);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxRequireProperPe, 10, SpringLayout.WEST, lblOutputRead);
		sl_contentPane.putConstraint(SpringLayout.EAST, chckbxRequireProperPe, -175, SpringLayout.EAST, contentPane);
		contentPane.add(chckbxRequireProperPe);

		chckbxFilterByMin = new JCheckBox("Filter Min Insert Size (bp)");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxFilterByMin, 3, SpringLayout.SOUTH,
				chckbxRequireProperPe);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxFilterByMin, 0, SpringLayout.WEST, chckbxRequireProperPe);
		chckbxFilterByMin.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (chckbxFilterByMin.isSelected()) {
					txtMin.setEnabled(true);
					chckbxRequireProperPe.setSelected(true);
					chckbxRequireProperPe.setEnabled(false);
				} else {
					txtMin.setEnabled(false);
					if (!chckbxFilterByMax.isSelected() && !rdbtnMidpoint.isSelected()) {
						chckbxRequireProperPe.setEnabled(true);
					}
				}
			}
		});
		contentPane.add(chckbxFilterByMin);

		txtMin = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtMin, 2, SpringLayout.NORTH, chckbxFilterByMin);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtMin, 0, SpringLayout.EAST, chckbxFilterByMin);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtMin, 65, SpringLayout.EAST, chckbxFilterByMin);
		txtMin.setEnabled(false);
		txtMin.setHorizontalAlignment(SwingConstants.CENTER);
		txtMin.setText("0");
		contentPane.add(txtMin);
		txtMin.setColumns(10);

		chckbxFilterByMax = new JCheckBox("Filter Max Insert Size (bp)");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxFilterByMax, 3, SpringLayout.SOUTH,
				chckbxRequireProperPe);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxFilterByMax, 6, SpringLayout.EAST, txtMin);
		sl_contentPane.putConstraint(SpringLayout.EAST, chckbxFilterByMax, 190, SpringLayout.EAST, txtMin);
		chckbxFilterByMax.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (chckbxFilterByMax.isSelected()) {
					txtMax.setEnabled(true);
					chckbxRequireProperPe.setSelected(true);
					chckbxRequireProperPe.setEnabled(false);
				} else {
					txtMax.setEnabled(false);
					if (!chckbxFilterByMin.isSelected() && !rdbtnMidpoint.isSelected()) {
						chckbxRequireProperPe.setEnabled(true);
					}
				}
			}
		});
		contentPane.add(chckbxFilterByMax);

		txtMax = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtMax, 2, SpringLayout.NORTH, chckbxFilterByMin);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtMax, 0, SpringLayout.EAST, chckbxFilterByMax);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtMax, 65, SpringLayout.EAST, chckbxFilterByMax);
		txtMax.setEnabled(false);
		txtMax.setHorizontalAlignment(SwingConstants.CENTER);
		txtMax.setText("1000");
		contentPane.add(txtMax);
		txtMax.setColumns(10);

		JSeparator sepReadOutput = new JSeparator();
		sl_contentPane.putConstraint(SpringLayout.NORTH, sepReadOutput, 10, SpringLayout.SOUTH, chckbxFilterByMin);
		sl_contentPane.putConstraint(SpringLayout.WEST, sepReadOutput, 10, SpringLayout.EAST, scrollPane_BED);
		sl_contentPane.putConstraint(SpringLayout.EAST, sepReadOutput, -10, SpringLayout.EAST, contentPane);
		sepReadOutput.setForeground(Color.BLACK);
		contentPane.add(sepReadOutput);

		lblSelectOutput = new JLabel("Select Output Strands:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblSelectOutput, 3, SpringLayout.SOUTH, sepReadOutput);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblSelectOutput, 10, SpringLayout.EAST, scrollPane_BED);
		lblSelectOutput.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblSelectOutput);

		rdbtnSeperate = new JRadioButton("Seperate");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnSeperate, -3, SpringLayout.NORTH, lblSelectOutput);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnSeperate, 30, SpringLayout.EAST, lblSelectOutput);
		rdbtnSeperate.setSelected(true);
		rdbtnSeperate.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (rdbtnSeperate.isSelected()) {
					btnSenseColor.setEnabled(true);
					btnAntiColor.setEnabled(true);
					btnCombinedColor.setEnabled(false);
				}
			}
		});
		contentPane.add(rdbtnSeperate);

		rdbtnComb = new JRadioButton("Combined");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnComb, -3, SpringLayout.NORTH, lblSelectOutput);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnComb, 80, SpringLayout.EAST, rdbtnSeperate);
		rdbtnComb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (rdbtnComb.isSelected()) {
					btnSenseColor.setEnabled(false);
					btnAntiColor.setEnabled(false);
					btnCombinedColor.setEnabled(true);
				}
			}
		});
		contentPane.add(rdbtnComb);

		ButtonGroup ReadStrand = new ButtonGroup();
		ReadStrand.add(rdbtnSeperate);
		ReadStrand.add(rdbtnComb);
		rdbtnSeperate.setSelected(true);

		btnSenseColor = new JButton("Sense Color");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnSenseColor, 8, SpringLayout.SOUTH, lblSelectOutput);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnSenseColor, 10, SpringLayout.EAST, scrollPane_BED);
		btnSenseColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnSenseColor.setForeground(JColorChooser.showDialog(btnSenseColor, "Select an Output Color",
						btnSenseColor.getForeground()));
			}
		});
		btnSenseColor.setForeground(new Color(0, 0, 255));
		contentPane.add(btnSenseColor);

		btnAntiColor = new JButton("Anti Color");
		btnAntiColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAntiColor.setForeground(
						JColorChooser.showDialog(btnAntiColor, "Select an Output Color", btnAntiColor.getForeground()));
			}
		});
		btnAntiColor.setForeground(new Color(255, 0, 0));
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnAntiColor, 0, SpringLayout.NORTH, btnSenseColor);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnAntiColor, 75, SpringLayout.EAST, btnSenseColor);
		contentPane.add(btnAntiColor);

		btnCombinedColor = new JButton("Combined Color");
		btnCombinedColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnCombinedColor.setForeground(JColorChooser.showDialog(btnCombinedColor, "Select an Output Color",
						btnCombinedColor.getForeground()));
			}
		});
		btnCombinedColor.setEnabled(false);
		btnCombinedColor.setForeground(new Color(0, 0, 0));
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnCombinedColor, 0, SpringLayout.NORTH, btnSenseColor);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnCombinedColor, 75, SpringLayout.EAST, btnAntiColor);
		contentPane.add(btnCombinedColor);

		sepReadManip = new JSeparator();
		sl_contentPane.putConstraint(SpringLayout.NORTH, sepReadManip, 8, SpringLayout.SOUTH, btnSenseColor);
		sl_contentPane.putConstraint(SpringLayout.WEST, sepReadManip, 10, SpringLayout.EAST, scrollPane_BED);
		sl_contentPane.putConstraint(SpringLayout.EAST, sepReadManip, -10, SpringLayout.EAST, contentPane);
		sepReadManip.setForeground(Color.BLACK);
		contentPane.add(sepReadManip);

		lblReadManipulation = new JLabel("Read Manipulation:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblReadManipulation, 3, SpringLayout.SOUTH, sepReadManip);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblReadManipulation, 10, SpringLayout.EAST, scrollPane_BED);
		contentPane.add(lblReadManipulation);

		lblTagShift = new JLabel("Tag Shift (bp):");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblTagShift, 10, SpringLayout.SOUTH, lblReadManipulation);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblTagShift, 10, SpringLayout.EAST, scrollPane_BAM);
		lblTagShift.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblTagShift);

		txtShift = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtShift, -1, SpringLayout.NORTH, lblTagShift);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtShift, 10, SpringLayout.EAST, lblTagShift);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtShift, 70, SpringLayout.EAST, lblTagShift);
		txtShift.setHorizontalAlignment(SwingConstants.CENTER);
		txtShift.setText("0");
		contentPane.add(txtShift);
		txtShift.setColumns(10);

		JLabel lblBinSizebp = new JLabel("Bin Size (bp):");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblBinSizebp, 0, SpringLayout.NORTH, lblTagShift);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblBinSizebp, 10, SpringLayout.EAST, txtShift);
		lblBinSizebp.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblBinSizebp);

		txtBin = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtBin, -1, SpringLayout.NORTH, lblTagShift);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtBin, 10, SpringLayout.EAST, lblBinSizebp);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtBin, 70, SpringLayout.EAST, lblBinSizebp);
		txtBin.setText("1");
		txtBin.setHorizontalAlignment(SwingConstants.CENTER);
		txtBin.setColumns(10);
		contentPane.add(txtBin);

		lblCpusToUse = new JLabel("CPU's to Use:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCpusToUse, 0, SpringLayout.NORTH, lblTagShift);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCpusToUse, 10, SpringLayout.EAST, txtBin);
		lblCpusToUse.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblCpusToUse);

		txtCPU = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtCPU, -1, SpringLayout.NORTH, lblTagShift);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtCPU, 10, SpringLayout.EAST, lblCpusToUse);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtCPU, 70, SpringLayout.EAST, lblCpusToUse);
		txtCPU.setHorizontalAlignment(SwingConstants.CENTER);
		txtCPU.setText("1");
		contentPane.add(txtCPU);
		txtCPU.setColumns(10);

		chckbxTagStandard = new JCheckBox("Set Tags to Be Equal");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxTagStandard, 40, SpringLayout.SOUTH,
				lblReadManipulation);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxTagStandard, 10, SpringLayout.EAST, scrollPane_BAM);
		contentPane.add(chckbxTagStandard);

		btnLoadBlacklistFilter = new JButton("Load Blacklist Filter");
		btnLoadBlacklistFilter.setEnabled(false);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnLoadBlacklistFilter, -1, SpringLayout.NORTH,
				chckbxTagStandard);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnLoadBlacklistFilter, 6, SpringLayout.EAST,
				chckbxTagStandard);
		contentPane.add(btnLoadBlacklistFilter);

		btnRemoveBlacklistfilter = new JButton("Remove BlacklistFilter");
		btnRemoveBlacklistfilter.setEnabled(false);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemoveBlacklistfilter, -1, SpringLayout.NORTH,
				chckbxTagStandard);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnRemoveBlacklistfilter, 0, SpringLayout.WEST, rdbtnComb);
		contentPane.add(btnRemoveBlacklistfilter);

		lblCurrentBlacklist = new JLabel("Current Blacklist:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrentBlacklist, 10, SpringLayout.EAST, scrollPane_BAM);
		lblCurrentBlacklist.setEnabled(false);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrentBlacklist, 5, SpringLayout.SOUTH,
				chckbxTagStandard);
		contentPane.add(lblCurrentBlacklist);

		lblNoBlacklistLoaded = new JLabel("No Blacklist Loaded");
		sl_contentPane.putConstraint(SpringLayout.EAST, lblNoBlacklistLoaded, -10, SpringLayout.EAST, contentPane);
		lblNoBlacklistLoaded.setEnabled(false);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblNoBlacklistLoaded, 0, SpringLayout.NORTH,
				lblCurrentBlacklist);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblNoBlacklistLoaded, 6, SpringLayout.EAST,
				lblCurrentBlacklist);
		lblNoBlacklistLoaded.setFont(new Font("Dialog", Font.PLAIN, 12));
		contentPane.add(lblNoBlacklistLoaded);

		JSeparator sepComposite = new JSeparator();
		sl_contentPane.putConstraint(SpringLayout.NORTH, sepComposite, 5, SpringLayout.SOUTH, lblCurrentBlacklist);
		sl_contentPane.putConstraint(SpringLayout.WEST, sepComposite, 10, SpringLayout.EAST, scrollPane_BAM);
		sl_contentPane.putConstraint(SpringLayout.EAST, sepComposite, -10, SpringLayout.EAST, contentPane);
		sepComposite.setForeground(Color.BLACK);
		contentPane.add(sepComposite);

		JLabel lblPleaseSelectComposite = new JLabel("Composite Transformation:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblPleaseSelectComposite, 3, SpringLayout.SOUTH,
				sepComposite);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblPleaseSelectComposite, 10, SpringLayout.EAST,
				scrollPane_BAM);
		lblPleaseSelectComposite.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblPleaseSelectComposite);

		rdbtnNone = new JRadioButton("None");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnNone, 6, SpringLayout.SOUTH, lblPleaseSelectComposite);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnNone, 10, SpringLayout.EAST, scrollPane_BAM);
		contentPane.add(rdbtnNone);

		rdbtnGaussianSmooth = new JRadioButton("Gaussian Smooth");
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnGaussianSmooth, 10, SpringLayout.EAST, scrollPane_BAM);
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnGaussianSmooth, 3, SpringLayout.SOUTH, rdbtnNone);
		contentPane.add(rdbtnGaussianSmooth);

		rdbtnSlidingWindow = new JRadioButton("Sliding Window");
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnSlidingWindow, 0, SpringLayout.NORTH, rdbtnNone);
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnSlidingWindow, 10, SpringLayout.EAST, rdbtnNone);
		contentPane.add(rdbtnSlidingWindow);

		ButtonGroup trans = new ButtonGroup();
		trans.add(rdbtnNone);
		trans.add(rdbtnSlidingWindow);
		trans.add(rdbtnGaussianSmooth);
		rdbtnNone.setSelected(true);

		lblStdDevSize = new JLabel("Std Dev Size (Bin #):");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblStdDevSize, 3, SpringLayout.NORTH, rdbtnGaussianSmooth);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblStdDevSize, 6, SpringLayout.EAST, rdbtnGaussianSmooth);
		lblStdDevSize.setEnabled(false);
		lblStdDevSize.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblStdDevSize);

		lblNumStd = new JLabel("# of Std Deviations:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblNumStd, 0, SpringLayout.NORTH, lblStdDevSize);
		lblNumStd.setEnabled(false);
		lblNumStd.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblNumStd);

		txtStdSize = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.EAST, txtStdSize, 50, SpringLayout.EAST, lblStdDevSize);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblNumStd, 8, SpringLayout.EAST, txtStdSize);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtStdSize, 8, SpringLayout.EAST, lblStdDevSize);
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtStdSize, -1, SpringLayout.NORTH, lblStdDevSize);
		txtStdSize.setEnabled(false);
		txtStdSize.setHorizontalAlignment(SwingConstants.CENTER);
		txtStdSize.setText("5");
		contentPane.add(txtStdSize);
		txtStdSize.setColumns(10);

		txtNumStd = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtNumStd, -1, SpringLayout.NORTH, lblStdDevSize);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtNumStd, 8, SpringLayout.EAST, lblNumStd);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtNumStd, 50, SpringLayout.EAST, lblNumStd);
		txtNumStd.setEnabled(false);
		txtNumStd.setHorizontalAlignment(SwingConstants.CENTER);
		txtNumStd.setText("3");
		contentPane.add(txtNumStd);
		txtNumStd.setColumns(10);

		lblWindowSizebin = new JLabel("Window Size (Bin #):");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblWindowSizebin, 3, SpringLayout.NORTH, rdbtnNone);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblWindowSizebin, 10, SpringLayout.EAST, rdbtnSlidingWindow);
		lblWindowSizebin.setEnabled(false);
		lblWindowSizebin.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblWindowSizebin);

		txtSmooth = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtSmooth, 2, SpringLayout.NORTH, rdbtnNone);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtSmooth, 10, SpringLayout.EAST, lblWindowSizebin);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtSmooth, 80, SpringLayout.EAST, lblWindowSizebin);
		txtSmooth.setHorizontalAlignment(SwingConstants.CENTER);
		txtSmooth.setEnabled(false);
		txtSmooth.setText("3");
		contentPane.add(txtSmooth);
		txtSmooth.setColumns(10);

		JSeparator sepOutput = new JSeparator();
		sl_contentPane.putConstraint(SpringLayout.NORTH, sepOutput, 15, SpringLayout.SOUTH, scrollPane_BAM);
		sl_contentPane.putConstraint(SpringLayout.WEST, sepOutput, 0, SpringLayout.WEST, scrollPane_BAM);
		sl_contentPane.putConstraint(SpringLayout.EAST, sepOutput, -10, SpringLayout.EAST, contentPane);
		sepOutput.setForeground(Color.BLACK);
		contentPane.add(sepOutput);

		JLabel lblOutputParameters = new JLabel("Output Parameters:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblOutputParameters, 8, SpringLayout.SOUTH, sepOutput);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblOutputParameters, 10, SpringLayout.WEST, contentPane);
		contentPane.add(lblOutputParameters);

		chckbxOutputData = new JCheckBox("Output Heatmap Matrix");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxOutputData, 8, SpringLayout.SOUTH, lblOutputParameters);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxOutputData, 0, SpringLayout.WEST, scrollPane_BAM);
		chckbxOutputData.setSelected(true);
		contentPane.add(chckbxOutputData);

		rdbtnTabdelimited = new JRadioButton("TAB-Delimited");
		contentPane.add(rdbtnTabdelimited);
		rdbtnCdt = new JRadioButton("CDT");
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnCdt, 6, SpringLayout.EAST, rdbtnTabdelimited);
		contentPane.add(rdbtnCdt);
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnTabdelimited, 0, SpringLayout.NORTH, chckbxOutputData);
		sl_contentPane.putConstraint(SpringLayout.NORTH, rdbtnCdt, 0, SpringLayout.NORTH, chckbxOutputData);

		ButtonGroup output = new ButtonGroup();
		output.add(rdbtnTabdelimited);
		output.add(rdbtnCdt);
		rdbtnCdt.setSelected(true);

		chckbxOutputGzip = new JCheckBox("Output GZIP");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxOutputGzip, 0, SpringLayout.NORTH, rdbtnCdt);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxOutputGzip, 10, SpringLayout.EAST, rdbtnCdt);
		contentPane.add(chckbxOutputGzip);

		lblOutputMatrixFormat = new JLabel("Matrix File Format:");
		sl_contentPane.putConstraint(SpringLayout.WEST, rdbtnTabdelimited, 10, SpringLayout.EAST,
				lblOutputMatrixFormat);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblOutputMatrixFormat, 10, SpringLayout.EAST, chckbxOutputData);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblOutputMatrixFormat, 3, SpringLayout.NORTH,
				chckbxOutputData);
		lblOutputMatrixFormat.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblOutputMatrixFormat);

		chckbxOutputCompositeData = new JCheckBox("Output Composite");
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxOutputCompositeData, 6, SpringLayout.SOUTH,
				chckbxOutputData);
		chckbxOutputCompositeData.setSelected(true);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxOutputCompositeData, 0, SpringLayout.WEST,
				scrollPane_BAM);
		contentPane.add(chckbxOutputCompositeData);

		txtCompositeName = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtCompositeName, 2, SpringLayout.NORTH,
				chckbxOutputCompositeData);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtCompositeName, 10, SpringLayout.EAST,
				chckbxOutputCompositeData);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtCompositeName, 250, SpringLayout.EAST,
				chckbxOutputCompositeData);
		txtCompositeName.setText("composite_average.out");
		contentPane.add(txtCompositeName);
		txtCompositeName.setColumns(10);

		btnOutputDirectory = new JButton("Output Directory");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnOutputDirectory, 10, SpringLayout.SOUTH,
				chckbxOutputCompositeData);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnOutputDirectory, 10, SpringLayout.WEST, contentPane);
		contentPane.add(btnOutputDirectory);

		lblDefaultToLocal = new JLabel("Default to Local Directory");
		sl_contentPane.putConstraint(SpringLayout.EAST, lblDefaultToLocal, -13, SpringLayout.EAST, contentPane);
		lblDefaultToLocal.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblDefaultToLocal.setBackground(Color.WHITE);
		contentPane.add(lblDefaultToLocal);

		lblCurrentOutput = new JLabel("Current Output:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblCurrentOutput, 4, SpringLayout.NORTH, btnOutputDirectory);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblDefaultToLocal, 10, SpringLayout.EAST, lblCurrentOutput);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblDefaultToLocal, 1, SpringLayout.NORTH, lblCurrentOutput);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblCurrentOutput, 0, SpringLayout.WEST, txtCompositeName);
		lblCurrentOutput.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		contentPane.add(lblCurrentOutput);

		rdbtnFivePrime.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (rdbtnFivePrime.isSelected()) {
					allowReadChoice(true);
				}
			}
		});

		rdbtnThreePrime.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (rdbtnThreePrime.isSelected()) {
					allowReadChoice(true);
				}
			}
		});

		rdbtnMidpoint.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (rdbtnMidpoint.isSelected()) {
					allowReadChoice(false);
					rdbtnComb.setSelected(true);
					chckbxRequireProperPe.setSelected(true);
					chckbxRequireProperPe.setEnabled(false);
				} else if (!chckbxFilterByMin.isSelected() && !chckbxFilterByMax.isSelected()) {
					chckbxRequireProperPe.setEnabled(true);
				}
			}
		});

		rdbtnNone.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (rdbtnNone.isSelected()) {
					lblWindowSizebin.setEnabled(false);
					lblStdDevSize.setEnabled(false);
					lblNumStd.setEnabled(false);
					txtSmooth.setEnabled(false);
					txtStdSize.setEnabled(false);
					txtNumStd.setEnabled(false);
				}
			}
		});
		rdbtnSlidingWindow.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (rdbtnSlidingWindow.isSelected()) {
					lblWindowSizebin.setEnabled(true);
					lblStdDevSize.setEnabled(false);
					lblNumStd.setEnabled(false);
					txtSmooth.setEnabled(true);
					txtStdSize.setEnabled(false);
					txtNumStd.setEnabled(false);
				}
			}
		});
		rdbtnGaussianSmooth.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (rdbtnGaussianSmooth.isSelected()) {
					lblWindowSizebin.setEnabled(false);
					lblStdDevSize.setEnabled(true);
					lblNumStd.setEnabled(true);
					txtSmooth.setEnabled(false);
					txtStdSize.setEnabled(true);
					txtNumStd.setEnabled(true);
				}
			}
		});
		;

		chckbxOutputData.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (chckbxOutputData.isSelected()) {
					activateOutput(true);
					lblOutputMatrixFormat.setEnabled(true);
					rdbtnTabdelimited.setEnabled(true);
					rdbtnCdt.setEnabled(true);
					chckbxOutputGzip.setEnabled(true);
				} else {
					lblOutputMatrixFormat.setEnabled(false);
					rdbtnTabdelimited.setEnabled(false);
					rdbtnCdt.setEnabled(false);
					chckbxOutputGzip.setEnabled(false);
					if (!chckbxOutputCompositeData.isSelected()) {
						activateOutput(false);
					}
				}
			}
		});

		chckbxOutputCompositeData.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (chckbxOutputCompositeData.isSelected()) {
					txtCompositeName.setEnabled(true);
					activateOutput(true);
				} else {
					txtCompositeName.setEnabled(false);
					if (!chckbxOutputData.isSelected()) {
						activateOutput(false);
					}
				}
			}
		});

		btnOutputDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OUT_DIR = FileSelection.getOutputDir(fc);
				if (OUT_DIR != null) {
					lblDefaultToLocal.setText(OUT_DIR.getAbsolutePath());
				}
			}
		});

		chckbxTagStandard.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (chckbxTagStandard.isSelected()) {
					activateBlacklist(true);
				} else {
					activateBlacklist(false);
				}
			}
		});

		btnLoadBlacklistFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File newBlack = FileSelection.getFile(fc, "bed");
				if (newBlack != null) {
					BLACKLIST = newBlack.getAbsoluteFile();
					lblNoBlacklistLoaded.setText(newBlack.getName());
				}
			}
		});

		btnRemoveBlacklistfilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BLACKLIST = null;
				lblNoBlacklistLoaded.setText("No Blacklist Loaded");
			}
		});

		btnPileup = new JButton("Pile Tags");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnPileup, 400, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnPileup, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnPileup, -400, SpringLayout.EAST, contentPane);
		contentPane.add(btnPileup);

		progressBar = new JProgressBar();
		sl_contentPane.putConstraint(SpringLayout.NORTH, progressBar, 3, SpringLayout.NORTH, btnPileup);
		sl_contentPane.putConstraint(SpringLayout.WEST, progressBar, -200, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -10, SpringLayout.EAST, contentPane);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		btnPileup.setActionCommand("start");

		btnPileup.addActionListener(this);
	}

	public void allowReadChoice(boolean activate) {
		// Allow Strand choice
		rdbtnComb.setEnabled(activate);
		rdbtnSeperate.setEnabled(activate);
		// Allow Read type choice
		rdbtnRead1.setEnabled(activate);
		rdbtnRead2.setEnabled(activate);
		rdbtnAllReads.setEnabled(activate);
	}

	public void activateOutput(boolean activate) {
		btnOutputDirectory.setEnabled(activate);
		lblDefaultToLocal.setEnabled(activate);
		lblCurrentOutput.setEnabled(activate);
	}

	public void activateBlacklist(boolean activate) {
		btnLoadBlacklistFilter.setEnabled(activate);
		btnRemoveBlacklistfilter.setEnabled(activate);
		lblCurrentBlacklist.setEnabled(activate);
		lblNoBlacklistLoaded.setEnabled(activate);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		massXable(contentPane, false);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		task = new Task();
		task.addPropertyChangeListener(this);
		task.execute();
	}

	public void massXable(Container con, boolean status) {
		Component[] components = con.getComponents();
		for (Component component : components) {
			component.setEnabled(status);
			if (component instanceof Container) {
				massXable((Container) component, status);
			}
		}
		if (status) {
			if (rdbtnMidpoint.isSelected()) {
				chckbxRequireProperPe.setEnabled(false);
				allowReadChoice(false);
			}
			if (chckbxFilterByMin.isSelected() || chckbxFilterByMax.isSelected()) {
				chckbxRequireProperPe.setEnabled(false);
			}
			if (!chckbxFilterByMin.isSelected()) {
				txtMin.setEnabled(false);
			}
			if (!chckbxFilterByMax.isSelected()) {
				txtMax.setEnabled(false);
			}
			if (rdbtnSeperate.isSelected()) {
				btnSenseColor.setEnabled(true);
				btnAntiColor.setEnabled(true);
				btnCombinedColor.setEnabled(false);
			} else {
				btnSenseColor.setEnabled(false);
				btnAntiColor.setEnabled(false);
				btnCombinedColor.setEnabled(true);
			}
			if (rdbtnNone.isSelected()) {
				lblWindowSizebin.setEnabled(false);
				lblStdDevSize.setEnabled(false);
				lblNumStd.setEnabled(false);
				txtSmooth.setEnabled(false);
				txtStdSize.setEnabled(false);
				txtNumStd.setEnabled(false);
			}
			if (rdbtnGaussianSmooth.isSelected()) {
				lblWindowSizebin.setEnabled(false);
				lblStdDevSize.setEnabled(true);
				lblNumStd.setEnabled(true);
				txtSmooth.setEnabled(false);
				txtStdSize.setEnabled(true);
				txtNumStd.setEnabled(true);
			}
			if (rdbtnSlidingWindow.isSelected()) {
				lblWindowSizebin.setEnabled(true);
				lblStdDevSize.setEnabled(false);
				lblNumStd.setEnabled(false);
				txtSmooth.setEnabled(true);
				txtStdSize.setEnabled(false);
				txtNumStd.setEnabled(false);
			}
			if (!chckbxOutputData.isSelected()) {
				lblOutputMatrixFormat.setEnabled(false);
				rdbtnTabdelimited.setEnabled(false);
				rdbtnCdt.setEnabled(false);
				chckbxOutputGzip.setEnabled(false);
			}
			if (!chckbxOutputCompositeData.isSelected()) {
				txtCompositeName.setEnabled(false);
			}
			if (!chckbxOutputData.isSelected() && !chckbxOutputCompositeData.isSelected()) {
				activateOutput(false);
			}
			if (chckbxTagStandard.isSelected()) {
				activateBlacklist(true);
			} else {
				activateBlacklist(false);
			}
		}
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
}
