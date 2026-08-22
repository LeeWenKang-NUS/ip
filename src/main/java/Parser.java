/** Converts raw user input and command arguments into domain values. */
public class Parser {

    /** Holds a recognized command and its verbatim argument. */
    public record ParsedCommand(Command command, String argument) {
    }

    /** Recognizes a command and extracts its argument from one input line. */
    public static ParsedCommand parse(String input) throws AutoException {
        Command command = Command.fromInput(input);
        return new ParsedCommand(command, command.argumentIn(input));
    }

    /** Parses a user-facing task number. */
    public static int parseTaskNumber(String argument) throws AutoException {
        String trimmed = argument.trim();
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            throw AutoException.notATaskNumber(trimmed);
        }
    }

    /** Creates a todo while preserving its description exactly as entered. */
    public static ToDo parseToDo(String argument) {
        return new ToDo(argument);
    }

    /** Parses a deadline description and its required {@code /by} value. */
    public static Deadline parseDeadline(String argument) throws AutoException {
        String[] parts = argument.split(" /by ", 2);
        if (parts.length < 2) {
            throw AutoException.deadlineNeedsBy();
        }
        return new Deadline(parts[0].trim(), parts[1].trim());
    }

    /** Parses an event description and its required start and end values. */
    public static Event parseEvent(String argument) throws AutoException {
        String[] nameAndTime = argument.split(" /from ", 2);
        if (nameAndTime.length < 2) {
            throw AutoException.eventNeedsFromAndTo();
        }
        String[] fromAndTo = nameAndTime[1].split(" /to ", 2);
        if (fromAndTo.length < 2) {
            throw AutoException.eventNeedsFromAndTo();
        }
        return new Event(nameAndTime[0].trim(),
                fromAndTo[0].trim(), fromAndTo[1].trim());
    }
}
