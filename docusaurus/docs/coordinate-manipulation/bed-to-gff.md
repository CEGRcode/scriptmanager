---
id: bed-to-gff
title: Convert BED to GFF
sidebar_label: bed-to-gff
---

Converts [BED file][bed-format]  to [GFF file][gff-format]

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

[bed-format]:file-formats.md
[gff-format]:file-formats.md
