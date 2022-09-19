module.exports = {
  someSidebar: {
    Guides: ["Guides/getting-started", "Guides/command-line", "Guides/tool-group", "Guides/color-guide"],
    Tutorials: ["Tutorials/threebasicplots-exo"],
    References: ["file-formats", "References/tool-index", "References/publications"],
    Contributing: ["Contributing/developer-guidelines", "Contributing/testing"],
    Community: ["Community/code-of-conduct"],
  },
  toolSidebar: {
    "BAM Format Converter": [
      "bam-format-converter/bam-to-scidx",
      "bam-format-converter/bam-to-gff",
      "bam-format-converter/bam-to-bed",
      "bam-format-converter/bam-to-bedgraph",
    ],
    "BAM Manipulation": [
      "bam-manipulation/bam-indexer",
      "bam-manipulation/sort-bam",
      "bam-manipulation/remove-duplicates",
      "bam-manipulation/merge-bam",
      "bam-manipulation/filter-pip-seq",
    ],
    "BAM Statistics": [
      "bam-statistics/se-stat",
      "bam-statistics/pe-stat",
      "bam-statistics/bam-correlation",
    ],
    "Coordinate Manipulation": [
      "coordinate-manipulation/expand-bed",
      "coordinate-manipulation/expand-gff",
      "coordinate-manipulation/bed-to-gff",
      "coordinate-manipulation/gff-to-bed",
      "coordinate-manipulation/sort-bed",
      "coordinate-manipulation/sort-gff",
    ],
    "Figure Generation": [
      "figure-generation/heatmap",
      "figure-generation/merge-heatmap",
      "figure-generation/composite-plot",
      "figure-generation/four-color",
      "figure-generation/heatmap-labeler",
      "figure-generation/three-color-heatmap",
    ],
    "File Utilities": [
      "file-utilities/md5checksum",
      "file-utilities/chrname-converter",
    ],
    "Peak Analysis": [
      "peak-analysis/peak-align-ref",
      "peak-analysis/filter-bed",
      "peak-analysis/tile-genome",
      "peak-analysis/rand-coord",
      "peak-analysis/signal-dup",
    ],
    "Read Analysis": [
      "read-analysis/tag-pileup",
      "read-analysis/scaling-factor",
      "read-analysis/scale-matrix",
      "read-analysis/aggregate-data",
    ],
    "Sequence Analysis": [
      "sequence-analysis/fasta-extract",
      "sequence-analysis/randomize-fasta",
      "sequence-analysis/search-motif",
      "sequence-analysis/dna-shape-bed",
      "sequence-analysis/dna-shape-fasta",
    ],
  },
};
