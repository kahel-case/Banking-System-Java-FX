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
        page_launcher.setVisible(false);
        page_createAccount.setVisible(false);
        page_signIn.setVisible(true);
    }

    @FXML
    protected void createAccountButtonClick() {
        refreshFields();
        page_launcher.setVisible(false);
        page_signIn.setVisible(false);
        page_createAccount.setVisible(true);
    }

    @FXML
    protected void backToMainButtonClick() {
        refreshFields();
        page_signIn.setVisible(false);
        page_createAccount.setVisible(false);
        page_launcher.setVisible(true);
    }

    @FXML
    protected void createAccountSuccessful() {
        refreshFields();
        JOptionPane.showMessageDialog(null, "Successfully create a new account!");

        page_signIn.setVisible(false);
        page_createAccount.setVisible(false);
        page_launcher.setVisible(true);
    }

    @FXML
    protected void logInSuccessful() throws IOException {
        refreshFields();

        String mainApplication = "/sys/bankingsystemjavafx/main_application.fxml";
        String style = "style.css";
        Utility.switchScene((Stage) btn_logIn.getScene().getWindow(), mainApplication, style);
    }

    /*
    *  PRIVATE METHODS
    * */
    private void refreshFields() {
        textField_userName.setText("");
        textField_createUserName.setText("");
        passwordField_password.setText("");
        passwordField_createPassword.setText("");
        passwordField_confirmPassword.setText("");
    }

}