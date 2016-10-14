package main;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import window_interface.BAM_Format_Converter.BAMtoBEDWindow;
import window_interface.BAM_Format_Converter.BAMtoGFFWindow;
import window_interface.BAM_Format_Converter.BAMtoscIDXWindow;
import window_interface.BAM_Format_Converter.FilterforPermanganateSeqWindow;
import window_interface.BAM_Manipulation.BAIIndexerWindow;
import window_interface.BAM_Manipulation.BAMRemoveDupWindow;
import window_interface.BAM_Manipulation.MergeBAMWindow;
import window_interface.BAM_Manipulation.SortBAMWindow;
import window_interface.BAM_Statistics.PEStatWindow;
import window_interface.BAM_Statistics.SEStatWindow;
import window_interface.BAM_Statistics.SignalDuplicationWindow;
import window_interface.Cluster_Tools.CorrelationMatrixWindow;
import window_interface.Cluster_Tools.HierarchicalClusteringWindow;
import window_interface.Coordinate_Manipulation.BED_Manipulation.BEDtoGFFWindow;
import window_interface.Coordinate_Manipulation.BED_Manipulation.ExpandBEDWindow;
import window_interface.Coordinate_Manipulation.BED_Manipulation.SortBEDWindow;
import window_interface.Coordinate_Manipulation.GFF_Manipulation.ExpandGFFWindow;
import window_interface.Coordinate_Manipulation.GFF_Manipulation.GFFtoBEDWindow;
import window_interface.Coordinate_Manipulation.GFF_Manipulation.SortGFFWindow;
import window_interface.Sequence_Analysis.DNAShapefromBEDWindow;
import window_interface.Sequence_Analysis.DNAShapefromFASTAWindow;
import window_interface.Sequence_Analysis.FASTAExtractWindow;
import window_interface.Sequence_Analysis.FourColorSequenceWindow;
import window_interface.Sequence_Analysis.RandomizeFASTAWindow;
import window_interface.Tag_Analysis.AggregateDataWindow;
import window_interface.Tag_Analysis.GeneTrackWindow;
import window_interface.Tag_Analysis.HeatMapWindow;
import window_interface.Tag_Analysis.MergeHeatMapWindow;
import window_interface.Tag_Analysis.PeakPairWindow;
import window_interface.Tag_Analysis.TagPileupWindow;

public class ScriptManager {

	private JFrame frmScriptManager;

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmScriptManager = new JFrame();
		frmScriptManager.setTitle("Script Manager v0.11");
		frmScriptManager.setBounds(100, 100, 475, 275);
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
		tabbedPane.addTab("BAM Statistics", null, pnlStat, null);
		
		JButton btnBAMStats = new JButton("BAM Statistics");
		btnBAMStats.setToolTipText("Output Alignment Statistics and Parameters given an Input BAM File.\nBAM file must be sorted and BAM BAI Index must be present.");
		pnlStat.add(btnBAMStats);
		springLayout.putConstraint(SpringLayout.NORTH, btnBAMStats, 43, SpringLayout.SOUTH, tabbedPane);
		springLayout.putConstraint(SpringLayout.WEST, btnBAMStats, 55, SpringLayout.WEST, frmScriptManager.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, btnBAMStats, -66, SpringLayout.SOUTH, frmScriptManager.getContentPane());
		JButton btnPEStats = new JButton("Paired-End Statistics");
		btnPEStats.setToolTipText("Output Alignment Statistics and Parameters given an Paired-End BAM File.\nAlso generates Insert Size Histogram.\nBAM file must be sorted and BAM BAI Index must be present.");
		pnlStat.add(btnPEStats);
		
		JButton btnSignalDuplication = new JButton("Signal Duplication");
		btnSignalDuplication.setToolTipText("Output signal duplication statistics");
		pnlStat.add(btnSignalDuplication);
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
		
		JPanel pnlBAM_Manip = new JPanel();
		pnlBAM_Manip.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		tabbedPane.addTab("BAM Manipulation", null, pnlBAM_Manip, null);
		
