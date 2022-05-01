module edu.ntnu.idatt2001.nicolahb {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;

    opens edu.ntnu.idatt2001.nicolahb.client to javafx.fxml;
    exports edu.ntnu.idatt2001.nicolahb.client;
    exports edu.ntnu.idatt2001.nicolahb.client.models;
    opens edu.ntnu.idatt2001.nicolahb.client.models to javafx.fxml;
}
