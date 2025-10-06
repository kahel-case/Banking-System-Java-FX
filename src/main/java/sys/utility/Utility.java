package sys.utility;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Utility {

    // EXAMPLE USE:
    // Utility.switchScene((Stage) myNode.getScene().getWindow(), "myFXML.fxml");
    public static void switchScene(Stage stage, String nextScene) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(Utility.class.getResource(nextScene)));
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setMaximized(false);
        stage.centerOnScreen();

        stage.show();
    }

    public static void switchScene(Stage stage, String nextScene, String style) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(Utility.class.getResource(nextScene)));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(style);

        stage.setScene(scene);
        stage.setMaximized(false);
        stage.centerOnScreen();

        stage.show();
    }
}
