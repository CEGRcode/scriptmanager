---
id: sort-bam
title: Sort BAM
sidebar_label: Sort BAM
---
![sort-bam](/../static/icons/BAM_Manipulation/BAMFileSort_square.svg)

Sort BAM files in order to efficiently extract and manipulate. RAM intensive process. If program freezes, increase JAVA heap size.

<img src={require('/../static/md-img/BAM_Manipulation/SortBAMWindow.png').default} style={{width:70+'%'}}/>

Many bioinformatic files require sorting BAM files so that they can be efficiently parsed. It is good practice to keep your BAM files sorted.


## Command Line Interface (Picard and Samtools)
_CommandLine tools already exist for this function. This tool only exists as a GUI wrapper in ScriptManager._

Please see the [Samtools sort tool][samtools-sort] or the [Picard SortSam tool][picard-sort].

[samtools-sort]:http://www.htslib.org/doc/samtools-sort.html
[picard-sort]:https://broadinstitute.github.io/picard/command-line-overview.html#SortSam
