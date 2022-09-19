---
id: tag-pileup
title: Tag Pileup
sidebar_label: tag-pileup
---

![tag-pileup](/../static/icons/Read_Analysis/TagPileup_square.svg)


Pileup 5' ends of aligned tags given BED and BAM files according to user-defined parameters

<img src={require('/../static/md-img/Read_Analysis/TagPileupWindow.png').default} style={{width:70+'%'}}/>

## Command Line Interface

Usage:
```bash
java -jar ScriptManager.jar read-analysis tag-pileup [-1 | -2 | -a | -m] [--separate
| --combined] [-N | -w | -W=<winVals> | -g | -G=<gaussVals> <gaussVals>
[-G=<gaussVals> <gaussVals>]...] [-dhptVz] [--cdt] [--tab] [-M
[=<outputMatrix>]]... [-b=<binSize>] [--cpu=<cpu>]
[-f=<blacklistFilter>] [-n=<MIN_INSERT>] [-o=<outputComposite>]
[-s=<shift>] [-x=<MAX_INSERT>] <bedFile> <bamFile>
```

The TagPileup tool is used to look at read density across a bed file. This tool has perhaps the most complex option structure of the ScriptManager tools.


The help guide groups the options by their relation to different aspects of ScriptManager:



### Positional Inputs

| Option | Description |
| ------ | ----------- |
| `bedFile` | The BED file with reference coordinates to pileup on. |
| `bamFile` | The BAM file from which we remove duplicates. Make sure its indexed! |

### General Options

| Option | Description |
| ------ | ----------- |
| `-d, --dry-run` | print all parameters without running anything |


### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output-composite=<outputComposite>` | specify output file for composite values |
| `-M, --output-matrix[=<outputMatrix>` ] | specify output basename for matrix files (files each for sense and anti will be output) |
| `-z, --gzip` | output compressed output (default=false) |
| `--cdt` |  output matrix in cdt format (default) |
| `--tab` | output matrix in tab format |


### Read Options

| Option | Description |
| ------ | ----------- |
| `-1, --read1` | pileup of read 1 (default) |
| `-2, --read2` | pileup of read 2 |
| `-a, --all-reads` | pileup all reads |
| `-m, --midpoint` | pile midpoint (require PE) |


### Strand Options

| Option | Description |
| ------ | ----------- |
| `--separate` | select output strands as separate (default) |
| `--combined` | select output strands as combined |

### Composite Transformation/Smoothing Options

| Option | Description |
| ------ | ----------- |
| `-N, --no-smooth` | no smoothing applied to composite (default) |
| `-w, --window-smooth` | sliding window smoothing applied to composite using default 3 bins for window size |
| `-W, --window-values=<winVals>` | sliding window smoothing applied to composite with user specified window size (in #bins) |
| `-g, --gauss-smooth` | gauss smoothing applied to composite using default values: 5 bins and 3 standard deviations |
| `-G, --gauss-values=<gaussVals> <gaussVals>` | gauss smoothing applied to composite with user specified standard deviation(SD) size (in #bins) followed by the number of SD |

There are three available options for smoothing:
1. No smooth
2. Window smooth
3. Gaussian smooth

For the window smoothing, you can indicate a window size for applying the sliding window for smoothing with an integer. This integer indicates the number of bins per window (bins defined and explained in the "Calculation Options"). You can use the `-w` flag as a shortcut for the GUI version default value of 3.

For the gaussian smoothing, you can think of the stnadardeviaiton size as the

`<image-of-gaussian-equation>`


### Calculation Options

| Option | Description |
| ------ | ----------- |
| `-s, --shift=<shift>` | set a shift in bp (default=0bp) |
| `-b, --bin-size=<binSize>` | set a bin size for the output (default=1bp) |
| `-t, --standard` | set tags to be equal (default=false) |
| `--cpu=<cpu>` | set number of CPUs to use (default=1) |


### Filter Options

| Option | Description |
| ------ | ----------- |
| `-f, --blacklist-filter=<blacklistFilter>` | specify a blacklist file to filter BED by, must use with -t flag |
| `-p, --require-pe` | require proper paired ends (default=false), automatically turned on with any of flags -mnx |
| `-n, --min-insert=<MIN_INSERT>` | filter by minimum insert size in bp, require PE (default=no minimum) |
| `-x, --max-insert=<MAX_INSERT>` | filter by maximum insert size in bp, require PE (default=no maximum) |


### Composite Plot Figure

For visualizing composite data like the GUI window, you need to use a separate tool in the CLI tools. See [composite-plot][giturl-composite] tool.

[file-format]:file-formats.md
