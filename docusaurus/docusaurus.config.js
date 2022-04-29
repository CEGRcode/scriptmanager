/** @type {import('@docusaurus/types').DocusaurusConfig} */
module.exports = {
  title: 'ScriptManager',
  tagline: 'Toolbox for analyzing your genomic datasets',
  url: 'https://github.com/CEGRcode',
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
        alt: 'ScriptManager',
        src: 'img/ScriptManagerTempLogo_AgencyFBfont.png', //svg file was here
      },
      items: [
        {
          type: 'doc',
          docId: 'quick-start',
          label: 'Docs',
          position: 'left',
        },
        {
          type: 'doc',
          docId: 'read-analysis/tag-pileup',
          label: 'Tools',
          position: 'left'
        },
        { 
          href: 'https://github.com/CEGRcode/scriptmanager',
          label: 'GitHub',
          position: 'right',
        },
      ],
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
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      },
    ],
  ],
};
