/** Deletes a numbered task and persists the updated list. */
public class DeleteCommand extends Command {
    private final int taskNumber;

    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

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
