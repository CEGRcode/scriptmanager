package scriptmanager.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import scriptmanager.objects.CoordinateObjects.GenericCoord;

/**
 * Create a genomic chromosome/contig size reference that is essentially defined
 * as an ArrayList chromsome/contig names and a parallel ArrayList storing each
 * chromsome/contig's length (in bp). Total bp genome length and genome name are
 * also tracked and this class includes helper methods for generating random
 * coordinates.
 *
 * @author William KM Lai
 * @see scriptmanager.scripts.Peak_Analysis.RandomCoordinate
 * @see scriptmanager.scripts.Peak_Analysis.TileGenome
 */
public class GenomeSizeReference {

	private static String genomeName = "";
	private static long genomeSize = 0;

	private static ArrayList<String> chromName = null;
	private static ArrayList<Long> chromSize = null;

	/**
	 * Constructor for empty GenomeSizeReference object. I.e., empty chromName &amp;
	 * chromSize lists with genomeSize set to 0 and genomeName as an empty Sstring.
	 */
	public GenomeSizeReference() {
		chromName = new ArrayList<String>();
		chromSize = new ArrayList<Long>();
	}

	/**
	 * Constructor for defining a new instance based on the provided genome build.
	 *
	 * @param build indicate the genome build to use
	 */
	public GenomeSizeReference(String build) {
		setGenome(build);
	}

	/**
	 * Initialize genome build to a predefined set of chromosome/contig sets.
	 *
	 * @param build this string determines which genome build to use (see initialize_...() methods)
	 * @throws IllegalArgumentException when unrecognized build String is input, this exception is thrown
	 */
	public void setGenome(String build) throws IllegalArgumentException {
		if (build.equals("sacCer3")) {
			initialize_sacCer3();
		} else if (build.equals("sacCer3_cegr")) {
			initialize_sacCer3_cegr();
		} else if (build.equals("hg38")) {
			initialize_hg38();
		} else if (build.equals("hg38_contigs")) {
			initialize_hg38();
		} else if (build.equals("hg19")) {
			initialize_hg19();
		} else if (build.equals("hg19_contigs")) {
			initialize_hg19_contig();
		} else if (build.equals("mm10")) {
			initialize_mm10();
		} else {
			throw new IllegalArgumentException(" (!)Invalid genomeName selected(" + build + "), please select from one of the provided genomes: sacCer3_cegr, hg19, hg19_contigs, and mm10\n");
		}
	}

	/**
	 * Retrieve the name of the current genome build used in this
	 * GenomeSizeReference instance
	 *
	 * @return a String describing the current genome build (e.g. sacCer3, hg19_contigs, ...)
	 */
	public String getGenome() {
		return genomeName;
	}

	/**
	 * Retrieve the total genome size of this GenomeSizeReference instance.
	 *
	 * @return genomeSize which should be equivalent to sum of all Longs in chromSize
	 */
	public long getGenomeSize() {
		return genomeSize;
	}

	/**
	 * Retrieve list of chromosome names (chromName, parallel to chromSize).
	 *
	 * @return list of chromosome names (Strings)
	 */
	public ArrayList<String> getChrom() {
		return chromName;
	}

	/**
	 * Retrieve list of chromosome lengths in bp (chromSize, parallel to chromName).
	 *
	 * @return list of bp lengths (Long)
	 */
	public ArrayList<Long> getChromSize() {
		return chromSize;
	}

	/**
	 * Set instance to use sacCer3 (yeast) genome build using the *roman* numeral
	 * chromosome naming system. This includes the "complete" assembled chromosomes,
	 * the mitochondrial chromosome ("chrM"), and the 2-micron plasmid.
	 *
	 * https://hgdownload.soe.ucsc.edu/goldenPath/sacCer3/bigZips/sacCer3.chrom.sizes
	 */
	public static void initialize_sacCer3() {
		genomeName = "sacCer3";
		genomeSize = 12163423;
		String[] chrom = { "chrI", "chrII", "chrIII", "chrIV", "chrV", "chrVI", "chrVII", "chrVIII", "chrIX", "chrX",
				"chrXI", "chrXII", "chrXIII", "chrXIV", "chrXV", "chrXVI", "chrM", "2-micron" };
		chromName = new ArrayList<String>(Arrays.asList(chrom));
		long[] size = new long[] { 230218, 813184, 316620, 1531933, 576874, 270161, 1090940, 562643, 439888, 745751,
				666816, 1078177, 924431, 784333, 1091291, 948066, 85779, 6318 };
		chromSize = new ArrayList<Long>();
		for (int x = 0; x < size.length; x++) {
			chromSize.add(Long.valueOf(size[x]));
		}
	}

