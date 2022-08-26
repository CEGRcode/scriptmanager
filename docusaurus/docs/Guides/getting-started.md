---
id: getting-started
title: Getting Started
sidebar_label: Getting Started
slug: /
---

Welcome to the ScriptManager wiki!

ScriptManager is a tool written in Java for analyzing sequencing data and generating figures.

Both Graphical User Interface (GUI) and Command Line Interface (CLI) execution options are available.

## Dependencies

### Java

This tool is a Java-based tool and has been developed to run using Java versions 8 or higher. Set up Java on your machine in one of many ways...

* **Option A:** Download [OpenJDK for Java 11][temurin-11] (recommend for novice users)

* **Option B:** If you have Anaconda set up on a Unix/MacOS system, create a [conda][conda-openjdk] environment with Java using the following command:
```
conda install -n my-env -c conda-forge openjdk
```

Feel free to use whatever Java installation method you are most comfortable with. Most machines already have Java installed! **So check if you've got it first!**

:::note
**For Developers:** If you are writing code for scriptmanager and need to flip between Java versions to perform testing across different versions and releases, consider using  [SDKMAN!][sdk-notes] (what Olivia does).
:::

## Set-up: Download Executable and Start
There are two ways to obtain the ScriptManager executable JAR file.

### Download pre-compiled binary (easy)
We pre-compiled the JAR binary files to run ScriptManager (same for all operating systems) and made them available for direct download [on Github][github-releases].

Click [here][direct-download-jar] for the latest JAR (v0.13).

### Clone the Github repository (for latest dev version)
Open your terminal and move to the directory where you want to install scriptmanager and type the following command to download all the source code so you can build the executable JAR file from scratch.
```bash
git clone https://github.com/CEGRcode/scriptmanager
```

Then you need to build the executable JAR file with the following two commands.
```
cd scriptmanager
./gradlew build
```

The ScriptManager jar file will be created in the `build/libs` directory. As long as you have this file, you can move it wherever you want and do whatever you want with the rest of the ScriptManager files (even delete them). If you ever lose the JAR file, you can regenerate it by rerunning the Gradle build command in while in the `scriptmanager` directory.

:::caution
Please note that the latest **Java version** may not be compatible our supported Gradle version to compile. The JAR can be *executed* on most versions but compiling the code may require installing an older version of Java. Consider directly downloading the JAR executable (instructions above) if these steps aren't working.
:::

### Update the Github repository (update to latest dev version)
If you ever need to get the latest code from the Github repo, just navigate to the `scriptmanager` directory and run the following commands in the terminal to update and then re-build your JAR executable.
```bash
cd scriptmanager
git pull
./gradlew build
```

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

## Citing Us
If you use ScriptManager in your work, you can use the [Lang et al (2022)][pearc-paper] publication or use ScriptManager's unique [RRID:**SCR_021797**][rrid-link].

:::tip
We welcome anyone who uses ScriptManager for analysis in their publications to let us know by sending an email to owl8@cornell.edu so we can list it in our [showcase of publications][publications-list]!
:::

[temurin-11]:https://adoptium.net/temurin/releases?version=11
[conda-openjdk]:https://anaconda.org/conda-forge/openjdk
[github-releases]:https://github.com/CEGRcode/scriptmanager/releases
[direct-download-jar]:https://github.com/CEGRcode/scriptmanager/releases/download/v0.13/ScriptManager-v0.13.jar
[pearc-paper]:https://dl.acm.org/doi/abs/10.1145/3491418.3535161
[rrid-link]:https://scicrunch.org/resources/data/record/nlx_144509-1/SCR_021797/resolver?q=SCR_021797%2A&l=SCR_021797%2A&i=rrid:scr_021797

[github-repo]:https://github.com/CEGRcode/scriptmanager
[sdk-notes]:/docs/Contributing/developer-guidelines#sdkman
[cli]:/docs/Guides/command-line
[publications-list]:/docs/References/publications
