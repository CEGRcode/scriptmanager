package scriptmanager.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import htsjdk.samtools.AbstractBAMFileIndex;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;
import htsjdk.samtools.util.CloseableIterator;

import scriptmanager.objects.Exceptions.OptionException;
import scriptmanager.objects.Exceptions.ScriptManagerException;
import scriptmanager.objects.PileupParameters;
import scriptmanager.objects.CoordinateObjects.BEDCoord;

/**
 * Class containing a set of shared methods to be used across script classes.
 * 
 * @author William KM Lai
 * @see scriptmanager.cli.Read_Analysis.TagPileupCLI
 * @see scriptmanager.cli.Peak_Analysis.FRiXCalculatorCLI
 * @see scriptmanager.window_interface.Read_Analysis.TagPileupOutput
 * @see scriptmanager.window_interface.Peak_Analysis.FRiXCalculatorOutput
 */
public class BAMUtilities {

	/**
	 * Creates a new BAMUtilities object
	 */
	public BAMUtilities(){}

	/**
	 * Search the BAM header and sum the size of each chromosome/reference sequence
	 * 
	 * @param BAM input BAM-formatted file
	 * @return total bp of ref sequence aligned to
	 * @throws IOException
	 */
	public static double getGenomeSize(File BAM) throws IOException {
//		SamReaderFactory factory = SamReaderFactory.makeDefault().enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS, SamReaderFactory.Option.VALIDATE_CRC_CHECKSUMS).validationStringency(ValidationStringency.SILENT);
		SamReader factory = SamReaderFactory.makeDefault().open(BAM);
		AbstractBAMFileIndex bai = (AbstractBAMFileIndex) factory.indexing().getIndex();
		// sum the length of each ref sequence
		double totalGenome = 0;
		for (int x = 0; x < bai.getNumberOfReferences(); x++) {
			SAMSequenceRecord seq = factory.getFileHeader().getSequence(x);
			totalGenome += seq.getSequenceLength();
		}
		bai.close();
		factory.close();
		return (totalGenome);
	}


	/**
	 * Loop through BAM index metadata and tally-up total aligned read count according to PileupParameters criteria. (No filtering with blacklist)
	 * 
	 * @param BAM input BAM-formatted file
	 * @param p parameter storage object for read encoding information (aspect/read)
	 * @return Number of total reads in the BAM file
	 * @throws OptionException
	 * @throws IOException
	 */
	public static double getReadCount(File BAM, PileupParameters p) throws OptionException, ScriptManagerException, IOException {
		// Pre-calculate criteria
		int ASPECT = p.getAspect();
		int READ = p.getRead();
		// Pull R1, R2, and 5'/3'end encodings
		boolean checkR1 = (READ == PileupParameters.READ1 || READ == PileupParameters.ALLREADS);
		boolean checkR2 = (READ == PileupParameters.READ2 || READ == PileupParameters.ALLREADS);

		// Check all parameters are not in conflict
		p.validate();
		// Do not handle parameter validation past this point.

		// Instantiate variables
		double totalAligned = 0; // total count to return
		boolean unpairedWarning = false; // track Midpoint/Fragment encoding with reads missing read paired flag

		SamReader factory = SamReaderFactory.makeDefault().open(BAM);
		CloseableIterator<SAMRecord> iter = factory.iterator();
		while (iter.hasNext()) {
			// Get record
			SAMRecord sr = iter.next();

			// Ignore all unmapped reads
			if (sr.getReadUnmappedFlag()) {
				continue;
			}
			// Paired reads
			if(sr.getReadPairedFlag()) {
				// Read 1
				if (sr.getFirstOfPairFlag()) {
					// Count for R1 - 5/3 prime
					if (checkR1) {
						// Only count properly-paired when params set to require paired-end
						if (!p.getPErequire() || (p.getPErequire() && sr.getProperPairFlag())) {
							totalAligned++;
						}
					// Count for Midpoint (require PE)
					} else if (ASPECT == PileupParameters.MIDPOINT && sr.getProperPairFlag()) {
						totalAligned++;
					// Count for Full Fragment (require PE)
					} else if (ASPECT == PileupParameters.FRAGMENT && sr.getProperPairFlag()) {
						totalAligned += sr.getInferredInsertSize();
					}
				// Read 2
				} else {
					// Count for R2 - 5/3 prime
					if (checkR2) {
						// Only count properly-paired when params set to require paired-end
						if (!p.getPErequire() || (p.getPErequire() && sr.getProperPairFlag())) {
							totalAligned++;
						}
					}
				}
			// Non-paired (i.e. single-end)
			} else {
				// Count for R1 - 5/3 prime
				if (checkR1) {
					totalAligned++;
				} else if (ASPECT == PileupParameters.MIDPOINT || ASPECT == PileupParameters.FRAGMENT) { // Should not be true...
					// TODO: How do we want to handle cases that reach here with PileupParameters.MIDPOINT or PileupParameters.FRAGMENT?
					// Throw exception and handle as warning? Printing message to STDERR for now...
					// Update boolean to log warning to STDERR
					unpairedWarning = true;
				}
			}
		}
		iter.close();
		factory.close();

		// Print warnings to STDERR and throw exceptions
		if (totalAligned == 0) {
			throw new ScriptManagerException("This BAM file contains zero aligned reads that can be counted for according to the BAMUtilities.getReadCount() criteria. Check that you aren't trying to count midpoints or full fragments from a set of single-end alignments.");
		} else if (unpairedWarning) {
			System.err.println("WARNING: Check your BAM file. You indicated midpoint or full fragment encoding but unpaired reads were found.\nPlease make sure your BAM file isn't a mixture of single-end and paired-end reads to avoid calculation errors.");
		}

		return (totalAligned);
	}

