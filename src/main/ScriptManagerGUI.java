package main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import objects.GradientButton;
import objects.ToolDescriptions;

import window_interface.BAM_Statistics.PEStatWindow;
import window_interface.BAM_Statistics.SEStatWindow;
import window_interface.BAM_Statistics.BAMGenomeCorrelationWindow;
import window_interface.BAM_Manipulation.BAIIndexerWindow;
import window_interface.BAM_Manipulation.BAMMarkDupWindow;
import window_interface.BAM_Manipulation.FilterforPIPseqWindow;
import window_interface.BAM_Manipulation.MergeBAMWindow;
import window_interface.BAM_Manipulation.SortBAMWindow;
import window_interface.BAM_Format_Converter.BAMtoBEDWindow;
import window_interface.BAM_Format_Converter.BAMtoGFFWindow;
import window_interface.BAM_Format_Converter.BAMtobedGraphWindow;
import window_interface.BAM_Format_Converter.BAMtoscIDXWindow;
import window_interface.Peak_Analysis.BEDPeakAligntoRefWindow;
import window_interface.Peak_Analysis.FilterBEDbyProximityWindow;
import window_interface.Peak_Analysis.RandomCoordinateWindow;
import window_interface.Peak_Analysis.SignalDuplicationWindow;
import window_interface.Peak_Analysis.TileGenomeWindow;
import window_interface.Peak_Calling.GeneTrackWindow;
import window_interface.Peak_Calling.PeakPairWindow;
import window_interface.Coordinate_Manipulation.BED_Manipulation.BEDtoGFFWindow;
import window_interface.Coordinate_Manipulation.BED_Manipulation.ExpandBEDWindow;
import window_interface.Coordinate_Manipulation.BED_Manipulation.SortBEDWindow;
import window_interface.Coordinate_Manipulation.GFF_Manipulation.ExpandGFFWindow;
import window_interface.Coordinate_Manipulation.GFF_Manipulation.GFFtoBEDWindow;
import window_interface.Coordinate_Manipulation.GFF_Manipulation.SortGFFWindow;
import window_interface.File_Utilities.MD5ChecksumWindow;
import window_interface.File_Utilities.ConvertBEDChrNamesWindow;
import window_interface.File_Utilities.ConvertGFFChrNamesWindow;
import window_interface.Read_Analysis.AggregateDataWindow;
import window_interface.Read_Analysis.ScaleMatrixWindow;
import window_interface.Read_Analysis.ScalingFactorWindow;
import window_interface.Read_Analysis.TagPileupWindow;
import window_interface.Sequence_Analysis.DNAShapefromBEDWindow;
import window_interface.Sequence_Analysis.DNAShapefromFASTAWindow;
import window_interface.Sequence_Analysis.FASTAExtractWindow;
import window_interface.Sequence_Analysis.RandomizeFASTAWindow;
import window_interface.Sequence_Analysis.SearchMotifWindow;
import window_interface.Figure_Generation.FourColorSequenceWindow;
import window_interface.Figure_Generation.TwoColorHeatMapWindow;
import window_interface.Figure_Generation.ThreeColorHeatMapWindow;
import window_interface.Figure_Generation.MergeHeatMapWindow;
import window_interface.Figure_Generation.LabelHeatMapWindow;

public class ScriptManagerGUI {
	private JFrame frmScriptManager;
	Color picardColor = new Color(255,102,0);

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmScriptManager = new JFrame();
		frmScriptManager.setTitle("Script Manager v" + ToolDescriptions.VERSION);
		frmScriptManager.setBounds(100, 100, 600, 350);
		frmScriptManager.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmScriptManager.setResizable(false);
		SpringLayout springLayout = new SpringLayout();
		frmScriptManager.getContentPane().setLayout(springLayout);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		springLayout.putConstraint(SpringLayout.NORTH, tabbedPane, 10, SpringLayout.NORTH,
				frmScriptManager.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, tabbedPane, 10, SpringLayout.WEST,
				frmScriptManager.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, tabbedPane, -10, SpringLayout.SOUTH,
				frmScriptManager.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, tabbedPane, -10, SpringLayout.EAST,
				frmScriptManager.getContentPane());
		frmScriptManager.getContentPane().add(tabbedPane);

		// >>>>>>>> BAM_Statistics <<<<<<<<
		JPanel pnlStat = new JPanel();
		SpringLayout sl_pnlStat = new SpringLayout();
		pnlStat.setLayout(sl_pnlStat);
		tabbedPane.addTab("BAM Statistics", null, pnlStat, null);

		// >SEStats
		JTextArea txtOutputAlignmentStatistics = new JTextArea();
		initializeTextArea(txtOutputAlignmentStatistics);
		txtOutputAlignmentStatistics.setText(ToolDescriptions.se_stat_description);
		sl_pnlStat.putConstraint(SpringLayout.NORTH, txtOutputAlignmentStatistics, 10, SpringLayout.NORTH, pnlStat);
		sl_pnlStat.putConstraint(SpringLayout.EAST, txtOutputAlignmentStatistics, -10, SpringLayout.EAST, pnlStat);
		pnlStat.add(txtOutputAlignmentStatistics);

		JButton btnBAMStats = new JButton("BAM Statistics");
		btnBAMStats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							SEStatWindow frame = new SEStatWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlStat.putConstraint(SpringLayout.NORTH, btnBAMStats, 0, SpringLayout.NORTH, txtOutputAlignmentStatistics);
		sl_pnlStat.putConstraint(SpringLayout.WEST, btnBAMStats, 10, SpringLayout.WEST, pnlStat);
		sl_pnlStat.putConstraint(SpringLayout.WEST, txtOutputAlignmentStatistics, 10, SpringLayout.EAST, btnBAMStats);
		pnlStat.add(btnBAMStats);

		// >PEStats
		JTextArea txtPEStats = new JTextArea();
		initializeTextArea(txtPEStats);
		txtPEStats.setText(ToolDescriptions.pe_stat_description);
		sl_pnlStat.putConstraint(SpringLayout.NORTH, txtPEStats, 10, SpringLayout.SOUTH, txtOutputAlignmentStatistics);
// 		sl_pnlStat.putConstraint(SpringLayout.NORTH, txtPEStats, 10, SpringLayout.SOUTH, btnBAMStats);
		sl_pnlStat.putConstraint(SpringLayout.EAST, txtPEStats, -10, SpringLayout.EAST, pnlStat);
		pnlStat.add(txtPEStats);

		JButton btnPEStats = new JButton("Paired-End Statistics");
		btnPEStats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							PEStatWindow frame = new PEStatWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlStat.putConstraint(SpringLayout.NORTH, btnPEStats, 0, SpringLayout.NORTH, txtPEStats);
		sl_pnlStat.putConstraint(SpringLayout.WEST, btnPEStats, 10, SpringLayout.WEST, pnlStat);
		sl_pnlStat.putConstraint(SpringLayout.WEST, txtPEStats, 10, SpringLayout.EAST, btnPEStats);
		pnlStat.add(btnPEStats);

		// >BAMCorr
		JTextArea txtBamGenomeCorrelation = new JTextArea();
		initializeTextArea(txtBamGenomeCorrelation);
		txtBamGenomeCorrelation.setText(ToolDescriptions.bam_correlation_description);
		sl_pnlStat.putConstraint(SpringLayout.NORTH, txtBamGenomeCorrelation, 10, SpringLayout.SOUTH, txtPEStats);
// 		sl_pnlStat.putConstraint(SpringLayout.NORTH, txtBamGenomeCorrelation, 10, SpringLayout.SOUTH, btnPEStats);
		sl_pnlStat.putConstraint(SpringLayout.EAST, txtBamGenomeCorrelation, -10, SpringLayout.EAST, pnlStat);
		pnlStat.add(txtBamGenomeCorrelation);

