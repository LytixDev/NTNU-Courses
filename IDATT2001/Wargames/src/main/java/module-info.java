module edu.ntnu.idatt2001.nicolahb {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;

    opens edu.ntnu.idatt2001.nicolahb.gui to javafx.fxml;
    exports edu.ntnu.idatt2001.nicolahb.gui;
    exports edu.ntnu.idatt2001.nicolahb.gui.models;
    exports edu.ntnu.idatt2001.nicolahb;
    opens edu.ntnu.idatt2001.nicolahb.gui.models to javafx.fxml;
    exports edu.ntnu.idatt2001.nicolahb.gui.controllers;
    opens edu.ntnu.idatt2001.nicolahb.gui.controllers to javafx.fxml;
}
