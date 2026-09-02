package auto.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import auto.exception.AutoException;
import auto.storage.Storage;
import auto.task.Task;
import auto.task.TaskList;
import auto.task.ToDo;

class DeleteCommandTest extends CommandTestSupport {
    @TempDir
    Path tempDirectory;

    @Test
    void execute_validNumber_deletesPersistsAndAcknowledgesTask() throws Exception {
        Task first = new ToDo("read book");
        Task second = new ToDo("write essay");
        TaskList tasks = new TaskList(List.of(first, second));
        Storage storage = writableStorage(tempDirectory);

        CommandResult result = new DeleteCommand(1).execute(tasks, storage);

        assertEquals(1, tasks.size());
        assertSame(second, tasks.get(1));
        assertEquals("[T][ ] write essay", storage.load().tasks().getFirst().toString());
        assertTrue(result.message().contains("Roger! I've deleted this task:"));
        assertTrue(result.message().contains("Now you have 1 tasks in the list."));
    }

    @Test
    void execute_saveFails_restoresTaskAtOriginalPosition() throws Exception {
        Task first = new ToDo("read book");
        Task second = new ToDo("write essay");
        TaskList tasks = new TaskList(List.of(first, second));

        CommandResult result = new DeleteCommand(1).execute(tasks, failingStorage(tempDirectory));

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(1));
        assertSame(second, tasks.get(2));
        assertTrue(result.message().contains("Sorry, I couldn't save your tasks."));
    }

    @Test
    void execute_invalidNumber_throwsWithoutChangingTasks() {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList(List.of(task));
        DeleteCommand command = new DeleteCommand(0);

        AutoException exception = assertThrows(AutoException.class,
                () -> command.execute(tasks, writableStorage(tempDirectory)));

        assertEquals("Ohhh Noooo... there is no task 0!", exception.getMessage());
        assertEquals(1, tasks.size());
        assertSame(task, tasks.asList().getFirst());
    }
}
