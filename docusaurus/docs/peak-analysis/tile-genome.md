---
id: tile-genome
title: Genomic Coordinate Tile
sidebar_label: tile-genome
---

![tile-genome](/../static/icons/Peak_Analysis/TileGenome_square.svg)

Generate a coordinate file that tiles (non-overlapping) across an entire genome.

<img src={require('/../static/md-img/Peak_Analysis/TileGenomeWindow.png').default} style={{width:70+'%'}}/>

## Command Line Interface

Usage:
```bash
java -jar ScriptManager.jar peak-analysis tile-genome [-fhV] [-o=<output>]
[-w=<window>] <genome>
```

| Input | Description |
| ------ | ----------- |
| `<genome>` | reference genome [sacCer3_cegr|hg19|hg19_contigs|mm10] |


### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output=<output>` | specify output directory (name will be `genome_tiles_<genome>_<window>bp.<ext>`) |


| Option | Description |
| ------ | ----------- |
| `-f, --gff` | file format output as GFF (default format as BED) |
| `-w, --window=<window>` | window size in bp (default=200) |
