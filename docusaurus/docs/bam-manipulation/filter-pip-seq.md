---
id: filter-pip-seq
title: Filter PIPseq
sidebar_label: filter-pip-seq
---

Usage:
```bash
java -jar ScriptManager.jar bam-manipulation filter-pip-seq [-hV] [-f=<filterString>]
[-o=<output>] <bamFile> <genomeFASTA>
```

Description:

Filter BAM file by -1 nucleotide. Requires genome FASTA file. Note this program does not index the resulting BAM file and user must use appropriate samtools command to generate BAI.


### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output=<output>` | specify output file (default=`<bamFileNoExt>_PSfilter.bam`) |
| `-f, --filter=<filterString>` | filter by upstream sequence, works only for single-nucleotide A,T,C, or G. (default seq='T')|
