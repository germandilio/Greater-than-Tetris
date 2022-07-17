module ru.hse.germandilio.tetris.tetris {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires static lombok;

    exports ru.hse.germandilio.tetris.server;
    exports ru.hse.germandilio.tetris.server.application;
    exports ru.hse.germandilio.tetris.server.bricks;
    exports ru.hse.germandilio.tetris.server.clienthandling;
    exports ru.hse.germandilio.tetris.server.database;
    exports ru.hse.germandilio.tetris.server.control;
    exports ru.hse.germandilio.tetris.server.game;
    exports ru.hse.germandilio.tetris.server.generator;

    exports ru.hse.germandilio.tetris.shared.commands;

    exports ru.hse.germandilio.tetris.client.application;
    exports ru.hse.germandilio.tetris.client.controllers;
    exports ru.hse.germandilio.tetris.client.model;
    exports ru.hse.germandilio.tetris.client.model.gameboard;
    exports ru.hse.germandilio.tetris.client.model.client;

    opens ru.hse.germandilio.tetris.client.application to javafx.fxml;
    opens ru.hse.germandilio.tetris.client.model.gameboard to javafx.fxml;
    opens ru.hse.germandilio.tetris.client.controllers to javafx.fxml;
    opens ru.hse.germandilio.tetris.client.model to javafx.fxml;
    opens ru.hse.germandilio.tetris.client.model.client to javafx.fxml;
    exports ru.hse.germandilio.tetris.shared;
    opens ru.hse.germandilio.tetris.shared to javafx.fxml;
}