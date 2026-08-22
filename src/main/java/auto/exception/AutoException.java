package auto.exception;

public class AutoException extends Exception {
    private static final String PREFIX = "Ohhh Noooo... ";

    private AutoException(String message) {
        super(PREFIX + message);
    }

    public static AutoException unknownCommand() {
        return new AutoException("I don't understand you!");
    }

    public static AutoException noSuchTask(int taskNumber) {
        return new AutoException(String.format("there is no task %d!", taskNumber));
    }

    public static AutoException notATaskNumber(String argument) {
        return new AutoException(String.format("'%s' is not a task number!", argument));
    }

    public static AutoException deadlineNeedsBy() {
        return new AutoException("a deadline needs a /by date!");
    }

    public static AutoException eventNeedsFromAndTo() {
        return new AutoException("an event needs a /from and a /to time!");
    }

    public static AutoException eventEndsBeforeItStarts() {
        return new AutoException("an event's /to date cannot be before its /from date!");
    }

    public static AutoException invalidDate(String value) {
        return new AutoException(String.format(
                "'%s' is not a valid date! Use dd/MM/yyyy.", value));
    }
}
