---
id: threebasicplots-exo
title: "ChIP-exo: three basic plots"
sidebar_label: "ChIP-exo: three basic plots"
---

_Generating 3 basic sequence-specific ChIP-exo plots: composite, heatmap, and four-color plots_


## 1. Open ScriptManager

...by double-clicking on the icon

![open-sm]



## 2. Download Data

+ You need one BED file, one BAM file, and the reference genome FASTA to complete this exercise.
  + [Sample BED file for Reb1][testdata-reb1-bed]
  + [Sample BAM file for Reb1][testdata-reb1-bam]
  + [Reference Yeast Genome(sacCer3_cegr)][saccer3cegr-fasta]

## 3. Check for BAM index

If \*.BAI exists, you're good. Move onto the next step.
```
filename1.bam
filename1.bam.bai
filename2.bam
...
```

If \*.BAI does NOT exist...
```
filename1.bam
filename2.bam
...
```

  * Navigate to __BAM Manipulation__ -> [__BAM-BAI Indexer__][bam-indexer]
![maingui-bamidx](/../static/figs-ex-chipexo/maingui-bamidx.png)
  * Generate BAI index files for each BAM file of interest by loading your BAM file and clicking "Index."
![gui-bamidx](/../static/figs-ex-chipexo/gui-bamidx.png)

:::tip

SAM/BAM standard is to keep BAI file in same directory as BAM file with the ScriptManager-generated filename

:::

## 4. Resizing BED files

BED file coordinates often need to be resized for more informative tag pileups. The BED file we provide in this example is already sized to a 250bp window so for the purposes of this exercise, you can _skip this step_.

+ Navigate to __Coordinate File Manipulation__ -> [__Expand BED File__][expand-bed]

![maingui-expandbed](/../static/figs-ex-chipexo/maingui-expandbed.png)

:::tip
For Reb1 (yeast) 250-500 bp windows are generally sufficient. Mammalian samples may require larger windows (500-2000 bp) based on the amount of indirect-crosslinking
:::


## 5. Pileup tags

Pileup the BAM data within a set of BED coordinate windows to generate the composite plot and the matrix(CDT) files that will be used to generate the heatmaps.

+ Navigate to __Sequence Read Analysis__ -> [__Tag Pileup__][tag-pileup]

![maingui-tagpileup](/../static/figs-ex-chipexo/maingui-tagpileup.png)

+ Load the BED and BAM files
  + The default parameters tag pileup is set to expect a sequence-specific strand separated ChIP-exo dataset. Modifications to these parameters are needed for specific analysis or alternative assays.
+	Select output directory and ‘Pile Tags’
      + Bioinformatic projects should be organized in a uniform and consistent manner as described below
      + [Paper on how to organize bioinformatics projects (Noble 2009)][noble2009]

![gui-tagpileup](/../static/figs-ex-chipexo/gui-tagpileup.png)
+ The displayed composite plot can be modified by right-clicking and selecting properties.
    + Things such as axis labels, axis range, and colors can be modified here.
    + The final image can then be saved by right-clicking and selecting ‘Save as’. PNG is fine for most cases, but SVG is strongly recommended if this composite plot will be used in Adobe Illustrator later.

![gui-tagpileup-out](/../static/figs-ex-chipexo/gui-tagpileup-out.png)

