# Java to XMI Parser

## Introduction

## Setup
### Requirements
* Built on Oracle JDK 7
* Must run on Oracle JDK (not JRE or Open JDK) because it requires Sun's tools.jar library (see below for setup instructions). Dependency on tools.jar may limit execution JDK 7 only

### Tools.jar
* tools.jar is included in every Oracle JDK since JDK 6. However, the safest route for execution is to use JDK 7 because the APIs introduced in future tools.jar are not guaranteed to be backwards compatible with the JDK 7 tools.jar APIs (which this project is based on).

### Class Paths
* Set "JAVA_HOME" system variable to `C:\Program Files\Java\JDK_FOLDER`. For example: `C:\Program Files\Java\jdk1.7.0_21`.
* Add the following to your "Path" system variable to `%JAVA_HOME%\bin;%JAVA_HOME%\lib\tools.jar;`

## Execution
Run:

	java -classpath "PATH_TO_CLASS_FILES;PATH_TO_TOOLS.JAR" XMIGenerator FOLDER_NAME

Example:

    java -classpath "C:\Users\joshua\Development\XMI Generator - Java;C:\Program Files\Java\jdk1.7.0_21\lib\tools.jar" XMIGenerator java-project

## Notes
* This tool will attempt to compile the provided source code file(s), so if any critical compilation errors occur, the program will exit and display the errors. If any non-critical compilation errors occur, the program will continue and attempt to create an XMI file, but the resulting XMI file may not be 100% correct.
* Many non-critical compilation errors will occur if your software relies on external libraries for which only .class files or .jar files are available. The resulting XMI file will be correct, but will lack detailed information for classes contained within the external libraries.