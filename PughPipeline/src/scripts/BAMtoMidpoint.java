package scripts;

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

import net.sf.samtools.AbstractBAMFileIndex;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMSequenceRecord;
import net.sf.samtools.util.CloseableIterator;

@SuppressWarnings("serial")
public class BAMtoMidpoint extends JFrame {
	private File BAM = null;
	private File OUTPUTPATH = null;
	private String READ = "MIDPOINT";
	
	private SAMFileReader inputSam = null;
	private PrintStream OUT = null;
	
	private JTextArea textArea;
	
	private ArrayList<Integer> BP;
	private ArrayList<Integer> M_OCC;
	
	private int MIN = 0;
	private int MAX = 1000;
	private int CHROMSTOP = -999;
	
	public BAMtoMidpoint(File b, File o, int mi, int ma) {
		setTitle("BAM to Midpoint Progress");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		BAM = b;
		OUTPUTPATH = o;
		MIN = mi;
		MAX = ma;
	}
	
	public void run() throws IOException, InterruptedException {
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
			MIDPOINT();
			OUT.close();
		} else {
			textArea.append("BAI Index File does not exist for: " + BAM.getName() + "\n");
			OUT.println("BAI Index File does not exist for: " + BAM.getName() + "\n");
			Thread.sleep(2000);
		}
		dispose();	
	}
	
	public void addTag(SAMRecord sr) {
		//Get the start of the record 
//		int recordStart = sr.getUnclippedStart();
//		int recordStop = recordStart + sr.getInferredInsertSize();
		int recordMid = sr.getUnclippedStart() + (sr.getInferredInsertSize() / 2);
		if(sr.getReadNegativeStrandFlag()) recordMid = sr.getUnclippedEnd() - (sr.getInferredInsertSize() / 2);
		
		//Make sure we only add tags that have valid midpoints
		if(recordMid > 0 && recordMid <= CHROMSTOP) {
			if(BP.contains(new Integer(recordMid))) {
				int index = BP.indexOf(new Integer(recordMid));
				M_OCC.set(index, new Integer(M_OCC.get(index).intValue() + 1));
			} else {
				//Sometimes the start coordinate will be out of order due to (-) strand correction
				//Need to efficiently identify where to place it relative to the other bps
				int index = BP.size() - 1;
				if(index >= 0) {
					while(index >= 0 && recordMid < BP.get(index).intValue()) {
						index--;
					}
				}
				if(index < BP.size() - 1) {
					BP.add(index + 1, new Integer(recordMid));
					M_OCC.add(index + 1, new Integer(0));
				} else {
					BP.add(new Integer(recordMid));
					M_OCC.add(new Integer(1));
				}
			}
		}
	}
	
	public void dumpExcess(String chrom) {
		int trim = (MAX * 10) - (MAX * 2);
		if(MAX * 10 < 1000) { trim = 600; }
		while(trim > 0) {
			OUT.println(chrom + "\t" + BP.get(0).intValue() + "\t" + M_OCC.get(0).intValue());
			BP.remove(0);
			M_OCC.remove(0);
			trim--;
		}
	}
	
	public void MIDPOINT() {
		inputSam = new SAMFileReader(BAM, new File(BAM.getAbsolutePath() + ".bai"));
		AbstractBAMFileIndex bai = (AbstractBAMFileIndex) inputSam.getIndex();
					
		for(int numchrom = 0; numchrom < bai.getNumberOfReferences(); numchrom++) {
			SAMSequenceRecord seq = inputSam.getFileHeader().getSequence(numchrom);
			System.out.println("Processing: " + seq.getSequenceName());
			textArea.append("Processing: " + seq.getSequenceName() + "\n");

			BP = new ArrayList<Integer>();
			M_OCC = new ArrayList<Integer>();
			CHROMSTOP = seq.getSequenceLength();
			
			CloseableIterator<SAMRecord> iter = inputSam.query(seq.getSequenceName(), 0, seq.getSequenceLength(), false);
			while (iter.hasNext()) {
				//Create the record object 
				SAMRecord sr = iter.next();
				
				//Must be PAIRED-END mapped, mate must be mapped, must be read1
				if(sr.getReadPairedFlag()) {
					if(sr.getProperPairFlag() && sr.getFirstOfPairFlag()) {
						if(sr.getInferredInsertSize() >= MIN && sr.getInferredInsertSize() <= MAX) {
							addTag(sr);
						}
					}
				}
				
				//Dump ArrayLists to OUT if they get too big in order to save RAM and therefore time
				if((BP.size() > (MAX * 10) && (MAX * 10) > 1000) || (BP.size() > 1000 && (MAX * 10) < 1000)) {
					dumpExcess(seq.getSequenceName());
				}
				
			}
			iter.close();
			for(int z = 0; z < BP.size(); z++) {
				OUT.println(seq.getSequenceName() + "\t" + BP.get(z).intValue() + "\t" + M_OCC.get(z).intValue());		
			}
		}
		inputSam.close();
		bai.close();
	}
	
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}