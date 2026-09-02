package auto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests Auto's command responses, persistence integration, and startup recovery. */
class AutoTest {
    @TempDir
    Path tempDirectory;

    @Test
    void getResponse_multipleCommands_preservesStateAndPersistsTasks() throws Exception {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Auto auto = new Auto(dataFile.toString());

        String addResponse = auto.getResponse("todo read book");
        String markResponse = auto.getResponse("mark 1");
        String listResponse = auto.getResponse("list");

        assertTrue(addResponse.contains("Got it. I've added this task:"));
        assertTrue(markResponse.contains("Nice! I've marked this task as done"));
        assertTrue(listResponse.contains("1. [T][X] read book"));
        assertEquals(List.of("T | 1 | cmVhZCBib29r"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void getResponse_invalidCommand_returnsFriendlyError() {
        Auto auto = new Auto(tempDirectory.resolve("tasks.txt").toString());

        String response = auto.getResponse("borrow book");

        assertEquals("Ohhh Noooo... I don't understand you!", response);
    }

    @Test
    void getResponse_existingData_loadsTasksBeforeFirstCommand() throws Exception {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 1 | cmVhZCBib29r", StandardCharsets.UTF_8);

        Auto auto = new Auto(dataFile.toString());

        assertTrue(auto.getResponse("list").contains("1. [T][X] read book"));
    }

    @Test
    void getStartupMessage_partiallyCorruptedData_returnsWarningAndKeepsValidTasks()
            throws Exception {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | cmVhZCBib29r\nnot a task", StandardCharsets.UTF_8);
        Auto auto = new Auto(dataFile.toString());

        String startupMessage = auto.getStartupMessage();
        String response = auto.getResponse("list");

        assertTrue(startupMessage.contains("Warning: 1 invalid data line(s) were skipped"));
        assertEquals("Here are the tasks in your list:" + System.lineSeparator()
                + "1. [T][ ] read book", response);
    }

    @Test
    void getResponse_invalidDataPath_reportsLoadAndSaveFailures() {
        Auto auto = new Auto(tempDirectory.toString());

        String startupMessage = auto.getStartupMessage();
        String response = auto.getResponse("todo read book");

        assertTrue(startupMessage.contains(
                "Sorry, I couldn't load your saved tasks. Starting with an empty task list."));
        assertTrue(response.contains(
                "Sorry, I couldn't save your tasks. Your latest change was not applied."));
        assertEquals("Here are the tasks in your list:", auto.getResponse("list"));
    }
}
