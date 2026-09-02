package auto.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import auto.task.Task;
import auto.util.DateUtil;

/** Handles all console input and output for Auto. */
public class Ui {
    private static final String DIVIDER = "=======================================================";
    private final Scanner scanner;
    private final Consumer<String> output;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
        output = System.out::println;
    }

    /**
     * Creates an output-only UI that sends each rendered message to the given sink.
     *
     * @param output Destination for rendered messages.
     */
    public Ui(Consumer<String> output) {
        scanner = null;
        this.output = output;
    }

    /** Displays the application banner and greeting. */
    public void showWelcome() {
        showMessage(
                "    _         _        ",
                "   / \\  _   _| |_ ___  ",
                "  / _ \\| | | | __/ _ \\ ",
                " / ___ \\ |_| | || (_) |",
                "/_/   \\_\\__,_|\\__\\___/ ",
                "",
                "Hello! I'm Auto, your personal assistant.",
                "What can I do for you?");
    }

    /** Reads the user's next command. */
    public String readCommand() {
        assert scanner != null : "An output-only UI cannot read commands";
        return scanner.nextLine();
    }

    /** Displays the farewell message shown when the user exits Auto. */
    public void showGoodbye() {
        showMessage("Bye. Hope to see you again soon!");
    }

    /** Displays all tasks in list order using one-based numbering. */
    public void showTaskList(List<Task> tasks) {
        StringBuilder message = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            message.append(System.lineSeparator())
                    .append(String.format("%d. %s", i + 1, tasks.get(i)));
        }
        showMessage(message.toString());
    }

    /** Displays scheduled tasks that occur on the requested date. */
    public void showTasksOccurringOn(List<Task> tasks, LocalDate date) {
        StringBuilder message = new StringBuilder(
                "Here are the tasks occurring on " + DateUtil.format(date) + ":");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.occursOn(date)) {
                message.append(System.lineSeparator())
                        .append(String.format("%d. %s", i + 1, task));
            }
        }
        showMessage(message.toString());
    }

    /** Displays tasks whose descriptions contain the keyword. */
    public void showMatchingTasks(List<Task> tasks, String keyword) {
        StringBuilder message = new StringBuilder("Here are the matching tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.matches(keyword)) {
                message.append(System.lineSeparator())
                        .append(String.format("%d. %s", i + 1, task));
            }
        }
        showMessage(message.toString());
    }

    /** Displays confirmation that the specified task was marked as complete. */
    public void showTaskMarked(Task task) {
        showTask("Nice! I've marked this task as done", task);
    }

    /** Displays confirmation that the specified task was marked as incomplete. */
    public void showTaskUnmarked(Task task) {
        showTask("Nice! I've marked this task as not done yet", task);
    }

    /** Displays the deleted task and the number of tasks that remain. */
    public void showTaskDeleted(Task task, int taskCount) {
        showMessage(
                "Roger! I've deleted this task:",
                "  " + task,
                String.format("Now you have %d tasks in the list.", taskCount));
    }

    /** Displays the added task and the updated number of tasks. */
    public void showTaskAdded(Task task, int taskCount) {
        showMessage(
                "Got it. I've added this task:",
                "  " + task,
                String.format("Now you have %d tasks in the list.", taskCount));
    }

    /** Displays a user-facing command or validation error. */
    public void showError(String message) {
        showMessage(message);
    }

    /** Displays a user-facing storage warning or error. */
    public void showStorageMessage(String message) {
        showMessage(message);
    }

    /** Releases the console input resource when the application exits. */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }

    private void showTask(String message, Task task) {
        showMessage(message, "  " + task);
    }

    private void showMessage(String... lines) {
        StringBuilder message = new StringBuilder(DIVIDER);
        for (String line : lines) {
            message.append(System.lineSeparator()).append(line);
        }
        message.append(System.lineSeparator()).append(DIVIDER);
        output.accept(message.toString());
    }
}
