---
id: chrname-converter
title: Chromosome Name Converter
sidebar_label: Chr Name Converter
---

![ChrNameConverter](/../static/icons/File_Utilities/ConvertChrNames_square.svg)

Different files using different chromosome naming systems for the same genome build presents a challenge during analysis. This tool is provided as a converter between chromosome naming systems for tab-delimited file formats such as BED and GFF.

__Arabic &harr; Roman__<br />
chr1 &harr; chrI<br />
chr2 &harr; chrII<br />
chr3 &harr; chrIII<br />
...<br />
chr16 &harr; chrXVI

More specifically, the __sacCer3 genome build__ uses an arabic numeral naming system (chr1 through chr16) or a roman numeral naming system (chrI through chrXVI). This script serves as a converter between these naming systems and includes options around the mitochondrial and 2-micron naming features.

chrM &harr; chrmt

Also, a mitochondiral chromosome name conversion is also included with the selection of a checkbox option.

<img src={require('/../static/md-img/File_Utilities/ConvertBEDChrNamesWindow.png').default} style={{width:50+'%'}}/><img src={require('/../static/md-img/File_Utilities/ConvertGFFChrNamesWindow.png').default} style={{width:50+'%'}}/>

### Command Line Interface