	/**
	 * Calculates the standardization ratio for a given BAM file
	 * @param BAM BAM file used to calculate ratio
	 * @parm p store read aspect and read output encodings
	 * @return The standardization ratio for a given BAM file
	 * @throws IOException Invalid file or parameters
	 * @throws ScriptManagerException 
	 * @throws OptionException 
	 */
	public static double calculateStandardizationRatio(File BAM, PileupParameters p) throws IOException, OptionException, ScriptManagerException {
		// Get Genome Size
		double totalGenome = getGenomeSize(BAM);
		// Get total aligned
		double totalAligned = getReadCount(BAM, p);
		// Divide for ratio
		if(totalAligned > 0) { return (totalGenome / totalAligned); }
		// Return default
		return 1;
	}

	/**
	 * Calculates the standardization ratio for a given BAM file, ignoring blacklisted reads
	 * @param BAM BAM file used to calculate ratio 
	 * @param BLACKFile BED file containing blacklisted regions
	 * @param read Read Type (1 = Read1, 1 = Read2, 3 = All reads)
	 * @return The standardization ratio for a given BAM file
	 * @throws IOException Invalid file or parameters
	 */
	public static double calculateStandardizationRatio(File BAM, File BLACKFile, int read) throws IOException {
		//Blacklist filter in 500bp blocks on the genome with any blacklist region overlapping negating the entire block
		int windowSize = 500;
		
		List<String> chromName = new ArrayList<String>();
		List<Long> chromLength= new ArrayList<Long>();
		double totalAligned = 0;
		double totalGenome = 0;
		
		SamReaderFactory factory = SamReaderFactory.makeDefault().enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS, SamReaderFactory.Option.VALIDATE_CRC_CHECKSUMS).validationStringency(ValidationStringency.SILENT);
		SamReader inputBAM = factory.open(BAM);
		AbstractBAMFileIndex inputBAI = (AbstractBAMFileIndex) inputBAM.indexing().getIndex();
		for (int z = 0; z < inputBAI.getNumberOfReferences(); z++) {
			SAMSequenceRecord rec = inputBAM.getFileHeader().getSequence(z);
			chromName.add(rec.getSequenceName());
			chromLength.add(Long.valueOf(rec.getSequenceLength()));
			totalGenome += rec.getSequenceLength();
		}
		inputBAM.close();
		inputBAI.close();

