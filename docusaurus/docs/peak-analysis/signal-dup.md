---
id: signal-dup
title: Signal Duplication (suspended)
sidebar_label: Signal Duplication (suspended)
---
:::warning
Signal Duplication is still under development and not yet actively supported.
:::

Calculate duplication statistics at user-specified regions.

<img src={require('/../static/md-img/Peak_Analysis/SignalDuplicationWindow.png').default} style={{width:70+'%'}}/>

## Command Line Interface

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
