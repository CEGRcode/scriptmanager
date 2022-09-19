---
id: heatmap
title: Two-color Heatmap
sidebar_label: Two-color Heatmap
---

![Two-colorheatmap](/../static/icons/Figure_Generation/TwoColorHeatmap_square.svg)

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

This tool generates a heatmap from a tab-delimited matrix input of numeric values.

The values to color scale is defined as a range from a minimum value of 0 (designated white) to a customizable maximum value specified by the user (customizable but typically red, blue, or black), hence, the "two colors" in the "Two Color Heatmap".

<!--$
\begin{bmatrix}
1 & 2 & .. & 10\\
\vdots & \ddots & \ddots & \vdots\\
\vdots & \ddots & \ddots & \vdots\\
\vdots & \ddots & \ddots & \vdots\\
\vdots & \ddots & \ddots & \vdots\\
\vdots & \ddots & \ddots & \vdots\\
\vdots & \ddots & \ddots & \vdots\\
\vdots & \ddots & \ddots & \vdots\\
\vdots & \ddots & \ddots & \vdots\\
\vdots & \ddots & \ddots & \vdots\\
\vdots & \ddots & \ddots & \vdots\\
\vdots & \ddots & \ddots & \vdots\\
\vdots & \ddots & \ddots & \vdots\\
11 & 30 & .. & 1
\end{bmatrix}$ &rarr; ![blue-nucleosome](/../static/md-img/blue_nucleosome.png) ![default-nucleosome](/../static/md-img/default_nucleosome.png) ![custom-nucleosome](/../static/md-img/custom_nucleosome.png)-->

<div className="row">
  <div class = "col col--3">
  <img src={require('/../static/md-img/Figure_Generation/matrix.jpg').default} style={{width:85+'%',}}/>
  </div>
  <div class = "col col--2">
  <img src={require('/../static/md-img/blue_nucleosome.png').default} style={{width:100+'%',}}/>
  <p>--blue</p>
  </div>
    <div class = "col col--2">
  <img src={require('/../static/md-img/default_nucleosome.png').default} style={{width:100+'%',}}/>
  <p>--black</p>
  </div>
  <div class = "col col--2">
  <img src={require('/../static/md-img/custom_nucleosome.png').default} style={{width:100+'%',}}/>
  <p>--custom</p>
  </div>
</div>

<!--[blue-nucleosome](/../static/md-img/Figure_Generation/matrix.jpg)
![blue-nucleosome](/../static/md-img/blue_nucleosome.png)
![default-nucleosome](/../static/md-img/default_nucleosome.png)
![custom-nucleosome](/../static/md-img/custom_nucleosome.png)

                             --blue           --black          --custom-->

This tool is typically used for visualizing the matrix output of [**Tag Pileup**][tag-pileup] to look at the raw tag coverage of any biochemical sequencing assay (ChIP-exo, ATAC-seq, PROcap, etc.). But there are many other tools in ScriptmManager like [**Align BED to Reference**][peak-align-ref] that produce output that can be visualized by this tool. Any tab-delimited text file can be used (many third party tools use such outputs) so long as the appropriate start row and start column are specified to skip over header columns and rows.

<img src={require('/../static/md-img/Figure_Generation/TwoColorHeatMapWindow.png').default} style={{width:70+'%'}}/>

### File inputs

This script does not restrict selection of file inputs because a variety of file extensions may be parsed out for the numeric matrix. The tool supports bulk selection and processing of files.

### Color selection

Details of the implementation are described below but typically red and blue are used for "antisense" and "sense" tag counts (respectively) from strand-specific assays and black is used for tag counts of strand non-specific assays such as ATAC-seq and MNase-seq. Users can further select from any RGB color by clicking "custom" and "Heatmap Color" to open the color selection panel to either choose from a palette of colors or input a hexidecimal color code.

:::tip
The Pugh Lab standard for ChIP-exo heatmap strand colors is <Highlight color="blue">‘Sense’ = blue</Highlight> and <Highlight color="red">‘Anti’ = red</Highlight>.
:::

### Contrast threshold

The user can specify the contrast threshold in a couple ways:

- **Absolute:** The user can directly define the value marking the "maximum color" (any values equal to this or higher will have the RGB value of the color selected in the above section).
- **Percentile:** Or the user can tell the script to dynamically determine the top $p$ percentile of values across the numeric matrix (excluding zeros) and set them all to have the "maximum color" value and scale color assignment for the rest of the values to white from there.

Read more about the contrast threshold below ("Details of color-scaling strategy").

### Image dimensions

The image height and width specify the number of pixels to squish or expand the numeric matrix into for the final output `.png` heat map image.

### Image Compression

The image compression options allow the user to choose from several image compression strategies but we recommend "Treeview" for base-pair resolution tag-pileup occupancy data. This is the same strategy implemented by previous microarray visualization packages ([Saldanha et al, 2004][treeview-paper]).

### Details of color-scaling strategy

Let every value in the input tab-delimited matrix($M$) be indexed by $i$ and $j$. The output heatmap of pixels can be defined by three matrices ($\textcolor{red}R$, $\textcolor{green}G$, and $\textcolor{blue}B$) of the same dimensions similarly indexed by $i$ and $j$ that define the RGB pixel color at each position.

The first step of the heatmap script is to define the contrast threshold, or value-based ceiling ($v_{max}$) for the color scale. The user can select this based on an "absolute" strategy or a "percentile" strategy.

