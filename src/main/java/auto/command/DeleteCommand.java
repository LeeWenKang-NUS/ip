package auto.command;

import auto.exception.AutoException;
import auto.storage.Storage;
import auto.task.Task;
import auto.task.TaskList;
import auto.ui.Ui;

/** Deletes a numbered task and persists the updated list. */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that deletes the task with the specified one-based number.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws AutoException {
        Task task = tasks.delete(taskNumber);
        if (!saveTasks(tasks, ui, storage)) {
            tasks.restoreDeleted(taskNumber, task);
            return;
        }
        ui.showTaskDeleted(task, tasks.size());
    }
}
