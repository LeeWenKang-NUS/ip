import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Auto {
    public static void main(String[] args) {
        String banner = "    _         _        \n"
                + "   / \\  _   _| |_ ___  \n"
                + "  / _ \\| | | | __/ _ \\ \n"
                + " / ___ \\ |_| | || (_) |\n"
                + "/_/   \\_\\__,_|\\__\\___/ \n\n";
        String greeting = "=======================================================\n"
                + banner
                + "Hello! I'm Auto, your personal assistant.\n"
                + "What can I do for you?\n"
                + "=======================================================";
        System.out.println(greeting);

        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> taskList = loadTasks();

        boolean isRunning = true;
        while (isRunning) {
            String userInput = scanner.nextLine();

            try {
                Command command = Command.fromInput(userInput);
                String argument = command.argumentIn(userInput);

                switch (command) {
                    case BYE -> {
                        System.out.println("=======================================================");
                        System.out.println("Bye. Hope to see you again soon!");
                        System.out.println("=======================================================");
                        isRunning = false;
                    }
                    case LIST -> {
                        System.out.println("=======================================================");
                        System.out.println("Here are the tasks in your list:");
                        for (int i = 0; i < taskList.size(); i++) {
                            System.out.println(String.format("%d. %s", i + 1, taskList.get(i)));
                        }
                        System.out.println("=======================================================");
                    }
                    case MARK -> {
                        int taskIndex = parseTaskIndex(argument, taskList.size());
                        Task task = taskList.get(taskIndex);
                        boolean wasCompleted = task.isCompleted();
                        task.mark();
                        if (!saveTasks(taskList)) {
                            if (!wasCompleted) {
                                task.unmark();
                            }
                            break;
                        }
                        System.out.println("=======================================================");
                        System.out.println("Nice! I've marked this task as done");
                        System.out.println("  " + task);
                        System.out.println("=======================================================");
                    }
                    case UNMARK -> {
                        int taskIndex = parseTaskIndex(argument, taskList.size());
                        Task task = taskList.get(taskIndex);
                        boolean wasCompleted = task.isCompleted();
                        task.unmark();
                        if (!saveTasks(taskList)) {
                            if (wasCompleted) {
                                task.mark();
                            }
                            break;
                        }
                        System.out.println("=======================================================");
                        System.out.println("Nice! I've marked this task as not done yet");
                        System.out.println("  " + task);
                        System.out.println("=======================================================");
                    }
                    case DELETE -> {
                        int taskIndex = parseTaskIndex(argument, taskList.size());
                        Task task = taskList.remove(taskIndex);
                        if (!saveTasks(taskList)) {
                            taskList.add(taskIndex, task);
                            break;
                        }
                        System.out.println("=======================================================");
                        System.out.println("Roger! I've deleted this task:");
                        System.out.println("  " + task);
                        System.out.println(String.format("Now you have %d tasks in the list.", taskList.size()));
                        System.out.println("=======================================================");
                    }
                    case TODO -> {
                        Task newTask = new ToDo(argument);
                        taskList.add(newTask);
                        if (!saveTasks(taskList)) {
                            taskList.remove(taskList.size() - 1);
                            break;
                        }
                        System.out.println("=======================================================");
                        System.out.println("Got it. I've added this task:");
                        System.out.println("  " + newTask);
                        System.out.println(String.format("Now you have %d tasks in the list.", taskList.size()));
                        System.out.println("=======================================================");
                    }
                    case DEADLINE -> {
                        String[] parts = argument.split(" /by ", 2);
                        if (parts.length < 2) {
                            throw AutoException.deadlineNeedsBy();
                        }
                        String taskName = parts[0].trim();
                        String by = parts[1].trim();
                        Task newTask = new Deadline(taskName, by);
                        taskList.add(newTask);
                        if (!saveTasks(taskList)) {
                            taskList.remove(taskList.size() - 1);
                            break;
                        }
                        System.out.println("=======================================================");
                        System.out.println("Got it. I've added this task:");
                        System.out.println("  " + newTask);
                        System.out.println(String.format("Now you have %d tasks in the list.", taskList.size()));
                        System.out.println("=======================================================");
                    }
                    case EVENT -> {
                        String[] nameAndTime = argument.split(" /from ", 2);
                        if (nameAndTime.length < 2) {
                            throw AutoException.eventNeedsFromAndTo();
                        }
                        String[] fromAndTo = nameAndTime[1].split(" /to ", 2);
                        if (fromAndTo.length < 2) {
                            throw AutoException.eventNeedsFromAndTo();
                        }
                        String taskName = nameAndTime[0].trim();
                        String from = fromAndTo[0].trim();
                        String to = fromAndTo[1].trim();
                        Task newTask = new Event(taskName, from, to);
                        taskList.add(newTask);
                        if (!saveTasks(taskList)) {
                            taskList.remove(taskList.size() - 1);
                            break;
                        }
                        System.out.println("=======================================================");
                        System.out.println("Got it. I've added this task:");
                        System.out.println("  " + newTask);
                        System.out.println(String.format("Now you have %d tasks in the list.", taskList.size()));
                        System.out.println("=======================================================");
                    }
                }
            } catch (AutoException e) {
                System.out.println("=======================================================");
                System.out.println(e.getMessage());
                System.out.println("=======================================================");
            }
        }

        scanner.close();
    }

    /** Loads saved tasks, recovering cleanly when the file cannot be used. */
    private static ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            Storage.LoadResult result = Storage.load();
            tasks.addAll(result.tasks());
            if (!result.warnings().isEmpty()) {
                printStorageMessage(String.format(
                        "Warning: %d invalid data line(s) were skipped: %s.",
                        result.warnings().size(), String.join(", ", result.warnings())));
            }
        } catch (IOException | RuntimeException e) {
            printStorageMessage(
                    "Sorry, I couldn't load your saved tasks. Starting with an empty task list.");
        }
        return tasks;
    }

    /** Saves a change and reports failure without exposing a stack trace. */
    private static boolean saveTasks(ArrayList<Task> tasks) {
        try {
            Storage.save(tasks);
            return true;
        } catch (IOException | RuntimeException e) {
            printStorageMessage(
                    "Sorry, I couldn't save your tasks. Your latest change was not applied.");
            return false;
        }
    }

    private static void printStorageMessage(String message) {
        System.out.println("=======================================================");
        System.out.println(message);
        System.out.println("=======================================================");
    }

    private static int parseTaskIndex(String argument, int taskCount) throws AutoException {
        String trimmed = argument.trim();
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            throw AutoException.notATaskNumber(trimmed);
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw AutoException.noSuchTask(taskNumber);
        }
        return taskNumber - 1;
    }
}
