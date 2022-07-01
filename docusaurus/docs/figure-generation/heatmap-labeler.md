---
id: heatmap-labeler
title: Label Heatmap
sidebar_label: Label Heatmap
---

![Label Heatmap](/../static/icons/FigureGeneration/Heatmaplabeler_square.svg)

This tool will embed a `.png` file into an output `.svg` with a title and axes and tickmark labels.


![LabelHeatmap](/../static/md-img/LabelHeatmap.png)


Typically this tool is used to label merged ChIP-exo heatmap or two-color ATAC-seq heatmap plots of tag counts but there is no reason it cannot be used for any input `.png`.

<img src={require('/../static/md-img/FigureGeneration/LabelHeatmap.png').default} style={{width:70+'%'}}/> 

Any file with a `.png` extension may be loaded into the Label Heatmap tool. When a batch of files have been loaded, the user can type out axes labels, font size, specify border widths, colors, and tick height.

Clicking "Generate" will execute the script to write `.svg` files, each with one of the `.png` files embedded and annotated by the user-specifications. The output files will be named like the input files with the `_label.svg` suffix replacing the `.png` extension.


## Command Line Interface

[png-format]:/docs/file-formats
