package ru.hse.germandilio.tetris.server.game;

import ru.hse.germandilio.tetris.server.clienthandling.Connection;

public interface GameSessions {
    void saveGameSessionResults(Connection client);

    void getTopSessions(Connection client, int topNumber);
}
