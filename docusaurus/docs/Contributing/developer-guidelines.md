---
id: developer-guidelines
title: General Developer Notes
sidebar_label: General Developer Notes
---

Welcome to the ScriptManager developer wiki!

This guide is designed to generally orient developers that plan to contribute to ScriptManager and establish some project-specific standards. There are some helpful checklists at the end for current developers.

Quick Links:
- [Picard Documentation][picard-javadocs]
- [Picocli Documentation][picocli]
- [SDK Man Documentation][sdkman-docs]
- [Release Roadmap][release-roadmap]
- [Gradle Documentation][gradle]
- [Eclipse IDE][eclipse-ide]
- [Lambda functions][lambda-tutorial] - we want to be using these more

## ScriptManager Design Principles

There are several principles to keep in mind during development, especially for new developers.
- A single built ScripManager JAR file should run consistently across any OS with Java installed.
- Every tool should have a graphical interface and every tool not based on an existing command line tool should have a command line interface.
- The code for each tool should be isolated into a script object, a window object, and a command line object and organized into their appropriate packages in the `src/` library and use the local `util` and `objects` packages with [Picocoli][picocli], [HTSJDK][htsjdk], and [JFree][jfree] packages as appropriate.

While there are plenty of developer tools available for Java developers, this page is provides some recommendations based on Olivia's setup as a starting point for new developers.


## Java Development
We write exclusively in Java or Java-compiled languages without any operating-system specific packages (to maintain portability across machines).

### SDKMAN!
Olivia recommends installing Java using [SDKMAN!][sdkman] for convenient flipping between Java versions and releases. While ScriptManager is currently developed to the Java 8 SE standard, it is good practice to check for forward and backward compatibility between Java versions. We are constantly monitoring new Java releases and developing according to a standard that is consistent across Java versions makes our lives easier down the road when the Java version standard is incremented.

### Integrated Development Environment (IDE) - Eclipse
We recommend using [Eclipse][eclipse] to write Java code for ScriptManager because it supports both [Gradle][gradle] (see below) and [WindowBuilder][window-builder] for convenient building of JAR files and graphical interface development.

### Gradle-based build
Compiling Java classes and building JAR files could be handled manually, but for this project, we let Gradle juggle the process of compiling, retrieving dependencies, and building the final JAR file. The dependencies we use are a mix of downloaded JAR files (`scriptmanager/lib/*.jar` and dependencies retrieved directly from [Maven][maven]).

Gradle will need to be updated periodically to support *building* on later Java versions.

:::caution
Please note the minimum Java version required to support building the JAR executable. Code compiling with Gradle creates an upper Java version limit depending on the version of Gradle used. See this [table][gradle-version-compatibility] for more information on version compatibility.
:::

## The Code Structure (Packages)
```
scriptmanager/src/
  |--charts
  |--cli
  |--main
  |--objects
  |--scripts
  |--util
  |--window_interface
```

### window_interface
There exists at least one window object for every tool. They are organized by tool groups (`scripts` and `cli` are similarly organized). The main `<ToolName>Window.java` object extends the JFrame class and manages the inputs from the user. Many tools also have a `<ToolName>Output.java` object which pops up a new window when the tool executes to show the user updates on the progress of the tool, stats on the output, or, for the tools with visual outputs, to display the generated charts/images.

### main
The main package includes the main class that parses subcommands to call the various CLI classes or initialize the main GUI (JFrame) class.

### cli
The structure of these classes is generally very simple. See [__Picocli documentation__][picocli] and check existing CLI class examples for more info.

### scripts
It is important that these classes do not extend JFrame. Since they can used by cli classes, we need to make sure there are no screen-rendering elements that would throw errors if run from the command line. This set of classes isolate out the job of taking inputs and performing the computations.

### charts
These classes are for making chart objects that visually display the data. They can be displayed on the monitor or saved as image files by the various tool objects.

### objects
The most used class is the `ToolDescriptions.java` which statically stores all the tool descriptions as Strings for easy updates and changes to the descriptions. It also stores the ScriptManager version so make sure that this is incremented appropriately.

### util
These include classes with generic methods that are used across multiple tools.


## New Tool Checklist

New tools should be written on branches. A pull request to the master branch can then be submitted and a reviewer will review the code and accept the merge.

* Create __new issue ticket__ to associate commits with
  * [ ] spec out the tool input/output/parameters
  * [ ] decide on a tool group to add it to
