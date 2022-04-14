---
id: tool-group
title: Tool Groups
sidebar_label: Tool Groups
---

## BAM Format Converter

_Description of BAM-Format-Converter tool_

| Tool Name | Input | Output | Description |
| ------------- | ------------- | ------------- | ------------- |
| [`bam-to-scidx`][bam-to-scidx] | [BAM][bam-format] | [scIDX][scidx-format] | Convert BAM file to scIDX file. |
| [`bam-to-gff`][bam-to-gff] | [BAM][bam-format] | [GFF][gff-format] | Convert BAM file to GFF file. |
| [`bam-to-bed`][bam-to-bed] | [BAM][bam-format] | [BED][bed-format] | Convert BAM file to BED file. |
| [`bam-to-bedgraph`][bam-to-bedgraph] | [BAM][bam-format] | [bedGraph][bedgraph-format] | Convert BAM file to bedGraph file. |


## BAM Manipulation

_General description of BAM Manipulation_

| Tool Name | Input | Output | Description |
| ------------- | ------------- | ------------- | ------------- |
| [`bam-indexer`][bam-indexer] | [BAM][bam-format] | [BAI][bai-format] | Generates BAI Index for input BAM files. Output BAI is in the same directory as input BAM file. |
| [`sort-bam`][sort-bam] | [BAM][bam-format] | [BAM][bam-format] | Sort BAM files in order to efficiently extract and manipulate.\nRAM intensive process. If program freezes, increase JAVA heap size. |
| [`remove-duplicates`][remove-duplicates] | [BAM][bam-format] | [BAM][bam-format] | Removes duplicate reads in Paired-End sequencing given identical 5' read locations. RAM intensive process. If program freezes, increase JAVA heap size. |
| [`merge-bam`][merge-bam] | [BAM][bam-format]\[..\] | [BAM][bam-format] | Merges Multiple BAM files into single BAM file. Sorting is performed automatically. RAM intensive process. If program freezes, increase JAVA heap size. |
| [`filter-pip-seq`][filter-pip-seq]| [BAM][bam-format] | [BAM][bam-format] | Filter BAM file by -1 nucleotide. Requires genome FASTA file. Note this program does not index the resulting BAM file and user must use appropriate samtools command to generate BAI. |


[samtools-index]:http://www.htslib.org/doc/samtools-index.html
[samtools-markdup]:http://www.htslib.org/doc/samtools-markdup.html
[samtools-merge]:http://www.htslib.org/doc/samtools-merge.html
[samtools-sort]:http://www.htslib.org/doc/samtools-sort.html
[picard-index]:https://broadinstitute.github.io/picard/command-line-overview.html#BuildBamIndex
[picard-markdup]:https://broadinstitute.github.io/picard/command-line-overview.html#MarkDuplicates
[picard-merge]:https://broadinstitute.github.io/picard/command-line-overview.html#MergeBamAlignment
[picard-sort]:https://broadinstitute.github.io/picard/command-line-overview.html#SortSam


## BAM Statistics

| Tool Name | Input | Output | Description |
| ------------- | ------------- | ------------- | ------------- |
| [se-stat][se-stat] | [BAM][bam-format] | [TXT][txt-format] | Genome-Genome correlations for replicate comparisons given multiple sorted and indexed (BAI) BAM files. |
| [pe-stat][pe-stat] | [BAM][bam-format] | [TXT][txt-format] | Generates Insert-size Histogram statistic (GEO requirement) and outputs BAM Header including alignment statistics and parameters given a sorted and indexed (BAI) paired-end BAM File. |
| [bam-correlation][bam-correlation) | [BAM][bam-format] | [TXT][txt-format] | Output BAM Header including alignment statistics and parameters given any indexed (BAI) BAM File. |


## Coordinate Manipulation

_Coordinate Manipulation tools description_

