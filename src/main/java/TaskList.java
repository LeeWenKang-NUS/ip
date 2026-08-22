import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Owns the task collection and its user-facing, one-based operations. */
public class TaskList {
    private final ArrayList<Task> tasks;

    public TaskList() {
        tasks = new ArrayList<>();
    }

    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>(initialTasks);
    }

    public void add(Task task) {
        tasks.add(task);
    }

    /** Returns the task identified by its one-based number. */
    public Task get(int taskNumber) throws AutoException {
        return tasks.get(toIndex(taskNumber));
    }

    /** Removes and returns the task identified by its one-based number. */
    public Task delete(int taskNumber) throws AutoException {
        return tasks.remove(toIndex(taskNumber));
    }

    public Task mark(int taskNumber) throws AutoException {
        Task task = get(taskNumber);
        task.mark();
        return task;
    }

    public Task unmark(int taskNumber) throws AutoException {
        Task task = get(taskNumber);
        task.unmark();
        return task;
    }

    public int size() {
        return tasks.size();
    }

    /** Returns a read-only view for display and persistence. */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /** Restores a deletion that could not be persisted. */
    public void restoreDeleted(int taskNumber, Task task) {
        tasks.add(taskNumber - 1, task);
    }

    /** Reverses the most recent addition when it could not be persisted. */
    public void removeLast() {
        tasks.remove(tasks.size() - 1);
    }

    private int toIndex(int taskNumber) throws AutoException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw AutoException.noSuchTask(taskNumber);
        }
        return taskNumber - 1;
    }
}
