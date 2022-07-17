package ru.hse.germandilio.tetris.server.control;

public interface InputHandler {
    /**
     * Get max number of clients can connect to server.
     *
     * @return {@link Integer}
     */
    int getClientsNumber();

    /**
     * Get timeout when all clients will be notified with predefined event.
     *
     * @return {@link Long}
     */
    long getTimeout();
}
