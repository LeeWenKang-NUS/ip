import java.util.ArrayList;
import java.util.Scanner;

public class Auto {
    public static void main(String[] args) {
        String banner = "    _         _        \n"
                + "   / \\  _   _| |_ ___  \n"
                + "  / _ \\| | | | __/ _ \\ \n"
                + " / ___ \\ |_| | || (_) |\n"
                + "/_/   \\_\\__,_|\\__\\___/ \n\n";
        String greeting = "=======================================================\n"
                + banner
                + "Hello! I'm Auto, your personal assistant.\n"
                + "What can I do for you?\n"
                + "=======================================================";
        System.out.println(greeting);

        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> taskList = new ArrayList<Task>();

        while (true) {
            String userInput = scanner.nextLine();

            if (userInput.equals("bye")) {
                System.out.println("=======================================================");
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println("=======================================================");
                break;
            } else if (userInput.equals("list")) {
                System.out.println("=======================================================");
                for (int i = 0; i < taskList.size(); i++) {
                    System.out.println(String.format("%d. %s", i + 1, taskList.get(i)));
                }
                System.out.println("=======================================================");
            } else if (userInput.startsWith("mark ")) {
                int taskIndex = Integer.parseInt(userInput.substring(5).trim()) - 1;
                Task task = taskList.get(taskIndex);
                task.mark();
                System.out.println("=======================================================");
                System.out.println("Nice! I've marked this task as done");
                System.out.println("  " + task);
                System.out.println("=======================================================");
            } else if (userInput.startsWith("unmark ")) {
                int taskIndex = Integer.parseInt(userInput.substring(7).trim()) - 1;
                Task task = taskList.get(taskIndex);
                task.unmark();
                System.out.println("=======================================================");
                System.out.println("Nice! I've marked this task as not done yet");
                System.out.println("  " + task);
                System.out.println("=======================================================");
            } else {
                taskList.add(new Task(userInput));
                System.out.println("=======================================================");
                System.out.println("Added: " + userInput);
                System.out.println("=======================================================");
            }
        }

        scanner.close();
    }
}
