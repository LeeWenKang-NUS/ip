package auto;

import java.io.IOException;

import auto.command.Command;
import auto.exception.AutoException;
import auto.parser.Parser;
import auto.storage.Storage;
import auto.task.TaskList;
import auto.ui.Ui;

public class Auto {
    private static final String DEFAULT_DATA_FILE = "data/auto.txt";
    private final Storage storage;
    private final Ui ui;
    private TaskList tasks;

    public Auto(String filePath) {
        storage = new Storage(filePath);
        ui = new Ui();
        tasks = new TaskList();
    }

    public static void main(String[] args) {
        String filePath = System.getProperty("auto.data.file", DEFAULT_DATA_FILE);
        new Auto(filePath).run();
    }

    /** Starts the interaction loop and releases UI resources on exit. */
    public void run() {
        ui.showWelcome();
        tasks = loadTasks();

        try {
            boolean isRunning = true;
            while (isRunning) {
                String userInput = ui.readCommand();

                try {
                    Command command = Parser.parse(userInput);
                    command.execute(tasks, ui, storage);
                    isRunning = !command.isExit();
                } catch (AutoException e) {
                    ui.showError(e.getMessage());
                }
            }
        } finally {
            ui.close();
        }
    }

    /** Loads saved tasks, recovering cleanly when the file cannot be used. */
    private TaskList loadTasks() {
        try {
            Storage.LoadResult result = storage.load();
            if (!result.warnings().isEmpty()) {
                ui.showStorageMessage(String.format(
                        "Warning: %d invalid data line(s) were skipped: %s.",
                        result.warnings().size(), String.join(", ", result.warnings())));
            }
            return new TaskList(result.tasks());
        } catch (IOException | RuntimeException e) {
            ui.showStorageMessage(
                    "Sorry, I couldn't load your saved tasks. Starting with an empty task list.");
            return new TaskList();
        }
    }

}