		//Load Blacklist into HashMap
		HashMap<String, ArrayList<BEDCoord>> BLACKLIST = loadBlacklist(BLACKFile);
		
		inputBAM = SamReaderFactory.makeDefault().open(BAM);
		for(int x = 0; x < chromName.size(); x++) {
			String seq = chromName.get(x);
			long chromSize = chromLength.get(x);
			//Blacklist filter each chromosome, set blacklisted windows to NaN
			float[] chrom = maskChrom(seq, chromSize, windowSize, BLACKLIST);
			//Iterate through chromosome loading tags into window bin 
			CloseableIterator<SAMRecord> iter = inputBAM.query(seq, 0, (int)chromSize, false);
			//SAMRecords are 1-based
			while (iter.hasNext()) {
				SAMRecord sr = iter.next();
				int FivePrime = sr.getUnclippedStart() - 1;
				if(sr.getReadNegativeStrandFlag()) { FivePrime = sr.getUnclippedEnd(); }
				int INDEX = (FivePrime / windowSize);
				if(!sr.getReadUnmappedFlag() && INDEX < chrom.length) { //Test for mappability
					if(sr.getReadPairedFlag()) { //Test for paired-end status
						if(sr.getSecondOfPairFlag() && read == 1) { chrom[INDEX]++; } //count read 2
						else if(sr.getFirstOfPairFlag() && (read == 0 || read == 2)) { chrom[INDEX]++; } // count read 1
						if(sr.getProperPairFlag() && sr.getFirstOfPairFlag() && read == 3) { chrom[INDEX]++; } //count properly paired reads for midpoint
					} else if(read == 0 || read == 2) { //If the read is mapped but not paired-end, default to read 1
						chrom[INDEX]++;
					}
				}
			}
			iter.close();
			for(int i = 0; i < chrom.length; i++) { if(!Float.isNaN(chrom[i])) { totalAligned += chrom[i]; } }
		}
		if(totalAligned > 0) { return (totalGenome / totalAligned); }
		else { return 1; }
	}

	/**
	 * Sets blacklisted positions to NaN
	 * @param chrom Chromosome to be processed
	 * @param chromSize Length of the chromosome
	 * @param windowSize The window/bin size 
	 * @param BLACKLIST BED file containing blacklisted regions
	 * @return An array representing the chromosome, with blacklisted regions being represented as NaN and valid regions being zero
	 */
	private static float[] maskChrom(String chrom, long chromSize, int windowSize, HashMap<String, ArrayList<BEDCoord>> BLACKLIST) {
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
	
	/**
	 * Loads the blacklist BED file into a Hashmap<String, ArrayList<BEDCoord>>
	 * @param BLACKFile BED file to make the blacklist Hashmap with
	 * @return A Hashmap<String, ArrayList<BEDCoord>> with the name of chromosomes as keys and ArrayLists of blacklisted coordinates as values
	 * @throws FileNotFoundException Script could not find valid input file
	 */
	private static HashMap<String, ArrayList<BEDCoord>> loadBlacklist(File BLACKFile) throws FileNotFoundException {
		HashMap<String, ArrayList<BEDCoord>>  BLACKLIST = new HashMap<String, ArrayList<BEDCoord>>();
		Scanner scan = new Scanner(BLACKFile);
		while (scan.hasNextLine()) {
			String[] temp = scan.nextLine().split("\t");
			if(temp.length > 2) {
				if(!temp[0].contains("track") && !temp[0].contains("#")) {
					if(Integer.parseInt(temp[1]) >= 0) {
						int start = Integer.parseInt(temp[1]);
						int stop = Integer.parseInt(temp[2]);
						BEDCoord coord = new BEDCoord(temp[0], start, stop , ".");
						if (BLACKLIST.containsKey(temp[0])) { BLACKLIST.get(temp[0]).add(coord); }
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
		return BLACKLIST;
	}
}