| Tool Name | Input | Output | Description |
| ------------- | ------------- | ------------- | ------------- |
| [expand-bed][expand-bed] | [BED][bed-format] | [BED][bed-format] | Expands input BED file by adding positions to the border or around the center. |
| [expand-gff][expand-gff] | [GFF][gff-format] | [GFF][gff-format] | Expands input GFF file by adding positions to the border or around the center. |
| [bed-to-gff][bed-to-gff] | [BED][bed-format] | [GFF][gff-format] | Converts BED file format to GFF file format. |
| [gff-to-bed][gff-to-bed] | [GFF][gff-format] | [BED][bed-format] | Converts GFF file format to BED file format. |
| [sort-bed][sort-bed] | [BED][bed-format] | [BED][bed-format] | Sort a CDT file and its corresponding BED file by the total score in the CDT file across the specified interval. |
| [sort-gff][sort-gff] | [GFF][gff-format] | [GFF][gff-format] | Sort a CDT file and its corresponding GFF file by the total score in the CDT file across the specified interval. |


## Figure Generation

| Tool Name | Input | Output | Description |
| ------------- | ------------- | ------------- | ------------- |
| [heatmap][heatmap] | [CDT][cdt-format] | [PNG][png-format] | Generate heatmap using CDT files. |
| [merge-heatmap][merge-heatmap] | [PNG][png-format] + [PNG][png-format] | [PNG][png-format] | Merge Sense and Antisense png heatmaps. |
| [four-color][four-color] | [FASTA][fasta-format] | [PNG][png-format] | Generate 4Color sequence plot given FASTA file and user-defined RGB colors. |
| [composite-plot][composite] | [CDT][cdt-format] | [PNG][png-format] | Generate a Composite Plot PNG from composite data like the output in TagPileup. |


## File Utilities

_File Utilities tools description_

| Tool Name | Input | Output | Description |
| ------------- | ------------- | ------------- | ------------- |
| [md5checksum][md5checksum] | * | [TXT][txt-format] | Calculate MD5 checksum for an input file. |


## Peak Analysis

_Peak Analysis tools description_

| Tool Name | Input | Output | Description |
| ------------- | ------------- | ------------- | ------------- |
| [`peak-align-ref`][peak-align-ref] | [BED(ref)][bed-format] + [BED(peak)][bed-format] | [CDT][cdt-format] | Align BED peaks to Reference BED file creating CDT files for heatmap generation. |
| [`filter-bed`][filter-bed] | [BED][bed-format] | [BED(cluster)][bed-format] + [BED(filter)][bed-format] | Filter BED file using user-specified exclusion zone using the score column to determine which peak to retain. |
| [`tile-genome`][tile-genome] | genomeName | \[[BED][bed-format]\|[GFF][gff-format]\] | Generate a coordinate file that tiles (non-overlapping) across an entire genome. |
| [`rand-coord`][rand-coord] | genomeName | \[[BED][bed-format]\|[GFF][gff-format]\] | Generate a coordinate file that tiles (non-overlapping) across an entire genome. |
| [`signal-dup`][signal-dup] | [GFF][gff-format] + [BAM][bam-format] | [TXT][txt-format] | Calculate duplication statistics at user-specified regions. |


## Read Analysis

These Read Analysis tools are mostly tools that work with the [CDT or other tab-delimited Matrix file formats][file-format].

Most commonly used  is the TagPileup tool.

Others manipulate files to calculate normalization factors, normalize the data, or other manipulations and transformations of read info.

| Tool Name | Input | Output | Description |
| ------------- | ------------- | ------------- | ------------- |
| [`tag-pileup`][tag-pileup] | [BED][bed-format] + [BAM][bam-format] | [CDT][cdt-format] | Pileup 5' ends of aligned tags given BED and BAM files according to user-defined parameters. |
| [`scaling-factor`][scaling-factor] | [BAM][bam-format] | [TXT][txt-format] | Calculate the factor as either total tag normalization or normalization of ChIP-seq data with control. (PMID:22883957) |
| [`scale-matrix`][scale-matrix] | \[[CDT][cdt-format]\|[TAB][mat-format]\] | [TAB][mat-format]\[..\] | Apply a user-specified scaling factor to tab-delimited matrix data. |
| [`aggregate-data`][aggregate-data] | \[[CDT][cdt-format]\|[TAB][mat-format]\]\[..\] | [TAB][mat-format]\[..\] | The AggregateData tool is used to process a bunch of matrix files into one matrix file. |


## Sequence Analysis

_Sequence Analysis tools description_

