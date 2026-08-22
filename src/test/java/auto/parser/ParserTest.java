package auto.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import auto.command.AddCommand;
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

/** Tests command dispatch, argument parsing, date validation, and parser failures. */
class ParserTest {
    @Test
    void parse_allSupportedCommandWords_returnsExpectedCommandTypes() throws Exception {
        assertAll(
                () -> assertInstanceOf(ExitCommand.class, Parser.parse("bye")),
                () -> assertInstanceOf(ListCommand.class, Parser.parse("list")),
                () -> assertInstanceOf(OccurCommand.class, Parser.parse("occur 22/08/2026")),
                () -> assertInstanceOf(MarkCommand.class, Parser.parse("mark 1")),
                () -> assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1")),
                () -> assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1")),
                () -> assertInstanceOf(AddCommand.class, Parser.parse("todo read book")),
                () -> assertInstanceOf(AddCommand.class,
                        Parser.parse("deadline submit report /by 22/08/2026")),
                () -> assertInstanceOf(AddCommand.class,
                        Parser.parse("event conference /from 21/08/2026 /to 23/08/2026")));
    }

    @Test
    void parse_unknownEmptyAndBareCommands_throwsUnknownCommand() {
        assertUnknownCommand("borrow book");
        assertUnknownCommand("");
        assertUnknownCommand("mark");
        assertUnknownCommand("todo");
        assertUnknownCommand("deadline");
        assertUnknownCommand("event");
        assertUnknownCommand("occur");
    }

    @Test
    void parse_wrongCaseOrOuterWhitespace_throwsUnknownCommand() {
        assertUnknownCommand("List");
        assertUnknownCommand("BYE");
        assertUnknownCommand(" list");
        assertUnknownCommand("list ");
    }

    @Test
    void parse_taskNumberCommandsWithNonNumber_throwsTaskNumberError() {
        assertAll(
                () -> assertExceptionMessage("Ohhh Noooo... 'abc' is not a task number!",
                        () -> Parser.parse("mark abc")),
                () -> assertExceptionMessage("Ohhh Noooo... '1.5' is not a task number!",
                        () -> Parser.parse("unmark 1.5")),
                () -> assertExceptionMessage("Ohhh Noooo... '' is not a task number!",
                        () -> Parser.parse("delete   ")));
    }

    @Test
    void parse_occurWithInvalidDate_throwsInvalidDateError() {
        assertExceptionMessage(
                "Ohhh Noooo... '31/02/2026' is not a valid date! Use dd/MM/yyyy.",
                () -> Parser.parse("occur 31/02/2026"));
    }

    @Test
    void parseTaskNumber_integerWithSurroundingWhitespace_returnsInteger() throws Exception {
        assertEquals(12, Parser.parseTaskNumber("  12  "));
        assertEquals(1, Parser.parseTaskNumber("+1"));
        assertEquals(-1, Parser.parseTaskNumber("-1"));
    }

    @Test
    void parseTaskNumber_nonIntegerEmptyAndOverflow_throwsTrimmedArgumentError() {
        assertAll(
                () -> assertExceptionMessage("Ohhh Noooo... 'one' is not a task number!",
                        () -> Parser.parseTaskNumber(" one ")),
                () -> assertExceptionMessage("Ohhh Noooo... '' is not a task number!",
                        () -> Parser.parseTaskNumber("   ")),
                () -> assertExceptionMessage("Ohhh Noooo... '2147483648' is not a task number!",
                        () -> Parser.parseTaskNumber("2147483648")));
    }

    @Test
    void parseToDo_descriptionWithWhitespaceAndSeparators_preservesDescriptionExactly() {
        ToDo todo = Parser.parseToDo("  read | book  ");

        assertEquals("[T][ ]   read | book  ", todo.toString());
    }

    @Test
    void parseDeadline_validInput_trimsDescriptionAndDate() throws Exception {
        Deadline deadline = Parser.parseDeadline("  submit report   /by   22/08/2026  ");

        assertEquals("[D][ ] submit report (by: Aug 22 2026)", deadline.toString());
    }

