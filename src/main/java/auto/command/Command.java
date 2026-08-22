package auto.command;

import java.io.IOException;

import auto.exception.AutoException;
import auto.storage.Storage;
import auto.task.TaskList;
import auto.ui.Ui;

/** Represents an executable user command. */
public abstract class Command {
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws AutoException;

    public boolean isExit() {
        return false;
    }

    /** Saves a mutation and presents storage failures consistently. */
    protected boolean saveTasks(TaskList tasks, Ui ui, Storage storage) {
        try {
            storage.save(tasks.asList());
            return true;
        } catch (IOException | RuntimeException e) {
            ui.showStorageMessage(
                    "Sorry, I couldn't save your tasks. Your latest change was not applied.");
            return false;
        }
    }
}
