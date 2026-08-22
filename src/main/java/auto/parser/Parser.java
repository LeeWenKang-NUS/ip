package auto.parser;

import java.time.LocalDate;

import auto.command.AddCommand;
import auto.command.Command;
import auto.command.DeleteCommand;
import auto.command.ExitCommand;
import auto.command.ListCommand;
import auto.command.MarkCommand;
import auto.command.OccurCommand;
import auto.command.UnmarkCommand;
import auto.exception.AutoException;
import auto.task.Deadline;
import auto.task.Event;
import auto.task.ToDo;
import auto.util.DateUtil;

/** Converts raw user input and command arguments into domain values. */
public class Parser {
    /** Parses one input line into a fully configured executable command. */
    public static Command parse(String input) throws AutoException {
        if (input.equals("bye")) {
            return new ExitCommand();
        } else if (input.equals("list")) {
            return new ListCommand();
        } else if (input.startsWith("occur ")) {
            return new OccurCommand(parseDate(argumentAfter(input, "occur").trim()));
        } else if (input.startsWith("mark ")) {
            return new MarkCommand(parseTaskNumber(argumentAfter(input, "mark")));
        } else if (input.startsWith("unmark ")) {
            return new UnmarkCommand(parseTaskNumber(argumentAfter(input, "unmark")));
        } else if (input.startsWith("delete ")) {
            return new DeleteCommand(parseTaskNumber(argumentAfter(input, "delete")));
        } else if (input.startsWith("todo ")) {
            return new AddCommand(parseToDo(argumentAfter(input, "todo")));
        } else if (input.startsWith("deadline ")) {
            return new AddCommand(parseDeadline(argumentAfter(input, "deadline")));
        } else if (input.startsWith("event ")) {
            return new AddCommand(parseEvent(argumentAfter(input, "event")));
        }
        throw AutoException.unknownCommand();
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
        return new Deadline(parts[0].trim(), parseDate(parts[1].trim()));
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
        LocalDate from = parseDate(fromAndTo[0].trim());
        LocalDate to = parseDate(fromAndTo[1].trim());
        if (to.isBefore(from)) {
            throw AutoException.eventEndsBeforeItStarts();
        }
        return new Event(nameAndTime[0].trim(), from, to);
    }

    private static String argumentAfter(String input, String keyword) {
        return input.substring(keyword.length() + 1);
    }

    /**
     * Parses a strict user-facing date and translates parsing failures into an
     * {@link AutoException} containing the invalid value.
     *
     * @param value date text in {@code dd/MM/yyyy} format
     * @return parsed date
     * @throws AutoException if the value is not a valid date in the required format
     */
    private static LocalDate parseDate(String value) throws AutoException {
        try {
            return DateUtil.parseInput(value);
        } catch (IllegalArgumentException e) {
            throw AutoException.invalidDate(value);
        }
    }
}
