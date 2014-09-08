package scripts;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.util.CloseableIterator;

import javax.swing.JLayeredPane;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import charts.CompositePlot;
import objects.BEDCoord;

@SuppressWarnings("serial")
public class TagPileup extends JFrame {
	Vector<BEDCoord> INPUT = null;
	Vector<File> BAMFiles = null;
	File OUTPUT = null;
	private int READ = 0;
	private int STRAND = 0;
	private int TRANS = 0;
	private int SHIFT = 0;
	private int BIN = 1;
	private int SMOOTH = 0;
	private int STDSIZE = 0;
	private int STDNUM = 0;
	
	SAMFileReader inputSam;
	PrintStream OUT_S1 = null;
	PrintStream OUT_S2 = null;
	
	final JLayeredPane layeredPane;
	final JTabbedPane tabbedPane;
	final JTabbedPane tabbedPane_Scatterplot;
	final JTabbedPane tabbedPane_Statistics;
	
	//TagPileup pile = new TagPileup(INPUT, BAMFiles.get(x), OUTPUT, READ, STRAND, SHIFT, BIN);
	public TagPileup(Vector<BEDCoord> in, Vector<File> ba, File o, int r, int stra, int shif, int bi, int tra, int smo, int size, int num) {
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
		OUTPUT = o;
		READ = r;
		STRAND = stra;
		SHIFT = shif;
		BIN = bi;
		
		TRANS = tra;
		SMOOTH = smo;
		STDSIZE = size;
		STDNUM = num;
	}
	