* Write __tests__ for Github Actions (automatic testing)
  * [ ] Write data with small storage footprint
  * [ ] Capture a variety of edge cases (different parameter combinations, adding extra input files as needed))
  * [ ] Write tests into shell script for Github Actions
* `objects.ToolDescriptions.java`
  * [ ] Add tool description String (used by main window and CLI help docs)
* `window_interface.MyToolWindow.java`
  * [ ] Extends JFrame (see Java Swing documentation)
  * [ ] For particularly complex tool inputs, it may help to mock-up the window in Adobe Illustrator
* `window_interface.MyToolOutput.java` (Optional)
  * [ ] Some tools do not have an output frame but rather pop up a simple `JDialog` window indicating the operation has completed.
  * [ ] Tools that have bigger outputs, esp figures/images/chargs, should create an output frame
  * [ ] Extends JFrame
* `scripts.MyTool.java`
  * [ ] Make sure that the script object can be called and executed in a headless way (unit tests and CLI run)
  * [ ] Every tool should return a command line string for logging purposes.
* `cli.MyToolCLI.java`
  * [ ] __Skip if re-implementing existing command line tool for the GUI__
  * [ ] Use [Picocli][picocli] library to parse command line options
  * [ ] Create script object and call as appropriate
  * [ ] Return appropriate exit code
  * [ ] Import tool description from `ToolDescriptions` and add to appropriate help documentation fields
  * [ ] Test parameter constraints
* `main.ScriptManagerGUI.java`
  * [ ] Add collapsible panel to appropriate tool group in tool three
  * [ ] Import title, description, and other appropriate tool information
* `main.ScriptManager.java`
  * [ ] Create subcommand call for CLI (extend local abstract classs)
* Update Docusaurus (documentation)
  * [ ] add screenshots and descriptions of input
  * [ ] use warnings and note boxes as appropriate
  * [ ] add to tool index, tool-group, and file-formats pages
  * [ ] make sure page renders appropriately
* Write __Galaxy wrapper__
* Pull your changes into master! 🎉

:::tip
The easiest way to write a new tool is to copy-paste the code from a similarly-structured tool and edit!
:::


## Version Incrementing Checklist

The [Release Roadmap][release-roadmap] on Github organizes issue tickets and creates a projection of which issues should be addressed for each release. This helps when writing up the release notes and tagging all the appropriate issues as well as visually tracks what tasks are left to do in each release. When we are ready for a release, the following checklist should be followed to ensure that we update everything together without missing anything.


* Check [Release Roadmap][release-roadmap]
  * [ ] Make sure all issues are closed and pulled into master
  * [ ] Remove/archive column so next version is first to display
* Docusaurus updates
  * [ ] Make sure new tools have their own pages that thoroughly describe what they do
  * [ ] Affected tools have been updated accordingly (check commit log for list of tools)
  * [ ] Make sure `last updated` timestamps are appropriate/correct
  * [ ] Increment version across docs
* Testing
  * [ ] Ideally some degree of user testing on the development version has been performed (ask the bench scientists).
  * [ ] Make sure latest Github Actions build ran successfully
* `build.gradle`
  * [ ] Increment version (`version = ___`) and strip `dev` from JAR filename
* `src/objects/ToolDescriptions.java`
  * [ ] Increment ScriptManager version constant (used by CLI tools, propogation will happen automatically)
* Github version tag
  * [ ] Commit & pull request, review into master
  * [ ] Create version release & add version tag to the commit id
  * [ ] Compile JAR and save with source tar archive on release page
  * [ ] Write up summary for the version tag commit including links to resolved/relevant issue tickets
* Switch naming back to `dev`
  * [ ] `build.gradle` file should switch naming JAR to use `dev`

[eclipse]:https://www.eclipse.org/ide/
[eclipse-ide]:https://www.eclipse.org/eclipseide/
[gradle]:https://docs.gradle.org/current/userguide/userguide.html
[gradle-version-compatibility]:https://docs.gradle.org/current/userguide/compatibility.html
[htsjdk]:https://github.com/samtools/htsjdk
[jfree]:https://github.com/jfree/jfreechart
[maven]:https://maven.apache.org/
[picard-javadocs]:https://broadinstitute.github.io/picard/javadoc/picard/index.html
[picocli]:https://picocli.info/
[sdkman]:https://sdkman.io/install
[sdkman-docs]:https://sdkman.io/
[window-builder]:https://www.eclipse.org/windowbuilder/


[release-roadmap]:https://github.com/CEGRcode/scriptmanager/projects/6
[lambda-tutorial]:https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
