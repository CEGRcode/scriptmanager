package main;

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


import window_interface.BAM_Statistics.PEStatWindow;
import window_interface.BAM_Statistics.SEStatWindow;
import window_interface.BAM_Statistics.BAMGenomeCorrelationWindow;
import window_interface.BAM_Manipulation.BAIIndexerWindow;
import window_interface.BAM_Manipulation.BAMRemoveDupWindow;
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
import window_interface.Read_Analysis.AggregateDataWindow;
import window_interface.Read_Analysis.TagPileupWindow;
import window_interface.Sequence_Analysis.DNAShapefromBEDWindow;
import window_interface.Sequence_Analysis.DNAShapefromFASTAWindow;
import window_interface.Sequence_Analysis.FASTAExtractWindow;
import window_interface.Sequence_Analysis.RandomizeFASTAWindow;
import window_interface.Sequence_Analysis.SearchMotifWindow;
import window_interface.Figure_Generation.FourColorSequenceWindow;
import window_interface.Figure_Generation.HeatMapWindow;
import window_interface.Figure_Generation.MergeHeatMapWindow;

public class ScriptManager {
	public static final String VERSION = "0.11-dev";

	private JFrame frmScriptManager;	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmScriptManager = new JFrame();
		frmScriptManager.setTitle("Script Manager v" + VERSION);
		frmScriptManager.setBounds(100, 100, 600, 350);
		frmScriptManager.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmScriptManager.setResizable(false);
		SpringLayout springLayout = new SpringLayout();
		frmScriptManager.getContentPane().setLayout(springLayout);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		springLayout.putConstraint(SpringLayout.NORTH, tabbedPane, 10, SpringLayout.NORTH, frmScriptManager.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, tabbedPane, 10, SpringLayout.WEST, frmScriptManager.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, tabbedPane, -10, SpringLayout.SOUTH, frmScriptManager.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, tabbedPane, -10, SpringLayout.EAST, frmScriptManager.getContentPane());
		frmScriptManager.getContentPane().add(tabbedPane);
		
		JPanel pnlStat = new JPanel();
		SpringLayout sl_pnlStat = new SpringLayout();
		pnlStat.setLayout(sl_pnlStat);
		tabbedPane.addTab("BAM Statistics", null, pnlStat, null);
		
		JTextArea txtOutputAlignmentStatistics = new JTextArea();
		initializeTextArea(txtOutputAlignmentStatistics);
		txtOutputAlignmentStatistics.setText("Output BAM Header including alignment statistics and parameters given any indexed (BAI) BAM File.");
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

		JTextArea txtPEStats = new JTextArea();
		initializeTextArea(txtPEStats);
		txtPEStats.setText("Generates Insert-size Histogram statistics (GEO requirement) and outputs BAM Header including alignment statistics and parameters given a sorted and indexed (BAI) paired-end BAM File.");
		sl_pnlStat.putConstraint(SpringLayout.NORTH, txtPEStats, 10, SpringLayout.SOUTH, txtOutputAlignmentStatistics);
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

		JTextArea txtBamGenomeCorrelation = new JTextArea();
		initializeTextArea(txtBamGenomeCorrelation);
		txtBamGenomeCorrelation.setText("Genome-Genome correlations for replicate comparisons given multiple sorted and indexed (BAI) BAM files.");
		sl_pnlStat.putConstraint(SpringLayout.NORTH, txtBamGenomeCorrelation, 10, SpringLayout.SOUTH, txtPEStats);
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
		sl_pnlStat.putConstraint(SpringLayout.NORTH, btnBamGenomeCorrelation, 0, SpringLayout.NORTH, txtBamGenomeCorrelation);
		sl_pnlStat.putConstraint(SpringLayout.WEST, btnBamGenomeCorrelation, 10, SpringLayout.WEST, pnlStat);
		sl_pnlStat.putConstraint(SpringLayout.WEST, txtBamGenomeCorrelation, 10, SpringLayout.EAST, btnBamGenomeCorrelation);
		pnlStat.add(btnBamGenomeCorrelation);
		
		JPanel pnlBamManip = new JPanel();
		SpringLayout sl_pnlBamManip = new SpringLayout();
		pnlBamManip.setLayout(sl_pnlBamManip);
		tabbedPane.addTab("BAM Manipulation", null, pnlBamManip, null);
		
