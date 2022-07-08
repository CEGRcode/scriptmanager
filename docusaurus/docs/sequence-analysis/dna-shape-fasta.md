---
id: dna-shape-fasta
title: DNA Shape from FASTA File
sidebar_label: dna-shape-fasta
---

![dna-shape-fasta](/../static/icons/SequenceAnalysis/DNAShapefromFASTA_square.svg)

Calculate intrinsic DNA shape parameters given input FASTA files. Based on
Roh's lab DNAshape server data

<img src={require('/../static/md-img/SequenceAnalysis/DNAShapefromFASTAFile.png').default} style={{width:70+'%'}}/> 

Usage:
```bash
java -jar ScriptManager.jar sequence-analysis dna-shape-fasta [-aghlprV]
[--avg-composite] [-o=<outputBasename>] <fastaFile>
```


### Positional Input

Expects a [FASTA][fasta-format] formatted file with many sequences to stack up with each other (like [fasta-extract tool][fasta-extract] output).

### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output=<outputBasename>` | Specify output basename (files for each shape indicated will share this base) |
| `--avg-composite` | Save average composite |

### Shape Options

| Option | Description |
| ------ | ----------- |
| `-g, --groove` | output minor groove width
| `-r, --roll` | output roll
| `-p, --propeller` | output propeller twist
| `-l, --helical` | output helical twist
| `-a, --all` | output groove, roll, propeller twist, and helical twist, equivalent to `-grpl`.

[Roh_paper]:www.pubmed.gov

[cdt-format]:file-formats.md
[fasta-format]:file-formats.md
