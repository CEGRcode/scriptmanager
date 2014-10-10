package scripts;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sf.samtools.AbstractBAMFileIndex;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMSequenceRecord;
import net.sf.samtools.util.CloseableIterator;
import objects.Peak;

@SuppressWarnings("serial")
public class GeneTrack extends JFrame {
	private JTextArea textArea;
	
	private File INPUT = null;
	private PrintStream OUT = null;
	private SAMFileReader inputSam = null;
	
	private int READ = 0;
	
	private int SIGMA = 5;
	private int EXCLUSION = 20;
	private int UP_WIDTH = 10;
	private int DOWN_WIDTH = 10;
	private int FILTER = 1;
	
	//Arbitrarily set to 5 std deviations up and down for gaussian kernel smoothing
	private int NUM_STD = 5;
	private double[] gaussWeight;
	private int WIDTH;
	
	//Arbitrarily set windowSize to 10,000bp in order to chop up the genome efficiently
	private int windowSize = 10000;
	
	//Array to contain genetrack peaks separated by strand
	private ArrayList<Peak> FPEAKS = null;
	private ArrayList<Peak> RPEAKS = null;
	
	private double[] F_GOCC;
	private double[] R_GOCC;
	private double[] F_TOCC;
	private double[] R_TOCC;
	private double[] F_STD;
	private double[] R_STD;
	
	public GeneTrack(File in, int r, int s, int e, int u, int d, int f) {
		setTitle("BAM to Genetrack Progress");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		INPUT = in;
		READ = r;
		SIGMA = s;
		EXCLUSION = e;
		UP_WIDTH = u;
		DOWN_WIDTH = d;
		FILTER = f;
	}
	
	public void run() {
		String TIME = getTimeStamp();;
		String PARAM = "s" + SIGMA + "e" + EXCLUSION;
		String READNAME = "READ1";
		if(READ == 1) READNAME = "READ2";
		else if(READ == 2) READNAME = "COMBINED";
		String NAME = INPUT.getName().split("\\.")[0] + "_" + READNAME + "_" + PARAM + ".gff";
		textArea.append(TIME + "\n" + NAME + "\n");
		textArea.append("Sigma: " + SIGMA + "\nExclusion: " + EXCLUSION + "\nFilter: " + FILTER + "\nUpstream width of called Peaks: " + UP_WIDTH + "\nDownstream width of called Peaks: " + DOWN_WIDTH + "\n");
		
		try { OUT = new PrintStream(new File(NAME)); }
		catch (FileNotFoundException e) { e.printStackTrace(); }

		//Set genetrack parameters
		gaussWeight = gaussKernel();
		WIDTH = SIGMA * NUM_STD;
				
		File f = new File(INPUT.getAbsolutePath() + ".bai");
		if(f.exists() && !f.isDirectory()) {	
			inputSam = new SAMFileReader(INPUT, new File(INPUT.getAbsolutePath() + ".bai"));
			AbstractBAMFileIndex bai = (AbstractBAMFileIndex) inputSam.getIndex();
			
			for(int numchrom = 0; numchrom < bai.getNumberOfReferences(); numchrom++) {
				SAMSequenceRecord seq = inputSam.getFileHeader().getSequence(numchrom);
				System.out.println("Processing: " + seq.getSequenceName());
				textArea.append("Processing: " + seq.getSequenceName() + "\n");
				
				FPEAKS = new ArrayList<Peak>();
				RPEAKS = new ArrayList<Peak>();
				
				int numwindows = (int) (seq.getSequenceLength() / windowSize);
				for(int x = 0; x < numwindows; x++) {
					int start = x * windowSize;
					int stop = start + windowSize + WIDTH;
					
					F_GOCC = new double[windowSize];
					R_GOCC = new double[windowSize];
					F_TOCC = new double[windowSize];
					R_TOCC = new double[windowSize];
					F_STD = new double[windowSize];
					R_STD = new double[windowSize];
					
					CloseableIterator<SAMRecord> iter = inputSam.query(seq.getSequenceName(), start, stop, false);
					loadGenomeFragment(iter, start, stop);
					iter.close();
					
					filterbyLocalMaxima(seq.getSequenceName(), start);
				}
				int finalstart = numwindows * 10000;
				int finalstop = seq.getSequenceLength();
				F_GOCC = new double[finalstop - finalstart];
				R_GOCC = new double[finalstop - finalstart];
				F_TOCC = new double[finalstop - finalstart];
				R_TOCC = new double[finalstop - finalstart];
				
				CloseableIterator<SAMRecord> iter = inputSam.query(seq.getSequenceName(), finalstart, finalstop, false);
				loadGenomeFragment(iter, finalstart, finalstop);
				iter.close();
				
				filterbyLocalMaxima(seq.getSequenceName(), finalstart);
				
				for(int z = 0; z < FPEAKS.size(); z++) {
					OUT.println(FPEAKS.get(z).toString());		
				}
				for(int z = 0; z < RPEAKS.size(); z++) {
					OUT.println(RPEAKS.get(z).toString());		
				}
				
			}
			inputSam.close();
			bai.close();
			OUT.close();
		} else {
			textArea.append("BAI Index File does not exist for: " + INPUT.getName() + "\n");
			OUT.println("BAI Index File does not exist for: " + INPUT.getName() + "\n");
		}
		dispose();
	}
	
