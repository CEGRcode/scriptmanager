package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;

import window_interface.BAM_Format_Converter.BAMtoBEDWindow;
import window_interface.BAM_Format_Converter.BAMtoMidpointWindow;
import window_interface.BAM_Format_Converter.BAMtoscIDXWindow;
import window_interface.BAM_Format_Converter.FilterforPermanganateSeqWindow;
import window_interface.BAM_Manipulation.BAIIndexerWindow;
import window_interface.BAM_Manipulation.BAMRemoveDupWindow;
import window_interface.BAM_Manipulation.MergeBAMWindow;
import window_interface.BAM_Manipulation.SortBAMWindow;
import window_interface.BAM_Statistics.PEStatWindow;
import window_interface.BAM_Statistics.SEStatWindow;
import window_interface.BAM_Statistics.SignalDuplicationWindow;
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
import window_interface.Tag_Analysis.GeneTrackWindow;
import window_interface.Tag_Analysis.HeatMapWindow;
import window_interface.Tag_Analysis.MergeHeatMapWindow;
import window_interface.Tag_Analysis.PeakPairWindow;
import window_interface.Tag_Analysis.TagPileupWindow;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JSplitPane;

public class ScriptManager {

	private JFrame frmScriptManager;

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmScriptManager = new JFrame();
		frmScriptManager.setTitle("Script Manager v0.9");
		frmScriptManager.setBounds(100, 100, 500, 276);
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
		
		JPanel pnlCoord_Manip = new JPanel();
		tabbedPane.addTab("Coordinate Manipulation", null, pnlCoord_Manip, null);
		
		JSplitPane splitPaneExpand = new JSplitPane();
		pnlCoord_Manip.add(splitPaneExpand);
		
		JButton btnExpandBedFile = new JButton("Expand BED File");
		splitPaneExpand.setLeftComponent(btnExpandBedFile);
		
		JButton btnExpandGffFile = new JButton("Expand GFF File");
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
		splitPaneConvert.setLeftComponent(btnBedToGFF);
		
		JButton btnGffToBed = new JButton("Convert GFF to BED");
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
		splitPaneSort.setLeftComponent(btnBEDSort);
		
		JButton btnSortGffFile = new JButton("Sort GFF by CDT");
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
		
		JPanel pnlBamConvert = new JPanel();
		tabbedPane.addTab("BAM Format Converter", null, pnlBamConvert, null);
		
		JButton btnBamToscIDX = new JButton("BAM to scIDX");
		pnlBamConvert.add(btnBamToscIDX);
		btnBamToscIDX.setToolTipText("Convert BAM file to scIDX file.");
		
		JButton btnBamToBed = new JButton("BAM to BED");
		btnBamToBed.setToolTipText("Convert BAM file to BED file.");
		pnlBamConvert.add(btnBamToBed);
		
		JButton btnBamToMidpoint = new JButton("BAM to Midpoint");
		pnlBamConvert.add(btnBamToMidpoint);
		
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
		pnlBamConvert.add(btnFilterForPermanganateseq);
		
		JPanel pnlTagAnalysis = new JPanel();
		tabbedPane.addTab("Tag Analysis", null, pnlTagAnalysis, null);
		
		JButton btnGenetrack = new JButton("GeneTrack");
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
		pnlTagAnalysis.add(btnTagPileup);
		
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
			pnlTagAnalysis.add(btnHeatMap);
			
			JButton btnMergeHeatPlots = new JButton("Merge Heat Plots");
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
			
		
			
			JPanel pnlSeqAnalysis = new JPanel();
			tabbedPane.addTab("Sequence Analysis", null, pnlSeqAnalysis, null);
			
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
			pnlSeqAnalysis.add(btnFASTAExtract);
			
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
			pnlSeqAnalysis.add(btnRandomizeFasta);
			
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
			pnlSeqAnalysis.add(btncolorSequencePlot);
			
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
			pnlSeqAnalysis.add(btnDnaShapeBed);
			
			JButton btnDnaShapeFasta = new JButton("DNA Shape from FASTA");
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
		
		btnBamToMidpoint.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							BAMtoMidpointWindow frame = new BAMtoMidpointWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				 });
			}
		});
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