+ Note that besides the composite plot image, ScriptManager has saved the matrix \*.CDT files to your Output Directory together with the composite plot values file (If you didn't change the name it would be called `composite_avg.out`). These CDT files will be used as the input for generating heatmaps in the next step.



## 6.	Generate Heatmaps

+ Navigate to __Figure Generation__ -> [__Heat Map__][heatmap].

![maingui-heatmap](/../static/figs-ex-chipexo/maingui-heatmap.png)

+ Heatmap Generator can only generate one color at a time, so ‘Sense’ and ‘Anti’ files should be processed separately. The lab standard for strand colors is ‘Sense’ == blue and ‘Anti’ == red.
  + You can start by generating the 'Sense' heatmap first. Click "Load Files" and select the "\_sense.cdt" output CDT files from running the [Tag Pileup][tag-pileup] step.
  + Next check that you are using the blue colorsper the lab standard
  + If you're using the files linked earlier, the heatmaps show the best contrast when using the default "Percentile Threshold" value. This is a step you may need to play around with to find the right contrast for you data. Otherwise your heatmaps will come out too light or too dark if your data is too shallow or too deep. Whatever you choose, make sure you use the same thresholding for the "Anti" heatmap.
  + The last step before generating is to click the "Output Heatmap" checkbox. The Heatmap generator does not save the produced PNG by default.
  + Click "Generate" to save you Sense PNG heatmap!

![gui-heatmap-sense](/../static/figs-ex-chipexo/gui-heatmap-sense.png)

+ The process for generating the "Anti" image is similar:
  + Start by removing the "Sense" file using the "Remove Files" button. Make sure you select the files you want to remove before pressing the button.
  + Next switch the color to red for "Anti"
  + Since all the other parameters should be the same from you "Sense" run, go ahead and just click "Generate"


![gui-heatmap-anti](/../static/figs-ex-chipexo/gui-heatmap-anti.png)


+ Navigate to __Figure Generation__ -> [__Merge Heatmaps__][merge-heatmap] so we can merge our strand-separated heatmaps into a single PNG.

![maingui-mergehm](/../static/figs-ex-chipexo/maingui-mergehm.png)

+ The script will automatically match sense to anti through the standardized naming conventions in ScriptManager
   + Click "Load PNG Files" to select the output from the HeatMap tool
   + Click "Generate" to merge the PNG files into the same heatmap

![gui-mergehm](/../static/figs-ex-chipexo/gui-mergehm.png)

+ The following are the two heatmap  mages before and after merging

![reb1-blue](/../static/figs-ex-chipexo/Reb1_sense.png) ![reb1-red](/../static/figs-ex-chipexo/Reb1_anti.png) ![reb1-merge](/../static/figs-ex-chipexo/Reb1_merge.png)


## 7.	Generate Four-color plots

+ Navigate to __DNA Sequence Analysis__ -> [__FASTA from BED__][fasta-extract] to create the input for generating a Four Color plot.

![maingui-extractfasta](/../static/figs-ex-chipexo/maingui-extractfasta.png)

+ Load the FASTA file containing the Genome FASTA and the appropriate BED file for sequence FASTA generation.
  + A \*.fai file will be generated for the genome file the first time it is used. If the Genome FASTA file is NOT in proper FASTA format the script will fail
  + For sequence-specific factors, a window of ~30 bp is appropriate relative to the motif midpoint. (Go back to ExpandBed to create such a file)

![gui-extractfasta](/../static/figs-ex-chipexo/gui-extractfasta.png)

+ Navigate to __Figure Generation__ -> [__Four Color Plot__][four-color] to generate the plot once you have generated the FASTA file of the sequences within the BED regions.

![maingui-fourcolor](/../static/figs-ex-chipexo/maingui-fourcolor.png)

+ Load the FASTA file containing the FASTA sequences.

![gui-fourcolor](/../static/figs-ex-chipexo/gui-fourcolor.png)

+ Tah dah! You've made the third and final figure of this tutorial! It's kind of tall but you can resize it in your favorite image editing software.

![reb1-fourcolor](/../static/figs-ex-chipexo/fourcolor.png)

## Command-Line Bash Script

Within the [Github repository][github-repo], there is (will be) also an [example bash script][ex-chipexo-bash] that takes a BED file, BAM file, and OUTPUT basename as arguments to generate heatmaps. This can serve as a template for you to write out your own workflows as bash scripts that execute command-line style ScriptManager.

```bash

```

[open-sm]:/../static/figs-ex-chipexo/open-sm.png
[gui-expandbed]:/../static/figs-ex-chipexo/gui-expandbed.png

[testdata-reb1-bed]:https://github.com/CEGRcode/2018-Rossi_GenomeResearch/blob/master/Fig1_Reb1/A.Reb1_Rhee_primary_sites_975.bed
[testdata-reb1-bam]:ftp://data1.commons.psu.edu/pub/commons/eberly/pughlab/yeast-epigenome-project/12141_YEP.zip
[saccer3cegr-fasta]:https://github.com/CEGRcode/GenoPipe/blob/master/EpitopeID/utility_scripts/genome_data/download_sacCer3_Genome.sh
[noble2009]:https://journals.plos.org/ploscompbiol/article/file?id=10.1371/journal.pcbi.1000424&type=printable
[github-repo]:https://www.github.com/CEGRcode/scriptmanager
[ex-chipexo-bash]:https://www.github.com/CEGRcode/scriptmanager

[bam-indexer]:/docs/bam-manipulation/bam-indexer.md
[expand-bed]:/docs/coordinate-manipulation/expand-bed.md
[tag-pileup]:/docs/read-analysis/tag-pileup.md
[heatmap]:/docs/figure-generation/heatmap.md
[merge-heatmap]:/docs/figure-generation/merge-heatmap.md
[fasta-extract]:/docs/sequence-analysis/fasta-extract.md
[four-color]:/docs/figure-generation/four-color.md
