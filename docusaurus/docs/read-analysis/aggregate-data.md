---
id: aggregate-data
title: Aggregate Data
sidebar_label: aggregate-data
---

![aggregate-data](/../static/icons/Read_Analysis/AggregateData_square.svg)

Compile data from tab-delimited file into matrix according to user-specified
metric.

<img src={require('/../static/md-img/Read_Analysis/AggregateDataWindow.png').default} style={{width:70+'%'}}/>

## Command Line Interface

Usage:
```bash
java -jar ScriptManager.jar read-analysis aggregate-data [--sum | --avg | --med |
--mod | --min | --max | --var] [-fhmV] [-l=<startCOL>] [-o=<output>]
[-r=<startROW>] [<inputFiles>...]
```

The AggregateData tool is used to process a bunch of matrix files into one matrix file.


### Input Options

Since this tool process a bunch of files together, there are two ways of feeding input files:

(1) You can list them out in the command line tool,

`java -jar ScriptManager.jar read-analysis aggregate-data matFile1 matFile2 ... matFileX <OPTIONS>`

(2) or you can write all the paths for all your files in a single file and pass that as the input using the `-f` flag

`java -jar ScriptManager.jar read-analysis aggregate-data inputFile -f <OPTIONS>`

where `inputFile` is listed out line by line:

```
/path/to/matFile1
/path/to/matFile2
...
/path/to/matFileX
```

_Note that absolute file paths are easier to work with. For relative paths, you'll have to check that they are built with respect to the ScriptManager directory._




### Output Options

| Option | Description |
| ------ | ----------- |
| `-m, --merge` | merge to one output file |
| `-o, --output=<output>` | Specify output file, default is "aggregate_matrix.txt" or the input filename if -f flag used |

The file output can be specified by the user using this flag. Otherwise the output will be `aggregate_matrix.txt` in the same directory as ScriptManager. Or based on the input filename if the `-f` flag is used.


### Aggregation Method Options

| Option | Description |
| ------ | ----------- |
| `--sum` | use summation method (default) |
| `--avg` | use average method |
| `--med` | use median method |
| `--mod` | use mode method |
| `--min` | use minimum method |
| `--max` | use maximum method |
| `--var` | use positional variance method |



### Coord Start Options

| Option | Description |
| ------ | ----------- |
| `-r, --start-row` |  |
| `-l, --start-col` |  |

[file-format]:file-formats.md
