package ru.hse.germandilio.tetris.server.clienthandling;

public interface CommandSender {
    /**
     * Send command.
     *
     * @param command {@code String} represented command.
     */
    void sendCommand(String command);

    /**
     * Close listening socket and/or thread.
     *
     * @throws Exception on closing
     */
    void close() throws Exception;
}
