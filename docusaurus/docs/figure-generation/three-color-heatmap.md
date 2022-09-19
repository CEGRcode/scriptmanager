---
id: three-color-heatmap
title: Three Color Heatmap
sidebar_label: three-color-heatmap
---

![Three Color Heatmap](/../static/icons/Figure_Generation/ThreeColorHeatmap_square.svg)

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

Generate heat map with middling values.

<!-- ![ThreeColorHeatMapSchematic.png] -->

:::note
Extended description goes here.
:::

<img src={require('/../static/md-img/Figure_Generation/ThreeColorHeatMapWindow.png').default} style={{width:70+'%'}}/>

```bash
Usage: script-manager figure-generation three-color [-pn=<percentile> |
       -an=<absolute>] [-pd=<percentile> | -ad=<absolute>] [-px=<percentile> |
       -ax=<absolute>] [-cn=<min> | -cd=<mid> | -cx=<max> | -ca=<nan>] [-0hV]
       [-l=<startCOL>] [-o=<output>] [-r=<startROW>] [-x=<pixelWidth>]
       [-y=<pixelHeight>] [-z=<compression>] <CDT>
Generate heat map with middling values.
      <CDT>
  -o, --output=<output>      specify output filename, please use PNG extension
                             (default=CDT filename with "_<compression-type>.
                               png" appended to the name in working directory
                               of ScriptManager
  -r, --start-row=<startROW>
  -l, --start-col=<startCOL>
  -x, --width=<pixelWidth>   indicate a pixel width for the heatmap
                               (default=200)
  -y, --height=<pixelHeight> indicate a pixel height for the heatmap
                               (default=600)
  -z, --compression=<compression>
                             choose an image compression type: 1=Treeview,
                               2=Bicubic, 3=Bilinear, 4=Nearest Neighbor
                               (default=1Treeview)
  -0, --include-zeros        used with `-p` flag, indicating exclusion of zero
                               values when calculating percentile thresholds
  -h, --help                 Show this help message and exit.
  -V, --version              Print version information and exit.

Select minimum value:
	(select no more than one of these options)
      -pn=<percentile>       use this percentile (as opposed to specific
                               values) for contrast thresholding in determining
                               max contrast value of the heatmap
      -an=<absolute>         the minimum value for contrast thresholding in the
                               heatmap (default,min=-10)

Select middling value:
	(select no more than one of these options)
      -pd=<percentile>       use this percentile (as opposed to specific
                               values) for contrast thresholding in determining
                               max contrast value of the heatmap
      -ad=<absolute>         the midpoint value for contrast thresholding in
                               the heatmap (default,mid=0)

Select maximum value:

	(select no more than one of these options)
      -px=<percentile>       use this percentile (as opposed to specific
                               values) for contrast thresholding in determining
                               max contrast value of the heatmap
      -ax=<absolute>         the maximum value for contrast thresholding
                               (default,max=10)

Select heatmap colors:
      -cn, --color-min=<min> Color indicating minimum values (default=YELLOW)
                               For custom color: type hexadecimal string to
                               represent colors (e.g. "FF0000" is hexadecimal
                               for red).
                              See <http://www.javascripter.net/faq/rgbtohex.
                               htm> for some color options with their
                               corresponding hex strings.

      -cd, --color-mid=<mid> Color indicating middle values (default=BLACK) For
                               custom color: type hexadecimal string to
                               represent colors (e.g. "FF0000" is hexadecimal
                               for red).
                              See <http://www.javascripter.net/faq/rgbtohex.
                               htm> for some color options with their
                               corresponding hex strings.

      -cx, --color-max=<max> Color indicating maximum values (default=CYAN) For
                               custom color: type hexadecimal string to
                               represent colors (e.g. "FF0000" is hexadecimal
                               for red).
                              See <http://www.javascripter.net/faq/rgbtohex.
                               htm> for some color options with their
                               corresponding hex strings.

      -ca, --color-nan=<nan> Color indicating not-a-number values
                               (default=GRAY) For custom color: type
                               hexadecimal string to represent colors (e.g.
                               "FF0000" is hexadecimal for red).
                              See <http://www.javascripter.net/faq/rgbtohex.
                               htm> for some color options with their
                               corresponding hex strings.

```
