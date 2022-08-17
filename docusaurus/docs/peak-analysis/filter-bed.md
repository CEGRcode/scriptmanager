---
id: filter-bed
title: Filter BED by Proximity
sidebar_label: filter-bed
---

![filter-bed](/../static/icons/Peak_Analysis/FilterBEDbyProximity_square.svg)

Filter BED file using user-specified exclusion zone using the score column to
determine which peak to retain.

<img src={require('/../static/md-img/Peak_Analysis/FilterBEDbyProximityWindow.png').default} style={{width:70+'%'}}/>

## Command Line Interface

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
