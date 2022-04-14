module.exports = {
  title: 'ScriptManager',
  tagline: 'Toolbox for analyzing your genomic datasets',
  url: 'https://github.io/CEGRcode',
  baseUrl: '/scriptmanager/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  favicon: 'img/ScriptManagerTempLogo_AgencyFBfont.png', //ico file was here
  organizationName: 'CEGRcode', // Usually your GitHub org/user name.
  projectName: 'scriptmanager', // Usually your repo name.
  themeConfig: {
    navbar: {
      title: 'ScriptManager',
      logo: {
        alt: 'My Site Logo',
        src: 'img/ScriptManagerTempLogo_AgencyFBfont.png', //svg file was here
      },
      items: [
        {
          to: 'docs/',
          activeBasePath: 'docs',
          label: 'Docs',
          position: 'left',
        },
        {
          to: 'docs/read-analysis/tag-pileup',
          label: 'Tools',
          position: 'left'
        },
        // {
        //   to: 'docs/bam-format-converter/bam-to-scidx',
        //   label: 'BAM Format Converter',
        //   position: 'left'
        // },
        // {
        //   to: 'docs/bam-manipulation/sort-bam',
        //   label: 'BAM Manipulation',
        //   position: 'left'
        // },
        // {
        //   to: 'docs/bam-statistics/se-stat',
        //   label: 'BAM Statistics',
        //   position: 'left'
        // },
        // {
        //   to: 'docs/coordinate-manipulation/expand-bed',
        //   label: 'Coordinate Manipulation',
        //   position: 'left'
        // },
        // {
        //   to: 'docs/figure-generation/heatmap',
        //   label: 'Figure Generation',
        //   position: 'left'
        // },
        // {
        //   to: 'docs/file-utilities/md5checksum',
        //   label: 'File Utilities',
        //   position: 'left'
        // },
        // {
        //   to: 'docs/peak-analysis/peak-align-ref',
        //   label: 'Peak Analysis',
        //   position: 'left'
        // },
        // {
        //   to: 'docs/read-analysis/tag-pileup',
        //   label: 'Read Analysis',
        //   position: 'left'
        // },
        // {
        //   to: 'docs/sequence-analysis/fasta-extract',
        //   label: 'Sequence Analysis',
        //   position: 'left'
        // },
        {
          to: 'blog',
          label: 'Blog',
          position: 'left'
        },
        {
          href: 'https://github.com/owlang/scriptmanager',
          label: 'GitHub',
          position: 'right',
        },
      ],
    },
    footer: {
      style: 'dark',
      links: [
        {
          title: 'Docs',
          items: [
            {
              label: 'Style Guide',
              to: 'docs/',
            },
            {
              label: 'Tool Index (A-Z)',
              to: 'docs/tool-index',
            },
          ],
        },
        {
          title: 'Community',
          items: [
            {
              label: 'Stack Overflow',
              href: 'https://stackoverflow.com/questions/tagged/docusaurus',
            },
            {
              label: 'Discord',
              href: 'https://discordapp.com/invite/docusaurus',
            },
            {
              label: 'Twitter',
              href: 'https://twitter.com/docusaurus',
            },
          ],
        },
        {
          title: 'More',
          items: [
            {
              label: 'Blog',
              to: 'blog',
            },
            {
              label: 'GitHub',
              href: 'https://github.com/owlang/scriptmanager',
            },
          ],
        },
      ],
      copyright: `Copyright © ${new Date().getFullYear()} My Project, Inc. Built with Docusaurus.`,
    },
  },
  presets: [
    [
      '@docusaurus/preset-classic',
      {
        docs: {
          sidebarPath: require.resolve('./sidebars.js'),
          // Please change this to your repo.
          editUrl:
            'https://github.com/facebook/docusaurus/edit/master/website/',
        },
        tools: {
            sidebarPath: require.resolve('./sidebars.js'),
            // Please change this to your repo.
            editUrl:
              'https://github.com/facebook/docusaurus/edit/master/website/',
          },
        blog: {
          showReadingTime: true,
          // Please change this to your repo.
          editUrl:
            'https://github.com/facebook/docusaurus/edit/master/website/blog/',
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      },
    ],
  ],
};
