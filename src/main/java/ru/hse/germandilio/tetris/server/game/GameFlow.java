package ru.hse.germandilio.tetris.server.game;

import ru.hse.germandilio.tetris.server.clienthandling.Connection;

public interface GameFlow {
    boolean readyToStartGame();

    void startGame(Connection client);

    boolean readyToEndGame();

    void endGame(Connection client);
}
