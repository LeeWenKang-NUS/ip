import java.time.LocalDate;

public class Deadline extends Task {
    private final LocalDate by;

    public Deadline(String name, LocalDate by) {
        super(name);
        this.by = by;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return by.equals(date);
    }

    @Override
    public String toDataString() {
        return String.format("D | %d | %s | %s",
                isCompleted() ? 1 : 0, encode(getName()), encode(DateUtil.format(by)));
    }

    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), DateUtil.format(by));
    }
}
