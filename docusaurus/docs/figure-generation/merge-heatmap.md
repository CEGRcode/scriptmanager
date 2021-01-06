---
id: merge-heatmap
title: Merge Heatmap
sidebar_label: merge-heatmap
---

Usage:
```bash
java -jar ScriptManager.jar figure-generation merge-heatmap [-hV] [-o=<output>]
<senseFile> <antiFile>
```

Description:

Merge Sense and Antisense png heatmaps

### Positional Input

Expects two [PNG][png-format] like the output from the [heatmap tool][heatmap]


### Output Options

| Option | Description |
| ------ | ----------- |
| `-o, --output=<output>` | specify output filename, please use PNG extension (`<senseFile>_merged.png` appended to the name in working directory of ScriptManager |

[cdt-format]:file-format.md
[png-format]:file-format.md
