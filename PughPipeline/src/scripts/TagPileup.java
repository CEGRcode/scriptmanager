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
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLayeredPane;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import charts.CompositePlot;
import objects.BEDCoord;
import objects.PileupParameters;
import scripts.PileupScripts.PileupExtract;
import scripts.PileupScripts.TransformArray;
import util.JTVOutput;

@SuppressWarnings("serial")
public class TagPileup extends JFrame {
	Vector<BEDCoord> INPUT = null;
	Vector<File> BAMFiles = null;
	
	PileupParameters PARAM = null;
	
	private int STRAND = 0;
	private int CPU = 1;
	
	PrintStream OUT_S1 = null;
	PrintStream OUT_S2 = null;
	
	final JLayeredPane layeredPane;
	final JTabbedPane tabbedPane;
	final JTabbedPane tabbedPane_Scatterplot;
	final JTabbedPane tabbedPane_Statistics;
	
	//TagPileup pile = new TagPileup(INPUT, BAMFiles.get(x), OUTPUT, READ, STRAND, SHIFT, BIN);
	public TagPileup(Vector<BEDCoord> in, Vector<File> ba, PileupParameters param) {
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
		
		tabbedPane_Scatterplot = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Pileup Plot", null, tabbedPane_Scatterplot, null);
		
		tabbedPane_Statistics = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Pileup Statistics", null, tabbedPane_Statistics, null);

		INPUT = in;
		BAMFiles = ba;
		PARAM = param;
		STRAND = param.getStrand();
		CPU = param.getCPU();
	}
	
