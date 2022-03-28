module edu.ntnu.idatt2001.nicolahb {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens edu.ntnu.idatt2001.nicolahb to javafx.fxml;
    exports edu.ntnu.idatt2001.nicolahb;
}
