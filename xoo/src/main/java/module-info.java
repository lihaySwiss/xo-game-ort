module com.example.xoo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.logging;
    requires java.sql;

    opens com.example.xoo to javafx.fxml;
    exports com.example.xoo;
    exports com.example.database.Model;
    opens com.example.database.Model to javafx.fxml;
    exports com.example.database.viewModel;
    opens com.example.database.viewModel to javafx.fxml;

}