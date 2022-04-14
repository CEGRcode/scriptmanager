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

For more information on CLI usage, see *Command Line Overview*.

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

## General Options (common to all ScriptManager tools)

| Option | Description |
| ------ | ----------- |
| -h, --help | Show this help message and exit. |
| -V, --version | Print version information and exit. |



### Help Option (`-h`)

At any point in building a command, if you get stuck or are unsure of your options, use the `-h` flag to show options. This can list the available subcommands or parameter and argument options.


## Color Customization

Some tools allow you to customize colors used in the output, specifically among the `figure-generation` tools.

E.g. `composite`, `heatmap`, and `four-color`

Default colors are set for these tools so that no color needs to be specified for the program to execute. The following is an example of heatmap's default execution.

`java -jar ScriptManager.jar figure-generation heatmap nucleosomes.cdt `

In the case of `heatmap`, there are also preset color flags are available for the user to choose from.

`java -jar ScriptManager.jar figure-generation heatmap nucleosomes.cdt --blue`

However if you want to use a color outside the preset values, you can indicate RGB colors using _hexstrings_. These are a sequence of 6 characters, where each pair of characters represent an Red, Green, and Blue value, 0-255 each). The help documentation points the user to [this url](http://www.javascripter.net/faq/rgbtohex.htm) for users to browse colors and get the appropriate hexstring.

_Note user should not use the pound symbol `#` in front of the hexidecimal because it renders the token invisible to bash and thus, ScriptManager too_

`java -jar ScriptManager.jar figure-generation heatmap nucleosomes.cdt -c 9400D3`


## Output Options

### Default filename
Most tools generate a filename based on the input filenames and attempt to mimick the file naming system of the GUI tools.

### Specify Output filename
The tools of ScriptManager all use the `-o` flag to specify output filenames or output file basenames.

When appropriate, some tools have constraints are added to check the extension of the output filename so that it matches the correct format. The tools also check that the parent directory exist before attempting to execute. The tool will print messages that let the user know when the filename fails these checks.

### Redirect to STDOUT
The tools of ScriptManager all use the `-s` flag to indicate that the results should stream to ”standard output” or "STDOUT". This is to mimic the function of other tools like Samtools, Bedtools, etc.

_Note only some of the tools have this option. Check the help guide if you're not sure._

_Note this flag cannot be used in combination with the `-o` flag._

For example, if we wanted to run ExpandBED on a BED file to expand the window before getting the TagPileup results, we could execute each tool sequentially with an intermediate file, `intermediate.bed`, as follows:

Template:

`COMMANDA input.file -o intermediate.file`
`COMMANDB intermediate.file -o results.file`

`COMMANDA input.file -c | COMMANDB -o results.file`
`COMMANDB <( COMMANDA input.file -c ) -o results.file

Example:

`java -jar ScriptManager.jar coordinate-manipulation expand-bed Tup1_peaks.bed -b 500 -o intermediate.bed`\
`java -jar ScriptManager.jar read-analysis tag-pileup intermediate.bed data.bam -o RESULTS.composite`


`java -jar ScriptManager.jar coordinate-manipulation expand-bed Tup1_peaks.bed -b 500 -c | java -jar ScriptManager.jar read-analysis tag-pileup - data.bam -o RESULTS.composite`

`java -jar ScriptManager.jar read-analysis tag-pileup <(java -jar ScriptManager.jar coordinate-manipulation expand-bed Tup1_peaks.bed -b 500 -c) data.bam -o RESULTS.composite`

Advantages:
1. Save on disk space (fewer intermediate files)
2. Potentially speed up command by skipping steps to write intermediate file to disk (save on I/O operations
