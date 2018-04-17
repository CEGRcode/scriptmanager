package scripts.BAM_Statistics;

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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.JLayeredPane;
import javax.swing.JTabbedPane;

import charts.Histogram;
import charts.LineChart;

@SuppressWarnings("serial")
public class PEStats extends JFrame {
	
	Vector<File> bamFiles = null;
	private File OUTPUT_PATH = null;
	private boolean OUTPUT_STATUS = false;
	PrintStream OUT = null;
	private File OUTPNG = null;
	private static int MIN_INSERT = 0;
	private static int MAX_INSERT = 1000;
	
	SamReader reader;
	final SamReaderFactory factory = SamReaderFactory.makeDefault().enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS, SamReaderFactory.Option.VALIDATE_CRC_CHECKSUMS).validationStringency(ValidationStringency.SILENT);

	final JLayeredPane layeredPane;
	final JTabbedPane tabbedPane;
	final JTabbedPane tabbedPane_Histogram;
	final JTabbedPane tabbedPane_InsertStats;
	final JTabbedPane tabbedPane_Duplication;
	final JTabbedPane tabbedPane_DupStats;
		
	public PEStats(Vector<File> input, File o, boolean out, int min, int max) {
		setTitle("BAM File Paired-end Statistics");
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
		
		tabbedPane_Histogram = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Insert Histogram", null, tabbedPane_Histogram, null);
		
		tabbedPane_InsertStats = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("PE Insert Stats", null, tabbedPane_InsertStats, null);
		
		tabbedPane_Duplication = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Duplication Rate", null, tabbedPane_Duplication, null);
		
		tabbedPane_DupStats = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Duplication Stats", null, tabbedPane_DupStats, null);

		bamFiles = input;
		OUTPUT_PATH = o;
		OUTPUT_STATUS = out;
		MIN_INSERT = min;
		MAX_INSERT = max;
	}
	
	public void run() throws IOException {
		//Iterate through all BAM files in Vector	
		for(int x = 0; x < bamFiles.size(); x++) {
			//Open Output File
			if(OUTPUT_STATUS) {
				String NAME = bamFiles.get(x).getName().split("\\.")[0];
				if(OUTPUT_PATH != null) {
					try {
						OUT = new PrintStream(new File(OUTPUT_PATH.getCanonicalPath() + File.separator + NAME + "_InsertHistogram.out"));
						OUTPNG = new File(OUTPUT_PATH.getCanonicalPath() + File.separator + NAME + "_PE.png");
					}
					catch (FileNotFoundException e) { e.printStackTrace(); }
					catch (IOException e) {	e.printStackTrace(); }
				} else {
					try {
						OUT = new PrintStream(new File(NAME));
						OUTPNG = new File(NAME + "_PE.png");
					}
					catch (FileNotFoundException e) { e.printStackTrace(); }
				}				
			}	
			
			//Print TimeStamp
			String time = getTimeStamp();
			JTextArea PE_STATS = new JTextArea();
			PE_STATS.setEditable(false);
			PE_STATS.append(time + "\n");
			JTextArea DUP_STATS = new JTextArea();
			DUP_STATS.setEditable(false);
			DUP_STATS.append(time + "\n");
						
			//Check if BAI index file exists
			File f = new File(bamFiles.get(x) + ".bai");
			if(f.exists() && !f.isDirectory()) {
				PE_STATS.append(bamFiles.get(x).getName() + "\n");
				PE_STATS.append("Chromosome_ID\tChromosome_Size\tAligned_Reads\n");
				DUP_STATS.append(bamFiles.get(x).getName() + "\n");
				DUP_STATS.append("Duplicate Rate\tNumber of Duplicate Molecules\n");
				
				//Code to get individual chromosome stats
				reader = factory.open(bamFiles.get(x));
				AbstractBAMFileIndex bai = (AbstractBAMFileIndex) reader.indexing().getIndex();
				
				//Variables to keep track of insert size histogram
				double InsertAverage = 0;
				double counter = 0;
				double[] HIST = new double[(MAX_INSERT - MIN_INSERT) + 1];
				
				//Variables to contain duplication rates
				HashMap<String, Integer> CHROM_COMPLEXITY = null;
				HashMap<Integer, Integer> ALL_COMPLEXITY = new HashMap<Integer, Integer>();
				
				//Variables which contain basic sequence information
				double totalAlignedRead1 = 0;
				double totalAlignedRead2 = 0;
				double totalAlignedReads = 0;
				double totalGenome = 0;
			
				for (int z = 0; z < bai.getNumberOfReferences(); z++) {
					SAMSequenceRecord seq = reader.getFileHeader().getSequence(z);
					double aligned = reader.indexing().getIndex().getMetaData(z).getAlignedRecordCount();

					//Basic statistic calculations
					PE_STATS.append(seq.getSequenceName() + "\t" + seq.getSequenceLength() + "\t" + aligned + "\n");
					totalGenome += seq.getSequenceLength();
					
					//Loop through each chromosome looking at each perfect F-R PE read
					CHROM_COMPLEXITY = new HashMap<String, Integer>();
					CloseableIterator<SAMRecord> iter = reader.query(seq.getSequenceName(), 0, seq.getSequenceLength(), false);
					while (iter.hasNext()) {
						//Create the record object 
						SAMRecord sr = iter.next();	
						
						if(!sr.getReadUnmappedFlag()) { //Test for mapped read
							if(sr.getReadPairedFlag()) { //Test for paired-end status
								if(sr.getSecondOfPairFlag()) { totalAlignedRead2++; } //count read 2
								else if(sr.getFirstOfPairFlag()) { totalAlignedRead1++; } // count read 1
								//Insert size calculations
								if(sr.getProperPairFlag() && sr.getFirstOfPairFlag()) {
									//Insert size calculations
									int distance = Math.abs(sr.getInferredInsertSize());
									if(distance <= MAX_INSERT && distance >= MIN_INSERT) HIST[distance - MIN_INSERT]++;
									InsertAverage += distance;
									counter++;
									
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
							} else { //If the read is mapped but not paired-end, default to read 1
								totalAlignedRead1++;
							}
						}
					}
					iter.close();
					
					//Load each chromosome up into master duplication hashmap
					Iterator<String> keys = CHROM_COMPLEXITY.keySet().iterator();
					while(keys.hasNext()) {
				         String str = (String) keys.next();
				         if(ALL_COMPLEXITY.isEmpty()) {
				        	 ALL_COMPLEXITY.put(CHROM_COMPLEXITY.get(str), new Integer(1));
				         } else if(!ALL_COMPLEXITY.containsKey(CHROM_COMPLEXITY.get(str))) {
				        	 ALL_COMPLEXITY.put(CHROM_COMPLEXITY.get(str), new Integer(1));
				         } else if(ALL_COMPLEXITY.containsKey(CHROM_COMPLEXITY.get(str))){
								ALL_COMPLEXITY.put(CHROM_COMPLEXITY.get(str), new Integer(((Integer) ALL_COMPLEXITY.get(CHROM_COMPLEXITY.get(str))).intValue() + 1));
				         }
					}			
				}
				totalAlignedReads = totalAlignedRead1 + totalAlignedRead2;
				PE_STATS.append("Total Genome Size: " + totalGenome + "\tTotal Aligned Tags: " + totalAlignedReads + "\n\n");
				
				//Output replicates used to make bam file
				for( String comment : reader.getFileHeader().getComments()) {
					PE_STATS.append(comment + "\n");
				}
				
				//Output program used to align bam file
				for (int z = 0; z < reader.getFileHeader().getProgramRecords().size(); z++) {
					PE_STATS.append(reader.getFileHeader().getProgramRecords().get(z).getId() + "\t");
					PE_STATS.append(reader.getFileHeader().getProgramRecords().get(z).getProgramVersion() + "\n");
					PE_STATS.append(reader.getFileHeader().getProgramRecords().get(z).getCommandLine() + "\n");
				}
				reader.close();
				bai.close();
				PE_STATS.append("\n");
				
				//Insert Size statistics
				if(counter != 0) InsertAverage /= counter;
				PE_STATS.append("Average Insert Size: " + InsertAverage + "\n");
				PE_STATS.append("Median Insert Size: " + getMedian(HIST) + "\n");
				PE_STATS.append("Std deviation of Insert Size: " + getStdDev(HIST, InsertAverage) + "\n");
				PE_STATS.append("Number of ReadPairs: " + counter + "\n\nHistogram\nSize (bp)\tFrequency\n");
				if(OUT != null) { OUT.println("InsertSize (bp)\t" + bamFiles.get(x).getName()); }

				int[] DOMAIN = new int[(MAX_INSERT - MIN_INSERT) + 1];
				for(int z = 0; z < HIST.length; z++) {
					int bp = MIN_INSERT + z;
					DOMAIN[z] = bp;
					if(OUT != null) OUT.println(bp + "\t" + HIST[z]);
					PE_STATS.append(bp + "\t" + HIST[z] + "\n");
				}
				
				//Duplication statistics
				double UNIQUE_MOLECULES = 0;
				String[] BIN_NAME = initializeBIN_Names();
				ArrayList<Double> BIN = new ArrayList<Double>();
				initializeBINS(BIN);	
				
				Iterator<Integer> keys = ALL_COMPLEXITY.keySet().iterator();
				while(keys.hasNext()) {
			         Integer str = (Integer) keys.next();
			         int index = getBinIndex(str.intValue());
			         BIN.set(index, BIN.get(index) + (ALL_COMPLEXITY.get(str).doubleValue() * str.doubleValue()));		         
			         UNIQUE_MOLECULES += ALL_COMPLEXITY.get(str).doubleValue(); 
				}
				
				for(int z = 0; z < BIN.size(); z++) {
					DUP_STATS.append(BIN_NAME[z] + "\t" + BIN.get(z).toString() + "\n");
				}
				DUP_STATS.append("Unique Molecules:\n" + UNIQUE_MOLECULES);
				
				//Add pe stats to tabbed pane
				PE_STATS.setCaretPosition(0);
				JScrollPane pe_pane = new JScrollPane(PE_STATS, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				tabbedPane_InsertStats.add(bamFiles.get(x).getName(), pe_pane);
				//Add duplication stats to tabbed pane
				DUP_STATS.setCaretPosition(0);
				JScrollPane dup_pane = new JScrollPane(DUP_STATS, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				tabbedPane_DupStats.add(bamFiles.get(x).getName(), dup_pane);
						
				tabbedPane_Histogram.add(bamFiles.get(x).getName(), Histogram.createBarChart(HIST, DOMAIN, OUTPNG));
				tabbedPane_Duplication.add(bamFiles.get(x).getName(), LineChart.createLineChart(BIN, BIN_NAME));
				
		        firePropertyChange("bam",x, x + 1);
		        
			} else {
				if(OUT != null) OUT.println("BAI Index File does not exist for: " + bamFiles.get(x).getName() + "\n");
				PE_STATS.append("BAI Index File does not exist for: " + bamFiles.get(x).getName() + "\n\n");
				DUP_STATS.append("BAI Index File does not exist for: " + bamFiles.get(x).getName() + "\n\n");
			}
			if(OUT != null) OUT.close();
		}		
	}
	
	public static double getMedian(double[] histogram) {
		double sum = 0;
		for(int x = 0; x < histogram.length; x++) { sum += histogram[x]; }
		if(sum % 2 == 1 && sum > 0) {
			int num = (int) ((sum + 1) / 2);
			double count = 0;
			for(int x = 0; x < histogram.length; x++) {
				count += histogram[x];
				if(count >= num) return (x + MIN_INSERT);
			}
		} else if(sum > 0) {
			double first = -999;
			double second = -999;
			int num = (int) (sum / 2);
			double count = 0;
			for(int x = 0; x < histogram.length; x++) {
				count += histogram[x];
				if(count >= num & first == -999) first = (x + MIN_INSERT);
				if(count >= num + 1) second = (x + MIN_INSERT);
				if(first != -999 && second != -999) { return (first + second) / 2; }
			}
		}		
		return 0;
	}
	
	public static double getStdDev(double[] histogram, double avg) {
		double stddev = 0;
		double sum = 0;
		for(int x = 0; x < histogram.length; x++) {
			stddev += (Math.pow(((x + MIN_INSERT) - avg), 2) * histogram[x]);
			sum += histogram[x];
		}
		if(sum > 0) return Math.sqrt(stddev / sum);
		else return 0;
	}
	
	public static int getBinIndex(int COUNT) {
		if(COUNT == 1) return 0;
        else if(COUNT >= 2 && COUNT <= 10) return 1;
        else if(COUNT >= 11 && COUNT <= 25) return 2;
        else if(COUNT >= 26 && COUNT <= 50) return 3;
        else if(COUNT >= 51 && COUNT <= 75) return 4;
        else if(COUNT >= 76 && COUNT <= 100) return 5;
        else if(COUNT >= 101 && COUNT <= 125) return 6;
        else if(COUNT >= 126 && COUNT <= 150) return 7;
        else if(COUNT >= 151 && COUNT <= 250) return 8;
        else if(COUNT >= 251 && COUNT <= 500) return 9;
        else if(COUNT >= 501 && COUNT <= 1000) return 10;
        else if(COUNT >= 1001 && COUNT <= 5000) return 11;
        else if(COUNT >= 5001 && COUNT <= 10000) return 12;
        else if(COUNT >= 10001) return 13;
		
		return -999;
	}
	
	public static void initializeBINS(ArrayList<Double> BIN) {
		BIN.add(new Double(0)); // Bin 1
		BIN.add(new Double(0)); // Bin 2-10
		BIN.add(new Double(0)); // Bin 11-25
		BIN.add(new Double(0)); // Bin 26-50
		BIN.add(new Double(0)); // Bin 51-75
		BIN.add(new Double(0)); // Bin 76-100
		BIN.add(new Double(0)); // Bin 101-125
		BIN.add(new Double(0)); // Bin 126-150
		BIN.add(new Double(0)); // Bin 151-250
		BIN.add(new Double(0)); // Bin 251-500
		BIN.add(new Double(0)); // Bin 501-1,000
		BIN.add(new Double(0)); // Bin 1,001-5,000
		BIN.add(new Double(0)); // Bin 5,001-10,000
		BIN.add(new Double(0)); // Bin 10,000+
	}
	
	public static String[] initializeBIN_Names() {
		String[] NAME = new String[14];
		NAME[0] = "1";
		NAME[1] = "2-10";
		NAME[2] = "11-25";
		NAME[3] = "26-50";
		NAME[4] = "51-75";
		NAME[5] = "76-100";
		NAME[6] = "101-125";
		NAME[7] = "126-150";
		NAME[8] = "151-250";
		NAME[9] = "251-500";
		NAME[10] = "501-1,000";
		NAME[11] = "1,001-5,000";
		NAME[12] = "5,001-10,000";
		NAME[13] = "10,000+";
		return NAME;
	}
	
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}
