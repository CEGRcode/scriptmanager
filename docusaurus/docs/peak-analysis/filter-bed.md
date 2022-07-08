---
id: filter-bed
title: Filter BED by Proximity
sidebar_label: filter-bed
---

![filter-bed](/../static/icons/PeakAnalysis/FilterBEDbyProximity.svg)

Filter BED file using user-specified exclusion zone using the score column to
determine which peak to retain.

<img src={require('/../static/md-img/PeakAnalysis/filter-bed.png').default} style={{width:70+'%'}}/> 

Usage:
```bash
java -jar ScriptManager.jar peak-analysis filter-bed [-hV] [-e=<exclusion>]
[-o=<outputBasename>] <bedFile>
```

| Input | Description |
| ------ | ----------- |
| `<bedFile>` | The BED file we are filtering on|

### Output Options
| Option | Description |
| ------ | ----------- |
| `-o, --output=<outputBasename>` | specify output file |
| `-e, --exclusion=<exclusion>` | exclusion distance in bp (default=100) |
