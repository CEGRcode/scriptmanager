---
id: md5checksum
title: MD5 Checksum
sidebar_label: MD5 Checksum
---

![MD5Checksum](/../static/icons/File_Utilities/MD5Checksum_square.svg)

A common quality control and security step that everyone should use when downloading files from another source is to compare [MD5 checksum][md5sum-original] values to ensure that the file that was downloaded exactly matches the reported MD5checksum value for the file.

MD5 is one of several methodologies (message digest function, others including the hash function [SHA-1][sha1sum-link]) for calculating a near-unique alphanumeric code for a file. MD5sum values also need to be generated for submissions to public data repositories such as NCBI's [Gene Expression Omnibus (GEO)][submission-reqs-geo] and [Sequence Read Archive(SRA)][submission-reqs-sra] and EBI's [European Nucleotide Archive (ENA)][submission-reqs-ena].

![ENA_MD5sum](/../static/md-img/ENA_MD5sum.png)

For example, the European Nucleotide Archive provides MD5 checksum file values for FASTQ and BAM files downloaded from their servers. To ensure that you downloaded the correct file, to ensure that the file was not corrupted, and to ensure that the file downloaded completely, you can run this tool on the file and make sure that the alphanumeric code matches the code provided by ENA for the file.

<img src={require('/../static/md-img/File_Utilities/MD5ChecksumWindow.png').default} style={{width:70+'%'}}/>

The window allows file formats of any kind to be uploaded to the tool since an MD5 sum calculation is agnostic to file formats. After pressing "Calculate," the tool will execute to determine a MD5sum value for each input file and save it to a file called `md5checksum.txt` in the "Output Directory." The following is an example of the output format where each line shows the input file within parentheses and the MD5 sum value after the `=`.

```
MD5 (Peaks.bed) = b174c550aeae515accc73308e136ec1f
MD5 (composite_average.out) = 7182636d6e3f31bbac970e936c6760c5
```

### Command Line Interface

_CommandLine tools already exist for this function. This tool only exists as a GUI wrapper in ScriptManager._

Please see the [md5sum tool][md5sum-original].

[md5sum-original]:https://www.geeksforgeeks.org/md5sum-linux-command/
[sha1sum-link]:https://en.wikipedia.org/wiki/SHA-1
[submission-reqs-geo]:https://ghtf.biochem.uci.edu/ncbi-geo-submission/
[submission-reqs-sra]:https://anonsvn.ncbi.nlm.nih.gov/repos/v1/trunk/sra/doc/SRA_1-1/SRA_Quick_Start_Guide.pdf
[submission-reqs-ena]:https://biodiversitydata-se.github.io/mol-data/ena-metabar.html
