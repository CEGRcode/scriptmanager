# scriptmanager

Expanded Documentation at https://pughlab.mbg.cornell.edu/scriptmanager/

### GUI pipeline containing useful NGS analysis scripts.

Scripts are generically categorized within semi-descriptive tabs and are designed to be run in parallel with each other and themselves.


### Tutorial to using basic plotting capabilities of ScriptManager
Open **ScriptManager-Tutorial.docx** available in root directory

## Build Instructions
### From source
(after cloning this repo):
```
$ cd scriptmanager
$ ./gradlew build
```

The compiled JAR file will be output into the `build/libs` directory. 

### Singularity
```
$ cd scriptmanager
$ singularity build --fakeroot scriptmanager.sif scriptmanager.def
```

## Running ScriptManager

### .jar file
To run the GUI verson of ScriptManager, you can double click the compiled JAR file or run the followinug command in the terminal
```
$ java -jar /path/to/jarfile
```

Running the CLI version of ScriptManager is similar to running the GUI. To view the list of command options, use the `-h` flag.
```
$ java -jar /path/to/jarfile -h
```

**Example:**
```
$ java -jar /path/to/jarfile coordinate-manipulation bed-to-gff BEDFILE.bed -o OUTPUT.gff
```

### Singularity
```
$ singularity exec scriptmanager.sif scriptmanager ${command}
```

**Example:**
```
$ singularity exec scriptmanager.sif scriptmanager coordinate-manipulation bed-to-gff BEDFILE.bed -o OUTPUT.gff
```

## Current scripts available (210106):

**BAM Statistics:**

  -BAM Statistics
  
  -Paired-End Statistics
  
  -BAM Genome Correlation
  

**BAM Manipulation:**

  -BAM-BAI Indexer
  
  -BAM File Sorter
  
  -BAM Remove Duplicates
  
  -BAM Replicate Merge
  
  -Filter for PIP-seq
  

**BAM Format Converter:**

  -BAM to scIDX
  
  -BAM to GFF
  
  -BAM to BED
  
  -BAM to bedGraph
  
  
**File Utilities:**

  -MD5 Checksum

  
**Peak Calling:**

  -Genetrack - still highly unstable

  
**Peak Analysis:**

  -Align BED to Reference
  
  -Filter BED by Proximity
  
  -Genomic Coordinate Tile
  
  -Generate Random Coordinate
    
  -Signal Duplication


**Coordinate File Manipulation:**

  -Expand BED/GFF File
  
  -Convert BED/GFF to GFF/BED
  
  -Sort BED/GFF by CDT
 

**Sequence Read Analysis:**

  -Tag Pileup
  
  -Calculate Scaling Factor
  
  -Scale Matrix Data
  
  -Aggregate Data
  

**DNA Sequence Analysis:**

  -FASTA from BED
  
  -Randomize FASTA
  
  -Search Motif in FASTA
  
  -DNA Shape from BED
  
  -DNA Shape from FASTA


  **Figure Generation:**

  -Heatmap
  
  -Merge Heatmaps
  
  -4Color Sequence Plot
