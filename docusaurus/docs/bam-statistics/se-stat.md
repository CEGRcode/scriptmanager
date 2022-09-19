---
id: se-stat
title: Single-End Statistics
sidebar_label: se-stat
---

![se-stat](/../static/icons/BAM_Statistics/SEStats_square.svg)

Output BAM Header including alignment statistics and parameters given any indexed (BAI) BAM File.

<img src={require('/../static/md-img/BAM_Statistics/SEStatWindow.png').default} style={{width:70+'%'}}/>

## Command Line Interface

Usage:
```bash
java -jar ScriptManager.jar bam-statistics se-stat <bamFile>
[-hV] [-o=<output>]
```

### Positional Input

This tool takes a single BAM file for input. As with other tools, this tool requires the BAM file be indexed.


### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output=<output>` | Specify output file |
