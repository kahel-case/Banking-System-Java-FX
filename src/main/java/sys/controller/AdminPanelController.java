package sys.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import sys.utility.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class AdminPanelController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        readUserStatusFile();
    }

    @SuppressWarnings("unchecked")
    public void readUserStatusFile() {
        String STATUS_FILE = "userStatus.dat";
        File f = new File(STATUS_FILE);
        Map<String, String> userMap = new HashMap<>();

        if (!f.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if (obj instanceof Map<?, ?> map) {
                for (Object key : map.keySet()) {
                    if (key instanceof String username && map.get(key) instanceof String status) {
                        if (username.equals("admin")) {
                            continue;
                        }
                        if (status.equalsIgnoreCase("Active") || status.equalsIgnoreCase("Blocked")) {
                            UserStatus u = new UserStatus(username, "- No activities - ", status);
                            user_contentArea.getChildren().addFirst(u);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private Button backToLoginPage;
    @FXML private VBox user_contentArea;

    @FXML private Button blockUser;
    @FXML private Button unblockUser;

    @FXML
    protected void backToLoginPage() throws IOException {
        String launcher = "/sys/bankingsystemjavafx/launcher.fxml";
        String style = "style.css";
        Utility.switchScene((Stage) backToLoginPage.getScene().getWindow(), launcher, style);
    }

    private static void blockUser(Text userName, Text status) {
        status.setText("Blocked");
        Utility.setStatus(userName.getText(), status.getText());
        System.out.println(userName.getText());
    }

    private static void unblockUser(Text userName, Text status) {
        status.setText("Active");
        Utility.setStatus(userName.getText(), status.getText());
        System.out.println(userName.getText());
    }

    private static class UserStatus extends HBox {
        private Text userName;
        private Text comment;
        private Text status;

        private Button blockButton;
        private Button unblockButton;

        public UserStatus(String userName, String comment, String status) {
            // --- Layout settings ---
            setSpacing(15);
            setPadding(new Insets(15));
            HBox.setMargin(this, new Insets(15));
            setAlignment(Pos.CENTER);
            setPrefWidth(200);

            // Always allow this HBox to grow horizontally
            HBox.setHgrow(this, Priority.ALWAYS);

            // --- CSS for the HBox ---
            setStyle("""
                -fx-background-color: #f8f9fa;
                -fx-background-radius: 10;
                -fx-border-color: #cccccc;
                -fx-border-radius: 10;
                -fx-border-width: 1;
            """);

            // --- Create text elements ---
            this.userName = new Text(userName);
            this.userName.setStyle("-fx-fill: #555; -fx-font-weight: bold;");

            this.comment = new Text(comment);
            this.comment.setStyle("-fx-fill: #0078d7; -fx-font-weight: bold;");

            this.status = new Text(status);
            this.status.setStyle("-fx-fill: #28a745; -fx-font-style: italic;");

            // --- TextFlow container ---
            TextFlow textFlow = new TextFlow(this.userName, this.comment, this.status);
            textFlow.setPadding(new Insets(5));
            textFlow.setStyle("-fx-background-color: transparent;");

            // --- Buttons ---
            blockButton = new Button("Block User");
            blockButton.setId("blockUser");
            blockButton.setOnAction(_ -> blockUser(this.userName, this.status));
            blockButton.setStyle("""
                -fx-background-color: #dc3545;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 6;
            """);

            unblockButton = new Button("Unblock User");
            unblockButton.setId("unblockUser");
            unblockButton.setOnAction(_ -> unblockUser(this.userName, this.status));
            unblockButton.setStyle("""
                -fx-background-color: #28a745;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 6;
            """);

            // --- Add everything to HBox ---
            getChildren().addAll(textFlow, blockButton, unblockButton);
        }
        public Text getUserName() {return userName; }
        public Text getComment() {return comment;}
        public Text getStatus() {return status;}
        public Button getBlockButton() {return blockButton;}
        public Button getUnblockButton() {return unblockButton;}
    }
}
