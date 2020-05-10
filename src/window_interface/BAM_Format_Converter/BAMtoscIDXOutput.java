package window_interface.BAM_Format_Converter;

// import htsjdk.samtools.AbstractBAMFileIndex;
// import htsjdk.samtools.SAMRecord;
// import htsjdk.samtools.SAMSequenceRecord;
// import htsjdk.samtools.SamReader;
// import htsjdk.samtools.SamReaderFactory;
// import htsjdk.samtools.util.CloseableIterator;

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

import objects.CustomOutputStream;
import scripts.BAM_Format_Converter.BAMtoscIDX;

@SuppressWarnings("serial")
public class BAMtoscIDXOutput extends JFrame {
	private File BAM = null;
	private File OUTPUTPATH = null;
	private int STRAND = 0;
	private String READ = "READ1";
	
	private static int PAIR = 1;
	private static int MIN_INSERT = -9999;
	private static int MAX_INSERT = -9999;
	
// 	private SamReader inputSam = null;
	private File OUT = null;
	
// 	private ArrayList<Integer> BP;
// 	private ArrayList<Integer> F_OCC;
// 	private ArrayList<Integer> R_OCC;
// 	private ArrayList<Integer> M_OCC;

	private JTextArea textArea;
	private int CHROMSTOP = -999;
	
	public BAMtoscIDXOutput(File b, File o, int s, int pair_status, int min_size, int max_size) {
		setTitle("BAM to scIDX Progress");
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
	}
	
