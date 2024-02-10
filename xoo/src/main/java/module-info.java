module com.example.xoo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.logging;

    opens com.example.xoo to javafx.fxml;
    exports com.example.xoo;
}