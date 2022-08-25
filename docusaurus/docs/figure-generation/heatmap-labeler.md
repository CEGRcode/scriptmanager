---
id: heatmap-labeler
title: Label Heatmap
sidebar_label: Label Heatmap
---

![Label Heatmap](/../static/icons/Figure_Generation/Heatmaplabeler_square.svg)

This tool will embed a `.png` file into an output `.svg` with a title and axes and tickmark labels.

<img src={require('/../static/md-img/Figure_Generation/LabelHeatMapSchematic.jpg').default} style={{width:70+'%'}}/>

Typically this tool is used to label figures such as merged ChIP-exo heatmaps, ATAC-seq heatmaps plot of tag counts, or four color sequence plots but there is no reason it cannot be used for any input `.png`.

<img src={require('/../static/md-img/Figure_Generation/LabelHeatMapWindow.png').default} style={{width:70+'%'}}/>

Any file with a `.png` extension may be loaded into the Label Heatmap tool. When a batch of files have been loaded, the user can type out axes labels, font size, specify border widths, colors, and tick height.

Clicking "Generate" will execute the script to write `.svg` files, each with one of the `.png` files embedded and annotated by the user-specifications. The output files will be named like the input files with the `_label.svg` suffix replacing the `.png` extension.

### Axis Line formating
* **Border width** refers to the pixel width of all lines used from the border thickness around the PNG to the tick mark line widths.
* **X-tick height** refers to the pixel height/length of the lines along the bottom of the PNG that mark the left, right, and midpoint values.
* **Color** refers to the color value for all the lines making up the border and tick marks as well as the text  color.


### Label Formatting
These parameters refer to the formatting of the font in the SVG.
* **Font Size** will change the font size of all labels (both x-tick mark labels and axis labels)
* **Left label** refers the the minimum/leftmost x-axis tick mark label.
* **Mid label** refers the the midpoint x-axis tick mark label (typically 0 if plot derived from a reference set expanded from the center of a reference point).
* **Right label** refers the the maximum/rightmost x-axis tick mark label.
* **X-axis Label** refers the text used for describing the x-axis, typically describing a reference point.
* **Y-axis Label** refers the text used for describing the y-axis, typically describing the set of features that the heatmap/sequence plot is visualizing.

## Command Line Interface

```bash
Usage: script-manager figure-generation label-heatmap [-hV] [-c=<color>]
       [-f=<FONTSIZE>] [-l=<xLeftLabel>] [-m=<xMidLabel>] [-o=<OUTPUT>]
       [-r=<xRightLabel>] [-t=<xTickHeight>] [-w=<borderWidth>] [-x=<xLabel>]
       [-y=<yLabel>] <PNG>
Create an SVG label for heatmap inputs
      <PNG>                Input image (required)
  -o, --output=<OUTPUT>    Output image (default="OutputHeatmap.svg"
  -c, --color=<color>      Output color: Must be in Hex format. (default=black)
  -w, --width=<borderWidth>
                           Line thickness of border. Integer required. (default
                             = 2)
  -t, --tick-height=<xTickHeight>
                           X-tickmark height. (default=10)
  -f, --font-size=<FONTSIZE>
                           Font size. (default=18)
  -l, --left-label=<xLeftLabel>
                           Left X-tick label. (default=nolabel)
  -m, --mid-label=<xMidLabel>
                           Mid X-tick label. (default=nolabel)
  -r, --right-label=<xRightLabel>
                           Right X-tick label. (default=nolabel)
  -x, --x-label=<xLabel>   X-axis label. (default=nolabel)
  -y, --y-label=<yLabel>   Y-axis label. (default=nolabel)
  -h, --help               Show this help message and exit.
  -V, --version            Print version information and exit.
```

[png-format]:/docs/file-formats#png
