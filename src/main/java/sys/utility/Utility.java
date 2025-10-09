package sys.utility;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class Utility {

    // === SCENE SWITCHING ===
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

    // === USER ACCOUNT SYSTEM ===
    private static final String DATA_FILE = "users.dat";
    private static Map<String, String> users = loadUsers();

    @SuppressWarnings("unchecked")
    private static Map<String, String> loadUsers() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (Map<String, String>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private static void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean usernameExists(String username) {
        return users.containsKey(username);
    }

    public static boolean createUser(String username, String password) {
        if (usernameExists(username)) return false;
        users.put(username, password);
        saveUsers();

        // Create transaction file for the new user
        FileHandler.addUser(username);
        return true;
    }

    public static void initializeAdmin() {
        String username = "admin";
        String password = "123";
        if (usernameExists(username)) return;
        users.put(username, password);
        saveUsers();
    }

    public static boolean validateLogin(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }

    // === USER ACCOUNT SYSTEM ===
    private static final String STATUS_FILE = "users.dat";
    private static Map<String, String> userStatus = loadUserStatus();

    @SuppressWarnings("unchecked")
    private static Map<String, String> loadUserStatus() {
        File f = new File(STATUS_FILE);
        if (!f.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (Map<String, String>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private static void saveUserStatus() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(userStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean userExists(String username) {
        return userStatus.containsKey(username);
    }

    public static boolean createUserStatus(String username) {
        if (userExists(username)) return false;
        userStatus.put(username, "Active"); // default status
        saveUserStatus();

        return true;
    }

    public static void setStatus(String username, String status) {
        if (!userExists(username)) return;
        users.put(username, status);
        saveUsers();
    }

    public static String getStatus(String username) {
        return users.getOrDefault(username, "Unknown");
    }

    public static boolean isActive(String username) {
        return "Active".equalsIgnoreCase(getStatus(username));
    }

    public static void switchSceneWithUser(Stage stage, String nextScene, String style, String username) throws IOException {
        if (username.equals("admin")) { // OPENS THE ADMIN PANEL
            String launcher = "/sys/bankingsystemjavafx/admin_panel.fxml";
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(Utility.class.getResource(launcher)));
            Parent root = loader.load();

            Object controller = loader.getController();
            try {
                controller.getClass().getMethod("setUsername", String.class).invoke(controller, username);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Scene scene = new Scene(root);
            scene.getStylesheets().add(style);
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.centerOnScreen();
            stage.show();
        } else {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(Utility.class.getResource(nextScene)));
            Parent root = loader.load();

            Object controller = loader.getController();
            try {
                controller.getClass().getMethod("setUsername", String.class).invoke(controller, username);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Scene scene = new Scene(root);
            scene.getStylesheets().add(style);
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.centerOnScreen();
            stage.show();
        }
    }

    public static double getLatestBalance(String logText) {
        String[] parts = logText.split("Balance:");
        if (parts.length < 2) {
            throw new IllegalArgumentException("No balance found in log");
        }

        String lastBalancePart = parts[parts.length - 1].trim();

        lastBalancePart = lastBalancePart.replace("₱", "").replace(",", "");

        String[] tokens = lastBalancePart.split("\\s+");
        String balanceString = tokens[0];

        return Double.parseDouble(balanceString);
    }
}
