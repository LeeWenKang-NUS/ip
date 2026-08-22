package auto.command;

import auto.exception.AutoException;
import auto.storage.Storage;
import auto.task.Task;
import auto.task.TaskList;
import auto.ui.Ui;

/** Marks a numbered task as incomplete and persists the change. */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that unmarks the task with the specified one-based number.
     *
     * @param taskNumber one-based number of the task to unmark
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws AutoException {
        Task task = tasks.get(taskNumber);
        boolean wasCompleted = task.isCompleted();
        tasks.unmark(taskNumber);
        if (!saveTasks(tasks, ui, storage)) {
            if (wasCompleted) {
                task.mark();
            }
            return;
        }
        ui.showTaskUnmarked(task);
    }
}
