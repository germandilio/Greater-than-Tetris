package ru.hse.germandilio.tetris.client.controllers;

import ru.hse.germandilio.tetris.client.model.GameResult;

import java.util.List;

public interface ActionProvider {
    void showTopResults(List<GameResult> results);

    void forceEndGame();

    void endGameWithoutOpponentResults();

    void endGame();

    void waitForEndGame();

    void placeBrick(boolean[][] brick);

    void startGame();

    void waitForNewGame();

    void unblockUser();
}
