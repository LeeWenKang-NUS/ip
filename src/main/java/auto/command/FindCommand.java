package auto.command;

import auto.storage.Storage;
import auto.task.TaskList;
import auto.ui.Ui;

/** Displays tasks whose descriptions contain a requested keyword. */
public class FindCommand extends Command {
    private final String keyword;

    /** Creates a command that searches task descriptions for the specified keyword. */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.asList(), keyword);
    }
}
