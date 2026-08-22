package auto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests Auto's application loop, persistence integration, and startup recovery. */
class AutoTest {
    private static final String DATA_FILE_PROPERTY = "auto.data.file";

    @TempDir
    Path tempDirectory;

    private InputStream originalIn;
    private PrintStream originalOut;
    private String originalDataFileProperty;
    private ByteArrayOutputStream output;

    @BeforeEach
    void setUpConsole() {
        originalIn = System.in;
        originalOut = System.out;
        originalDataFileProperty = System.getProperty(DATA_FILE_PROPERTY);
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreConsoleAndProperty() {
        System.setIn(originalIn);
        System.setOut(originalOut);
        if (originalDataFileProperty == null) {
            System.clearProperty(DATA_FILE_PROPERTY);
        } else {
            System.setProperty(DATA_FILE_PROPERTY, originalDataFileProperty);
        }
    }

    @Test
    void run_validCommandSequence_mutatesDisplaysAndPersistsTasks() throws Exception {
        Path dataFile = tempDirectory.resolve("tasks.txt");

        runAuto(dataFile, String.join("\n",
                "todo read book",
                "mark 1",
                "list",
                "bye",
                ""));

        assertEquals(List.of("T | 1 | cmVhZCBib29r"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
        assertOutputContains(
                "Hello! I'm Auto, your personal assistant.",
                "Got it. I've added this task:",
                "Nice! I've marked this task as done",
                "1. [T][X] read book",
                "Bye. Hope to see you again soon!");
    }

    @Test
    void run_unknownCommand_reportsErrorAndContinuesUntilBye() {
        runAuto(tempDirectory.resolve("tasks.txt"), "borrow book\nbye\n");

        assertOutputContains(
                "Ohhh Noooo... I don't understand you!",
                "Bye. Hope to see you again soon!");
    }

    @Test
    void run_partiallyCorruptedData_warnsAndRetainsValidTasks() throws Exception {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, String.join("\n",
                "T | 1 | cmVhZCBib29r",
                "not a task",
                "D | 0 | c3VibWl0IHJlcG9ydA== | QXVnIDIyIDIwMjY="),
                StandardCharsets.UTF_8);

        runAuto(dataFile, "list\nbye\n");

        assertOutputContains(
                "Warning: 1 invalid data line(s) were skipped: "
                        + "line 2 is invalid and was skipped.",
                "1. [T][X] read book",
                "2. [D][ ] submit report (by: Aug 22 2026)");
    }

    @Test
    void run_dataPathIsDirectory_recoversFromLoadAndRollsBackFailedSave() {
        runAuto(tempDirectory, "todo read book\nlist\nbye\n");

        assertOutputContains(
                "Sorry, I couldn't load your saved tasks. Starting with an empty task list.",
                "Sorry, I couldn't save your tasks. Your latest change was not applied.",
                "Here are the tasks in your list:",
                "Bye. Hope to see you again soon!");
        assertFalse(outputText().contains("1. [T][ ] read book"));
    }

    @Test
    void run_invalidPath_recoversFromRuntimeLoadFailureAndExitsCleanly() {
        runAuto("bye\n", "\0");

        assertOutputContains(
                "Sorry, I couldn't load your saved tasks. Starting with an empty task list.",
                "Bye. Hope to see you again soon!");
    }

    @Test
    void main_customDataFileProperty_usesConfiguredPath() throws Exception {
        Path dataFile = tempDirectory.resolve("custom/tasks.txt");
        System.setProperty(DATA_FILE_PROPERTY, dataFile.toString());
        System.setIn(inputOf("todo read book\nbye\n"));

        Auto.main(new String[0]);

        assertEquals(List.of("T | 0 | cmVhZCBib29r"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    private void runAuto(Path dataFile, String input) {
        runAuto(input, dataFile.toString());
    }

    private void runAuto(String input, String filePath) {
        System.setIn(inputOf(input));
        new Auto(filePath).run();
    }

    private ByteArrayInputStream inputOf(String input) {
        return new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    }

    private void assertOutputContains(String... expectedFragments) {
        String actual = outputText();
        for (String fragment : expectedFragments) {
            assertTrue(actual.contains(fragment), () -> "Missing output: " + fragment);
        }
    }

    private String outputText() {
        return output.toString(StandardCharsets.UTF_8);
    }
}