	/**
	 * Set instance to use sacCer3 (yeast) genome build using the *arabic* numeral
	 * chromosome naming system. This includes the "complete" assembled chromosomes,
	 * the mitochondrial chromosome ("chrM"), and the 2-micron plasmid.
	 *
	 * https://hgdownload.soe.ucsc.edu/goldenPath/sacCer3/bigZips/sacCer3.chrom.sizes
	 */
	public static void initialize_sacCer3_cegr() {
		genomeName = "sacCer3_cegr";
		genomeSize = 12163423;
		String[] chrom = {"chr1", "chr2", "chr3", "chr4", "chr5", "chr6", "chr7", "chr8", "chr9", "chr10", "chr11", "chr12", "chr13", "chr14", "chr15", "chr16", "chrM", "2-micron"};
		chromName = new ArrayList<String>(Arrays.asList(chrom));
		long[] size = new long[] {230218, 813184, 316620, 1531933, 576874, 270161, 1090940, 562643, 439888, 745751, 666816, 1078177, 924431, 784333, 1091291, 948066, 85779, 6318};
		chromSize = new ArrayList<Long>();
		for (int x = 0; x < size.length; x++) {
			chromSize.add(Long.valueOf(size[x]));
		}
	}

	/**
	 * Set instance to use hg19 (human) genome build. This includes only the
	 * "complete" assembled chromosomes. Excludes the mitochondrial chromosome and
	 * alternative contigs.
	 *
	 * https://hgdownload.soe.ucsc.edu/goldenPath/hg19/bigZips/hg19.chrom.sizes
	 */
	public static void initialize_hg19() {
		genomeName = "hg19";
		genomeSize = (long) 3.095677412E9;
		String[] chrom = {"chr1", "chr2", "chr3", "chr4", "chr5", "chr6", "chr7", "chrX", "chr8", "chr9", "chr10", "chr11", "chr12", "chr13", "chr14", "chr15", "chr16", "chr17", "chr18", "chr20", "chrY", "chr19", "chr22", "chr21"};
		chromName = new ArrayList<String>(Arrays.asList(chrom));
		long[] size = new long[] {249250621, 243199373, 198022430, 191154276, 180915260, 171115067, 159138663, 155270560, 146364022, 141213431, 135534747, 135006516, 133851895, 115169878, 107349540, 102531392, 90354753,	81195210, 78077248, 63025520, 59373566,	59128983, 51304566, 48129895};
		chromSize = new ArrayList<Long>();
		for (int x = 0; x < size.length; x++) {
			chromSize.add(Long.valueOf(size[x]));
		}
	}

