package war;

/*
  WarGameApp.java - Main entry point for the War card game application.
  Loads the FXML layout and launches the JavaFX window.

  @author Nathan Tshishimbi
 * @version 1.3 (UD3) / (05/09/2026)
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class WarGameApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        URL fxmlUrl = getClass().getResource("/war/game.fxml");
        if (fxmlUrl == null) {
            throw new IOException("Cannot find game.fxml — check the file path in resources.");
        }
        Parent root = FXMLLoader.load(fxmlUrl);
        primaryStage.setTitle("War! Card Game");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
