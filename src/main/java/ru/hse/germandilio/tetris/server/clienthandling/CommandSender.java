package ru.hse.germandilio.tetris.server.clienthandling;

public interface CommandSender {
    void sendCommand(String command);

    void close() throws Exception;
}