	/**
	 * Set instance to use hg19 (human) genome build. This includes the "complete"
	 * assembled chromosomes, the mitochondrial chromosome ("chrM"), and the full
	 * set of alternative contigs.
	 *
	 * https://hgdownload.soe.ucsc.edu/goldenPath/hg19/bigZips/hg19.chrom.sizes
	 */
	public static void initialize_hg19_contig() {
		genomeName = "hg19";
		genomeSize = (long) 3.137161264E9;
		String[] chrom = { "chr1", "chr2", "chr3", "chr4", "chr5", "chr6", "chr7", "chrX", "chr8", "chr9", "chr10",
				"chr11", "chr12", "chr13", "chr14", "chr15", "chr16", "chr17", "chr18", "chr20", "chrY", "chr19",
				"chr22", "chr21", "chr6_ssto_hap7", "chr6_mcf_hap5", "chr6_cox_hap2", "chr6_mann_hap4", "chr6_apd_hap1",
				"chr6_qbl_hap6", "chr6_dbb_hap3", "chr17_ctg5_hap1", "chr4_ctg9_hap1", "chr1_gl000192_random",
				"chrUn_gl000225", "chr4_gl000194_random", "chr4_gl000193_random", "chr9_gl000200_random",
				"chrUn_gl000222", "chrUn_gl000212", "chr7_gl000195_random", "chrUn_gl000223", "chrUn_gl000224",
				"chrUn_gl000219", "chr17_gl000205_random", "chrUn_gl000215", "chrUn_gl000216", "chrUn_gl000217",
				"chr9_gl000199_random", "chrUn_gl000211", "chrUn_gl000213", "chrUn_gl000220", "chrUn_gl000218",
				"chr19_gl000209_random", "chrUn_gl000221", "chrUn_gl000214", "chrUn_gl000228", "chrUn_gl000227",
				"chr1_gl000191_random", "chr19_gl000208_random", "chr9_gl000198_random", "chr17_gl000204_random",
				"chrUn_gl000233", "chrUn_gl000237", "chrUn_gl000230", "chrUn_gl000242", "chrUn_gl000243",
				"chrUn_gl000241", "chrUn_gl000236", "chrUn_gl000240", "chr17_gl000206_random", "chrUn_gl000232",
				"chrUn_gl000234", "chr11_gl000202_random", "chrUn_gl000238", "chrUn_gl000244", "chrUn_gl000248",
				"chr8_gl000196_random", "chrUn_gl000249", "chrUn_gl000246", "chr17_gl000203_random",
				"chr8_gl000197_random", "chrUn_gl000245", "chrUn_gl000247", "chr9_gl000201_random", "chrUn_gl000235",
				"chrUn_gl000239", "chr21_gl000210_random", "chrUn_gl000231", "chrUn_gl000229", "chrM", "chrUn_gl000226",
				"chr18_gl000207_random" };

		chromName = new ArrayList<String>(Arrays.asList(chrom));
		long[] size = new long[] { 249250621, 243199373, 198022430, 191154276, 180915260, 171115067, 159138663,
				155270560, 146364022, 141213431, 135534747, 135006516, 133851895, 115169878, 107349540, 102531392,
				90354753, 81195210, 78077248, 63025520, 59373566, 59128983, 51304566, 48129895, 4928567, 4833398,
				4795371, 4683263, 4622290, 4611984, 4610396, 1680828, 590426, 547496, 211173, 191469, 189789, 187035,
				186861, 186858, 182896, 180455, 179693, 179198, 174588, 172545, 172294, 172149, 169874, 166566, 164239,
				161802, 161147, 159169, 155397, 137718, 129120, 128374, 106433, 92689, 90085, 81310, 45941, 45867,
				43691, 43523, 43341, 42152, 41934, 41933, 41001, 40652, 40531, 40103, 39939, 39929, 39786, 38914, 38502,
				38154, 37498, 37175, 36651, 36422, 36148, 34474, 33824, 27682, 27386, 19913, 16571, 15008, 4262 };
		chromSize = new ArrayList<Long>();
		for (int x = 0; x < size.length; x++) {
			chromSize.add(Long.valueOf(size[x]));
		}
	}

	/**
	 * Set instance to use hg38/GrCh38 (human) genome build. This includes only the
	 * "complete" assembled chromosomes. Excludes the mitochondrial chromosome and
	 * alternative contigs.
	 *
	 * https://github.com/igvteam/igv/blob/master/genomes/sizes/hg38.chrom.sizes
	 */
	public static void initialize_hg38() {
		genomeName = "hg38";
		genomeSize = (long) 3.088269832E9;
		String[] chrom = { "chr1", "chr2", "chr3", "chr4", "chr5", "chr6", "chr7", "chr8", "chr9", "chr10", "chr11",
				"chr12", "chr13", "chr14", "chr15", "chr16", "chr17", "chr18", "chr19", "chr20", "chr21", "chr22",
				"chrX", "chrY" };
		chromName = new ArrayList<String>(Arrays.asList(chrom));
		long[] size = new long[] { 248956422, 242193529, 198295559, 190214555, 181538259, 170805979, 159345973,
				145138636, 138394717, 133797422, 135086622, 133275309, 114364328, 107043718, 101991189, 90338345,
				83257441, 80373285, 58617616, 64444167, 46709983, 50818468, 156040895, 57227415 };
		chromSize = new ArrayList<Long>();
		for (int x = 0; x < size.length; x++) {
			chromSize.add(Long.valueOf(size[x]));
		}
	}

