package scripts.BAM_Format_Converter;

import htsjdk.samtools.AbstractBAMFileIndex;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.util.CloseableIterator;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class BAMtoGFF extends JFrame {
	private File BAM = null;
	private File OUTPUTPATH = null;
	private int STRAND = 0;
	private String READ = "READ1";
	
	private static int PAIR = 1;
	private static int MIN_INSERT = -9999;
	private static int MAX_INSERT = -9999;
	
	private SamReader inputSam = null;
	private PrintStream OUT = null;

	private JTextArea textArea;
	
	private int CHROMSTOP = -999;
	
	public BAMtoGFF(File b, File o, int s, int pair_status,  int min_size, int max_size) {
		setTitle("BAM to GFF Progress");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		BAM = b;
		OUTPUTPATH = o;
		STRAND = s;
		PAIR = pair_status;
		
		MIN_INSERT = min_size;
		MAX_INSERT = max_size;
		
		if(STRAND == 0) { READ = "READ1"; }
		else if(STRAND == 1) { READ = "READ2"; }
		else if(STRAND == 2) { READ = "COMBINED"; }
		else if(STRAND == 3) { READ = "MIDPOINT"; }
		else if(STRAND == 4) { READ = "FRAGMENT"; }

	}
	
	public void run() throws IOException, InterruptedException {
		System.out.println(getTimeStamp());
		
		//Open Output File
		String NAME = BAM.getName().split("\\.")[0] + "_" + READ + ".gff";
		if(OUTPUTPATH != null) {
			try { OUT = new PrintStream(new File(OUTPUTPATH.getCanonicalPath() + File.separator + NAME)); }
			catch (FileNotFoundException e) { e.printStackTrace(); }
			catch (IOException e) {	e.printStackTrace(); }
		} else {
			try { OUT = new PrintStream(new File(NAME)); }
			catch (FileNotFoundException e) { e.printStackTrace(); }
		}
				
		textArea.append(NAME + "\n");
		textArea.append(getTimeStamp() + "\n");
		
		//Check to Make Sure BAI-index file exists
		File f = new File(BAM.getAbsolutePath() + ".bai");
		if(f.exists() && !f.isDirectory()) {
			textArea.append("-----------------------------------------\nBAM to GFF Parameters:\n");
			textArea.append("BAM file: " + BAM + "\n");
			textArea.append("Output: " + NAME + "\n");
			
			textArea.append("Output Read: " + READ + "\n");
			textArea.append("Require proper Mate-pair: ");
			if(PAIR == 0) { textArea.append("no" + "\n"); }
			else { textArea.append("yes" + "\n"); }

			textArea.append("Minimum insert size required to output: ");
			if(MIN_INSERT == -9999) { textArea.append("NaN\n"); }
			else { textArea.append(MIN_INSERT + "\n"); }
			textArea.append("Maximum insert size required to output: ");
			if(MAX_INSERT == -9999) { textArea.append("NaN\n"); }
			else { textArea.append(MAX_INSERT + "\n"); }
			
			//Print Header
			OUT.print("#" + getTimeStamp() + ";" + BAM.getName() + ";" + READ);
			if(PAIR != 0) { OUT.print(";PE_Required"); }
			if(MIN_INSERT != -9999) { OUT.print(";Min_Insert-" + MIN_INSERT); }
			if(MAX_INSERT != -9999) { OUT.print(";Max_Insert-" + MAX_INSERT); }
			OUT.println();
			
			//Begin processing reads in BAM file
			processREADS();
			
			OUT.close();
		} else {
			textArea.append("BAI Index File does not exist for: " + BAM.getName() + "\n");
			OUT.println("BAI Index File does not exist for: " + BAM.getName() + "\n");
		}
		Thread.sleep(2000);
		dispose();
		
		System.out.println(getTimeStamp());
	}
	
	public void outputRead(SAMRecord read) {
		//chr22  TeleGene enhancer  10000000  10001000  500 +  .  touch1
		//chr22  TeleGene promoter  10010000  10010100  900 +  .  touch1
		int recordStart = read.getUnclippedStart();
		int recordStop = read.getUnclippedEnd();
		String chrom = read.getReferenceName();
		String dir = "+";
		if(read.getReadNegativeStrandFlag()) dir = "-";
		
		if(STRAND <= 2) { 
			if(recordStart > 0 && recordStop <= CHROMSTOP) { //Make sure we only output real reads
				OUT.println(chrom + "\tbam2gff\t" + read.getReadName() + "\t" + recordStart + "\t" + recordStop + "\t" + read.getReadLength() + "\t" + dir + "\t.\t" + read.getReadName());
			}
		} else if(STRAND == 3) { 
			recordStop = read.getMateAlignmentStart() + read.getReadLength() - 1;
			if(read.getMateAlignmentStart() - 1 < recordStart) {
				recordStart = read.getMateAlignmentStart() - 1;
				recordStop = read.getUnclippedEnd();
			}
			int midStart = (recordStart + recordStop) / 2;
			int midStop = midStart + 1;
			
			if(midStart > 0 && midStop <= CHROMSTOP) { //Make sure we only output real reads
				int size = Math.abs(read.getInferredInsertSize());
				OUT.println(chrom + "\tbam2gff\t" + read.getReadName() + "\t" + midStart + "\t" + midStop + "\t" + size + "\t" + dir + "\t.\t" + read.getReadName());
			}
		} else if(STRAND == 4) { 
			if(read.getReadNegativeStrandFlag()) {
				recordStart = read.getMateAlignmentStart();
				recordStop = read.getAlignmentEnd();
			} else {
				recordStop = read.getMateAlignmentStart() + read.getReadLength();
			}
			if(recordStart > 0 && recordStop <= CHROMSTOP) { //Make sure we only output real reads
				int size = Math.abs(read.getInferredInsertSize());
				OUT.println(chrom + "\tbam2gff\t" + read.getReadName() + "\t" + recordStart + "\t" + recordStop + "\t" + size + "\t" + dir + "\t.\t" + read.getReadName());
			}
		}
	}
			
	public void processREADS() {
		inputSam = SamReaderFactory.makeDefault().open(BAM);//factory.open(BAM);
		AbstractBAMFileIndex bai = (AbstractBAMFileIndex) inputSam.indexing().getIndex();
					
		for(int numchrom = 0; numchrom < bai.getNumberOfReferences(); numchrom++) {
			SAMSequenceRecord seq = inputSam.getFileHeader().getSequence(numchrom);
			System.out.println("Processing: " + seq.getSequenceName());
			textArea.append("Processing: " + seq.getSequenceName() + "\n");

			CHROMSTOP = seq.getSequenceLength();
			
			CloseableIterator<SAMRecord> iter = inputSam.query(seq.getSequenceName(), 0, seq.getSequenceLength(), false);
			while (iter.hasNext()) {
				//Create the record object 
				SAMRecord sr = iter.next();
				
				if(STRAND == 3 || STRAND == 4) { //Output Midpoint or fragment
					//Must be PAIRED-END mapped, mate must be mapped, must be read1
					if(sr.getReadPairedFlag()) {
						if(sr.getProperPairFlag() && sr.getFirstOfPairFlag()) {
							boolean flag1 = (Math.abs(sr.getInferredInsertSize()) >= MIN_INSERT && MIN_INSERT != -9999) || MIN_INSERT == -9999; //check if insert size >= min if in use
							boolean flag2 = (Math.abs(sr.getInferredInsertSize()) <= MAX_INSERT && MAX_INSERT != -9999) || MAX_INSERT == -9999; //check if insert size <= max if in use
							if(flag1 && flag2) { outputRead(sr); }
						}
					}
				} else if(STRAND == 2) { //Output combined READ 1 && READ 2
					if(PAIR == 0) { outputRead(sr); } //Output read if proper mate-pairing is NOT required
					else if(sr.getReadPairedFlag()) { //otherwise, check for PE flag
						if(sr.getProperPairFlag()) { outputRead(sr); } //output read if proper mate-pair is detected
					}
				} else if(STRAND == 0) { //Output READ 1
					if(sr.getReadPairedFlag()) { //Check if PAIRED-END
						if(((sr.getProperPairFlag() && PAIR == 1) || PAIR == 0) && sr.getFirstOfPairFlag()) { //mate must be mapped if PAIR requirement, must be read1
							boolean flag1 = (Math.abs(sr.getInferredInsertSize()) >= MIN_INSERT && MIN_INSERT != -9999) || MIN_INSERT == -9999; //check if insert size >= min if in use
							boolean flag2 = (Math.abs(sr.getInferredInsertSize()) <= MAX_INSERT && MAX_INSERT != -9999) || MAX_INSERT == -9999; //check if insert size <= max if in use
							if(flag1 && flag2) { outputRead(sr); } //add tag if both flags true
						}
					} else if(PAIR == 0) { outputRead(sr); } //Output if not paired-end, by default it is Read1, and mate-pair not required
				} else if(STRAND == 1) { //Output READ 2
					if(sr.getReadPairedFlag()) { ////Must be PAIRED-END for valid Read 2
						if(((sr.getProperPairFlag() && PAIR == 1) || PAIR == 0) && !sr.getFirstOfPairFlag()) { //mate must be mapped if PAIR requirement, must be read2
							boolean flag1 = (Math.abs(sr.getInferredInsertSize()) >= MIN_INSERT && MIN_INSERT != -9999) || MIN_INSERT == -9999; //check if insert size >= min if in use
							boolean flag2 = (Math.abs(sr.getInferredInsertSize()) <= MAX_INSERT && MAX_INSERT != -9999) || MAX_INSERT == -9999; //check if insert size <= max if in use
							if(flag1 && flag2) { outputRead(sr); } //add tag if both flags true
						}
					}
				}
			}
			iter.close();
		}
		bai.close();
	}
		
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}