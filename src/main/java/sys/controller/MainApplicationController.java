package sys.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import sys.utility.Utility;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class MainApplicationController implements Initializable {

    private int _currentBalance = 10000000;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Circle clip = new Circle(avatar.getFitWidth() / 2, avatar.getFitHeight() / 2, avatar.getFitWidth() / 2);
        avatar.setClip(clip);

        backToMainMenu();
        setCurrentBalanceLabels();
        refreshFields();
    }

    @FXML private ImageView avatar;
    @FXML private Label label_userName;
    @FXML private Label label_mainMenu_currentBalance;
    @FXML private Label label_deposit_currentBalance;
    @FXML private Label label_withdraw_currentBalance;
    @FXML private Label label_transfer_currentBalance;

    @FXML private VBox page_mainMenu;
    @FXML private VBox page_deposit;
    @FXML private VBox page_withdraw;
    @FXML private VBox page_transfer;

    @FXML private TextField textField_deposit;
    @FXML private TextField textField_withdraw;
    @FXML private TextField textField_transfer;
    @FXML private TextField textField_receiverName;

    @FXML private Button btn_logOut;

    @FXML
    protected void notificationButtonClick() {
        System.out.println("Notification");
    }

    @FXML
    protected void depositButtonClick() {
        page_deposit.setVisible(true);

        page_mainMenu.setVisible(false);
        page_withdraw.setVisible(false);
        page_transfer.setVisible(false);
    }

    @FXML
    protected void withdrawButtonClick() {
        page_withdraw.setVisible(true);

        page_mainMenu.setVisible(false);
        page_deposit.setVisible(false);
        page_transfer.setVisible(false);
    }

    @FXML
    protected void transferButtonClick() {
        page_transfer.setVisible(true);

        page_mainMenu.setVisible(false);
        page_deposit.setVisible(false);
        page_withdraw.setVisible(false);
    }

    @FXML
    protected void historyButtonClick() {
        System.out.println("history");
    }

    @FXML
    protected void backToMainMenu() {
        refreshFields();
        page_mainMenu.setVisible(true);
        page_deposit.setVisible(false);
        page_withdraw.setVisible(false);
        page_transfer.setVisible(false);
    }

    @FXML
    protected void logOutSuccessful() throws IOException {
        String launcher = "/sys/bankingsystemjavafx/launcher.fxml";
        String style = "style.css";
        Utility.switchScene((Stage) btn_logOut.getScene().getWindow(), launcher, style);
    }

    /*
     *  PRIVATE METHODS
     * */
    private void setCurrentBalanceLabels() {
        DecimalFormat d = new DecimalFormat("#,###");
        String formattedBalance = d.format(_currentBalance);
        label_mainMenu_currentBalance.setText(formattedBalance);
        label_deposit_currentBalance.setText(formattedBalance);
        label_withdraw_currentBalance.setText(formattedBalance);
        label_transfer_currentBalance.setText(formattedBalance);
    }

    private void refreshFields() {
        textField_deposit.setText("");
        textField_withdraw.setText("");
        textField_transfer.setText("");
        textField_receiverName.setText("");
    }
}
