---
id: dna-shape-bed
title: DNA Shape from BED File
sidebar_label: dna-shape-bed
---

![dna-shape-bed](/../static/icons/Sequence_Analysis/DNAShapefromBED_square.svg)

Calculate intrinsic DNA shape parameters given BED file and Genome FASTA file.

<!--![DNA Shape Cheatsheet](IMGADDRESS) -->
<img src={require('/../static/md-img/Sequence_Analysis/DNAShapefromBEDWindow.png').default} style={{width:70+'%'}}/>

Usage:
```bash
java -jar ScriptManager.jar sequence-analysis dna-shape-bed [-afghlprV]
[--avg-composite] [-o=<outputBasename>] <genomeFile> <bedFile>
```

Based on Roh's lab DNAshape server data.
Notes: Sequences with Ns are thrown out.
      `<genomeFile>`      reference genome FASTA file
      `<bedFile>`         the BED file of sequences to extract

### Positional Input

Expects a [FASTA][fasta-format] formatted file with many sequences to stack up with each other (like [fasta-extract tool][fasta-extract] output).


### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output=<outputBasename>` | Specify output basename (files for each shape indicated will share this base) |
| `--avg-composite` | Save average composite |



### Strand Options

| Option | Description |
| ------ | ----------- |
| `-f, --force` | force-strandedness (default) |

### Shape Options

| Option | Description |
| ------ | ----------- |
| `-g, --groove` | output minor groove width
| `-r, --roll` | output roll
| `-p, --propeller` | output propeller twist
| `-l, --helical` | output helical twist
| `-a, --all` | output groove, roll, propeller twist, and helical twist, equivalent to -grpl.

For each shape option to calculate indicated by the command, a [CDT file][cdt-format] will be generated with an extension indicating the shape  type calculated.

If the *groove* information is indicated in the command to be used for the output, a file called `<outputBasename>_MGW.cdt` will be generated.
Similarly for *propeller*, *helical*, and *roll*, the output matrix [CDT files][cdt-format] will be named with the suffixes `_PTwist.cdt`, `_HTwist.cdt`, and `_Roll.cdt`, respectively.

[rohs-paper]:https://pubmed.ncbi.nlm.nih.gov/23703209/
[fasta-extract]:/docs/sequence-analysis/fasta-extract

[cdt-format]:/docs/file-formats#cdt
[bed-format]:/docs/file-formats#bed
[fasta-format]:/docs/file-formats#fasta
