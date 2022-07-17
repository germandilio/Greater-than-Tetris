package ru.hse.germandilio.tetris.client.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import ru.hse.germandilio.tetris.client.controllers.Reset;

@Getter
@Setter
public class GameSessionStats implements Reset {
    private long maxSessionTime;
    private String name;
    private boolean endedGame;

    private String opponentName;
    private long opponentGameDuration;
    private int opponentBricksPlaced;
    private boolean opponentLeaveGame;

    private String winnerName;

    @Setter(AccessLevel.NONE)
    private long gameSessionDuration = 0L;

    @Setter(AccessLevel.NONE)
    private int bricksPlaced = 0;

    public GameSessionStats() {
        name = null;
        reset();
    }

    public void brickPlaced() {
        ++bricksPlaced;
    }

    public void updateStopWatch() {
        ++gameSessionDuration;
    }

    public void stopGame() {
        gameSessionDuration = maxSessionTime;
    }

    public boolean gameStarted() {
        return name != null && opponentName != null;
    }

    public boolean hasOpponent() {
        return !opponentLeaveGame && opponentName != null && !opponentName.isEmpty();
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

        endedGame = false;
    }
}
