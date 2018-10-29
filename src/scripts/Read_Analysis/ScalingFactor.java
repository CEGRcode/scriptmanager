package scripts.Read_Analysis;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;

import htsjdk.samtools.AbstractBAMFileIndex;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;
import htsjdk.samtools.util.CloseableIterator;
import objects.CoordinateObjects.BEDCoord;
import charts.ScalingPlotter;

/**
 * NCIS code adapted from Mahony Lab
 * https://github.com/seqcode/seqcode-core
 * NCIS algorithm from Liang & Keles (BMC Bioinformatics 2012)
 */

@SuppressWarnings("serial")
public class ScalingFactor extends JFrame {
	
	ArrayList<File> BAMFiles = null;
	private File BLACKLISTFile = null;
	private File CONTROL = null;
	private String OUTPUTPATH = null;
	private boolean OUTPUTSTATUS = false;
	private String FILEID = null;
	
	private int scaleType = -1;
	private int windowSize = 500;
	private double minFraction = 0.75;

	private List<String> chromName = null;
	private List<Long> chromLength = null;
	
	private List<Float> Sgenome = new ArrayList<Float>();
	private double STagcount = 0;
	private List<Float> Cgenome = new ArrayList<Float>();
	private double CTagcount = 0;
	
	private HashMap<String, ArrayList<BEDCoord>> BLACKLIST = null;
	private ArrayList<Double> SCALINGFACTORS = new ArrayList<Double>();
	
	final JLayeredPane layeredPane;
	final JTabbedPane tabbedPane;
	final JTabbedPane tabbedPane_CummulativeScatterplot;
	final JTabbedPane tabbedPane_MarginalScatterplot;
	
	public ScalingFactor(ArrayList<File> b, File bl, File c, String out_path, boolean out, int scale, int win, double min) {
		setTitle("Scaling Factor");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 800, 800);
		
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
				
		BAMFiles = b;
		BLACKLISTFile = bl;
		CONTROL = c;
		OUTPUTPATH = out_path;
		OUTPUTSTATUS = out;
		scaleType = scale;
		windowSize = win;
		minFraction = min;
		
