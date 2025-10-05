package sys.bankingsystemjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import javax.swing.*;

public class LauncherController {
    @FXML private TextField textField_userName;
    @FXML private TextField textField_createUserName;
    @FXML private PasswordField passwordField_password;
    @FXML private PasswordField passwordField_createPassword;
    @FXML private PasswordField passwordField_confirmPassword;

    @FXML private Button btn_signIn;
    @FXML private Button btn_createAccount;
    @FXML private VBox page_launcher;
    @FXML private VBox page_signIn;
    @FXML private VBox page_createAccount;

    @FXML
    protected void signInButtonClick() {
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
        refreshFields();
        JOptionPane.showMessageDialog(null, "Successfully create a new account!");
        page_launcher.setVisible(true);

        page_signIn.setVisible(false);
        page_createAccount.setVisible(false);
    }

    private void refreshFields() {
        textField_userName.setText("");
        textField_createUserName.setText("");
        passwordField_password.setText("");
        passwordField_createPassword.setText("");
        passwordField_confirmPassword.setText("");
    }
}