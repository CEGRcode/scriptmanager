import React from "react";
import clsx from "clsx";
import Layout from "@theme/Layout";
import Link from "@docusaurus/Link";
import useDocusaurusContext from "@docusaurus/useDocusaurusContext";
import useBaseUrl from "@docusaurus/useBaseUrl";
import styles from "./styles.module.css";
import Tabs from "@theme/Tabs";
import TabItem from "@theme/TabItem";

const chipexo = [
  /*{
    title: "ChIP-exo",
    imageUrl: "img/ChIP-exo.jpg",
    description: (
      <>
        ChIP-exo: "ScriptManager supports strand-specific base-pair resolution
        analysis of base-pair resolution assays like ChIP-exo"
      </>
    ),
    label: "View tutorial",
    href: "docs/Tutorials/threebasicplots-exo",
  },*/
  {
    /*title: "ChIP-exo",*/
    imageUrl: "img/ChIP-exo2.jpg",
    /*description: (
      <>
        ChIP-exo: "ScriptManager supports strand-specific base-pair resolution
        analysis of base-pair resolution assays like ChIP-exo"
      </>
    ),*/
    /*label: "View tutorial",
    href: "docs/Tutorials/threebasicplots-exo",*/
  },
  {
    /*title: "ChIP-exo",*/
    imageUrl: "img/composite.jpg",
    /*description: (
      <>
        ChIP-exo: "ScriptManager supports strand-specific base-pair resolution
        analysis of base-pair resolution assays like ChIP-exo"
      </>
    ),*/
    /*label: "View tutorial",
    href: "docs/Tutorials/threebasicplots-exo",*/
  },
];

const Genomic = [
  /*{
    title: "Genomic Features",
    imageUrl: "img/Genomic Features.jpg",
    description: (
      <>
        Genomic Features: "Visualize genomic features such as the relative
        positional relationship of all kinds of annotations such as peak
        coordinate files, transcription start sites (TSS), or anything that is
        represented by a genomic coordinate interval."
      </>
    ),
    label: "View tutorial",
    href: "",
  },*/
  {
    /*title: "Genomic Features",*/
    imageUrl: "img/12141_Motif_1_bound_50bp.png",
    description: (
      <>
        Visualize genomic patterns such as nucleotide enrichment across a set of protein-bound sites (called a "four-color plot").
      </>
    ),
    /*label: "View tutorial",*/
    /*href: "",*/
  } /*,
  {
    title: "Genomic Features",
    imageUrl: "img/Genomic Features.jpg",
    description: (
      <>
        Genomic Features: "Visualize genomic features such as the relative
        positional relationship of all kinds of annotations such as peak
        coordinate files, transcription start sites (TSS), or anything that is
        represented by a genomic coordinate interval."
      </>
    ),
    label: "View tutorial",
    href: "",
  },*/,
];

const Atacseq = [
  /*{
    title: "ATAC-seq",
    imageUrl: "img/ATAC-seq.jpg",
    description: (
      <>
        ATAC-seq: "Pileup Next Generation Sequencing (NGS) data from assays like
        ATAC-seq with optional filters for mono-nucleosomal fragments"
      </>
    ),
    label: "View tutorial",
    href: "",
  },*/
  {
    /*title: "ATAC-seq",*/
    imageUrl: "img/ENCFF534DCE_InsertHistogram.png",
    description: (
      <>
        Perform quality checks of genomics data like calculating fragment insert size histograms for ATACseq data.
      </>
    ),
    /*label: "View tutorial",*/
    /*href: "",*/
  },
  /*{
    title: "ATAC-seq",
    imageUrl: "img/ATAC-seq.jpg",
    description: (
      <>
        ATAC-seq: "Pileup Next Generation Sequencing (NGS) data from assays like
        ATAC-seq with optional filters for mono-nucleosomal fragments"
      </>
    ),
    label: "View tutorial",
    href: "",
  },*/
];

/*----- Funtion fore the ChIP-exo tab which only has 2 diagrams  ------*/
function Feature2({ imageUrl }) {
  const imgUrl = useBaseUrl(imageUrl);
  return (
    <div className={("", styles.feature2)}>
      <div className="text--center">
        <img className={styles.feature2Image} src={imgUrl} />
      </div>
    </div>
  );
}

function Feature({ imageUrl, title, description, label, href }) {
  const imgUrl = useBaseUrl(imageUrl);
  return (
    <div className={clsx("col col--6", styles.feature)}>
      {imgUrl && (
        <div className="text--center">
          <img className={styles.featureImage} src={imgUrl} alt={title} />
        </div>
      )}
      <h3 className="text--center">{title}</h3>
      <p className="text--left" style={{ padding: 10 + "px" }}>
        {description}
      </p>
    </div>
  );
}

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
          <TabItem value="ChIP-exo" label="ChIP-exo" default>
            {chipexo && chipexo.length > 0 && (
              <section className={styles.features}>
                <div className="container">
                  <div
                    className=""
                    style={{
                      display: "flex",
                      justifyContent: "center",
                    }}
                  >
                    {chipexo.map((props, idx) => (
                      <Feature2 key={idx} {...props} />
                    ))}
                  </div>
                  <div className="row">
                    <div className="col col--2"></div>
                    <div className="col col--8">
                      <p>
                        ScriptManager supports strand-specific base-pair
                        resolution analysis of base-pair resolution assays like
                        ChIP-exo. Shown here are examples of the two-color
                        merged heatmap (left) and composite plot (right)
                        analyses of real ChIP-exo data
                      </p>
                      <a href="docs/Tutorials/threebasicplots-exo">
                        View tutorial
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
                      </a>
                    </div>
                    <div className="col col--2"></div>
                  </div>
                </div>
              </section>
            )}
          </TabItem>
          <TabItem value="Genomic Features" label="Genomic Features">
            {Genomic && Genomic.length > 0 && (
              <section className={styles.features}>
                <div className="container">
                  <div
                    className="row"
                    style={{
                      display: "flex",
                      justifyContent: "center",
                    }}
                  >
                    {Genomic.map((props, idx) => (
                      <Feature key={idx} {...props} />
                    ))}
                  </div>
                </div>
              </section>
            )}
          </TabItem>
          <TabItem value="ATAC-seq" label="ATAC-seq">
            {Atacseq && Atacseq.length > 0 && (
              <section className={styles.features}>
                <div className="container">
                  <div
                    className="row"
                    style={{
                      display: "flex",
                      justifyContent: "center",
                    }}
                  >
                    {Atacseq.map((props, idx) => (
                      <Feature key={idx} {...props} />
                    ))}
                  </div>
                </div>
              </section>
            )}
          </TabItem>
        </Tabs>
      </main>
    </Layout>
  );
}

export default Home;
