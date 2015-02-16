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
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class BAMtoTAB extends JFrame {
	private File BAM = null;
	private File OUTPUTPATH = null;
	private int STRAND = 0;
	private String READ = "READ1";
	
	private SamReader inputSam = null;
	//final SamReaderFactory factory = SamReaderFactory.makeDefault().enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS, SamReaderFactory.Option.CACHE_FILE_BASED_INDEXES, SamReaderFactory.Option.VALIDATE_CRC_CHECKSUMS).validationStringency(ValidationStringency.LENIENT);
	private PrintStream OUT = null;
	
	private ArrayList<Integer> BP;
	private ArrayList<Integer> F_OCC;
	private ArrayList<Integer> R_OCC;
	
	private JTextArea textArea;
	
	private int CHROMSTOP = -999;
	
	public BAMtoTAB(File b, File o, int s) {
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
		System.out.println(getTimeStamp());
		
		//Open Output File
		String NAME = BAM.getName().split("\\.")[0] + "_" + READ + ".tab";
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
			OUT.println("chrom\tindex\tforward\treverse\tvalue");
			
			if(STRAND == 0) { READ1(); }
			else if(STRAND == 1) { READ2(); }
			else if(STRAND == 2) { COMBINED(); }
			OUT.close();
		} else {
			textArea.append("BAI Index File does not exist for: " + BAM.getName() + "\n");
			OUT.println("BAI Index File does not exist for: " + BAM.getName() + "\n");
		}
		Thread.sleep(2000);
		dispose();
		
		System.out.println(getTimeStamp());
	}
	
	public void addTag(SAMRecord sr) {
		//Get the start of the record 
		int recordStart = sr.getUnclippedStart();//.getAlignmentStart();
		//Accounts for reverse tag reporting 3' end of tag and converting BED to IDX/GFF format
		if(sr.getReadNegativeStrandFlag()) { recordStart = sr.getUnclippedEnd(); }//.getAlignmentEnd(); }					

		//Make sure we only add tags that have valid starts
		if(recordStart > 0 && recordStart <= CHROMSTOP) {
			if(BP.contains(new Integer(recordStart))) {
				int index = BP.indexOf(new Integer(recordStart));
				if(sr.getReadNegativeStrandFlag()) {
					R_OCC.set(index, new Integer(R_OCC.get(index).intValue() + 1));
				} else {
					F_OCC.set(index, new Integer(F_OCC.get(index).intValue() + 1));
				}
			} else {
				//Sometimes the start coordinate will be out of order due to (-) strand correction
				//Need to efficiently identify where to place it relative to the other bps
				int index = BP.size() - 1;
				if(index >= 0) {
					while(index >= 0 && recordStart < BP.get(index).intValue()) {
						index--;
					}
				}
				if(index < BP.size() - 1) {
					BP.add(index + 1, new Integer(recordStart));
					if(sr.getReadNegativeStrandFlag()) {
						R_OCC.add(index + 1, new Integer(1));
						F_OCC.add(index + 1, new Integer(0));
					} else {
						F_OCC.add(index + 1, new Integer(1));
						R_OCC.add(index + 1, new Integer(0));
					}
				} else {
					BP.add(new Integer(recordStart));
					if(sr.getReadNegativeStrandFlag()) {
						R_OCC.add(new Integer(1));
						F_OCC.add(new Integer(0));
					} else {
						F_OCC.add(new Integer(1));
						R_OCC.add(new Integer(0));
					}
				}
			}
		}
	}
	
	public void dumpExcess(String chrom) {
		int trim = 9000;
		while(trim > 0) {
			int sum = F_OCC.get(0).intValue() + R_OCC.get(0).intValue();
			OUT.println(chrom + "\t" + BP.get(0).intValue() + "\t" + F_OCC.get(0).intValue() + "\t" + R_OCC.get(0).intValue() + "\t" + sum);
			BP.remove(0);
			F_OCC.remove(0);
			R_OCC.remove(0);
			trim--;
		}
	}
	
	public void READ1() {
		inputSam = SamReaderFactory.makeDefault().open(BAM);//factory.open(BAM);
		AbstractBAMFileIndex bai = (AbstractBAMFileIndex) inputSam.indexing().getIndex();
					
		for(int numchrom = 0; numchrom < bai.getNumberOfReferences(); numchrom++) {
			SAMSequenceRecord seq = inputSam.getFileHeader().getSequence(numchrom);
			System.out.println("Processing: " + seq.getSequenceName());
			textArea.append("Processing: " + seq.getSequenceName() + "\n");

			CHROMSTOP = seq.getSequenceLength();
			BP = new ArrayList<Integer>();
			F_OCC = new ArrayList<Integer>();
			R_OCC = new ArrayList<Integer>();
			
			CloseableIterator<SAMRecord> iter = inputSam.query(seq.getSequenceName(), 0, seq.getSequenceLength(), false);
			while (iter.hasNext()) {
				//Create the record object 
				SAMRecord sr = iter.next();
				
				//Must be PAIRED-END mapped, mate must be mapped, must be read1
				if(sr.getReadPairedFlag()) {
					if(sr.getProperPairFlag() && sr.getFirstOfPairFlag()) {
						addTag(sr);
					}
				} else {
					//Also outputs if not paired-end since by default it is read-1
					addTag(sr);
				}
				
				//Dump ArrayLists to OUT if they get too big in order to save RAM and therefore time
				if(BP.size() > 10000) {
					dumpExcess(seq.getSequenceName());
				}
				
			}
			iter.close();
			for(int z = 0; z < BP.size(); z++) {
				int sum = F_OCC.get(z).intValue() + R_OCC.get(z).intValue();
				OUT.println(seq.getSequenceName() + "\t" + BP.get(z).intValue() + "\t" + F_OCC.get(z).intValue() + "\t" + R_OCC.get(z).intValue() + "\t" + sum);		
			}
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
			BP = new ArrayList<Integer>();
			F_OCC = new ArrayList<Integer>();
			R_OCC = new ArrayList<Integer>();
			
			CloseableIterator<SAMRecord> iter = inputSam.query(seq.getSequenceName(), 0, seq.getSequenceLength(), false);
			while (iter.hasNext()) {
				//Create the record object 
				SAMRecord sr = iter.next();
				
				//Must be PAIRED-END mapped, mate must be mapped, must be read2
				if(sr.getReadPairedFlag()) {
					if(sr.getProperPairFlag() && !sr.getFirstOfPairFlag()) {
						addTag(sr);
					}
				}
				
				//Dump ArrayLists to OUT if they get too big in order to save RAM and therefore time
				if(BP.size() > 10000) {
					dumpExcess(seq.getSequenceName());
				}
			}
			iter.close();
			for(int x = 0; x < BP.size(); x++) {
				int sum = F_OCC.get(x).intValue() + R_OCC.get(x).intValue();
				OUT.println(seq.getSequenceName() + "\t" + BP.get(x).intValue() + "\t" + F_OCC.get(x).intValue() + "\t" + R_OCC.get(x).intValue() + "\t" + sum);		
			}
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
			BP = new ArrayList<Integer>();
			F_OCC = new ArrayList<Integer>();
			R_OCC = new ArrayList<Integer>();
			
			CloseableIterator<SAMRecord> iter = inputSam.query(seq.getSequenceName(), 0, seq.getSequenceLength(), false);
			while (iter.hasNext()) {
				//Create the record object 
				SAMRecord sr = iter.next();
				
				//No filter required here
				addTag(sr);
								
				//Dump ArrayLists to OUT if they get too big in order to save RAM and therefore time
				if(BP.size() > 10000) {
					dumpExcess(seq.getSequenceName());
				}
			}
			iter.close();
			for(int x = 0; x < BP.size(); x++) {
				int sum = F_OCC.get(x).intValue() + R_OCC.get(x).intValue();
				OUT.println(seq.getSequenceName() + "\t" + BP.get(x).intValue() + "\t" + F_OCC.get(x).intValue() + "\t" + R_OCC.get(x).intValue() + "\t" + sum);		
			}
		}
		bai.close();
	}
	
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}