	public void run() {
			
		for(int z = 0; z < BAMFiles.size(); z++) {
			//Pull first BAM file
			File BAM = BAMFiles.get(z);
			
			//Generate TimeStamp
			String time = getTimeStamp();
			
			if(OUTPUT != null) {
				if(STRAND == 0) {
					try { OUT_S1 = new PrintStream(OUTPUT + File.separator + generateFileName(BAM.getName(), 0));
					OUT_S2 = new PrintStream(OUTPUT + File.separator + generateFileName(BAM.getName(), 1));
					} catch (FileNotFoundException e) {	e.printStackTrace(); }
				} else {
					try { OUT_S1 = new PrintStream(OUTPUT + File.separator + generateFileName(BAM.getName(), 2));
					} catch (FileNotFoundException e) {	e.printStackTrace(); }
				}
			}
			if(OUT_S1 != null) OUT_S1.println(time);
			if(OUT_S2 != null) OUT_S2.println(time);
			
			JTextArea STATS = new JTextArea();
			STATS.setEditable(false);
			STATS.append(time + "\n");

			double[] AVG_S1 = null;
			double[] AVG_S2 = null;
			double[] DOMAIN = null;
			
			double[] TAG_S1 = null;
			double[] TAG_S2 = null;
			double COUNT = 0;
			
			File f = new File(BAM + ".bai");
			//Check if BAI index file exists
			if(f.exists() && !f.isDirectory()) {
				if(OUT_S1 != null) OUT_S1.println(BAM.getName() + "_sense");
				if(OUT_S2 != null) OUT_S2.println(BAM.getName() + "_anti");
				STATS.append(BAM.getName() + "\n");
				
				inputSam = new SAMFileReader(BAM, new File(BAM + ".bai"));
				for(int x = 0; x < INPUT.size(); x++) {
					BEDCoord read = INPUT.get(x);
					TAG_S1 = new double[read.getStop() - read.getStart()];
					if(STRAND == 0) TAG_S2 = new double[read.getStop() - read.getStart()];
					
					//Keep track of average profile for composite
					if(AVG_S1 == null) { 
						AVG_S1 = new double[TAG_S1.length];
						if(STRAND == 0) AVG_S2 = new double[TAG_S2.length];
					}
					
					//FETCH coordinate start minus shift to stop plus shift
					//SAMRecords are 1-based
					CloseableIterator<SAMRecord> iter = inputSam.query(read.getChrom(), read.getStart() - SHIFT - 1, read.getStop() + SHIFT + 1, false);
					while (iter.hasNext()) {
						//Create the record object 
						SAMRecord sr = iter.next();
						
						//Must be PAIRED-END mapped, mate must be mapped, must be read1
						if(sr.getReadPairedFlag()) {
							if(sr.getProperPairFlag()) {
								if((sr.getFirstOfPairFlag() && READ == 0) || (!sr.getFirstOfPairFlag() && READ == 1) || READ == 2) {
									int FivePrime = sr.getUnclippedStart() - 1;
									if(sr.getReadNegativeStrandFlag()) { 
										FivePrime = sr.getUnclippedEnd();
										//SHIFT DATA HERE IF NECCESSARY
										FivePrime -= SHIFT;
									} else { FivePrime += SHIFT; }
									FivePrime -= read.getStart();
									//FivePrime = filterRead(FivePrime, sr.getReadNegativeStrandFlag(), read.getDir());
									
			                        //Increment Final Array keeping track of pileup
									if(FivePrime >= 0 && FivePrime < TAG_S1.length) {
										if(STRAND == 0) {
											if(!sr.getReadNegativeStrandFlag() && read.getDir().equals("-")) { TAG_S2[FivePrime] += 1; }
								        	else if(sr.getReadNegativeStrandFlag() && read.getDir().equals("+")) { TAG_S2[FivePrime] += 1; }
								        	else if(!sr.getReadNegativeStrandFlag() && read.getDir().equals("+")) { TAG_S1[FivePrime] += 1; }
								        	else if(sr.getReadNegativeStrandFlag() && read.getDir().equals("-")) { TAG_S1[FivePrime] += 1;}
										} else {
											TAG_S1[FivePrime] += 1;
										}
									}
								}
							}
						} else if(READ == 0 || READ == 2) {
							//Also outputs if not paired-end since by default it is read-1
							int FivePrime = sr.getUnclippedStart() - 1;
							if(sr.getReadNegativeStrandFlag()) { 
								FivePrime = sr.getUnclippedEnd();
								//SHIFT DATA HERE IF NECCESSARY
								FivePrime -= SHIFT;
							} else { FivePrime += SHIFT; }
							FivePrime -= read.getStart();
							//FivePrime = filterRead(FivePrime, sr.getReadNegativeStrandFlag(), read.getDir());                       
	                        //Increment Final Array keeping track of pileup
	                        if(FivePrime >= 0 && FivePrime < TAG_S1.length) {
	                        	if(STRAND == 0) {
	                        		if(!sr.getReadNegativeStrandFlag() && read.getDir().equals("-")) { TAG_S2[FivePrime] += 1; }
						        	else if(sr.getReadNegativeStrandFlag() && read.getDir().equals("+")) { TAG_S2[FivePrime] += 1; }
						        	else if(!sr.getReadNegativeStrandFlag() && read.getDir().equals("+")) { TAG_S1[FivePrime] += 1; }
						        	else if(sr.getReadNegativeStrandFlag() && read.getDir().equals("-")) { TAG_S1[FivePrime] += 1;}
								} else {
									TAG_S1[FivePrime] += 1;
								}
	                        }
						}
						if(read.getDir().equals("-")) { 
							TransformArray.reverseTran(TAG_S1);
							TransformArray.reverseTran(TAG_S2);
						}
					}
					iter.close();
					
					if(OUT_S1 != null) { OUT_S1.print(read.getName()); }
					if(OUT_S2 != null) { OUT_S2.print(read.getName()); }
					for(int i = 0; i < TAG_S1.length; i++) {
						if(OUT_S1 != null) {
							OUT_S1.print("\t" + TAG_S1[i]);
							if(i < AVG_S1.length) { AVG_S1[i] += TAG_S1[i]; }
						}
						if(OUT_S2 != null) {
							OUT_S2.print("\t" + TAG_S2[i]);
							if(i < AVG_S2.length) { AVG_S2[i] += TAG_S2[i]; }
						}
					}
					if(OUT_S1 != null) { OUT_S1.println(); }
					if(OUT_S2 != null) { OUT_S2.println(); }
					COUNT++;
				}
				inputSam.close();
				
				DOMAIN = new double[AVG_S1.length];
				for(int i = 0; i < AVG_S1.length; i++) {
					if(COUNT != 0) { AVG_S1[i] /= COUNT; }
					if(AVG_S2 != null && COUNT != 0) { AVG_S2[i] /= COUNT; }
					DOMAIN[i] = (double)((AVG_S1.length / 2) - (AVG_S1.length - i));
					if(AVG_S2 != null) STATS.append(DOMAIN[i] + "\t" + AVG_S1[i] + "\t" + AVG_S2[i] + "\n");
					else STATS.append(DOMAIN[i] + "\t" + AVG_S1[i] + "\n");
				}
			} else {
				if(OUT_S1 != null) OUT_S1.println("BAI Index File does not exist for: " + BAM.getName() + "\n");
				if(OUT_S2 != null) OUT_S2.println("BAI Index File does not exist for: " + BAM.getName() + "\n");
				STATS.append("BAI Index File does not exist for: " + BAM.getName() + "\n\n");
			}
			
			STATS.setCaretPosition(0);
			JScrollPane newpane = new JScrollPane(STATS, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			tabbedPane_Statistics.add(BAM.getName(), newpane);
			if(STRAND == 0) tabbedPane_Scatterplot.add(BAM.getName(), CompositePlot.createCompositePlot(DOMAIN, AVG_S1, AVG_S2));
			else tabbedPane_Scatterplot.add(BAM.getName(), CompositePlot.createCompositePlot(DOMAIN, AVG_S1));
			if(OUT_S1 != null) OUT_S1.close();
			if(OUT_S2 != null) OUT_S2.close();
			
	        firePropertyChange("tag", z, z + 1);
		}
		
	}
	
	private int filterRead(int coord, boolean Readstrand, String CoordDir) {
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
	}
	
	public String generateFileName(String origin, int strandnum) {
		String[] name = origin.split("\\.");
		
		String strand = "sense";
		if(strandnum == 1) strand = "anti";
		else if(strandnum == 2) strand = "combined";
		String read = "read1";
		if(READ == 1) strand = "read2";
		else if(READ == 2) strand = "readc";
		
		String filename = name[0] + "_" + read + "_" + strand + ".tab";
		return filename;
	}
		
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}
