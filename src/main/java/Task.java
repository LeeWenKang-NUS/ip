import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** Represents a task with a description and completion status. */
public class Task {
    private String name;
    private boolean completed = false;

    public Task(String name) {
        this.name = name;
    }

    public void mark() {
        this.completed = true;
    }

    public void unmark() {
        this.completed = false;
    }

    public boolean isCompleted() {
        return completed;
    }

    protected String getName() {
        return name;
    }

    /** Encodes text so storage separators in user input remain unambiguous. */
    protected static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /** Returns this task in the durable storage format. */
    public String toDataString() {
        return String.format("T | %d | %s", completed ? 1 : 0, encode(name));
    }

    @Override
    public String toString() {
        return String.format("[%s] %s",
                this.completed ? "X" : " ",
                this.name);
    }
}