		JButton btnBamGenomeCorrelation = new JButton("BAM Genome Correlation");
		btnBamGenomeCorrelation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							BAMGenomeCorrelationWindow frame = new BAMGenomeCorrelationWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlStat.putConstraint(SpringLayout.NORTH, btnBamGenomeCorrelation, 0, SpringLayout.NORTH,
				txtBamGenomeCorrelation);
		sl_pnlStat.putConstraint(SpringLayout.WEST, btnBamGenomeCorrelation, 10, SpringLayout.WEST, pnlStat);
		sl_pnlStat.putConstraint(SpringLayout.WEST, txtBamGenomeCorrelation, 10, SpringLayout.EAST,
				btnBamGenomeCorrelation);
		pnlStat.add(btnBamGenomeCorrelation);

		// >>>>>>>> BAM_Manipulation <<<<<<<<
		JPanel pnlBamManip = new JPanel();
		SpringLayout sl_pnlBamManip = new SpringLayout();
		pnlBamManip.setLayout(sl_pnlBamManip);
		tabbedPane.addTab("BAM Manipulation", null, pnlBamManip, null);

		// >BAMIndexer
		JTextArea txtBAIIndex = new JTextArea();
		initializeTextArea(txtBAIIndex);
		sl_pnlBamManip.putConstraint(SpringLayout.NORTH, txtBAIIndex, 10, SpringLayout.NORTH, pnlBamManip);
		sl_pnlBamManip.putConstraint(SpringLayout.EAST, txtBAIIndex, -10, SpringLayout.EAST, pnlBamManip);
		txtBAIIndex.setText(ToolDescriptions.bam_indexer_description);
		pnlBamManip.add(txtBAIIndex);

		JButton btnBaiIndexer = new JButton("BAM-BAI Indexer");
		btnBaiIndexer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							BAIIndexerWindow frame = new BAIIndexerWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlBamManip.putConstraint(SpringLayout.NORTH, btnBaiIndexer, 0, SpringLayout.NORTH, txtBAIIndex);
		sl_pnlBamManip.putConstraint(SpringLayout.WEST, btnBaiIndexer, 10, SpringLayout.WEST, pnlBamManip);
		sl_pnlBamManip.putConstraint(SpringLayout.WEST, txtBAIIndex, 10, SpringLayout.EAST, btnBaiIndexer);
		pnlBamManip.add(btnBaiIndexer);

		// >BAMFileSorter
		JTextArea txtBamSort = new JTextArea();
		initializeTextArea(txtBamSort);
		sl_pnlBamManip.putConstraint(SpringLayout.NORTH, txtBamSort, 10, SpringLayout.SOUTH, txtBAIIndex);
		sl_pnlBamManip.putConstraint(SpringLayout.EAST, txtBamSort, -10, SpringLayout.EAST, pnlBamManip);
		txtBamSort.setText(ToolDescriptions.sort_bam_description);
		pnlBamManip.add(txtBamSort);

		JButton btnBamSort = new JButton("BAM File Sorter");
		btnBamSort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							SortBAMWindow frame = new SortBAMWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlBamManip.putConstraint(SpringLayout.NORTH, btnBamSort, 0, SpringLayout.NORTH, txtBamSort);
		sl_pnlBamManip.putConstraint(SpringLayout.WEST, btnBamSort, 10, SpringLayout.WEST, pnlBamManip);
		sl_pnlBamManip.putConstraint(SpringLayout.WEST, txtBamSort, 10, SpringLayout.EAST, btnBamSort);
		pnlBamManip.add(btnBamSort);

		// >BAMRemoveDup
		JTextArea txtMarkDuplicates = new JTextArea();
		initializeTextArea(txtMarkDuplicates);
		sl_pnlBamManip.putConstraint(SpringLayout.NORTH, txtMarkDuplicates, 10, SpringLayout.SOUTH, txtBamSort);
		sl_pnlBamManip.putConstraint(SpringLayout.EAST, txtMarkDuplicates, -10, SpringLayout.EAST, pnlBamManip);
		txtMarkDuplicates.setText(ToolDescriptions.remove_duplicates_description);
		pnlBamManip.add(txtMarkDuplicates);

		JButton btnMarkDuplicates = new GradientButton("BAM MarkDuplicates");
		btnMarkDuplicates.setBackground(picardColor);
		btnMarkDuplicates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							BAMMarkDupWindow frame = new BAMMarkDupWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlBamManip.putConstraint(SpringLayout.NORTH, btnMarkDuplicates, 0, SpringLayout.NORTH,	txtMarkDuplicates);
		sl_pnlBamManip.putConstraint(SpringLayout.WEST, btnMarkDuplicates, 10, SpringLayout.WEST, pnlBamManip);
		sl_pnlBamManip.putConstraint(SpringLayout.WEST, txtMarkDuplicates, 10, SpringLayout.EAST, btnMarkDuplicates);
		pnlBamManip.add(btnMarkDuplicates);

		// >BAMReplicateMerge
		JTextArea txtBamReplicateMerge = new JTextArea();
		initializeTextArea(txtBamReplicateMerge);
		sl_pnlBamManip.putConstraint(SpringLayout.NORTH, txtBamReplicateMerge, 10, SpringLayout.SOUTH, txtMarkDuplicates);
		sl_pnlBamManip.putConstraint(SpringLayout.EAST, txtBamReplicateMerge, -10, SpringLayout.EAST, pnlBamManip);
		txtBamReplicateMerge.setText(ToolDescriptions.merge_bam_description);
		pnlBamManip.add(txtBamReplicateMerge);

		JButton btnBamReplicateMerge = new JButton("BAM Replicate Merge");
		btnBamReplicateMerge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							MergeBAMWindow frame = new MergeBAMWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlBamManip.putConstraint(SpringLayout.NORTH, btnBamReplicateMerge, 0, SpringLayout.NORTH,
				txtBamReplicateMerge);
		sl_pnlBamManip.putConstraint(SpringLayout.WEST, btnBamReplicateMerge, 10, SpringLayout.WEST, pnlBamManip);
		sl_pnlBamManip.putConstraint(SpringLayout.WEST, txtBamReplicateMerge, 10, SpringLayout.EAST,
				btnBamReplicateMerge);
		pnlBamManip.add(btnBamReplicateMerge);

		// >FilterPIPseq
		JTextArea txtFilterForPIPseq = new JTextArea();
		initializeTextArea(txtFilterForPIPseq);
		sl_pnlBamManip.putConstraint(SpringLayout.NORTH, txtFilterForPIPseq, 10, SpringLayout.SOUTH,
				txtBamReplicateMerge);
		sl_pnlBamManip.putConstraint(SpringLayout.EAST, txtFilterForPIPseq, -10, SpringLayout.EAST, pnlBamManip);
		txtFilterForPIPseq.setText(ToolDescriptions.filter_pip_seq_description);
		pnlBamManip.add(txtFilterForPIPseq);

		JButton btnFilterForPIPseq = new JButton("Filter for PIP-seq");
		btnFilterForPIPseq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							FilterforPIPseqWindow frame = new FilterforPIPseqWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlBamManip.putConstraint(SpringLayout.NORTH, btnFilterForPIPseq, 0, SpringLayout.NORTH, txtFilterForPIPseq);
		sl_pnlBamManip.putConstraint(SpringLayout.WEST, btnFilterForPIPseq, 10, SpringLayout.WEST, pnlBamManip);
		sl_pnlBamManip.putConstraint(SpringLayout.WEST, txtFilterForPIPseq, 10, SpringLayout.EAST, btnFilterForPIPseq);
		pnlBamManip.add(btnFilterForPIPseq);