| Tool Name | Input | Output | Description |
| ------------- | ------------- | ------------- | ------------- |
| [`fasta-extract`][fasta-extract] | [BED][bed-format] + [FASTA(genome)][fasta-format] | [FASTA][fasta-format] | Generate FASTA file from indexed Genome FASTA file and BED file. Script will generate FAI index if not present in Genome FASTA folder. |
| [`randomize-fasta`][randomize-fasta] | [FASTA][fasta-format] | [FASTA][fasta-format] | Randomizes FASTA sequence for each input entry |
| [`search-motif`][search-motif] | [FASTA][fasta-format] | [BED][bed-format] | Search for an IUPAC DNA sequence motif in FASTA files with mismatches allowed. |
| [`dna-shape-bed`][dna-shape-bed] | [BED][bed-format] + [FASTA(genome)][fasta-format] | [CDT][cdt-format] | Calculate intrinsic DNA shape parameters given BED file and Genome FASTA file. Based on Roh's lab [DNAshape server data][Roh_paper]. |
| [`dna-shape-fasta`][dna-shape-fasta] | [FASTA][fasta-format] | [CDT][cdt-format] | Calculate intrinsic DNA shape parameters given input FASTA files. Based on Roh's lab [DNAshape server data][Roh_paper]. |

[Roh_paper]:https://pubmed.ncbi.nlm.nih.gov/23703209


[bam-to-bedgraph]:bam-format-converter/bam-to-bedgraph.md
[bam-to-bed]:bam-format-converter/bam-to-bed.md
[bam-to-gff]:bam-format-converter/bam-to-gff.md
[bam-to-scidx]:bam-format-converter/bam-to-scidx.md

[bam-indexer]:bam-manipulation/bam-indexer.md
[filter-pip-seq]:bam-manipulation/filter-pip-seq.md
[merge-bam]:bam-manipulation/merge-bam.md
[remove-duplicates]:bam-manipulation/remove-duplicates.md
[sort-bam]:bam-manipulation/sort-bam.md

[bam-correlation]:bam-statistics/bam-correlation.md
[pe-stat]:bam-statistics/pe-stat.md
[se-stat]:bam-statistics/se-stat.md

[bed-to-gff]:coordinate-manipulation/bed-to-gff.md
[gff-to-bed]:coordinate-manipulation/gff-to-bed.md
[expand-bed]:coordinate-manipulation/expand-bed.md
[expand-gff]:coordinate-manipulation/expand-gff.md
[sort-bed]:coordinate-manipulation/sort-bed.md
[sort-gff]:coordinate-manipulation/sort-gff.md

[composite]:figure-generation/composite-plot.md
[four-color]:figure-generation/four-color.md
[heatmap]:figure-generation/heatmap.md
[merge-heatmap]:figure-generation/merge-heatmap.md

[md5checksum]:file-utilities/md5checksum.md

[filter-bed]:peak-analysis/filter-bed.md
[peak-align-ref]:peak-analysis/peak-align-ref.md
[rand-coord]:peak-analysis/rand-coord.md
[signal-dup]:peak-analysis/signal-dup.md
[tile-genome]:peak-analysis/tile-genome.md

[aggregate-data]:read-analysis/aggregate-data.md
[scale-matrix]:read-analysis/scale-matrix.md
[scaling-factor]:read-analysis/scaling-factor.md
[similarity-matrix]:read-analysis/similarity-matrix.md
[tag-pileup]:read-analysis/tag-pileup.md

[dna-shape-bed]:sequence-analysis/dna-shape-bed.md
[dna-shape-fasta]:sequence-analysis/dna-shape-fasta.md
[fasta-extract]:sequence-analysis/fasta-extract.md
[randomize-fasta]:sequence-analysis/randomize-fasta.md
[search-motif]:sequence-analysis/search-motif.md

[bai-format]:file-formats.md
[bam-format]:file-formats.md
[bed-format]:file-formats.md
[bedgraph-format]:file-formats.md
[cdt-format]:file-formats.md
[fasta-format]:file-formats.md
[gff-format]:file-formats.md
[mat-format]:file-formats.md
[png-format]:file-formats.md
[txt-format]:file-formats.md
[scidx-format]:file-formats.md
