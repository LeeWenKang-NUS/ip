package auto.command;

import auto.exception.AutoException;
import auto.storage.Storage;
import auto.task.Task;
import auto.task.TaskList;
import auto.ui.Ui;

/** Marks a numbered task as complete and persists the change. */
public class MarkCommand extends Command {
    private final int taskNumber;

    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws AutoException {
        Task task = tasks.get(taskNumber);
        boolean wasCompleted = task.isCompleted();
        tasks.mark(taskNumber);
        if (!saveTasks(tasks, ui, storage)) {
            if (!wasCompleted) {
                task.unmark();
            }
            return;
        }
        ui.showTaskMarked(task);
    }
}
