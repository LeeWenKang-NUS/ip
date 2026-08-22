package auto.command;

import auto.storage.Storage;
import auto.task.Task;
import auto.task.TaskList;
import auto.ui.Ui;

/** Adds a parsed task and persists the updated list. */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the specified task.
     *
     * @param task task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.add(task);
        if (!saveTasks(tasks, ui, storage)) {
            tasks.removeLast();
            return;
        }
        ui.showTaskAdded(task, tasks.size());
    }
}
