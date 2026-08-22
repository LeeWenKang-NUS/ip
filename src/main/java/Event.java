public class Event extends Task {
    private String from;
    private String to;

    public Event(String name, String from, String to) {
        super(name);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toDataString() {
        return String.format("E | %d | %s | %s | %s", isCompleted() ? 1 : 0,
                encode(getName()), encode(from), encode(to));
    }

    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(), from, to);
    }
}
