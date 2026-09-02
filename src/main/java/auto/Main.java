package auto;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Configures and displays Auto's JavaFX window.
 */
public class Main extends Application {
    private static final String MAIN_WINDOW_FXML = "/view/MainWindow.fxml";

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Main.class.getResource(MAIN_WINDOW_FXML));
        stage.setScene(new Scene(root));
        stage.setTitle("Auto");
        stage.setMinWidth(400);
        stage.setMinHeight(600);
        stage.show();
    }
}