	/**
	 * Set instance to use hg38/GrCh38 (human) genome build. This includes the
	 * "complete" assembled chromosomes, the mitochondrial chromosome ("chrM"), and
	 * the full set of alternative contigs.
	 *
	 * https://github.com/igvteam/igv/blob/master/genomes/sizes/hg38.chrom.sizes
	 */
	public static void initialize_hg38_contig() {
		genomeName = "hg38_contig";
		genomeSize = (long) 3.209286105E9;
		String[] chrom = { "chr1", "chr2", "chr3", "chr4", "chr5", "chr6", "chr7", "chr8", "chr9", "chr10", "chr11",
				"chr12", "chr13", "chr14", "chr15", "chr16", "chr17", "chr18", "chr19", "chr20", "chr21", "chr22",
				"chrX", "chrY", "chrM", "chr11_KI270721v1_random", "chr14_GL000009v2_random", "chr14_GL000225v1_random",
				"chr14_KI270722v1_random", "chr14_GL000194v1_random", "chr14_KI270723v1_random",
				"chr14_KI270724v1_random", "chr14_KI270725v1_random", "chr14_KI270726v1_random",
				"chr15_KI270727v1_random", "chr16_KI270728v1_random", "chr17_GL000205v2_random",
				"chr17_KI270729v1_random", "chr17_KI270730v1_random", "chr1_KI270706v1_random",
				"chr1_KI270707v1_random", "chr1_KI270708v1_random", "chr1_KI270709v1_random", "chr1_KI270710v1_random",
				"chr1_KI270711v1_random", "chr1_KI270712v1_random", "chr1_KI270713v1_random", "chr1_KI270714v1_random",
				"chr22_KI270731v1_random", "chr22_KI270732v1_random", "chr22_KI270733v1_random",
				"chr22_KI270734v1_random", "chr22_KI270735v1_random", "chr22_KI270736v1_random",
				"chr22_KI270737v1_random", "chr22_KI270738v1_random", "chr22_KI270739v1_random",
				"chr2_KI270715v1_random", "chr2_KI270716v1_random", "chr3_GL000221v1_random", "chr4_GL000008v2_random",
				"chr5_GL000208v1_random", "chr9_KI270717v1_random", "chr9_KI270718v1_random", "chr9_KI270719v1_random",
				"chr9_KI270720v1_random", "chr1_KI270762v1_alt", "chr1_KI270766v1_alt", "chr1_KI270760v1_alt",
				"chr1_KI270765v1_alt", "chr1_GL383518v1_alt", "chr1_GL383519v1_alt", "chr1_GL383520v2_alt",
				"chr1_KI270764v1_alt", "chr1_KI270763v1_alt", "chr1_KI270759v1_alt", "chr1_KI270761v1_alt",
				"chr2_KI270770v1_alt", "chr2_KI270773v1_alt", "chr2_KI270774v1_alt", "chr2_KI270769v1_alt",
				"chr2_GL383521v1_alt", "chr2_KI270772v1_alt", "chr2_KI270775v1_alt", "chr2_KI270771v1_alt",
				"chr2_KI270768v1_alt", "chr2_GL582966v2_alt", "chr2_GL383522v1_alt", "chr2_KI270776v1_alt",
				"chr2_KI270767v1_alt", "chr3_JH636055v2_alt", "chr3_KI270783v1_alt", "chr3_KI270780v1_alt",
				"chr3_GL383526v1_alt", "chr3_KI270777v1_alt", "chr3_KI270778v1_alt", "chr3_KI270781v1_alt",
				"chr3_KI270779v1_alt", "chr3_KI270782v1_alt", "chr3_KI270784v1_alt", "chr4_KI270790v1_alt",
				"chr4_GL383528v1_alt", "chr4_KI270787v1_alt", "chr4_GL000257v2_alt", "chr4_KI270788v1_alt",
				"chr4_GL383527v1_alt", "chr4_KI270785v1_alt", "chr4_KI270789v1_alt", "chr4_KI270786v1_alt",
				"chr5_KI270793v1_alt", "chr5_KI270792v1_alt", "chr5_KI270791v1_alt", "chr5_GL383532v1_alt",
				"chr5_GL949742v1_alt", "chr5_KI270794v1_alt", "chr5_GL339449v2_alt", "chr5_GL383530v1_alt",
				"chr5_KI270796v1_alt", "chr5_GL383531v1_alt", "chr5_KI270795v1_alt", "chr6_GL000250v2_alt",
				"chr6_KI270800v1_alt", "chr6_KI270799v1_alt", "chr6_GL383533v1_alt", "chr6_KI270801v1_alt",
				"chr6_KI270802v1_alt", "chr6_KB021644v2_alt", "chr6_KI270797v1_alt", "chr6_KI270798v1_alt",
				"chr7_KI270804v1_alt", "chr7_KI270809v1_alt", "chr7_KI270806v1_alt", "chr7_GL383534v2_alt",
				"chr7_KI270803v1_alt", "chr7_KI270808v1_alt", "chr7_KI270807v1_alt", "chr7_KI270805v1_alt",
				"chr8_KI270818v1_alt", "chr8_KI270812v1_alt", "chr8_KI270811v1_alt", "chr8_KI270821v1_alt",
				"chr8_KI270813v1_alt", "chr8_KI270822v1_alt", "chr8_KI270814v1_alt", "chr8_KI270810v1_alt",
				"chr8_KI270819v1_alt", "chr8_KI270820v1_alt", "chr8_KI270817v1_alt", "chr8_KI270816v1_alt",
				"chr8_KI270815v1_alt", "chr9_GL383539v1_alt", "chr9_GL383540v1_alt", "chr9_GL383541v1_alt",
				"chr9_GL383542v1_alt", "chr9_KI270823v1_alt", "chr10_GL383545v1_alt", "chr10_KI270824v1_alt",
				"chr10_GL383546v1_alt", "chr10_KI270825v1_alt", "chr11_KI270832v1_alt", "chr11_KI270830v1_alt",
				"chr11_KI270831v1_alt", "chr11_KI270829v1_alt", "chr11_GL383547v1_alt", "chr11_JH159136v1_alt",
				"chr11_JH159137v1_alt", "chr11_KI270827v1_alt", "chr11_KI270826v1_alt", "chr12_GL877875v1_alt",
				"chr12_GL877876v1_alt", "chr12_KI270837v1_alt", "chr12_GL383549v1_alt", "chr12_KI270835v1_alt",
				"chr12_GL383550v2_alt", "chr12_GL383552v1_alt", "chr12_GL383553v2_alt", "chr12_KI270834v1_alt",
				"chr12_GL383551v1_alt", "chr12_KI270833v1_alt", "chr12_KI270836v1_alt", "chr13_KI270840v1_alt",
				"chr13_KI270839v1_alt", "chr13_KI270843v1_alt", "chr13_KI270841v1_alt", "chr13_KI270838v1_alt",
				"chr13_KI270842v1_alt", "chr14_KI270844v1_alt", "chr14_KI270847v1_alt", "chr14_KI270845v1_alt",
				"chr14_KI270846v1_alt", "chr15_KI270852v1_alt", "chr15_KI270851v1_alt", "chr15_KI270848v1_alt",
				"chr15_GL383554v1_alt", "chr15_KI270849v1_alt", "chr15_GL383555v2_alt", "chr15_KI270850v1_alt",
				"chr16_KI270854v1_alt", "chr16_KI270856v1_alt", "chr16_KI270855v1_alt", "chr16_KI270853v1_alt",
				"chr16_GL383556v1_alt", "chr16_GL383557v1_alt", "chr17_GL383563v3_alt", "chr17_KI270862v1_alt",
				"chr17_KI270861v1_alt", "chr17_KI270857v1_alt", "chr17_JH159146v1_alt", "chr17_JH159147v1_alt",
				"chr17_GL383564v2_alt", "chr17_GL000258v2_alt", "chr17_GL383565v1_alt", "chr17_KI270858v1_alt",
				"chr17_KI270859v1_alt", "chr17_GL383566v1_alt", "chr17_KI270860v1_alt", "chr18_KI270864v1_alt",
				"chr18_GL383567v1_alt", "chr18_GL383570v1_alt", "chr18_GL383571v1_alt", "chr18_GL383568v1_alt",
				"chr18_GL383569v1_alt", "chr18_GL383572v1_alt", "chr18_KI270863v1_alt", "chr19_KI270868v1_alt",
				"chr19_KI270865v1_alt", "chr19_GL383573v1_alt", "chr19_GL383575v2_alt", "chr19_GL383576v1_alt",
				"chr19_GL383574v1_alt", "chr19_KI270866v1_alt", "chr19_KI270867v1_alt", "chr19_GL949746v1_alt",
				"chr20_GL383577v2_alt", "chr20_KI270869v1_alt", "chr20_KI270871v1_alt", "chr20_KI270870v1_alt",
				"chr21_GL383578v2_alt", "chr21_KI270874v1_alt", "chr21_KI270873v1_alt", "chr21_GL383579v2_alt",
				"chr21_GL383580v2_alt", "chr21_GL383581v2_alt", "chr21_KI270872v1_alt", "chr22_KI270875v1_alt",
				"chr22_KI270878v1_alt", "chr22_KI270879v1_alt", "chr22_KI270876v1_alt", "chr22_KI270877v1_alt",
				"chr22_GL383583v2_alt", "chr22_GL383582v2_alt", "chrX_KI270880v1_alt", "chrX_KI270881v1_alt",
				"chr19_KI270882v1_alt", "chr19_KI270883v1_alt", "chr19_KI270884v1_alt", "chr19_KI270885v1_alt",
				"chr19_KI270886v1_alt", "chr19_KI270887v1_alt", "chr19_KI270888v1_alt", "chr19_KI270889v1_alt",
				"chr19_KI270890v1_alt", "chr19_KI270891v1_alt", "chr1_KI270892v1_alt", "chr2_KI270894v1_alt",
				"chr2_KI270893v1_alt", "chr3_KI270895v1_alt", "chr4_KI270896v1_alt", "chr5_KI270897v1_alt",
				"chr5_KI270898v1_alt", "chr6_GL000251v2_alt", "chr7_KI270899v1_alt", "chr8_KI270901v1_alt",
				"chr8_KI270900v1_alt", "chr11_KI270902v1_alt", "chr11_KI270903v1_alt", "chr12_KI270904v1_alt",
				"chr15_KI270906v1_alt", "chr15_KI270905v1_alt", "chr17_KI270907v1_alt", "chr17_KI270910v1_alt",
				"chr17_KI270909v1_alt", "chr17_JH159148v1_alt", "chr17_KI270908v1_alt", "chr18_KI270912v1_alt",
				"chr18_KI270911v1_alt", "chr19_GL949747v2_alt", "chr22_KB663609v1_alt", "chrX_KI270913v1_alt",
				"chr19_KI270914v1_alt", "chr19_KI270915v1_alt", "chr19_KI270916v1_alt", "chr19_KI270917v1_alt",
				"chr19_KI270918v1_alt", "chr19_KI270919v1_alt", "chr19_KI270920v1_alt", "chr19_KI270921v1_alt",
				"chr19_KI270922v1_alt", "chr19_KI270923v1_alt", "chr3_KI270924v1_alt", "chr4_KI270925v1_alt",
				"chr6_GL000252v2_alt", "chr8_KI270926v1_alt", "chr11_KI270927v1_alt", "chr19_GL949748v2_alt",
				"chr22_KI270928v1_alt", "chr19_KI270929v1_alt", "chr19_KI270930v1_alt", "chr19_KI270931v1_alt",
				"chr19_KI270932v1_alt", "chr19_KI270933v1_alt", "chr19_GL000209v2_alt", "chr3_KI270934v1_alt",
				"chr6_GL000253v2_alt", "chr19_GL949749v2_alt", "chr3_KI270935v1_alt", "chr6_GL000254v2_alt",
				"chr19_GL949750v2_alt", "chr3_KI270936v1_alt", "chr6_GL000255v2_alt", "chr19_GL949751v2_alt",
				"chr3_KI270937v1_alt", "chr6_GL000256v2_alt", "chr19_GL949752v1_alt", "chr6_KI270758v1_alt",
				"chr19_GL949753v2_alt", "chr19_KI270938v1_alt", "chrUn_KI270302v1", "chrUn_KI270304v1",
				"chrUn_KI270303v1", "chrUn_KI270305v1", "chrUn_KI270322v1", "chrUn_KI270320v1", "chrUn_KI270310v1",
				"chrUn_KI270316v1", "chrUn_KI270315v1", "chrUn_KI270312v1", "chrUn_KI270311v1", "chrUn_KI270317v1",
				"chrUn_KI270412v1", "chrUn_KI270411v1", "chrUn_KI270414v1", "chrUn_KI270419v1", "chrUn_KI270418v1",
				"chrUn_KI270420v1", "chrUn_KI270424v1", "chrUn_KI270417v1", "chrUn_KI270422v1", "chrUn_KI270423v1",
				"chrUn_KI270425v1", "chrUn_KI270429v1", "chrUn_KI270442v1", "chrUn_KI270466v1", "chrUn_KI270465v1",
				"chrUn_KI270467v1", "chrUn_KI270435v1", "chrUn_KI270438v1", "chrUn_KI270468v1", "chrUn_KI270510v1",
				"chrUn_KI270509v1", "chrUn_KI270518v1", "chrUn_KI270508v1", "chrUn_KI270516v1", "chrUn_KI270512v1",
				"chrUn_KI270519v1", "chrUn_KI270522v1", "chrUn_KI270511v1", "chrUn_KI270515v1", "chrUn_KI270507v1",
				"chrUn_KI270517v1", "chrUn_KI270529v1", "chrUn_KI270528v1", "chrUn_KI270530v1", "chrUn_KI270539v1",
				"chrUn_KI270538v1", "chrUn_KI270544v1", "chrUn_KI270548v1", "chrUn_KI270583v1", "chrUn_KI270587v1",
				"chrUn_KI270580v1", "chrUn_KI270581v1", "chrUn_KI270579v1", "chrUn_KI270589v1", "chrUn_KI270590v1",
				"chrUn_KI270584v1", "chrUn_KI270582v1", "chrUn_KI270588v1", "chrUn_KI270593v1", "chrUn_KI270591v1",
				"chrUn_KI270330v1", "chrUn_KI270329v1", "chrUn_KI270334v1", "chrUn_KI270333v1", "chrUn_KI270335v1",
				"chrUn_KI270338v1", "chrUn_KI270340v1", "chrUn_KI270336v1", "chrUn_KI270337v1", "chrUn_KI270363v1",
				"chrUn_KI270364v1", "chrUn_KI270362v1", "chrUn_KI270366v1", "chrUn_KI270378v1", "chrUn_KI270379v1",
				"chrUn_KI270389v1", "chrUn_KI270390v1", "chrUn_KI270387v1", "chrUn_KI270395v1", "chrUn_KI270396v1",
				"chrUn_KI270388v1", "chrUn_KI270394v1", "chrUn_KI270386v1", "chrUn_KI270391v1", "chrUn_KI270383v1",
				"chrUn_KI270393v1", "chrUn_KI270384v1", "chrUn_KI270392v1", "chrUn_KI270381v1", "chrUn_KI270385v1",
				"chrUn_KI270382v1", "chrUn_KI270376v1", "chrUn_KI270374v1", "chrUn_KI270372v1", "chrUn_KI270373v1",
				"chrUn_KI270375v1", "chrUn_KI270371v1", "chrUn_KI270448v1", "chrUn_KI270521v1", "chrUn_GL000195v1",
				"chrUn_GL000219v1", "chrUn_GL000220v1", "chrUn_GL000224v1", "chrUn_KI270741v1", "chrUn_GL000226v1",
				"chrUn_GL000213v1", "chrUn_KI270743v1", "chrUn_KI270744v1", "chrUn_KI270745v1", "chrUn_KI270746v1",
				"chrUn_KI270747v1", "chrUn_KI270748v1", "chrUn_KI270749v1", "chrUn_KI270750v1", "chrUn_KI270751v1",
				"chrUn_KI270752v1", "chrUn_KI270753v1", "chrUn_KI270754v1", "chrUn_KI270755v1", "chrUn_KI270756v1",
				"chrUn_KI270757v1", "chrUn_GL000214v1", "chrUn_KI270742v1", "chrUn_GL000216v2", "chrUn_GL000218v1",
				"chrY_KI270740v1_random" };
		chromName = new ArrayList<String>(Arrays.asList(chrom));
		long[] size = new long[] { 248956422, 242193529, 198295559, 190214555, 181538259, 170805979, 159345973,
				145138636, 138394717, 133797422, 135086622, 133275309, 114364328, 107043718, 101991189, 90338345,
				83257441, 80373285, 58617616, 64444167, 46709983, 50818468, 156040895, 57227415, 16569, 100316, 201709,
				211173, 194050, 191469, 38115, 39555, 172810, 43739, 448248, 1872759, 185591, 280839, 112551, 175055,
				32032, 127682, 66860, 40176, 42210, 176043, 40745, 41717, 150754, 41543, 179772, 165050, 42811, 181920,
				103838, 99375, 73985, 161471, 153799, 155397, 209709, 92689, 40062, 38054, 176845, 39050, 354444,
				256271, 109528, 185285, 182439, 110268, 366580, 50258, 911658, 425601, 165834, 136240, 70887, 223625,
				120616, 143390, 133041, 138019, 110395, 110099, 96131, 123821, 174166, 161578, 173151, 109187, 224108,
				180671, 173649, 248252, 113034, 205312, 162429, 184404, 220246, 376187, 111943, 586476, 158965, 164536,
				119912, 205944, 244096, 126136, 179043, 195710, 82728, 226852, 164558, 1612928, 101241, 172708, 173459,
				131892, 4672374, 175808, 152148, 124736, 870480, 75005, 185823, 197536, 271782, 157952, 209586, 158166,
				119183, 1111570, 271455, 126434, 209988, 145606, 282736, 292436, 985506, 300230, 624492, 141812, 374415,
				133535, 36640, 158983, 305841, 132244, 162988, 71551, 171286, 60032, 439082, 179254, 181496, 309802,
				188315, 210133, 177092, 296895, 204059, 154407, 200998, 191409, 67707, 186169, 167313, 408271, 40090,
				120804, 238139, 169178, 138655, 152874, 119498, 184319, 76061, 56134, 191684, 180306, 103832, 169134,
				306913, 37287, 322166, 1511111, 180703, 1351393, 478999, 263054, 327382, 296527, 244917, 388773, 430880,
				134193, 63982, 232857, 2659700, 192462, 89672, 375691, 391357, 196688, 2877074, 278131, 70345, 133151,
				1821992, 223995, 235827, 108763, 90219, 178921, 111737, 289831, 164789, 198278, 104552, 167950, 159547,
				167999, 61734, 52969, 385657, 170222, 188024, 155864, 43156, 233762, 987716, 128386, 118774, 58661,
				183433, 63917, 166743, 143900, 201197, 74653, 116689, 82692, 259914, 186262, 304135, 263666, 101331,
				96924, 162811, 284869, 144206, 248807, 170399, 157053, 171027, 204239, 209512, 155532, 170698, 184499,
				170680, 162212, 214158, 161218, 162896, 378547, 1144418, 130957, 4795265, 190869, 136959, 318687,
				106711, 214625, 572349, 196384, 5161414, 137721, 157099, 325800, 88070, 1423190, 174061, 157710, 729520,
				74013, 274009, 205194, 170665, 184516, 190932, 123111, 170701, 198005, 282224, 187935, 189352, 166540,
				555799, 4604811, 229282, 218612, 1064304, 176103, 186203, 200773, 170148, 215732, 170537, 177381,
				163458, 4677643, 1091841, 197351, 4827813, 1066390, 164170, 4606388, 1002683, 165607, 4929269, 987100,
				76752, 796479, 1066800, 2274, 2165, 1942, 1472, 21476, 4416, 1201, 1444, 2276, 998, 12399, 37690, 1179,
				2646, 2489, 1029, 2145, 2321, 2140, 2043, 1445, 981, 1884, 1361, 392061, 1233, 1774, 3920, 92983,
				112505, 4055, 2415, 2318, 2186, 1951, 1300, 22689, 138126, 5674, 8127, 6361, 5353, 3253, 1899, 2983,
				2168, 993, 91309, 1202, 1599, 1400, 2969, 1553, 7046, 31033, 44474, 4685, 4513, 6504, 6158, 3041, 5796,
				1652, 1040, 1368, 2699, 1048, 1428, 1428, 1026, 1121, 1803, 2855, 3530, 8320, 1048, 1045, 1298, 2387,
				1537, 1143, 1880, 1216, 970, 1788, 1484, 1750, 1308, 1658, 971, 1930, 990, 4215, 1136, 2656, 1650, 1451,
				2378, 2805, 7992, 7642, 182896, 179198, 161802, 179693, 157432, 15008, 164239, 210658, 168472, 41891,
				66486, 198735, 93321, 158759, 148850, 150742, 27745, 62944, 40191, 36723, 79590, 71251, 137718, 186739,
				176608, 161147, 37240 };
		chromSize = new ArrayList<Long>();
		for (int x = 0; x < size.length; x++) {
			chromSize.add(Long.valueOf(size[x]));
		}
	}

