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

        while (true) {
            String userInput = scanner.nextLine();

            if (userInput.equals("bye")) {
                System.out.println("=======================================================");
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println("=======================================================");
                break;
            } else {
                System.out.println("=======================================================");
                System.out.println(userInput);
                System.out.println("=======================================================");
            }
        }

        scanner.close();
    }
}
