module ru.hse.germandilio.tetris.tetris {
    requires javafx.controls;
    requires javafx.fxml;


    exports ru.hse.germandilio.tetris.server.generator;
    exports ru.hse.germandilio.tetris.commands;
    exports ru.hse.germandilio.tetris.client.application;
    opens ru.hse.germandilio.tetris.client.application to javafx.fxml;
    exports ru.hse.germandilio.tetris.client.model.gameboard;
    opens ru.hse.germandilio.tetris.client.model.gameboard to javafx.fxml;
    exports ru.hse.germandilio.tetris.client.controllers;
    opens ru.hse.germandilio.tetris.client.controllers to javafx.fxml;
    exports ru.hse.germandilio.tetris.client.model;
    opens ru.hse.germandilio.tetris.client.model to javafx.fxml;
    exports ru.hse.germandilio.tetris.client.model.client;
    opens ru.hse.germandilio.tetris.client.model.client to javafx.fxml;
}