	/**
	 * Set instance to use mm10 (mouse) genome build. This includes only the
	 * "complete" assembled chromosomes. Excludes the mitochondrial chromosome and
	 * alternative contigs.
	 *
	 * https://hgdownload.soe.ucsc.edu/goldenPath/mm10/bigZips/mm10.chrom.sizes
	 */
	public static void initialize_mm10() {
		genomeName = "mm10";
		genomeSize = (long) 2.725521370E9;
		String[] chrom = {"chr1", "chr2", "chr3", "chr4", "chr5", "chr6", "chr7", "chr8", "chr9", "chr10", "chr11", "chr12", "chr13", "chr14", "chr15", "chr16", "chr17", "chr18", "chr19", "chrX", "chrY"};
		chromName = new ArrayList<String>(Arrays.asList(chrom));
		long[] size = new long[] { 195471971, 182113224, 160039680, 156508116, 151834684, 149736546, 145441459, 129401213, 124595110, 130694993, 122082543, 120129022, 120421639, 124902244, 104043685, 98207768, 94987271, 90702639, 61431566, 171031299, 91744698};
		chromSize = new ArrayList<Long>();
		for (int x = 0; x < size.length; x++) {
			chromSize.add(Long.valueOf(size[x]));
		}
	}

	/**
	 * Generates a random coordinate of a specified interval length and random
	 * direction uniformly sampling from genomeSize
	 *
	 * @param WINDOW window size (interval length) of returned coordinate interval
	 * @return random coordinate interval with chr, start, stop, and direction
	 */
	public GenericCoord generateRandomCoord(int WINDOW) {
		GenericCoord coord = null;
		boolean VALID = false;
		while(!VALID) {
			long location = ThreadLocalRandom.current().nextLong(genomeSize);
			long runningTotal = 0;
			int chromIndex = 0;
			for (int x = 0; x < chromSize.size(); x++) {
				if (location < runningTotal + chromSize.get(x).longValue()) {
					chromIndex = x;
					x = chromSize.size() + 1;
				} else {
					runningTotal += chromSize.get(x).longValue();
				}
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

	/**
	 * Check to make sure WINDOW size is smaller than every chromosome to prevent
	 * infinite loop.
	 *
	 * @param WINDOW the size of the window to check
	 * @return true if WINDOW is smaller than all chr, false if there is at least one chr smaller than WINDOW
	 */
	public boolean isSmaller(int WINDOW) {
		for(int x = 0; x < chromSize.size(); x++) {
			if(WINDOW > chromSize.get(0)) { return false; }
		}
		return true;
	}
}
