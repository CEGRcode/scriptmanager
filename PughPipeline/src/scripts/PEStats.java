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
	File output = null;
	private int MIN_INSERT = 0;
	private int MAX_INSERT = 1000;
	
	SamReader reader;
	final SamReaderFactory factory = SamReaderFactory.makeDefault().enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS, SamReaderFactory.Option.VALIDATE_CRC_CHECKSUMS).validationStringency(ValidationStringency.SILENT);

	PrintStream OUT = null;
	
	final JLayeredPane layeredPane;
	final JTabbedPane tabbedPane;
	final JTabbedPane tabbedPane_Histogram;
	final JTabbedPane tabbedPane_InsertStats;
	final JTabbedPane tabbedPane_Duplication;
	final JTabbedPane tabbedPane_DupStats;
	
	public PEStats(Vector<File> input, File o, int min, int max) {
		setTitle("BAM File Statistics");
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
		output = o;
		MIN_INSERT = min;
		MAX_INSERT = max;
	}
	
	public void run() throws IOException {
		
		if(output != null) {
			try {
				OUT = new PrintStream(output);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		//Print TimeStamp
		String time = getTimeStamp();
		if(OUT != null) OUT.println(time);
		
		for(int x = 0; x < bamFiles.size(); x++) {
			JTextArea PE_STATS = new JTextArea();
			PE_STATS.setEditable(false);
			PE_STATS.append(time + "\n");
			JTextArea DUP_STATS = new JTextArea();
			DUP_STATS.setEditable(false);
			DUP_STATS.append(time + "\n");
			
			//Check if BAI index file exists
			File f = new File(bamFiles.get(x) + ".bai");
			if(f.exists() && !f.isDirectory()) {
				if(OUT != null) OUT.println(bamFiles.get(x).getName());
				if(OUT != null) OUT.println("Chromosome_ID\tChromosome_Size\tAligned_Reads");
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
				double totalTags = 0;
				double totalGenome = 0;
			
				for (int z = 0; z < bai.getNumberOfReferences(); z++) {
					SAMSequenceRecord seq = reader.getFileHeader().getSequence(z);
					double aligned = reader.indexing().getIndex().getMetaData(z).getAlignedRecordCount();

					//Basic statistic calculations
					if(OUT != null) OUT.println(seq.getSequenceName() + "\t" + seq.getSequenceLength() + "\t" + aligned);
					PE_STATS.append(seq.getSequenceName() + "\t" + seq.getSequenceLength() + "\t" + aligned + "\n");
					totalTags += aligned;
					totalGenome += seq.getSequenceLength();
					
					//Loop through each chromosome looking at each perfect F-R PE read
					CHROM_COMPLEXITY = new HashMap<String, Integer>();
					CloseableIterator<SAMRecord> iter = reader.query(seq.getSequenceName(), 0, seq.getSequenceLength(), false);
					while (iter.hasNext()) {
						//Create the record object 
						SAMRecord sr = iter.next();
										
						if(sr.getReadPairedFlag()) {
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
				
				if(OUT != null) OUT.println("Total Genome Size: " + totalGenome + "\tTotal Aligned Tags: " + totalTags + "\n");
				PE_STATS.append("Total Genome Size: " + totalGenome + "\tTotal Aligned Tags: " + totalTags + "\n\n");
				
				//Output replicates used to make bam file
				for( String comment : reader.getFileHeader().getComments()) {
					if(OUT != null) OUT.println(comment);
					PE_STATS.append(comment + "\n");
				}
				
				//Output program used to align bam file
				for (int z = 0; z < reader.getFileHeader().getProgramRecords().size(); z++) {
					if(OUT != null) {
						OUT.print(reader.getFileHeader().getProgramRecords().get(z).getId() + "\t");
						OUT.println(reader.getFileHeader().getProgramRecords().get(z).getProgramVersion());
						OUT.println(reader.getFileHeader().getProgramRecords().get(z).getCommandLine());
					}
					PE_STATS.append(reader.getFileHeader().getProgramRecords().get(z).getId() + "\t");
					PE_STATS.append(reader.getFileHeader().getProgramRecords().get(z).getProgramVersion() + "\n");
					PE_STATS.append(reader.getFileHeader().getProgramRecords().get(z).getCommandLine() + "\n");
				}
				reader.close();
				bai.close();
				
				if(OUT != null) OUT.println();
				PE_STATS.append("\n");
				
				//Insert Size statistics
				if(counter != 0) InsertAverage /= counter;
				if(OUT != null) OUT.println("Average Insert Size: " + InsertAverage + "\nNumber of ReadPairs: " + counter + "\n\nHistogram\nSize (bp)\tFrequency");
				PE_STATS.append("Average Insert Size: " + InsertAverage + "\nNumber of ReadPairs: " + counter + "\n\nHistogram\nSize (bp)\tFrequency\n");
				int[] DOMAIN = new int[(MAX_INSERT - MIN_INSERT) + 1];
				for(int z = 0; z < HIST.length; z++) {
					int bp = MIN_INSERT + z;
					DOMAIN[z] = bp;
					if(OUT != null) OUT.println(bp + "\t" + HIST[z]);
					PE_STATS.append(bp + "\t" + HIST[z] + "\n");
				}
				
				//Duplication statistics
				double UNIQUE_MOLECULES = 0;
				ArrayList<Double> BIN = new ArrayList<Double>();
				//Initialize BINs for 1,2,3,4,5-9,10-99
				BIN.add(new Double(0)); // Bin 1
				BIN.add(new Double(0)); // Bin 2
				BIN.add(new Double(0)); // Bin 3
				BIN.add(new Double(0)); // Bin 4
				BIN.add(new Double(0)); // Bin 5-9
				BIN.add(new Double(0)); // Bin 10-99		
				
				Iterator<Integer> keys = ALL_COMPLEXITY.keySet().iterator();
				while(keys.hasNext()) {
			         Integer str = (Integer) keys.next();
			         if(str.intValue() == 1) BIN.set(0, ALL_COMPLEXITY.get(str).doubleValue());
			         else if(str.intValue() == 2) BIN.set(1, ALL_COMPLEXITY.get(str).doubleValue() * 2);
			         else if(str.intValue() == 3) BIN.set(2, ALL_COMPLEXITY.get(str).doubleValue() * 3);
			         else if(str.intValue() == 4) BIN.set(3, ALL_COMPLEXITY.get(str).doubleValue() * 4);
			         else if(str.intValue() >= 5 && str.intValue() < 10) BIN.set(4, BIN.get(4) + ALL_COMPLEXITY.get(str).doubleValue() * str.doubleValue());
			         else if(str.intValue() >= 10) {
			        	 int index = (int) ((Math.log10(str.intValue())) + 4);
			        	 //Add new bins by order of magnitudes until array size matches current duplication level
			        	 while(BIN.size() <= index) {
			        		 BIN.add(new Double(0));
			        	 }
			        	 BIN.set(index, new Double((ALL_COMPLEXITY.get(str).doubleValue() * str.doubleValue()) + BIN.get(index)));
			         }
			         UNIQUE_MOLECULES += ALL_COMPLEXITY.get(str).doubleValue(); 
				}
				
				String[] BIN_NAME = new String[BIN.size()];
				for(int z = 0; z < BIN.size(); z++) {
					if(z < 4) { BIN_NAME[z] = new Integer(z + 1).toString(); }
					else if(z == 4) { BIN_NAME[z] = "5-9"; }
					else if(z >= 5) {
						int start = (int) Math.pow(10, z - 4);
						int stop = (int) Math.pow(10, z - 3) - 1;
						BIN_NAME[z] = start + "-" + stop;
					}
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
				
				tabbedPane_Histogram.add(bamFiles.get(x).getName(), Histogram.createBarChart(HIST, DOMAIN));
				tabbedPane_Duplication.add(bamFiles.get(x).getName(), LineChart.createLineChart(BIN, BIN_NAME));
				
		        firePropertyChange("bam",x, x + 1);

			} else {
				if(OUT != null) OUT.println("BAI Index File does not exist for: " + bamFiles.get(x).getName() + "\n");
				PE_STATS.append("BAI Index File does not exist for: " + bamFiles.get(x).getName() + "\n\n");
				DUP_STATS.append("BAI Index File does not exist for: " + bamFiles.get(x).getName() + "\n\n");
			}
		}
		if(OUT != null) OUT.close();
	}
	
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}
