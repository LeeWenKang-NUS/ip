import java.time.LocalDate;

public class Event extends Task {
    private final LocalDate from;
    private final LocalDate to;

    public Event(String name, LocalDate from, LocalDate to) {
        super(name);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toDataString() {
        return String.format("E | %d | %s | %s | %s", isCompleted() ? 1 : 0,
                encode(getName()), encode(DateUtil.format(from)), encode(DateUtil.format(to)));
    }

    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(),
                DateUtil.format(from), DateUtil.format(to));
    }
}
