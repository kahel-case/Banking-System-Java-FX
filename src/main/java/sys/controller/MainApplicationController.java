package sys.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import sys.utility.Utility;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class MainApplicationController implements Initializable {

    private double _currentBalance;
    private String _userName;
    private TextFlow[] TransactionLogs;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Circle clip = new Circle(avatar.getFitWidth() / 2, avatar.getFitHeight() / 2, avatar.getFitWidth() / 2);
        avatar.setClip(clip);

        backToMainMenu();
        setCurrentBalanceLabels();
        refreshFields();

        setTextFormatters(textField_deposit);
        setTextFormatters(textField_withdraw);
        setTextFormatters(textField_transfer);
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
    @FXML private VBox page_history;
    @FXML private VBox page_notifications;

    @FXML private VBox transactionHistory_contentArea;

    @FXML private TextField textField_deposit;
    @FXML private TextField textField_withdraw;
    @FXML private TextField textField_transfer;
    @FXML private TextField textField_receiverName;

    @FXML private Button btn_logOut;

    @FXML
    protected void depositButtonClick() {
        switchPage(Page.DEPOSIT);
    }

    @FXML
    protected void withdrawButtonClick() {
        switchPage(Page.WITHDRAW);
    }

    @FXML
    protected void transferButtonClick() {
        switchPage(Page.TRANSFER);
    }

    @FXML
    protected void notificationButtonClick() {
        switchPage(Page.NOTIFICATIONS);
    }

    @FXML
    protected void historyButtonClick() {
        switchPage(Page.HISTORY);
    }

    @FXML
    protected void backToMainMenu() {
        refreshFields();
        switchPage(Page.MENU);
    }

    @FXML
    protected void logOutSuccessful() throws IOException {
        String launcher = "/sys/bankingsystemjavafx/launcher.fxml";
        String style = "style.css";
        Utility.switchScene((Stage) btn_logOut.getScene().getWindow(), launcher, style);
    }

    /*
    *  ACTION BUTTONS
    * */
    @FXML
    protected void depositAction() {
        if (textField_deposit.getText().isBlank()) {
            refreshFields();
            message("Please enter an amount!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (Double.parseDouble(textField_deposit.getText()) <= 0) {
            refreshFields();
            message("Input that is equal to or less than zero is not permitted!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TransactionLog log = new TransactionLog(0, Double.parseDouble(textField_deposit.getText()), "DEPOSIT");
        transactionHistory_contentArea.getChildren().addFirst(log);

        refreshFields();
    }

    @FXML
    protected void withdrawAction() {
        if (textField_withdraw.getText().isBlank()) {
            refreshFields();
            message("Please enter an amount!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (Double.parseDouble(textField_withdraw.getText()) <= 0) {
            refreshFields();
            message("Input that is equal to or less than zero is not permitted!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TransactionLog log = new TransactionLog(0, Double.parseDouble(textField_withdraw.getText()), "WITHDRAW");
        transactionHistory_contentArea.getChildren().addFirst(log);

        refreshFields();
    }

    @FXML
    protected void transferAction() {
        if (textField_transfer.getText().isBlank()) {
            refreshFields();
            message("Please enter an amount!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (Double.parseDouble(textField_transfer.getText()) <= 0) {
            refreshFields();
            message("Input that is equal to or less than zero is not permitted!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (textField_receiverName.getText().isBlank()) {
            refreshFields();
            message("Please enter the receiver's name!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TransactionLog log = new TransactionLog(0, Double.parseDouble(textField_transfer.getText()), textField_receiverName.getText(), "TRANSFER");
        transactionHistory_contentArea.getChildren().addFirst(log);

        refreshFields();
    }

    /*
     *  PRIVATE METHODS
     * */
    private enum Page {
        MENU, DEPOSIT, WITHDRAW, TRANSFER, HISTORY, NOTIFICATIONS
    }

    private void switchPage(Page page) {
        page_mainMenu.setVisible(false);
        page_deposit.setVisible(false);
        page_withdraw.setVisible(false);
        page_transfer.setVisible(false);
        page_history.setVisible(false);
        page_notifications.setVisible(false);

        switch (page) {
            case MENU -> page_mainMenu.setVisible(true);
            case DEPOSIT -> page_deposit.setVisible(true);
            case WITHDRAW -> page_withdraw.setVisible(true);
            case TRANSFER -> page_transfer.setVisible(true);
            case HISTORY -> page_history.setVisible(true);
            case NOTIFICATIONS -> page_notifications.setVisible(true);
        }
    }

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

    private void setTextFormatters(TextField textField) {
        // Pattern: Digits, optional decimal point, up to 2 digits after
        Pattern validEditingState = Pattern.compile("\\d*(\\.\\d{0,2})?");

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (validEditingState.matcher(newText).matches()) {
                return change;
            } else {
                System.out.println("ERROR");
                return null;
            }
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }

    private void message(String message, String title, int messageType) {
        Object[] options = {"Okay"};

        JOptionPane.showOptionDialog(
                null,
                message,
                title,
                JOptionPane.DEFAULT_OPTION,
                messageType,
                null,
                options,
                options[0]
        );
    }

    /*
    * PRIVATE CLASSES
    * */

    private static class TransactionLog extends TextFlow {
        TransactionLog(int referenceNumber, double amount, String paymentMethod) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);

            Text text = new Text();

            this.setId("transaction_log");
            this.getStylesheets().add("style.css");
            this.setLineSpacing(5);

            text.setFont(Font.font("Monospaced", 12));
            text.setText(String.format("""
                            %-30s %-30d%n\
                            %-30s %-30s%n\
                            %-30s ₱%-30.2f%n\
                            %-30s %-30S""",
                    "Reference Number:", referenceNumber, "Date & Time:", formattedDateTime, "Amount:", amount, "Payment Method:", paymentMethod));
            this.getChildren().add(text);

            VBox.setMargin(this, new Insets(15));
            this.setPadding(new Insets(15));
        }

        TransactionLog(int referenceNumber, double amount, String receiver, String paymentMethod) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);

            Text text = new Text();

            this.setId("transaction_log");
            this.getStylesheets().add("style.css");
            this.setLineSpacing(5);

            text.setFont(Font.font("Monospaced", 12));
            text.setText(String.format("""
                            %-30s %-30d%n\
                            %-30s %-30s%n\
                            %-30s ₱%-30.2f%n\
                            %-30s %-30s%n\
                            %-30s %-30S""",
                    "Reference Number:", referenceNumber, "Date & Time:", formattedDateTime, "Amount:", amount, "Receiver's Name:", receiver, "Payment Method:", paymentMethod));
            this.getChildren().add(text);

            VBox.setMargin(this, new Insets(15));
            this.setPadding(new Insets(15));
        }
    }
}
