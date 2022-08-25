---
id: remove-duplicates
title: Mark Duplicates (Picard)
sidebar_label: Mark Duplicates
---

![remove-duplicates](/../static/icons/BAM_Manipulation/BAMMarkDuplicates_square.svg)

Removes or marks duplicate reads in paired-end sequencing given identical 5' read positions. _Read more in the [Picard documentation][picard-markdup]_.

<img src={require('/../static/md-img/BAM_Manipulation/BAMMarkDupWindow.png').default} style={{width:70+'%'}}/>

### File inputs (BAM list)

This script filters BAM-type files so make sure your input is properly formatted and uses the appropriate `.bam` extension. The script also supports bulk selection and processing of files.

### Output file (BAM)
The output filename for this script is based on a user-customizable text field that defaults to `merged_BAM.bam`.

:::tip
Make sure if you change the filename that you keep the `.bam` file extension.
:::

### Upstream sequence
The sequence upstream of the 5' end of read 1 to check for and filter by. If the sequence in the reference genome upstream of the 5'end of read 1 matches this sequence, the read-pair information is written to the output BAM file.

:::caution

For classic PIP-seq datasets the default "T" sequence should be used.

:::

This tool supports different sequences in the event an as of yet unknown future biochemical assay or analysis requires this filtering based on a different sequence. For example, a user investigating and comparing the rates of permanganate oxidation and piperdine cleavage at other nucelotides might compare BAM files filtered by other upstream sequences such as "C" which is known to be cleaved under such treatment (just not as frequently as at "T").

### Generate BAI file (GUI only)
By checking this box, the script will automatically generate a BAI index file for each new filtered BAM file.

:::note
The CLI cannot index the resulting BAM file. The user must use appropriate [samtools][samtools-index]/[Picard][picard-index] command to generate BAI.
:::


## Command Line Interface (Picard and Samtools)
_CommandLine tools already exist for this function. This tool only exists as a GUI wrapper in ScriptManager._

Please see the [Samtools markdup tool][samtools-markdup] or the [Picard MarkDuplicates tool][picard-markdup] for a command line tool that performs this function.

[samtools-markdup]:http://www.htslib.org/doc/samtools-markdup.html
[picard-markdup]:https://broadinstitute.github.io/picard/command-line-overview.html#MarkDuplicates