		// >>>>>>>> BAM_Format_Converter <<<<<<<<
		JPanel pnlBamConvert = new JPanel();
		SpringLayout sl_pnlBamConvert = new SpringLayout();
		pnlBamConvert.setLayout(sl_pnlBamConvert);
		tabbedPane.addTab("BAM Format Converter", null, pnlBamConvert, null);

		// >BAMtoscIdx
		JTextArea txtBamToscIDX = new JTextArea();
		initializeTextArea(txtBamToscIDX);
		txtBamToscIDX.setText(ToolDescriptions.bam_to_scidx_description);
		sl_pnlBamConvert.putConstraint(SpringLayout.NORTH, txtBamToscIDX, 10, SpringLayout.NORTH, pnlBamConvert);
		sl_pnlBamConvert.putConstraint(SpringLayout.EAST, txtBamToscIDX, -10, SpringLayout.EAST, pnlBamConvert);
		pnlBamConvert.add(txtBamToscIDX);

		JButton btnBamToscIDX = new JButton("BAM to scIDX");
		btnBamToscIDX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							BAMtoscIDXWindow frame = new BAMtoscIDXWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlBamConvert.putConstraint(SpringLayout.NORTH, btnBamToscIDX, 0, SpringLayout.NORTH, txtBamToscIDX);
		sl_pnlBamConvert.putConstraint(SpringLayout.WEST, btnBamToscIDX, 10, SpringLayout.WEST, pnlBamConvert);
		sl_pnlBamConvert.putConstraint(SpringLayout.WEST, txtBamToscIDX, 10, SpringLayout.EAST, btnBamToscIDX);
		pnlBamConvert.add(btnBamToscIDX);

		// >BAMtoGFF
		JTextArea txtBamToGFF = new JTextArea();
		initializeTextArea(txtBamToGFF);
		txtBamToGFF.setText(ToolDescriptions.bam_to_gff_description);
		sl_pnlBamConvert.putConstraint(SpringLayout.NORTH, txtBamToGFF, 10, SpringLayout.SOUTH, txtBamToscIDX);
		sl_pnlBamConvert.putConstraint(SpringLayout.NORTH, txtBamToGFF, 10, SpringLayout.SOUTH, btnBamToscIDX);
		sl_pnlBamConvert.putConstraint(SpringLayout.EAST, txtBamToGFF, -10, SpringLayout.EAST, pnlBamConvert);
		pnlBamConvert.add(txtBamToGFF);

		JButton btnBamToGff = new JButton("BAM to GFF");
		btnBamToGff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							BAMtoGFFWindow frame = new BAMtoGFFWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlBamConvert.putConstraint(SpringLayout.NORTH, btnBamToGff, 0, SpringLayout.NORTH, txtBamToGFF);
		sl_pnlBamConvert.putConstraint(SpringLayout.WEST, btnBamToGff, 10, SpringLayout.WEST, pnlBamConvert);
		sl_pnlBamConvert.putConstraint(SpringLayout.WEST, txtBamToGFF, 10, SpringLayout.EAST, btnBamToGff);
		pnlBamConvert.add(btnBamToGff);

		// >BAMtoBED
		JTextArea txtBamToBed = new JTextArea();
		initializeTextArea(txtBamToBed);
		txtBamToBed.setText(ToolDescriptions.bam_to_bed_description);
		sl_pnlBamConvert.putConstraint(SpringLayout.NORTH, txtBamToBed, 10, SpringLayout.SOUTH, txtBamToGFF);
		sl_pnlBamConvert.putConstraint(SpringLayout.NORTH, txtBamToBed, 10, SpringLayout.SOUTH, btnBamToGff);
		sl_pnlBamConvert.putConstraint(SpringLayout.EAST, txtBamToBed, -10, SpringLayout.EAST, pnlBamConvert);
		pnlBamConvert.add(txtBamToBed);

		JButton btnBamToBed = new JButton("BAM to BED");
		btnBamToBed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							BAMtoBEDWindow frame = new BAMtoBEDWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlBamConvert.putConstraint(SpringLayout.NORTH, btnBamToBed, 0, SpringLayout.NORTH, txtBamToBed);
		sl_pnlBamConvert.putConstraint(SpringLayout.WEST, btnBamToBed, 10, SpringLayout.WEST, pnlBamConvert);
		sl_pnlBamConvert.putConstraint(SpringLayout.WEST, txtBamToBed, 10, SpringLayout.EAST, btnBamToBed);
		pnlBamConvert.add(btnBamToBed);

		// >BAMtobedGraph
		JTextArea txtBamToBedgraph = new JTextArea();
		initializeTextArea(txtBamToBedgraph);
		txtBamToBedgraph.setText(ToolDescriptions.bam_to_bedgraph_description);
		sl_pnlBamConvert.putConstraint(SpringLayout.NORTH, txtBamToBedgraph, 10, SpringLayout.SOUTH, txtBamToBed);
		sl_pnlBamConvert.putConstraint(SpringLayout.NORTH, txtBamToBedgraph, 10, SpringLayout.SOUTH, btnBamToBed);
		sl_pnlBamConvert.putConstraint(SpringLayout.EAST, txtBamToBedgraph, -10, SpringLayout.EAST, pnlBamConvert);
		pnlBamConvert.add(txtBamToBedgraph);

		JButton btnBamToBedgraph = new JButton("BAM to bedGraph");
		btnBamToBedgraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							BAMtobedGraphWindow frame = new BAMtobedGraphWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlBamConvert.putConstraint(SpringLayout.NORTH, btnBamToBedgraph, 0, SpringLayout.NORTH, txtBamToBedgraph);
		sl_pnlBamConvert.putConstraint(SpringLayout.WEST, btnBamToBedgraph, 10, SpringLayout.WEST, pnlBamConvert);
		sl_pnlBamConvert.putConstraint(SpringLayout.WEST, txtBamToBedgraph, 10, SpringLayout.EAST, btnBamToBedgraph);
		pnlBamConvert.add(btnBamToBedgraph);

		// >>>>>>>> File_Utilities <<<<<<<<
		JPanel pnlFileUtility = new JPanel();
		SpringLayout sl_pnlFileUtility = new SpringLayout();
		pnlFileUtility.setLayout(sl_pnlFileUtility);
		tabbedPane.addTab("File Utilities", null, pnlFileUtility, null);

		// >MD5checksum
		JTextArea txtMD5 = new JTextArea();
		initializeTextArea(txtMD5);
		txtMD5.setText(ToolDescriptions.md5checksum_description);
		sl_pnlFileUtility.putConstraint(SpringLayout.NORTH, txtMD5, 10, SpringLayout.NORTH, pnlFileUtility);
		sl_pnlFileUtility.putConstraint(SpringLayout.EAST, txtMD5, -10, SpringLayout.EAST, pnlFileUtility);
		pnlFileUtility.add(txtMD5);

		JButton btnMD5 = new JButton("MD5 Checksum");
		btnMD5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							MD5ChecksumWindow frame = new MD5ChecksumWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlFileUtility.putConstraint(SpringLayout.NORTH, btnMD5, 0, SpringLayout.NORTH, txtMD5);
		sl_pnlFileUtility.putConstraint(SpringLayout.WEST, btnMD5, 10, SpringLayout.WEST, pnlFileUtility);
		sl_pnlFileUtility.putConstraint(SpringLayout.WEST, txtMD5, 10, SpringLayout.EAST, btnMD5);
		pnlFileUtility.add(btnMD5);
		
