---
id: four-color
title: Four Color Sequence Plot
sidebar_label: Four Color Plot
---

![four-color](/../static/icons/Figure_Generation/FourColorSequencePlot_square.svg)

export const Highlight = ({children, color}) => (
<span
style={{
      backgroundColor: color,
      borderRadius: '2px',
      color: '#fff',
      padding: '0.2rem',
    }}>
{children}
</span>
);

Generate 4 color sequence plot from a FASTA file and user-defined RGB colors.

<img src={require('/../static/md-img/Figure_Generation/FourColorSequenceWindow.png').default} style={{width:70+'%'}}/>

### Input files (FASTA)
The [FASTA][fasta-format] formatted input files must have one or more sequences that are converted to color blocks of the user-specified pixel dimensions and stacked on top of each other.

:::caution

Sequences from the input FASTA must be the same number of nucleotides in length.

:::


### Nucleotide colors
The default colors for each nucleotide are as follows:

|  | default color (hex) |
| - | --------- |
| A | <Highlight color="#D00000"> `#D00000` </Highlight>|
| T | <Highlight color="#00D000"> `#00D000` </Highlight> |
| G | <Highlight color="#FFB400"> `#FFB400` </Highlight> |
| C | <Highlight color="#0000D0"> `#0000D0` </Highlight> |
| N | <Highlight color="#808080"> `#808080` </Highlight> |

The default colors can be customized using the color selector tool. You can open any of the color selection windows by clicking on "A Color", "T Color", "G Color", "C Color", or "N Color".

__[Read more about color selection in the color guide][color-guide]__


### Pixel dimensions
* **Pixel Height** refers to the pixel height of each nucleotide block represented in the PNG of the four color plot.
* **Pixel Width** refers to the pixel width of each nucleotide block represented in the PNG of the four color plot.


## Command Line Interface

Usage:
```bash
java -jar ScriptManager.jar figure-generation four-color [-hV] [-o=<output>]
[-x=<pixelWidth>] [-y=<pixelHeight>] [-c=<colors> <colors> <colors>
<colors> [<colors>]]... <fastaFile>
```

### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output=<output>` | specify output filename, please use PNG extension (default=FASTA filename with "_4color.png" appended to the name in working directory of ScriptManager |


### Plot Design Options

| Option | Description |
| ------ | ----------- |
| `-c, --colors=<colors> <colors> <colors> <colors> [<colors>]` | For custom colors: List colors to use for ATGC[N], in that order. Type hexadecimal string to represent colors, e.g. FF0000 is hexadecimal for red. (default=A-red,T-green,G-yellow,C-blue,N-gray, if only 4 colors specified, N will be set to gray) See http://www.javascripter.net/faq/rgbtohex.htm for some color options with their corresponding hex strings. |
| `-x, --pixel-width=<pixelWidth>` | pixel width (default=1)|
| `-y, --pixel-height=<pixelHeight>` | pixel height (default=1)|


[color-guide]:/docs/Guides/color-guide
[color-hex-url]:http://www.javascripter.net/faq/rgbtohex.htm

[fasta-extract]:/docs/sequence-analysis/fasta-extract

[fasta-format]:/docs/file-formats#fasta
[png-format]:/docs/file-formats#png
