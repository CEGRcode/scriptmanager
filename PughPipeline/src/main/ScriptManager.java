package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.SpringLayout;

import components.BAIIndexerWindow;
import components.BAMtoBEDWindow;
import components.BAMtoMidpointWindow;
import components.BAMtoTABWindow;
import components.FourColorSequenceWindow;
import components.GeneTrackWindow;
import components.MergeBAMWindow;
import components.PEWindow;
import components.PeakPairWindow;
import components.SEWindow;
import components.SortBAMWindow;
import components.TagPileupWindow;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTabbedPane;
import javax.swing.JPanel;

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
		frmScriptManager.setTitle("Script Manager v0.2");
		frmScriptManager.setBounds(100, 100, 500, 275);
		frmScriptManager.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmScriptManager.setResizable(false);
		SpringLayout springLayout = new SpringLayout();
		frmScriptManager.getContentPane().setLayout(springLayout);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		springLayout.putConstraint(SpringLayout.NORTH, tabbedPane, 10, SpringLayout.NORTH, frmScriptManager.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, tabbedPane, 10, SpringLayout.WEST, frmScriptManager.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, tabbedPane, 243, SpringLayout.NORTH, frmScriptManager.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, tabbedPane, 490, SpringLayout.WEST, frmScriptManager.getContentPane());
		frmScriptManager.getContentPane().add(tabbedPane);
		
		JPanel pnlStat = new JPanel();
		tabbedPane.addTab("Statistics", null, pnlStat, null);
		
		JButton btnBAMStats = new JButton("BAM Statistics");
		btnBAMStats.setToolTipText("Output Alignment Statistics and Parameters given an Input BAM File.\nBAM file must be sorted and BAM BAI Index must be present.");
		pnlStat.add(btnBAMStats);
		springLayout.putConstraint(SpringLayout.NORTH, btnBAMStats, 43, SpringLayout.SOUTH, tabbedPane);
		springLayout.putConstraint(SpringLayout.WEST, btnBAMStats, 55, SpringLayout.WEST, frmScriptManager.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, btnBAMStats, -66, SpringLayout.SOUTH, frmScriptManager.getContentPane());
		JButton btnPEStats = new JButton("Paired-End Statistics");
		btnPEStats.setToolTipText("Output Alignment Statistics and Parameters given an Paired-End BAM File.\nAlso generates Insert Size Histogram.\nBAM file must be sorted and BAM BAI Index must be present.");
		pnlStat.add(btnPEStats);
		
		btnBAMStats.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							SEWindow frame = new SEWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				 });
			}
		});
				
		btnPEStats.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							PEWindow frame = new PEWindow();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				 });
			}
		});
		
		JPanel pnlExtract = new JPanel();
		pnlExtract.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		tabbedPane.addTab("BAM Manipulation", null, pnlExtract, null);
		
				JButton btnBaiIndexer = new JButton("BAM-BAI Indexer");
				btnBaiIndexer.setToolTipText("Generates BAI Index for given BAM files");
				pnlExtract.add(btnBaiIndexer);
				btnBaiIndexer.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
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
				pnlExtract.add(btnBamSort);
				btnBamSort.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
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
				pnlExtract.add(btnBamReplicateMerge);
				btnBamReplicateMerge.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
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
		tabbedPane.addTab("BAM Converter", null, pnlBamConvert, null);
		
		JButton btnBamToTab = new JButton("BAM to TAB");
		pnlBamConvert.add(btnBamToTab);
		btnBamToTab.setToolTipText("Convert BAM file to TAB file.");
		
		JButton btnBamToBed = new JButton("BAM to BED");
		btnBamToBed.setToolTipText("Convert BAM file to BED file.");
		pnlBamConvert.add(btnBamToBed);
		
		JButton btnBamToMidpoint = new JButton("BAM to Midpoint");
		pnlBamConvert.add(btnBamToMidpoint);

		btnBamToBed.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
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
		
		btnBamToTab.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
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
		
		btnBamToMidpoint.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
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
		
		JPanel pnlAnalysis = new JPanel();
		tabbedPane.addTab("Data Analysis", null, pnlAnalysis, null);
		
		JButton btnGenetrack = new JButton("GeneTrack");
		btnGenetrack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		pnlAnalysis.add(btnGenetrack);
		btnGenetrack.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
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
		pnlAnalysis.add(btnPeakpairing);
		btnPeakpairing.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
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
		btnTagPileup.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
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
	
		JButton btncolorSequencePlot = new JButton("4Color Sequence Plot");
		pnlAnalysis.add(btncolorSequencePlot);
		btncolorSequencePlot.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
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
