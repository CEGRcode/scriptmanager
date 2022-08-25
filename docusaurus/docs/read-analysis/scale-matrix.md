---
id: scale-matrix
title: Scale Matrix
sidebar_label: scale-matrix
---

![scale-matrix](/../static/icons/Read_Analysis/ScaleMatrix_square.svg)

Apply a user-specified scaling factor to tab-delimited matrix data

<img src={require('/../static/md-img/Read_Analysis/ScaleMatrixWindow.png').default} style={{width:70+'%'}}/> 

## Command Line Interface

Usage:
```bash
java -jar ScriptManager.jar read-analysis scale-matrix [-hV] [-l=<startCOL>]
[-o=<output>] [-r=<startROW>] [-s=<scale>] <matrix>
```

## Positional Input

This tool takes a single matrix file for input.


### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output=<output>` | specify output filename |



### Scale Options

| Option | Description |
| ------ | ----------- |
| `-s, --scaling-factor` | scaling factor (default=1) |




### Coord Start Options

| Option | Description |
| ------ | ----------- |
| `-r, --start-row` |  |
| `-l, --start-col` |  |

[file-format]:file-formats.md
