/**
 * The commands that {@code Auto} understands.
 *
 * <p>Each constant owns its keyword and knows whether the command carries an
 * argument after that keyword. Keeping both facts here means the dispatch code
 * no longer repeats the keyword as a literal and its length as a magic number.
 */
public enum Command {
    BYE("bye", false),
    LIST("list", false),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true);

    /** The word the user types to invoke this command. */
    private final String keyword;

    /** Whether the keyword must be followed by a space and an argument. */
    private final boolean hasArgument;

    Command(String keyword, boolean hasArgument) {
        this.keyword = keyword;
        this.hasArgument = hasArgument;
    }

    /**
     * Returns the command the given input line invokes.
     *
     * <p>A command that takes an argument matches only when the keyword is
     * followed by a space, so {@code mark} on its own is not a {@code MARK}.
     * A command that takes no argument must match the whole line, so
     * {@code bye now} is not a {@code BYE}.
     *
     * @throws AutoException if no command matches the input
     */
    public static Command fromInput(String input) throws AutoException {
        for (Command command : values()) {
            if (command.matches(input)) {
                return command;
            }
        }
        throw AutoException.unknownCommand();
    }

    private boolean matches(String input) {
        return this.hasArgument
                ? input.startsWith(this.keyword + " ")
                : input.equals(this.keyword);
    }

    /**
     * Returns the part of the input line after this command's keyword, verbatim
     * and including any surrounding spaces. Returns an empty string for a
     * command that takes no argument.
     *
     * <p>Only correct for an input line this command {@linkplain
     * #fromInput(String) matched}, since it assumes the keyword and its
     * trailing space are present.
     */
    public String argumentIn(String input) {
        if (!this.hasArgument) {
            return "";
        }
        // Skip the keyword plus the single space that separates it from the argument.
        return input.substring(this.keyword.length() + 1);
    }
}