		JTextArea txtBAIIndex = new JTextArea();
		initializeTextArea(txtBAIIndex);
		sl_pnlBamManip.putConstraint(SpringLayout.NORTH, txtBAIIndex, 10, SpringLayout.NORTH, pnlBamManip);
		sl_pnlBamManip.putConstraint(SpringLayout.EAST, txtBAIIndex, -10, SpringLayout.EAST, pnlBamManip);
		txtBAIIndex.setText("Generates BAI Index for input BAM files. Output BAI is in the same directory as input BAM file.");
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

		JTextArea txtBamSort = new JTextArea();
		initializeTextArea(txtBamSort);
		sl_pnlBamManip.putConstraint(SpringLayout.NORTH, txtBamSort, 10, SpringLayout.SOUTH, txtBAIIndex);
		sl_pnlBamManip.putConstraint(SpringLayout.EAST, txtBamSort, -10, SpringLayout.EAST, pnlBamManip);
		txtBamSort.setText("Sort BAM files in order to efficiently extract and manipulate.\nRAM intensive process. If program freezes, increase JAVA heap size");
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

		JTextArea txtBamRemoveDuplicates = new JTextArea();
		initializeTextArea(txtBamRemoveDuplicates);
		sl_pnlBamManip.putConstraint(SpringLayout.NORTH, txtBamRemoveDuplicates, 10, SpringLayout.SOUTH, txtBamSort);
		sl_pnlBamManip.putConstraint(SpringLayout.EAST, txtBamRemoveDuplicates, -10, SpringLayout.EAST, pnlBamManip);
		txtBamRemoveDuplicates.setText("Removes duplicate reads in Paired-End sequencing given identical 5' read locations. RAM intensive process. If program freezes, increase JAVA heap size");
		pnlBamManip.add(txtBamRemoveDuplicates);

