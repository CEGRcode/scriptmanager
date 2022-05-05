---
id: general-guidelines
title: General Developer Notes
sidebar_label: General Developer Notes
---

## ScriptManager Design Principles

There are several principles to keep in mind during development, especially for new developers.
- A single built ScripManager JAR file should run consistently across any OS with Java installed.
- Every tool should have a graphical interface and every tool not based on an existing command line tool should have a command line interface.
- The code for each tool should be isolated into a script object, a window object, and a command line object and organized into their appropriate packages in the `src/` library and use the local `util` and `objects` packages with [Picocoli][picocli], [HTSJDK][htsjdk], and [JFree][jfree] packages as appropriate.

While there are plenty of developer tools available for Java developers, this page is provides some recommendations based on Olivia's setup as a starting point for new developers.

### Java version (SDK Man)
Olivia recommends installing Java using [SDKMan][sdkman] for convenient flipping between Java versions. While ScriptManager is currently developed to the Java 8 SE standard, it is good practice to check for forward and backward compatibility between Java versions. We are constantly monitoring new Java releases and by developing according to a standard that is consistent across Java versions makes our lives easier down the road when the Java version standard is incremented.

### Integrated Development Environment (IDE) - Eclipse
Olivia recommends using [Eclipse][eclipse] to write Java code for ScriptManager because it supports both [Gradle][gradle] and [WindowBuilder][window-builder] for convenient building of JAR files and graphical interface development.


### Resources

Optimization of code is very important and there are a lot of features in the newer versions of Java that we can take advantage of to improve the performance of our tools.

- [Lambda functions][lambda-tutorial]


### Github guidelines

#### New Issue Tickets


### New Tool Checklist
New tools should be written on branches. A pull request to the master branch can then be submitted and a reviewer will review the code and accept the merge.

* Write tests for Github Actions
  * [ ] Write data with small storage footprint
  * [ ] Capture a variety of edge cases
  * [ ] Write tests into shell script for Github Actions
* `objects.ToolDescriptions.java`
  * [ ] Add tool description String (used by main window and CLI help docs)
* `window_interface.MyToolWindow.java`
  * [ ] Extends JFrame (see Java Swing documentation)
* `window_interface.MyToolOutput.java` (Optional)
  * [ ] Some tools do not have an output frame but rather pop up a simple `JDialog` window.
  * [ ] Tools that have bigger outputs, esp figures/images/chargs, should create an output frame
  * [ ] Extends JFrame
* `scripts.MyTool.java`
  * [ ] Make sure that the script object can be called and executed in a headless way (unit tests and CLI run)
  * [ ] Every tool should return a command line string for logging purposes.
* `cli.MyToolCLI.java`
  - Use [Picocli][picocli] library to parse command line options
  * [ ] Create script object and call as appropriate
  * [ ] Return appropriate exit code
  * [ ] Import tool description from `ToolDescriptions` and add to appropriate help documentation fields
* `main.ScriptManagerGUI.java`
  * [ ] Add collapsible panel to appropriate tool group in tool three
  * [ ] Import title, description, and other appropriate tool information
* `main.ScriptManager.java`
  * [ ] Create subcommand call for CLI (extend local abstract classs)



### Version Incrementing Checklist

The [Release Roadmap][release-roadmap] on Github organizes issue tickets and creates a projection of which issues should be addressed for each release. This helps when writing up the release notes and tagging all the appropriate issues as well as visually tracks what tasks are left to do in each release. When we are ready for a release, the following checklist should be followed to ensure that we update everything together without missing anything.


* Check Release Roadmap
  * [ ] Make sure all issues are closed and pulled into master
  * [ ] Remove/archive column so next version is first to display
* Docusaurus updates
  * [ ] Make sure new tools have their own pages that thoroughly describe what they do
  * [ ] Affected tools have been updated accordingly (check commit log for list of tools)
  * [ ] Make sure `last updated` timestamps are appropriate/correct
* Testing
  * [ ] Ideally some degree of user testing on the development version has been performed (ask the bench scientists).
  * [ ] Make sure latest Github Actions build ran successfully
* `build.gradle`
  * [ ] Increment version (`version = ___`) and strip `dev` from JAR filename
* `src/objects/ToolDescriptions.java`
  * [ ] Increment ScriptManager version constant (used by CLI tools, propogation will happen automatically)
* Github version tag
  * [ ] Commit & pull request, review into master
  * [ ] Add version tag to the commit id
  * [ ] Compile JAR and save with source tar archive on release page
  * [ ] Write up summary for the version tag commit including links to resolved/relevant issue tickets









[eclipse]:https://www.eclipse.org/ide/
[gradle]:https://docs.gradle.org/current/userguide/userguide.html
[htsjdk]:https://github.com/samtools/htsjdk
[jfree]:https://github.com/jfree/jfreechart
[picocli]:https://picocli.info/
[sdkman]:https://sdkman.io/install
[window-builder]:https://www.eclipse.org/windowbuilder/


[release-roadmap]:https://github.com/CEGRcode/scriptmanager/projects/6
[lambda-tutorial]:https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
