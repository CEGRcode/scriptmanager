---
id: merge-heatmap
title: Merge Heatmap
sidebar_label: Merge Heatmap
---

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

This tool merges two PNG files into a third PNG file that is an average of each corresponding pair of pixels from the input files.

![GraphicalSummary]

Typical use of this tool is for merging <Highlight color="blue">blue(sense)</Highlight> and <Highlight color="red">red(anti-sense)</Highlight> [__two-color heatmap plots__][heatmap] plots for [__ChIP-exo analysis__][chip-exo-tutorial].

[ToolWindow]

Any file with a `.png` extension may be loaded into the Merge Heatmap tool. When a batch of files have been loaded, the user can click "Merge" which will execute the script which will run the script to pair off samples based on their filenames and merge each pair into a new file with the `*_merge.png` suffix.


The graphical interface for the Merge Heatmap tool relies on strict file naming conventions set by the [__Two-color heatmap tool__][heatmap] to pair a batch of PNG files for merging. For example in the following batch of files,

```
SampleA_anti_treeview.png
SampleB_anti_treeview.png
Another-Name_StructureA_anti_treeview.png
Another-Name_StructureA_sense_treeview.png
SampleA_sense_treeview.png
SampleB_sense_treeview.png
SampleC_sense_treeview.png
```

The following files will be paired off for merging:<br />
`SampleA_sense_treeview.png` &harr; `SampleA_anti_treeview.png`<br />
`SampleB_sense_treeview.png` &harr; `SampleB_anti_treeview.png`<br />
`Another-Name_StructureA_sense_treeview.png` &harr; `Another-Name_StructureA_anti_treeview.png`<br />
unpaired: `SampleC_sense_treeview.png`

:::caution
Make sure you are merging PNG files with the same pixel-dimensions.
:::

### Details of color-averaging strategy

Let every pixel in the __sense `.png` heatmap__ ($P_{sense}$),
every pixel in the __anti-sense `.png` heatmap__ ($P_{anti}$), and
every pixel in the __merged `.png` heatmap__ ($P_{merge}$) be indexed by $i$.

For each $S_i \in P_{sense}$ and $A_i \in P_{anti}$
, let
$S_i = (\textcolor{red}{r_s},\textcolor{green}{g_s},\textcolor{blue}{b_s})$
and
$A_i = (\textcolor{red}{r_a},\textcolor{green}{g_a},\textcolor{blue}{b_a})$.

We define the __merged `.png` heatmap__ ($P_{merge}$) pixel matrix as follows:
$$
\forall M_i \in P_{merge},\newline
M_i = (\frac{\textcolor{red}{r_s} + \textcolor{red}{r_a}}{2},
  \frac{\textcolor{green}{g_s} + \textcolor{green}{g_a}}{2},
  \frac{\textcolor{blue}{b_s} + \textcolor{blue}{b_a}}{2})
$$

## Command Line Interface
Usage:
```bash
java -jar ScriptManager.jar figure-generation merge-heatmap [-hV] [-o=<output>]
<senseFile> <antiFile>
```

Description:

Merge Sense and Antisense png heatmaps

### Positional Input

Expects two [PNG][png-format] like the output from the [heatmap tool][heatmap]. Filenaming format is unrestricted (contrast with GUI) since each of the merged files is explicitly named in the input parameters of the command line signature.


### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output=<output>` | specify output filename, please use PNG extension (`<senseFile>_merged.png` appended to the name in working directory of ScriptManager |



[cdt-format]:file-format.md
[png-format]:file-format.md

[heatmap]:figure-generation/heatmap.md
[chip-exo-tutorial]:threebasicplots-exo.md
