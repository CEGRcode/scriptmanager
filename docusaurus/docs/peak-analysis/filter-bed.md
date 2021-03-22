---
id: filter-bed
title: Filter BED by Proximity
sidebar_label: filter-bed
---

Filter BED file using user-specified exclusion zone using the score column to
determine which peak to retain.

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
