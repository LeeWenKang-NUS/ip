package auto.command;

import auto.storage.Storage;
import auto.task.TaskList;

/** Ends the current Auto session. */
public class ExitCommand extends Command {
    /** {@inheritDoc} */
    @Override
    public CommandResult execute(TaskList tasks, Storage storage) {
        return new CommandResult("Bye. Hope to see you again soon!");
    }
}
