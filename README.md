# Auto

Auto is a command-line task manager written in Java. It supports todos,
deadlines, events, persistent storage, and finding scheduled tasks by date.

## Prerequisites

- JDK 25
- Command Prompt on Windows for the quickstart command below

## Build and run the fat JAR

The Shadow plugin packages Auto and all its runtime dependencies into one
executable fat JAR. From the project root, create it with:

```cmd
gradlew.bat clean shadowJar
```

On macOS or Linux, use `./gradlew clean shadowJar` instead. `clean` removes old
build output, and `shadowJar` compiles the project and creates the executable
JAR.

The generated file is located at:

```text
build/libs/auto.jar
```

Run it from the project root with:

```cmd
java -jar build\libs\auto.jar
```

On macOS or Linux, use `java -jar build/libs/auto.jar`. Running the JAR directly
shows only Auto's console interface, without Gradle's task-progress output.
Rebuild the JAR after changing the source code.

Auto stores tasks in `data/auto.txt` relative to the directory from which the
JAR is run. Run it from the project root to use the repository's `data`
directory consistently.

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
