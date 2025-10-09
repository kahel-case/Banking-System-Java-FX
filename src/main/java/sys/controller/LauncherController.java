package sys.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sys.utility.Utility;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LauncherController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        page_launcher.setVisible(true);
        page_signIn.setVisible(false);
        page_createAccount.setVisible(false);
        refreshFields();
    }

    @FXML private TextField textField_userName;
    @FXML private TextField textField_createUserName;
    @FXML private PasswordField passwordField_password;
    @FXML private PasswordField passwordField_createPassword;
    @FXML private PasswordField passwordField_confirmPassword;

    @FXML private VBox page_launcher;
    @FXML private VBox page_signIn;
    @FXML private VBox page_createAccount;

    @FXML private Button btn_logIn;

    @FXML
    private void signInButtonClick() {
        refreshFields();
        page_signIn.setVisible(true);
        page_launcher.setVisible(false);
        page_createAccount.setVisible(false);
    }

    @FXML
    protected void createAccountButtonClick() {
        refreshFields();
        page_createAccount.setVisible(true);
        page_launcher.setVisible(false);
        page_signIn.setVisible(false);
    }

    @FXML
    protected void backToMainButtonClick() {
        refreshFields();
        page_launcher.setVisible(true);
        page_signIn.setVisible(false);
        page_createAccount.setVisible(false);
    }

    @FXML
    protected void createAccountSuccessful() {
        String username = textField_createUserName.getText().trim();
        String password = passwordField_createPassword.getText();
        String confirmPassword = passwordField_confirmPassword.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill out all fields!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(null, "Passwords do not match!");
            return;
        }

        if (Utility.usernameExists(username)) {
            JOptionPane.showMessageDialog(null, "Username already exists!");
            return;
        }

        boolean success = Utility.createUser(username, password) && Utility.createUserStatus(username);
        if (success) {
            JOptionPane.showMessageDialog(null, "Account created successfully!");
            backToMainButtonClick();
        } else {
            JOptionPane.showMessageDialog(null, "Failed to create account. Try again.");
        }
    }

    @FXML
    protected void logInSuccessful() throws IOException {
        String username = textField_userName.getText().trim();
        String password = passwordField_password.getText();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter both username and password!");
            return;
        }

        if (Utility.validateLogin(username, password)) {
            if (!Utility.isActive(username)) {
                JOptionPane.showMessageDialog(null, "You account has been blocked by the admin!");
                return;
            }
            String mainApplication = "/sys/bankingsystemjavafx/main_application.fxml";
            String style = "style.css";
            Utility.switchSceneWithUser((Stage) btn_logIn.getScene().getWindow(), mainApplication, style, username);
        } else {
            JOptionPane.showMessageDialog(null, "Invalid username or password!");
        }
    }

    private void refreshFields() {
        textField_userName.setText("");
        textField_createUserName.setText("");
        passwordField_password.setText("");
        passwordField_createPassword.setText("");
        passwordField_confirmPassword.setText("");
    }
}
