import java.io.IOException;
import java.util.ArrayList;

public class Auto {
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();
        ArrayList<Task> taskList = loadTasks(ui);

        boolean isRunning = true;
        while (isRunning) {
            String userInput = ui.readCommand();

            try {
                Command command = Command.fromInput(userInput);
                String argument = command.argumentIn(userInput);

                switch (command) {
                    case BYE -> {
                        ui.showGoodbye();
                        isRunning = false;
                    }
                    case LIST -> ui.showTaskList(taskList);
                    case MARK -> {
                        int taskIndex = parseTaskIndex(argument, taskList.size());
                        Task task = taskList.get(taskIndex);
                        boolean wasCompleted = task.isCompleted();
                        task.mark();
                        if (!saveTasks(taskList, ui)) {
                            if (!wasCompleted) {
                                task.unmark();
                            }
                            break;
                        }
                        ui.showTaskMarked(task);
                    }
                    case UNMARK -> {
                        int taskIndex = parseTaskIndex(argument, taskList.size());
                        Task task = taskList.get(taskIndex);
                        boolean wasCompleted = task.isCompleted();
                        task.unmark();
                        if (!saveTasks(taskList, ui)) {
                            if (wasCompleted) {
                                task.mark();
                            }
                            break;
                        }
                        ui.showTaskUnmarked(task);
                    }
                    case DELETE -> {
                        int taskIndex = parseTaskIndex(argument, taskList.size());
                        Task task = taskList.remove(taskIndex);
                        if (!saveTasks(taskList, ui)) {
                            taskList.add(taskIndex, task);
                            break;
                        }
                        ui.showTaskDeleted(task, taskList.size());
                    }
                    case TODO -> {
                        Task newTask = new ToDo(argument);
                        taskList.add(newTask);
                        if (!saveTasks(taskList, ui)) {
                            taskList.remove(taskList.size() - 1);
                            break;
                        }
                        ui.showTaskAdded(newTask, taskList.size());
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
                        if (!saveTasks(taskList, ui)) {
                            taskList.remove(taskList.size() - 1);
                            break;
                        }
                        ui.showTaskAdded(newTask, taskList.size());
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
                        if (!saveTasks(taskList, ui)) {
                            taskList.remove(taskList.size() - 1);
                            break;
                        }
                        ui.showTaskAdded(newTask, taskList.size());
                    }
                }
            } catch (AutoException e) {
                ui.showError(e.getMessage());
            }
        }

        ui.close();
    }

    /** Loads saved tasks, recovering cleanly when the file cannot be used. */
    private static ArrayList<Task> loadTasks(Ui ui) {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            Storage.LoadResult result = Storage.load();
            tasks.addAll(result.tasks());
            if (!result.warnings().isEmpty()) {
                ui.showStorageMessage(String.format(
                        "Warning: %d invalid data line(s) were skipped: %s.",
                        result.warnings().size(), String.join(", ", result.warnings())));
            }
        } catch (IOException | RuntimeException e) {
            ui.showStorageMessage(
                    "Sorry, I couldn't load your saved tasks. Starting with an empty task list.");
        }
        return tasks;
    }

    /** Saves a change and reports failure without exposing a stack trace. */
    private static boolean saveTasks(ArrayList<Task> tasks, Ui ui) {
        try {
            Storage.save(tasks);
            return true;
        } catch (IOException | RuntimeException e) {
            ui.showStorageMessage(
                    "Sorry, I couldn't save your tasks. Your latest change was not applied.");
            return false;
        }
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
