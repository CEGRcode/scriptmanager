---
id: bam-to-bed
title: BAM to BED
sidebar_label: bam-to-bed
---
import { ToolIcon } from "@site/src/components/ToolIcon";
<ToolIcon>children</ToolIcon>

<!-- <img src={image} alt="" style={{width:70+'%'}}/> -->
![bam-to-bed](/../static/icons/bam-format-converter/BAM-FormatConverter:BAMtoBED.svg)
Convert BAM file to BED file



<img src={require('/../static/md-img/BAMFormatConverter/BAM-FormatConverter_BAMtoBED.png').default} style={{width:70+'%'}}/> 

Usage:
```bash
java -jar ScriptManager.jar bam-format-converter bam-to-bed [-1 | -2 | -a | -m | -f]
[-hpsV] [-n=<MIN_INSERT>] [-o=<output>] [-x=<MAX_INSERT>] <bamFile>
```

### Positional Input

This tool takes a single BAM file for input. As with other tools, this tool requires the BAM file be indexed.

### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output=<output>` | specify output directory (name will be same as original with .bed ext) |
| `-s, --stdout` | stream output file to STDOUT (cannot be used with `-o` flag) |

### Filter Options
These filter options are shared across all the BAM Format Converter tools.

| Option | Description |
| ------ | ----------- |
| `-p, --mate-pair` | require proper mate pair (default not required) |
| `-n, --min-insert=<MIN_INSERT>` | filter by min insert size in bp |
| `-x, --max-insert=<MAX_INSERT>` | filter by max insert size in bp |

### Read Options

| Option | Description |
| ------ | ----------- |
| `-1, --read1` | output read 1 (default) |
| `-2, --read2` | output read 2 |
| `-a, --all-reads` | output combined |
| `-m, --midpoint` | output midpoint (require PE) |
| `-f, --fragment` | output fragment (requires PE) |

