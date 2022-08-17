---
id: bam-indexer
title: BAM Indexer
sidebar_label: BAM Indexer
---
![bam-indexer](/../static/icons/BAM_Manipulation/BAIIndexer_square.svg)

For most tools using BAM inputs (both within and without ScriptManager), a BAM index file (`.bai`) file is required so that the tool can efficiently query the file for alignment records. _Read more in the [Picard documentation][picard-index]_.

ScriptManager's [TagPileup][tag-pileup], [Merge BAM replicates][merge-bam], [BAM Correlation][bam-correlation], and BAM Format Converter tools ([bam-to-bed][bam-to-bed], [bam-to-gff][bam-to-gff], [bam-to-bedgraph][bam-to-bedgraph], and [bam-to-scidx][bam-to-scidx]) are some example tools that require a `.bai` file.

<img src={require('/../static/md-img/BAM_Manipulation/BAIIndexerWindow.png').default} style={{width:70+'%'}}/>

After clicking "Index", ScriptManager will index all of the loaded index files and save them to the "Output Directory" location with the `.bai` extension. Output files follow convention in naming the `.bai` file. If you are indexing the file `sample123.bam`, then the index file will be called `sample123.bam.bai` and located within the same directory.

:::tip

It is standard practice to generate and save the index file in the same place with the same name as the `.bam` file it is indexing so that your bioinformatics tools can find it.

:::

:::caution

BAM file **MUST** be [sorted][sort-bam] to successfully index.

:::

## Command Line Interface (Picard and Samtools)
_CommandLine tools already exist for this function. This tool only exists as a GUI wrapper in ScriptManager._

Please see the [Samtools index tool][samtools-index] or the [Picard BuildBamIndex tool][picard-index].


[samtools-index]:http://www.htslib.org/doc/samtools-index.html
[picard-index]:https://broadinstitute.github.io/picard/command-line-overview.html#BuildBamIndex

[bam-correlation]:/docs/bam-statistics/bam-correlation.md
[bam-to-bedgraph]:/docs/bam-format-converter/bam-to-bedgraph.md
[bam-to-bed]:/docs/bam-format-converter/bam-to-bed.md
[bam-to-gff]:/docs/bam-format-converter/bam-to-gff.md
[bam-to-scidx]:/docs/bam-format-converter/bam-to-scidx.md
[bed-to-gff]:/docs/coordinate-manipulation/bed-to-gff.md
[merge-bam]:/docs/bam-manipulation/merge-bam.md
[sort-bam]:/docs/bam-manipulation/sort-bam
[tag-pileup]:/docs/read-analysis/tag-pileup.md
