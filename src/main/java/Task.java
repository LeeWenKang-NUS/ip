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

    public String toString() {
        return String.format("[%s] %s",
                this.completed ? "X" : " ",
                this.name);
    }
}
