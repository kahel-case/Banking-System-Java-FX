package sys.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import sys.utility.FileHandler;
import sys.utility.FraudDetection;
import sys.utility.Utility;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AdminPanelController implements Initializable {

    @FXML private Button backToLoginPage;
    @FXML private VBox user_contentArea;
    @FXML private Button detectFraudButton;
    @FXML private ScrollPane fraudAlertsScrollPane;
    @FXML private VBox fraudAlerts_contentArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        readUserStatusFile();

        if (fraudAlertsScrollPane != null) {
            fraudAlertsScrollPane.setVisible(false);
        }
    }

    @SuppressWarnings("unchecked") // PUTS ALL USERNAME AND STATUS ON ADMIN PANEL
    public void readUserStatusFile() {
        String STATUS_FILE = "userStatus.dat";
        File f = new File(STATUS_FILE);

        if (!f.exists()) {
            return;
        }

        Runtime rt = Runtime.getRuntime();
        long beforeUsedMem = rt.totalMemory() - rt.freeMemory();
        long startTime = System.nanoTime();

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
            System.out.println("admin panel, read user file error.");
        }

        long endTime = System.nanoTime();
        long afterUsedMem = rt.totalMemory() - rt.freeMemory();

        String output = String.format("""
                [Runtime Monitor] readUserStatusFile() completed.
                - Thread: %s
                - Execution time: %.3f ms
                - Memory used: %.2f KB
                
                """,
        Thread.currentThread().getName(),
                (endTime - startTime) / 1_000_000.0,
                (afterUsedMem - beforeUsedMem) / 1024.0);
        System.out.printf(output);
        FileHandler.saveRuntime(output);
    }

    @FXML
    protected void backToLoginPage() throws java.io.IOException {
        String launcher = "/sys/bankingsystemjavafx/launcher.fxml";
        String style = "style.css";
        Utility.switchScene((Stage) backToLoginPage.getScene().getWindow(), launcher, style);
    }

    @FXML
    protected void runFraudDetection() {
        if (fraudAlerts_contentArea != null) {
            fraudAlerts_contentArea.getChildren().clear();
        }

        JOptionPane.showMessageDialog(null,
                "Running fraud detection analysis...\nThis may take a moment.",
                "Fraud Detection",
                JOptionPane.INFORMATION_MESSAGE);

        // RUNTIME START
        Runtime rt = Runtime.getRuntime();
        long beforeUsedMem = rt.totalMemory() - rt.freeMemory();
        long startTime = System.nanoTime();

        new Thread(() -> {
            try {
                System.out.println("[Runtime Monitor] Starting FraudDetection on " + Thread.currentThread().getName());

                List<String> suspiciousUsers = FraudDetection.detectSuspiciousUsers();

                // RUNTIME END
                long endTime = System.nanoTime();
                long afterUsedMem = rt.totalMemory() - rt.freeMemory();
                String output = String.format("""
                        [Runtime Monitor] FraudDetection.detectSuspiciousUsers() complete.
                        - Thread: %s
                        - Execution time: %.3f ms
                        - Memory used: %.2f KB
                        
                        """,
                        Thread.currentThread().getName(),
                        (endTime - startTime) / 1_000_000.0,
                        (afterUsedMem - beforeUsedMem) / 1024.0
                );

                System.out.printf(output);
                FileHandler.saveRuntime(output);

                javafx.application.Platform.runLater(() -> {
                    if (suspiciousUsers.isEmpty()) {
                        FraudAlert noFraud = new FraudAlert(
                                "No Suspicious Activity",
                                "No suspicious transactions detected at this time. All user activities appear normal."
                        );
                        fraudAlerts_contentArea.getChildren().add(noFraud);
                    } else {
                        for (String alert : suspiciousUsers) {
                            FraudAlert fraudAlert = new FraudAlert("Suspicious Activity Detected", alert);
                            fraudAlerts_contentArea.getChildren().add(fraudAlert);
                        }
                    }

                    if (fraudAlertsScrollPane != null) {
                        fraudAlertsScrollPane.setVisible(true);
                    }

                    String message = suspiciousUsers.isEmpty()
                            ? "Fraud detection complete!\nNo suspicious users found."
                            : "Fraud detection complete!\n" + suspiciousUsers.size() + " suspicious user(s) detected.";

                    JOptionPane.showMessageDialog(null,
                            message,
                            "Analysis Complete",
                            suspiciousUsers.isEmpty() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
                });

            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    JOptionPane.showMessageDialog(null,
                            "Error running fraud detection:\n" + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private static void blockUser(Text userName, Text status) {
        status.setText("Blocked");
        Utility.setStatus(userName.getText(), status.getText());
        System.out.println("Blocked user: " + userName.getText());
    }

    private static void unblockUser(Text userName, Text status) {
        status.setText("Active");
        Utility.setStatus(userName.getText(), status.getText());
        System.out.println("Unblocked user: " + userName.getText());
    }

    // USER STATUS CLASS
    private static class UserStatus extends HBox {
        private Text userName;
        private Text comment;
        private Text status;
        private Button blockButton;
        private Button unblockButton;

        public UserStatus(String userName, String comment, String status) {
            setSpacing(15);
            setPadding(new Insets(15));
            HBox.setMargin(this, new Insets(15));
            setAlignment(Pos.CENTER);
            setPrefWidth(200);
            HBox.setHgrow(this, Priority.ALWAYS);

            setStyle("""
                -fx-background-color: #f8f9fa;
                -fx-background-radius: 10;
                -fx-border-color: #cccccc;
                -fx-border-radius: 10;
                -fx-border-width: 1;
            """);

            this.userName = new Text(userName);
            this.userName.setStyle("-fx-fill: #555; -fx-font-weight: bold;");

            this.comment = new Text(comment);
            this.comment.setStyle("-fx-fill: #0078d7; -fx-font-weight: bold;");

            this.status = new Text(status);
            this.status.setStyle("-fx-fill: #28a745; -fx-font-style: italic;");

            TextFlow textFlow = new TextFlow(this.userName, this.comment, this.status);
            textFlow.setPadding(new Insets(5));
            textFlow.setStyle("-fx-background-color: transparent;");

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

            getChildren().addAll(textFlow, blockButton, unblockButton);
        }
    }

    private static class FraudAlert extends VBox {
        public FraudAlert(String title, String details) {
            setSpacing(8);
            setPadding(new Insets(12));
            VBox.setMargin(this, new Insets(5));

            setStyle("""
                -fx-background-color: #fff3cd;
                -fx-background-radius: 8;
                -fx-border-color: #ffc107;
                -fx-border-radius: 8;
                -fx-border-width: 2;
            """);

            Text titleText = new Text(title);
            titleText.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-fill: #856404;");

            Text detailsText = new Text(details);
            detailsText.setStyle("-fx-font-size: 12; -fx-fill: #856404;");
            detailsText.setWrappingWidth(480);

            getChildren().addAll(titleText, detailsText);
        }
    }
}
