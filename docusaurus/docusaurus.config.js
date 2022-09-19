// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const math = require('remark-math');
const katex = require('rehype-katex');
const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'ScriptManager',
  tagline: 'Toolbox for analyzing your genomic datasets',
  url: 'https://github.com/CEGRcode',
  baseUrl: '/scriptmanager/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  favicon: 'img/SM_favicon.png', //ico file was here

  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  organizationName: 'CEGRcode', // Usually your GitHub org/user name.
  projectName: 'scriptmanager', // Usually your repo name.

  // Even if you don't use internalization, you can use this field to set useful
  // metadata like html lang. For example, if your site is Chinese, you may want
  // to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          sidebarPath: require.resolve('./sidebars.js'),
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          //editUrl:
            //'https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/',
          // path: 'docs',
          remarkPlugins: [math],
          rehypePlugins: [katex],
          showLastUpdateAuthor: true,
          showLastUpdateTime: true,
        },
//        blog: {
//          showReadingTime: true,
//          // Please change this to your repo.
//          // Remove this to remove the "edit this page" links.
//          //editUrl:
//            //'https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/',
//        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      }),
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      navbar: {
        title: 'ScriptManager',
        logo: {
          alt: 'ScriptManager Logo',
          src: 'img/SM_favicon.png', //svg file was here
        },
        items: [
          {
            type: 'doc',
            docId: 'Guides/getting-started',
            position: 'left',
            label: 'General',
          },
          {
            type: 'doc',
            docId: 'read-analysis/tag-pileup',
            position: 'left',
            label: 'Tools',
          },
          {
            href: 'https://github.com/CEGRcode/scriptmanager',
            className: "header-github-link",
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
                label: 'Tool Index (A-Z)',
                to: '/docs/References/tool-index',
              },
              {
                label: 'File Formats',
                to: '/docs/file-formats',
              },
            ],
          },
          {
            title: 'Community',
            items: [
              {
                label: 'Pugh Lab',
                href: 'https://pughlab.mbg.cornell.edu',
              },
              {
                label: 'Lai Lab',
                href: 'https://williamkmlai.github.io',
              },
            ],
          },
          {
            title: 'Other Tools We Develop',
            items: [
              {
                label: 'ScriptManager',
                href: 'https://github.com/CEGRcode/scriptmanager',
              },
              {
                label: 'PEGR',
                href: 'https://github.com/seqcode/pegr',
              },
              {
                label: 'STENCIL',
                href: 'https://github.com/CEGRcode/stencil',
              },
              {
                label: 'GenoPipe',
                href: 'https://github.com/CEGRcode/GenoPipe',
              },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} ScriptManager. Built with Docusaurus.`,
      },
      prism: {
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme,
      },
    }),
  stylesheets: [
    {
      href: 'https://cdn.jsdelivr.net/npm/katex@0.13.24/dist/katex.min.css',
      type: 'text/css',
      integrity:
        'sha384-odtC+0UGzzFL/6PNoE8rX/SPcQDXBJ+uRepguP4QkPCm2LBxH3FA3y+fKSiJ+AmM',
      crossorigin: 'anonymous',
    },
  ],
};

module.exports = config;
