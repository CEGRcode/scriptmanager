package scripts;

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
public class BAMtoBED extends JFrame {
	private File BAM = null;
	private File OUTPUTPATH = null;
	private int STRAND = 0;
	private String READ = "READ1";
	
	private SamReader inputSam = null;
	//final SamReaderFactory factory = SamReaderFactory.makeDefault().enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS, SamReaderFactory.Option.CACHE_FILE_BASED_INDEXES, SamReaderFactory.Option.VALIDATE_CRC_CHECKSUMS).validationStringency(ValidationStringency.LENIENT);

	private PrintStream OUT = null;
	
	private JTextArea textArea;
	
	private int CHROMSTOP = 0;
	
	public BAMtoBED(File b, File o, int s) {
		setTitle("BAM to TAB Progress");
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
		if(STRAND == 0) { READ = "READ1"; }
		else if(STRAND == 1) { READ = "READ2"; }
		else if(STRAND == 2) { READ = "COMBINED"; }
	}
	
	public void run() throws IOException, InterruptedException {
		//Open Output File
		String NAME = BAM.getName().split("\\.")[0] + "_" + READ + ".bed";
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
			//Print Header
			OUT.println("#" + getTimeStamp() + ";" + BAM.getName() + ";" + READ);
			
			if(STRAND == 0) { READ1(); }
			else if(STRAND == 1) { READ2(); }
			else if(STRAND == 2) { COMBINED(); }
			OUT.close();
		} else {
			textArea.append("BAI Index File does not exist for: " + BAM.getName() + "\n");
			OUT.println("BAI Index File does not exist for: " + BAM.getName() + "\n");
			Thread.sleep(2000);
		}
		dispose();	
	}
	
	public void outputRead(SAMRecord read) {
		//chr7   118970079   118970129   TUPAC_0001:3:1:0:1452#0/1   37   -
		//chr7   118965072   118965122   TUPAC_0001:3:1:0:1452#0/2   37   +
		int recordStart = read.getUnclippedStart() - 1;
		int recordStop = read.getUnclippedEnd();
		String chrom = read.getReferenceName();
		String dir = "+";
		if(read.getReadNegativeStrandFlag()) dir = "-";
		//Make sure we only output real reads
		if(recordStart > 0 && recordStop <= CHROMSTOP) { OUT.println(chrom + "\t" + recordStart + "\t" + recordStop + "\t" + read.getReadName() + "\t" + read.getReadLength() + "\t" + dir); }
	}
	
	public void READ1() {
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
				
				//Must be PAIRED-END mapped, mate must be mapped, must be read1
				if(sr.getReadPairedFlag()) {
					if(sr.getProperPairFlag() && sr.getFirstOfPairFlag()) {
						outputRead(sr);
					}
				} else {
					//Also outputs if not paired-end since by default it is read-1
					outputRead(sr);
				}		
			}
			iter.close();
		}

		bai.close();
	}
	
	public void READ2() {
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
				
				//Must be PAIRED-END mapped, mate must be mapped, must be read2
				if(sr.getReadPairedFlag()) {
					if(sr.getProperPairFlag() && !sr.getFirstOfPairFlag()) {
						outputRead(sr);
					}
				}
			}
			iter.close();
		}
		bai.close();
	}
	
	public void COMBINED() {
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
				
				//No filter required here
				outputRead(sr);
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