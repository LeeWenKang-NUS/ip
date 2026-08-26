package auto.exception;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests every user-facing exception factory and its contextual message. */
class AutoExceptionTest {
    @Test
    void factoryMethods_allCases_returnExpectedPrefixedMessages() {
        assertAll(
                () -> assertMessage("Ohhh Noooo... I don't understand you!",
                        AutoException.unknownCommand()),
                () -> assertMessage("Ohhh Noooo... there is no task 0!",
                        AutoException.noSuchTask(0)),
                () -> assertMessage("Ohhh Noooo... there is no task 42!",
                        AutoException.noSuchTask(42)),
                () -> assertMessage("Ohhh Noooo... 'abc' is not a task number!",
                        AutoException.notATaskNumber("abc")),
                () -> assertMessage("Ohhh Noooo... a find command needs a keyword!",
                        AutoException.findNeedsKeyword()),
                () -> assertMessage("Ohhh Noooo... a deadline needs a /by date!",
                        AutoException.deadlineNeedsBy()),
                () -> assertMessage("Ohhh Noooo... an event needs a /from and a /to time!",
                        AutoException.eventNeedsFromAndTo()),
                () -> assertMessage(
                        "Ohhh Noooo... an event's /to date cannot be before its /from date!",
                        AutoException.eventEndsBeforeItStarts()),
                () -> assertMessage(
                        "Ohhh Noooo... '31/02/2026' is not a valid date! Use dd/MM/yyyy.",
                        AutoException.invalidDate("31/02/2026")));
    }

    private void assertMessage(String expected, AutoException exception) {
        assertEquals(expected, exception.getMessage());
    }
}