		tabbedPane_CummulativeScatterplot = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane_MarginalScatterplot = new JTabbedPane(JTabbedPane.TOP);		
		if(scaleType != 1) {
			tabbedPane.addTab("Cumulative Count Scaling Ratio", null, tabbedPane_CummulativeScatterplot, null);
			tabbedPane.addTab("Marginal Signal/Control Ratio", null, tabbedPane_MarginalScatterplot, null);
		}
	}
	
	public void run() throws IOException {
		//Load blacklist HashMap if blacklist file uploaded by user
		if(BLACKLISTFile != null) {	loadBlacklist(BLACKLISTFile); }
		
		//Load up the Control File once per run
		if(scaleType != 1) {
			System.out.println("\nLoading control genome array...");
			initalizeGenomeMetainformation(CONTROL);
			Cgenome = initializeList(CONTROL, false);
			System.out.println("Array loaded");
		}
		
		PrintStream OUT = null;
		if(OUTPUTSTATUS) { OUT = new PrintStream(OUTPUTPATH + File.separator + "ScalingFactors.out"); }
		
		for(int z = 0; z < BAMFiles.size(); z++) {
			File SAMPLE = BAMFiles.get(z);	//Pull current BAM file
			File f = new File(SAMPLE + ".bai"); //Generate file name for BAI index file
			//Check if BAI index file exists
			if(!f.exists() || f.isDirectory()) { JOptionPane.showMessageDialog(null, "BAI Index File does not exist for: " + SAMPLE.getName()); }
			else {
				System.out.println(getTimeStamp());
				FILEID = SAMPLE.getName();
				System.out.println("Sample file:\t" + FILEID);
				if(CONTROL != null) { System.out.println("Control file:\t" + CONTROL.getName()); }

				double SCALE = 1;
				if(scaleType == 1) {
					initalizeGenomeMetainformation(SAMPLE);
					Sgenome = initializeList(SAMPLE, true);
					double genomeSize = 0;
					for(int x = 0; x < chromLength.size(); x++) { genomeSize += chromLength.get(x); }
					if(genomeSize != 0) { SCALE = genomeSize / STagcount; }
					System.out.println("Sample tags: " + STagcount);
					System.out.println("Genome size: " + genomeSize);
					System.out.println("Total tag ratio: " + SCALE);
					SCALINGFACTORS.add(SCALE);
				} else {
					if(verifyFiles(SAMPLE)) {
						System.out.println("\nLoading sample genome array...");
						Sgenome = initializeList(SAMPLE, true);
						System.out.println("Array loaded");
						System.out.println("Sample tags: " + STagcount);
						System.out.println("Control tags: " + CTagcount);
						System.out.println("Bin count: " + Sgenome.size());
						
						if(scaleType == 2) {
							System.out.println("\nCalculating NCIS scaling ratio...");
							SCALE = 1 / scalingRatioByNCIS(Sgenome, Cgenome, OUTPUTPATH, FILEID, minFraction);
							System.out.println("NCIS sample scaling ratio: " + SCALE);
						} else if(scaleType == 3) {
							System.out.println("\nCalculating Total tag NCIS scaling ratio...");
							SCALE = 1 / scalingRatioByHitRatioAndNCIS(Sgenome, Cgenome, STagcount, CTagcount, OUTPUTPATH, FILEID, minFraction);
							System.out.println("NCIS with Total Tag sample scaling ratio: " + SCALE);
						}
						SCALINGFACTORS.add(SCALE);
					} else {
						SCALINGFACTORS.add(Double.NaN);
					}
				}
				
				//Output scaling factor is user-specified
				if(OUTPUTSTATUS) {
					OUT.println("Sample file:\t" + SAMPLE);
					if(scaleType == 1) { OUT.println("Scaling type:\tTotalTag"); }
					else {
						OUT.println("Control file:\t" + CONTROL);
						if(scaleType == 2) { OUT.println("Scaling type:\tNCIS"); }
						else if(scaleType == 3) { OUT.println("Scaling type:\tTotalTag with NCIS"); }
						OUT.println("Window size (bp):\t" + windowSize);
						OUT.println("Minimum fraction:\t" + minFraction);
					}
					OUT.println("Scaling factor:\t" + SCALE);
				}
				System.out.println(getTimeStamp());
			}
	        firePropertyChange("scale", z, (z + 1));
		}
		//Close output Printstream if open
		if(OUTPUTSTATUS) { OUT.close(); }
		
		//Make frame visible at completion of correlations if not already visible
		if(!this.isVisible()) { this.setVisible(true); }
		tabbedPane.addTab("Scaling Factor", makeTablePanel(SCALINGFACTORS));

	}
	
	public List<Float> initializeList(File BAM, boolean sample) {
		List<Float> GENOME = new ArrayList<Float>();
		SamReader inputBAM = SamReaderFactory.makeDefault().open(BAM);
		double TOTAL = 0;
		
		for(int x = 0; x < chromName.size(); x++) {
			String seq = chromName.get(x);
			long chromSize = chromLength.get(x);
			float[] chrom = new float[(int) (chromSize / windowSize) + 1];
			//Blacklist filter each chromosome, set blacklisted windows to NaN
			if(BLACKLIST != null) { chrom = maskChrom(seq, chromSize, windowSize); }
			//Iterate through chromosome loading tags into window bin 
			CloseableIterator<SAMRecord> iter = inputBAM.query(seq, 0, (int)chromSize, false);
			//SAMRecords are 1-based
			while (iter.hasNext()) {
				SAMRecord sr = iter.next();
				int FivePrime = sr.getUnclippedStart() - 1;
				if(sr.getReadNegativeStrandFlag()) { FivePrime = sr.getUnclippedEnd(); }
				int INDEX = (FivePrime / windowSize);
				if(sr.getReadPairedFlag()) { //If paired-end, take only read 1
					//Read 1
					if(sr.getFirstOfPairFlag()) { if(INDEX < chrom.length) { chrom[INDEX]++; } }
				} else { //If NOT paired-end, tag is always read 1
					if(INDEX < chrom.length) { chrom[INDEX]++; }
				}
			}
			iter.close();
			for(int i = 0; i < chrom.length; i++) {
				//System.out.println(seq + "\t" + chrom[i]);
				if(!Float.isNaN(chrom[i])) {
					TOTAL += chrom[i];
					GENOME.add(chrom[i]);
				}
			}
		}
		if(sample) { STagcount = TOTAL; }
		else { CTagcount = TOTAL; }
		return GENOME;
	}
	
	public float[] maskChrom(String chrom, long chromSize, int windowSize) {
		float[] chromArray = new float[(int) (chromSize / windowSize) + 1];
		if(BLACKLIST.containsKey(chrom)) {
			ArrayList<BEDCoord> blacklist = BLACKLIST.get(chrom);
			for(int x = 0; x < blacklist.size(); x++) {
				long START = blacklist.get(x).getStart();
				long STOP = blacklist.get(x).getStop();
				while(START < STOP) {
					int index = ((int)START / windowSize);
					if(index < chromArray.length) { chromArray[index] = Float.NaN; }
					START += windowSize;
				}
			}
		}
		return chromArray;
	}
	
	public void loadBlacklist(File BLACKFile) throws FileNotFoundException {
		BLACKLIST = new HashMap<String, ArrayList<BEDCoord>>();
	    Scanner scan = new Scanner(BLACKFile);
		while (scan.hasNextLine()) {
			String[] temp = scan.nextLine().split("\t");
			if(temp.length > 2) {
				if(!temp[0].contains("track") && !temp[0].contains("#")) {
					if(Integer.parseInt(temp[1]) >= 0) {
						int start = Integer.parseInt(temp[1]);
						int stop = Integer.parseInt(temp[2]);
						BEDCoord coord = new BEDCoord(temp[0], start, stop , ".");
						if(BLACKLIST.containsKey(temp[0])) { BLACKLIST.get(temp[0]).add(coord);	}
						else {
							ArrayList<BEDCoord> newchrom = new ArrayList<BEDCoord>();
							newchrom.add(coord);
							BLACKLIST.put(temp[0], newchrom);
						}			
					} else {
						System.err.println("Invalid Coordinate in File!!!\n" + Arrays.toString(temp));
					}
				}
			}
	    }
		scan.close();
		
//		Iterator it = BLACKLIST.entrySet().iterator();
//	    while (it.hasNext()) {
//	        HashMap.Entry pair = (HashMap.Entry)it.next();
//	        ArrayList<BEDCoord> temp = (ArrayList<BEDCoord>) pair.getValue();
//	        for(int x = 0; x < temp.size(); x++) {
//	        	System.out.println(pair.getKey() + " = " + temp.get(x).toString());
//	        }
//	        it.remove(); // avoids a ConcurrentModificationException
//	    }
	}
	
	public void initalizeGenomeMetainformation(File SAMPLE) throws IOException {
		SamReaderFactory factory = SamReaderFactory.makeDefault().enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS, SamReaderFactory.Option.VALIDATE_CRC_CHECKSUMS).validationStringency(ValidationStringency.SILENT);
		SamReader Sreader = factory.open(SAMPLE);
		AbstractBAMFileIndex Sbai = (AbstractBAMFileIndex) Sreader.indexing().getIndex();
		chromName = new ArrayList<String>();
		chromLength = new ArrayList<Long>();
		for (int z = 0; z < Sbai.getNumberOfReferences(); z++) {
			SAMSequenceRecord Sseq = Sreader.getFileHeader().getSequence(z);
			chromName.add(Sseq.getSequenceName());
			chromLength.add(new Long(Sseq.getSequenceLength()));
		}
		Sreader.close();
		Sbai.close();
	}
	
	public boolean verifyFiles(File SAMPLE) throws IOException {
		SamReaderFactory factory = SamReaderFactory.makeDefault().enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS, SamReaderFactory.Option.VALIDATE_CRC_CHECKSUMS).validationStringency(ValidationStringency.SILENT);
		SamReader Sreader = factory.open(SAMPLE);
		SamReader Creader = factory.open(CONTROL);
		AbstractBAMFileIndex Sbai = (AbstractBAMFileIndex) Sreader.indexing().getIndex();
		AbstractBAMFileIndex Cbai = (AbstractBAMFileIndex) Creader.indexing().getIndex();
		if(Sbai.getNumberOfReferences() != Cbai.getNumberOfReferences()) {
			JOptionPane.showMessageDialog(null, "Unequal number of chromosomes between sample and control!!!");
			System.err.println("Unequal number of chromosomes between sample and control!!!!");
			return false;
		}
		for (int z = 0; z < Sbai.getNumberOfReferences(); z++) {
			SAMSequenceRecord Sseq = Sreader.getFileHeader().getSequence(z);
			SAMSequenceRecord Cseq = Creader.getFileHeader().getSequence(z);
			if(!Sseq.getSequenceName().equals(Cseq.getSequenceName())) {
				JOptionPane.showMessageDialog(null, "Chromosome names do not match!!!\n" + Sseq.getSequenceName() + "\n" + Cseq.getSequenceName());
				System.err.println("Chromosome names do not match!!!\n" + Sseq.getSequenceName() + "\n" + Cseq.getSequenceName());
				return false;
			}
			if(Sseq.getSequenceLength() != Cseq.getSequenceLength()) {
				JOptionPane.showMessageDialog(null, "Chromosome lengths do not match!!!\n" + Sseq.getSequenceName() + "\t" + Sseq.getSequenceLength() + "\n" + Cseq.getSequenceName() + "\t" + Cseq.getSequenceLength());
				System.err.println("Chromosome lengths do not match!!!\n" + Sseq.getSequenceName() + "\t" + Sseq.getSequenceLength() + "\n" + Cseq.getSequenceName() + "\t" + Cseq.getSequenceLength());
				return false;
			}
		}
		Sreader.close();
		Creader.close();
		Sbai.close();
		Cbai.close();
		return true;
	}
	
	/**
	 * Find the scaling ratio according to the NCIS method from Liang & Keles (BMC Bioinf 2012).
	 * Also sets a background proportion estimate for the signal channel.  
	 * Should be run using *all* genomic windows in the Lists. 
	 * Uses ratios that are based on at least 75% of genomic regions by default. 
	 * @param setA : signal list
	 * @param setB : control list
	 * @param outputFile : optional file that will contain the data 
	 * @return
	 */
	public double scalingRatioByNCIS(List<Float> setA, List<Float> setB, String outpath, String fileid, double minFrac){
		double scalingRatio=1;
		double totalAtScaling=0;
		if(setA.size() != setB.size()){
			System.err.println("NCIS is trying to scale lists of two different lengths");
			System.exit(1);
		}
		
		float numPairs = (float)setA.size();
		List<PairedCounts> counts = new ArrayList<PairedCounts>();
		for(int x = 0; x < setA.size(); x++) { counts.add(new PairedCounts(setA.get(x), setB.get(x))); } 
		//NCIS uses increasing total tag counts versus enrichment ratio
		Collections.sort(counts, new Comparator<PairedCounts>() { public int compare(PairedCounts o1, PairedCounts o2) { return o1.compareByTotal(o2); } });
        
        //NCIS procedure
        double cumulA = 0, cumulB = 0, currRatio = 0, lastRatio = -1;
        float i = 0;
        for(PairedCounts pc : counts) {
        	cumulA += pc.x;
        	cumulB += pc.y;
        	totalAtScaling = pc.x + pc.y;	
      	 	i++;
      	 	if(i / numPairs > minFrac && cumulA > 0 && cumulB > 0){ //NCIS estimates begin using the lower 3 quartiles of the genome (based on total tags)
      	 		currRatio = (cumulA / cumulB);
      	 		if(lastRatio == -1 || currRatio < lastRatio){ lastRatio = currRatio; }
      	 		else { break; }
      	 	}
        }
        scalingRatio = currRatio;
        //Generate and output scatter plots
        plotGraphs(counts, totalAtScaling, scalingRatio, outpath, fileid, "NCIS");
        
		return(scalingRatio);
	}
	
	/**
	 * Find the scaling ratio according to the total tag normalization followed by NCIS method from Liang & Keles (BMC Bioinf 2012).
	 * Also sets a background proportion estimate for the signal channel.  
	 * Should be run using *all* genomic windows in the Lists. 
	 * Uses ratios that are based on at least 75% of genomic regions by default. 
	 * @param setA : signal list
	 * @param setB : control list
	 * @param outputFile : optional file that will contain the data 
	 * @return
	 */
	public double scalingRatioByHitRatioAndNCIS(List<Float> setA, List<Float> setB, double totalA, double totalB, String outpath, String fileid, double minFrac){
		double scalingRatio=1;
		double totalAtScaling=0;
		if(setA.size() != setB.size()){
			System.err.println("NCIS is trying to scale lists of two different lengths");
			System.exit(1);
		}
		
		//First normalize tag number between experiments using total reads
		float tRatio = (float) (totalA / totalB);
		List<Float> setnB = new ArrayList<Float>();
		for (int x = 0; x < setB.size() ; x++) { setnB.add(setB.get(x) * tRatio); }
		
		float numPairs = (float)setA.size();
		List<PairedCounts> counts = new ArrayList<PairedCounts>();
		for(int x=0; x<setA.size(); x++) { counts.add(new PairedCounts(setA.get(x), setnB.get(x))); } 
		//NCIS uses increasing total tag counts versus enrichment ratio
		Collections.sort(counts, new Comparator<PairedCounts>(){ public int compare(PairedCounts o1, PairedCounts o2) {return o1.compareByTotal(o2);} });
        
        //NCIS procedure
        double cumulA=0, cumulB=0, currRatio=0, lastRatio=-1;
        float i=0;
        for(PairedCounts pc : counts) {
        	cumulA+=pc.x;
        	cumulB+=pc.y;
        	totalAtScaling = pc.x+pc.y;
        	i++;
        	if(i / numPairs > minFrac && cumulA > 0 && cumulB > 0) { //NCIS estimates begin using the lower 3 quartiles of the genome (based on total tags)
        		currRatio = (cumulA/cumulB);
        		if(lastRatio == -1 || currRatio < lastRatio){ lastRatio = currRatio; }
        		else { break; }
        	}
        }
        scalingRatio = currRatio * tRatio; //Multiply by the total tag normalization
        //Generate and output scatter plots
        plotGraphs(counts, totalAtScaling, scalingRatio, outpath, fileid, "TotalReadsAndNCIS");

		return(scalingRatio);
	}

	/**
	 * Simple class for storing paired counts that are sortable in first dimension
	 * @author mahony
	 *
	 */
	public static class PairedCounts implements Comparable<PairedCounts>{
		public Double x,y;
		public PairedCounts(double a, double b){
			x=a;
			y=b;
		}
		/**
		 * Sort on increasing X variables
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(PairedCounts pc) {
			if(x<pc.x){return -1;}
			if(x>pc.x){return 1;}
			return 0;
		}
		
		/**
		 * Compare based on the sum of both paired counts
		 * @param pc
		 * @return
		 */
		public int compareByTotal(PairedCounts pc){
			if((x+y)<(pc.x+pc.y)){return -1;}
			if((x+y)>(pc.x+pc.y)){return 1;}
			return 0;
		}
		
	}
	
	public void plotGraphs(List<PairedCounts> counts, double totalAtScaling, double scalingRatio, String outpath, String fileid, String scaletype) {
		//Scaling plot generation
	 	//Cumulative ratio vs bin total
		List<Double> bintotals=new ArrayList<Double>();
		List<Double> ratios=new ArrayList<Double>();
		double cumulA=0, cumulB=0;
    	for(PairedCounts pc : counts){
        	cumulA+=pc.x;
        	cumulB+=pc.y;
        	if(cumulA>0 && cumulB>0){
        		Double ratio  = (cumulA / cumulB); 
        		bintotals.add(pc.x+pc.y);
        		ratios.add(ratio);
        	}
    	}
		
		//Marginal ratios vs bin totals
		List<Double> bintot=new ArrayList<Double>();
		List<Double> mratios=new ArrayList<Double>();
		for(int x=0; x<counts.size(); x++){
			PairedCounts pc = counts.get(x);
			if(pc.x>0 && pc.y>0){
				double currA=pc.x, currB=pc.y;
				double currTot=pc.x+pc.y;
				while(x<counts.size()-1 && (counts.get(x+1).x + counts.get(x+1).y)==currTot){
					x++;
					pc = counts.get(x);
					currA+=pc.x; 
					currB+=pc.y;
				}
				bintot.add(currTot);
				mratios.add(currA/currB);
			}
		}
		//Generate images
		tabbedPane_CummulativeScatterplot.add(fileid, ScalingPlotter.generateXYplot(bintotals, ratios, totalAtScaling, scalingRatio, fileid + " " + scaletype + " plot", "Binned Total Tag Count", "Cumulative Count Scaling Ratio"));
		tabbedPane_MarginalScatterplot.add(fileid, ScalingPlotter.generateXYplot(bintot, mratios, totalAtScaling, scalingRatio, fileid + " " + scaletype + " plot", "Binned Total Tag Count", "Marginal Signal/Control Ratio"));
		//Make frame visible at completion of correlations if not already visible
		if(!this.isVisible()) { this.setVisible(true); }
		
		if(OUTPUTSTATUS) {
			//Print data points to files
			try {
				FileWriter Cfout = new FileWriter(outpath + File.separator + fileid + "." + scaletype + "_scaling-ccr.count");
				for(int d=0; d<bintotals.size(); d++) { Cfout.write(bintotals.get(d)+"\t"+ratios.get(d)+"\n"); }
				Cfout.close();
				FileWriter Mfout = new FileWriter(outpath + File.separator + fileid + "." + scaletype + "_scaling-marginal.count");
				for(int d=0; d<bintot.size(); d++) { Mfout.write(bintot.get(d)+"\t"+mratios.get(d)+"\n"); }
				Mfout.close();
			} catch (IOException e) { e.printStackTrace(); }
		}
	}
	
	public JScrollPane makeTablePanel(ArrayList<Double> SCALE) {
		JTable table = new JTable(SCALE.size(), 2);
		table.setName("Scaling Factors");
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		for(int i = 0; i < SCALE.size(); i++) {
			table.setValueAt(BAMFiles.get(i).getName(), i, 0);
			table.setValueAt(SCALE.get(i).doubleValue(), i, 1);
		}
		table.getColumnModel().getColumn(0).setHeaderValue("Experiment");
		table.getColumnModel().getColumn(1).setHeaderValue("Scaling Factor");
		table.setPreferredSize(table.getPreferredSize());
		JScrollPane pane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		pane.setPreferredSize(new Dimension(590, 590));
		return pane;
	}
	
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}
