package sys.bankingsystemjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sys.utility.FileHandler;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    Image frameIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Logo/Logo_FrameIcon.png")));

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("launcher.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add("style.css");

        stage.setTitle("Banking System");
        stage.setScene(scene);
        stage.show();

        stage.getIcons().add(frameIcon);
    }

    public static void main(String[] args) {
        launch();
    }
}