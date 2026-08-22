package auto.command;

import java.io.IOException;

import auto.exception.AutoException;
import auto.storage.Storage;
import auto.task.TaskList;
import auto.ui.Ui;

/** Represents an executable user command. */
public abstract class Command {
    /**
     * Executes this command against the current application collaborators.
     *
     * @param tasks task list to query or modify
     * @param ui console UI used to display the command result
     * @param storage storage used to persist task-list mutations
     * @throws AutoException if the command refers to invalid user input or state
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws AutoException;

    /**
     * Returns whether this command ends the application interaction loop.
     *
     * @return {@code true} if the application should exit, otherwise {@code false}
     */
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
