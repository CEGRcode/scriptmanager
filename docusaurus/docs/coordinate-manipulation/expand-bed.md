---
id: expand-bed
title: Expand BED File
sidebar_label: expand-bed
---

Expands input BED file by adding positions to the border or around the center

Usage:
```bash
java -jar ScriptManager.jar coordinate-manipulation expand-bed [-hsV] [-b=<border>]
[-c=<center>] [-o=<output>] <bedFile>
```

### Positional Input

This tool takes a single [BED file][bed-format] for input.


### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output` | specify output directory (default name will be same as original with `.gff` ext) |
| `-s, --stdout` | output gff to STDOUT |



### Expansion Options

| Option | Description |
| ------ | ----------- |
| `-c, --center` | expand from center (default, at 250bp) |
| `-b, --border` | add to border |

[bed-format]:file-formats.md
