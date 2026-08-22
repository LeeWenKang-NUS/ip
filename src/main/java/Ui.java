import java.util.List;
import java.util.Scanner;

/** Handles all console input and output for Auto. */
public class Ui {
    private static final String DIVIDER = "=======================================================";
    private final Scanner scanner;

    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays the application banner and greeting. */
    public void showWelcome() {
        String banner = "    _         _        \n"
                + "   / \\  _   _| |_ ___  \n"
                + "  / _ \\| | | | __/ _ \\ \n"
                + " / ___ \\ |_| | || (_) |\n"
                + "/_/   \\_\\__,_|\\__\\___/ \n\n";
        System.out.println(DIVIDER + "\n"
                + banner
                + "Hello! I'm Auto, your personal assistant.\n"
                + "What can I do for you?\n"
                + DIVIDER);
    }

    /** Reads the user's next command. */
    public String readCommand() {
        return scanner.nextLine();
    }

    public void showGoodbye() {
        showMessage("Bye. Hope to see you again soon!");
    }

    public void showTaskList(List<Task> tasks) {
        System.out.println(DIVIDER);
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(String.format("%d. %s", i + 1, tasks.get(i)));
        }
        System.out.println(DIVIDER);
    }

    public void showTaskMarked(Task task) {
        showTask("Nice! I've marked this task as done", task);
    }

    public void showTaskUnmarked(Task task) {
        showTask("Nice! I've marked this task as not done yet", task);
    }

    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(DIVIDER);
        System.out.println("Roger! I've deleted this task:");
        System.out.println("  " + task);
        System.out.println(String.format("Now you have %d tasks in the list.", taskCount));
        System.out.println(DIVIDER);
    }

    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(DIVIDER);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println(String.format("Now you have %d tasks in the list.", taskCount));
        System.out.println(DIVIDER);
    }

    public void showError(String message) {
        showMessage(message);
    }

    public void showStorageMessage(String message) {
        showMessage(message);
    }

    /** Releases the console input resource when the application exits. */
    public void close() {
        scanner.close();
    }

    private void showTask(String message, Task task) {
        System.out.println(DIVIDER);
        System.out.println(message);
        System.out.println("  " + task);
        System.out.println(DIVIDER);
    }

    private void showMessage(String message) {
        System.out.println(DIVIDER);
        System.out.println(message);
        System.out.println(DIVIDER);
    }
}
