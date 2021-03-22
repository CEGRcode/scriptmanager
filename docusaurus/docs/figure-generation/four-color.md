---
id: four-color
title: Four Color
sidebar_label: four-color
---

Usage:
```bash
java -jar ScriptManager.jar figure-generation four-color [-hV] [-o=<output>]
[-x=<pixelWidth>] [-y=<pixelHeight>] [-c=<colors> <colors> <colors>
<colors> [<colors>]]... <fastaFile>
```

Description:

Generate 4Color sequence plot given FASTA file and user-defined RGB colors.

### Positional Input

Expects a [FASTA][fasta-format] formatted file with many sequences to stack up with each other (like [fasta-extract tool][fasta-extract] output).

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



[color-hex-url]:http://www.javascripter.net/faq/rgbtohex.htm

[fasta-extract]:sequence-analysis/fasta-extract.md

[fasta-format]:file-format.md
[png-format]:file-format.md