		// >ConvertBEDChrNames
		JTextArea txtConvertBEDChrNames = new JTextArea();
		initializeTextArea(txtConvertBEDChrNames);
		txtConvertBEDChrNames.setText(ToolDescriptions.convertBEDChrNamesDescription);
		sl_pnlFileUtility.putConstraint(SpringLayout.NORTH, txtConvertBEDChrNames, 10, SpringLayout.SOUTH, btnMD5);
		sl_pnlFileUtility.putConstraint(SpringLayout.EAST, txtConvertBEDChrNames, -10, SpringLayout.EAST, pnlFileUtility);
		pnlFileUtility.add(txtConvertBEDChrNames);
		
		JButton btnConvertBEDChrNames = new JButton("Convert BED Chr Names");
		btnConvertBEDChrNames.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							ConvertBEDChrNamesWindow frame = new ConvertBEDChrNamesWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlFileUtility.putConstraint(SpringLayout.NORTH, btnConvertBEDChrNames, 0, SpringLayout.NORTH, txtConvertBEDChrNames);
		sl_pnlFileUtility.putConstraint(SpringLayout.WEST, btnConvertBEDChrNames, 10, SpringLayout.WEST, pnlFileUtility);
		sl_pnlFileUtility.putConstraint(SpringLayout.WEST, txtConvertBEDChrNames, 10, SpringLayout.EAST, btnConvertBEDChrNames);
		pnlFileUtility.add(btnConvertBEDChrNames);

		// >ConvertGFFChrNames
		JTextArea txtConvertGFFChrNames = new JTextArea();
		initializeTextArea(txtConvertGFFChrNames);
		txtConvertGFFChrNames.setText(ToolDescriptions.convertGFFChrNamesDescription);
		sl_pnlFileUtility.putConstraint(SpringLayout.NORTH, txtConvertGFFChrNames, 10, SpringLayout.SOUTH, txtConvertBEDChrNames);
		sl_pnlFileUtility.putConstraint(SpringLayout.EAST, txtConvertGFFChrNames, -10, SpringLayout.EAST, pnlFileUtility);
		pnlFileUtility.add(txtConvertGFFChrNames);
		
		JButton btnConvertGFFChrNames = new JButton("Convert GFF Chr Names");
		btnConvertGFFChrNames.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							ConvertGFFChrNamesWindow frame = new ConvertGFFChrNamesWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlFileUtility.putConstraint(SpringLayout.NORTH, btnConvertGFFChrNames, 0, SpringLayout.NORTH, txtConvertGFFChrNames);
		sl_pnlFileUtility.putConstraint(SpringLayout.WEST, btnConvertGFFChrNames, 10, SpringLayout.WEST, pnlFileUtility);
		sl_pnlFileUtility.putConstraint(SpringLayout.WEST, txtConvertGFFChrNames, 10, SpringLayout.EAST, btnConvertGFFChrNames);
		pnlFileUtility.add(btnConvertGFFChrNames);

		// >>>>>>>> Peak_Calling <<<<<<<<
		JPanel pnlPeakCalling = new JPanel();
		SpringLayout sl_pnlPeakCalling = new SpringLayout();
		pnlPeakCalling.setLayout(sl_pnlPeakCalling);
		tabbedPane.addTab("Peak Calling", null, pnlPeakCalling, null);

		// >GeneTrack
		JTextArea txtGenetrack = new JTextArea();
		initializeTextArea(txtGenetrack);
		txtGenetrack.setText(ToolDescriptions.gene_track_description);
		sl_pnlPeakCalling.putConstraint(SpringLayout.NORTH, txtGenetrack, 10, SpringLayout.NORTH, pnlPeakCalling);
		sl_pnlPeakCalling.putConstraint(SpringLayout.EAST, txtGenetrack, -10, SpringLayout.EAST, pnlPeakCalling);
		pnlPeakCalling.add(txtGenetrack);

		JButton btnGenetrack = new JButton("GeneTrack");
		btnGenetrack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							GeneTrackWindow frame = new GeneTrackWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlPeakCalling.putConstraint(SpringLayout.NORTH, btnGenetrack, 0, SpringLayout.NORTH, txtGenetrack);
		sl_pnlPeakCalling.putConstraint(SpringLayout.WEST, btnGenetrack, 10, SpringLayout.WEST, pnlPeakCalling);
		sl_pnlPeakCalling.putConstraint(SpringLayout.WEST, txtGenetrack, 10, SpringLayout.EAST, btnGenetrack);
		pnlPeakCalling.add(btnGenetrack);

		// >PeakPairing
		JTextArea txtPeakpairing = new JTextArea();
		initializeTextArea(txtPeakpairing);
		txtPeakpairing.setText(ToolDescriptions.peak_pairing_description);
		sl_pnlPeakCalling.putConstraint(SpringLayout.NORTH, txtPeakpairing, 10, SpringLayout.SOUTH, txtGenetrack);
		sl_pnlPeakCalling.putConstraint(SpringLayout.NORTH, txtPeakpairing, 10, SpringLayout.SOUTH, btnGenetrack);
		sl_pnlPeakCalling.putConstraint(SpringLayout.EAST, txtPeakpairing, -10, SpringLayout.EAST, pnlPeakCalling);
		pnlPeakCalling.add(txtPeakpairing);

		JButton btnPeakpairing = new JButton("Peak-Pairing");
		btnPeakpairing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							PeakPairWindow frame = new PeakPairWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		pnlPeakCalling.add(btnPeakpairing);
		sl_pnlPeakCalling.putConstraint(SpringLayout.NORTH, btnPeakpairing, 0, SpringLayout.NORTH, txtPeakpairing);
		sl_pnlPeakCalling.putConstraint(SpringLayout.WEST, btnPeakpairing, 10, SpringLayout.WEST, pnlPeakCalling);
		sl_pnlPeakCalling.putConstraint(SpringLayout.WEST, txtPeakpairing, 10, SpringLayout.EAST, btnPeakpairing);
		btnPeakpairing.setEnabled(false);

		// >ReplicateMatching
		JTextArea txtReplicateMatch = new JTextArea();
		initializeTextArea(txtReplicateMatch);
		txtReplicateMatch.setText(ToolDescriptions.replicate_match_description);
		sl_pnlPeakCalling.putConstraint(SpringLayout.NORTH, txtReplicateMatch, 10, SpringLayout.SOUTH, txtPeakpairing);
		sl_pnlPeakCalling.putConstraint(SpringLayout.NORTH, txtReplicateMatch, 10, SpringLayout.SOUTH, btnPeakpairing);
		sl_pnlPeakCalling.putConstraint(SpringLayout.EAST, txtReplicateMatch, -10, SpringLayout.EAST, pnlPeakCalling);
		pnlPeakCalling.add(txtReplicateMatch);

		JButton btnReplicateMatch = new JButton("Replicate Match");
		sl_pnlPeakCalling.putConstraint(SpringLayout.NORTH, btnReplicateMatch, 0, SpringLayout.NORTH,
				txtReplicateMatch);
		sl_pnlPeakCalling.putConstraint(SpringLayout.WEST, btnReplicateMatch, 10, SpringLayout.WEST, pnlPeakCalling);
		sl_pnlPeakCalling.putConstraint(SpringLayout.WEST, txtReplicateMatch, 10, SpringLayout.EAST, btnReplicateMatch);
		pnlPeakCalling.add(btnReplicateMatch);
		btnReplicateMatch.setEnabled(false);

