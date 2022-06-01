package ru.hse.germandilio.tetris.server.game;

import ru.hse.germandilio.tetris.server.clienthandling.CommandSender;
import ru.hse.germandilio.tetris.server.clienthandling.Connection;

public interface GameManager {
    Connection playerConnected(CommandSender clientHandler);

    void disconnectPlayer(Connection client);

    void setName(Connection client, String name);

    void sendWaitingStartGame(Connection client);

    void sendWaitingEndGame(Connection client);

    void sendNextBrick(Connection client, int indexInSequence);

    boolean readyToStartGame();

    void startGame(Connection client);

    boolean readyToEndGame();

    void endGame(Connection client);

    void sendEndGameWithoutResults(Connection client);

    void saveGameSessionResults(Connection client);

    void getTopSessions(Connection client, int topNumber);
}
