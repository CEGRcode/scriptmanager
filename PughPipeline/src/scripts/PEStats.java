package scripts;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import net.sf.samtools.AbstractBAMFileIndex;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMSequenceRecord;
import net.sf.samtools.util.CloseableIterator;

import javax.swing.JLayeredPane;
import javax.swing.JTabbedPane;

import charts.Histogram;


@SuppressWarnings("serial")
public class PEStats extends JFrame {
	Vector<File> bamFiles = null;
	File output = null;
	private int MIN_INSERT = 0;
	private int MAX_INSERT = 1000;
	
	SAMFileReader reader;
	PrintStream OUT = null;
	
	final JLayeredPane layeredPane;
	final JTabbedPane tabbedPane;
	final JTabbedPane tabbedPane_Histogram;
	final JTabbedPane tabbedPane_Statistics;
	
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
		
		tabbedPane_Statistics = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Paired-End Statistics", null, tabbedPane_Statistics, null);

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
			JTextArea STATS = new JTextArea();
			STATS.setEditable(false);
			STATS.append(time + "\n");
			
			//Check if BAI index file exists
			File f = new File(bamFiles.get(x) + ".bai");
			if(f.exists() && !f.isDirectory()) {
				if(OUT != null) OUT.println(bamFiles.get(x).getName());
				if(OUT != null) OUT.println("Chromosome_ID\tChromosome_Size\tAligned_Reads");
				STATS.append(bamFiles.get(x).getName() + "\n");
				STATS.append("Chromosome_ID\tChromosome_Size\tAligned_Reads\n");
				
				//Code to get individual chromosome stats
				reader = new SAMFileReader(bamFiles.get(x), new File(bamFiles.get(x) + ".bai"));
				AbstractBAMFileIndex bai = (AbstractBAMFileIndex) reader.getIndex();
				double totalTags = 0;
				double totalGenome = 0;
			
				for (int z = 0; z < bai.getNumberOfReferences(); z++) {
					SAMSequenceRecord seq = reader.getFileHeader().getSequence(z);
					double aligned = reader.getIndex().getMetaData(z).getAlignedRecordCount();
					//int unaligned = reader.getIndex().getMetaData(z).getUnalignedRecordCount();
					if(OUT != null) OUT.println(seq.getSequenceName() + "\t" + seq.getSequenceLength() + "\t" + aligned);
					STATS.append(seq.getSequenceName() + "\t" + seq.getSequenceLength() + "\t" + aligned + "\n");
					totalTags += aligned;
					totalGenome += seq.getSequenceLength();
				}
				if(OUT != null) OUT.println("Total Genome Size: " + totalGenome + "\tTotal Aligned Tags: " + totalTags + "\n");
				STATS.append("Total Genome Size: " + totalGenome + "\tTotal Aligned Tags: " + totalTags + "\n\n");
				
				//Output replicates used to make bam file
				for( String comment : reader.getFileHeader().getComments()) {
					if(OUT != null) OUT.println(comment);
					STATS.append(comment + "\n");
				}
				
				//Output program used to align bam file
				for (int z = 0; z < reader.getFileHeader().getProgramRecords().size(); z++) {
					if(OUT != null) {
						OUT.print(reader.getFileHeader().getProgramRecords().get(z).getId() + "\t");
						OUT.println(reader.getFileHeader().getProgramRecords().get(z).getProgramVersion());
						OUT.println(reader.getFileHeader().getProgramRecords().get(z).getCommandLine());
					}
					STATS.append(reader.getFileHeader().getProgramRecords().get(z).getId() + "\t");
					STATS.append(reader.getFileHeader().getProgramRecords().get(z).getProgramVersion() + "\n");
					STATS.append(reader.getFileHeader().getProgramRecords().get(z).getCommandLine() + "\n");
				}
				
				if(OUT != null) OUT.println();
				STATS.append("\n");
				
				
				double average = 0;
				double counter = 0;
				double[] HIST = new double[(MAX_INSERT - MIN_INSERT) + 1];
				
				CloseableIterator<SAMRecord> iter = reader.iterator();
				while (iter.hasNext()) {
					SAMRecord sr = iter.next();
					if(sr.getReadPairedFlag()) {
						if(sr.getProperPairFlag() && sr.getFirstOfPairFlag()) {
							int distance = Math.abs(sr.getInferredInsertSize());
							if(distance <= MAX_INSERT && distance >= MIN_INSERT) HIST[distance - MIN_INSERT]++;
							average += distance;
							counter++;
						}
					}
				}
				iter.close();
				if(counter != 0) average /= counter;
				
				if(OUT != null) OUT.println("Average Insert Size: " + average + "\nNumber of ReadPairs: " + counter + "\n\nHistogram\nSize (bp)\tFrequency");
				STATS.append("Average Insert Size: " + average + "\nNumber of ReadPairs: " + counter + "\n\nHistogram\nSize (bp)\tFrequency\n");
				int[] DOMAIN = new int[(MAX_INSERT - MIN_INSERT) + 1];
				for(int z = 0; z < HIST.length; z++) {
					int bp = MIN_INSERT + z;
					DOMAIN[z] = bp;
					if(OUT != null) OUT.println(bp + "\t" + HIST[z]);
					STATS.append(bp + "\t" + HIST[z] + "\n");
				}
				reader.close();
				bai.close();
				
				STATS.setCaretPosition(0);
				JScrollPane newpane = new JScrollPane(STATS, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				tabbedPane_Statistics.add(bamFiles.get(x).getName(), newpane);
				tabbedPane_Histogram.add(bamFiles.get(x).getName(), Histogram.createBarChart(HIST, DOMAIN));
				
		        firePropertyChange("bam",x, x + 1);

			} else {
				if(OUT != null) OUT.println("BAI Index File does not exist for: " + bamFiles.get(x).getName() + "\n");
				STATS.append("BAI Index File does not exist for: " + bamFiles.get(x).getName() + "\n\n");
			}
		}
		if(OUT != null) OUT.close();
		//BAMIndexMetaData.printIndexStats(bamFiles.get(x))
	}
	
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}
