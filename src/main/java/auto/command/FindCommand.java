package auto.command;

import auto.storage.Storage;
import auto.task.TaskList;

/** Displays tasks whose descriptions contain a requested keyword. */
public class FindCommand extends Command {
    private final String keyword;

    /** Creates a command that searches task descriptions for the specified keyword. */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public CommandResult execute(TaskList tasks, Storage storage) {
        return new CommandResult(formatTasks(
                "Here are the matching tasks in your list:",
                tasks.asList(),
                task -> task.matches(keyword)));
    }
}