	public void filterbyLocalMaxima(String chrom, int start) {
		for(int z = 0; z < F_GOCC.length; z++) {
			int fiveprime = z + start - UP_WIDTH;
			int threeprime = z + start + DOWN_WIDTH;
			if(fiveprime < 1) { fiveprime = 1; }
			if(threeprime < 1) { threeprime = 1; }
			
			if(z == 0) {
				if(F_GOCC[z] >= F_GOCC[z + 1] && F_TOCC[z] > FILTER) { 
					FPEAKS.add(new Peak(chrom, fiveprime, threeprime, "+", (int)F_TOCC[z], F_STD[z]));
				}
				if(R_GOCC[z] >= R_GOCC[z + 1] && R_TOCC[z] > FILTER) {
					RPEAKS.add(new Peak(chrom, fiveprime, threeprime, "-", (int)R_TOCC[z], R_STD[z]));
				}
			} else if(z + 1 == F_GOCC.length) {
				if(F_GOCC[z] >= F_GOCC[z - 1] && F_TOCC[z] > FILTER) {
					FPEAKS.add(new Peak(chrom, fiveprime, threeprime, "+", (int)F_TOCC[z], F_STD[z]));
				}
				if(R_GOCC[z] >= R_GOCC[z - 1] && R_TOCC[z] > FILTER) {
					RPEAKS.add(new Peak(chrom, fiveprime, threeprime, "-", (int)R_TOCC[z], R_STD[z]));
				}
			} else {
				if(F_GOCC[z] >= F_GOCC[z + 1] && F_GOCC[z] > F_GOCC[z - 1] && F_TOCC[z] > FILTER) {
					FPEAKS.add(new Peak(chrom, fiveprime, threeprime, "+", (int)F_TOCC[z], F_STD[z]));
				}
				if(R_GOCC[z] >= R_GOCC[z + 1] && R_GOCC[z] > R_GOCC[z - 1] && R_TOCC[z] > FILTER) {
					RPEAKS.add(new Peak(chrom, fiveprime, threeprime, "-", (int)R_TOCC[z], R_STD[z]));
				}
			}
			//int bp = z + start + 1;
			//OUT.println(seq.getSequenceName() + "\t" + bp + "\t" + F_GOCC[z] + "\t" + R_GOCC[z] + "\t" + F_TOCC[z] + "\t" + R_TOCC[z]);		
		}
	}
	
