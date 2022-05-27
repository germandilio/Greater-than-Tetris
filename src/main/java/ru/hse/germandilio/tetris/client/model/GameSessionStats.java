package ru.hse.germandilio.tetris.client.model;

import ru.hse.germandilio.tetris.client.controllers.IReset;

public class GameSessionStats implements IReset {
    private long maxSessionTime;

    private long gameSessionDuration = 0;
    private int bricksPlaced = 0;
    private String name;

    private String opponentName;
    private long opponentGameDuration;
    private int opponentBricksPlaced;

    private String winnerName;

    public GameSessionStats() {
        name = null;
        reset();
    }

    public void brickPlaced() {
        ++bricksPlaced;
    }

    public int getBricksPlaced() {
        return bricksPlaced;
    }

    public void updateStopWatch() {
        ++gameSessionDuration;
    }

    public long getGameSessionDuration() {
        return gameSessionDuration;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public String getName() {
        return name;
    }

    public long getOpponentGameDuration() {
        return opponentGameDuration;
    }

    public int getOpponentBricksPlaced() {
        return opponentBricksPlaced;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOpponentGameDuration(long opponentGameDuration) {
        this.opponentGameDuration = opponentGameDuration;
    }

    public void setOpponentBricksPlaced(int opponentBricksPlaced) {
        this.opponentBricksPlaced = opponentBricksPlaced;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public long getMaxSessionTime() {
        return maxSessionTime;
    }

    public void setMaxSessionTime(long maxSessionTime) {
        this.maxSessionTime = maxSessionTime;
    }

    public boolean gameStarted() {
        return name != null && opponentName != null;
    }

    @Override
    public void reset() {
        // clear all data except username
        maxSessionTime = 0L;
        gameSessionDuration = 0L;
        bricksPlaced = 0;

        opponentName = null;
        opponentGameDuration = 0L;
        opponentBricksPlaced = 0;
        winnerName = null;
    }
}
