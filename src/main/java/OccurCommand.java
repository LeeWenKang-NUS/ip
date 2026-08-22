import java.time.LocalDate;

/** Displays deadlines and events occurring on a requested date. */
public class OccurCommand extends Command {
    private final LocalDate date;

    public OccurCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOccurringOn(tasks.asList(), date);
    }
}
