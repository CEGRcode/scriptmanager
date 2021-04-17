---
id: se-stat
title: Single-End Statistics
sidebar_label: se-stat
---

_Genome-Genome correlations for replicate comparisons given multiple sorted and indexed (BAI) BAM files._

## Command Line (se-stat)

Usage:
```bash
java -jar ScriptManager.jar bam-statistics se-stat <bamFile>
[-hV] [-o=<output>]
```

Description:

Output BAM Header including alignment statistics and parameters given any
indexed (BAI) BAM File.


### Positional Input

This tool takes a single BAM file for input. As with other tools, this tool requires the BAM file be indexed.


### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output=<output>` | Specify output file |
