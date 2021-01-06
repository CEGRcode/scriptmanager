package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import objects.CoordinateObjects.GenericCoord;

public class GenomeSizeReference {
	
	private static String genomeName = "";
	private static long genomeSize = 0;
	
	private static ArrayList<String> chromName = null;
	private static ArrayList<Long> chromSize = null;
		
	public GenomeSizeReference() {
		chromName = new ArrayList<String>();
		chromSize = new ArrayList<Long>();
	}
	
	public GenomeSizeReference(String build) {
		if(build.equals("sacCer3_cegr")) {
			initialize_sacCer3_cegr();
		} else if(build.equals("hg19")) {
			initialize_hg19();
		} else if(build.equals("hg19_contigs")) {
			initialize_hg19_contig();
		} else if(build.equals("mm10")) {
			initialize_mm10();
		} else {
			System.err.println("Non-existent genome build!\n");
		}
	}
	
	public void setGenome(String build) {
		if(build.equals("sacCer3_cegr")) {
			initialize_sacCer3_cegr();
		} else if(build.equals("hg19")) {
			initialize_hg19();
		} else if(build.equals("hg19_contigs")) {
			initialize_hg19_contig();
		} else if(build.equals("mm10")) {
			initialize_mm10();
		} else {
			System.err.println("Non-existent genome build!\n");
		}
	}
	
	public String getGenome() {
		return genomeName;
	}
	
	public long getGenomeSize() {
		return genomeSize;
	}
	
	public ArrayList<String> getChrom() {
		return chromName;
	}
	
	public ArrayList<Long> getChromSize() {
		return chromSize;
	}
	
	public static void initialize_sacCer3_cegr() {
		genomeName = "sacCer3_cegr";
		genomeSize = 12163423;
		String[] chrom = {"chr1", "chr2", "chr3", "chr4", "chr5", "chr6", "chr7", "chr8", "chr9", "chr10", "chr11", "chr12", "chr13", "chr14", "chr15", "chr16", "chrM", "2-micron"};
		chromName = new ArrayList<String>(Arrays.asList(chrom));
		long[] size = new long[] {230218, 813184, 316620, 1531933, 576874, 270161, 1090940, 562643, 439888, 745751, 666816, 1078177, 924431, 784333, 1091291, 948066, 85779, 6318};
		chromSize = new ArrayList<Long>();
		for(int x = 0; x < size.length; x++) { chromSize.add(Long.valueOf(size[x])); }
	}
	
	public static void initialize_hg19() {
		genomeName = "hg19";
		genomeSize = (long) 3.095677412E9;
		String[] chrom = {"chr1", "chr2", "chr3", "chr4", "chr5", "chr6", "chr7", "chrX", "chr8", "chr9", "chr10", "chr11", "chr12", "chr13", "chr14", "chr15", "chr16", "chr17", "chr18", "chr20", "chrY", "chr19", "chr22", "chr21"};
		chromName = new ArrayList<String>(Arrays.asList(chrom));
		long[] size = new long[] {249250621, 243199373, 198022430, 191154276, 180915260, 171115067, 159138663, 155270560, 146364022, 141213431, 135534747, 135006516, 133851895, 115169878, 107349540, 102531392, 90354753,	81195210, 78077248, 63025520, 59373566,	59128983, 51304566, 48129895};
		chromSize = new ArrayList<Long>();
		for(int x = 0; x < size.length; x++) { chromSize.add(Long.valueOf(size[x])); }
	}
	
