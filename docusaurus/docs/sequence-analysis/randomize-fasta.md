---
id: randomize-fasta
title: Randomize FASTA
sidebar_label: randomize-fasta
---

![randomize-fasta](/../static/icons/SequenceAnalysis/RandomizeFASTA_square.svg)

Randomizes FASTA sequence for each input entry

<img src={require('/../static/md-img/SequenceAnalysis/RandomizeFASTA.png').default} style={{width:70+'%'}}/> 

Usage:
```bash
java -jar ScriptManager.jar sequence-analysis randomize-fasta [-hV] [-o=<output>]
<fastaFile>
```

### Positional Input

      `<fastaFile>`         the FASTA file


### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output=<output>` | specify output directory (name will be same as original with .gff ext) |



[fasta-format]:file-formats.md
