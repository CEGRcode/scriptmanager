package scripts.Read_Analysis.PileupScripts;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.util.CloseableIterator;

import java.io.File;
import java.util.Vector;

import objects.PileupParameters;
import objects.CoordinateObjects.BEDCoord;
import util.ArrayUtilities;

public class PileupExtract implements Runnable{
	PileupParameters param;
	File BAM;
	Vector<BEDCoord> INPUT;
	int index;
	int subsetsize;
	SamReader inputSam;
		
	public void run() {
		inputSam = SamReaderFactory.makeDefault().open(BAM);
		for(int x = index; x < index + subsetsize; x++) {
			extract(INPUT.get(x));		
		}
	}
	
	public PileupExtract(PileupParameters p, File b, Vector<BEDCoord> i, int current, int length) {
		param = p;
		BAM = b;
		INPUT = i;
		index = current;
		subsetsize = length;
	}
	
	public void extract(BEDCoord coord) {
		double[] TAG_S1 = null;
		double[] TAG_S2 = null;
		int SHIFT = param.getShift();
		int STRAND = param.getStrand();
		
		// Ugly hack to account for the fact that read 1 5' end may be outside the window of interest even though read 2 and the midpoint may be in range
		// TODO FIX this into something more logical, probably check for read2 in region independently?
		int MIDPOINT_ADJUST = 0;
		if(param.getAspect() == 2) { MIDPOINT_ADJUST = 300; }
		
		int BEDSTART = (int)coord.getStart();
		int BEDSTOP = (int)coord.getStop();
		
		//Correct Window Size for proper transformations
		int WINDOW = (BEDSTOP - BEDSTART) + ((param.getBin() / 2) * 2);
		int QUERYWINDOW = 0;
		if(param.getTrans() == 1) {
			WINDOW = (BEDSTOP - BEDSTART) + (param.getBin() * param.getSmooth() * 2);
			QUERYWINDOW = (param.getBin() * param.getSmooth()); 
		}
		else if(param.getTrans() == 2) {
			WINDOW = (BEDSTOP - BEDSTART) + (param.getBin() * param.getStdSize() * param.getStdNum() * 2);
			QUERYWINDOW = (param.getBin() * param.getStdSize() * param.getStdNum()); 
		}
		TAG_S1 = new double[WINDOW];
		if(STRAND == 0) TAG_S2 = new double[WINDOW];
		
		//SAMRecords are 1-based and inclusive
		CloseableIterator<SAMRecord> iter = inputSam.query(coord.getChrom(), BEDSTART - QUERYWINDOW - SHIFT - MIDPOINT_ADJUST - 1, BEDSTOP + QUERYWINDOW + SHIFT + MIDPOINT_ADJUST + 1, false);
		while (iter.hasNext()) {
			//Create the record object 
			SAMRecord sr = iter.next();
			
			if(sr.getReadPairedFlag()) { //Must be PAIRED-END mapped
				if((sr.getProperPairFlag() && param.getPErequire()) || !param.getPErequire()) { //Must either be properly paired if paired-end or don't care about requirement
					// (Aspect=5/3prime and (Read 1 and want Read 1, Read 2 and want Read 2, want any read)) or (Read 1 and want midpoint) or (Read 1 and want fragment)
					if(((param.getAspect() == 0 || param.getAspect() == 1) && ((sr.getFirstOfPairFlag() && param.getRead() == 0) || (!sr.getFirstOfPairFlag() && param.getRead() == 1) || param.getRead() == 2 )) || (sr.getFirstOfPairFlag() && param.getAspect() == 2) || (sr.getFirstOfPairFlag() && param.getAspect() == 3)) {
						// Set marker (left side default, right side if positive strand and 5 prime or negative strand and 3 prime
						int mark = sr.getUnclippedStart() - 1;
						if((param.getAspect() == 0 && sr.getReadNegativeStrandFlag()) || (param.getAspect() == 1 && !sr.getReadNegativeStrandFlag())) {
							mark = sr.getUnclippedEnd() - 1;
						}
						
						if(sr.getProperPairFlag()) { //prevent cases where non-properly paired Read1 gets to this point
							//Find midpoint if aspect flag == 2
							if(param.getAspect() == 2) {
								if(sr.getInferredInsertSize()>0) {
									mark = sr.getAlignmentStart() - 1 + (sr.getInferredInsertSize() / 2);
								} else if(sr.getInferredInsertSize()<0) {
									mark = sr.getMateAlignmentStart() - 1 - (sr.getInferredInsertSize() / 2);
								} else {
									//Most aligners will flag records with an insert size of zero as improper pairs
									System.err.println("This statement should never print (insert size=0 when finding midpoint in PileupExtract)");
									continue;
								}
								// Correction to ensure that even insert size mark reoriented for negative strands
								if(sr.getInferredInsertSize() % 2 == 0 && coord.getDir().equals("-") ) { mark--; }
							} else if(param.getAspect() == 3) {
								if(sr.getInferredInsertSize()>0) { mark = sr.getAlignmentStart() - 1; }
								else if(sr.getInferredInsertSize()<0) { mark = sr.getMateAlignmentStart() - 1; }
								else {
									//Most aligners will flag records with an insert size of zero as improper pairs
									System.err.println("This statement should never print (insert size=0 when finding fragment start in PileupExtract)");
									continue;
								}
							}
							// Apply insert size filters
							if(Math.abs(sr.getInferredInsertSize()) < param.getMinInsert() && param.getMinInsert() != -9999) { continue; } //Test for MIN insert size cutoff here
							if(Math.abs(sr.getInferredInsertSize()) > param.getMaxInsert() && param.getMaxInsert() != -9999) { continue; } //Test for MAX insert size cutoff here
						} else if(param.getAspect() < 1) { continue; }		// Make sure that midpoint/fragment pileup must come from properly paired read

						// Shift as needed
						if(sr.getReadNegativeStrandFlag()) { mark -= SHIFT; }
						else { mark += SHIFT; }

						//Adjust tag start to be within array reference
						mark -= (BEDSTART - QUERYWINDOW);
						
						//Increment Final Array keeping track of pileup
						if(param.getAspect() < 3) {
							if(mark >= 0 && mark < TAG_S1.length) {
								if(STRAND == 0) {
									if(!sr.getReadNegativeStrandFlag() && coord.getDir().equals("-")) { TAG_S2[mark] += 1; }
						        	else if(sr.getReadNegativeStrandFlag() && coord.getDir().equals("+")) { TAG_S2[mark] += 1; }
						        	else if(!sr.getReadNegativeStrandFlag() && coord.getDir().equals("+")) { TAG_S1[mark] += 1; }
						        	else if(sr.getReadNegativeStrandFlag() && coord.getDir().equals("-")) { TAG_S1[mark] += 1;}
								} else {
									TAG_S1[mark] += 1;
								}
							}
						} else {
							int markStop = Math.min(mark + Math.abs(sr.getInferredInsertSize()), TAG_S1.length);
							for(int m = mark; m < markStop; m++) {
								if(m >= 0 && m < TAG_S1.length) {
									if(STRAND == 0) {
										if(!sr.getReadNegativeStrandFlag() && coord.getDir().equals("-")) { TAG_S2[m] += 1; }
										else if(sr.getReadNegativeStrandFlag() && coord.getDir().equals("+")) { TAG_S2[m] += 1; }
										else if(!sr.getReadNegativeStrandFlag() && coord.getDir().equals("+")) { TAG_S1[m] += 1; }
										else if(sr.getReadNegativeStrandFlag() && coord.getDir().equals("-")) { TAG_S1[m] += 1;}
									} else {
										TAG_S1[m] += 1;
									}
								}
							}
						}
					}
				}
			} else if((param.getRead() == 0 || param.getRead() == 2) && (param.getAspect() == 0 || param.getAspect() == 1)) { //Also outputs if not paired-end since by default it is read-1 (and 5' or 3' end)
				// Set marker (left side default, right side if positive strand and 5 prime or negative strand and 3 prime
				int mark = sr.getUnclippedStart() - 1;
				if((param.getAspect() == 0 && sr.getReadNegativeStrandFlag()) || (param.getAspect() == 1 && !sr.getReadNegativeStrandFlag())) {
					mark = sr.getUnclippedEnd() - 1;
				}
				// Shift as needed
				if(sr.getReadNegativeStrandFlag()) { mark -= SHIFT; }
				else { mark += SHIFT; }

				//Adjust tag start to be within array reference
				mark -= (BEDSTART - QUERYWINDOW);
				
				//Increment Final Array keeping track of pileup
				if(mark >= 0 && mark < TAG_S1.length) {
					if(STRAND == 0) {
						if(!sr.getReadNegativeStrandFlag() && coord.getDir().equals("-")) { TAG_S2[mark] += 1; }
						else if(sr.getReadNegativeStrandFlag() && coord.getDir().equals("+")) { TAG_S2[mark] += 1; }
						else if(!sr.getReadNegativeStrandFlag() && coord.getDir().equals("+")) { TAG_S1[mark] += 1; }
						else if(sr.getReadNegativeStrandFlag() && coord.getDir().equals("-")) { TAG_S1[mark] += 1;}
					} else {
						TAG_S1[mark] += 1;
					}
				}
			}
		}
		iter.close();
		
		if(coord.getDir().equals("-")) {
			TAG_S1 = ArrayUtilities.reverseArray(TAG_S1);
			TAG_S2 = ArrayUtilities.reverseArray(TAG_S2);
		}
		
		//Perform Binning here
		double[] binF = new double[TAG_S1.length];
		double[] binR = null;
		if(TAG_S2 != null) binR = new double[TAG_S2.length]; 
		
		for(int j = 0; j < TAG_S1.length; j++) {
			for(int k = j - (param.getBin() / 2); k <= j + (param.getBin() / 2); k++) {
				if(k < 0) k = 0;
				if(k >= TAG_S1.length) k = j + (param.getBin() / 2) + 1;
				else  {
					binF[k] += TAG_S1[j];
					if(binR != null) binR[k] += TAG_S2[j];
				}
			}
		}
		double[] finalF = new double[TAG_S1.length / param.getBin()];
		double[] finalR = null;
		if(TAG_S2 != null) finalR = new double[TAG_S2.length / param.getBin()];
		
		for(int x = (param.getBin() / 2); x < TAG_S1.length - (param.getBin() / 2); x += param.getBin()) {
			finalF[(x - (param.getBin() / 2)) / param.getBin()] = TAG_S1[x];
			if(TAG_S2 != null) finalR[(x - (param.getBin() / 2)) / param.getBin()] = TAG_S2[x];
		}
		
		//Perform Tag Standardization Here
		if(param.getStandard()) {
			for(int i = 0; i < finalF.length; i++) {
				if(finalF != null) finalF[i] *= param.getRatio();
				if(finalR != null) finalR[i] *= param.getRatio();
			}
		}
		
		coord.setFstrand(finalF);
		coord.setRstrand(finalR);
	}
}
