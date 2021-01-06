---
id: peak-align-ref
title: Align BED to Reference
sidebar_label: peak-align-ref
---

Align BED peaks to Reference BED file creating CDT files for heatmap generation

Usage:
```bash
java -jar ScriptManager.jar peak-analysis peak-align-ref [-hV] [-o=<output>]
<peakBED> <refBED>
```
| Input | Description |
| ------ | ----------- |
| `<peakBED>` | The BED peak file |
| `<refBED>` | The BED reference file |

### Output Options
| Option | Description |
| ------ | ----------- |
| `-o, --output=<output>` | specify output file |
