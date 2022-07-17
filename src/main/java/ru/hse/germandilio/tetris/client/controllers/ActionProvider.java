package ru.hse.germandilio.tetris.client.controllers;

import ru.hse.germandilio.tetris.client.model.ViewGameResult;

import java.util.List;

public interface ActionProvider {
    /**
     * Swot to user top list result
     *
     * @param results {@link List} of game session results
     */
    void showTopResults(List<ViewGameResult> results);

    /**
     * End client game because fatal error.
     */
    void forceEndGame();

    /**
     * End client game.
     */
    void endGame();

    /**
     * Wait for opponent.
     */
    void waitForEndGame();

    /**
     * Place brick that was sent by server.
     *
     * @param brick Brick to place
     */
    void placeBrick(boolean[][] brick);

    /**
     * Start client game.
     */
    void startGame();

    /**
     * Wait for opponent starts game.
     */
    void waitForNewGame();

    /**
     * Unblock user.
     */
    void unblockUser();
}
