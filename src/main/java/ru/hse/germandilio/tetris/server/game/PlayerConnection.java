package ru.hse.germandilio.tetris.server.game;

import ru.hse.germandilio.tetris.server.clienthandling.CommandSender;
import ru.hse.germandilio.tetris.server.clienthandling.Connection;

public interface PlayerConnection {
    Connection playerConnected(CommandSender clientHandler);

    void disconnectPlayer(Connection client);
}
