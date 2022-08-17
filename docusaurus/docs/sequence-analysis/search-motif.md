---
id: search-motif
title: Search Motif
sidebar_label: search-motif
---

![search-motif](/../static/icons/Sequence_Analysis/SearchMotif_square.svg)

Search for an IUPAC DNA sequence motif in FASTA files with mismatches allowed

<img src={require('/../static/md-img/Sequence_Analysis/SearchMotifWindow.png').default} style={{width:70+'%'}}/>

Usage:
```bash
java -jar ScriptManager.jar sequence-analysis search-motif [-hV] -m=<motif>
[-n=<ALLOWED_MISMATCH>] [-o=<output>] <fastaFile>
```

### Positional Input

      `<fastaFile>`         The FASTA file in which to search for the motif.

### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output=<output>` | specify output file |


### Search Options

| Option | Description |
| ------ | ----------- |
| `-m, --motif=<motif>` | the IUPAC motif to search for |
| `-n, --mismatches=<ALLOWED_MISMATCH>` | the number of mismatches allowed (default=0) |_


[fasta-format]:/docs/file-formats#fasta
