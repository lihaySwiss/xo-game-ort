module com.example.xoo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.logging;
    requires mysql.connector.java;
    requires java.sql;

    opens com.example.xoo to javafx.fxml;
    exports com.example.xoo;
    exports com.example.database;
    opens com.example.database to javafx.fxml;
    exports com.example.database.Model;
    opens com.example.database.Model to javafx.fxml;

}