    @Test
    void parseDeadline_missingOrMalformedDelimiter_throwsDeadlineNeedsByError() {
        assertAll(
                () -> assertDeadlineNeedsBy("submit report"),
                () -> assertDeadlineNeedsBy("submit report /by22/08/2026"),
                () -> assertDeadlineNeedsBy("submit report /BY 22/08/2026"));
    }

    @Test
    void parseDeadline_invalidEmptyAndImpossibleDates_throwsInvalidDateError() {
        assertAll(
                () -> assertInvalidDate("2026-08-22"),
                () -> assertInvalidDate(""),
                () -> assertInvalidDate("31/02/2026"));
    }

    @Test
    void parseEvent_validRange_trimsDescriptionAndDates() throws Exception {
        Event event = Parser.parseEvent(
                "  conference   /from   21/08/2026   /to   23/08/2026  ");

        assertEquals("[E][ ] conference (from: Aug 21 2026 to: Aug 23 2026)", event.toString());
    }

    @Test
    void parseEvent_sameStartAndEndDate_createsSingleDayEvent() throws Exception {
        Event event = Parser.parseEvent("meeting /from 22/08/2026 /to 22/08/2026");

        assertEquals("[E][ ] meeting (from: Aug 22 2026 to: Aug 22 2026)", event.toString());
    }

    @Test
    void parseEvent_missingOrMalformedDelimiters_throwsRequiredTimesError() {
        assertAll(
                () -> assertEventNeedsFromAndTo("conference"),
                () -> assertEventNeedsFromAndTo("conference /from 21/08/2026"),
                () -> assertEventNeedsFromAndTo("conference /to 23/08/2026"),
                () -> assertEventNeedsFromAndTo(
                        "conference /FROM 21/08/2026 /to 23/08/2026"),
                () -> assertEventNeedsFromAndTo(
                        "conference /from 21/08/2026 /TO 23/08/2026"));
    }

    @Test
    void parseEvent_invalidStartOrEndDate_throwsInvalidDateError() {
        assertAll(
                () -> assertExceptionMessage(
                        "Ohhh Noooo... '31/02/2026' is not a valid date! Use dd/MM/yyyy.",
                        () -> Parser.parseEvent(
                                "conference /from 31/02/2026 /to 23/08/2026")),
                () -> assertExceptionMessage(
                        "Ohhh Noooo... '2026-08-23' is not a valid date! Use dd/MM/yyyy.",
                        () -> Parser.parseEvent(
                                "conference /from 21/08/2026 /to 2026-08-23")));
    }

    @Test
    void parseEvent_endBeforeStart_throwsRangeError() {
        assertExceptionMessage(
                "Ohhh Noooo... an event's /to date cannot be before its /from date!",
                () -> Parser.parseEvent(
                        "conference /from 23/08/2026 /to 21/08/2026"));
    }

    private void assertUnknownCommand(String input) {
        assertExceptionMessage("Ohhh Noooo... I don't understand you!",
                () -> Parser.parse(input));
    }

    private void assertDeadlineNeedsBy(String input) {
        assertExceptionMessage("Ohhh Noooo... a deadline needs a /by date!",
                () -> Parser.parseDeadline(input));
    }

    private void assertInvalidDate(String date) {
        assertExceptionMessage(
                String.format("Ohhh Noooo... '%s' is not a valid date! Use dd/MM/yyyy.", date),
                () -> Parser.parseDeadline("submit report /by " + date));
    }

    private void assertEventNeedsFromAndTo(String input) {
        assertExceptionMessage("Ohhh Noooo... an event needs a /from and a /to time!",
                () -> Parser.parseEvent(input));
    }

    /** Verifies the parser's user-facing checked exception without duplicating try/catch code. */
    private void assertExceptionMessage(String expectedMessage, ThrowingParserCall call) {
        AutoException exception = assertThrows(AutoException.class, call::run);
        assertEquals(expectedMessage, exception.getMessage());
    }

    /** Represents a parser invocation that may reject its input. */
    @FunctionalInterface
    private interface ThrowingParserCall {
        void run() throws AutoException;
    }
}