		// >>>>>>>> Peak_Analysis <<<<<<<<
		JPanel pnlPeakAnalysis = new JPanel();
		SpringLayout sl_pnlPeakAnalysis = new SpringLayout();
		pnlPeakAnalysis.setLayout(sl_pnlPeakAnalysis);
		tabbedPane.addTab("Peak Analysis", null, pnlPeakAnalysis, null);

		// >PeakAlign
		JTextArea txtBedPeakAlignment = new JTextArea();
		initializeTextArea(txtBedPeakAlignment);
		txtBedPeakAlignment.setText(ToolDescriptions.peak_align_ref_description);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.NORTH, txtBedPeakAlignment, 10, SpringLayout.NORTH,
				pnlPeakAnalysis);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.EAST, txtBedPeakAlignment, -10, SpringLayout.EAST,
				pnlPeakAnalysis);
		pnlPeakAnalysis.add(txtBedPeakAlignment);

		JButton btnBedPeakAlignment = new JButton("Align BED to Reference");
		btnBedPeakAlignment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							BEDPeakAligntoRefWindow frame = new BEDPeakAligntoRefWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.NORTH, btnBedPeakAlignment, 0, SpringLayout.NORTH,
				txtBedPeakAlignment);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.WEST, btnBedPeakAlignment, 10, SpringLayout.WEST,
				pnlPeakAnalysis);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.WEST, txtBedPeakAlignment, 10, SpringLayout.EAST,
				btnBedPeakAlignment);
		pnlPeakAnalysis.add(btnBedPeakAlignment);

		// >FilterBED
		JTextArea txtBedFilter = new JTextArea();
		initializeTextArea(txtBedFilter);
		txtBedFilter.setText(ToolDescriptions.filter_bed_description);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.NORTH, txtBedFilter, 10, SpringLayout.SOUTH, txtBedPeakAlignment);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.EAST, txtBedFilter, -10, SpringLayout.EAST, pnlPeakAnalysis);
		pnlPeakAnalysis.add(txtBedFilter);

		JButton btnBedFilter = new JButton("Filter BED by Proximity");
		btnBedFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							FilterBEDbyProximityWindow frame = new FilterBEDbyProximityWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.NORTH, btnBedFilter, 0, SpringLayout.NORTH, txtBedFilter);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.WEST, btnBedFilter, 10, SpringLayout.WEST, pnlPeakAnalysis);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.WEST, txtBedFilter, 10, SpringLayout.EAST, btnBedFilter);
		pnlPeakAnalysis.add(btnBedFilter);

		// >TileGenome
		JTextArea txtGenomicCoordinateTile = new JTextArea();
		initializeTextArea(txtGenomicCoordinateTile);
		txtGenomicCoordinateTile.setText(ToolDescriptions.tile_genome_description);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.NORTH, txtGenomicCoordinateTile, 10, SpringLayout.SOUTH,
				txtBedFilter);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.EAST, txtGenomicCoordinateTile, -10, SpringLayout.EAST,
				pnlPeakAnalysis);
		pnlPeakAnalysis.add(txtGenomicCoordinateTile);

		JButton btnGenomicCoordinateTile = new JButton("Genomic Coordinate Tile");
		btnGenomicCoordinateTile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							TileGenomeWindow frame = new TileGenomeWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.NORTH, btnGenomicCoordinateTile, 0, SpringLayout.NORTH,
				txtGenomicCoordinateTile);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.WEST, btnGenomicCoordinateTile, 10, SpringLayout.WEST,
				pnlPeakAnalysis);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.WEST, txtGenomicCoordinateTile, 10, SpringLayout.EAST,
				btnGenomicCoordinateTile);
		pnlPeakAnalysis.add(btnGenomicCoordinateTile);

		// >RandCoord
		JTextArea txtRandomCoordinateGeneration = new JTextArea();
		initializeTextArea(txtRandomCoordinateGeneration);
		txtRandomCoordinateGeneration.setText(ToolDescriptions.rand_coord_description);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.NORTH, txtRandomCoordinateGeneration, 10, SpringLayout.SOUTH,
				txtGenomicCoordinateTile);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.EAST, txtRandomCoordinateGeneration, -10, SpringLayout.EAST,
				pnlPeakAnalysis);
		pnlPeakAnalysis.add(txtRandomCoordinateGeneration);

		JButton btnRandomCoordinateGeneration = new JButton("Generate Random Coordinate");
		btnRandomCoordinateGeneration.setToolTipText("Generate random BED coordinates based on reference genome.");
		btnRandomCoordinateGeneration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							RandomCoordinateWindow frame = new RandomCoordinateWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.NORTH, btnRandomCoordinateGeneration, 0, SpringLayout.NORTH,
				txtRandomCoordinateGeneration);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.WEST, btnRandomCoordinateGeneration, 10, SpringLayout.WEST,
				pnlPeakAnalysis);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.WEST, txtRandomCoordinateGeneration, 10, SpringLayout.EAST,
				btnRandomCoordinateGeneration);
		pnlPeakAnalysis.add(btnRandomCoordinateGeneration);

		// >Signal_Duplication
		JTextArea txtOutputSignalDuplication = new JTextArea();
		initializeTextArea(txtOutputSignalDuplication);
		txtOutputSignalDuplication.setText(ToolDescriptions.signal_dup_description);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.NORTH, txtOutputSignalDuplication, 10, SpringLayout.SOUTH,
				txtRandomCoordinateGeneration);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.EAST, txtOutputSignalDuplication, -10, SpringLayout.EAST,
				pnlPeakAnalysis);
		pnlPeakAnalysis.add(txtOutputSignalDuplication);

		JButton btnSignalDuplication = new JButton("Signal Duplication");
		btnSignalDuplication.setToolTipText("Output signal duplication statistics");
		btnSignalDuplication.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							SignalDuplicationWindow frame = new SignalDuplicationWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.NORTH, btnSignalDuplication, 0, SpringLayout.NORTH,
				txtOutputSignalDuplication);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.WEST, btnSignalDuplication, 10, SpringLayout.WEST,
				pnlPeakAnalysis);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.WEST, txtOutputSignalDuplication, 10, SpringLayout.EAST,
				btnSignalDuplication);
		pnlPeakAnalysis.add(btnSignalDuplication);

		// >>>>>>>> Coordinate_Manipulation <<<<<<<<
		JPanel pnlCoordManip = new JPanel();
		tabbedPane.addTab("Coordinate File Manipulation", null, pnlCoordManip, null);

		JSplitPane splitPaneExpand = new JSplitPane();
		pnlCoordManip.add(splitPaneExpand);

		// >ExpandBED
		JButton btnExpandBedFile = new JButton("Expand BED File");
		btnExpandBedFile.setToolTipText(ToolDescriptions.expand_bed_description);
		btnExpandBedFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							ExpandBEDWindow frame = new ExpandBEDWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		splitPaneExpand.setLeftComponent(btnExpandBedFile);

		// >ExpandGFF
		JButton btnExpandGffFile = new JButton("Expand GFF File");
		btnExpandGffFile.setToolTipText(ToolDescriptions.expand_gff_description);
		btnExpandGffFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							ExpandGFFWindow frame = new ExpandGFFWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		splitPaneExpand.setRightComponent(btnExpandGffFile);

		JSplitPane splitPaneConvert = new JSplitPane();
		pnlCoordManip.add(splitPaneConvert);

		// >BEDtoGFF
		JButton btnBedToGFF = new JButton("Convert BED to GFF");
		btnBedToGFF.setToolTipText(ToolDescriptions.bed_to_gff_description);
		btnBedToGFF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							BEDtoGFFWindow frame = new BEDtoGFFWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		splitPaneConvert.setLeftComponent(btnBedToGFF);

		// >GFFtoBED
		JButton btnGffToBed = new JButton("Convert GFF to BED");
		btnGffToBed.setToolTipText(ToolDescriptions.gff_to_bed_description);
		btnGffToBed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							GFFtoBEDWindow frame = new GFFtoBEDWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		splitPaneConvert.setRightComponent(btnGffToBed);

		JSplitPane splitPaneSort = new JSplitPane();
		pnlCoordManip.add(splitPaneSort);

		// >SortBED
		JButton btnBEDSort = new JButton("Sort BED by CDT");
		btnBEDSort.setToolTipText(ToolDescriptions.sort_bed_description);
		btnBEDSort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							SortBEDWindow frame = new SortBEDWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		splitPaneSort.setLeftComponent(btnBEDSort);

		// >SortGFF
		JButton btnSortGffFile = new JButton("Sort GFF by CDT");
		btnSortGffFile.setToolTipText(ToolDescriptions.sort_gff_description);
		btnSortGffFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							SortGFFWindow frame = new SortGFFWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		splitPaneSort.setRightComponent(btnSortGffFile);

		// >>>>>>>> Read_Analysis <<<<<<<<
		JPanel pnlReadAnalysis = new JPanel();
		SpringLayout sl_pnlReadAnalysis = new SpringLayout();
		pnlReadAnalysis.setLayout(sl_pnlReadAnalysis);
		tabbedPane.addTab("Sequence Read Analysis", null, pnlReadAnalysis, null);

		// >TagPileup
		JTextArea txtTagPileup = new JTextArea();
		initializeTextArea(txtTagPileup);
		txtTagPileup.setText(ToolDescriptions.tag_pileup_description);
		sl_pnlReadAnalysis.putConstraint(SpringLayout.NORTH, txtTagPileup, 10, SpringLayout.NORTH, pnlReadAnalysis);
		sl_pnlReadAnalysis.putConstraint(SpringLayout.EAST, txtTagPileup, -10, SpringLayout.EAST, pnlReadAnalysis);
		pnlReadAnalysis.add(txtTagPileup);

		JButton btnTagPileup = new JButton("Tag Pileup");
		btnTagPileup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							TagPileupWindow frame = new TagPileupWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlReadAnalysis.putConstraint(SpringLayout.NORTH, btnTagPileup, 0, SpringLayout.NORTH, txtTagPileup);
		sl_pnlReadAnalysis.putConstraint(SpringLayout.WEST, btnTagPileup, 10, SpringLayout.WEST, pnlReadAnalysis);
		sl_pnlReadAnalysis.putConstraint(SpringLayout.WEST, txtTagPileup, 10, SpringLayout.EAST, btnTagPileup);
		pnlReadAnalysis.add(btnTagPileup);

		// >ScalingFactor
		JTextArea txtCalculateScalingFactor = new JTextArea();
		initializeTextArea(txtCalculateScalingFactor);
		txtCalculateScalingFactor.setText(ToolDescriptions.scaling_factor_description);
		sl_pnlReadAnalysis.putConstraint(SpringLayout.NORTH, txtCalculateScalingFactor, 10, SpringLayout.SOUTH,
				txtTagPileup);
