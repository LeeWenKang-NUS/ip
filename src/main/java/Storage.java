import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Loads and saves the task list on the hard disk. */
public class Storage {
    private static final Path DATA_FILE = Path.of(
            System.getProperty("auto.data.file", "data/auto.txt"));

    /**
     * Replaces the data file with one display-formatted task per line.
     *
     * @param tasks current tasks to save
     * @throws IOException if the data directory or file cannot be written
     */
    public static void save(List<Task> tasks) throws IOException {
        Files.createDirectories(DATA_FILE.getParent());
        Files.write(DATA_FILE, tasks.stream().map(Task::toString).toList());
    }

    /**
     * Loads tasks from the data file, or returns an empty list if it does not exist.
     *
     * @return tasks stored during the previous run
     * @throws IOException if the data file cannot be read
     */
    public static List<Task> load() throws IOException {
        if (!Files.exists(DATA_FILE)) {
            return new ArrayList<>();
        }

        List<Task> tasks = new ArrayList<>();
        for (String line : Files.readAllLines(DATA_FILE)) {
            tasks.add(parseTask(line));
        }
        return tasks;
    }

    /** Converts one valid display-formatted data line back into a task. */
    private static Task parseTask(String line) {
        char type = line.charAt(1);
        boolean isCompleted = line.charAt(4) == 'X';
        String details = line.substring(7);
        Task task;

        if (type == 'T') {
            task = new ToDo(details);
        } else if (type == 'D') {
            int byIndex = details.lastIndexOf(" (by: ");
            String name = details.substring(0, byIndex);
            String by = details.substring(byIndex + 6, details.length() - 1);
            task = new Deadline(name, by);
        } else {
            int fromIndex = details.lastIndexOf(" (from: ");
            int toIndex = details.lastIndexOf(" to: ");
            String name = details.substring(0, fromIndex);
            String from = details.substring(fromIndex + 8, toIndex);
            String to = details.substring(toIndex + 5, details.length() - 1);
            task = new Event(name, from, to);
        }

        if (isCompleted) {
            task.mark();
        }
        return task;
    }
}
