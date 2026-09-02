package auto.ui;

import auto.Auto;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Handles user interaction with the main chat window.
 */
public class MainWindow {
    private static final String WELCOME_MESSAGE = String.join(System.lineSeparator(),
            "    _         _        ",
            "   / \\  _   _| |_ ___  ",
            "  / _ \\| | | | __/ _ \\ ",
            " / ___ \\ |_| | || (_) |",
            "/_/   \\_\\__,_|\\__\\___/ ",
            "",
            "Hello! I'm Auto, your personal assistant.",
            "What can I do for you?");

    private Auto auto;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    /**
     * Configures the chat history to follow newly added messages.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(
                observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the application instance used to process chat messages.
     *
     * @param auto Application instance shared for the lifetime of this window.
     */
    public void setAuto(Auto auto) {
        this.auto = auto;
        String welcomeMessage = WELCOME_MESSAGE;
        String startupMessage = auto.getStartupMessage();
        if (!startupMessage.isEmpty()) {
            welcomeMessage += System.lineSeparator() + System.lineSeparator() + startupMessage;
        }
        dialogContainer.getChildren().add(
                createMessageLabel("Auto: " + welcomeMessage, "auto-message"));
    }

    /**
     * Displays the user's message and an echoed response, then clears the input.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = auto.getResponse(input);
        dialogContainer.getChildren().addAll(
                createMessageLabel("You: " + input, "user-message"),
                createMessageLabel("Auto: " + response, "auto-message"));
        userInput.clear();
    }

    /**
     * Creates a wrapping chat-message label with the supplied style class.
     *
     * @param message Text displayed in the chat history.
     * @param styleClass CSS class used to identify the message speaker.
     * @return The configured message label.
     */
    private Label createMessageLabel(String message, String styleClass) {
        Label label = new Label(message);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.getStyleClass().add(styleClass);
        return label;
    }
}
