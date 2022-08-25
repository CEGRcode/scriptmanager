import React from "react";
import useBaseUrl from '@docusaurus/useBaseUrl';

// Based on https://theochu.com/docusaurus/sortable-tables/
// ----------------------------------------------------------------------------
// Please respect alphabetical (name) order when adding new entries.
// ----------------------------------------------------------------------------

// Suspended: gene-track, peak-pair, and similarity-matrix

export const toolIndex = [
  {
    name: "Aggregate Data",
    group: "RA",
    description: "Compile data from tab-delimited file into matrix according to user-specified metric.",
    command: "-",
    url: "docs/read-analysis/aggregate-data",
  },
  {
    name: "BAM Correlation",
    group: "BS",
    description: "Output BAM Header including alignment statistics and parameters given any indexed (BAI) BAM File.",
    command: "-",
    url: "docs/bam-statistics/bam-correlation",
  },
  {
    name: "BAM Indexer",
    group: "BM",
    description: "For most tools using BAM inputs (both within and without ScriptManager), a BAM index file (.bai) file is required so that the tool can efficiently query the file for alignment records.",
    command: "-",
    url: "docs/bam-manipulation/bam-indexer",
  },
  {
    name: "BAM to bedGraph",
    group: "BF",
    description: "Convert BAM file to bedGraph file",
    command: "-",
    url: "docs/bam-format-converter/bam-to-bedgraph",
  },
  {
    name: "BAM to BED",
    group: "BF",
    description: "Convert BAM file to BED file",
    command: "-",
    url: "docs/bam-format-converter/bam-to-bed",
  },
  {
    name: "BAM to GFF",
    group: "BF",
    description: "Convert BAM file to GFF file",
    command: "-",
    url: "docs/bam-format-converter/bam-to-gff",
  },
  {
    name: "BAM to scIdx",
    group: "BF",
    description: "Convert BAM file to scIDX file",
    command: "-",
    url: "docs/bam-format-converter/bam-to-scidx",
  },
  {
    name: "BED to GFF",
    group: "CM",
    description: "Converts BED file to GFF file",
    command: "-",
    url: "docs/coordinate-manipulation/bed-to-gff",
  },
  {
    name: "Composite Line Plot",
    group: "FG",
    description: "Generate a Composite Plot PNG from composite data like the output in TagPileup",
    command: "-",
    url: "docs/figure-generation/composite-plot",
  },
  {
    name: "DNA shape from BED",
    group: "SA",
    description: "Calculate intrinsic DNA shape parameters given BED file and Genome FASTA file.",
    command: "-",
    url: "docs/sequence-analysis/dna-shape-bed",
  },
  {
    name: "DNA shape from FASTA",
    group: "SA",
    description: "Calculate intrinsic DNA shape parameters given input FASTA files. Based on Roh's lab DNAshape server data",
    command: "-",
    url: "docs/sequence-analysis/dna-shape-fasta",
  },
  {
    name: "Expand BED",
    group: "CM",
    description: "Expands input BED file by adding positions to the border or around the center.",
    command: "-",
    url: "docs/coordinate-manipulation/expand-bed",
  },
  {
    name: "Expand GFF",
    group: "CM",
    description: "Expands input GFF file by adding positions to the border or around the center",
    command: "-",
    url: "docs/coordinate-manipulation/expand-gff",
  },
  {
    name: "Extract FASTA",
    group: "SA",
    description: "Generate FASTA file from indexed Genome FASTA file and BED file. Script will generate FAI index if not present in Genome FASTA folder.",
    command: "-",
    url: "docs/sequence-analysis/fasta-extract",
  },
  {
    name: "Filter BED by Proximity",
    group: "PA",
    description: "Filter BED file using user-specified exclusion zone using the score column to determine which peak to retain.",
    command: "-",
    url: "docs/peak-analysis/filter-bed",
  },
  {
    name: "Filter PIP-seq",
    group: "BM",
    description: "Filter BAM file by -1 nucleotide. Requires genome FASTA file. Note this program does not index the resulting BAM file and user must use appropriate samtools command to generate BAI.",
    command: "-",
    url: "docs/bam-manipulation/filter-pip-seq",
  },
  {
    name: "Four Color Plot",
    group: "FG",
    description: "Generate 4Color sequence plot given FASTA file and user-defined RGB colors.",
    command: "-",
    url: "docs/figure-generation/four-color",
  },
  {
    name: "GFF to BED",
    group: "CM",
    description: "Converts GFF file format to BED file format",
    command: "-",
    url: "docs/coordinate-manipulation/gff-to-bed",
  },
  {
    name: "Two Color Heatmap Plot",
    group: "FG",
    description: "This tool generates a heatmap from a tab-delimited matrix input of numeric values.",
    command: "-",
    url: "docs/figure-generation/heatmap",
  },
  {
    name: "MD5 Checksum",
    group: "FU",
    description: "A common quality control and security step that everyone should use when downloading files from another source is to compare MD5 checksum values to ensure that the file that was downloaded exactly matches the reported MD5checksum value for the file.",
    command: "-",
    url: "docs/file-utilities/md5checksum",
  },
  {
    name: "Merge BAM Replicates",
    group: "BM",
    description: "Merge multiple sorted alignment files, producing a single sorted output file that contains all the input records and maintains the existing sort order.",
    command: "-",
    url: "docs/bam-manipulation/merge-bam",
  },
  {
    name: "Merge Heatmaps",
    group: "FG",
    description: "This tool merges two PNG files into a third PNG file that is an average of each corresponding pair of pixels from the input files.",
    command: "-",
    url: "docs/figure-generation/merge-heatmap",
  },
  {
    name: "Paired-end Statistics",
    group: "BS",
    description: "Generates Insert-size Histogram statistic (GEO requirement) and outputs BAM Header including alignment statistics and parameters given a sorted and indexed (BAI) paired-end BAM File.",
    command: "-",
    url: "docs/bam-statistics/pe-stat",
  },
	{
		name: "Align BED to Reference",
		group: "PA",
		description: "Align BED peaks to Reference BED file creating CDT files for heatmap generation",
		command: "-",
		url: "docs/peak-analysis/peak-align-ref",
	},
	{
		name: "Random Coordinates",
		group: "PA",
		description: "Generate a coordinate file that tiles (non-overlapping) across an entire genome.",
		command: "-",
		url: "docs/peak-analysis/rand-coord",
	},
	{
		name: "Randomize FASTA",
		group: "SA",
		description: "Randomizes FASTA sequence for each input entry",
		command: "-",
		url: "docs/sequence-analysis/randomize-fasta",
	},
	{
	  name: "Mark (Remove) Duplicates",
	  group: "BM",
	  description: "This tool locates and tags duplicate reads in a BAM or SAM file, where duplicate reads are defined as originating from a single fragment of DNA.",
	  command: "Picard",
	  url: "docs/bam-manipulation/remove-duplicates",
	},
	{
		name: "Scale Matrix",
		group: "RA",
		description: "Apply a user-specified scaling factor to tab-delimited matrix data",
		command: "-",
		url: "docs/read-analysis/scale-matrix",
	},
	{
		name: "Calculate Scaling Factor",
		group: "RA",
		description: "Calculate the factor as either total tag normalization or normalization of ChIP-seq data with control (PMID:22883957)",
		command: "-",
		url: "docs/read-analysis/scaling-factor",
	},
	{
		name: "Single-end Statistics",
		group: "BS",
		description: "Genome-Genome correlations for replicate comparisons given multiple sorted and indexed (BAI) BAM files.",
		command: "-",
		url: "docs/bam-statistics/se-stat",
	},
	{
		name: "Search Motif",
		group: "SA",
		description: "Search for an IUPAC DNA sequence motif in FASTA files with mismatches allowed",
		command: "-",
		url: "docs/sequence-analysis/search-motif",
	},
	{
		name: "Signal Duplication",
		group: "PA",
		description: "Calculate duplication statistics at user-specified regions.",
		command: "-",
		url: "docs/peak-analysis/signal-dup",
	},
	{
		name: "Sort BAM",
		group: "BM",
		description: "Sort alignments by leftmost coordinates, or by read name when -n is used. An appropriate @HD-SO sort order header tag will be added or an existing one updated if necessary.",
		command: "-",
		url: "docs/bam-manipulation/sort-bam",
	},
	{
		name: "Sort BED",
		group: "CM",
		description: "Sort a CDT file and its corresponding BED file by the total score in the CDT file across the specified interval",
		command: "-",
		url: "docs/coordinate-manipulation/sort-bed",
	},
	{
		name: "Sort GFF",
		group: "CM",
		description: "Sort a CDT file and its corresponding GFF file by the total score in the CDT file across the specified interval",
		command: "-",
		url: "docs/coordinate-manipulation/sort-gff",
	},
	{
		name: "Tag Pileup",
		group: "RA",
		description: "Pileup 5' ends of aligned tags given BED and BAM files according to user-defined parameters",
		command: "-",
		url: "docs/read-analysis/tag-pileup",
	},
	{
		name: "Tile Genome",
		group: "PA",
		description: "Generate a coordinate file that tiles (non-overlapping) across an entire genome.",
		command: "-",
		url: "docs/peak-analysis/tile-genome",
	},
];


// ----------------------------------------------------------------------------
// PesterDataTable column definition
// ----------------------------------------------------------------------------
export const columns = [
  {
    Header: "Group",
    accessor: "group",
    className: "pester-data-table left",
    Cell: ({ cell: { value }, row: { original } }) => (
      <b> {value} </b>
    ),
  },
  {
    Header: "Tool Name",
    accessor: "name",
    className: "pester-data-table left",
    Cell: ({ cell: { value }, row: { original } }) => (
      <a href={useBaseUrl(`${original.url}`)} target="blank" rel="noreferrer noopener">
        {value}
      </a>
    ),
  },
  {
    Header: "Type",
    accessor: "command",
    className: "pester-data-table left",
  },
  {
    Header: "Description",
    accessor: "description",
    className: "pester-data-table",
  },
];
