module com.example.dynamic {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.dynamic to javafx.fxml;
    exports com.example.dynamic;
}