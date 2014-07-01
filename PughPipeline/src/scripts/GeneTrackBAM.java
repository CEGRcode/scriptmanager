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
public class GeneTrackBAM extends JFrame {
	private JTextArea textArea;
	
	private File INPUT = null;
	private PrintStream OUT = null;
	private SAMFileReader inputSam = null;
	
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
	
	private double[] F_GOCC;
	private double[] R_GOCC;
	private double[] F_TOCC;
	private double[] R_TOCC;
	
	
	public GeneTrackBAM(File in, int s, int e, int u, int d, int f) {
		setTitle("BAM to Genetrack Progress");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(150, 150, 600, 800);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		INPUT = in;
		SIGMA = s;
		EXCLUSION = e;
		UP_WIDTH = u;
		DOWN_WIDTH = d;
		FILTER = f;
	}
	
	public void run() {
		String TIME = getTimeStamp();;
		String READ = "s" + SIGMA + "e" + EXCLUSION;
		String NAME = INPUT.getName().split("\\.")[0] + "_" + READ + ".gff";
		textArea.append(TIME + "\n" + NAME + "\n");
		textArea.append("Sigma: " + SIGMA + "\nExclusion: " + EXCLUSION + "\nFilter: " + FILTER + "\nUpstream width of called Peaks: " + UP_WIDTH + "\nDownstream width of called Peaks: " + DOWN_WIDTH + "\n");
		
		try { OUT = new PrintStream(new File(NAME)); }
		catch (FileNotFoundException e) { e.printStackTrace(); }

		//Set genetrack parameters
		gaussWeight = gaussKernel();
		WIDTH = SIGMA * NUM_STD;
		
		ArrayList<Peak> FPEAKS = null;
		ArrayList<Peak> RPEAKS = null;
		
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
					
					//TODO currently will be hard-coded for properly paired READ1
					CloseableIterator<SAMRecord> iter = inputSam.query(seq.getSequenceName(), start, stop, false);
					loadGenomeFragment(iter, start, stop);
					iter.close();
					
					for(int z = 0; z < windowSize; z++) {
						if(z == 0) {
							if(F_GOCC[z] > F_GOCC[z + 1]) { FPEAKS.add(new Peak(seq.getSequenceName(), z + start + 1 - (EXCLUSION / 2), z + start + 1 + (EXCLUSION / 2), "+")); }
							if(R_GOCC[z] > R_GOCC[z + 1]) { RPEAKS.add(new Peak(seq.getSequenceName(), z + start + 1 - (EXCLUSION / 2), z + start + 1 + (EXCLUSION / 2), "-")); }
						} else if(z + 1 == windowSize) {
							if(F_GOCC[z] > F_GOCC[z - 1]) { FPEAKS.add(new Peak(seq.getSequenceName(), z + start + 1 - (EXCLUSION / 2), z + start + 1 + (EXCLUSION / 2), "+")); }
							if(R_GOCC[z] > R_GOCC[z - 1]) { RPEAKS.add(new Peak(seq.getSequenceName(), z + start + 1 - (EXCLUSION / 2), z + start + 1 + (EXCLUSION / 2), "-")); }
						} else {
							if(F_GOCC[z] > F_GOCC[z + 1] && F_GOCC[z] > F_GOCC[z - 1]) {
								FPEAKS.add(new Peak(seq.getSequenceName(), z + start + 1 - (EXCLUSION / 2), z + start + 1 + (EXCLUSION / 2), "+"));
							}
							if(R_GOCC[z] > R_GOCC[z + 1] && R_GOCC[z] > R_GOCC[z - 1]) {
								RPEAKS.add(new Peak(seq.getSequenceName(), z + start + 1 - (EXCLUSION / 2), z + start + 1 + (EXCLUSION / 2), "-"));
							}
						}
						
						//int bp = z + start + 1;
						//OUT.println(seq.getSequenceName() + "\t" + bp + "\t" + F_GOCC[z] + "\t" + R_GOCC[z] + "\t" + F_TOCC[z] + "\t" + R_TOCC[z]);		
					}
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
				
				for(int z = 0; z < F_GOCC.length; z++) {
					if(z == 0) {
						if(F_GOCC[z] > F_GOCC[z + 1]) { FPEAKS.add(new Peak(seq.getSequenceName(), z + finalstart + 1 - (EXCLUSION / 2), z + finalstart + 1 + (EXCLUSION / 2), "+")); }
						if(R_GOCC[z] > R_GOCC[z + 1]) { RPEAKS.add(new Peak(seq.getSequenceName(), z + finalstart + 1 - (EXCLUSION / 2), z + finalstart + 1 + (EXCLUSION / 2), "-")); }
					} else if(z + 1 == F_GOCC.length) {
						if(F_GOCC[z] > F_GOCC[z - 1]) { FPEAKS.add(new Peak(seq.getSequenceName(), z + finalstart + 1 - (EXCLUSION / 2), z + finalstart + 1 + (EXCLUSION / 2), "+")); }
						if(R_GOCC[z] > R_GOCC[z - 1]) { RPEAKS.add(new Peak(seq.getSequenceName(), z + finalstart + 1 - (EXCLUSION / 2), z + finalstart + 1 + (EXCLUSION / 2), "-")); }
					} else {
						if(F_GOCC[z] > F_GOCC[z + 1] && F_GOCC[z] > F_GOCC[z - 1]) {
							FPEAKS.add(new Peak(seq.getSequenceName(), z + finalstart + 1 - (EXCLUSION / 2), z + finalstart + 1 + (EXCLUSION / 2), "+"));
						}
						if(R_GOCC[z] > R_GOCC[z + 1] && R_GOCC[z] > R_GOCC[z - 1]) {
							RPEAKS.add(new Peak(seq.getSequenceName(), z + finalstart + 1 - (EXCLUSION / 2), z + finalstart + 1 + (EXCLUSION / 2), "-"));
						}
					}
					//int bp = z + finalstart + 1;
					//OUT.println(seq.getSequenceName() + "\t" + bp + "\t" + F_GOCC[z] + "\t" + R_GOCC[z] + "\t" + F_TOCC[z] + "\t" + R_TOCC[z]);		
				}
				
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
	
	public void loadGenomeFragment(CloseableIterator<SAMRecord> iter, int start, int stop) {
		while (iter.hasNext()) {
			//Create the record object 
			SAMRecord sr = iter.next();
			//Must be PAIRED-END mapped, mate must be mapped, must be read1
			if(sr.getReadPairedFlag()) {
				if(sr.getProperPairFlag() && sr.getFirstOfPairFlag()) {
					//Get the start of the record 
					int recordStart = sr.getUnclippedStart();
					//Accounts for reverse tag reporting 3' end of tag and converting BAM to IDX/GFF format
					if(sr.getReadNegativeStrandFlag()) { recordStart = sr.getUnclippedEnd(); }
					for(int POS = recordStart - WIDTH; POS <= recordStart + WIDTH; POS++) {
						if(POS - start >= 0 && POS - start < F_GOCC.length) {
							if(sr.getReadNegativeStrandFlag()) {
								R_GOCC[POS - start] += gaussWeight[POS - (recordStart - WIDTH)];
							} else {
								F_GOCC[POS - start] += gaussWeight[POS - (recordStart - WIDTH)];
							}
						}
					}
					if(recordStart - start >= 0 && recordStart - start < F_TOCC.length) {
						if(sr.getReadNegativeStrandFlag()) { R_TOCC[recordStart - start]++; }
						else { F_TOCC[recordStart - start]++; }
					}

				}
			} else {
				//Also outputs if not paired-end since by default it is read-1
				//TODO
			}
		}
	}

	public void parsePeaksbyExclusion(ArrayList<Peak> peaks) {
		//Sort by Peak Score
		Collections.sort(peaks, Peak.PeakScoreComparator);
		
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