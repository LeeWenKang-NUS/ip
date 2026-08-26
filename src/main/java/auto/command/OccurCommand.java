package auto.command;

import java.time.LocalDate;

import auto.storage.Storage;
import auto.task.TaskList;
import auto.ui.Ui;

/** Displays deadlines and events occurring on a requested date. */
public class OccurCommand extends Command {
    private final LocalDate date;

    /** Creates a command that displays tasks occurring on the specified date. */
    public OccurCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOccurringOn(tasks.asList(), date);
    }
}
