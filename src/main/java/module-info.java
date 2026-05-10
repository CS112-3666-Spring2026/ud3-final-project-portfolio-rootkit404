module war {
    requires javafx.controls;
    requires javafx.fxml;

    opens war to javafx.fxml;
    opens war.controllers to javafx.fxml;
    opens war.models to javafx.fxml;

    exports war;
    exports war.controllers;
    exports war.models;
}