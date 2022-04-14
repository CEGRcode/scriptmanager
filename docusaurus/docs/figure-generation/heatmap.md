---
id: heatmap
title: Heatmap
sidebar_label: heatmap
---

Usage:
```bash
java -jar ScriptManager.jar figure-generation heatmap [--black | --red | --blue |
-c=<custom>] [-hV] [-a=<absolute>] [-l=<startCOL>] [-o=<output>]
[-p=<percentile>] [-r=<startROW>] [-x=<pixelWidth>] [-y=<pixelHeight>]
[-z=<compression>] <CDT>
```

Description:

Generate heatmap using CDT files.


### Positional Input

Expects a [CDT][cdt-format] formatted matrix file of values to generate heatmap from.


### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output=<output>` | specify output filename, please use PNG extension (default=CDT filename with "_\<compression-type\>. png" appended to the name in working directory of ScriptManager |


### Plot Design Options

| Option | Description |
| ------ | ----------- |
| `-r, --start-row=<startROW>` |  |
| `-l, --start-col=<startCOL>` |  |
| `-x, --width=<pixelWidth>` | indicate a pixel width for the heatmap (default=200) |
| `-y, --height=<pixelHeight>` | indicate a pixel height for the heatmap (default=600) |
| `-z, --compression=<compression>` | choose an image compression type: 1=Treeview, 2=Bicubic, 3=Bilinear, 4=Nearest Neighbor (default=1Treeview) |
| `-a, --absolute-threshold=<absolute>` | use the specified value for contrast thresholding in the heatmap (default=10) |
| `-p, --percentile-threshold=<percentile>` | use the specified percentile value for contrast thresholding in the heatmap (try .95 if unsure) |



### Color Options

| Option | Description |
| ------ | ----------- |
| `--black` | Use the color black for generating the heatmap (default) |
| `--red` | Use the color red for generating the heatmap |
| `--blue` | Use the color blue for generating the heatmap |
| `-c, --color=<custom>` | For custom color: type hexadecimal string to represent colors (e.g. "FF0000" is hexadecimal for red). See \<http://www.javascripter.net/faq/rgbtohex.htm\> for some color options with their corresponding hex strings. |

:::note

The lab standard for strand colors is ‘Sense’ == blue and ‘Anti’ == red

:::


![default-nucleosome] ![blue-nucleosome] ![custom-nucleosome]



[color-hex-url]:http://www.javascripter.net/faq/rgbtohex.htm

[cdt-format]:file-formats.md
[png-format]:file-formats.md

[default-nucleosome]:../static/md-img/default_nucleosome.png
[blue-nucleosome]:../static/md-img/blue_nucleosome.png
[custom-nucleosome]:../static/md-img/custom_nucleosome.png
