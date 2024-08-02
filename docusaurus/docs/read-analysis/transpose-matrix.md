---
id: transpose-matrix
title: Transpose Matrix
sidebar_label: transpose-matrix
---

![transpose-matrix](/../static/icons/Read_Analysis/TransposeMatrix_square.svg)

Interchange the rows and columns of a matrix, while optionally preserving labels.

<img src={require('/../static/md-img/Read_Analysis/TransposeMatrixWindow.png').default} style={{width:70+'%'}}/> 

## Command Line Interface

Usage:
```bash
java -jar ScriptManager.jar read-analysis transpose-matrix [-hV] [-z] [-l=<startCOL>]
[-o=<output>] [-r=<startROW>] <matrix>
```

## Positional Input

This tool takes a single matrix file for input.


### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output=<output>` | specify output filename |
| `-z, --compress` | output compressed .gz file |


### Coord Start Options

| Option | Description |
| ------ | ----------- |
| `-r, --start-row` | row to start transposing the matrix (zero indexed) |
| `-l, --start-col` | column to start transposing the matrix (zero indexed) |

[file-format]:file-formats.md