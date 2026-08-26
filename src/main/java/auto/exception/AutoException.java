package auto.exception;

/** Represents a user-facing error caused by an invalid command or argument. */
public class AutoException extends Exception {
    private static final String PREFIX = "Ohhh Noooo... ";

    private AutoException(String message) {
        super(PREFIX + message);
    }

    /** Returns an error for input that does not match a supported command. */
    public static AutoException unknownCommand() {
        return new AutoException("I don't understand you!");
    }

    /** Returns an error for a task number outside the current task list. */
    public static AutoException noSuchTask(int taskNumber) {
        return new AutoException(String.format("there is no task %d!", taskNumber));
    }

    /** Returns an error for an argument that cannot be parsed as a task number. */
    public static AutoException notATaskNumber(String argument) {
        return new AutoException(String.format("'%s' is not a task number!", argument));
    }

    /** Returns an error for a find command without a keyword. */
    public static AutoException findNeedsKeyword() {
        return new AutoException("a find command needs a keyword!");
    }

    /** Returns an error for a deadline without its required {@code /by} value. */
    public static AutoException deadlineNeedsBy() {
        return new AutoException("a deadline needs a /by date!");
    }

    /** Returns an error for an event without both required range values. */
    public static AutoException eventNeedsFromAndTo() {
        return new AutoException("an event needs a /from and a /to time!");
    }

    /** Returns an error for an event whose end date precedes its start date. */
    public static AutoException eventEndsBeforeItStarts() {
        return new AutoException("an event's /to date cannot be before its /from date!");
    }

    /** Returns an error for a date that fails format or calendar validation. */
    public static AutoException invalidDate(String value) {
        return new AutoException(String.format(
                "'%s' is not a valid date! Use dd/MM/yyyy.", value));
    }
}
