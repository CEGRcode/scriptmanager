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
    description: "blah blah blah",
    command: "-",
    url: "docs/read-analysis/aggregate-data",
  },
  {
    name: "BAM Correlation",
    group: "BS",
    description: "blah blah blah",
    command: "-",
    url: "docs/bam-statistics/bam-correlation",
  },
  {
    name: "BAM Indexer",
    group: "BM",
    description: "blah blah blah",
    command: "-",
    url: "docs/bam-manipulation/bam-indexer",
  },
  {
    name: "BAM to bedGraph",
    group: "BF",
    description: "blah blah blah",
    command: "-",
    url: "docs/bam-format-converter/bam-to-bedgraph",
  },
  {
    name: "BAM to BED",
    group: "BF",
    description: "blah blah blah",
    command: "-",
    url: "docs/bam-format-converter/bam-to-bed",
  },
  {
    name: "BAM to GFF",
    group: "BF",
    description: "blah blah blah",
    command: "-",
    url: "docs/bam-format-converter/bam-to-gff",
  },
  {
    name: "BAM to scIdx",
    group: "BF",
    description: "blah blah blah",
    command: "-",
    url: "docs/bam-format-converter/bam-to-scidx",
  },
  {
    name: "BED to GFF",
    group: "CM",
    description: "blah blah blah",
    command: "-",
    url: "docs/coordinate-manipulation/bed-to-gff",
  },
  {
    name: "Composite Line Plot",
    group: "FG",
    description: "blah blah blah",
    command: "-",
    url: "docs/figure-generation/composite-plot",
  },
  {
    name: "DNA shape from BED",
    group: "SA",
    description: "blah blah blah",
    command: "-",
    url: "docs/sequence-analysis/dna-shape-bed",
  },
  {
    name: "DNA shape from FASTA",
    group: "SA",
    description: "blah blah blah",
    command: "-",
    url: "docs/sequence-analysis/dna-shape-fasta",
  },
  {
    name: "Expand BED",
    group: "CM",
    description: "blah blah blah",
    command: "-",
    url: "docs/coordinate-manipulation/expand-bed",
  },
  {
    name: "Expand GFF",
    group: "CM",
    description: "blah blah blah",
    command: "-",
    url: "docs/coordinate-manipulation/expand-gff",
  },
  {
    name: "DNA shape from FASTA",
    group: "SA",
    description: "blah blah blah",
    command: "-",
    url: "docs/sequence-analysis/fasta-extract",
  },
  {
    name: "Filter BED by Proximity",
    group: "PA",
    description: "blah blah blah",
    command: "-",
    url: "docs/peak-analysis/filter-bed",
  },
  {
    name: "Filter PIP-seq",
    group: "BM",
    description: "blah blah blah",
    command: "-",
    url: "docs/bam-manipulation/filter-pip-seq",
  },
  {
    name: "Four Color Plot",
    group: "FG",
    description: "blah blah blah",
    command: "-",
    url: "docs/figure-generation/four-color",
  },
  {
    name: "GFF to BED",
    group: "CM",
    description: "blah blah blah",
    command: "-",
    url: "docs/coordinate-manipulation/gff-to-bed",
  },
  {
    name: "Two Color Heatmap Plot",
    group: "FG",
    description: "blah blah blah",
    command: "-",
    url: "docs/figure-generation/heatmap",
  },
  {
    name: "MD5 Checksum",
    group: "FU",
    description: "blah blah blah",
    command: "-",
    url: "docs/file-utilities/md5checksum",
  },
  {
    name: "Merge BAM Replicates",
    group: "BM",
    description: "blah blah blah",
    command: "-",
    url: "docs/bam-manipulation/,merge-bam",
  },
  {
    name: "Merge Heatmaps",
    group: "FG",
    description: "blah blah blah",
    command: "-",
    url: "docs/figure-generation/merge-heatmap",
  },
  {
    name: "Paired-end Statistics",
    group: "BS",
    description: "blah blah blah",
    command: "-",
    url: "docs/bam-statistics/pe-stat",
  },
	{
		name: "Align BED to Reference",
		group: "PA",
		description: "blah blah blah",
		command: "-",
		url: "docs/peak-analysis/peak-align-ref",
	},
	{
		name: "Random Coordinates",
		group: "PA",
		description: "blah blah blah",
		command: "-",
		url: "docs/peak-analysis/rand-coord",
	},
	{
		name: "Randomize FASTA",
		group: "SA",
		description: "blah blah blah",
		command: "-",
		url: "docs/bam-manipulation/randomize-fasta",
	},
	{
	  name: "Mark (Remove) Duplicates",
	  group: "BM",
	  description: "blah blah blah",
	  command: "Picard",
	  url: "docs/bam-manipulation/remove-duplicates",
	},
	{
		name: "Scale Matrix",
		group: "RA",
		description: "blah blah blah",
		command: "-",
		url: "docs/read-analysis/scale-matrix",
	},
	{
		name: "Calculate Scaling Factor",
		group: "RA",
		description: "blah blah blah",
		command: "-",
		url: "docs/read-analysis/scaling-factor",
	},
	{
		name: "Single-end Statistics",
		group: "BS",
		description: "blah blah blah",
		command: "-",
		url: "docs/bam-statistics/se-stat",
	},
	{
		name: "Search Motif",
		group: "SA",
		description: "blah blah blah",
		command: "-",
		url: "docs/bam-manipulation/search-motif",
	},
	{
		name: "Signal Duplication",
		group: "PA",
		description: "blah blah blah",
		command: "-",
		url: "docs/peak-analysis/signal-dup",
	},
	{
		name: "Sort BAM",
		group: "BM",
		description: "blah blah blah",
		command: "-",
		url: "docs/bam-manipulation/sort-bam",
	},
	{
		name: "Sort BED",
		group: "CM",
		description: "blah blah blah",
		command: "-",
		url: "docs/coordinate-manipulation/sort-bed",
	},
	{
		name: "Sort GFF",
		group: "CM",
		description: "blah blah blah",
		command: "-",
		url: "docs/coordinate-manipulation/sort-gff",
	},
	{
		name: "Tag Pileup",
		group: "RA",
		description: "blah blah blah",
		command: "-",
		url: "docs/read-analysis/tag-pileup",
	},
	{
		name: "Tile Genome",
		group: "PA",
		description: "blah blah blah",
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
