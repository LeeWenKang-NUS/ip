package auto.command;

import auto.storage.Storage;
import auto.task.TaskList;
import auto.ui.Ui;

/** Ends the current Auto session. */
public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
