package auto.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import auto.task.Deadline;
import auto.task.Event;
import auto.task.Task;
import auto.task.ToDo;

/** Tests console input and output formatting provided by {@link Ui}. */
class UiTest {
    private static final String DIVIDER = "=======================================================";

    private InputStream originalIn;
    private PrintStream originalOut;
    private ByteArrayOutputStream output;

    @BeforeEach
    void setUp() {
        originalIn = System.in;
        originalOut = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    void readCommand_multipleLines_returnsLinesInOrder() {
        System.setIn(inputOf("list\nbye\n"));
        Ui ui = new Ui();

        assertEquals("list", ui.readCommand());
        assertEquals("bye", ui.readCommand());
    }

    @Test
    void showWelcome_displaysBannerAndGreeting() {
        Ui ui = new Ui();

        ui.showWelcome();

        assertOutput(
                DIVIDER,
                "    _         _        ",
                "   / \\  _   _| |_ ___  ",
                "  / _ \\| | | | __/ _ \\ ",
                " / ___ \\ |_| | || (_) |",
                "/_/   \\_\\__,_|\\__\\___/ ",
                "",
                "Hello! I'm Auto, your personal assistant.",
                "What can I do for you?",
                DIVIDER);
    }

    @Test
    void showTaskList_emptyList_displaysHeaderWithoutEntries() {
        Ui ui = new Ui();

        ui.showTaskList(List.of());

        assertOutput(DIVIDER, "Here are the tasks in your list:", DIVIDER);
    }

    @Test
    void showTaskList_multipleTasks_displaysOneBasedNumberedTasks() {
        Ui ui = new Ui();
        Task todo = new ToDo("read book");
        Task deadline = new Deadline("submit report", LocalDate.of(2026, 8, 22));
        deadline.mark();

        ui.showTaskList(List.of(todo, deadline));

        assertOutput(
                DIVIDER,
                "Here are the tasks in your list:",
                "1. [T][ ] read book",
                "2. [D][X] submit report (by: Aug 22 2026)",
                DIVIDER);
    }

    @Test
    void showTasksOccurringOn_mixedTasks_displaysMatchesWithOriginalNumbers() {
        Ui ui = new Ui();
        LocalDate requestedDate = LocalDate.of(2026, 8, 22);
        List<Task> tasks = List.of(
                new ToDo("prepare notes"),
                new Deadline("submit report", requestedDate),
                new Event("conference", requestedDate.minusDays(1), requestedDate.plusDays(1)),
                new Deadline("return book", requestedDate.plusDays(1)));

        ui.showTasksOccurringOn(tasks, requestedDate);

        assertOutput(
                DIVIDER,
                "Here are the tasks occurring on Aug 22 2026:",
                "2. [D][ ] submit report (by: Aug 22 2026)",
                "3. [E][ ] conference (from: Aug 21 2026 to: Aug 23 2026)",
                DIVIDER);
    }

    @Test
    void showTasksOccurringOn_noMatches_displaysHeaderWithoutEntries() {
        Ui ui = new Ui();
        LocalDate requestedDate = LocalDate.of(2026, 8, 22);

        ui.showTasksOccurringOn(
                List.of(new Deadline("submit report", requestedDate.plusDays(1))),
                requestedDate);

        assertOutput(DIVIDER, "Here are the tasks occurring on Aug 22 2026:", DIVIDER);
    }

    @Test
    void showTaskAdded_displaysTaskAndUpdatedCount() {
        Ui ui = new Ui();

        ui.showTaskAdded(new ToDo("read book"), 2);

        assertOutput(
                DIVIDER,
                "Got it. I've added this task:",
                "  [T][ ] read book",
                "Now you have 2 tasks in the list.",
                DIVIDER);
    }

    @Test
    void showTaskDeleted_displaysTaskAndZeroCount() {
        Ui ui = new Ui();

        ui.showTaskDeleted(new ToDo("read book"), 0);

        assertOutput(
                DIVIDER,
                "Roger! I've deleted this task:",
                "  [T][ ] read book",
                "Now you have 0 tasks in the list.",
                DIVIDER);
    }

    @Test
    void showTaskMarked_markedTask_displaysCompletedTask() {
        Ui ui = new Ui();
        Task task = new ToDo("read book");
        task.mark();

        ui.showTaskMarked(task);

        assertOutput(DIVIDER, "Nice! I've marked this task as done", "  [T][X] read book", DIVIDER);
    }

    @Test
    void showTaskUnmarked_unmarkedTask_displaysIncompleteTask() {
        Ui ui = new Ui();

        ui.showTaskUnmarked(new ToDo("read book"));

        assertOutput(
                DIVIDER,
                "Nice! I've marked this task as not done yet",
                "  [T][ ] read book",
                DIVIDER);
    }

    @Test
    void showGoodbye_displaysFarewellMessage() {
        Ui ui = new Ui();

        ui.showGoodbye();

        assertOutput(DIVIDER, "Bye. Hope to see you again soon!", DIVIDER);
    }

    @Test
    void showError_displaysProvidedMessage() {
        Ui ui = new Ui();

        ui.showError("Invalid command");

        assertOutput(DIVIDER, "Invalid command", DIVIDER);
    }

    @Test
    void showStorageMessage_displaysProvidedMessage() {
        Ui ui = new Ui();

        ui.showStorageMessage("Could not load tasks");

        assertOutput(DIVIDER, "Could not load tasks", DIVIDER);
    }

    /** Creates deterministic console input using the same encoding as the application tests. */
    private ByteArrayInputStream inputOf(String input) {
        return new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    }

    /** Verifies complete output after normalizing platform-specific line separators. */
    private void assertOutput(String... lines) {
        String expected = String.join("\n", lines) + "\n";
        String actual = output.toString(StandardCharsets.UTF_8)
                .replace("\r\n", "\n")
                .replace('\r', '\n');
        assertEquals(expected, actual);
    }
}
