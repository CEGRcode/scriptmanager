package scripts.Tag_Analysis;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLayeredPane;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import charts.CompositePlot;
import objects.BEDCoord;
import objects.PileupParameters;
import scripts.Tag_Analysis.PileupScripts.PileupExtract;
import scripts.Tag_Analysis.PileupScripts.TransformArray;
import util.BAMUtilities;
import util.JTVOutput;

@SuppressWarnings("serial")
public class TagPileup extends JFrame {
	Vector<File> BEDFiles = null;
	Vector<File> BAMFiles = null;
	
	PileupParameters PARAM = null;
	
	private int STRAND = 0;
	private int CPU = 1;
	
	PrintStream COMPOSITE = null;
	PrintStream OUT_S1 = null;
	PrintStream OUT_S2 = null;
	
	final JLayeredPane layeredPane;
	final JTabbedPane tabbedPane;
	final JTabbedPane tabbedPane_Scatterplot;
	final JTabbedPane tabbedPane_Statistics;
	
	//TagPileup pile = new TagPileup(INPUT, BAMFiles.get(x), OUTPUT, READ, STRAND, SHIFT, BIN);
	public TagPileup(Vector<File> be, Vector<File> ba, PileupParameters param) {
		setTitle("Tag Pileup Composite");
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

		BEDFiles = be;
		BAMFiles = ba;
		PARAM = param;
		STRAND = param.getStrand();
		CPU = param.getCPU();
	}
	
