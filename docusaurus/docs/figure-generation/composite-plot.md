---
id: composite-plot
title: Composite Plot
sidebar_label: Composite Plot
---

![Composite Plot](/../static/icons/Figure_Generation/Composite-plot.svg)

Generate a Composite Plot PNG from composite data like the output in TagPileup.

:::warning

Currently only a command line version is supported. There isn't a graphical interface yet.

:::

## Command Line Interface

Usage:

```bash
java -jar ScriptManager.jar figure-generation composite-plot [-hlV] [-o=<output>]
[-t=<title>] [-x=<pixelWidth>] [-y=<pixelHeight>] [-c=<colors>...]...
<compositeData>
```

### Positional Input

Expects a file format like the TagPileup's composite output

### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output=<output>` | specify output filename, please use PNG extension (default=Input filename with "\_compositePlot.png" appended to the name in working directory of ScriptManager |

### Plot Design Options

| Option | Description |
| ------ | ----------- |
| `-t, --title=<title>`             | set title (default uses input file name)            |
| `-l, --legend`                    | add a legend (default=no legend)                    |
| `-x, --width=<pixelWidth>`        | indicate a pixel width for the plot (default=500)   |
| `-y, --height=<pixelHeight>`      | indicate a pixel height for the plot (default=270)  |
| `-c, --custom-colors=<colors>...` | indicate colors to use for each series. Must indicate a number of colors that matches number of dataseries default behavior: if one series input, use black if two series input, use blue(sense) and red(anti) if greater than two series, cycle through a set of 20 preset colors. |

[color-hex-url]:http://www.javascripter.net/faq/rgbtohex.htm
[png-format]:/docs/file-formats
