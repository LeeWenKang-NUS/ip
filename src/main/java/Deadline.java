public class Deadline extends Task {
    private String by;

    public Deadline(String name, String by) {
        super(name);
        this.by = by;
    }

    @Override
    public String toDataString() {
        return String.format("D | %d | %s | %s",
                isCompleted() ? 1 : 0, encode(getName()), encode(by));
    }

    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), by);
    }
}
