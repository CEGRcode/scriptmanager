---
id: bed-to-gff
title: Convert BED to GFF
sidebar_label: BED to GFF
---

![BEDtoGFF](/../static/icons/Coordinate_Manipulation/BEDtoGFF_square.svg)

Converts [BED file][bed-format]  to [GFF file][gff-format]

<img src={require('/../static/md-img/Coordinate_Manipulation/BEDtoGFFWindow.png').default} style={{width:70+'%'}}/>


## Command Line Interface

Usage:
```bash
java -jar ScriptManager.jar coordinate-manipulation bed-to-gff [-hsV] [-o=<output>]
<bedFile>
```

### Positional Input

This tool takes a single [BED file][bed-format] for input.

### Output Options

| Option | Description |
| ------ | ----------- |
| -o, --output | specify output directory (default name will be same as original with .gff ext) |
| -s, --stdout | output gff to STDOUT |

[bed-format]:/docs/file-formats#bed
[gff-format]:/docs/file-formats#gff
