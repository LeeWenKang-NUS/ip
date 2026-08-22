package auto.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import auto.exception.AutoException;

/** Owns the task collection and its user-facing, one-based operations. */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing a defensive copy of the supplied tasks.
     *
     * @param initialTasks tasks with which to initialize the list
     */
    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Appends a task to the end of the list.
     *
     * @param task task to add
     */
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

    /**
     * Marks the task identified by its one-based number as complete.
     *
     * @param taskNumber one-based task number
     * @return task whose completion state was updated
     * @throws AutoException if the task number is outside the list
     */
    public Task mark(int taskNumber) throws AutoException {
        Task task = get(taskNumber);
        task.mark();
        return task;
    }

    /**
     * Marks the task identified by its one-based number as incomplete.
     *
     * @param taskNumber one-based task number
     * @return task whose completion state was updated
     * @throws AutoException if the task number is outside the list
     */
    public Task unmark(int taskNumber) throws AutoException {
        Task task = get(taskNumber);
        task.unmark();
        return task;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return current task count
     */
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

    /**
     * Validates a user-facing task number and converts it to a list index.
     *
     * @param taskNumber one-based task number
     * @return corresponding zero-based list index
     * @throws AutoException if the task number is outside the list
     */
    private int toIndex(int taskNumber) throws AutoException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw AutoException.noSuchTask(taskNumber);
        }
        return taskNumber - 1;
    }
}
