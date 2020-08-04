package scripts.Read_Analysis;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPOutputStream;

import org.jfree.chart.ChartPanel;

import charts.CompositePlot;
import htsjdk.samtools.AbstractBAMFileIndex;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import objects.PileupParameters;
import objects.CoordinateObjects.BEDCoord;
import scripts.Read_Analysis.PileupScripts.PileupExtract;
import util.ArrayUtilities;
import util.JTVOutput;

@SuppressWarnings("serial")
public class TagPileup {
	File BED = null;
	File BAM = null;
	
	PileupParameters PARAM = null;
	
	private int STRAND = 0;
	
	PrintStream COMPOSITE = null;
	// Generic print stream to accept PrintStream of GZIPOutputStream
	Writer OUT_S1 = null;
	Writer OUT_S2 = null;
	
	PrintStream PS = null;
	
	Component compositePlot = null;
	
	String outMatrixBasename;
	
	boolean GUI = false;
	
	public TagPileup(File be, File ba, PileupParameters param, PrintStream outputwindow_ps, String outMat, boolean guiStatus) {
		BED = be;
		BAM = ba;
		PARAM = param;
		PS = outputwindow_ps;
		outMatrixBasename = outMat;
		GUI = guiStatus;
	}
	
	public void run() throws IOException {
		//Set-up Matrix output writers
		int STRAND = PARAM.getStrand();
		if(PARAM.getOutputType() != 0) {
			if(STRAND == 0) {
				//Set FileName
				String NAME0;
				String NAME1;
				if(outMatrixBasename==null){
					NAME0 = PARAM.getOutput() + File.separator + generateFileName(BED.getName(), BAM.getName(), 0);
					NAME1 = PARAM.getOutput() + File.separator + generateFileName(BED.getName(), BAM.getName(), 1);
				}else{
					NAME0 = generateFileName(outMatrixBasename, 0);
					NAME1 = generateFileName(outMatrixBasename, 1);
				}
				//Build streams
				if(PARAM.getOutputGZIP()) {
					OUT_S1 = new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream( NAME0)), "UTF-8");
					OUT_S2 = new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream( NAME1)), "UTF-8");
				} else {
					OUT_S1 = new PrintWriter(NAME0);
					OUT_S2 = new PrintWriter(NAME1);
				}
			} else {
				//Set FileName
				String NAME2;
				if(outMatrixBasename==null){
					NAME2 = PARAM.getOutput() + File.separator + generateFileName(BED.getName(), BAM.getName(), 2);
				}else{
					NAME2 = generateFileName(outMatrixBasename, 2);
				}
				if(PARAM.getOutputGZIP()) {
					OUT_S1 = new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(NAME2)), "UTF-8");
				} else {
					OUT_S1 = new PrintWriter(NAME2);
				}
			}
		}
		
		printPS(getTimeStamp());	//Timestamp process
		printPS(BAM.getName());		//Label stat object with what BAM file is generating it
		
		//Validate and load BED coordinates
		System.err.println("Validating BED: " + BED.getName());
		Vector<BEDCoord> INPUT = validateBED(loadCoord(BED), BAM);
		
		//Split up job and send out to threads to process
		int CPU = PARAM.getCPU();
		ExecutorService parseMaster = Executors.newFixedThreadPool(CPU);
		if(INPUT.size() < CPU) CPU = INPUT.size();
		int subset = 0;
		int currentindex = 0;
		for(int x = 0; x < CPU; x++) {
			currentindex += subset;
			if(CPU == 1) { subset = INPUT.size(); }
			else if(INPUT.size() % CPU == 0) { subset = INPUT.size() / CPU; }
			else {
				int remainder = INPUT.size() % CPU;
				if(x < remainder ) subset = (int)(((double)INPUT.size() / (double)CPU) + 1);
				else subset = (int)(((double)INPUT.size() / (double)CPU));
			}
			//System.out.println("CPU: " + x + "\tInterval: " + currentindex + "\t" + subset);
			PileupExtract extract = new PileupExtract(PARAM, BAM, INPUT, currentindex, subset);
			parseMaster.execute(extract);
		}
		parseMaster.shutdown();
		while (!parseMaster.isTerminated()) {
		}
		
		double[] AVG_S1 = new double[getMaxBEDSize(INPUT)];
		double[] AVG_S2 = null;
		if(STRAND == 0) AVG_S2 = new double[AVG_S1.length];
		double[] DOMAIN = new double[AVG_S1.length];

		//Account for the shifted oversized window produced by binning and smoothing
		int OUTSTART = 0;
		if(PARAM.getTrans() == 1) { OUTSTART = PARAM.getSmooth(); }
		else if(PARAM.getTrans() == 2) { OUTSTART = (PARAM.getStdSize() * PARAM.getStdNum()); }
		
		//Write headers
		if(PARAM.getOutputType() == 2) {
			if(OUT_S1 != null) OUT_S1.write("YORF\tNAME");
			if(OUT_S2 != null) OUT_S2.write("YORF\tNAME");
			double[] tempF = INPUT.get(0).getFStrand();
								
			for(int i = OUTSTART; i < tempF.length - OUTSTART; i++) {
				int index = i - OUTSTART;
				if(OUT_S1 != null) OUT_S1.write("\t" + index);
				if(OUT_S2 != null) OUT_S2.write("\t" + index);
			}
			if(OUT_S1 != null) OUT_S1.write("\n");
			if(OUT_S2 != null) OUT_S2.write("\n");
		}
		
		//Output individual sites
		for(int i = 0; i < INPUT.size(); i++) {
			double[] tempF = INPUT.get(i).getFStrand();
			double[] tempR = INPUT.get(i).getRStrand();
			if(OUT_S1 != null) OUT_S1.write(INPUT.get(i).getName());
			if(OUT_S2 != null) OUT_S2.write(INPUT.get(i).getName());
			
			if(PARAM.getOutputType() == 2) {
				if(OUT_S1 != null) OUT_S1.write("\t" + INPUT.get(i).getName());
				if(OUT_S2 != null) OUT_S2.write("\t" + INPUT.get(i).getName());
			}
			
			for(int j = 0; j < tempF.length; j++) {
				//Output values outside of window overhang
				if(j >= OUTSTART && j < tempF.length - OUTSTART) {
					if(OUT_S1 != null) OUT_S1.write("\t" + tempF[j]);
					if(OUT_S2 != null) OUT_S2.write("\t" + tempR[j]);
				}
				//Sum positions across BED coordinates
				AVG_S1[j] += tempF[j];
				if(AVG_S2 != null) AVG_S2[j] += tempR[j];
			}
			if(OUT_S1 != null) OUT_S1.write("\n");
			if(OUT_S2 != null) OUT_S2.write("\n");
		}

		//Calculate average and domain here
		int temp = (int) (((double)AVG_S1.length / 2.0) + 0.5);
		for(int i = 0; i < AVG_S1.length; i++) {
			DOMAIN[i] = (double)((temp - (AVG_S1.length - i)) * PARAM.getBin()) + 1;
			AVG_S1[i] /= INPUT.size();
			if(AVG_S2 != null) AVG_S2[i] /= INPUT.size();
		}
		
		//Transform average given transformation parameters
		if(PARAM.getTrans() == 1) { 
			AVG_S1 = ArrayUtilities.windowSmooth(AVG_S1, PARAM.getSmooth());
			if(AVG_S2 != null) AVG_S2 = ArrayUtilities.windowSmooth(AVG_S2, PARAM.getSmooth());
		} else if(PARAM.getTrans() == 2) {
			AVG_S1 = ArrayUtilities.gaussSmooth(AVG_S1, PARAM.getStdSize(), PARAM.getStdNum());
			if(AVG_S2 != null) AVG_S2 = ArrayUtilities.gaussSmooth(AVG_S2, PARAM.getStdSize(), PARAM.getStdNum());
		}
		
		//Trim average here and output to statistics pane
		double[] AVG_S1_trim = new double[AVG_S1.length - (OUTSTART * 2)];
		double[] AVG_S2_trim = null;
		if(STRAND == 0) AVG_S2_trim = new double[AVG_S1_trim.length];
		double[] DOMAIN_trim = new double[AVG_S1_trim.length];
		for(int i = OUTSTART; i < AVG_S1.length - OUTSTART; i++) {
			if(AVG_S2 != null) {
				printPS(DOMAIN[i] + "\t" + AVG_S1[i] + "\t" + AVG_S2[i]);
				AVG_S2_trim[i - OUTSTART] = AVG_S2[i];
			}
			else { printPS(DOMAIN[i] + "\t" + AVG_S1[i]); }
			AVG_S1_trim[i - OUTSTART] = AVG_S1[i];
			DOMAIN_trim[i - OUTSTART] = DOMAIN[i];
		}
		AVG_S1 = AVG_S1_trim;
		AVG_S2 = AVG_S2_trim;
		DOMAIN = DOMAIN_trim;
		
		//Output composite data file setup
		if(PARAM.getOutputCompositeStatus()) {
			try { COMPOSITE = new PrintStream(PARAM.getCompositeFile());
			} catch (FileNotFoundException e) {	e.printStackTrace(); }
		}
		
		//Output composite data to tab-delimited file
		if(COMPOSITE != null) {
			for(int a = 0; a < DOMAIN.length; a++) {
				COMPOSITE.print("\t" + DOMAIN[a]);
			}
			COMPOSITE.println();
			if(STRAND == 0) {
				COMPOSITE.print(generateFileName(BED.getName(), BAM.getName(), 0));
				for(int a = 0; a < AVG_S1.length; a++) {
					COMPOSITE.print("\t" + AVG_S1[a]);
				}
				COMPOSITE.println();
				COMPOSITE.print(generateFileName(BED.getName(), BAM.getName(), 1));
				for(int a = 0; a < AVG_S2.length; a++) {
					COMPOSITE.print("\t" + AVG_S2[a]);
				}
				COMPOSITE.println();
			} else {
				COMPOSITE.print(generateFileName(BED.getName(), BAM.getName(), 2));
				for(int a = 0; a < AVG_S1.length; a++) {
					COMPOSITE.print("\t" + AVG_S1[a]);
				}
				COMPOSITE.println();
			}			
		}
		
		//Make composite plots
		if(GUI){
			if(STRAND == 0){ compositePlot = CompositePlot.createCompositePlot(DOMAIN, AVG_S1, AVG_S2, BED.getName(), PARAM.getColors()); }
			else{ compositePlot = CompositePlot.createCompositePlot(DOMAIN, AVG_S1, BED.getName(), PARAM.getColors()); }
		}
		
		//Output JTV files				
		if(OUT_S1 != null && PARAM.getOutputType() == 2) {
			if(PARAM.getOutputJTV()) {
				if(STRAND == 0) JTVOutput.outputJTV(PARAM.getOutput() + File.separator + generateFileName(BED.getName(), BAM.getName(), 0), PARAM.getSenseColor());
				else JTVOutput.outputJTV(PARAM.getOutput() + File.separator + generateFileName(BED.getName(), BAM.getName(), 2), PARAM.getCombinedColor());
			}
			OUT_S1.close();
		}
		if(OUT_S2 != null && PARAM.getOutputType() == 2){
			if(PARAM.getOutputJTV()) { JTVOutput.outputJTV(PARAM.getOutput() + File.separator + generateFileName(BED.getName(), BAM.getName(), 1), PARAM.getAntiColor()); }
			OUT_S2.close();
		}
		
	}
	
	//Get size of largest array for composite generation
	public static int getMaxBEDSize(Vector<BEDCoord> sites) {
		int maxSize = 0;
		for(int x = 0; x < sites.size(); x++) {
			int SIZE = sites.get(x).getFStrand().length;
			if(SIZE > maxSize) {
				maxSize = SIZE;
			}
		}
		return maxSize;
	}
	
	//Validate BED coordinates exist within BAM file and satisfy BED format
	public Vector<BEDCoord> validateBED(Vector<BEDCoord> COORD, File BAM) throws IOException {
		Vector<BEDCoord> FINAL = new Vector<BEDCoord>();
		ArrayList<Integer> indexFail = new ArrayList<Integer>();
		
		//Get chromosome IDs
		SamReader inputSam = SamReaderFactory.makeDefault().open(BAM);
		AbstractBAMFileIndex bai = (AbstractBAMFileIndex) inputSam.indexing().getIndex();
		ArrayList<String> chrom = new ArrayList<String>();
		for(int x = 0; x < bai.getNumberOfReferences(); x++) {
			chrom.add(inputSam.getFileHeader().getSequence(x).getSequenceName());
		}
		inputSam.close();
		bai.close();

		//check each BED coordinate...
		for(int x = 0; x < COORD.size(); x++) {
			//check for	(1) bed chrom that aren't in BAM file OR
			//			(2) starts smaller than the stop
			if( (!chrom.contains(COORD.get(x).getChrom())) || (COORD.get(x).getStart() > COORD.get(x).getStop()) ) {
				if(!indexFail.contains(new Integer(x))) {
					indexFail.add(new Integer(x));
				}
			}
		}
		
		if(indexFail.size() == COORD.size()) {
			System.err.println("No BED Coordinates exist within BAM file!!!");
		}
		
		//Create new input file without failed indexes to more efficiently use CPUs
		for(int x = 0; x < COORD.size(); x++) {
			if(!indexFail.contains(new Integer(x))) {
				FINAL.add(COORD.get(x));
			}
		}
		
		return FINAL;
	}
	
	public String generateFileName(String bed, String bam, int strandnum) {
		String[] bedname = bed.split("\\.");
		String[] bamname = bam.split("\\.");
		
		String read = "read1";
		if(PARAM.getRead() == 1) { read = "read2"; }
		else if(PARAM.getRead() == 2) { read = "readc"; }
		
		return(generateFileName(bedname[0] + "_" + bamname[0] + "_" + read, strandnum));
	}
	
	public String generateFileName(String basename, int strandnum) {
		String strand = "sense";
		if(strandnum == 1) { strand = "anti"; }
		else if(strandnum == 2) { strand = "combined"; }
		
		String filename = basename + "_" + strand;
		if(PARAM.getOutputType() == 1) { filename += ".tab"; }
		else { filename += ".cdt"; }
		
		if(PARAM.getOutputGZIP()) { filename += ".gz"; }
		
		return filename;
	}
		
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
		
    public Vector<BEDCoord> loadCoord(File INPUT) throws FileNotFoundException {
		Scanner scan = new Scanner(INPUT);
		Vector<BEDCoord> COORD = new Vector<BEDCoord>();
		while (scan.hasNextLine()) {
			String[] temp = scan.nextLine().split("\t");
			if(temp.length > 2) { 
				if(!temp[0].contains("track") && !temp[0].contains("#")) {
					String name = "";
					if(temp.length > 3) { name = temp[3]; }
					else { name = temp[0] + "_" + temp[1] + "_" + temp[2]; }
					if(Integer.parseInt(temp[1]) >= 0) {
						if(temp.length > 4) { 
							if(temp[5].equals("+")) { COORD.add(new BEDCoord(temp[0], Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), "+", name)); }
							else { COORD.add(new BEDCoord(temp[0], Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), "-", name)); }
						} else { COORD.add(new BEDCoord(temp[0], Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), "+", name)); }

					} else {
						System.err.println("Invalid Coordinate in File!!!\n" + Arrays.toString(temp));
					}
				}
			}
		}
		scan.close();
		return COORD;
    }
    
    public Component getCompositePlot(){ return(compositePlot); }
    
    private void printPS(String line){
    	if(PS!=null){ PS.println(line); }
    	else{ System.err.println(line); }
    }
    
}
