# Auto

Auto is a command-line task manager written in Java. It supports todos,
deadlines, events, persistent storage, and finding scheduled tasks by date.

## Prerequisites

- JDK 25
- Command Prompt on Windows for the quickstart command below

## Quickstart

From the project root, run:

```cmd
gradlew.bat shadowJar
java -jar build\libs\duke.jar
```

The first command builds an executable JAR containing the application and its
dependencies. The second command launches Auto directly, without displaying
Gradle's task-progress output while the application is running. Rebuild the JAR
after changing the source code.

## Commands

```text
todo DESCRIPTION
deadline DESCRIPTION /by dd/MM/yyyy
event DESCRIPTION /from dd/MM/yyyy /to dd/MM/yyyy
list
occur dd/MM/yyyy
mark TASK_NUMBER
unmark TASK_NUMBER
delete TASK_NUMBER
bye
```

For example:

```text
deadline submit report /by 22/08/2026
event conference /from 22/08/2026 /to 24/08/2026
occur 23/08/2026
```

Dates entered by the user use `dd/MM/yyyy`. Auto displays dates using
`MMM dd yyyy`, such as `Aug 22 2026`. An event's `/to` date cannot be before its
`/from` date.

## Storage

Auto loads and saves tasks automatically using `data/auto.txt`, relative to the
directory from which the application is run. Stored text fields are Base64
encoded so task descriptions containing separators can be recovered safely.

## Source structure

`src/main/java` remains the Java source root. Classes are organized under the
base `auto` package:

```text
auto
|-- Auto.java
|-- command
|-- exception
|-- parser
|-- storage
|-- task
|-- ui
`-- util
```

When using IntelliJ IDEA, configure the project to use JDK 25 and mark
`src/main/java` as the source root.
