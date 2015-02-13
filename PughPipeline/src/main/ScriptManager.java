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
import window_interface.BAM_Format_Converter.BAMtoTABWindow;
import window_interface.BAM_Manipulation.BAIIndexerWindow;
import window_interface.BAM_Manipulation.MergeBAMWindow;
import window_interface.BAM_Manipulation.SortBAMWindow;
import window_interface.BAM_Statistics.PEStatWindow;
import window_interface.BAM_Statistics.SEStatWindow;
import window_interface.BED_Manipulation.BEDtoGFFWindow;
import window_interface.BED_Manipulation.ExpandBEDWindow;
import window_interface.BED_Manipulation.SortBEDWindow;
import window_interface.Data_Analysis.FourColorSequenceWindow;
import window_interface.Data_Analysis.GeneTrackWindow;
import window_interface.Data_Analysis.PeakPairWindow;
import window_interface.Data_Analysis.SignalDuplicationWindow;
import window_interface.Data_Analysis.TagPileupWindow;
import window_interface.GFF_Manipulation.ExpandGFFWindow;
import window_interface.GFF_Manipulation.GFFtoBEDWindow;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ScriptManager {

	private JFrame frmScriptManager;

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmScriptManager = new JFrame();
		frmScriptManager.setTitle("Script Manager v0.5");
		frmScriptManager.setBounds(100, 100, 450, 250);
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
		
		JPanel pnlBED_Manip = new JPanel();
		tabbedPane.addTab("BED Manipulation", null, pnlBED_Manip, null);
		
		JButton btnExpandBedFile = new JButton("Expand BED File");
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
		pnlBED_Manip.add(btnExpandBedFile);
		
		JButton btnBedToGFF = new JButton("Convert BED to GFF");
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
		
		JButton btnBEDSort = new JButton("Sort BED File");
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
		pnlBED_Manip.add(btnBEDSort);
		pnlBED_Manip.add(btnBedToGFF);
		
		JPanel pnlGFF_Manip = new JPanel();
		tabbedPane.addTab("GFF Manipulation", null, pnlGFF_Manip, null);
		
		JButton btnExpandGffFile = new JButton("Expand GFF File");
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
		pnlGFF_Manip.add(btnExpandGffFile);
		
		JButton btnSortGffFile = new JButton("Sort GFF File");
		btnSortGffFile.setEnabled(false);
		pnlGFF_Manip.add(btnSortGffFile);
		
		JButton btnGffToBed = new JButton("Convert GFF to BED");
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
		pnlGFF_Manip.add(btnGffToBed);
		
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
		
		JButton btnBamToTab = new JButton("BAM to TAB");
		pnlBamConvert.add(btnBamToTab);
		btnBamToTab.setToolTipText("Convert BAM file to TAB file.");
		
		JButton btnBamToBed = new JButton("BAM to BED");
		btnBamToBed.setToolTipText("Convert BAM file to BED file.");
		pnlBamConvert.add(btnBamToBed);
		
		JButton btnBamToMidpoint = new JButton("BAM to Midpoint");
		pnlBamConvert.add(btnBamToMidpoint);
		
		JPanel pnlAnalysis = new JPanel();
		tabbedPane.addTab("Data Analysis", null, pnlAnalysis, null);
		
		JButton btnGenetrack = new JButton("GeneTrack");
		btnGenetrack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		pnlAnalysis.add(btnGenetrack);
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
		pnlAnalysis.add(btnPeakpairing);
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
		pnlAnalysis.add(btnTagPileup);
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
		
		JButton btnSignalDuplication = new JButton("Signal Duplication");
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
		pnlAnalysis.add(btnSignalDuplication);
			
			JButton btnFASTAExtract = new JButton("FASTA from BED");
			btnFASTAExtract.setEnabled(false);
			pnlAnalysis.add(btnFASTAExtract);
		
			JButton btncolorSequencePlot = new JButton("4Color Sequence Plot");
			pnlAnalysis.add(btncolorSequencePlot);
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
		
		btnBamToTab.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							BAMtoTABWindow frame = new BAMtoTABWindow();
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