	public void run() throws IOException, InterruptedException {
		System.out.println(getTimeStamp());
		
		//Open Output File
		String NAME = BAM.getName().split("\\.")[0] + "_" + READ + ".tab";
		if(OUTPUTPATH != null) {
			OUT = new File(OUTPUTPATH.getCanonicalPath() + File.separator + NAME);
// 			try { OUT = new File(OUTPUTPATH.getCanonicalPath() + File.separator + NAME); }
// 			catch (FileNotFoundException e) { e.printStackTrace(); }
// 			catch (IOException e) {	e.printStackTrace(); }
		} else {
			OUT = new File(NAME);
// 			try { OUT = new File(NAME); }
// 			catch (FileNotFoundException e) { e.printStackTrace(); }
		}
		
		//Call script here, pass in ps and OUT
		PrintStream PS = new PrintStream( new CustomOutputStream(textArea) );
		PS.println(NAME);
		BAMtoscIDX script_obj = new BAMtoscIDX(BAM, OUT, STRAND, PAIR, MIN_INSERT, MAX_INSERT, PS);
		script_obj.run();
		
// 		textArea.append(getTimeStamp() + "\n");
// 		
// 		//Check to Make Sure BAI-index file exists
// 		File f = new File(BAM.getAbsolutePath() + ".bai");
// 		if(f.exists() && !f.isDirectory()) {
// 			textArea.append("-----------------------------------------\nBAM to scIDX Parameters:\n");
// 			textArea.append("BAM file: " + BAM + "\n");
// 			textArea.append("Output: " + NAME + "\n");
// 			
// 			textArea.append("Require proper Mate-pair: ");
// 			if(PAIR == 0) { textArea.append("no" + "\n"); }
// 			else { textArea.append("yes" + "\n"); }
// 			
// 			textArea.append("Output Read: " + READ + "\n");
// 			textArea.append("Minimum insert size required to output: ");
// 			if(MIN_INSERT == -9999) { textArea.append("NaN\n"); }
// 			else { textArea.append(MIN_INSERT + "\n"); }
// 			textArea.append("Maximum insert size required to output: ");
// 			if(MAX_INSERT == -9999) { textArea.append("NaN\n"); }
// 			else { textArea.append(MAX_INSERT + "\n"); }
// 			
// 			//Print Header
// 			OUT.println("#" + getTimeStamp() + ";" + BAM.getName() + ";" + READ);
// 			if(STRAND <= 2) { OUT.println("chrom\tindex\tforward\treverse\tvalue"); }
// 			else { OUT.println("chrom\tindex\tmidpoint\tnull\tvalue"); }
// 
// 			//Begin processing reads in BAM file
// 			if(STRAND <= 2) { processREADS(); }
// 			else { processMIDPOINT(); }
// 			
// 			OUT.close();
// 		} else {
// 			textArea.append("BAI Index File does not exist for: " + BAM.getName() + "\n");
// 			OUT.println("BAI Index File does not exist for: " + BAM.getName() + "\n");
// 		}
		Thread.sleep(2000);
		dispose();
		
		System.out.println(getTimeStamp());
	}
		
// 	public void addTag(SAMRecord sr) {
// 		//Get the start of the record 
// 		int recordStart = sr.getUnclippedStart();//.getAlignmentStart();
// 		//Accounts for reverse tag reporting 3' end of tag and converting BED to IDX/GFF format
// 		if(sr.getReadNegativeStrandFlag()) { recordStart = sr.getUnclippedEnd(); }//.getAlignmentEnd(); }					
// 				
// 		//Make sure we only add tags that have valid starts
// 		if(recordStart > 0 && recordStart <= CHROMSTOP) {
// 			if(BP.contains(new Integer(recordStart))) {
// 				int index = BP.indexOf(new Integer(recordStart));
// 				if(sr.getReadNegativeStrandFlag()) {
// 					R_OCC.set(index, new Integer(R_OCC.get(index).intValue() + 1));
// 				} else {
// 					F_OCC.set(index, new Integer(F_OCC.get(index).intValue() + 1));
// 				}
// 			} else {
// 				//Sometimes the start coordinate will be out of order due to (-) strand correction
// 				//Need to efficiently identify where to place it relative to the other bps
// 				int index = BP.size() - 1;
// 				if(index >= 0) {
// 					while(index >= 0 && recordStart < BP.get(index).intValue()) {
// 						index--;
// 					}
// 				}
// 				if(index < BP.size() - 1) {
// 					BP.add(index + 1, new Integer(recordStart));
// 					if(sr.getReadNegativeStrandFlag()) {
// 						R_OCC.add(index + 1, new Integer(1));
// 						F_OCC.add(index + 1, new Integer(0));
// 					} else {
// 						F_OCC.add(index + 1, new Integer(1));
// 						R_OCC.add(index + 1, new Integer(0));
// 					}
// 				} else {
// 					BP.add(new Integer(recordStart));
// 					if(sr.getReadNegativeStrandFlag()) {
// 						R_OCC.add(new Integer(1));
// 						F_OCC.add(new Integer(0));
// 					} else {
// 						F_OCC.add(new Integer(1));
// 						R_OCC.add(new Integer(0));
// 					}
// 				}
// 			}
// 		}
// 	}
// 
// 	public void addMidTag(SAMRecord sr) {
// 		//int recordMid = sr.getUnclippedStart() + (sr.getInferredInsertSize() / 2);
// 		//if(sr.getReadNegativeStrandFlag()) { recordMid = sr.getUnclippedEnd() + (sr.getInferredInsertSize() / 2); }
// 
// 		int recordStart = sr.getUnclippedStart() - 1;
// 		int recordStop = sr.getMateAlignmentStart() + sr.getReadLength() - 1;
// 		if(sr.getMateAlignmentStart() - 1 < recordStart) {
// 			recordStart = sr.getMateAlignmentStart() - 1;
// 			recordStop = sr.getUnclippedEnd();
// 		}
// 		int recordMid = (recordStart + recordStop) / 2;
// 		
// 		//Make sure we only add tags that have valid midpoints
// 		if(recordMid > 0 && recordMid <= CHROMSTOP) {
// 			if(BP.contains(new Integer(recordMid))) {
// 				int index = BP.indexOf(new Integer(recordMid));
// 				M_OCC.set(index, new Integer(M_OCC.get(index).intValue() + 1));
// 			} else {
// 				//Sometimes the start coordinate will be out of order due to (-) strand correction
// 				//Need to efficiently identify where to place it relative to the other bps
// 				int index = BP.size() - 1;
// 				if(index >= 0) {
// 					while(index >= 0 && recordMid < BP.get(index).intValue()) {
// 						index--;
// 					}
// 				}
// 				if(index < BP.size() - 1) {
// 					BP.add(index + 1, new Integer(recordMid));
// 					M_OCC.add(index + 1, new Integer(1));
// 				} else {
// 					BP.add(new Integer(recordMid));
// 					M_OCC.add(new Integer(1));
// 				}
// 			}
// 		}
// 	}
// 	
// 	public void dumpExcess(String chrom) {
// 		int trim = 9000;
// 		while(trim > 0) {
// 			int sum = F_OCC.get(0).intValue() + R_OCC.get(0).intValue();
// 			OUT.println(chrom + "\t" + BP.get(0).intValue() + "\t" + F_OCC.get(0).intValue() + "\t" + R_OCC.get(0).intValue() + "\t" + sum);
// 			BP.remove(0);
// 			F_OCC.remove(0);
// 			R_OCC.remove(0);
// 			trim--;
// 		}
// 	}
// 	
// 	public void dumpMidExcess(String chrom) {
// 		int trim = (MAX_INSERT * 10) - (MAX_INSERT * 2);
// 		if(MAX_INSERT * 10 < 1000) { trim = 600; }
// 		while(trim > 0) {
// 			OUT.println(chrom + "\t" + BP.get(0).intValue() + "\t" + M_OCC.get(0).intValue() + "\t0\t" + M_OCC.get(0).intValue());
// 			//OUT.println(chrom + "\t" + BP.get(0).intValue() + "\t" + M_OCC.get(0).intValue());		
// 
// 			BP.remove(0);
// 			M_OCC.remove(0);
// 			trim--;
// 		}
// 	}
// 	
// 	public void processREADS() {
// 		inputSam = SamReaderFactory.makeDefault().open(BAM);//factory.open(BAM);
// 		AbstractBAMFileIndex bai = (AbstractBAMFileIndex) inputSam.indexing().getIndex();
// 					
// 		for(int numchrom = 0; numchrom < bai.getNumberOfReferences(); numchrom++) {
// 			SAMSequenceRecord seq = inputSam.getFileHeader().getSequence(numchrom);
// 			System.out.println("Processing: " + seq.getSequenceName());
// 			textArea.append("Processing: " + seq.getSequenceName() + "\n");
// 
// 			CHROMSTOP = seq.getSequenceLength();
// 			BP = new ArrayList<Integer>();
// 			F_OCC = new ArrayList<Integer>();
// 			R_OCC = new ArrayList<Integer>();
// 			
// 			CloseableIterator<SAMRecord> iter = inputSam.query(seq.getSequenceName(), 0, seq.getSequenceLength(), false);
// 			while (iter.hasNext()) {
// 				//Create the record object 
// 				SAMRecord sr = iter.next();
// 				
// 				if(STRAND == 2) { //Output combined READ 1 && READ 2
// 					if(PAIR == 0) { addTag(sr); } //Output read if proper mate-pairing is NOT required
// 					else if(sr.getReadPairedFlag()) { //otherwise, check for PE flag
// 						if(sr.getProperPairFlag()) { addTag(sr); } //output read if proper mate-pair is detected
// 					}
// 				} else if(STRAND == 0) { //Output READ 1
// 					if(sr.getReadPairedFlag()) { //Check if PAIRED-END
// 						if(((sr.getProperPairFlag() && PAIR == 1) || PAIR == 0) && sr.getFirstOfPairFlag()) { //mate must be mapped if PAIR requirement, must be read1
// 							boolean flag1 = (Math.abs(sr.getInferredInsertSize()) >= MIN_INSERT && MIN_INSERT != -9999) || MIN_INSERT == -9999; //check if insert size >= min if in use
// 							boolean flag2 = (Math.abs(sr.getInferredInsertSize()) <= MAX_INSERT && MAX_INSERT != -9999) || MAX_INSERT == -9999; //check if insert size <= max if in use
// 							if(flag1 && flag2) { addTag(sr); } //add tag if both flags true
// 						}
// 					} else if(PAIR == 0) { addTag(sr); } //Output if not paired-end, by default it is Read1, and mate-pair not required
// 				} else if(STRAND == 1) { //Output READ 2
// 					if(sr.getReadPairedFlag()) { ////Must be PAIRED-END for valid Read 2
// 						if(((sr.getProperPairFlag() && PAIR == 1) || PAIR == 0) && !sr.getFirstOfPairFlag()) { //mate must be mapped if PAIR requirement, must be read2
// 							boolean flag1 = (Math.abs(sr.getInferredInsertSize()) >= MIN_INSERT && MIN_INSERT != -9999) || MIN_INSERT == -9999; //check if insert size >= min if in use
// 							boolean flag2 = (Math.abs(sr.getInferredInsertSize()) <= MAX_INSERT && MAX_INSERT != -9999) || MAX_INSERT == -9999; //check if insert size <= max if in use
// 							if(flag1 && flag2) { addTag(sr); } //add tag if both flags true
// 						}
// 					}
// 				}
// 				
// 				//Dump ArrayLists to OUT if they get too big in order to save RAM and therefore time
// 				if(BP.size() > 10000) {	dumpExcess(seq.getSequenceName()); }
// 				
// 			}
// 			iter.close();
// 			for(int z = 0; z < BP.size(); z++) {
// 				int sum = F_OCC.get(z).intValue() + R_OCC.get(z).intValue();
// 				OUT.println(seq.getSequenceName() + "\t" + BP.get(z).intValue() + "\t" + F_OCC.get(z).intValue() + "\t" + R_OCC.get(z).intValue() + "\t" + sum);		
// 			}
// 		}
// 		bai.close();
// 	}
// 	
// 	public void processMIDPOINT() {
// 		inputSam = SamReaderFactory.makeDefault().open(BAM);//factory.open(BAM);
// 		AbstractBAMFileIndex bai = (AbstractBAMFileIndex) inputSam.indexing().getIndex();
// 					
// 		for(int numchrom = 0; numchrom < bai.getNumberOfReferences(); numchrom++) {
// 			SAMSequenceRecord seq = inputSam.getFileHeader().getSequence(numchrom);
// 			System.out.println("Processing: " + seq.getSequenceName());
// 			textArea.append("Processing: " + seq.getSequenceName() + "\n");
// 
// 			BP = new ArrayList<Integer>();
// 			M_OCC = new ArrayList<Integer>();
// 			CHROMSTOP = seq.getSequenceLength();
// 			
// 			CloseableIterator<SAMRecord> iter = inputSam.query(seq.getSequenceName(), 0, seq.getSequenceLength(), false);
// 			while (iter.hasNext()) {
// 				//Create the record object 
// 				SAMRecord sr = iter.next();
// 				
// 				//Must be PAIRED-END mapped, mate must be mapped, must be read1
// 				if(sr.getReadPairedFlag()) {
// 					if(sr.getProperPairFlag() && sr.getFirstOfPairFlag()) {
// 						boolean flag1 = (Math.abs(sr.getInferredInsertSize()) >= MIN_INSERT && MIN_INSERT != -9999) || MIN_INSERT == -9999; //check if insert size >= min if in use
// 						boolean flag2 = (Math.abs(sr.getInferredInsertSize()) <= MAX_INSERT && MAX_INSERT != -9999) || MAX_INSERT == -9999; //check if insert size <= max if in use
// 						if(flag1 && flag2) { addMidTag(sr); }
// 					}
// 				}
// 				
// 				//Dump ArrayLists to OUT if they get too big in order to save RAM and therefore time
// 				if((BP.size() > (MAX_INSERT * 10) && (MAX_INSERT * 10) > 1000) || (BP.size() > 1000 && (MAX_INSERT * 10) < 1000)) {
// 					dumpMidExcess(seq.getSequenceName());
// 				}
// 				
// 			}
// 			iter.close();
// 			for(int z = 0; z < BP.size(); z++) {
// 				OUT.println(seq.getSequenceName() + "\t" + BP.get(z).intValue() + "\t" + M_OCC.get(z).intValue() + "\t0\t" + M_OCC.get(z).intValue());
// 				//OUT.println(seq.getSequenceName() + "\t" + BP.get(z).intValue() + "\t" + M_OCC.get(z).intValue());		
// 			}
// 		}
// 		bai.close();
// 	}
	
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}