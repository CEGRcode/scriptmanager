## Singularity

### Build Singularity Image
```
$ cd scriptmanager
$ singularity build --fakeroot scriptmanager.sif scriptmanager.def
```

### Run from Singularity Image
```
$ singularity exec scriptmanager.sif scriptmanager ${command}
```

**Example:**
```
$ singularity exec scriptmanager.sif scriptmanager coordinate-manipulation bed-to-gff BEDFILE.bed -o OUTPUT.gff
```