	public void loadGenomeFragment(CloseableIterator<SAMRecord> iter, int start, int stop) {
		double[] tempF = new double[F_TOCC.length];
		double[] tempR = new double[R_TOCC.length];
		
		while (iter.hasNext()) {
			//Create the record object 
			SAMRecord sr = iter.next();
			
			int recordStart = -999;
			
			//Check for paired-end
			if(sr.getReadPairedFlag()) {
				//Must be PAIRED-END mapped, mate must be mapped, must be read 1
				if(sr.getProperPairFlag() && sr.getFirstOfPairFlag() && (READ == 0 || READ == 2)) {
					//Get the start of the record 
					recordStart = sr.getUnclippedStart();
					//Accounts for reverse tag reporting 3' end of tag and converting BAM to IDX/GFF format
					if(sr.getReadNegativeStrandFlag()) { recordStart = sr.getUnclippedEnd(); }
				} else if(sr.getProperPairFlag() && !sr.getFirstOfPairFlag() && (READ == 1 || READ == 2)) {
					//Get the start of the record 
					recordStart = sr.getUnclippedStart();
					//Accounts for reverse tag reporting 3' end of tag and converting BAM to IDX/GFF format
					if(sr.getReadNegativeStrandFlag()) { recordStart = sr.getUnclippedEnd(); }
				} 
			} else if(READ == 0 || READ == 2) {
				//Get the start of the record 
				recordStart = sr.getUnclippedStart();
				//Accounts for reverse tag reporting 3' end of tag and converting BAM to IDX/GFF format
				if(sr.getReadNegativeStrandFlag()) { recordStart = sr.getUnclippedEnd(); }				
			}
	
			if(recordStart > 0) {
				for(int POS = recordStart - WIDTH; POS <= recordStart + WIDTH; POS++) {
					if(POS - start >= 0 && POS - start < F_GOCC.length) {
						if(sr.getReadNegativeStrandFlag()) {
							R_GOCC[POS - start] += gaussWeight[POS - (recordStart - WIDTH)];
							if(POS == recordStart) tempR[POS - start]++;
						} else {
							F_GOCC[POS - start] += gaussWeight[POS - (recordStart - WIDTH)];
							if(POS == recordStart) tempF[POS - start]++;
						}
					}
				}
			}
		}
		for(int x = 0; x < tempF.length; x++) {
			double Favg = 0, Ravg = 0, Fcount = 0, Rcount = 0;
			for(int y = x - UP_WIDTH; y <= x + DOWN_WIDTH; y++) {
				if(y < 0) y = 0;
				if(y < tempF.length) {
					if(tempF[y] != 0) {
						F_TOCC[x] += tempF[y];
						Favg += (y * tempF[y]);
						Fcount += tempF[y];
					}
					if(tempR[y] != 0) {
						R_TOCC[x] += tempR[y];
						Ravg += (y * tempR[y]);
						Rcount += tempR[y];
					}
				}
			}
			double Fstd = 0, Rstd = 0;
			Favg /= Fcount;
			Ravg /= Rcount;
			for(int y = x - UP_WIDTH; y <= x + DOWN_WIDTH; y++) {
				if(y < 0) y = 0;
				if(y < tempF.length) {
					if(tempF[y] != 0) for(int z = 0; z < tempF[y]; z++) Fstd += Math.pow(y - Favg, 2);
					if(tempR[y] != 0) for(int z = 0; z < tempR[y]; z++) Rstd += Math.pow(y - Ravg, 2);
				}
			}
			if(Fcount == 1) F_STD[x] = 0;
			else F_STD[x] = Math.sqrt(Fstd / Fcount);
			if(Rcount == 1) R_STD[x] = 0;
			else R_STD[x] = Math.sqrt(Rstd / Rcount);
			
		}

	}

	public void parsePeaksbyExclusion(ArrayList<Peak> peaks) {
		//Sort by Peak Score
		Collections.sort(peaks, Peak.PeakTagComparator);
		
		//TODO adjust x and y by the changing size of the peak array
		for(int x = 0; x < peaks.size(); x++) {
			for(int y = 0; y < peaks.size(); y++) {
				if(x != y) {
					if(peaks.get(x).getStop() > peaks.get(y).getStart() && peaks.get(x).getStart() < peaks.get(y).getStop()) {
						peaks.remove(y);
					} else if(peaks.get(x).getStart() < peaks.get(y).getStop() && peaks.get(x).getStop() > peaks.get(y).getStart()) {
						peaks.remove(y);
					}
				}
			}
		}
		
		//Output peaks that pass exclusion filtering
		for(int x = 0; x < peaks.size(); x++) {
			OUT.println(peaks.toString());
		}
	}
	
	public double getStd(double[] tag) {
		if(tag.length > 0) {
			double std = 0;
			double avg = 0;
			for(int x = 0; x < tag.length; x++) { avg += tag[x]; }
			avg /= tag.length;
			for(int x = 0; x < tag.length; x++) { std += Math.pow(tag[x] - avg, 2); }
			return Math.sqrt(std / (tag.length - 1));
		} else return Double.NaN;
	}
	
	private double[] gaussKernel() {
		double[] Garray = new double[(int) (SIGMA * NUM_STD * 2) + 1];
		for(int x = 0; x < Garray.length; x++) {
             double HEIGHT = Math.exp(-1 * Math.pow((x - (Garray.length / 2)), 2) / (2 * Math.pow(SIGMA, 2)));
             HEIGHT /= (SIGMA * Math.sqrt(2 * Math.PI));
             //Garray[x] = Double.parseDouble(String.format("%.6g%n", HEIGHT));
             Garray[x] = HEIGHT;
		}
		return Garray;
     }
	
	private static String getTimeStamp() {
		Date date= new Date();
		String time = new Timestamp(date.getTime()).toString();
		return time;
	}
}