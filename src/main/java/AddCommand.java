/** Adds a parsed task and persists the updated list. */
public class AddCommand extends Command {
    private final Task task;

    public AddCommand(Task task) {
        this.task = task;
    }

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
