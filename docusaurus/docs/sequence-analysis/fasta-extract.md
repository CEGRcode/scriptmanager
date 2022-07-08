---
id: fasta-extract
title: Extract FASTA
sidebar_label: fasta-extract
---

![fasta-extract](/../static/icons/SequenceAnalysis/FASTAfromBED_square.svg)

Generate FASTA file from indexed Genome FASTA file and BED file. Script will generate FAI index if not present in Genome FASTA folder.

<img src={require('/../static/md-img/SequenceAnalysis/ExtractFASTA.png').default} style={{width:70+'%'}}/> 

Usage:
```bash
java -jar ScriptManager.jar sequence-analysis fasta-extract [-cfhV] [-o=<output>]
<genomeFile> <bedFile>
```

### Positional Input

The first positional input

      `<genomeFile>`        reference genome FASTA file
      `<bedFile>`           the BED file of sequences to extract

### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output=<output>` | Specify output file |


### Extract Options

| Option | Description |
| ------ | ----------- |
| `-c, --coord-header` | use genome coordinate for output FASTA header (default is to use bed file headers) |
| `-f, --force` | force-strandedness (default) |

[fasta-format]:file-formats.md
