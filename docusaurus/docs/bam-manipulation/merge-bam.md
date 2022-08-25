---
id: merge-bam
title: Merge BAM
sidebar_label: Merge BAM
---

![merge-bam](/../static/icons/BAM_Manipulation/MergeSamFiles_square.svg)

Merges Multiple BAM files into single BAM file. Sorting is performed automatically. RAM intensive process. If program freezes, increase JAVA heap size.

<img src={require('/../static/md-img/BAM_Manipulation/MergeBAMWindow.png').default} style={{width:70+'%'}}/>

This is frequently used for replicate merging. All input files loaded will be saved to a merged BAM file with the default `merged_BAM.bam` but user-customizable filename in the "Output Directory".

:::tip

Make sure to keep the `.bam` file extension to follow bioinformatics best practices.

:::

### Use multiple CPUs
User may speed up the merging by checking this box to allow threading for parallelization of the merge and sort algorithms.

### Generate BAI file
By checking this box, the script will automatically generate a BAI index file for each new filtered BAM file.

:::note
The CLI cannot index the resulting BAM file. The user must use appropriate [samtools][samtools-index]/[Picard][picard-index] command to generate BAI.
:::

## Command Line Interface (Picard and Samtools)
_CommandLine tools already exist for this function. This tool only exists as a GUI wrapper in ScriptManager._

Please see the [Samtools merge tool][samtools-merge] or the [Picard MergeBamAlignment tool][picard-merge] for a command line tool that performs this function.


[samtools-merge]:http://www.htslib.org/doc/samtools-merge.html
[picard-merge]:https://broadinstitute.github.io/picard/command-line-overview.html#MergeBamAlignment
