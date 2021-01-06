---
id: quick-start
title: Getting Started
sidebar_label: Getting Started
slug: /
---

:::caution

Go through all the tools in this wiki and add caution/warning/tips. See doc1#Admonishions for Markdown formatting of these statements

:::

Welcome to the ScriptManager wiki!

ScriptManager is a tool written in Java for analyzing sequencing data and generating figures.

Both Graphical User Interface (GUI) and Command Line Interface (CLI) execution options are available.

## Dependencies

### Java

This tool is a Java-based tool and has been developed for Java versions 8 or higher.

Download [OpenJDK for Java 8](https://openjdk.java.net/install/)

or create a conda environment with Java (what Olivia does)




## Set-up: Download & Install (do this once)

Open your terminal and move to the directory where you want to install scriptmanager and type the following commands.

```bash
git clone https://github.com/CEGRcode/scriptmanager
cd scriptmanager
./gradlew build
```

The ScriptManager jar file will be created in the `build/libs` directory. As long as you have this file, you can move it wherever you want and do whatever you want with the rest of the scriptmanager files (even delete them). If you lose it, you can always remake it by running the Gradle build again.

## Testing

Execute the commands below after building the `jar` file to test that the file built correctly.
<!---Clean-up test file, write test script, and add to repo-->
<!---Add doc for executing during setup-->
<!---Check java version-->

```bash
cd travis_test
bash test_all.sh
```



## Running ScriptManager

### Usage-Graphical User Interface (GUI)

To run the GUI version of ScriptManager, execute the jar file without any arguments or flags:

```bash
java -jar /path/to/ScriptManager.jar
```

For example, from your `scriptmanager` directory on the 0.13 build, you would execute

```bash
java -jar build/libs/ScriptManager-0.13.jar
```


### Usage-Command Line Interface (CLI)

To run the [CLI version][cli] of ScriptManager, you must append the two subcommands corresponding to the tool you wish to run and any input arguments and options. The subcommand structure reflects the organization of the GUI tool.

```bash
java -jar /path/to/ScriptManager.jar <tool-group> <tool-name> <INPUTS> <OPTIONS>
```

The `tool-group` corresponds to one of the tabs in the GUI tool while the `tool-name` corresponds to the specific tool within the `tool-group` group. Each tool will have its own set of input requirements and options. You will have to rely on the `-h` flag for usage help or the documentation here for the input arguments.


## Getting Help

The help documentation is very useful so at any point in constructing your command you get stuck, use the `-h` flag to see what your options are and what you might be missing.

<!---Read through the tool guide (use `-h` flag if using CLI) to check for parameter restrictions-->
<!---Add FAQs page-->
* For Bugs: please open an issue on [Github][github-repo] with the following info
    * command you ran with a description of the input files. _Note: we may ask you for a copy of the input files later_
    * entire stack trace (error messages that followed execution of the command)
    * version of ScriptManager you're running (use the `-V` flag)
    * Java version and OS you ran the command on

## Additional Resources
* Detailed tool documentation
* File format specifications
* Javadoc




[github-repo]:https://github.com/CEGRcode/scriptmanager

[cli]:commandline.md