				JButton btnBaiIndexer = new JButton("BAM-BAI Indexer");
				btnBaiIndexer.setToolTipText("Generates BAI Index for given BAM files");
				pnlBAM_Manip.add(btnBaiIndexer);
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
				
				JButton btnBamSort = new JButton("BAM File Sorter");
				btnBamSort.setToolTipText("Sort BAM files in order to efficiently extract and manipulate.\nRAM intensive process. If program freezes, increase JAVA heap size");
				pnlBAM_Manip.add(btnBamSort);
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
				btnBamRemoveDuplicates.setToolTipText("Removes duplicate reads in Paired-End sequencing given identical 5' read locations\nRAM intensive process. If program freezes, increase JAVA heap size");
				pnlBAM_Manip.add(btnBamRemoveDuplicates);
				
				JButton btnBamReplicateMerge = new JButton("BAM Replicate Merge");
				btnBamReplicateMerge.setToolTipText("Merges Multiple BAM files into single BAM file.\nSorting is Performed Automatically.\nRAM intensive process. If program freezes, increase JAVA heap size");
				pnlBAM_Manip.add(btnBamReplicateMerge);
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
		
		JPanel pnlBamConvert = new JPanel();
		tabbedPane.addTab("BAM Format Converter", null, pnlBamConvert, null);
		
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
		btnBamToscIDX.setToolTipText("Convert BAM file to scIDX file.");
		pnlBamConvert.add(btnBamToscIDX);
		
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
		btnBamToBed.setToolTipText("Convert BAM file to BED file.");
		pnlBamConvert.add(btnBamToBed);
				
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
		btnBamToGff.setToolTipText("Convert BAM file to GFF file.");
		pnlBamConvert.add(btnBamToGff);
		
