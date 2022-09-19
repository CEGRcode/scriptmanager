---
id: sort-gff
title: Sort GFF by CDT
sidebar_label: sort-gff
---

![sort-gff](/../static/icons/Coordinate_Manipulation/SortGFF_sqaure.svg)

Sort a CDT file and its corresponding GFF file by the total score in the CDT file across the specified interval

<img src={require('/../static/md-img/Coordinate_Manipulation/SortGFFWindow.png').default} style={{width:70+'%'}}/>


## Command Line Interface

Usage:
```bash
java -jar ScriptManager.jar coordinate-manipulation sort-gff [-hV] [-c=<center>]
[-o=<outputBasename>] [-x=<index> <index>]... <gffFile> <cdtReference>
```

### Positional Input

| Input | Description |
| ------ | ----------- |
| `<gffFile>` | the GFF file to sort |
| `<cdtReference>` | the reference [CDT][cdt-format] file to sort the input by |


### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output=<outputBasename>` | specify output file basename (no .`cdt`/`.gff` extension, script will add that) |



### Sort Options

These options indicate which windows to sort the files by (choose one).

| Option | Description |
| ------ | ----------- |
| `-c, --center=<center>` | sort by center on the input size of expansion in bins (default=100) |
| `-x, --index=<index> <index>` | sort by index from the specified start to the specified stop (0-indexed and half-open interval) |


[cdt-format]:/docs/file-formats#cdt
[gff-format]:/docs/file-formats#gff