	public static void initialize_hg19_contig() {
		genomeName = "hg19";
		genomeSize = (long) 3.137161264E9;
		String[] chrom = {"chr1", "chr2", "chr3", "chr4", "chr5", "chr6", "chr7", "chrX", "chr8", "chr9", "chr10", "chr11", "chr12", "chr13", "chr14", "chr15", "chr16", "chr17", "chr18", "chr20", "chrY", "chr19", "chr22", "chr21",
				"chr6_ssto_hap7", "chr6_mcf_hap5", "chr6_cox_hap2", "chr6_mann_hap4", "chr6_apd_hap1", "chr6_qbl_hap6", "chr6_dbb_hap3", "chr17_ctg5_hap1", "chr4_ctg9_hap1", "chr1_gl000192_random", "chrUn_gl000225", "chr4_gl000194_random", "chr4_gl000193_random", "chr9_gl000200_random",
				"chrUn_gl000222", "chrUn_gl000212", "chr7_gl000195_random", "chrUn_gl000223", "chrUn_gl000224", "chrUn_gl000219", "chr17_gl000205_random", "chrUn_gl000215", "chrUn_gl000216", "chrUn_gl000217", "chr9_gl000199_random", "chrUn_gl000211","chrUn_gl000213", "chrUn_gl000220",
				"chrUn_gl000218", "chr19_gl000209_random", "chrUn_gl000221", "chrUn_gl000214", "chrUn_gl000228", "chrUn_gl000227", "chr1_gl000191_random", "chr19_gl000208_random", "chr9_gl000198_random", "chr17_gl000204_random", "chrUn_gl000233", "chrUn_gl000237", "chrUn_gl000230",
				"chrUn_gl000242", "chrUn_gl000243", "chrUn_gl000241", "chrUn_gl000236", "chrUn_gl000240", "chr17_gl000206_random", "chrUn_gl000232", "chrUn_gl000234", "chr11_gl000202_random", "chrUn_gl000238", "chrUn_gl000244", "chrUn_gl000248",	"chr8_gl000196_random", "chrUn_gl000249",
				"chrUn_gl000246", "chr17_gl000203_random", "chr8_gl000197_random", "chrUn_gl000245", "chrUn_gl000247", "chr9_gl000201_random", "chrUn_gl000235", "chrUn_gl000239", "chr21_gl000210_random", "chrUn_gl000231", "chrUn_gl000229", "chrM", "chrUn_gl000226", "chr18_gl000207_random"};
		
		chromName = new ArrayList<String>(Arrays.asList(chrom));
		long[] size = new long[] {249250621, 243199373, 198022430, 191154276, 180915260, 171115067, 159138663, 155270560, 146364022, 141213431, 135534747, 135006516, 133851895, 115169878, 107349540, 102531392, 90354753,	81195210, 78077248, 63025520, 59373566,	59128983, 51304566, 48129895,
			4928567, 4833398, 4795371, 4683263, 4622290, 4611984, 4610396, 1680828, 590426, 547496, 211173, 191469, 189789, 187035, 186861, 186858, 182896, 180455,	179693, 179198, 174588, 172545, 172294, 172149, 169874, 166566, 164239, 161802, 161147, 159169, 155397, 137718, 129120, 128374,
			106433, 92689, 90085, 81310, 45941,45867, 43691, 43523, 43341, 42152, 41934, 41933, 41001, 40652, 40531, 40103, 39939, 39929, 39786, 38914, 38502, 38154, 37498, 37175, 36651, 36422, 36148, 34474, 33824, 27682, 27386, 19913, 16571, 15008, 4262};
		chromSize = new ArrayList<Long>();
		for(int x = 0; x < size.length; x++) { chromSize.add(Long.valueOf(size[x])); }
	}
	
	public static void initialize_mm10() {
		genomeName = "mm10";
		genomeSize = (long) 2.725521370E9;
		String[] chrom = {"chr1", "chr2", "chr3", "chr4", "chr5", "chr6", "chr7", "chr8", "chr9", "chr10", "chr11", "chr12", "chr13", "chr14", "chr15", "chr16", "chr17", "chr18", "chr19", "chrX", "chrY"};
		chromName = new ArrayList<String>(Arrays.asList(chrom));
		long[] size = new long[] { 195471971, 182113224, 160039680, 156508116, 151834684, 149736546, 145441459, 129401213, 124595110, 130694993, 122082543, 120129022, 120421639, 124902244, 104043685, 98207768, 94987271, 90702639, 61431566, 171031299, 91744698};
		chromSize = new ArrayList<Long>();
		for(int x = 0; x < size.length; x++) { chromSize.add(Long.valueOf(size[x])); }
	}

	public GenericCoord generateRandomCoord(int WINDOW) {
		GenericCoord coord = null;
		boolean VALID = false;
		while(!VALID) {
			long location = ThreadLocalRandom.current().nextLong(genomeSize);
			long runningTotal = 0;
			int chromIndex = 0;
			for(int x = 0; x < chromSize.size(); x++) {
				if(location < runningTotal + chromSize.get(x).longValue()) {
					chromIndex = x;
					x = chromSize.size() + 1;
				} else { runningTotal += chromSize.get(x).longValue(); }
			}
			location -= runningTotal;
			String CHROM = chromName.get(chromIndex);
			long START = location - (WINDOW / 2);
			long STOP = location + (WINDOW / 2);
			String DIR = "+";
			if(ThreadLocalRandom.current().nextBoolean()) { DIR = "-"; }
			
			if(START > 0 && STOP < chromSize.get(chromIndex).longValue()) {
				VALID = true;	
				coord = new GenericCoord(CHROM, START, STOP, DIR);
			}
		}
		return coord;
	}

	//Check to make sure WINDOW size is smaller than every chromosome to prevent infinite loop
	public boolean isSmaller(int WINDOW) {
		for(int x = 0; x < chromSize.size(); x++) {
			if(WINDOW > chromSize.get(0)) { return false; }
		}
		return true;
	}
}
