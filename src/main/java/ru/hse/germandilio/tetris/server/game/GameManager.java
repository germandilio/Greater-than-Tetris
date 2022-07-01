package ru.hse.germandilio.tetris.server.game;

import ru.hse.germandilio.tetris.server.clienthandling.Connection;

public interface GameManager extends GameFlow, GameSessions, PlayerConnection {
    void setName(Connection client, String name);

    void sendWaitingStartGame(Connection client);

    void sendWaitingEndGame(Connection client);

    void sendNextBrick(Connection client, int indexInSequence);

    void sendEndGameWithoutResults(Connection client);
}

