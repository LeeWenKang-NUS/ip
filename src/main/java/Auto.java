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
        ArrayList<String> stringList = new ArrayList<String>();

        while (true) {
            String userInput = scanner.nextLine();

            if (userInput.equals("bye")) {
                System.out.println("=======================================================");
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println("=======================================================");
                break;
            } else if (userInput.equals("list")) {
                System.out.println("=======================================================");
                for (int i = 0; i < stringList.size(); i++) {
                    System.out.println(String.format("%d. %s", i + 1, stringList.get(i)));
                }
                System.out.println("=======================================================");
            } else {
                stringList.add(userInput);
                System.out.println("=======================================================");
                System.out.println("Added: " + userInput);
                System.out.println("=======================================================");
            }
        }

        scanner.close();
    }
}
