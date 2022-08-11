import React from "react";
import clsx from "clsx";
import Layout from "@theme/Layout";
import Link from "@docusaurus/Link";
import useDocusaurusContext from "@docusaurus/useDocusaurusContext";
import useBaseUrl from "@docusaurus/useBaseUrl";
import styles from "./styles.module.css";
import Tabs from "@theme/Tabs";
import TabItem from "@theme/TabItem";

/* Initialize gallery information stored as Javascript objects */

// Create a default information object to ensure  all required fields are filled
const DefaultInfo = {
  title: "Assay Name",
  imageList: [
    {
      url: "img/SM_favicon.png",
    },
  ],
  description: (
    <>
    Describe utility of scriptmanager for assay-specific figure examples.
    </>
  ),
  label: "Get Started",
  href: "docs/",
};

// Create an Javascript object for the ChIPexo gallery tab
const ChipexoInfo = {
  ...DefaultInfo,
  title: "ChIP-exo",
  imageList: [
    {
      url: "img/ChIP-exo_heatmap.jpg",
    },
    {
      url: "img/ChIP-exo_composite.jpg",
    },
  ],
  description: (
    <>
    ScriptManager supports strand-specific base-pair resolution analysis of base-pair resolution assays like
    ChIP-exo. Shown here are examples of the two-color merged heatmap (left) and composite plot (right)
    analyses of real ChIP-exo data.
    </>
  ),
  label: "View tutorial",
  href: "docs/Tutorials/threebasicplots-exo",
};

// Create an Javascript object for the Genomic Features gallery tab
const GenomicInfo = {
  ...DefaultInfo,
  title: "Genomic Features",
  imageList: [
    {
      url: "img/12141_Motif_1_bound_50bp.png",
    },
  ],
  description: (
    <>
      Visualize genomic patterns such as a figure called a "Four Color Sequence Plot"
      which shows nucleotide enrichment across a set of protein-bound sites .
    </>
  ),
};

// Create an Javascript object for the ATAC-seq gallery tab
const AtacseqInfo = {
  ...DefaultInfo,
  title: "ATAC-seq",
  imageUrl: "img/ENCFF534DCE_InsertHistogram.png",
  imageList: [
    {
      url: "img/ENCFF534DCE_InsertHistogram.png",
    },
  ],
  description: (
    <>
      Perform quality checks of genomics data like calculating fragment insert
      size histograms for ATACseq data.
    </>
  ),
};

// Format list of images/figures for a single tab in GalleryTabContent
function DisplayImageElement({ url }) {
  const imgUrl = useBaseUrl(url);
  return (
    // <div className={("", styles.galleryImageMargins)}>
        <img className={styles.galleryImage} src={imgUrl} />
    // </div>
  );
}

// Create and format content for a tab in the gallery section
//  -standardizes formatting of gallery
function GalleryTabContent(props) {
  return (
    <section className={styles.features}>
      <div className="container">
        <div
          className="row"
          style={{
            display: "flex",
            justifyContent: "center",
          }}
        >
          <div className={clsx("col col--6", styles.feature)}>
            <div
              className=""
              style={{
                display: "flex",
                justifyContent: "center",
              }}
            >
              {props.imageList?.map((props, idx) => (
                <DisplayImageElement key={idx} {...props} />
              ))}
            </div>
            <p className="text--left" style={{ padding: 10 + "px" }}>
              {props.description} <br></br>
              <a href={props.href}>
                {props.label} &nbsp;
                <svg
                  width="13.5"
                  height="13.5"
                  aria-hidden="true"
                  viewBox="0 0 24 24"
                  class="iconExternalLink_node_modules-@docusaurus-theme-classic-lib-next-theme-IconExternalLink-styles-module"
                >
                  <path
                    fill="currentColor"
                    d="M21 13v10h-21v-19h12v2h-10v15h17v-8h2zm3-12h-10.988l4.035 4-6.977 7.07 2.828 2.828 6.977-7.07 4.125 4.172v-11z"
                  ></path>
                </svg>
                &nbsp;
              </a>
            </p>
          </div>
        </div>
      </div>
    </section>
  );
}

// Main function to create homepage
//   header: create homepage hero banner (light and dark modes)
//   main: create tabbed figure gallery grouped by different sequencing assay types
function Home() {
  const context = useDocusaurusContext();
  const { siteConfig = {} } = context;
  return (
    <Layout
      title={`Hello from ${siteConfig.title}`}
      description="Description will go into a meta tag in <head />"
    >
      <header className={clsx("hero hero--primary ", styles.heroBanner)}>
        <div className="container">
          <div className={styles.backgroundImg}>
            <img src="img/DNAlayer.png"></img>
          </div>
          <div className={styles.backgroundImgDarkMode}>
            <img src="img/DNAlayer_darkmode.jpg"></img>
          </div>
          <div className="row">
            <div className={clsx("col col-6", styles.leftcol)}>
              <h1 className="hero__title">{siteConfig.title}</h1>
              <p className="hero__subtitle">{siteConfig.tagline}</p>
              <div className={styles.buttons}>
                <Link
                  className={clsx("button button--lg", styles.getStarted)}
                  to={useBaseUrl("docs/")}
                >
                  Get Started
                </Link>
              </div>
            </div>
            <div className="col col-6" style={{ background: "" }}>
              <div className={styles.userinterface}>
                <img src="img/header_img.png"></img>
              </div>
            </div>
          </div>
        </div>
      </header>
      <main>
        <h1 className="text--center" style={{ marginTop: 80 + "px" }}>
          Figure Gallery
        </h1>
        <Tabs class="tabs">
        <TabItem value={ChipexoInfo.title} label={ChipexoInfo.title} default >
            <GalleryTabContent {...ChipexoInfo} />
          </TabItem>
          <TabItem value={GenomicInfo.title} label={GenomicInfo.title} >
            <GalleryTabContent {...GenomicInfo} />
          </TabItem>
          <TabItem value={AtacseqInfo.title} label={AtacseqInfo.title} >
            <GalleryTabContent {...AtacseqInfo} />
          </TabItem>
        </Tabs>
      </main>
    </Layout>
  );
}

export default Home;
