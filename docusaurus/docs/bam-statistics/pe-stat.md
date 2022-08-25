---
id: pe-stat
title: Paired-End Statistics
sidebar_label: pe-stat
---

![pe-stat](/../static/icons/BAM_Statistics/PEStats_square.svg)

Generates Insert-size Histogram statistic (GEO requirement) and outputs BAM Header including alignment statistics and parameters given a sorted and indexed (BAI) paired-end BAM File.

<img src={require('/../static/md-img/BAM_Statistics/PEStatWindow.png').default} style={{width:70+'%'}}/>

This tool processes each input BAM file by calculating and tallying the insert-size of every single read pair.



:::caution
Make sure your BAM input files are [sorted][sort-bam] and [indexed][bam-indexer].
:::

## Command Line Interface
Usage:

```bash
script-manager bam-statistics pe-stat <bamFile> [-dhsV] [-n=<MIN_INSERT>]
[-o=<outputBasename>] [-x=<MAX_INSERT>]
```

### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output=<outputBasename>` | specify output basename, default is the BAM input filename without extension |
| `-s, --summary` | write summary of insert histogram by chromosome (default false) |
| `-d, --duplication-stats` | calculate duplication statistics if this flag is used (default false) |


### Filter Options

| Option | Description |
| ------ | ----------- |
| `-n, --min=<MIN_INSERT>` | histogram range minimum (0 default) |
| `-x, --max=<MAX_INSERT>` | histogram range maximum (1000 default) |


[sort-bam]:/docs/bam-manipulation/sort-bam
[bam-indexer]:/docs/bam-manipulation/bam-indexer
