---
name: New Tool
about: Use this template's checklist for brand new tools
title: ''
labels: New Tool
assignees: ''

---

## Describe tool

... Blah blah blah blah...

## Checklist

New tools should be written on branches off `dev`. A pull request to the `dev` branch can then be submitted and a reviewer will review the code and accept the merge.

- [ ] Write tests
- [ ] Write tool (list changed files)
  - [ ] `objects.ToolDescriptions.java`
  - [ ] `scripts.MyTool.java`
  - [ ] `cli.MyToolCLI.java`
  - [ ] `window_interface.MyToolWindow.java`
  - [ ] `window_interface.MyToolOutput.java`
  - [ ] `main.ScriptManagerGUI.java`
  - [ ] `main.ScriptManager.java`
- [ ] Add logging support
- [ ] Update documentation (docusaurus)
- [ ] Write Galaxy wrapper

Then you can pull your changes into master! 🎉