	public void run() throws FileNotFoundException {		
		for(int z = 0; z < BAMFiles.size(); z++) {
			//Pull first BAM file
			File BAM = BAMFiles.get(z);
			//Generate TimeStamp
			String time = getTimeStamp();
			
			if(PARAM.getOutput() != null) {
				if(STRAND == 0) {
					try { OUT_S1 = new PrintStream(PARAM.getOutput() + File.separator + generateFileName(BAM.getName(), 0));
					OUT_S2 = new PrintStream(PARAM.getOutput() + File.separator + generateFileName(BAM.getName(), 1));
					} catch (FileNotFoundException e) {	e.printStackTrace(); }
				} else {
					try { OUT_S1 = new PrintStream(PARAM.getOutput() + File.separator + generateFileName(BAM.getName(), 2));
					} catch (FileNotFoundException e) {	e.printStackTrace(); }
				}
			}
			
			JTextArea STATS = new JTextArea();
			STATS.setEditable(false);
			STATS.append(time + "\n");
			
			File f = new File(BAM + ".bai");
			//Check if BAI index file exists
			if(!f.exists() || f.isDirectory()) {
				if(OUT_S1 != null) OUT_S1.println("BAI Index File does not exist for: " + BAM.getName() + "\n");
				if(OUT_S2 != null) OUT_S2.println("BAI Index File does not exist for: " + BAM.getName() + "\n");
				STATS.append("BAI Index File does not exist for: " + BAM.getName() + "\n\n");
			} else {
			//if(f.exists() && !f.isDirectory()) {
				STATS.append(BAM.getName() + "\n");
				
				//Code to standardize tags sequenced to genome size (1 tag / 1 bp)
				if(PARAM.getStandard()) {
					SamReader inputSam = SamReaderFactory.makeDefault().open(BAM);
					AbstractBAMFileIndex bai = (AbstractBAMFileIndex) inputSam.indexing().getIndex();
					double counter = 0;
					double totalAligned = 0;
					double totalGenome = 0;
					
					for (int x = 0; x < bai.getNumberOfReferences(); x++) {
						SAMSequenceRecord seq = inputSam.getFileHeader().getSequence(x);
						totalAligned += inputSam.indexing().getIndex().getMetaData(z).getAlignedRecordCount();
						totalGenome += seq.getSequenceLength();
					}
					CloseableIterator<SAMRecord> iter = inputSam.iterator();
					while (iter.hasNext()) {
						SAMRecord sr = iter.next();
						if(sr.getReadPairedFlag()) {
							if(sr.getProperPairFlag() && sr.getFirstOfPairFlag()) {
								counter++;
							}
						}
					}
					bai.close();
					iter.close();
					if(counter != 0) PARAM.setRatio(counter / totalGenome);
					else PARAM.setRatio(totalAligned / totalGenome);
					System.out.println(counter + "\t" + totalGenome + "\t" + PARAM.getRatio());
				}
				
				//Split up job and send out to threads to process				
				ExecutorService parseMaster = Executors.newFixedThreadPool(CPU);
				if(INPUT.size() < CPU) CPU = INPUT.size();
				int subset = 0;
				int currentindex = 0;
				for(int x = 0; x < CPU; x++) {
					currentindex += subset;
					if(CPU == 1) subset = INPUT.size();
					else if(INPUT.size() % CPU == 0) subset = INPUT.size() / CPU;
					else {
						int remainder = INPUT.size() % CPU;
						if(x < remainder ) subset = (int)(((double)INPUT.size() / (double)CPU) + 1);
						else subset = (int)(((double)INPUT.size() / (double)CPU));
					}
					PileupExtract extract = new PileupExtract(PARAM, BAM, INPUT, currentindex, subset);
					parseMaster.execute(extract);
				}
				parseMaster.shutdown();
				while (!parseMaster.isTerminated()) {
				}
			
				double[] AVG_S1 = new double[INPUT.get(0).getFStrand().length];
				double[] AVG_S2 = null;
				if(STRAND == 0) AVG_S2 = new double[AVG_S1.length];
				double[] DOMAIN = new double[AVG_S1.length];

				//Account for the shifted oversized window produced by binning and smoothing
				int OUTSTART = 0;
				if(PARAM.getTrans() == 1) { OUTSTART = PARAM.getSmooth(); }
				else if(PARAM.getTrans() == 2) { OUTSTART = (PARAM.getStdSize() * PARAM.getStdNum()); }
								
				if(PARAM.getOutputType() == 2) {
					if(OUT_S1 != null) OUT_S1.print("YORF\tNAME");
					if(OUT_S2 != null) OUT_S2.print("YORF\tNAME");
					double[] tempF = INPUT.get(0).getFStrand();
										
					for(int i = OUTSTART; i < tempF.length - OUTSTART; i++) {
						int index = i - OUTSTART;
						if(OUT_S1 != null) OUT_S1.print("\t" + index);
						if(OUT_S2 != null) OUT_S2.print("\t" + index);
					}
					if(OUT_S1 != null) OUT_S1.println();
					if(OUT_S2 != null) OUT_S2.println();
				}
				
				//Output individual sites
				for(int i = 0; i < INPUT.size(); i++) {
					double[] tempF = INPUT.get(i).getFStrand();
					double[] tempR = INPUT.get(i).getRStrand();
					if(OUT_S1 != null) OUT_S1.print(INPUT.get(i).getName());
					if(OUT_S2 != null) OUT_S2.print(INPUT.get(i).getName());
					
					if(PARAM.getOutputType() == 2) {
						if(OUT_S1 != null) OUT_S1.print("\t" + INPUT.get(i).getName());
						if(OUT_S2 != null) OUT_S2.print("\t" + INPUT.get(i).getName());
					}
					
					for(int j = 0; j < tempF.length; j++) {
						if(j >= OUTSTART && j < tempF.length - OUTSTART) {
							if(OUT_S1 != null) OUT_S1.print("\t" + tempF[j]);
							if(OUT_S2 != null) OUT_S2.print("\t" + tempR[j]);
						}
						AVG_S1[j] += tempF[j];
						if(AVG_S2 != null) AVG_S2[j] += tempR[j];
					}
					if(OUT_S1 != null) OUT_S1.println();
					if(OUT_S2 != null) OUT_S2.println();
				}

				//Calculate average and domain here
				int temp = (int) (((double)AVG_S1.length / 2.0) + 0.5);
				for(int i = 0; i < AVG_S1.length; i++) {
					DOMAIN[i] = (double)((temp - (AVG_S1.length - i)) * PARAM.getBin());
					AVG_S1[i] /= INPUT.size();
					if(AVG_S2 != null) AVG_S2[i] /= INPUT.size();
				}
				
				//Transform average given transformation parameters
				if(PARAM.getTrans() == 1) { 
					AVG_S1 = TransformArray.smoothTran(AVG_S1, PARAM.getSmooth());
					if(AVG_S2 != null) AVG_S2 = TransformArray.smoothTran(AVG_S2, PARAM.getSmooth());
				} else if(PARAM.getTrans() == 2) {
					AVG_S1 = TransformArray.gaussTran(AVG_S1, PARAM.getStdSize(), PARAM.getStdNum());
					if(AVG_S2 != null) AVG_S2 = TransformArray.gaussTran(AVG_S2, PARAM.getStdSize(), PARAM.getStdNum());
				}
				
				//Trim average here and output to statistics pane
				double[] AVG_S1_trim = new double[AVG_S1.length - (OUTSTART * 2)];
				double[] AVG_S2_trim = null;
				if(STRAND == 0) AVG_S2_trim = new double[AVG_S1_trim.length];
				double[] DOMAIN_trim = new double[AVG_S1_trim.length];
				for(int i = OUTSTART; i < AVG_S1.length - OUTSTART; i++) {
					if(AVG_S2 != null) {
						STATS.append(DOMAIN[i] + "\t" + AVG_S1[i] + "\t" + AVG_S2[i] + "\n");
						AVG_S2_trim[i - OUTSTART] = AVG_S2[i];
					}
					else  STATS.append(DOMAIN[i] + "\t" + AVG_S1[i] + "\n");
					AVG_S1_trim[i - OUTSTART] = AVG_S1[i];
					DOMAIN_trim[i - OUTSTART] = DOMAIN[i];
				}
				AVG_S1 = AVG_S1_trim;
				AVG_S2 = AVG_S2_trim;
				DOMAIN = DOMAIN_trim;
				
				if(STRAND == 0) tabbedPane_Scatterplot.add(BAM.getName(), CompositePlot.createCompositePlot(DOMAIN, AVG_S1, AVG_S2));
				else tabbedPane_Scatterplot.add(BAM.getName(), CompositePlot.createCompositePlot(DOMAIN, AVG_S1));
				if(OUT_S1 != null && PARAM.getOutputType() == 2) {
					if(STRAND == 0) JTVOutput.outputJTV(PARAM.getOutput() + File.separator + generateFileName(BAM.getName(), 0), "blue");
					else JTVOutput.outputJTV(PARAM.getOutput() + File.separator + generateFileName(BAM.getName(), 2), "green");
					OUT_S1.close();
				}
				if(OUT_S2 != null && PARAM.getOutputType() == 2){
					JTVOutput.outputJTV(PARAM.getOutput() + File.separator + generateFileName(BAM.getName(), 1), "red");
					OUT_S2.close();
				}
							
			}
			STATS.setCaretPosition(0);
			JScrollPane newpane = new JScrollPane(STATS, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			tabbedPane_Statistics.add(BAM.getName(), newpane);
			
	        firePropertyChange("tag", z, z + 1);
		}		
	}
	
	/*private int filterRead(int coord, boolean Readstrand, String CoordDir) {
		//check for strandedness here
		//Readstrand true if -, false if +
        if(STRAND == 0) {
        	if(!Readstrand && CoordDir.equals("-")) { return -999; }
        	else if(Readstrand && CoordDir.equals("+")) { return -999; }
        } else if(STRAND == 1) {
        	if(!Readstrand && CoordDir.equals("+")) { return -999; }
        	else if(Readstrand && CoordDir.equals("-")) { return -999;}
        }
        return coord;
	}*/
	
	public String generateFileName(String origin, int strandnum) {
		String[] name = origin.split("\\.");
		
		String strand = "sense";
		if(strandnum == 1) strand = "anti";
		else if(strandnum == 2) strand = "combined";
		String read = "read1";
		if(PARAM.getRead() == 1) read = "read2";
		else if(PARAM.getRead() == 2) read = "readc";
		
		String filename = name[0] + "_" + read + "_" + strand;
		if(PARAM.getOutputType() == 1) filename += ".tab";
		else filename += ".cdt";
		return filename;
	}
		
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}
