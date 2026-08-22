package auto.exception;

/** Represents a user-facing error caused by an invalid command or argument. */
public class AutoException extends Exception {
    private static final String PREFIX = "Ohhh Noooo... ";

    private AutoException(String message) {
        super(PREFIX + message);
    }

    /**
     * Creates an error for input that does not match a supported command.
     *
     * @return unknown-command error
     */
    public static AutoException unknownCommand() {
        return new AutoException("I don't understand you!");
    }

    /**
     * Creates an error for a task number outside the current task list.
     *
     * @param taskNumber invalid one-based task number
     * @return missing-task error containing the invalid number
     */
    public static AutoException noSuchTask(int taskNumber) {
        return new AutoException(String.format("there is no task %d!", taskNumber));
    }

    /**
     * Creates an error for an argument that cannot be parsed as a task number.
     *
     * @param argument invalid task-number argument
     * @return task-number format error containing the invalid argument
     */
    public static AutoException notATaskNumber(String argument) {
        return new AutoException(String.format("'%s' is not a task number!", argument));
    }

    /**
     * Creates an error for a deadline command without its required {@code /by} value.
     *
     * @return missing-deadline-date error
     */
    public static AutoException deadlineNeedsBy() {
        return new AutoException("a deadline needs a /by date!");
    }

    /**
     * Creates an error for an event without both required range values.
     *
     * @return missing-event-range error
     */
    public static AutoException eventNeedsFromAndTo() {
        return new AutoException("an event needs a /from and a /to time!");
    }

    /**
     * Creates an error for an event whose end date precedes its start date.
     *
     * @return invalid-event-range error
     */
    public static AutoException eventEndsBeforeItStarts() {
        return new AutoException("an event's /to date cannot be before its /from date!");
    }

    /**
     * Creates an error for a date that does not satisfy the required format or calendar rules.
     *
     * @param value invalid date value
     * @return invalid-date error containing the rejected value
     */
    public static AutoException invalidDate(String value) {
        return new AutoException(String.format(
                "'%s' is not a valid date! Use dd/MM/yyyy.", value));
    }
}
