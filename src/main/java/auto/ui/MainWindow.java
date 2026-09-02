package auto.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Handles user interaction with the main chat window.
 */
public class MainWindow {
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
     * Displays the user's message and an echoed response, then clears the input.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        dialogContainer.getChildren().addAll(
                createMessageLabel("You: " + input, "user-message"),
                createMessageLabel("Auto: " + input, "auto-message"));
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