// 		sl_pnlReadAnalysis.putConstraint(SpringLayout.NORTH, txtCalculateScalingFactor, 10, SpringLayout.SOUTH, btnTagPileup);
		sl_pnlReadAnalysis.putConstraint(SpringLayout.EAST, txtCalculateScalingFactor, -10, SpringLayout.EAST,
				pnlReadAnalysis);
		pnlReadAnalysis.add(txtCalculateScalingFactor);

		JButton btnScale = new JButton("Calculate Scaling Factor");
		btnScale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							ScalingFactorWindow frame = new ScalingFactorWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlReadAnalysis.putConstraint(SpringLayout.NORTH, btnScale, 0, SpringLayout.NORTH,
				txtCalculateScalingFactor);
		sl_pnlReadAnalysis.putConstraint(SpringLayout.WEST, btnScale, 10, SpringLayout.WEST, pnlReadAnalysis);
		sl_pnlReadAnalysis.putConstraint(SpringLayout.WEST, txtCalculateScalingFactor, 10, SpringLayout.EAST, btnScale);
		pnlReadAnalysis.add(btnScale);

		// >ScaleMatrix
		JTextArea txtApplyScalingFactor = new JTextArea();
		initializeTextArea(txtApplyScalingFactor);
		txtApplyScalingFactor.setText(ToolDescriptions.scale_matrix_description);
		sl_pnlReadAnalysis.putConstraint(SpringLayout.NORTH, txtApplyScalingFactor, 10, SpringLayout.SOUTH,
				txtCalculateScalingFactor);
