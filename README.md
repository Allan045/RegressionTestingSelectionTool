# Introduction
This project contains the extended version of the Regression Test Selecion Tool. Used to automate the selecion of test classes for the regression test using the Firewall of classes.
In this version, it is possible to use smelly classes (classes with code smells) in the selection technique.

- The 'RegressionTestSelectionTool' folder has the Java project of the RTST.

- The 'Experiment' folder has the Java project used to evaluate the changes made in the tool.

- The 'Results' folder has all the result data obtained with the Experiment project. For more details, there is a README inside Results.

## Requirements

- PMD (most recent version)
- Java, version 16
- DependencyFinder, version 1.2.1-beta5.
- Linux Operacional System.

## Compile RTST

To compile the RTST, just to in the project folder and type

`mvn clean compile assembly:single`.

## Running the experiment

To run the experiment project, import the .jar file of the RTST in the Experiment Project. 
It's also needed to set the parameters in the TestSelector constructor, and choose the path for the projects and the result file.

