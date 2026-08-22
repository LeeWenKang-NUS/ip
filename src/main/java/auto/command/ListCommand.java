package auto.command;

import auto.storage.Storage;
import auto.task.TaskList;
import auto.ui.Ui;

/** Displays every task in the list. */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.asList());
    }
}