// 		sl_pnlReadAnalysis.putConstraint(SpringLayout.NORTH, txtApplyScalingFactor, 10, SpringLayout.SOUTH, btnScale);
		sl_pnlReadAnalysis.putConstraint(SpringLayout.EAST, txtApplyScalingFactor, -10, SpringLayout.EAST,
				pnlReadAnalysis);
		pnlReadAnalysis.add(txtApplyScalingFactor);

		JButton btnScaleMatrixData = new JButton("Scale Matrix Data");
		btnScaleMatrixData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							ScaleMatrixWindow frame = new ScaleMatrixWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlReadAnalysis.putConstraint(SpringLayout.NORTH, btnScaleMatrixData, 0, SpringLayout.NORTH,
				txtApplyScalingFactor);
		sl_pnlReadAnalysis.putConstraint(SpringLayout.WEST, btnScaleMatrixData, 10, SpringLayout.WEST, pnlReadAnalysis);
		sl_pnlReadAnalysis.putConstraint(SpringLayout.WEST, txtApplyScalingFactor, 10, SpringLayout.EAST,
				btnScaleMatrixData);
		pnlReadAnalysis.add(btnScaleMatrixData);

		// >AggregateData
		JTextArea txtAggregateData = new JTextArea();
		initializeTextArea(txtAggregateData);
		txtAggregateData.setText(ToolDescriptions.aggregate_data_description);
		sl_pnlReadAnalysis.putConstraint(SpringLayout.NORTH, txtAggregateData, 10, SpringLayout.SOUTH,
				txtApplyScalingFactor);
		sl_pnlReadAnalysis.putConstraint(SpringLayout.NORTH, txtAggregateData, 10, SpringLayout.SOUTH,
				btnScaleMatrixData);
		sl_pnlReadAnalysis.putConstraint(SpringLayout.EAST, txtAggregateData, -10, SpringLayout.EAST, pnlReadAnalysis);
		pnlReadAnalysis.add(txtAggregateData);

		JButton btnAggregateData = new JButton("Aggregate Data");
		btnAggregateData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							AggregateDataWindow frame = new AggregateDataWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlReadAnalysis.putConstraint(SpringLayout.NORTH, btnAggregateData, 0, SpringLayout.NORTH, txtAggregateData);
		sl_pnlReadAnalysis.putConstraint(SpringLayout.WEST, btnAggregateData, 10, SpringLayout.WEST, pnlReadAnalysis);
		sl_pnlReadAnalysis.putConstraint(SpringLayout.WEST, txtAggregateData, 10, SpringLayout.EAST, btnAggregateData);
		pnlReadAnalysis.add(btnAggregateData);

		// >>>>>>>> Sequence_Analysis <<<<<<<<
		JPanel pnlSeqAnalysis = new JPanel();
		SpringLayout sl_pnlSeqAnalysis = new SpringLayout();
		pnlSeqAnalysis.setLayout(sl_pnlSeqAnalysis);
		tabbedPane.addTab("DNA Sequence Analysis", null, pnlSeqAnalysis, null);

		// >FASTAExtract
		JTextArea txtFASTAExtract = new JTextArea();
		initializeTextArea(txtFASTAExtract);
		txtFASTAExtract.setText(ToolDescriptions.fasta_extract_description);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.NORTH, txtFASTAExtract, 10, SpringLayout.NORTH, pnlSeqAnalysis);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.EAST, txtFASTAExtract, -10, SpringLayout.EAST, pnlSeqAnalysis);
		pnlSeqAnalysis.add(txtFASTAExtract);

		JButton btnFASTAExtract = new JButton("FASTA from BED");
		btnFASTAExtract.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							FASTAExtractWindow frame = new FASTAExtractWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.NORTH, btnFASTAExtract, 0, SpringLayout.NORTH, txtFASTAExtract);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.WEST, btnFASTAExtract, 10, SpringLayout.WEST, pnlSeqAnalysis);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.WEST, txtFASTAExtract, 10, SpringLayout.EAST, btnFASTAExtract);
		pnlSeqAnalysis.add(btnFASTAExtract);

		// >RandomizeFASTA
		JTextArea txtRandomizeFasta = new JTextArea();
		initializeTextArea(txtRandomizeFasta);
		txtRandomizeFasta.setText(ToolDescriptions.randomize_fasta_description);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.NORTH, txtRandomizeFasta, 10, SpringLayout.SOUTH, txtFASTAExtract);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.EAST, txtRandomizeFasta, -10, SpringLayout.EAST, pnlSeqAnalysis);
		pnlSeqAnalysis.add(txtRandomizeFasta);

		JButton btnRandomizeFasta = new JButton("Randomize FASTA");
		btnRandomizeFasta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							RandomizeFASTAWindow frame = new RandomizeFASTAWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.NORTH, btnRandomizeFasta, 0, SpringLayout.NORTH,
				txtRandomizeFasta);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.WEST, btnRandomizeFasta, 10, SpringLayout.WEST, pnlSeqAnalysis);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.WEST, txtRandomizeFasta, 10, SpringLayout.EAST, btnRandomizeFasta);
		pnlSeqAnalysis.add(btnRandomizeFasta);

		// >SearchMotif
		JTextArea txtSearchMotif = new JTextArea();
		initializeTextArea(txtSearchMotif);
		txtSearchMotif.setText(ToolDescriptions.search_motif_description);
// 		sl_pnlSeqAnalysis.putConstraint(SpringLayout.NORTH, txtSearchMotif, 10, SpringLayout.SOUTH, txtRandomizeFasta);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.NORTH, txtSearchMotif, 10, SpringLayout.SOUTH, btnRandomizeFasta);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.EAST, txtSearchMotif, -10, SpringLayout.EAST, pnlSeqAnalysis);
		pnlSeqAnalysis.add(txtSearchMotif);

		JButton btnSearchMotif = new JButton("Search Motif in FASTA");
		btnSearchMotif.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							SearchMotifWindow frame = new SearchMotifWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.NORTH, btnSearchMotif, 0, SpringLayout.NORTH, txtSearchMotif);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.WEST, btnSearchMotif, 10, SpringLayout.WEST, pnlSeqAnalysis);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.WEST, txtSearchMotif, 10, SpringLayout.EAST, btnSearchMotif);
		pnlSeqAnalysis.add(btnSearchMotif);

		// >DNAShapeFromBED
		JTextArea txtDnaShapeBed = new JTextArea();
		initializeTextArea(txtDnaShapeBed);
		txtDnaShapeBed.setText(ToolDescriptions.dna_shape_from_bed_description);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.NORTH, txtDnaShapeBed, 10, SpringLayout.SOUTH, txtSearchMotif);
// 		sl_pnlSeqAnalysis.putConstraint(SpringLayout.NORTH, txtDnaShapeBed, 10, SpringLayout.SOUTH, btnSearchMotif);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.EAST, txtDnaShapeBed, -10, SpringLayout.EAST, pnlSeqAnalysis);
		pnlSeqAnalysis.add(txtDnaShapeBed);

		JButton btnDnaShapeBed = new JButton("DNA Shape from BED");
		btnDnaShapeBed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							DNAShapefromBEDWindow frame = new DNAShapefromBEDWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.NORTH, btnDnaShapeBed, 0, SpringLayout.NORTH, txtDnaShapeBed);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.WEST, btnDnaShapeBed, 10, SpringLayout.WEST, pnlSeqAnalysis);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.WEST, txtDnaShapeBed, 10, SpringLayout.EAST, btnDnaShapeBed);
		pnlSeqAnalysis.add(btnDnaShapeBed);

		// >DNAShapeFromFASTA
		JTextArea txtDnaShapeFasta = new JTextArea();
		initializeTextArea(txtDnaShapeFasta);
		txtDnaShapeFasta.setText(ToolDescriptions.dna_shape_from_fasta_description);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.NORTH, txtDnaShapeFasta, 10, SpringLayout.SOUTH, txtDnaShapeBed);
// 		sl_pnlSeqAnalysis.putConstraint(SpringLayout.NORTH, txtDnaShapeFasta, 10, SpringLayout.SOUTH, btnDnaShapeBed);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.EAST, txtDnaShapeFasta, -10, SpringLayout.EAST, pnlSeqAnalysis);
		pnlSeqAnalysis.add(txtDnaShapeFasta);

		JButton btnDnaShapeFasta = new JButton("DNA Shape from FASTA");
		btnDnaShapeFasta.setToolTipText("Calculate intrinsic DNA shape given input FASTA file");
		btnDnaShapeFasta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							DNAShapefromFASTAWindow frame = new DNAShapefromFASTAWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.NORTH, btnDnaShapeFasta, 0, SpringLayout.NORTH, txtDnaShapeFasta);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.WEST, btnDnaShapeFasta, 10, SpringLayout.WEST, pnlSeqAnalysis);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.WEST, txtDnaShapeFasta, 10, SpringLayout.EAST, btnDnaShapeFasta);
		pnlSeqAnalysis.add(btnDnaShapeFasta);

		// >>>>>>>> Figure_Generation <<<<<<<<
		JPanel pnlFigure = new JPanel();
		SpringLayout sl_pnlFigure = new SpringLayout();
		pnlFigure.setLayout(sl_pnlFigure);
		tabbedPane.addTab("Figure Generation", null, pnlFigure, null);

		// >TwoColorHeatMap
		JTextArea txtTwoColorHeatMap = new JTextArea();
		initializeTextArea(txtTwoColorHeatMap);
		txtTwoColorHeatMap.setText(ToolDescriptions.heatmap_description);
		sl_pnlFigure.putConstraint(SpringLayout.NORTH, txtTwoColorHeatMap, 10, SpringLayout.NORTH, pnlFigure);
		sl_pnlFigure.putConstraint(SpringLayout.EAST, txtTwoColorHeatMap, -10, SpringLayout.EAST, pnlFigure);
		pnlFigure.add(txtTwoColorHeatMap);

		JButton btnTwoColorHeatMap = new JButton("Two-Color Heat Map");
		btnTwoColorHeatMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							TwoColorHeatMapWindow frame = new TwoColorHeatMapWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlFigure.putConstraint(SpringLayout.NORTH, btnTwoColorHeatMap, 0, SpringLayout.NORTH, txtTwoColorHeatMap);
		sl_pnlFigure.putConstraint(SpringLayout.WEST, btnTwoColorHeatMap, 10, SpringLayout.WEST, pnlFigure);
		sl_pnlFigure.putConstraint(SpringLayout.WEST, txtTwoColorHeatMap, 10, SpringLayout.EAST, btnTwoColorHeatMap);
		pnlFigure.add(btnTwoColorHeatMap);