- **Absolute:** For this strategy, the user simply specifies the value of $v_{max}$ directly.
- **Percentile:** For this strategy, the user specifies a percentile value $p$ based on the values of the input matrix, $M$.

  1.  Put all the **non-zero** values of $M$ into a sorted list, $m$.
  2.  Identify the value in $m$ marking the top $p$ percentile.
  3.  Set $v_{max}$ to this value.

_For developers:_ $v_{max}$ _is the `COLOR_RATIO` variable in the script's code._

Before proceeding further, we will define the heatmap color selected by the user in RGB values as $\textcolor{red}{r_{max}}$, $\textcolor{green}{g_{max}}$, $\textcolor{blue}{b_{max}}$. For example, if the user selected "Red", then $\textcolor{red}{r_{max}}=255$, $\textcolor{green}{g_{max}}=0$, $\textcolor{blue}{b_{max}}=0$. If the user selected "black", then $\textcolor{red}{r_{max}}=0$, $\textcolor{green}{g_{max}}=0$, $\textcolor{blue}{b_{max}}=0$.

Two-color heatmap also defines white for matrix values corresponding to zero or below, i.e. $\textcolor{red}r_{min}=255$, $\textcolor{green}{g_{min}}=255$, $\textcolor{blue}{b_{min}}=255$. However in the following equations we will just use $255$ for simplicity.

Now the script will build out the heatmap with pixel color values defined as follows:

$$
\forall i,j\newline
\space\\
\textcolor{red}R_{i,j} =
\begin{cases}
\textcolor{red}{r_{max}}         &, \text{if } M_{i,j}>v_{max}\\
255             &, \text{if } M_{i,j}<0\\
\textcolor{red}{r_{max}} \frac{M_{i,j}}{v_{max}} +
255 (1 - \frac{M_{i,j}}{v_{max}})
                &, \text{otherwise}
\end{cases}
\\
\space\\
\textcolor{green}G_{i,j} =
\begin{cases}
\textcolor{green}{g_{max}}         &, \text{if } M_{i,j}>v_{max}\\
255             &, \text{if } M_{i,j}<0\\
\textcolor{green}{g_{max}} \frac{M_{i,j}}{v_{max}} +
255 (1 - \frac{M_{i,j}}{v_{max}})
                &, \text{otherwise}
\end{cases}
\\
\space\\
\textcolor{blue}B_{i,j} =
\begin{cases}
\textcolor{blue}{b_{max}}         &, \text{if } M_{i,j}>v_{max}\\
255             &, \text{if } M_{i,j}<0\\
\textcolor{blue}{b_{max}} \frac{M_{i,j}}{v_{max}} +
255 (1 - \frac{M_{i,j}}{v_{max}})
                &, \text{otherwise}
\end{cases}
\newline
$$

## Command Line Interface

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

| Option                  | Description                                                                                                                                                           |
| ----------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `-o, --output=<output>` | specify output filename, please use PNG extension (default=CDT filename with "\_\<compression-type\>. png" appended to the name in working directory of ScriptManager |

### Plot Design Options

| Option                                    | Description                                                                                                 |
| ----------------------------------------- | ----------------------------------------------------------------------------------------------------------- |
| `-r, --start-row=<startROW>`              |                                                                                                             |
| `-l, --start-col=<startCOL>`              |                                                                                                             |
| `-x, --width=<pixelWidth>`                | indicate a pixel width for the heatmap (default=200)                                                        |
| `-y, --height=<pixelHeight>`              | indicate a pixel height for the heatmap (default=600)                                                       |
| `-z, --compression=<compression>`         | choose an image compression type: 1=Treeview, 2=Bicubic, 3=Bilinear, 4=Nearest Neighbor (default=1Treeview) |
| `-a, --absolute-threshold=<absolute>`     | use the specified value for contrast thresholding in the heatmap (default=10)                               |
| `-p, --percentile-threshold=<percentile>` | use the specified percentile value for contrast thresholding in the heatmap (try .95 if unsure)             |

### Color Options

| Option                 | Description                                                                                                                                                                                                         |
| ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `--black`              | Use the color black for generating the heatmap (default)                                                                                                                                                            |
| `--red`                | Use the color red for generating the heatmap                                                                                                                                                                        |
| `--blue`               | Use the color blue for generating the heatmap                                                                                                                                                                       |
| `-c, --color=<custom>` | For custom color: type hexadecimal string to represent colors (e.g. "FF0000" is hexadecimal for red). See http://www.javascripter.net/faq/rgbtohex.htm for some color options with their corresponding hex strings. |


Default colors are set for these tools so that no color needs to be specified for the program to execute. The following is an example of heatmap's default execution (uses black).

`java -jar ScriptManager.jar figure-generation heatmap nucleosomes.cdt `


There are also preset color flags are available for the user to choose from (blue).

`java -jar ScriptManager.jar figure-generation heatmap nucleosomes.cdt --blue`

However if you want to use a color outside the preset values, you can indicate RGB colors using _hexstrings_. You can read more about color customizations [here][color-guide].

`java -jar ScriptManager.jar figure-generation heatmap nucleosomes.cdt -c 9400D3`

[color-hex-url]:http://www.javascripter.net/faq/rgbtohex.htm
[treeview-paper]:https://pubmed.ncbi.nlm.nih.gov/15180930/
[tag-pileup]:/docs/read-analysis/tag-pileup
[peak-align-ref]:/docs/peak-analysis/peak-align-ref
[cdt-format]:/docs/file-formats
[png-format]:/docs/file-formats
[color-guide]:/docs/Guides/color-guide
