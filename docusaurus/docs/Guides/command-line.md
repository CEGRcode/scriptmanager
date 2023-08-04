---
id: command-line
title: Command Line
sidebar_label: Command Line
---

The Command Line Interface (CLI) tools from ScriptManager were written to mirror the tools wrapped in the Graphical User Interface (GUI) version.

With the addition of this interface, the user can *automate* these analyses and a *record* can be kept of previous analyses. Furthermore, when a sequence of ScriptManager tools is required for an analysis, there is no longer a need to babysit the execution of these tools to start the execution of the next tool.

Ultimately this will **save time** and improve **reproducibility** of results.

## Usage

To run tools from the CLI version of ScriptManager, use the following format.

`java -jar /path/to/ScriptManager.jar <TOOLGROUP> <TOOLNAME> <INPUTS> <OPTIONS>`

The `TOOLGROUP` corresponds to one of the tabs in the GUI tool while the `TOOLNAME` corresponds to the specific tool within the `TOOLGROUP` group. Each tool will have its own set of input requirements and options. You will have to rely on the `-h` flag for usage help or the documentation here for the specific tool you wish to use.

<table>
<tr valign="top"><td>

| Tool Group  | Tool Name |
| ------------- | ------------- |
| **bam-format-converter** | [bam-to-bedgraph](https://github.com/CEGRcode/scriptmanager/wiki/BAM-Format-Converter#bam-to-bedgraph) |
| | [bam-to-bed](https://github.com/CEGRcode/scriptmanager/wiki/BAM-Format-Converter#bam-to-bed) |
| | [bam-to-gff](https://github.com/CEGRcode/scriptmanager/wiki/BAM-Format-Converter#bam-to-gff) |
| | [bam-to-scidx](https://github.com/CEGRcode/scriptmanager/wiki/BAM-Format-Converter#bam-to-scidx) |
| **bam-manipulation** | [bam-indexer\*](https://github.com/CEGRcode/scriptmanager/wiki/BAM-Manipulation#bam-indexer) |
| | [remove-duplicates\*](https://github.com/CEGRcode/scriptmanager/wiki/BAM-Manipulation#remove-duplicates) |
| | [filter-pip-seq](https://github.com/CEGRcode/scriptmanager/wiki/BAM-Manipulation#filter-pip-seq) |
| | [merge-bam\*](https://github.com/CEGRcode/scriptmanager/wiki/BAM-Manipulation#merge-bam) |
| | [sort-bam\*](https://github.com/CEGRcode/scriptmanager/wiki/BAM-Manipulation#sort-bam) |
| **bam-statistics** | [se-stat](https://github.com/CEGRcode/scriptmanager/wiki/BAM-Statistics#se-stat) |
| | [pe-stat](https://github.com/CEGRcode/scriptmanager/wiki/BAM-Statistics#pe-stat) |
| | [bam-correlation](https://github.com/CEGRcode/scriptmanager/wiki/BAM-Statistics#bam-correlation) |
| **coordinate-manipulation** | [bed-to-gff](https://github.com/CEGRcode/scriptmanager/wiki/Coordinate-Manipulation#bed-to-gff) |
| | [gff-to-bed](https://github.com/CEGRcode/scriptmanager/wiki/Coordinate-Manipulation#gff-to-bed) |
| | [expand-bed](https://github.com/CEGRcode/scriptmanager/wiki/Coordinate-Manipulation#expand-bed) |
| | [expand-gff](https://github.com/CEGRcode/scriptmanager/wiki/Coordinate-Manipulation#expand-gff) |
| | [sort-bed](https://github.com/CEGRcode/scriptmanager/wiki/Coordinate-Manipulation#sort-bed) |
| | [sort-gff](https://github.com/CEGRcode/scriptmanager/wiki/Coordinate-Manipulation#sort-gff) |

\* - these tools don't have a CLI version\
because the GUI versions originally wrap\
existing CLI tools. Executing these will\
refer the user to the appropriate tool.

</td><td>

| Tool Group  | Tool Name |
| ------------- | ------------- |
| **figure-generation** | [composite](https://github.com/CEGRcode/scriptmanager/wiki/Figure-Generation#composite) |
| | [four-color](https://github.com/CEGRcode/scriptmanager/wiki/Figure-Generation#four-color) |
| | [heatmap](https://github.com/CEGRcode/scriptmanager/wiki/Figure-Generation#heatmap) |
| | [merge-heatmap](https://github.com/CEGRcode/scriptmanager/wiki/Figure-Generation#merge-heatmap) |
| **file-utilities** | [md5checksum\*](https://github.com/CEGRcode/scriptmanager/wiki/File-Utilities#md5checksum) |
| **peak-analysis** | [peak-align-ref](https://github.com/CEGRcode/scriptmanager/wiki/Peak-Analysis#peak-align-ref) |
| | [filter-bed](https://github.com/CEGRcode/scriptmanager/wiki/Peak-Analysis#filter-bed) |
| | [rand-coord](https://github.com/CEGRcode/scriptmanager/wiki/Peak-Analysis#rand-coord) |
| | [signal-dup](https://github.com/CEGRcode/scriptmanager/wiki/Peak-Analysis#signal-dup) |
| | [tile-genome](https://github.com/CEGRcode/scriptmanager/wiki/Peak-Analysis#tile-genome) |
| **peak-calling** | gene-track |
| | peak-pair |
| **read-analysis** | [aggregate-data](https://github.com/CEGRcode/scriptmanager/wiki/Read-Analysis#aggregate-data) |
| | [scale-matrix](https://github.com/CEGRcode/scriptmanager/wiki/Read-Analysis#scale-matrix) |
| | [scaling-factor](https://github.com/CEGRcode/scriptmanager/wiki/Read-Analysis#scaling-factor) |
| | [similarity-matrix](https://github.com/CEGRcode/scriptmanager/wiki/Read-Analysis#similarity-matrix-suspended) |
| | [tag-pileup](https://github.com/CEGRcode/scriptmanager/wiki/Read-Analysis#tag-pileup) |
| **seq-analysis** | [dna-shape-bed](https://github.com/CEGRcode/scriptmanager/wiki/Sequence-Analysis#dna-shape-bed) |
| | [dna-shape-fasta](https://github.com/CEGRcode/scriptmanager/wiki/Sequence-Analysis#dna-shape-fasta) |
| | [fasta-extract](https://github.com/CEGRcode/scriptmanager/wiki/Sequence-Analysis#fasta-extract) |
| | [randomize-fasta](https://github.com/CEGRcode/scriptmanager/wiki/Sequence-Analysis#randomize-fasta) |
| | [search-motif](https://github.com/CEGRcode/scriptmanager/wiki/Sequence-Analysis#search-motif) |

</td></tr>
</table>

## General Options

The following options are shared by **all** ScriptManager tools.

| Option | Description |
| ------ | ----------- |
| `-h, --help` | Show this help message and exit. |
| `-V, --version` | Print version information and exit. |

### Help Option (`-h`)

At any point in building a command, if you get stuck or are unsure of your options, use the `-h` flag to show options. This can list the available subcommands or parameter and argument options.


## Output Options

### Default filename
Most tools generate a filename based on the input filenames and attempt to mimic the file naming system of the GUI tools.

### Specify Output filename (`-o`)
Many tools allow the user to specify output filenames or output file basenames (`-o`).

When appropriate, some tools have constraints are added to check the extension of the output filename so that it matches the correct format. The tools also check that the parent directory exist before attempting to execute. The tool will print messages that let the user know when the filename fails these checks.

### Redirect to STDOUT (`-s`)
Some tools allow the user to stream the output to [standard output][stdout-help] or "STDOUT". This is to mimic the function of other tools like Samtools, Bedtools, etc.

#### Advantages of streaming:
1. Save on disk space (fewer intermediate files)
2. Potentially speed up command by skipping steps to write intermediate file to disk (save on I/O operations).

:::note
Only some of the tools have this option. Check the help guide if you're not sure.
:::

:::note
This flag cannot be used in combination with the `-o` flag.
:::

### Examples

For example, if we wanted to run some `COMMANDA` tool and then use the output as an input for the `COMMANDB` tool, there are several ways to run this in a shell script. The first way explicitly saves the intermediate file between commands.

```bash
# Method A -save intermediate files
COMMANDA input.file -o intermediate.file
COMMANDB intermediate.file -o results.file
```

...or alternatively, we could stream the output of `COMMANDA` to the input of `COMMANDB` using the pipe (`|`) character:
```bash
# Method B -pipe stream
COMMANDA input.file -s | COMMANDB -o results.file
```

...or we could redirect (`<`) the stream directly into the positional argument location:
```bash
# Method C -redirect stream
COMMANDB <( COMMANDA input.file -s ) -o results.file
```

<br></br>

More specifically, below shows how these methods would look linking the inputs and outputs of the [Expand BED][expand-bed] and [Tag Pileup][tag-pileup] tools for a user that wants to look at the tag pileup results around a wider coordinate interval window.
```bash
# Method A -save intermediate files
java -jar ScriptManager.jar coordinate-manipulation expand-bed Tup1_peaks.bed -b 500 -o intermediate.bed
java -jar ScriptManager.jar read-analysis tag-pileup intermediate.bed data.bam -o RESULTS.composite
# Method B -pipe stream
java -jar ScriptManager.jar coordinate-manipulation expand-bed Tup1_peaks.bed -b 500 -s \
  | java -jar ScriptManager.jar read-analysis tag-pileup - data.bam -o RESULTS.composite
# Method C -redirect stream
java -jar ScriptManager.jar read-analysis tag-pileup \
  <(java -jar ScriptManager.jar coordinate-manipulation expand-bed Tup1_peaks.bed -b 500 -s) \
  data.bam -o RESULTS.composite
```


[stdout-help]:https://linuxhint.com/bash_stdin_stderr_stdout/
[expand-bed]:/docs/coordinate-manipulation/expand-bed
[tag-pileup]:/docs/read-analysis/tag-pileup