// 		>ThreeColorHeatMap
		JTextArea txtThreeColorHeatMap = new JTextArea();
		initializeTextArea(txtThreeColorHeatMap);
		txtThreeColorHeatMap.setText(ToolDescriptions.threecolorheatmap_description);
		sl_pnlFigure
				.putConstraint(SpringLayout.NORTH, txtThreeColorHeatMap, 10, SpringLayout.SOUTH,
				txtTwoColorHeatMap);
		sl_pnlFigure.putConstraint(SpringLayout.NORTH, txtThreeColorHeatMap, 10, SpringLayout.SOUTH,
				btnTwoColorHeatMap);
		sl_pnlFigure.putConstraint(SpringLayout.EAST, txtThreeColorHeatMap, -10, SpringLayout.EAST, pnlFigure);
		pnlFigure.add(txtThreeColorHeatMap);

		JButton btnThreeColorHeatMap = new JButton("Three Color Heat Map");
		btnThreeColorHeatMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							ThreeColorHeatMapWindow frame = new ThreeColorHeatMapWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlFigure.putConstraint(SpringLayout.NORTH, btnThreeColorHeatMap, 0, SpringLayout.NORTH,
				txtThreeColorHeatMap);
		sl_pnlFigure.putConstraint(SpringLayout.WEST, btnThreeColorHeatMap, 10, SpringLayout.WEST, pnlFigure);
		sl_pnlFigure.putConstraint(SpringLayout.WEST, txtThreeColorHeatMap, 10, SpringLayout.EAST,
				btnThreeColorHeatMap);
		pnlFigure.add(btnThreeColorHeatMap);

		// >MergeHeatMap
		JTextArea txtMergeHeatmap = new JTextArea();
		initializeTextArea(txtMergeHeatmap);
		txtMergeHeatmap.setText(ToolDescriptions.merge_heatmap_description);
		sl_pnlFigure.putConstraint(SpringLayout.NORTH, txtMergeHeatmap, 10, SpringLayout.SOUTH, txtThreeColorHeatMap);
		sl_pnlFigure.putConstraint(SpringLayout.NORTH, txtMergeHeatmap, 10, SpringLayout.SOUTH, btnThreeColorHeatMap);
		sl_pnlFigure.putConstraint(SpringLayout.EAST, txtMergeHeatmap, -10, SpringLayout.EAST, pnlFigure);
		pnlFigure.add(txtMergeHeatmap);

		JButton btnMergeHeatmap = new JButton("Merge Heatmaps");
		btnMergeHeatmap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							MergeHeatMapWindow frame = new MergeHeatMapWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlFigure.putConstraint(SpringLayout.NORTH, btnMergeHeatmap, 0, SpringLayout.NORTH, txtMergeHeatmap);
		sl_pnlFigure.putConstraint(SpringLayout.WEST, btnMergeHeatmap, 10, SpringLayout.WEST, pnlFigure);
		sl_pnlFigure.putConstraint(SpringLayout.WEST, txtMergeHeatmap, 10, SpringLayout.EAST, btnMergeHeatmap);
		pnlFigure.add(btnMergeHeatmap);

		// >LabelHeatMap
		JTextArea txtLabelHeatMap = new JTextArea();
		initializeTextArea(txtLabelHeatMap);
		txtLabelHeatMap.setText(ToolDescriptions.label_heatmap_description);
		sl_pnlFigure.putConstraint(SpringLayout.NORTH, txtLabelHeatMap, 10, SpringLayout.SOUTH, txtMergeHeatmap);
		sl_pnlFigure.putConstraint(SpringLayout.NORTH, txtLabelHeatMap, 10, SpringLayout.SOUTH, btnMergeHeatmap);
		sl_pnlFigure.putConstraint(SpringLayout.EAST, txtLabelHeatMap, -10, SpringLayout.EAST, pnlFigure);
		pnlFigure.add(txtLabelHeatMap);

		JButton btnLabelHeatMap = new JButton("Label HeatMap");
		btnLabelHeatMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							LabelHeatMapWindow frame = new LabelHeatMapWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlFigure.putConstraint(SpringLayout.NORTH, btnLabelHeatMap, 0, SpringLayout.NORTH,
				txtLabelHeatMap);
		sl_pnlFigure.putConstraint(SpringLayout.WEST, btnLabelHeatMap, 10, SpringLayout.WEST, pnlFigure);
		sl_pnlFigure.putConstraint(SpringLayout.WEST, txtLabelHeatMap, 10, SpringLayout.EAST,
				btnLabelHeatMap);
		pnlFigure.add(btnLabelHeatMap);

		// >FourColorPlot
		JTextArea txtcolorSequencePlot = new JTextArea();
		initializeTextArea(txtcolorSequencePlot);
		txtcolorSequencePlot.setText(ToolDescriptions.four_color_description);
		sl_pnlFigure.putConstraint(SpringLayout.NORTH, txtcolorSequencePlot, 10, SpringLayout.SOUTH, txtLabelHeatMap);
		sl_pnlFigure.putConstraint(SpringLayout.NORTH, txtcolorSequencePlot, 10, SpringLayout.SOUTH, btnLabelHeatMap);
		sl_pnlFigure.putConstraint(SpringLayout.EAST, txtcolorSequencePlot, -10, SpringLayout.EAST, pnlFigure);
		pnlFigure.add(txtcolorSequencePlot);

		JButton btncolorSequencePlot = new JButton("4Color Sequence Plot");
		btncolorSequencePlot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							FourColorSequenceWindow frame = new FourColorSequenceWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		sl_pnlFigure.putConstraint(SpringLayout.NORTH, btncolorSequencePlot, 0, SpringLayout.NORTH,
				txtcolorSequencePlot);
		sl_pnlFigure.putConstraint(SpringLayout.WEST, btncolorSequencePlot, 10, SpringLayout.WEST, pnlFigure);
		sl_pnlFigure.putConstraint(SpringLayout.WEST, txtcolorSequencePlot, 10, SpringLayout.EAST,
				btncolorSequencePlot);
		pnlFigure.add(btncolorSequencePlot);

		// Set default tab to open to...
		// 0=BAM_Statistics 5=Peak_Analysis
		// 1=BAM_Manipulation 6=Coordinate_Manipulation
		// 2=BAM_Format_Converter 7=Sequence_Analysis
		// 3=File_Utilities 8=DNA_Sequence_Analysis
		// 4=Peak_Calling 9=Figure_Generation
		tabbedPane.setSelectedIndex(0);
	}

	private void initializeTextArea(JTextArea text) {
		text.setWrapStyleWord(true);
		text.setEditable(false);
		text.setLineWrap(true);
	}

	/**
	 * Create the application.
	 */
	public ScriptManagerGUI() {
		initialize();
	}

	/**
	 * Launch the application.
	 */
	public void launchApplication() {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ScriptManagerGUI window = new ScriptManagerGUI();
					window.frmScriptManager.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
