package auto.command;

import auto.storage.Storage;
import auto.task.TaskList;
import auto.ui.Ui;

/** Ends the current Auto session. */
public class ExitCommand extends Command {
    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExit() {
        return true;
    }
}