	public void run() throws IOException {
		if(PARAM.getOutputCompositeStatus()) {
			try { COMPOSITE = new PrintStream(PARAM.getOutput() + File.separator + PARAM.getCompositeFile());
			} catch (FileNotFoundException e) {	e.printStackTrace(); }
		}
		for(int z = 0; z < BAMFiles.size(); z++) {
			File BAM = BAMFiles.get(z);	//Pull current BAM file
			File f = new File(BAM + ".bai"); //Generate file name for BAI index file
			//Check if BAI index file exists
			if(!f.exists() || f.isDirectory()) { JOptionPane.showMessageDialog(null, "BAI Index File does not exist for: " + BAM.getName()); }
			else {
				
				//Code to standardize tags sequenced to genome size (1 tag / 1 bp)
				if(PARAM.getStandard()) { PARAM.setRatio(BAMUtilities.calculateStandardizationRatio(BAM)); }
								
				for(int BED_Index = 0; BED_Index < BEDFiles.size(); BED_Index++) {
					JTextArea STATS = new JTextArea(); //Generate statistics object
					STATS.setEditable(false); //Make it un-editable
					STATS.append(getTimeStamp() + "\n"); //Timestamp process
					STATS.append(BAM.getName() + "\n"); //Label stat object with what BAM file is generating it

					if(PARAM.getOutputType() != 0) {
						if(STRAND == 0) {
							try { OUT_S1 = new PrintStream(PARAM.getOutput() + File.separator + generateFileName(BEDFiles.get(BED_Index).getName(), BAM.getName(), 0));
							OUT_S2 = new PrintStream(PARAM.getOutput() + File.separator + generateFileName(BEDFiles.get(BED_Index).getName(), BAM.getName(), 1));
							} catch (FileNotFoundException e) {	e.printStackTrace(); }
						} else {
							try { OUT_S1 = new PrintStream(PARAM.getOutput() + File.separator + generateFileName(BEDFiles.get(BED_Index).getName(), BAM.getName(), 2));
							} catch (FileNotFoundException e) {	e.printStackTrace(); }
						}
					}
					
					Vector<BEDCoord> INPUT = loadCoord(BEDFiles.get(BED_Index));
										
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
						DOMAIN[i] = (double)((temp - (AVG_S1.length - i)) * PARAM.getBin()) + 1;
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
						else { STATS.append(DOMAIN[i] + "\t" + AVG_S1[i] + "\n"); }
						AVG_S1_trim[i - OUTSTART] = AVG_S1[i];
						DOMAIN_trim[i - OUTSTART] = DOMAIN[i];
					}
					AVG_S1 = AVG_S1_trim;
					AVG_S2 = AVG_S2_trim;
					DOMAIN = DOMAIN_trim;
					
					//Output composite data to tab-delimited file
					if(COMPOSITE != null) {
						for(int a = 0; a < DOMAIN.length; a++) {
							COMPOSITE.print("\t" + DOMAIN[a]);
						}
						COMPOSITE.println();
						if(STRAND == 0) {
							COMPOSITE.print(generateFileName(BEDFiles.get(BED_Index).getName(), BAM.getName(), 0));
							for(int a = 0; a < AVG_S1.length; a++) {
								COMPOSITE.print("\t" + AVG_S1[a]);
							}
							COMPOSITE.println();
							COMPOSITE.print(generateFileName(BEDFiles.get(BED_Index).getName(), BAM.getName(), 1));
							for(int a = 0; a < AVG_S2.length; a++) {
								COMPOSITE.print("\t" + AVG_S2[a]);
							}
							COMPOSITE.println();
						} else {
							COMPOSITE.print(generateFileName(BEDFiles.get(BED_Index).getName(), BAM.getName(), 2));
							for(int a = 0; a < AVG_S1.length; a++) {
								COMPOSITE.print("\t" + AVG_S1[a]);
							}
							COMPOSITE.println();
						}			
					}
					
					if(STRAND == 0) tabbedPane_Scatterplot.add(BAM.getName(), CompositePlot.createCompositePlot(DOMAIN, AVG_S1, AVG_S2, BEDFiles.get(BED_Index).getName(), PARAM.getColors()));
					else tabbedPane_Scatterplot.add(BAM.getName(), CompositePlot.createCompositePlot(DOMAIN, AVG_S1, BEDFiles.get(BED_Index).getName(), PARAM.getColors()));
										
					if(OUT_S1 != null && PARAM.getOutputType() == 2) {
						if(STRAND == 0) JTVOutput.outputJTV(PARAM.getOutput() + File.separator + generateFileName(BEDFiles.get(BED_Index).getName(), BAM.getName(), 0), PARAM.getSenseColor());
						else JTVOutput.outputJTV(PARAM.getOutput() + File.separator + generateFileName(BEDFiles.get(BED_Index).getName(), BAM.getName(), 2), PARAM.getCombinedColor());
						OUT_S1.close();
					}
					if(OUT_S2 != null && PARAM.getOutputType() == 2){
						JTVOutput.outputJTV(PARAM.getOutput() + File.separator + generateFileName(BEDFiles.get(BED_Index).getName(), BAM.getName(), 1), PARAM.getAntiColor());
						OUT_S2.close();
					}
					STATS.setCaretPosition(0);
					JScrollPane newpane = new JScrollPane(STATS, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					tabbedPane_Statistics.add(BAM.getName(), newpane);
			        firePropertyChange("tag", (z + 1) * (BED_Index + 1), ((z + 1) * (BED_Index + 1)) + 1);								
				}
			}
		}		
	}
	
	public String generateFileName(String bed, String bam, int strandnum) {
		String[] bedname = bed.split("\\.");
		String[] bamname = bam.split("\\.");
		
		String strand = "sense";
		if(strandnum == 1) strand = "anti";
		else if(strandnum == 2) strand = "combined";
		String read = "read1";
		if(PARAM.getRead() == 1) read = "read2";
		else if(PARAM.getRead() == 2) read = "readc";
		
		String filename = bedname[0] + "_" + bamname[0] + "_" + read + "_" + strand;
		if(PARAM.getOutputType() == 1) filename += ".tab";
		else filename += ".cdt";
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
						System.out.println("Invalid Coordinate in File!!!\n" + Arrays.toString(temp));
					}
				}
			}
		}
		scan.close();
		return COORD;
    }
}
