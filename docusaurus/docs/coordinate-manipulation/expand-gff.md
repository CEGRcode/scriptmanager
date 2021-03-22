---
id: expand-gff
title: Expand GFF File
sidebar_label: expand-gff
---


Expands input GFF file by adding positions to the border or around the center

Usage:
```bash
java -jar ScriptManager.jar coordinate-manipulation expand-gff [-hsV] [-b=<border>]
[-c=<center>] [-o=<output>] <gffFile>
```

### Positional Input

This tool takes a single [GFF file][gff-format] for input.



### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output` | specify output directory (default name will be same as original with `.bed` ext) |
| `-s, --stdout` | output bed to STDOUT |


### Expansion Options

| Option | Description |
| ------ | ----------- |
| `-c, --center` | expand from center (default, at 250bp) |
| `-b, --border` | add to border |


[gff-format]:file-formats.md
