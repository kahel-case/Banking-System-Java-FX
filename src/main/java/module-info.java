module sys.bankingsystemjavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.desktop;

    opens sys.bankingsystemjavafx to javafx.fxml;
    exports sys.bankingsystemjavafx;
}