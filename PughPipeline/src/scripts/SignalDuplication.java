package scripts;

import htsjdk.samtools.AbstractBAMFileIndex;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;
import htsjdk.samtools.util.CloseableIterator;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.JLayeredPane;
import javax.swing.JTabbedPane;

import objects.GFFCoord;
import charts.LineChart;

@SuppressWarnings("serial")
public class SignalDuplication extends JFrame {
	Vector<File> bamFiles = null;
	ArrayList<GFFCoord> COORD = null;
	ArrayList<GFFCoord> GENOME = null;
	
	File input = null;
	private double WINDOW = 0;
	
	SamReader reader;
	final SamReaderFactory factory = SamReaderFactory.makeDefault().enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS, SamReaderFactory.Option.VALIDATE_CRC_CHECKSUMS).validationStringency(ValidationStringency.SILENT);

	PrintStream OUT = null;
	
	final JLayeredPane layeredPane;
	final JTabbedPane tabbedPane;
	final JTabbedPane tabbedPane_Duplication;
	final JTabbedPane tabbedPane_DupStats;
	
	public SignalDuplication(File in, Vector<File> bam, double w) {
		setTitle("Signal Duplication Rate");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 800, 600);
		
		layeredPane = new JLayeredPane();
		getContentPane().add(layeredPane, BorderLayout.CENTER);
		SpringLayout sl_layeredPane = new SpringLayout();
		layeredPane.setLayout(sl_layeredPane);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		sl_layeredPane.putConstraint(SpringLayout.NORTH, tabbedPane, 6, SpringLayout.NORTH, layeredPane);
		sl_layeredPane.putConstraint(SpringLayout.WEST, tabbedPane, 6, SpringLayout.WEST, layeredPane);
		sl_layeredPane.putConstraint(SpringLayout.SOUTH, tabbedPane, -6, SpringLayout.SOUTH, layeredPane);
		sl_layeredPane.putConstraint(SpringLayout.EAST, tabbedPane, -6, SpringLayout.EAST, layeredPane);
		layeredPane.add(tabbedPane);
			
		tabbedPane_Duplication = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Duplication Rate", null, tabbedPane_Duplication, null);
		
		tabbedPane_DupStats = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Duplication Stats", null, tabbedPane_DupStats, null);

		bamFiles = bam;
		input = in;
		WINDOW = w;
	}
	
	public void run() throws IOException {
		//Print TimeStamp
		String time = getTimeStamp();
		if(OUT != null) OUT.println(time);
		
		//Load up GFF file into COORD
		loadCoord();
		Collections.sort(COORD, GFFCoord.PeakPositionComparator);
		Collections.sort(COORD, GFFCoord.PeakChromComparator);
		
		for(int x = 0; x < bamFiles.size(); x++) {
			JTextArea DUP_STATS = new JTextArea();
			DUP_STATS.setEditable(false);
			DUP_STATS.append(time + "\n");
			
			//Check if BAI index file exists
			File f = new File(bamFiles.get(x) + ".bai");
			if(f.exists() && !f.isDirectory()) {
				if(OUT != null) OUT.println(bamFiles.get(x).getName());
				if(OUT != null) OUT.println("Chromosome_ID\tChromosome_Size\tAligned_Reads");
				DUP_STATS.append(bamFiles.get(x).getName() + "\n");
				DUP_STATS.append("Duplicate Rate\tNumber of Duplicate Molecules\n");
				
				//Code to get individual chromosome stats
				reader = factory.open(bamFiles.get(x));
				AbstractBAMFileIndex bai = (AbstractBAMFileIndex) reader.indexing().getIndex();
				
				//Variables to contain duplication rates
				HashMap<String, Integer> CHROM_COMPLEXITY = null;
				HashMap<Integer, Integer> SIG_COMPLEXITY = new HashMap<Integer, Integer>();
				HashMap<Integer, Integer> GEN_COMPLEXITY = new HashMap<Integer, Integer>();
				
				for (int z = 0; z < bai.getNumberOfReferences(); z++) {
					SAMSequenceRecord seq = reader.getFileHeader().getSequence(z);
					
					//Loop through each chromosome looking at each perfect F-R PE read
					CHROM_COMPLEXITY = new HashMap<String, Integer>();
					CloseableIterator<SAMRecord> iter = reader.query(seq.getSequenceName(), 0, seq.getSequenceLength(), false);
					while (iter.hasNext()) {
						//Create the record object 
						SAMRecord sr = iter.next();
										
						if(sr.getReadPairedFlag()) {
							if(sr.getProperPairFlag() && sr.getFirstOfPairFlag()) {
								//Unique ID
								String tagName = sr.getAlignmentStart() + "_" + sr.getMateAlignmentStart() + "_" + sr.getInferredInsertSize();
								//Duplication rate for each chrom determined
								if(CHROM_COMPLEXITY.isEmpty()) {
									CHROM_COMPLEXITY.put(tagName, new Integer(1));
								} else if(!CHROM_COMPLEXITY.containsKey(tagName)) {
									CHROM_COMPLEXITY.put(tagName, new Integer(1));
								} else if(CHROM_COMPLEXITY.containsKey(tagName)){
									CHROM_COMPLEXITY.put(tagName, new Integer(((Integer) CHROM_COMPLEXITY.get(tagName)).intValue() + 1));
								}
								
							}
						}
					}
					iter.close();
					
					//Load each chromosome up into master duplication hashmap
					Iterator<String> keys = CHROM_COMPLEXITY.keySet().iterator();
					while(keys.hasNext()) {
				         String str = (String) keys.next();
						//Determine if molecule has ANY overlap with determined signal region
						if(checkOverlap(seq.getSequenceName(), str, COORD)) { //if so, push into SIGNAL hash
					         if(SIG_COMPLEXITY.isEmpty()) {
					        	 SIG_COMPLEXITY.put(CHROM_COMPLEXITY.get(str), new Integer(1));
					         } else if(!SIG_COMPLEXITY.containsKey(CHROM_COMPLEXITY.get(str))) {
					        	 SIG_COMPLEXITY.put(CHROM_COMPLEXITY.get(str), new Integer(1));
					         } else if(SIG_COMPLEXITY.containsKey(CHROM_COMPLEXITY.get(str))){
					        	 SIG_COMPLEXITY.put(CHROM_COMPLEXITY.get(str), new Integer(((Integer) SIG_COMPLEXITY.get(CHROM_COMPLEXITY.get(str))).intValue() + 1));
					         }
				        } else { 	//else, push into BACKGROUND hash
					         if(GEN_COMPLEXITY.isEmpty()) {
					        	 GEN_COMPLEXITY.put(CHROM_COMPLEXITY.get(str), new Integer(1));
					         } else if(!GEN_COMPLEXITY.containsKey(CHROM_COMPLEXITY.get(str))) {
					        	 GEN_COMPLEXITY.put(CHROM_COMPLEXITY.get(str), new Integer(1));
					         } else if(GEN_COMPLEXITY.containsKey(CHROM_COMPLEXITY.get(str))){
					        	 GEN_COMPLEXITY.put(CHROM_COMPLEXITY.get(str), new Integer(((Integer) GEN_COMPLEXITY.get(CHROM_COMPLEXITY.get(str))).intValue() + 1));
					         }
				        }
					}			
				}
				
				reader.close();
				bai.close();

				//Duplication statistics
				double SIG_UNIQUE_MOLECULES = 0;
				double GEN_UNIQUE_MOLECULES = 0;
				ArrayList<Double> S_BIN = new ArrayList<Double>();
				ArrayList<Double> G_BIN = new ArrayList<Double>();
				//Initialize BINs for 1,2,3,4,5-9,10-99
				S_BIN.add(new Double(0)); // Bin 1
				S_BIN.add(new Double(0)); // Bin 2
				S_BIN.add(new Double(0)); // Bin 3
				S_BIN.add(new Double(0)); // Bin 4
				S_BIN.add(new Double(0)); // Bin 5-9
				S_BIN.add(new Double(0)); // Bin 10-99
				G_BIN.add(new Double(0)); // Bin 1
				G_BIN.add(new Double(0)); // Bin 2
				G_BIN.add(new Double(0)); // Bin 3
				G_BIN.add(new Double(0)); // Bin 4
				G_BIN.add(new Double(0)); // Bin 5-9
				G_BIN.add(new Double(0)); // Bin 10-99	
				
				Iterator<Integer> Ckeys = SIG_COMPLEXITY.keySet().iterator();
				while(Ckeys.hasNext()) {
			         Integer str = (Integer) Ckeys.next();
			         if(str.intValue() == 1) S_BIN.set(0, SIG_COMPLEXITY.get(str).doubleValue());
			         else if(str.intValue() == 2) S_BIN.set(1, SIG_COMPLEXITY.get(str).doubleValue() * 2);
			         else if(str.intValue() == 3) S_BIN.set(2, SIG_COMPLEXITY.get(str).doubleValue() * 3);
			         else if(str.intValue() == 4) S_BIN.set(3, SIG_COMPLEXITY.get(str).doubleValue() * 4);
			         else if(str.intValue() >= 5 && str.intValue() < 10) S_BIN.set(4, S_BIN.get(4) + SIG_COMPLEXITY.get(str).doubleValue() * str.doubleValue());
			         else if(str.intValue() >= 10) {
			        	 int index = (int) ((Math.log10(str.intValue())) + 4);
			        	 //Add new bins by order of magnitudes until array size matches current duplication level
			        	 while(S_BIN.size() <= index) {
			        		 S_BIN.add(new Double(0));
			        	 }
			        	 S_BIN.set(index, new Double((SIG_COMPLEXITY.get(str).doubleValue() * str.doubleValue()) + S_BIN.get(index)));
			         }
			         SIG_UNIQUE_MOLECULES += SIG_COMPLEXITY.get(str).doubleValue(); 
				}
				
				Iterator<Integer> Gkeys = GEN_COMPLEXITY.keySet().iterator();
				while(Gkeys.hasNext()) {
			         Integer str = (Integer) Gkeys.next();
			         if(str.intValue() == 1) G_BIN.set(0, GEN_COMPLEXITY.get(str).doubleValue());
			         else if(str.intValue() == 2) G_BIN.set(1, GEN_COMPLEXITY.get(str).doubleValue() * 2);
			         else if(str.intValue() == 3) G_BIN.set(2, GEN_COMPLEXITY.get(str).doubleValue() * 3);
			         else if(str.intValue() == 4) G_BIN.set(3, GEN_COMPLEXITY.get(str).doubleValue() * 4);
			         else if(str.intValue() >= 5 && str.intValue() < 10) G_BIN.set(4, G_BIN.get(4) + GEN_COMPLEXITY.get(str).doubleValue() * str.doubleValue());
			         else if(str.intValue() >= 10) {
			        	 int index = (int) ((Math.log10(str.intValue())) + 4);
			        	 //Add new bins by order of magnitudes until array size matches current duplication level
			        	 while(G_BIN.size() <= index) {
			        		 G_BIN.add(new Double(0));
			        	 }
			        	 G_BIN.set(index, new Double((GEN_COMPLEXITY.get(str).doubleValue() * str.doubleValue()) + G_BIN.get(index)));
			         }
			         GEN_UNIQUE_MOLECULES += GEN_COMPLEXITY.get(str).doubleValue(); 
				}
				
				String[] BIN_NAME = new String[S_BIN.size()];
				for(int z = 0; z < S_BIN.size(); z++) {
					if(z < 4) { BIN_NAME[z] = new Integer(z + 1).toString(); }
					else if(z == 4) { BIN_NAME[z] = "5-9"; }
					else if(z >= 5) {
						int start = (int) Math.pow(10, z - 4);
						int stop = (int) Math.pow(10, z - 3) - 1;
						BIN_NAME[z] = start + "-" + stop;
					}
					DUP_STATS.append(BIN_NAME[z] + "\t" + S_BIN.get(z).toString() + "\n");
				}
				DUP_STATS.append("Signal Unique Molecules:\n" + SIG_UNIQUE_MOLECULES  + "\n\n");

				for(int z = 0; z < BIN_NAME.length; z++) {
					DUP_STATS.append(BIN_NAME[z] + "\t" + G_BIN.get(z).toString() + "\n");
				}
				DUP_STATS.append("Genomic Unique Molecules:\n" + GEN_UNIQUE_MOLECULES + "\n");
				
				//Add duplication stats to tabbed pane
				DUP_STATS.setCaretPosition(0);
				JScrollPane dup_pane = new JScrollPane(DUP_STATS, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				tabbedPane_DupStats.add(bamFiles.get(x).getName(), dup_pane);
				tabbedPane_Duplication.add(bamFiles.get(x).getName(), LineChart.createLineChart(S_BIN, G_BIN, BIN_NAME));
				
		        firePropertyChange("bam",x, x + 1);

			} else {
				if(OUT != null) OUT.println("BAI Index File does not exist for: " + bamFiles.get(x).getName() + "\n");
				DUP_STATS.append("BAI Index File does not exist for: " + bamFiles.get(x).getName() + "\n\n");
			}
		}
		if(OUT != null) OUT.close();
	}
	
	public boolean checkOverlap(String chrom, String ID, ArrayList<GFFCoord> coord) {
		String[] temp = ID.split("_");
		int start = Integer.parseInt(temp[0]); 
		int stop = start + Integer.parseInt(temp[2]);
		if(start > stop) {
			int x = stop;
			stop = start;
			start = x;
		}

		for(int z = 0; z < coord.size(); z++) {
			if(coord.get(z).getChrom().equals(chrom)) {
				//System.out.println("here");
				if(start <= coord.get(z).getStop() + (WINDOW / 2) && start >= coord.get(z).getStart() - (WINDOW / 2)) { return true; }
				if(stop <= coord.get(z).getStop() +  (WINDOW / 2) && stop >= coord.get(z).getStart() -  (WINDOW / 2)) { return true; }
				if(start <= coord.get(z).getStart() -  (WINDOW / 2) && stop >= coord.get(z).getStop() +  (WINDOW / 2)) { return true; }
			}
		}
		return false;
	}
	
    public void loadCoord() throws FileNotFoundException {
    	//chr1	cwpair	.	45524	45525	3067.0	.	.	cw_distance=26

		Scanner scan = new Scanner(input);
		COORD = new ArrayList<GFFCoord>();
		while (scan.hasNextLine()) {
			String[] temp = scan.nextLine().split("\t");
			if(temp.length > 3) { 
				if(!temp[0].contains("track") && !temp[0].contains("#")) {
					String name = "";
					if(temp.length > 7) { name = temp[8]; }
					else { name = temp[0] + "_" + temp[3] + "_" + temp[4]; }
					if(Integer.parseInt(temp[3]) >= 0) {
						if(temp[6].equals("+") || temp[6].equals(".")) { COORD.add(new GFFCoord(temp[0], Integer.parseInt(temp[3]), Integer.parseInt(temp[4]), "+", name)); }
						else { COORD.add(new GFFCoord(temp[0], Integer.parseInt(temp[3]), Integer.parseInt(temp[4]), "-", name)); }
					} else {
						System.out.println("Invalid Coordinate in File!!!\n" + Arrays.toString(temp));
					}
				}
			}
		}
		scan.close();
    }
	
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}
