import React from "react";

// Based on https://theochu.com/docusaurus/sortable-tables/
// ----------------------------------------------------------------------------
// Please respect chronological (year) order when adding new entries.
// ----------------------------------------------------------------------------

// consider adding lists of tool-links for highlighting tools used in various publications

export const publications = [
  {
    "author": "John",
    "title": "Genome-wide promoter assembly in E. coli measured at single-base resolution.",
    "year": "2022",
    "journal": "Genome Res.",
    "PMID": "35483960"
  },

  {
    "author": "Zhao",
    "title": "Ssl2/TFIIH function in transcription start site scanning by RNA polymerase II in Saccharomyces cerevisiae.",
    "year": "2021",
    "journal": "Elife.",
    "PMID": "34652274"
  },

  {
    "author": "Lai",
    "title": "A ChIP-exo screen of 887 Protein Capture Reagents Program transcription factor antibodies in human cells.",
    "year": "2021",
    "journal": "Genome Res.",
    "PMID": "34426512"
  },

  {
    "author": "Rossi",
    "title": "A high-resolution protein architecture of the budding yeast genome.",
    "year": "2021",
    "journal": "Nature.",
    "PMID": "33692541"
  },

  {
    "author": "Mittal",
    "title": "High similarity among ChEC-seq datasets.",
    "year": "2021",
    "journal": "bioRxiv.",
    "doi": "https://doi.org/10.1101/2021.02.04.429774"
  },

  {
    "author": "Badjatia",
    "title": "Acute stress drives global repression through two independent RNA polymerase II stalling events in Saccharomyces.",
    "year": "2021",
    "journal": "Cell Rep.",
    "PMID": "33472084"
  },

  {
    "author": "Rossi",
    "title": "Simplified ChIP-exo assays.",
    "year": "2018",
    "journal": "Nat Commun.",
    "PMID": "30030442"
  },

  {
    "author": "Rossi",
    "title": "Genome-wide determinants of sequence-specific DNA binding of general regulatory factors.",
    "year": "2018",
    "journal": "Genome Res.",
    "PMID": "29563167"
  },

  {
    "author": "Vinayachandran",
    "title": "Widespread and precise reprogramming of yeast protein-genome interactions in response to heat shock.",
    "year": "2018",
    "journal": "Genome Res.",
    "PMID": "29444801"
  },

  {
    "author": "Lai",
    "title": "Genome-wide uniformity of human 'open' pre-initiation complexes.",
    "year": "2017",
    "journal": "Genome Res.",
    "PMID": "27927716"
  },

  {
    "author": "Rossi",
    "title": "Correspondence: DNA shape is insufficient to explain binding.",
    "year": "2017",
    "journal": "Nat Commun.",
    "PMID": "28580956"
  }
];

// Format title link
function DisplayTitleLink({value, original}) {
  var url = original.doi;
  if(original.PMID){
    url = "https://pubmed.ncbi.nlm.nih.gov/" + original.PMID;
  }
  return (
    <a href={`${url}`} target="blank" rel="noreferrer noopener">
      {value}
    </a>
  );
}

// ----------------------------------------------------------------------------
// PesterDataTable column definition
// ----------------------------------------------------------------------------
export const columns = [
  {
    Header: "Author",
    accessor: "author",
    className: "pester-data-table left",
  },
  {
    Header: "Year",
    accessor: "year",
    className: "pester-data-table",
    Cell: ({ cell: { value }, row: { original } }) => (
      <b> {value} </b>
    ),
  },
  {
    Header: "Title",
    accessor: "title",
    className: "pester-data-table left",
    Cell: ({ cell: { value }, row: { original } }) => (
      <DisplayTitleLink value={value} original={original}/>
    ),
  },
  {
    Header: "Journal",
    accessor: "journal",
    className: "pester-data-table",
  },
];