		JButton btnBamRemoveDuplicates = new JButton("BAM Remove Duplicates");
		btnBamRemoveDuplicates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							BAMRemoveDupWindow frame = new BAMRemoveDupWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				 });
			}
		});
		sl_pnlBamManip.putConstraint(SpringLayout.NORTH, btnBamRemoveDuplicates, 0, SpringLayout.NORTH, txtBamRemoveDuplicates);
		sl_pnlBamManip.putConstraint(SpringLayout.WEST, btnBamRemoveDuplicates, 10, SpringLayout.WEST, pnlBamManip);
		sl_pnlBamManip.putConstraint(SpringLayout.WEST, txtBamRemoveDuplicates, 10, SpringLayout.EAST, btnBamRemoveDuplicates);
		pnlBamManip.add(btnBamRemoveDuplicates);

		JTextArea txtBamReplicateMerge = new JTextArea();
		initializeTextArea(txtBamReplicateMerge);
		sl_pnlBamManip.putConstraint(SpringLayout.NORTH, txtBamReplicateMerge, 10, SpringLayout.SOUTH, txtBamRemoveDuplicates);
		sl_pnlBamManip.putConstraint(SpringLayout.EAST, txtBamReplicateMerge, -10, SpringLayout.EAST, pnlBamManip);
		txtBamReplicateMerge.setText("Merges Multiple BAM files into single BAM file. Sorting is performed automatically. RAM intensive process. If program freezes, increase JAVA heap size");
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
		sl_pnlBamManip.putConstraint(SpringLayout.NORTH, btnBamReplicateMerge, 0, SpringLayout.NORTH, txtBamReplicateMerge);
		sl_pnlBamManip.putConstraint(SpringLayout.WEST, btnBamReplicateMerge, 10, SpringLayout.WEST, pnlBamManip);
		sl_pnlBamManip.putConstraint(SpringLayout.WEST, txtBamReplicateMerge, 10, SpringLayout.EAST, btnBamReplicateMerge);
		pnlBamManip.add(btnBamReplicateMerge);
		
		JTextArea txtFilterForPIPseq = new JTextArea();
		initializeTextArea(txtFilterForPIPseq);
		sl_pnlBamManip.putConstraint(SpringLayout.NORTH, txtFilterForPIPseq, 10, SpringLayout.SOUTH, txtBamReplicateMerge);
		sl_pnlBamManip.putConstraint(SpringLayout.EAST, txtFilterForPIPseq, -10, SpringLayout.EAST, pnlBamManip);
		txtFilterForPIPseq.setText("Filter BAM file by -1 nucleotide. Requires genome FASTA file.");
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
		
		JPanel pnlBamConvert = new JPanel();
		SpringLayout sl_pnlBamConvert = new SpringLayout();
		pnlBamConvert.setLayout(sl_pnlBamConvert);
		tabbedPane.addTab("BAM Format Converter", null, pnlBamConvert, null);
		
		JTextArea txtBamToscIDX = new JTextArea();
		initializeTextArea(txtBamToscIDX);
		txtBamToscIDX.setText("Convert BAM file to scIDX file");
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
		
		JTextArea txtBamToGFF = new JTextArea();
		initializeTextArea(txtBamToGFF);
		txtBamToGFF.setText("Convert BAM file to GFF file");
		sl_pnlBamConvert.putConstraint(SpringLayout.NORTH, txtBamToGFF, 10, SpringLayout.SOUTH, txtBamToscIDX);
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
	
		JTextArea txtBamToBed = new JTextArea();
		initializeTextArea(txtBamToBed);
		txtBamToBed.setText("Convert BAM file to BED file");
		sl_pnlBamConvert.putConstraint(SpringLayout.NORTH, txtBamToBed, 10, SpringLayout.SOUTH, txtBamToGFF);
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
		
		JTextArea txtBamToBedgraph = new JTextArea();
		initializeTextArea(txtBamToBedgraph);
		txtBamToBedgraph.setText("Convert BAM file to bedGraph file");
		sl_pnlBamConvert.putConstraint(SpringLayout.NORTH, txtBamToBedgraph, 10, SpringLayout.SOUTH, txtBamToBed);
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
		
		JPanel pnlFileUtility = new JPanel();
		SpringLayout sl_pnlFileUtility = new SpringLayout();
		pnlFileUtility.setLayout(sl_pnlFileUtility);
		tabbedPane.addTab("File Utilities", null, pnlFileUtility, null);
		
		JTextArea txtMD5 = new JTextArea();
		initializeTextArea(txtMD5);
		txtMD5.setText("Calculate MD5 checksum for files");
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

		JPanel pnlPeakCalling = new JPanel();
		SpringLayout sl_pnlPeakCalling = new SpringLayout();
		pnlPeakCalling.setLayout(sl_pnlPeakCalling);
		tabbedPane.addTab("Peak Calling", null, pnlPeakCalling, null);
		
		JTextArea txtGenetrack = new JTextArea();
		initializeTextArea(txtGenetrack);
		txtGenetrack.setText("Genetrack peak-calling algorithm");
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
		
		JTextArea txtPeakpairing = new JTextArea();
		initializeTextArea(txtPeakpairing);
		txtPeakpairing.setText("Peak-pairing algorithm");
		sl_pnlPeakCalling.putConstraint(SpringLayout.NORTH, txtPeakpairing, 10, SpringLayout.SOUTH, txtGenetrack);
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
		
		JTextArea txtReplicateMatch = new JTextArea();
		initializeTextArea(txtReplicateMatch);
		txtReplicateMatch.setText("Peak-pair replicate analysis");
		sl_pnlPeakCalling.putConstraint(SpringLayout.NORTH, txtReplicateMatch, 10, SpringLayout.SOUTH, txtPeakpairing);
		sl_pnlPeakCalling.putConstraint(SpringLayout.EAST, txtReplicateMatch, -10, SpringLayout.EAST, pnlPeakCalling);
		pnlPeakCalling.add(txtReplicateMatch);
		
		JButton btnReplicateMatch = new JButton("Replicate Match");
		sl_pnlPeakCalling.putConstraint(SpringLayout.NORTH, btnReplicateMatch, 0, SpringLayout.NORTH, txtReplicateMatch);
		sl_pnlPeakCalling.putConstraint(SpringLayout.WEST, btnReplicateMatch, 10, SpringLayout.WEST, pnlPeakCalling);
		sl_pnlPeakCalling.putConstraint(SpringLayout.WEST, txtReplicateMatch, 10, SpringLayout.EAST, btnReplicateMatch);
		pnlPeakCalling.add(btnReplicateMatch);
		btnReplicateMatch.setEnabled(false);
		
		JPanel pnlPeakAnalysis = new JPanel();
		SpringLayout sl_pnlPeakAnalysis = new SpringLayout();
		pnlPeakAnalysis.setLayout(sl_pnlPeakAnalysis);
		tabbedPane.addTab("Peak Analysis", null, pnlPeakAnalysis, null);
		
		JTextArea txtBedPeakAlignment = new JTextArea();
		initializeTextArea(txtBedPeakAlignment);
		txtBedPeakAlignment.setText("Align BED peaks to Reference BED file creating CDT files for heatmap generation");
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.NORTH, txtBedPeakAlignment, 10, SpringLayout.NORTH, pnlPeakAnalysis);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.EAST, txtBedPeakAlignment, -10, SpringLayout.EAST, pnlPeakAnalysis);
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
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.NORTH, btnBedPeakAlignment, 0, SpringLayout.NORTH, txtBedPeakAlignment);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.WEST, btnBedPeakAlignment, 10, SpringLayout.WEST, pnlPeakAnalysis);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.WEST, txtBedPeakAlignment, 10, SpringLayout.EAST, btnBedPeakAlignment);
		pnlPeakAnalysis.add(btnBedPeakAlignment);
		
		JTextArea txtBedFilter = new JTextArea();
		initializeTextArea(txtBedFilter);
		txtBedFilter.setText("Filter BED file using user-specified exclusion zone using the score column to determine which peak to retain.");
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
		
		JTextArea txtGenomicCoordinateTile = new JTextArea();
		initializeTextArea(txtGenomicCoordinateTile);
		txtGenomicCoordinateTile.setText("Generate a coordinate file that tiles (non-overlapping) across an entire genome.");
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.NORTH, txtGenomicCoordinateTile, 10, SpringLayout.SOUTH, txtBedFilter);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.EAST, txtGenomicCoordinateTile, -10, SpringLayout.EAST, pnlPeakAnalysis);
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
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.NORTH, btnGenomicCoordinateTile, 0, SpringLayout.NORTH, txtGenomicCoordinateTile);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.WEST, btnGenomicCoordinateTile, 10, SpringLayout.WEST, pnlPeakAnalysis);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.WEST, txtGenomicCoordinateTile, 10, SpringLayout.EAST, btnGenomicCoordinateTile);
		pnlPeakAnalysis.add(btnGenomicCoordinateTile);
		
		JTextArea txtRandomCoordinateGeneration = new JTextArea();
		initializeTextArea(txtRandomCoordinateGeneration);
		txtRandomCoordinateGeneration.setText("Generate a coordinate file that tiles (non-overlapping) across an entire genome.");
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.NORTH, txtRandomCoordinateGeneration, 10, SpringLayout.SOUTH, txtGenomicCoordinateTile);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.EAST, txtRandomCoordinateGeneration, -10, SpringLayout.EAST, pnlPeakAnalysis);
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
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.NORTH, btnRandomCoordinateGeneration, 0, SpringLayout.NORTH, txtRandomCoordinateGeneration);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.WEST, btnRandomCoordinateGeneration, 10, SpringLayout.WEST, pnlPeakAnalysis);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.WEST, txtRandomCoordinateGeneration, 10, SpringLayout.EAST, btnRandomCoordinateGeneration);
		pnlPeakAnalysis.add(btnRandomCoordinateGeneration);
		
		JTextArea txtOutputSignalDuplication = new JTextArea();
		initializeTextArea(txtOutputSignalDuplication);
		txtOutputSignalDuplication.setText("Calculate duplication statistics at user-specified regsions.");
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.NORTH, txtOutputSignalDuplication, 10, SpringLayout.SOUTH, txtRandomCoordinateGeneration);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.EAST, txtOutputSignalDuplication, -10, SpringLayout.EAST, pnlPeakAnalysis);
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
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.NORTH, btnSignalDuplication, 0, SpringLayout.NORTH, txtOutputSignalDuplication);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.WEST, btnSignalDuplication, 10, SpringLayout.WEST, pnlPeakAnalysis);
		sl_pnlPeakAnalysis.putConstraint(SpringLayout.WEST, txtOutputSignalDuplication, 10, SpringLayout.EAST, btnSignalDuplication);
		pnlPeakAnalysis.add(btnSignalDuplication);
			
		JPanel pnlCoordManip = new JPanel();
		tabbedPane.addTab("Coordinate File Manipulation", null, pnlCoordManip, null);
		
		JSplitPane splitPaneExpand = new JSplitPane();
		pnlCoordManip.add(splitPaneExpand);
		
		JButton btnExpandBedFile = new JButton("Expand BED File");
		btnExpandBedFile.setToolTipText("Expand BED file given user-defined criteria");
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
		
		JButton btnExpandGffFile = new JButton("Expand GFF File");
		btnExpandGffFile.setToolTipText("Expand GFF file given user-defined criteria");
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
		
		JButton btnBedToGFF = new JButton("Convert BED to GFF");
		btnBedToGFF.setToolTipText("Convert BED file to GFF file");
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
		
		JButton btnGffToBed = new JButton("Convert GFF to BED");
		btnGffToBed.setToolTipText("Convert GFF file to BED file");
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
		
		JButton btnBEDSort = new JButton("Sort BED by CDT");
		btnBEDSort.setToolTipText("Sort BED file by CDT file statistics");
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
		
		JButton btnSortGffFile = new JButton("Sort GFF by CDT");
		btnSortGffFile.setToolTipText("Sort BED file by CDT file statistics");
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
		
		JPanel pnlReadAnalysis = new JPanel();
		SpringLayout sl_pnlReadAnalysis = new SpringLayout();
		pnlReadAnalysis.setLayout(sl_pnlReadAnalysis);
		tabbedPane.addTab("Sequence Read Analysis", null, pnlReadAnalysis, null);
		
		JTextArea txtTagPileup = new JTextArea();
		initializeTextArea(txtTagPileup);
		txtTagPileup.setText("Pileup 5' ends of aligned tags given BED and BAM files according to user-defined parameters");
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
		
		JTextArea txtAggregateData = new JTextArea();
		initializeTextArea(txtAggregateData);
		txtAggregateData.setText("Compile data from CDT file into matrix according to user-specified metric");
		sl_pnlReadAnalysis.putConstraint(SpringLayout.NORTH, txtAggregateData, 10, SpringLayout.SOUTH, txtTagPileup);
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
					
		JPanel pnlSeqAnalysis = new JPanel();
		SpringLayout sl_pnlSeqAnalysis = new SpringLayout();
		pnlSeqAnalysis.setLayout(sl_pnlSeqAnalysis);
		tabbedPane.addTab("DNA Sequence Analysis", null, pnlSeqAnalysis, null);
		
		JTextArea txtFASTAExtract = new JTextArea();
		initializeTextArea(txtFASTAExtract);
		txtFASTAExtract.setText("Generate FASTA file from indexed Genome FASTA file and BED file. Script will generate FAI index if not present in Genome FASTA folder.");
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
		
		JTextArea txtRandomizeFasta = new JTextArea();
		initializeTextArea(txtRandomizeFasta);
		txtRandomizeFasta.setText("Randomize FASTA sequence for each input entry");
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
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.NORTH, btnRandomizeFasta, 0, SpringLayout.NORTH, txtRandomizeFasta);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.WEST, btnRandomizeFasta, 10, SpringLayout.WEST, pnlSeqAnalysis);
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.WEST, txtRandomizeFasta, 10, SpringLayout.EAST, btnRandomizeFasta);
		pnlSeqAnalysis.add(btnRandomizeFasta);
		
		JTextArea txtSearchMotif = new JTextArea();
		initializeTextArea(txtSearchMotif);
		txtSearchMotif.setText("Search for an IUPAC DNA sequence motif in FASTA files with mismatches allowed");
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.NORTH, txtSearchMotif, 10, SpringLayout.SOUTH, txtRandomizeFasta);
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

		JTextArea txtDnaShapeBed = new JTextArea();
		initializeTextArea(txtDnaShapeBed);
		txtDnaShapeBed.setText("Calculate intrinsic DNA shape parameters given BED file and Genome FASTA file. Based on Roh's lab DNAshape server data");
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.NORTH, txtDnaShapeBed, 10, SpringLayout.SOUTH, txtSearchMotif);
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
		
		JTextArea txtDnaShapeFasta = new JTextArea();
		initializeTextArea(txtDnaShapeFasta);
		txtDnaShapeFasta.setText("Calculate intrinsic DNA shape parameters given input FASTA files. Based on Roh's lab DNAshape server data");
		sl_pnlSeqAnalysis.putConstraint(SpringLayout.NORTH, txtDnaShapeFasta, 10, SpringLayout.SOUTH, txtDnaShapeBed);
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
		
		JPanel pnlFigure = new JPanel();
		SpringLayout sl_pnlFigure = new SpringLayout();
		pnlFigure.setLayout(sl_pnlFigure);
		tabbedPane.addTab("Figure Generation", null, pnlFigure, null);
		
		JTextArea txtHeatMap = new JTextArea();
		initializeTextArea(txtHeatMap);
		txtHeatMap.setText("Generate heat map using CDT files.");
		sl_pnlFigure.putConstraint(SpringLayout.NORTH, txtHeatMap, 10, SpringLayout.NORTH, pnlFigure);
		sl_pnlFigure.putConstraint(SpringLayout.EAST, txtHeatMap, -10, SpringLayout.EAST, pnlFigure);
		pnlFigure.add(txtHeatMap);
		
		JButton btnHeatMap = new JButton("Heat Map");
		btnHeatMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							HeatMapWindow frame = new HeatMapWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				 });
			}
		});
		sl_pnlFigure.putConstraint(SpringLayout.NORTH, btnHeatMap, 0, SpringLayout.NORTH, txtHeatMap);
		sl_pnlFigure.putConstraint(SpringLayout.WEST, btnHeatMap, 10, SpringLayout.WEST, pnlFigure);
		sl_pnlFigure.putConstraint(SpringLayout.WEST, txtHeatMap, 10, SpringLayout.EAST, btnHeatMap);
		pnlFigure.add(btnHeatMap);
		btnHeatMap.setEnabled(false);
		
		JTextArea txtMergeHeatPlot = new JTextArea();
		initializeTextArea(txtMergeHeatPlot);
		txtMergeHeatPlot.setText("Merge Sense and Antisense png plots");
		sl_pnlFigure.putConstraint(SpringLayout.NORTH, txtMergeHeatPlot, 10, SpringLayout.SOUTH, txtHeatMap);
		sl_pnlFigure.putConstraint(SpringLayout.EAST, txtMergeHeatPlot, -10, SpringLayout.EAST, pnlFigure);
		pnlFigure.add(txtMergeHeatPlot);
		
		JButton btnMergeHeatPlots = new JButton("Merge Heat Plots");
		btnMergeHeatPlots.addActionListener(new ActionListener() {
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
		sl_pnlFigure.putConstraint(SpringLayout.NORTH, btnMergeHeatPlots, 0, SpringLayout.NORTH, txtMergeHeatPlot);
		sl_pnlFigure.putConstraint(SpringLayout.WEST, btnMergeHeatPlots, 10, SpringLayout.WEST, pnlFigure);
		sl_pnlFigure.putConstraint(SpringLayout.WEST, txtMergeHeatPlot, 10, SpringLayout.EAST, btnMergeHeatPlots);
		pnlFigure.add(btnMergeHeatPlots);
		
		JTextArea txtcolorSequencePlot = new JTextArea();
		initializeTextArea(txtcolorSequencePlot);
		txtcolorSequencePlot.setText("Generate 4Color sequence plot given FASTA file and user-defined RGB colors");
		sl_pnlFigure.putConstraint(SpringLayout.NORTH, txtcolorSequencePlot, 10, SpringLayout.SOUTH, txtMergeHeatPlot);
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
		sl_pnlFigure.putConstraint(SpringLayout.NORTH, btncolorSequencePlot, 0, SpringLayout.NORTH, txtcolorSequencePlot);
		sl_pnlFigure.putConstraint(SpringLayout.WEST, btncolorSequencePlot, 10, SpringLayout.WEST, pnlFigure);
		sl_pnlFigure.putConstraint(SpringLayout.WEST, txtcolorSequencePlot, 10, SpringLayout.EAST, btncolorSequencePlot);
		pnlFigure.add(btncolorSequencePlot);

	}
	
	private void initializeTextArea(JTextArea text) {
		text.setWrapStyleWord(true);
		text.setEditable(false);
		text.setLineWrap(true);
	}
	
	/**
	 * Create the application.
	 */
	public ScriptManager() {
		initialize();
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ScriptManager window = new ScriptManager();
					window.frmScriptManager.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
