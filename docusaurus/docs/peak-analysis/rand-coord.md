---
id: rand-coord
title: Generate Random Coordinate
sidebar_label: rand-coord
---

Generate a coordinate file that tiles (non-overlapping) across an entire genome.

Usage:
```bash
java -jar ScriptManager.jar peak-analysis rand-coord [-fhV] [-n=<numSites>]
[-o=<output>] [-w=<window>] <genome>
```

| Input | Description |
| ------ | ----------- |
| `<genome>` | reference genome [sacCer3_cegr|hg19|hg19_contigs|mm10] |

### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output=<output>` | specify output directory (name will be `random_coordinates_<genome>_<window>bp.<ext>`) |
| `-f, --gff` | file format output as GFF (default format as BED) |
| `-n, --num-sites=<numSites>` | number of sites (default=1000) |
| `-w, --window=<window>` | window size in bp (default=200) |
