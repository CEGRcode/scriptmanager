---
id: signal-dup
title: Signal Duplication
sidebar_label: signal-dup
---

Calculate duplication statistics at user-specified regions.

Usage:
```bash
java -jar ScriptManager.jar peak-analysis signal-dup [-hV] [-o=<output>]
[-w=<window>] <bamFile> <gffFile>
```

| Input | Description |
| ------ | ----------- |
| `<bamFile>` | The BAM file whose statistics we want. |
| `<gffFile>` | The GFF file indicating the region in which to calculate duplication statistics |

### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output=<output>` | specify output file |



| Option | Description |
| ------ | ----------- |
| `-w, --window=<window>` | size of signal window around center in bp (default=100) |