		JButton btnFilterForPermanganateseq = new JButton("Filter for Permanganate-Seq");
		btnFilterForPermanganateseq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							FilterforPermanganateSeqWindow frame = new FilterforPermanganateSeqWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				 });
			}
		});
		btnFilterForPermanganateseq.setToolTipText("Filter BAM file by -1 nucleotide");
		pnlBamConvert.add(btnFilterForPermanganateseq);
		
		JPanel pnlCoord_Manip = new JPanel();
		tabbedPane.addTab("Coordinate File Manipulation", null, pnlCoord_Manip, null);
		
		JSplitPane splitPaneExpand = new JSplitPane();
		pnlCoord_Manip.add(splitPaneExpand);
		
		JButton btnExpandBedFile = new JButton("Expand BED File");
		btnExpandBedFile.setToolTipText("Expand BED file given user-defined criteria");
		splitPaneExpand.setLeftComponent(btnExpandBedFile);
		
		JButton btnExpandGffFile = new JButton("Expand GFF File");
		btnExpandGffFile.setToolTipText("Expand GFF file given user-defined criteria");
		splitPaneExpand.setRightComponent(btnExpandGffFile);
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
		
		JSplitPane splitPaneConvert = new JSplitPane();
		pnlCoord_Manip.add(splitPaneConvert);
		
		JButton btnBedToGFF = new JButton("Convert BED to GFF");
		btnBedToGFF.setToolTipText("Convert BED file to GFF file");
		splitPaneConvert.setLeftComponent(btnBedToGFF);
		
		JButton btnGffToBed = new JButton("Convert GFF to BED");
		btnGffToBed.setToolTipText("Convert GFF file to BED file");
		splitPaneConvert.setRightComponent(btnGffToBed);
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
		
		JSplitPane splitPaneSort = new JSplitPane();
		pnlCoord_Manip.add(splitPaneSort);
		
		JButton btnBEDSort = new JButton("Sort BED by CDT");
		btnBEDSort.setToolTipText("Sort BED file by CDT file statistics");
		splitPaneSort.setLeftComponent(btnBEDSort);
		
		JButton btnSortGffFile = new JButton("Sort GFF by CDT");
		btnSortGffFile.setToolTipText("Sort BED file by CDT file statistics");
		splitPaneSort.setRightComponent(btnSortGffFile);
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
		
		JPanel pnlCluster = new JPanel();
		tabbedPane.addTab("Cluster Tools", null, pnlCluster, null);
		
		JButton btnCorrelationMatrix = new JButton("Create Correlation Matrix");
		btnCorrelationMatrix.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							CorrelationMatrixWindow frame = new CorrelationMatrixWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				 });
			}
		});
		pnlCluster.add(btnCorrelationMatrix);
		
		JButton btnHierarchicalClustering = new JButton("Hierarchical Clustering");
		btnHierarchicalClustering.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							HierarchicalClusteringWindow frame = new HierarchicalClusteringWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				 });
			}
		});
		pnlCluster.add(btnHierarchicalClustering);
		
		JButton btnKmeansClustering = new JButton("k-Means Clustering");
		btnKmeansClustering.setEnabled(false);
		pnlCluster.add(btnKmeansClustering);

		
		JPanel pnlTagAnalysis = new JPanel();
		tabbedPane.addTab("Sequence Tag Analysis", null, pnlTagAnalysis, null);
		
		JButton btnGenetrack = new JButton("GeneTrack");
		btnGenetrack.setToolTipText("Genetrack peak-calling algorithm");
		btnGenetrack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		pnlTagAnalysis.add(btnGenetrack);
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
		
		JButton btnPeakpairing = new JButton("Peak-Pairing");
		btnPeakpairing.setEnabled(false);
		pnlTagAnalysis.add(btnPeakpairing);
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
		
		JButton btnTagPileup = new JButton("Tag Pileup");
		btnTagPileup.setToolTipText("Pileup 5' ends of aligned tags given BED and BAM files according to user-defined parameters");
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
		
		JButton btnReplicateMatch = new JButton("Replicate Match");
		btnReplicateMatch.setEnabled(false);
		pnlTagAnalysis.add(btnReplicateMatch);
		pnlTagAnalysis.add(btnTagPileup);
		
			JButton btnHeatMap = new JButton("Heat Map");
			btnHeatMap.setToolTipText("Generate heat map using CDT files");
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
			pnlTagAnalysis.add(btnHeatMap);
			
			JButton btnMergeHeatPlots = new JButton("Merge Heat Plots");
			btnMergeHeatPlots.setToolTipText("Merge Sense and Antisense png plots");
			pnlTagAnalysis.add(btnMergeHeatPlots);
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
			pnlTagAnalysis.add(btnAggregateData);
					
			JPanel pnlSeqAnalysis = new JPanel();
			tabbedPane.addTab("DNA Sequence Analysis", null, pnlSeqAnalysis, null);
			
			JButton btnFASTAExtract = new JButton("FASTA from BED");
			btnFASTAExtract.setToolTipText("Generate FASTA file from Genome FASTA file and BED file");
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
			pnlSeqAnalysis.add(btnFASTAExtract);
			
			JButton btnRandomizeFasta = new JButton("Randomize FASTA");
			btnRandomizeFasta.setToolTipText("Randomize FASTA sequence for each input entry");
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
			pnlSeqAnalysis.add(btnRandomizeFasta);
			
			JButton btncolorSequencePlot = new JButton("4Color Sequence Plot");
			btncolorSequencePlot.setToolTipText("Generate 4Color sequence plot given FASTA file and user-defined RGB colors");
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
			pnlSeqAnalysis.add(btncolorSequencePlot);
			
			JButton btnDnaShapeBed = new JButton("DNA Shape from BED");
			btnDnaShapeBed.setToolTipText("Calculate intrinsic DNA shape parameters given BED file and Genome FASTA file");
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
			pnlSeqAnalysis.add(btnDnaShapeBed);
			
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
			pnlSeqAnalysis.add(btnDnaShapeFasta);
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
