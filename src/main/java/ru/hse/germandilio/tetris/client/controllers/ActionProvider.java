package ru.hse.germandilio.tetris.client.controllers;

import ru.hse.germandilio.tetris.commands.GameStatus;

public interface ActionProvider {
    void startTimer(long time);

    void startGame(String partnerName);

    void endGame(int opponentBricks, long opponentTime, int myBricks, long myTime,
                 String winnerName);

    void endGameSingle();

    String getUserName();

    long getGameTime();

    void waitForNextBrick();

    void placeNextBrick(boolean[][] brick);

    void waitForAction(String label);

    void setStatus(GameStatus status);

    GameStatus getStatus();

    void closeBlockingWindow();
}
