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
	private int SHIFT = 0;
	private int BIN = 1;
	
	SAMFileReader inputSam;
	PrintStream OUT = null;
	
	final JLayeredPane layeredPane;
	final JTabbedPane tabbedPane;
	final JTabbedPane tabbedPane_Scatterplot;
	final JTabbedPane tabbedPane_Statistics;
	
	//TagPileup pile = new TagPileup(INPUT, BAMFiles.get(x), OUTPUT, READ, STRAND, SHIFT, BIN);
	public TagPileup(Vector<BEDCoord> in, Vector<File> ba, File o, int r, int stra, int shif, int bi) {
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
	}
	
	public void run() {
		//Generate TimeStamp
		String time = getTimeStamp();
		
		for(int z = 0; z < BAMFiles.size(); z++) {
			File BAM = BAMFiles.get(z);
			
			if(OUTPUT != null) {
				try { OUT = new PrintStream(OUTPUT + File.separator + generateFileName(BAM.getName()));
				} catch (FileNotFoundException e) {	e.printStackTrace(); }
			}
			if(OUT != null) OUT.println(time);
			
			JTextArea STATS = new JTextArea();
			STATS.setEditable(false);
			STATS.append(time + "\n");

			double[] AVG = null;
			double[] DOMAIN = null;
			double COUNT = 0;
			
			File f = new File(BAM + ".bai");
			//Check if BAI index file exists
			if(f.exists() && !f.isDirectory()) {
				if(OUT != null) OUT.println(BAM.getName());
				STATS.append(BAM.getName() + "\n");
				
				inputSam = new SAMFileReader(BAM, new File(BAM + ".bai"));
				for(int x = 0; x < INPUT.size(); x++) {
					BEDCoord read = INPUT.get(x);
					double[] TAG = new double[read.getStop() - read.getStart()];
					
					//Keep track of average profile for composite
					if(AVG == null) { AVG = new double[TAG.length];	}
					
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
									FivePrime = filterRead(FivePrime, sr.getReadNegativeStrandFlag(), read.getDir());                       
			                        //Increment Final Array keeping track of pileup
			                        if(FivePrime >= 0 && FivePrime < TAG.length) { TAG[FivePrime] += 1; }
								}
							}
						} else if(READ == 0 || READ == 2){
							//Also outputs if not paired-end since by default it is read-1
							int FivePrime = sr.getUnclippedStart() - 1;
							if(sr.getReadNegativeStrandFlag()) { 
								FivePrime = sr.getUnclippedEnd();
								//SHIFT DATA HERE IF NECCESSARY
								FivePrime -= SHIFT;
							} else { FivePrime += SHIFT; }
							FivePrime -= read.getStart();
							FivePrime = filterRead(FivePrime, sr.getReadNegativeStrandFlag(), read.getDir());                       
	                        //Increment Final Array keeping track of pileup
	                        if(FivePrime >= 0 && FivePrime < TAG.length) { TAG[FivePrime] += 1; }
						}
						if(read.getDir().equals("-")) { reverse(TAG); }
						
					}
					iter.close();
					
					if(OUT != null) { OUT.print(read.getName()); }
					for(int i = 0; i < TAG.length; i++) {
						if(OUT != null) { OUT.print("\t" + TAG[i]); }
						if(i < AVG.length) { AVG[i] += TAG[i]; }
					}
					if(OUT != null) { OUT.println(); }
					COUNT++;
				}
				inputSam.close();
				
				DOMAIN = new double[AVG.length];
				for(int i = 0; i < AVG.length; i++) {
					if(COUNT != 0) { AVG[i] /= COUNT; }
					DOMAIN[i] = (double)((AVG.length / 2) - (AVG.length - i));
					STATS.append(DOMAIN[i] + "\t" + AVG[i] + "\n");
				}
				
			} else {
				if(OUT != null) OUT.println("BAI Index File does not exist for: " + BAM.getName() + "\n");
				STATS.append("BAI Index File does not exist for: " + BAM.getName() + "\n\n");
			}
			
			STATS.setCaretPosition(0);
			JScrollPane newpane = new JScrollPane(STATS, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			tabbedPane_Statistics.add(BAM.getName(), newpane);
			tabbedPane_Scatterplot.add(BAM.getName(), CompositePlot.createCompositePlot(DOMAIN, AVG));
			if(OUT != null) OUT.close();
			
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
	
//	private double[] gaussTran(double[] orig) {
//		double[] Garray = new double[orig.length];
//		int window = (Parameters.getSDSize() * Parameters.getSDNum()) / Parameters.getResolution();
//		double SDSize = (double)Parameters.getSDSize();
//		for(int x = 0; x < orig.length; x++) {
//             double score = 0;
//             double weight = 0;
//             for(int y = x - window; y <= x + window; y++) {
//                    if(y < 0) y = -1;
//                    else if(y < orig.length) {
//                    	double HEIGHT = Math.exp(-1 * Math.pow((y - x), 2) / (2 * Math.pow(SDSize, 2)));
//                    	score += (HEIGHT * orig[y]);
//                    	weight += HEIGHT;
//                    } else y = x + window + 1;
//             }
//             if(weight != 0) Garray[x] = score / weight;
//		}
//		return Garray;
//     }
	
	public String generateFileName(String origin) {
		String[] name = origin.split("\\.");
		
		String strand = "sense";
		if(STRAND == 1) strand = "anti";
		else if(STRAND == 2) strand = "combined";
		String read = "read1";
		if(READ == 1) strand = "read2";
		else if(READ == 2) strand = "readc";
		
		String filename = name[0] + "_" + read + "_" + strand + ".tab";
		return filename;
	}
	
	public static void reverse(double[] data) {
		if(data != null) {
			int x = 0;
			int y = data.length - 1;
			double temp;
			while (y > x) {
				temp = data[y];
				data[y] = data[x];
				data[x] = temp;
				y--;
				x++;
			}
		}
	}
	
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}
