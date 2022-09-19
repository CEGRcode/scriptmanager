---
id: expand-bed
title: Expand BED File
sidebar_label: Expand BED
author: Olivia Lang
---

![ExpandBED](/../static/icons/Coordinate_Manipulation/ExpandBED_square.svg)

Expands input BED file by adding positions to the border or around the center.

![ExpandBED_center_500bp](/../static/md-img/ExpandBED_center_500bp.png)

This script is related to several [Bedtools functions][bedtools-slop] and is typically used to expand a set of peak annotations from the center to create a set of genomic intervals of fixed size. BED files with fixed interval lengths are useful for a variety of tools including ScriptManager's [Tag Pileup][tag-pileup] occupancy counts, [Extract FASTA sequence][fasta-extract] for [Four Color plots][four-color], and even for a number of other third-party analysis tools.

The script can also pad the intervals of BED coordinates to create intervals that aren't necessarily all of a fixed length.

<img src={require('/../static/md-img/Coordinate_Manipulation/ExpandBEDWindow.png').default} style={{width:70+'%'}}/>

### Input BED files
The graphical interface restricts file selection by the `.bed` file extension. This tool supports batch processing of files.


### Expansion strategy & size
Depending on the strategy selected, the "Size of Expansion" (in bp) can mean different things.

* __Expand from Center:__The figure at the top of the page illustrates the "expand from center" expansion strategy where the midpoint is determined by script and then the window is expanded evenly on both sides of the midpoint to the size of expansion specified by the user. This results in a BED file with intervals of a fixed length.
* __Add to Border:__ This strategy pads both sides of the borders of the input intervals by a fixed amount specified by the user ("Size of Expansion"). Depending on whether or not the input BED file contains intervals of a fixed length, the resulting expansions will not necessarily include intervals of a fixed length.

### Output format
For each input BED file, a new BED file is created with the original filename and the `.bed` file extension replaced with the `*_<sizeofexpansion>bp.bed` suffix.


## Command Line Interface

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

[bedtools-slop]:https://bedtools.readthedocs.io/en/latest/content/tools/slop.html

[bed-format]:/docs/file-formats#bed
[tag-pileup]:/docs/read-analysis/tag-pileup
[four-color]:/docs/figure-generation/four-color
[fasta-extract]:/docs/sequence-analysis/fasta-extract
