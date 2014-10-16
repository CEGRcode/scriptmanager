package scripts;

import htsjdk.samtools.AbstractBAMFileIndex;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;

import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class SEStats extends JFrame {
	Vector<File> bamFiles = null;
	File output = null;
	//SAMFileReader reader;
	
	final SamReaderFactory factory = SamReaderFactory.makeDefault().enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS, SamReaderFactory.Option.VALIDATE_CRC_CHECKSUMS).validationStringency(ValidationStringency.SILENT);
	SamReader reader;
	PrintStream OUT = null;
	
	private JTextArea textArea;
	
	public SEStats(Vector<File> input, File o) {
		setTitle("BAM File Statistics");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);

		bamFiles = input;
		output = o;
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
	}
	
	public void run() {
		
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
		textArea.append(time + "\n");
		
		for(int x = 0; x < bamFiles.size(); x++) {
						//Check if BAI index file exists
			File f = new File(bamFiles.get(x) + ".bai");
			if(f.exists() && !f.isDirectory()) {
				if(OUT != null) OUT.println(bamFiles.get(x).getName());
				if(OUT != null) OUT.println("Chromosome_ID\tChromosome_Size\tAligned_Reads");
				textArea.append(bamFiles.get(x).getName() + "\n");
				textArea.append("Chromosome_ID\tChromosome_Size\tAligned_Reads\n");
				
				//Code to get individual chromosome stats
				//reader = new SamReader(bamFiles.get(x), new File(bamFiles.get(x) + ".bai"));
				reader = factory.open(bamFiles.get(x));
				
				AbstractBAMFileIndex bai = (AbstractBAMFileIndex) reader.indexing().getIndex();
				double totalTags = 0;
				double totalGenome = 0;
			
				for (int z = 0; z < bai.getNumberOfReferences(); z++) {
					SAMSequenceRecord seq = reader.getFileHeader().getSequence(z);
					double aligned = reader.indexing().getIndex().getMetaData(z).getAlignedRecordCount();
					//int unaligned = reader.getIndex().getMetaData(z).getUnalignedRecordCount();
					if(OUT != null) OUT.println(seq.getSequenceName() + "\t" + seq.getSequenceLength() + "\t" + aligned);
					textArea.append(seq.getSequenceName() + "\t" + seq.getSequenceLength() + "\t" + aligned + "\n");
					totalTags += aligned;
					totalGenome += seq.getSequenceLength();
				}

				if(OUT != null) OUT.println("Total Genome Size: " + totalGenome + "\tTotal Aligned Tags: " + totalTags + "\n");
				textArea.append("Total Genome Size: " + totalGenome + "\tTotal Aligned Tags: " + totalTags + "\n\n");
				
				//Output replicates used to make bam file
				for( String comment : reader.getFileHeader().getComments()) {
					if(OUT != null) OUT.println(comment);
					textArea.append(comment + "\n");
				}
				
				//Output program used to align bam file
				for (int z = 0; z < reader.getFileHeader().getProgramRecords().size(); z++) {
					if(OUT != null) {
						OUT.print(reader.getFileHeader().getProgramRecords().get(z).getId() + "\t");
						OUT.println(reader.getFileHeader().getProgramRecords().get(z).getProgramVersion());
						OUT.println(reader.getFileHeader().getProgramRecords().get(z).getCommandLine());
					}
					textArea.append(reader.getFileHeader().getProgramRecords().get(z).getId() + "\t");
					textArea.append(reader.getFileHeader().getProgramRecords().get(z).getProgramVersion() + "\n");
					textArea.append(reader.getFileHeader().getProgramRecords().get(z).getCommandLine() + "\n");
				}
				
				if(OUT != null) OUT.println();
				textArea.append("\n");
				
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bai.close();
				
			} else {
				if(OUT != null) OUT.println("BAI Index File does not exist for: " + bamFiles.get(x).getName() + "\n");
				textArea.append("BAI Index File does not exist for: " + bamFiles.get(x).getName() + "\n\n");	
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
