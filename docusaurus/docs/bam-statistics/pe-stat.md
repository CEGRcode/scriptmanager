---
id: pe-stat
title: Paired-End Statistics
sidebar_label: pe-stat
---

![pe-stat](/../static/icons/BAMStatistics/Paired-endstatistics_square.svg)


_Generates Insert-size Histogram statistic (GEO requirement) and outputs BAM Header including alignment statistics and parameters given a sorted and indexed (BAI) paired-end BAM File._

<img src={require('/../static/md-img/BAMStatistics/Paired-EndStatistics.png').default} style={{width:70+'%'}}/> 

## Command Line (pe-stat)


Usage:

```bash
script-manager bam-statistics pe-stat <bamFile> [-dhsV] [-n=<MIN_INSERT>]
[-o=<outputBasename>] [-x=<MAX_INSERT>]
```

Description:

Generates Insert-size Histogram statistic (GEO requirement) and outputs BAM
Header including alignment statistics and parameters given a sorted and indexed
(BAI) paired-end BAM File.


### Positional Input

This tool takes a single BAM file for input. As with other tools, this tool requires the BAM file be indexed.


### Output Options

```bash
| Option | Description |
| ------ | ----------- |
| -o, --output=<outputBasename> | specify output basename, default is the BAM input filename without extension |
| -s, --summary | write summary of insert histogram by chromosome (default false) |
| -d, --duplication-stats | calculate duplication statistics if this flag is used (default false) |
```

### Filter Options

| Option | Description |
| ------ | ----------- |
| `-n, --min=<MIN_INSERT>` | histogram range minimum (0 default) |
| `-x, --max=<MAX_INSERT>` | histogram range maximum (1000 default) |
